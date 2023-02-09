package com.ddf.fakeplayer.main;

import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.actor.attribute.SharedAttributes;
import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.main.cli.CLIMain;
import com.ddf.fakeplayer.main.config.CustomSkinData;
import com.ddf.fakeplayer.main.config.PlayerData;
import com.ddf.fakeplayer.main.gui.GUIMain;
import com.ddf.fakeplayer.main.config.Config;
import com.ddf.fakeplayer.system.VanillaWorldSystems;
import com.ddf.fakeplayer.util.Logger;
import com.ddf.fakeplayer.websocket.WebSocketServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Main {
    private static Path baseDir;
    protected Logger logger;
    protected Config config;
    protected final List<Client> clients;
    protected WebSocketServer webSocketServer;

    protected Main(Config config) {
        initLogger();
        logger = Logger.getLogger();
        this.config = config;
        this.clients = Collections.synchronizedList(new ArrayList<>());
    }

    public static Path getBaseDir() {
        return Main.baseDir;
    }

    public abstract void initLogger();

    @Deprecated
    public void addPlayer(String name, String skin) {
        addPlayer(new PlayerData(name, skin));
    }

    public synchronized void addPlayer(PlayerData playerData, Boolean connect) {
        config.addPlayerData(playerData);
        Client client = addClient(playerData);
        if (connect)
            client.connect(config.getServerAddress(), config.getServerPort());
        if (webSocketServer != null && config.isWebSocketEnabled())
            webSocketServer.sendAddPlayerMessage(client);
    }

    public synchronized void addPlayer(PlayerData playerData) {
        addPlayer(playerData, true);
    }

    public synchronized void removePlayer(String name) {
        removeClient(name);
        config.removePlayerData(name);
        if (webSocketServer != null && config.isWebSocketEnabled())
            webSocketServer.sendRemovePlayerMessage(name);
    }

    public Client getClient(String name) {
        synchronized (clients) {
            for (Client client : clients) {
                if (client.getPlayerName().equals(name)) {
                    return client;
                }
            }
            return null;
        }
    }

    public synchronized Client addClient(PlayerData playerData) {
        removeClient(playerData.getName());
        Client client = new Client(playerData.getName(), config.getServerKeyPair());
        client.setAllowChatMessageControl(playerData.isAllowChatMessageControl());
        client.setDefaultPacketCodec(config.getDefaultPacketCodec());
        switch (playerData.getSkin().toLowerCase()) {
            case "steve":
                client.setSkinType(Client.SkinType.STEVE);
                break;
            case "alex":
                client.setSkinType(Client.SkinType.ALEX);
                break;
            case "custom":
                CustomSkinData skinData = config.getCustomSkin(playerData.getCustomSkin());
                if (skinData == null) {
                    client.setSkinType(Client.SkinType.STEVE);
                    break;
                }
                client.setSkinType(skinData.isSlim()
                        ? Client.SkinType.CUSTOM_SLIM : Client.SkinType.CUSTOM);
                client.setCustomSkinImageWidth(skinData.getImageWidth());
                client.setCustomSkinImageHeight(skinData.getImageHeight());
                client.setCustomSkinImageData(skinData.getImageData());
                break;
        }
        client.setAutoReconnect(config.isAutoReconnect());
        client.setReconnectDelay(config.getReconnectDelay());
        client.addStateChangedListener((client1, oldState, currentState) -> {
            switch (currentState) {
                case CONNECTED:
                    if (webSocketServer != null && config.isWebSocketEnabled())
                        webSocketServer.sendConnectPlayerMessage(client1);
                    config.getPlayerData(client1.getPlayerName()).setOnlineStatus(true);
                    break;
                case DISCONNECTED:
                    if (webSocketServer != null && config.isWebSocketEnabled())
                        webSocketServer.sendDisconnectPlayerMessage(client1);
                    config.getPlayerData(client1.getPlayerName()).setOnlineStatus(false);
                    break;
            }
            // 将状态更改写回到文件
            try {
                config.save();
            } catch (IOException ignored) {
            }
        });
        clients.add(client);
        return client;
    }

    public void removeClient(String name) {
        clients.removeIf(client -> {
            boolean remove = client.getPlayerName().equals(name);
            if (remove)
                client.close();
            return remove;
        });
    }

    public synchronized Config getConfig() {
        return config;
    }

    public synchronized void setConfig(Config config) {
        this.config = config;
    }

    public List<Client> getClients() {
        return clients;
    }

    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }

    public boolean isWebSocketStarted() {
        return webSocketServer != null;
    }

    public void setWebSocketEnabled(boolean enabled) {
        config.setWebSocketEnabled(enabled);
        if (enabled) {
            if (isWebSocketStarted()) {
                stopWebSocket();
            }
            webSocketServer = new WebSocketServer(this, config.getWebSocketPort());
            webSocketServer.start();
        } else {
            stopWebSocket();
        }
    }

    public void updateWebSocketPort(int port) {
        if (port != config.getWebSocketPort()) {
            config.setWebSocketPort(port);
            if (config.isWebSocketEnabled()) {
                if (isWebSocketStarted()) {
                    stopWebSocket();
                }
                webSocketServer = new WebSocketServer(this, port);
                webSocketServer.start();
            }
        }
    }

    public void stopWebSocket() {
        if (webSocketServer != null) {
            try {
                webSocketServer.stop();
            } catch (InterruptedException ignored) {
            }
            webSocketServer = null;
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            jarPath = jarPath.replaceFirst("/", "");
        }
        if (jarPath.endsWith(".jar")) {
            baseDir = Paths.get(jarPath)
                    .getParent()
                    .getParent()
                    .toAbsolutePath();
        } else {
            baseDir = Paths.get(".").toAbsolutePath();
        }
        Path configDir = baseDir.resolve("config");
        Files.createDirectories(configDir);
        Path configPath = configDir.resolve("config.yaml");
        Config config = Config.load(configPath);
        I18N.setCurrentLocale(config.getLocale());

        VanillaWorldSystems.init(null, null);
        SharedAttributes.init();
        JSLoader.init();

        if (System.getProperty("fakeplayer.nogui", "false").equals("true")) {
            CLIMain.main(config);
        } else {
            GUIMain.main(config);
        }
    }
}
