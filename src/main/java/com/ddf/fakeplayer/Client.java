package com.ddf.fakeplayer;

import com.ddf.fakeplayer.entity.player.Player;
import com.ddf.fakeplayer.json.ExtraData;
import com.ddf.fakeplayer.json.skin.SkinData;
import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.util.Logger;
import com.ddf.fakeplayer.util.ProtocolVersionUtil;
import com.ddf.fakeplayer.world.World;
import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Client {
    private static final Random rand = new Random();

    private volatile boolean stop = true;
    private volatile boolean autoReconnect = true;
    private volatile long reconnectDelay = 1000;
    private volatile CountDownLatch latch;
    private volatile BedrockClient bedrockClient;
    private volatile BedrockClientSession session;
    private volatile BedrockPacketCodec packetCodec;
    private volatile ClientPacketHandler packetHandler;
    private volatile World world;
    private volatile Player player;

    private Logger logger;
    private Thread thread;
    private State state;
    private List<StateChangedListener> stateChangedListeners = new ArrayList<>();
    private BedrockPacketCodec defaultPacketCodec = Bedrock_v408.V408_CODEC;
    private String skinDataJson = Resources.SKIN_DATA_STEVE_JSON;
    private int chunkRadius = 20;
    private String playerName;
    private UUID playerUUID;
    private final KeyPair clientKeyPair;
    private KeyPair serverKeyPair;

    public Client(String playerName, KeyPair serverKeyPair){
        this(playerName, UUID.nameUUIDFromBytes(playerName.getBytes(StandardCharsets.UTF_8)), serverKeyPair);
    }

    public Client(String playerName, UUID uuid, KeyPair serverKeyPair){
        this.logger = Logger.getLogger();
        this.playerName = playerName;
        this.playerUUID = uuid;

        this.clientKeyPair = KeyUtil.generateKeyPair();
        this.serverKeyPair = serverKeyPair;
        setState(State.STOPPED);
    }

    public synchronized void connect(String address, int port) {
        if (this.isConnected() || this.getState() != State.STOPPED) {
            return;
        }
        setState(State.CONNECTING);
        this.stop = false;
        thread = new Thread(() -> {
            do {
                try {
                    latch = new CountDownLatch(1);
                    this.world = new World();
                    this.player = new Player(playerName, playerUUID, this);
                    world.addEntity(player);
                    this.packetHandler = new ClientPacketHandler(this);
                    bedrockClient = new BedrockClient(new InetSocketAddress(0));
                    bedrockClient.bind().join();
                    final InetSocketAddress addressToConnect = new InetSocketAddress(address, port);
                    bedrockClient.ping(addressToConnect).whenComplete((bedrockPong, throwable) -> {
                        if (throwable != null) {
                            packetCodec = null;
                            return;
                        }
                        try {
                            packetCodec = ProtocolVersionUtil.getPacketCodec(bedrockPong.getProtocolVersion());
                        } catch (IllegalArgumentException e) {
                            packetCodec = null;
                        }
                    }).join();
                    if (packetCodec == null) {
                        packetCodec = defaultPacketCodec;
                        logger.log(playerName + " 协议版本获取失败,尝试使用默认协议版本(" + defaultPacketCodec.getProtocolVersion() + ")连接");
                    }
                    bedrockClient.connect(addressToConnect).whenComplete((session, throwable) -> {
                        if (throwable != null) {
                            return;
                        }
                        this.session = session;
                        session.setPacketCodec(packetCodec);
                        session.setPacketHandler(packetHandler);
                        packetHandler.handleConnected();
                    }).join();
                } catch (Exception e) {
                    setState(State.DISCONNECTED);
                    logger.log(e);
                }

                if(isConnected()) {
                    setState(State.CONNECTED);
                    session.addDisconnectHandler(disconnectReason -> latch.countDown());
                    logger.log(player.getName() + " 已连接");
                    try {
                        latch.await();
                    } catch (InterruptedException ignored) {}
                }

                bedrockClient.close();
                setState(State.DISCONNECTED);
                logger.log(playerName + " 已断开连接");
                
                if (!stop && autoReconnect) {
                    try {
                        Thread.sleep(reconnectDelay + nextLong(reconnectDelay));
                    } catch (InterruptedException ignored) {}
                    setState(State.RECONNECTING);
                    logger.log(playerName + " 重连中");
                }

            } while (!stop && autoReconnect);
            setState(State.STOPPED);
            logger.log(playerName + " 已停止");
        });
        thread.start();
    }

    public void sendPacket(BedrockPacket packet) {
        session.sendPacket(packet);
    }

    public String getHostAddress(){
        return session.getAddress().getAddress().getHostAddress();
    }

    public int getPort(){
        return session.getAddress().getPort();
    }

    public boolean isConnected(){
        return session!= null && !session.isClosed();
    }

    public synchronized void disconnect() {
        if (isConnected()) {
            setState(State.DISCONNECTING);
            bedrockClient.close();
        }
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean wait) {
        if (getState() == State.STOPPED) {
            return;
        }
        stop = true;
        disconnect();
        setState(State.STOPPING);
        if (latch != null) {
            latch.countDown();
        }
        if (wait && thread != null) {
            try {
                thread.join();
            } catch (InterruptedException ignored) { }
        }
    }

    public ExtraData createExtraData() {
        ExtraData extraData = new ExtraData();
        extraData.setXUID(Long.toString(player.getUUID().getLeastSignificantBits()));
        extraData.setDisplayName(player.getName());
        extraData.setIdentity(player.getUUID().toString());
        return extraData;
    }

    public SkinData createSkinData(){
        SkinData skin = SkinData.createFromSkinJson(skinDataJson);
        skin.setClientRandomId(rand.nextLong());
        skin.setCurrentInputMode(0);
        skin.setDefaultInputMode(0);
        skin.setDeviceId("11111111111111111111111111111111");
        skin.setDeviceModel("DeviceModel");
        skin.setDeviceOS(0);
        skin.setGameVersion(packetCodec.getMinecraftVersion());
        skin.setGuiScale(0);
        skin.setLanguageCode("LanguageCode");
        skin.setPlatformOfflineId("");
        skin.setPlatformOnlineId("");
        skin.setSelfSignedId(player.getUUID().toString());
        skin.setServerAddress(getHostAddress() + ":" + getPort());
        skin.setThirdPartyName(player.getName());
        return skin;
    }

    private long nextLong(long n) {
        long bits, val;
        do {
            bits = (rand.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits-val+(n-1) < 0L);
        return val;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public long getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public BedrockPacketCodec getPacketCodec() {
        return packetCodec;
    }

    public BedrockPacketCodec getDefaultPacketCodec() {
        return defaultPacketCodec;
    }

    public void setDefaultPacketCodec(BedrockPacketCodec defaultPacketCodec) {
        this.defaultPacketCodec = defaultPacketCodec;
    }

    public ClientPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void setPacketHandler(ClientPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public BedrockClient getBedrockClient() {
        return bedrockClient;
    }

    public void setBedrockClient(BedrockClient bedrockClient) {
        this.bedrockClient = bedrockClient;
    }

    public BedrockClientSession getSession() {
        return session;
    }

    public void setSession(BedrockClientSession session) {
        this.session = session;
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized void setState(State state) {
        State oldState = this.state;
        this.state = state;
        if (oldState != state) {
            getStateChangedListeners().forEach(listener -> {
                if (listener != null) {
                    listener.stateChanged(this, oldState, state);
                }
            });
        }
    }

    public synchronized void addStateChangedListener(StateChangedListener listener) {
        getStateChangedListeners().add(listener);
    }

    public synchronized void removeStateChangedListener(StateChangedListener listener) {
        getStateChangedListeners().remove(listener);
    }

    public synchronized List<StateChangedListener> getStateChangedListeners() {
        return stateChangedListeners;
    }

    public String getSkinDataJson() {
        return skinDataJson;
    }

    public void setSkinDataJson(String skinDataJson) {
        this.skinDataJson = skinDataJson;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public KeyPair getClientKeyPair() {
        return clientKeyPair;
    }

    public KeyPair getServerKeyPair() {
        return serverKeyPair;
    }

    public void setServerKeyPair(KeyPair serverKeyPair) {
        this.serverKeyPair = serverKeyPair;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    public void setChunkRadius(int chunkRadius) {
        this.chunkRadius = chunkRadius;
    }

    public interface StateChangedListener {
        void stateChanged(Client client, State oldState, State currentState);
    }

    public enum State {
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
        RECONNECTING,
        STOPPING,
        STOPPED
    }
}
