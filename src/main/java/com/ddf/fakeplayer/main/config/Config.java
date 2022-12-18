package com.ddf.fakeplayer.main.config;

import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.util.ProtocolVersionUtil;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.util.*;

public class Config {
    private static final Yaml YAML;

    private boolean debug = true;
    private boolean configured = false;
    private int defaultProtocolVersion = 408;
    private String serverAddress = "localhost";
    private int serverPort = 19132;
    private String serverPublicKey = "";
    private String serverPrivateKey = "";
    private boolean autoReconnect = true;
    private long reconnectDelay = 3000;
    private boolean webSocketEnabled = false;
    private int webSocketPort = 54321;
    private String language = "zh-cn";
    private String theme = "dark";
    private List<PlayerData> players = new ArrayList<>();
    private Map<String, CustomSkinData> customSkins = new LinkedHashMap<>();
    private transient KeyPair serverKeyPair;
    private transient Path configPath;
    private transient BedrockPacketCodec defaultPacketCodec = Bedrock_v408.V408_CODEC;
    private transient Locale locale;

    private static final Object locker;      //多线程锁

    static {
        locker = new Object();
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer();
        PropertyUtils pu = new PropertyUtils() {
            {
                setBeanAccess(BeanAccess.FIELD);
                setSkipMissingProperties(true);
            }
            @Override
            protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
                Set<Property> properties = new LinkedHashSet<>();
                Collection<Property> props = getPropertiesMap(type, bAccess).values();
                props.forEach(property -> {
                    if (property.isReadable() && (isAllowReadOnlyProperties() || property.isWritable())) {
                        properties.add(property);
                    }
                });
                return properties;
            }
        };
        representer.setPropertyUtils(pu);
        YAML = new Yaml(new Constructor(), representer, dumperOptions, loaderOptions);
    }

    public static Config load(Path path) throws IOException {
        if (Files.notExists(path) || !Files.isRegularFile(path)) {
            Config config = new Config();
            config.setConfigPath(path);
            config.generateKeyPair();
            config.save();
            return config;
        } else {
            String str = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            Config config = YAML.loadAs(str, Config.class);
            config.setConfigPath(path);
            try {
                PublicKey publicKey = KeyUtil.decodePublicKey(config.getServerPublicKey());
                PrivateKey privateKey = KeyUtil.decodePrivateKey(config.getServerPrivateKey());
                config.serverKeyPair = new KeyPair(publicKey, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
                config.generateKeyPair();
                config.save();
            }
            try {
                config.defaultPacketCodec = ProtocolVersionUtil.getPacketCodec(config.defaultProtocolVersion);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            config.locale = Locale.forLanguageTag(config.language);
            // 将可能的新增config写回
            config.save();
            return config;
        }
    }

    private void generateKeyPair() {
        serverKeyPair = KeyUtil.generateKeyPair();
        serverPublicKey = KeyUtil.encodeKeyToBase64(serverKeyPair.getPublic());
        serverPrivateKey = KeyUtil.encodeKeyToBase64(serverKeyPair.getPrivate());
    }

    public void save() throws IOException {
        save(configPath);
    }

    public void save(Path path) throws IOException {
        synchronized (locker) {
            String dump = YAML.dumpAsMap(this);
            Files.write(path, dump.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public int getDefaultProtocolVersion() {
        return defaultProtocolVersion;
    }

    public void setDefaultProtocolVersion(int protocolVersion) {
        this.defaultProtocolVersion = protocolVersion;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPrivateKey(String serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    public String getServerPrivateKey() {
        return serverPrivateKey;
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

    public boolean isWebSocketEnabled() {
        return webSocketEnabled;
    }

    public void setWebSocketEnabled(boolean webSocketEnabled) {
        this.webSocketEnabled = webSocketEnabled;
    }

    public int getWebSocketPort() {
        return webSocketPort;
    }

    public void setWebSocketPort(int webSocketPort) {
        this.webSocketPort = webSocketPort;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Map<String, CustomSkinData> getCustomSkins() {
        return customSkins;
    }

    public void setCustomSkins(Map<String, CustomSkinData> customSkins) {
        this.customSkins = customSkins;
    }

    public void addCustomSkin(String name, CustomSkinData customSkinData) {
        this.customSkins.put(name, customSkinData);
    }

    public void removeCustomSkin(String name) {
        this.customSkins.remove(name);
    }

    public CustomSkinData getCustomSkin(String name) {
        return this.customSkins.get(name);
    }

    public void setPlayerDataList(List<PlayerData> players) {
        this.players = players;
    }

    public List<PlayerData> getPlayerDataList() {
        return players;
    }

    public void addPlayerData(PlayerData playerData) {
        synchronized (locker) {
            players.removeIf(playerData1 -> playerData1.getName().equals(playerData.getName()));
            players.add(playerData);
        }
    }

    @Deprecated
    public void addPlayerData(String name, String skin, boolean allowChatMessageControl) {
        PlayerData data = new PlayerData();
        data.setName(name);
        data.setSkin(skin);
        data.setAllowChatMessageControl(allowChatMessageControl);
        addPlayerData(data);
    }

    public void removePlayerData(String name) {
        synchronized (locker) {
            players.removeIf(playerData -> playerData.getName().equals(name));
        }
    }

    public PlayerData getPlayerData(String name) {
        synchronized (locker) {
            for (PlayerData playerData : players) {
                if (playerData.getName().equals(name)) {
                    return playerData;
                }
            }
        }
        return null;
    }

    public KeyPair getServerKeyPair() {
        return serverKeyPair;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
    }

    public BedrockPacketCodec getDefaultPacketCodec() {
        return defaultPacketCodec;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}