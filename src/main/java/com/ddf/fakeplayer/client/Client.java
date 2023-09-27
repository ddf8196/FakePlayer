package com.ddf.fakeplayer.client;

import com.ddf.fakeplayer.Resources;
import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.ItemStackRequest;
import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.json.ExtraData;
import com.ddf.fakeplayer.json.skin.SkinData;
import com.ddf.fakeplayer.level.MultiPlayerLevel;
import com.ddf.fakeplayer.network.ClientPacketHandler;
import com.ddf.fakeplayer.network.ClientPacketSender;
import com.ddf.fakeplayer.network.PacketSender;
import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.util.Logger;
import com.ddf.fakeplayer.util.PingUtil;
import com.ddf.fakeplayer.util.ProtocolVersionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v567.Bedrock_v567;
import org.cloudburstmc.protocol.bedrock.codec.v582.Bedrock_v582;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockClientInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements Closeable {
    private static final NioEventLoopGroup CLIENT_EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final AtomicBoolean autoReconnect = new AtomicBoolean(true);
    private volatile long reconnectDelay = 1000;
    private volatile BedrockCodec packetCodec;

//    private volatile BedrockClient bedrockClient;
    private volatile Channel clientChannel;
    private volatile BedrockClientSession session;
    private volatile ClientPacketSender packetSender;
    private volatile ClientPacketHandler packetHandler;
    private volatile MultiPlayerLevel level;
    private volatile FakePlayer player;

    private Logger logger;
    private ClientThread clientThread;
    private boolean initialized = false;
    private ReconnectTask reconnectTask;
    private final Queue<BedrockPacket> receivedPackets = new ConcurrentLinkedQueue<>();
    private final Queue<Runnable> runnableQueue = new ConcurrentLinkedQueue<>();
    private final Map<Integer, ItemStackRequest> itemStackRequests = new ConcurrentHashMap<>();
    private State state;
    private final List<StateChangeListener> stateChangeListeners = Collections.synchronizedList(new ArrayList<>());
    private BedrockCodec defaultPacketCodec = Bedrock_v582.CODEC;
    private SkinType skinType = SkinType.STEVE;
    private int customSkinImageWidth;
    private int customSkinImageHeight;
    private String customSkinImageData;
    private int chunkRadius = 20;
    private String playerName;
    private UUID playerUUID;
    private final UUID deviceId = UUID.randomUUID();
    private final KeyPair clientKeyPair;
    private volatile KeyPair serverKeyPair;
    private final Object stateLock = new Object();
    private final Object stopLock = new Object();
    private boolean allowChatMessageControl;

    public Client(String playerName, KeyPair serverKeyPair) {
        this(playerName, UUID.nameUUIDFromBytes(playerName.getBytes(StandardCharsets.UTF_8)), serverKeyPair, false);
    }

    public Client(String playerName, UUID uuid, KeyPair serverKeyPair, boolean allowChatMessageControl) {
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
                level = new MultiPlayerLevel(packetSender);
                player = new FakePlayer(level, playerName, playerUUID, this);
                level.addEntity(player);
                packetHandler = new ClientPacketHandler(this);

                Bootstrap bootstrap = new Bootstrap()
                        .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                        .group(CLIENT_EVENT_LOOP_GROUP.next());

                final InetSocketAddress addressToConnect = new InetSocketAddress(address, port);
                try {
                    BedrockPong bedrockPong = PingUtil.ping(addressToConnect, 10, TimeUnit.SECONDS).get(10, TimeUnit.SECONDS);
                    packetCodec = ProtocolVersionUtil.getPacketCodec(bedrockPong.protocolVersion());
                } catch (Throwable t) {
                    packetCodec = defaultPacketCodec;
                    logger.logI18N("log.client.getProtocolVersionFail", playerName, defaultPacketCodec.getProtocolVersion());
                }
                bootstrap.option(RakChannelOption.RAK_PROTOCOL_VERSION, ProtocolVersionUtil.getRakNetProtocolVersion(packetCodec));
                bootstrap.option(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());

/*
                bedrockClient = new BedrockClient(new InetSocketAddress(0));
                bedrockClient.bind().join();
                final InetSocketAddress addressToConnect = new InetSocketAddress(address, port);
                try {
                    BedrockPong bedrockPong = bedrockClient.ping(addressToConnect, 10, TimeUnit.SECONDS).get(10, TimeUnit.SECONDS);
                    packetCodec = ProtocolVersionUtil.getPacketCodec(bedrockPong.getProtocolVersion());
                } catch (Throwable t) {
                    packetCodec = defaultPacketCodec;
                    logger.logI18N("log.client.getProtocolVersionFail", playerName, defaultPacketCodec.getProtocolVersion());
                }
                bedrockClient.setRakNetVersion(ProtocolVersionUtil.getRakNetProtocolVersion(packetCodec));
*/
                if (ProtocolVersionUtil.getBlockPalette(packetCodec) != null) {
                    level.getGlobalBlockPalette().initFromNbtMapList(ProtocolVersionUtil.getBlockPalette(packetCodec));
                }

                ChannelFuture channelFuture = bootstrap.handler(new BedrockClientInitializer() {
                            @Override
                            protected void initSession(BedrockClientSession session) {
                                setState(State.CONNECTED);
                                packetHandler.setDisconnectCallback(disconnectReason -> runOnClientThread(() -> {
                                    setState(State.DISCONNECTED);
                                    logger.logI18N("log.client.disconnected", playerName, disconnectReason);
                                    reconnectOrStop(address, port);
                                }));
                                logger.logI18N("log.client.connected", player.getName());

                                Client.this.session = session;
                                // Connection established
                                // Make sure to set the packet codec version you wish to use before sending out packets
                                session.setCodec(packetCodec);
                                // Remember to set a packet handler so you receive incoming packets
                                session.setPacketHandler(packetHandler);
                                // Now send packets...
                                packetHandler.handleConnected();

                            }
                        })
                        .connect(addressToConnect);

                clientChannel = channelFuture.channel();
                channelFuture.syncUninterruptibly();

                /*
                bedrockClient.connect(addressToConnect, 1, TimeUnit.SECONDS).whenComplete((session, throwable) -> runOnClientThread(() -> {
                    if (throwable != null) {
                        return;
                    }
                    setState(State.CONNECTED);
                    session.addDisconnectHandler(disconnectReason -> runOnClientThread(() -> {
                        setState(State.DISCONNECTED);
                        logger.logI18N("log.client.disconnected", playerName, disconnectReason.name());
                        reconnectOrStop(address, port);
                    }));
                    logger.logI18N("log.client.connected", player.getName());
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

                 */
            } catch (Throwable throwable) {
                if (clientChannel != null) {
                    clientChannel.close();
                }
                setState(State.DISCONNECTED);
                reconnectOrStop(address, port);
                logger.logI18N("log.client.disconnected", playerName, throwable);
            }
        });
    }

    private void reconnectOrStop(String address, int port) {
        if (!stop.get() && autoReconnect.get()) {
            reconnectTask = new ReconnectTask(Client.this, address, port, reconnectDelay + ThreadLocalRandom.current().nextInt((int) Math.min(reconnectDelay, Integer.MAX_VALUE)));
        } else {
            setState(State.STOPPED);
            logger.logI18N("log.client.stopped", playerName);
        }
    }

    public void sendPacket(BedrockPacket packet) {
        if (isConnected()) {
            session.sendPacket(packet);
        }
    }

    public PacketSender getPacketSender() {
        return this.packetSender;
    }

    public String getHostAddress() {
        SocketAddress socketAddress = session.getSocketAddress();
        if (socketAddress instanceof InetSocketAddress) {
            return ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
        }
        return "";
    }

    public int getPort() {
        SocketAddress socketAddress = session.getSocketAddress();
        if (socketAddress instanceof InetSocketAddress) {
            return ((InetSocketAddress) socketAddress).getPort();
        }
        return 0;
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    public void disconnect() {
        if (isConnected()) {
            setState(State.DISCONNECTING);
            if (clientChannel != null)
                clientChannel.close();
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
                    logger.logI18N("log.client.stopped", playerName);
                }
            } finally {
                if (latch != null)
                    latch.countDown();
            }
        });
        if (wait) {
            try {
                latch.await();
            } catch (InterruptedException ignored) {
            }
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

    public SkinData createSkinData() {
        SkinData skin = SkinData.createFromSkinJson(skinType.getSkinDataJson());
        skin.setClientRandomId(ThreadLocalRandom.current().nextLong());
        skin.setCurrentInputMode(1);
        skin.setDefaultInputMode(1);
        skin.setDeviceId(deviceId.toString());
        skin.setDeviceModel("FakePlayer");
        skin.setDeviceOS(1);
        skin.setGameVersion(packetCodec.getMinecraftVersion());
        skin.setGuiScale(0);
        skin.setLanguageCode("zh_CN");
        skin.setPlatformOfflineId("");
        skin.setPlatformOnlineId("");
        skin.setSelfSignedId(player.getClientUUID().toString());
        skin.setServerAddress(getHostAddress() + ":" + getPort());
        skin.setThirdPartyName(player.getName());
        if (skinType == SkinType.CUSTOM || skinType == SkinType.CUSTOM_SLIM) {
            skin.setSkinId(UUID.randomUUID() + skin.getSkinId() + UUID.randomUUID());
            skin.setSkinImageWidth(customSkinImageWidth);
            skin.setSkinImageHeight(customSkinImageHeight);
            skin.setSkinData(customSkinImageData);
        }
        return skin;
    }

    public void update() {
        if (reconnectTask != null) {
            reconnectTask.tick();
        }
        if (!isConnected() && !receivedPackets.isEmpty()) {
            receivedPackets.clear();
        }
        if (isConnected()) {
            while (!receivedPackets.isEmpty()) {
                BedrockPacket packet = receivedPackets.poll();
                if (packet != null) {
                    packet.handle(packetHandler);
                }
            }
            this.level.tick();
        }
        while (!runnableQueue.isEmpty()) {
            Runnable runnable = runnableQueue.poll();
            if (runnable != null) {
                runnable.run();
            }
        }
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

    public BedrockCodec getPacketCodec() {
        return packetCodec;
    }

    public BedrockCodec getDefaultPacketCodec() {
        return defaultPacketCodec;
    }

    public void setDefaultPacketCodec(BedrockCodec defaultPacketCodec) {
        this.defaultPacketCodec = defaultPacketCodec;
    }

    public ClientPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void setPacketHandler(ClientPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

//    public BedrockClient getBedrockClient() {
//        return bedrockClient;
//    }
//
//    public void setBedrockClient(BedrockClient bedrockClient) {
//        this.bedrockClient = bedrockClient;
//    }

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

    public SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public int getCustomSkinImageWidth() {
        return customSkinImageWidth;
    }

    public void setCustomSkinImageWidth(int customSkinImageWidth) {
        this.customSkinImageWidth = customSkinImageWidth;
    }

    public int getCustomSkinImageHeight() {
        return customSkinImageHeight;
    }

    public void setCustomSkinImageHeight(int customSkinImageHeight) {
        this.customSkinImageHeight = customSkinImageHeight;
    }

    public String getCustomSkinImageData() {
        return customSkinImageData;
    }

    public void setCustomSkinImageData(String customSkinImageData) {
        this.customSkinImageData = customSkinImageData;
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

    public enum SkinType {
        STEVE(Resources.SKIN_DATA_STEVE_JSON),
        ALEX(Resources.SKIN_DATA_ALEX_JSON),
        CUSTOM(Resources.SKIN_DATA_CUSTOM_JSON),
        CUSTOM_SLIM(Resources.SKIN_DATA_CUSTOM_SLIM_JSON);

        private final String skinDataJson;

        SkinType(String skinDataJson) {
            this.skinDataJson = skinDataJson;
        }

        public String getSkinDataJson() {
            return skinDataJson;
        }
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
        private final Client client;

        public ClientThread(Client client) {
            this.client = client;
            setPriority(Thread.MAX_PRIORITY);
        }

        public void setStop() {
            stop.set(true);
        }

        @Override
        public void run() {
            started = true;
            JSLoader.initContext();
            while (!stop.get()) {
                long startTime = System.currentTimeMillis();
                try {
                    client.update();
                } catch (Throwable t) {
                    client.logger.log(client.playerName, " 客户端线程发生异常: ", t);
                }
                long time = System.currentTimeMillis() - startTime;
                if (time >= MIN_TICK_TIME) {
                    continue;
                }
                try {
                    Thread.sleep(MIN_TICK_TIME - time);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public boolean isStarted() {
            return started;
        }
    }
}
