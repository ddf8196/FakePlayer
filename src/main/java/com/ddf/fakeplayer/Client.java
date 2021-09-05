package com.ddf.fakeplayer;

import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.json.ExtraData;
import com.ddf.fakeplayer.json.skin.SkinData;
import com.ddf.fakeplayer.level.MultiPlayerLevel;
import com.ddf.fakeplayer.network.ClientPacketHandler;
import com.ddf.fakeplayer.network.ClientPacketSender;
import com.ddf.fakeplayer.network.PacketSender;
import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.util.Logger;
import com.ddf.fakeplayer.util.ProtocolVersionUtil;
import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements Closeable {
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final AtomicBoolean autoReconnect = new AtomicBoolean(true);
    private volatile long reconnectDelay = 1000;
    private volatile BedrockPacketCodec packetCodec;

    private volatile BedrockClient bedrockClient;
    private volatile BedrockClientSession session;
    private volatile ClientPacketSender packetSender;
    private volatile ClientPacketHandler packetHandler;
    private volatile MultiPlayerLevel level;
    private volatile FakePlayer player;

    private volatile ItemRegistry itemRegistry;
    private Logger logger;
    private ClientThread clientThread;
    private boolean initialized = false;
    private ReconnectTask reconnectTask;
    private final Queue<BedrockPacket> receivedPackets = new ConcurrentLinkedQueue<>();
    private final Queue<Runnable> runnableQueue = new ConcurrentLinkedQueue<>();
    private State state;
    private final List<StateChangeListener> stateChangeListeners = Collections.synchronizedList(new ArrayList<>());
    private BedrockPacketCodec defaultPacketCodec = Bedrock_v408.V408_CODEC;
    private String skinDataJson = Resources.SKIN_DATA_STEVE_JSON;
    private int chunkRadius = 20;
    private String playerName;
    private UUID playerUUID;
    private final UUID deviceId = UUID.randomUUID();
    private final KeyPair clientKeyPair;
    private volatile KeyPair serverKeyPair;
    private final Object stateLock = new Object();
    private final Object stopLock = new Object();
    private boolean allowChatMessageControl;

    public Client(String playerName, KeyPair serverKeyPair){
        this(playerName, UUID.nameUUIDFromBytes(playerName.getBytes(StandardCharsets.UTF_8)), serverKeyPair, false);
    }

    public Client(String playerName, UUID uuid, KeyPair serverKeyPair, boolean allowChatMessageControl){
        this.logger = Logger.getLogger();
        this.playerName = playerName;
        this.playerUUID = uuid;
        this.serverKeyPair = serverKeyPair;
        this.clientKeyPair = KeyUtil.generateKeyPair();
        this.clientThread = new ClientThread(this);
        this.packetSender = new ClientPacketSender(this);
        this.allowChatMessageControl = allowChatMessageControl;
        setState(State.STOPPED);
    }

    public void connect(String address, int port) {
        connect(address, port, false);
    }

    private void connect(String address, int port, boolean isReconnect) {
        synchronized (stopLock) {
            if (!this.initialized && this.clientThread != null) {
                this.initialized = true;
                this.clientThread.start();
            }
        }
        runOnClientThread(() -> {
            if (this.isConnected()) {
                return;
            }
            if (isReconnect) {
                reconnectTask = null;
                setState(State.RECONNECTING);
            } else {
                setState(State.CONNECTING);
                if (reconnectTask != null) {
                    reconnectTask.cancel();
                    reconnectTask = null;
                }
            }
            this.stop.set(false);
            try {
                itemRegistry = new ItemRegistry();
                level = new MultiPlayerLevel(itemRegistry, packetSender);
                player = new FakePlayer(level, playerName, playerUUID, this);
                level.addEntity(player);
                packetHandler = new ClientPacketHandler(this);
                bedrockClient = new BedrockClient(new InetSocketAddress(0));
                bedrockClient.bind().join();
                final InetSocketAddress addressToConnect = new InetSocketAddress(address, port);
                try {
                    BedrockPong bedrockPong = bedrockClient.ping(addressToConnect, 1, TimeUnit.SECONDS).get(1, TimeUnit.SECONDS);
                    packetCodec = ProtocolVersionUtil.getPacketCodec(bedrockPong.getProtocolVersion());
                } catch (Throwable t) {
                    packetCodec = defaultPacketCodec;
                    logger.log(playerName, " 协议版本获取失败,尝试使用默认协议版本(", defaultPacketCodec.getProtocolVersion(), ")连接");
                }
                bedrockClient.setRakNetVersion(ProtocolVersionUtil.getRakNetProtocolVersion(packetCodec));
                bedrockClient.connect(addressToConnect, 1, TimeUnit.SECONDS).whenComplete((session, throwable) -> runOnClientThread(() ->  {
                    if (throwable != null) {
                        return;
                    }
                    setState(State.CONNECTED);
                    session.addDisconnectHandler(disconnectReason -> runOnClientThread(() ->  {
                        setState(State.DISCONNECTED);
                        logger.log(playerName, " 已断开连接: ", disconnectReason.name());
                        reconnectOrStop(address, port);
                    }));
                    logger.log(player.getName(), " 已连接");
                    this.session = session;
                    session.setPacketCodec(packetCodec);
                    session.setBatchHandler((bedrockSession, byteBuf, collection) -> {
                        if (isConnected() && this.session == bedrockSession)
                            receivedPackets.addAll(collection);
                    });
                    if (isConnected() && this.session == session) {
                        packetHandler.handleConnected();
                    }
                })).join();
            } catch (Throwable throwable) {
                if (bedrockClient != null) {
                    bedrockClient.close();
                }
                setState(State.DISCONNECTED);
                reconnectOrStop(address, port);
                logger.log(playerName, " 已断开连接: ", throwable);
            }
        });
    }

    private void reconnectOrStop(String address, int port) {
        if (!stop.get() && autoReconnect.get()) {
            reconnectTask = new ReconnectTask(Client.this, address, port, reconnectDelay + ThreadLocalRandom.current().nextInt((int) Math.min(reconnectDelay, Integer.MAX_VALUE)));
        } else {
            setState(State.STOPPED);
            logger.log(playerName, "已停止");
        }
    }

    public void sendPacket(BedrockPacket packet) {
        session.sendPacket(packet);
    }

    public PacketSender getPacketSender() {
        return this.packetSender;
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

    public void disconnect() {
        if (isConnected()) {
            setState(State.DISCONNECTING);
            bedrockClient.close();
            receivedPackets.clear();
        }
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean wait) {
        synchronized (stopLock) {
            if (!initialized) {
                return;
            }
        }
        CountDownLatch latch = wait ? new CountDownLatch(1) : null;
        runOnClientThread(() -> {
            try {
                if (reconnectTask != null) {
                    reconnectTask.cancel();
                    reconnectTask = null;
                }
                boolean wasConnected = isConnected();
                stop.set(true);
                disconnect();
                if (!wasConnected && getState() != State.STOPPED) {
                    setState(State.STOPPED);
                    logger.log(playerName, "已停止");
                }
            } finally {
                if (latch != null)
                    latch.countDown();
            }
        });
        if (wait) {
            try {
                latch.await();
            } catch (InterruptedException ignored) {}
        }
    }

    public void runOnClientThread(Runnable runnable) {
        runnableQueue.add(runnable);
    }

    @Override
    public void close() {
        synchronized (stopLock) {
            stop(true);
            clientThread.setStop();
            clientThread = null;
        }
    }

    public ExtraData createExtraData() {
        ExtraData extraData = new ExtraData();
        extraData.setXUID(Long.toString(player.getClientUUID().getLeastSignificantBits()));
        extraData.setDisplayName(player.getName());
        extraData.setIdentity(player.getClientUUID().toString());
        return extraData;
    }

    public SkinData createSkinData(){
        SkinData skin = SkinData.createFromSkinJson(skinDataJson);
        skin.setClientRandomId(ThreadLocalRandom.current().nextLong());
        skin.setCurrentInputMode(1);
        skin.setDefaultInputMode(1);
        skin.setDeviceId(deviceId.toString());
        skin.setDeviceModel("DeviceModel");
        skin.setDeviceOS(1);
        skin.setGameVersion(packetCodec.getMinecraftVersion());
        skin.setGuiScale(0);
        skin.setLanguageCode("zh_CN");
        skin.setPlatformOfflineId("");
        skin.setPlatformOnlineId("");
        skin.setSelfSignedId(player.getClientUUID().toString());
        skin.setServerAddress(getHostAddress() + ":" + getPort());
        skin.setThirdPartyName(player.getName());
        return skin;
    }

    public void update() {
        this.level.tick();
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public boolean isStop() {
        return stop.get();
    }

    public void setStop(boolean stop) {
        this.stop.set(stop);
    }

    public boolean isAutoReconnect() {
        return autoReconnect.get();
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect.set(autoReconnect);
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

    public State getState() {
        synchronized (stateLock) {
            return state;
        }
    }

    private void setState(State state) {
        synchronized (stateLock) {
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
    }

    public void addStateChangedListener(StateChangeListener listener) {
        getStateChangedListeners().add(listener);
    }

    public void removeStateChangedListener(StateChangeListener listener) {
        getStateChangedListeners().remove(listener);
    }

    public List<StateChangeListener> getStateChangedListeners() {
        return stateChangeListeners;
    }

    public String getSkinDataJson() {
        return skinDataJson;
    }

    public void setSkinDataJson(String skinDataJson) {
        this.skinDataJson = skinDataJson;
    }

    public MultiPlayerLevel getLevel() {
        return level;
    }

    public FakePlayer getPlayer() {
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

    public boolean isAllowChatMessageControl() {
        return allowChatMessageControl;
    }

    public void setAllowChatMessageControl(boolean allowChatMessageControl) {
        this.allowChatMessageControl = allowChatMessageControl;
    }

    public interface StateChangeListener {
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

    private static class ReconnectTask {
        private Client client;
        private long remainingTick;
        private boolean canceled = false;
        private final String address;
        private final int port;

        public ReconnectTask(Client client, String address, int port, long reconnectDelay) {
            this.client = client;
            this.remainingTick = reconnectDelay / 50;
            this.address = address;
            this.port = port;
        }

        public void tick() {
            if (!canceled && --remainingTick == 0) {
                client.connect(address, port, true);
            }
        }

        public void cancel() {
            canceled = true;
        }
    }

    private static class ClientThread extends Thread {
        private static final long MIN_TICK_TIME = 50;
        private volatile boolean started = false;
        private final AtomicBoolean stop = new AtomicBoolean(false);
        private Client client;

        public ClientThread(Client client) {
            this.client = client;
        }

        public void setStop() {
            stop.set(true);
        }

        @Override
        public void run() {
            started = true;
            while (!stop.get()) {
                long startTime = System.currentTimeMillis();
                try {
                    if (client.reconnectTask != null) {
                        client.reconnectTask.tick();
                    }
                    if (!client.isConnected()) {
                        client.receivedPackets.clear();
                    } else {
                        while (!client.receivedPackets.isEmpty()) {
                            BedrockPacket packet = client.receivedPackets.poll();
                            if (packet != null) {
                                packet.handle(client.packetHandler);
                            }
                        }
                        client.update();
                    }
                    while (!client.runnableQueue.isEmpty()) {
                        Runnable runnable = client.runnableQueue.poll();
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                } catch (Throwable t) {
                    client.logger.log(client.playerName, " 客户端线程发生异常: ", t);
                }
                long time = System.currentTimeMillis() - startTime;
                if (time >= MIN_TICK_TIME) {
                    continue;
                }
                try {
                    Thread.sleep(MIN_TICK_TIME - time);
                } catch (InterruptedException ignored) {}
            }
        }

        public boolean isStarted() {
            return started;
        }
    }
}
