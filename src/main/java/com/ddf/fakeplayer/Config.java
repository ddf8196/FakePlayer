package com.ddf.fakeplayer;

import com.ddf.fakeplayer.util.KeyUtil;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v390.Bedrock_v390;
import com.nukkitx.protocol.bedrock.v407.Bedrock_v407;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;
import com.nukkitx.protocol.bedrock.v419.Bedrock_v419;
import com.nukkitx.protocol.bedrock.v422.Bedrock_v422;
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
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class Config {
    private static final Yaml yaml;

    //private boolean useGUI = false;
    private boolean configured = false;
    private int protocolVersion = 408;
    private String serverAddress = "localhost";
    private int serverPort = 19132;
    private String serverPublicKey = "";
    private String serverPrivateKey = "";
    private boolean autoReconnect = true;
    private long reconnectDelay = 1000;
    private long playerConnectionDelay = 1000;
    private List<PlayerData> players = new ArrayList<>();
    private transient KeyPair serverKeyPair;
    private transient Path configPath;
    private transient BedrockPacketCodec packetCodec = Bedrock_v408.V408_CODEC;

    static {
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
        yaml = new Yaml(new Constructor(), representer, dumperOptions, loaderOptions);
    }

    public static Config load(Path path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        if (Files.notExists(path) || !Files.isRegularFile(path)) {
            Config config = new Config();
            config.setConfigPath(path);
            KeyPair keyPair = KeyUtil.generateKeyPair();
            config.serverKeyPair = keyPair;
            config.serverPublicKey = KeyUtil.encodeKeyToBase64(keyPair.getPublic());
            config.serverPrivateKey = KeyUtil.encodeKeyToBase64(keyPair.getPrivate());
            config.save();
            return config;
        } else {
            String str = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            Config config = yaml.loadAs(str, Config.class);
            config.setConfigPath(path);
            PublicKey publicKey = KeyUtil.decodePublicKey(config.getServerPublicKey());
            PrivateKey privateKey = KeyUtil.decodePrivateKey(config.getServerPrivateKey());
            config.serverKeyPair = new KeyPair(publicKey, privateKey);
            switch (config.protocolVersion) {
                case 390:
                    config.packetCodec = Bedrock_v390.V390_CODEC;
                    break;
                case 407:
                    config.packetCodec = Bedrock_v407.V407_CODEC;
                    break;
                case 408:
                    config.packetCodec = Bedrock_v408.V408_CODEC;
                    break;
                case 419:
                    config.packetCodec = Bedrock_v419.V419_CODEC;
                    break;
                case 422:
                    config.packetCodec = Bedrock_v422.V422_CODEC;
                    break;
            }
            return config;
        }
    }

    public void save() throws IOException {
        String dump = yaml.dumpAsMap(this);
        Files.write(configPath, dump.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public void setUseGUI(boolean useGUI) {
        //this.useGUI = useGUI;
    }

    public boolean isUseGUI() {
        //return useGUI;
        return false;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
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

    public long getPlayerConnectionDelay() {
        return playerConnectionDelay;
    }

    public void setPlayerConnectionDelay(long playerConnectionDelay) {
        this.playerConnectionDelay = playerConnectionDelay;
    }

    public void setPlayers(List<PlayerData> players) {
        this.players = players;
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    public void addPlayerData(String name, String skin) {
        PlayerData data = new PlayerData();
        data.setName(name);
        data.setSkin(skin);
        players.removeIf(playerData -> playerData.getName().equals(name));
        players.add(data);
    }

    public void removePlayerData(String name) {
        players.removeIf(playerData -> playerData.getName().equals(name));
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

    public BedrockPacketCodec getPacketCodec() {
        return packetCodec;
    }

    public void setPacketCodec(BedrockPacketCodec packetCodec) {
        this.packetCodec = packetCodec;
    }

    public static class PlayerData {
        private String name = "";
        private String skin = "steve";

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSkin(String skin) {
            this.skin = skin;
        }

        public String getSkin() {
            return skin;
        }
    }

}