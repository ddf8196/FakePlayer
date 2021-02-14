package com.ddf.fakeplayer;

import com.ddf.fakeplayer.entity.player.Player;
import com.ddf.fakeplayer.json.ExtraData;
import com.ddf.fakeplayer.json.skin.SkinData;
import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.world.World;
import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Client {
    private static final Random rand = new Random();

    private volatile boolean stop = false;
    private volatile boolean autoReconnect = true;
    private volatile long reconnectDelay = 1000;
    private volatile CountDownLatch latch;
    private volatile BedrockClientSession session;

    private BedrockPacketCodec packetCodec = Bedrock_v408.V408_CODEC;
    private ClientPacketHandler packetHandler;
    private BedrockClient bedrockClient;
    private Thread thread;


    private String skinDataJson = Resources.SKIN_DATA_STEVE_JSON;

    private World world;
    private Player player;
    private int chunkRadius = 8;
    private String playerName;
    private UUID playerUUID;

    private final KeyPair clientKeyPair;
    private KeyPair serverKeyPair;

    public Client(String playerName, KeyPair serverKeyPair){
        this(playerName, UUID.nameUUIDFromBytes(playerName.getBytes(StandardCharsets.UTF_8)), serverKeyPair);
    }

    public Client(String playerName, UUID uuid, KeyPair serverKeyPair){
        this.playerName = playerName;
        this.playerUUID = uuid;

        this.clientKeyPair = KeyUtil.generateKeyPair();
        this.serverKeyPair = serverKeyPair;
    }

    public void connect(String address, int port) {
        if (this.isConnected()) {
            return;
        }
        thread = new Thread(() -> {
            do {
                try {
                    this.stop = false;
                    this.world = new World();
                    this.player = new Player(playerName, playerUUID, this);
                    world.addEntity(player);
                    this.packetHandler = new ClientPacketHandler(this);

                    bedrockClient = new BedrockClient(new InetSocketAddress(0));
                    bedrockClient.bind().join();
                    final InetSocketAddress addressToConnect = new InetSocketAddress(address, port);
                    bedrockClient.connect(addressToConnect).whenComplete((session, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                            return;
                        }
                        this.session = session;
                        session.setPacketCodec(packetCodec);
                        session.setPacketHandler(packetHandler);
                        packetHandler.handleConnected();
                    }).join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(isConnected()) {
                    latch = new CountDownLatch(1);
                    session.addDisconnectHandler(disconnectReason -> latch.countDown());
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(playerName + " 已断开连接");
                
                if (!stop && autoReconnect) {
                    try {
                        Thread.sleep(reconnectDelay);
                        System.out.println(playerName + " 重连中");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (!stop && autoReconnect);
        });
        thread.start();
    }

    public void sendPacket(BedrockPacket packet) {
        session.sendPacket(packet);
    }

    public String getHostName(){
        return session.getAddress().getHostName();
    }

    public int getPort(){
        return session.getAddress().getPort();
    }

    public boolean isConnected(){
        return session!= null && !session.isClosed();
    }

    public void disconnect() {
        if (isConnected()) {
            session.disconnect();
        }
    }

    public void shutdown() {
        stop = true;
        disconnect();
        try {
            if (latch != null) {
                latch.countDown();
            }
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ExtraData createExtraData() {
        ExtraData extraData = new ExtraData();
        extraData.XUID = Long.toString(player.getUUID().getLeastSignificantBits());
        extraData.displayName = player.getName();
        extraData.identity = player.getUUID().toString();
        return extraData;
    }

    public SkinData createSkinData(){
        SkinData skin = SkinData.createFromSkinJson(skinDataJson);
        skin.ClientRandomId = rand.nextLong();
        skin.CurrentInputMode = 0;
        skin.DefaultInputMode = 0;
        skin.DeviceId = "11111111111111111111111111111111";
        skin.DeviceModel = "DeviceModel";
        skin.DeviceOS = 0;
        skin.GameVersion = packetCodec.getMinecraftVersion();
        skin.GuiScale = 0;
        skin.LanguageCode = "LanguageCode";
        skin.PlatformOfflineId = "";
        skin.PlatformOnlineId = "";
        skin.SelfSignedId = player.getUUID().toString();
        skin.ServerAddress = getHostName() + ":" + getPort();
        skin.ThirdPartyName = player.getName();
        return skin;
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

    public void setPacketCodec(BedrockPacketCodec packetCodec) {
        this.packetCodec = packetCodec;
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

    public interface ConnectedListener {

    }
}
