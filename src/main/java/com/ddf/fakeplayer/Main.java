package com.ddf.fakeplayer;

import com.ddf.fakeplayer.cli.CLIMain;
import com.ddf.fakeplayer.gui.GUIMain;
import com.ddf.fakeplayer.util.Config;
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

    public abstract void initLogger();

    public synchronized void addPlayer(String name, String skin) {
        config.addPlayerData(name, skin);
        addClient(name, skin).connect(config.getServerAddress(), config.getServerPort());
    }

    public synchronized void removePlayer(String name) {
        removeClient(name);
        config.removePlayerData(name);
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

    public synchronized Client addClient(String name, String skin) {
        removeClient(name);
        Client client = new Client(name, config.getServerKeyPair());
        client.setDefaultPacketCodec(config.getDefaultPacketCodec());
        switch (skin) {
            case "steve":
                client.setSkinDataJson(Resources.SKIN_DATA_STEVE_JSON);
                break;
            case "alex":
                client.setSkinDataJson(Resources.SKIN_DATA_ALEX_JSON);
                break;
        }
        client.setAutoReconnect(config.isAutoReconnect());
        client.setReconnectDelay(config.getReconnectDelay());
        clients.add(client);
        return client;
    }

    public void removeClient(String name) {
        synchronized (clients) {
            clients.removeIf(client -> {
                boolean remove = client.getPlayerName().equals(name);
                if (remove) {
                    client.stop();
                }
                return remove;
            });
        }
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
            } catch (IOException | InterruptedException ignored) {}
            webSocketServer = null;
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
            jarPath = jarPath.replaceFirst("/", "");
        }
        Path baseDir;
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

        if (System.getProperty("fakeplayer.gui", "false").equals("true")) {
            GUIMain.main(config);
        } else {
            CLIMain.main(config);
        }
    }
}
