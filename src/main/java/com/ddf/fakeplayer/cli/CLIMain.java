package com.ddf.fakeplayer.cli;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.util.Config;
import com.ddf.fakeplayer.Main;
import com.ddf.fakeplayer.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class CLIMain extends Main {
    private boolean stopped = false;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private CLIMain(Config config) throws IOException {
        super(config);
        logger.log("配置文件已加载: " + config.getConfigPath().toRealPath().toString());
    }

    @Override
    public void initLogger() {
        Logger.init(new Logger() {
            @Override
            public synchronized void log(String log) {
                System.out.println(LocalDateTime.now().format(Logger.FORMATTER) + log);
            }
        });
    }

    public void start() throws IOException {
        if (!config.isConfigured()) {
            logger.log("请输入服务器地址，默认localhost，按回车键结束输入: ");
            String address = readLine();
            if (address == null) {
                return;
            } else if (!address.isEmpty()) {
                config.setServerAddress(address);
            }
            logger.log("请输入服务器端口，默认19132，按回车键结束输入: ");
            String port = readLine();
            if (port == null) {
                return;
            } else if (!port.isEmpty()) {
                config.setServerPort(Integer.parseInt(port));
            }

            logger.log("配置完成");
            logger.log("请将以下配置添加到BDS的server.properties中并重启BDS: ");
            System.out.println("trusted-key=" + config.getServerPublicKey());
            config.setConfigured(true);
            config.save();
        }

        config.getPlayers().forEach(playerData -> addClient(playerData.getName(), playerData.getSkin()));
        clients.forEach(client -> client.connect(config.getServerAddress(), config.getServerPort()));

        setWebSocketEnabled(config.isWebSocketEnabled());
        logger.log("启动完成，输入help或?可查看帮助");

        while (!isStopped()) {
            String line = readLine();
            if (line == null) {
                return;
            }
            String[] string = line.split(" ");
            if (string.length <= 0) {
                continue;
            }
            switch (string[0]) {
                case "？":
                case "?":
                case "help":
                    logger.log("add 假人名称 - 添加一个假人");
                    logger.log("remove 假人名称 - 移除一个假人");
                    logger.log("list - 列出所有假人");
                    logger.log("publicKey - 显示当前使用的公钥");
                    logger.log("stop - 断开所有连接并停止运行");
                    logger.log("enableWebSocket - 启用WebSocket");
                    logger.log("disableWebSocket - 禁用WebSocket");
                    logger.log("help - 显示此帮助信息");
                    break;
                case "add":
                    if (string.length >= 2) {
                        String playerName = string[1];
                        addPlayer(playerName, "steve");
                    }
                    break;
                case "remove":
                    if (string.length >= 2) {
                        String playerName = string[1];
                        removePlayer(playerName);
                    }
                    break;
                case "list":
                    logger.log("目前共有 " + clients.size() + " 个假人: ");
                    clients.forEach(client -> logger.log(client.getPlayerName()));
                    break;
                case "publicKey":
                    logger.log(config.getServerPublicKey());
                    break;
                case "stop":
                    logger.log("正在停止...");
                    stop();
                    break;
                case "enableWebSocket":
                    setWebSocketEnabled(true);
                    break;
                case "disableWebSocket":
                    setWebSocketEnabled(false);
                    break;
            }
        }
        logger.log("已停止");
    }

    private String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public Client addClient(String name, String skin) {
        Client client = super.addClient(name, skin);
        try {
            Thread.sleep(config.getPlayerConnectionDelay());
        } catch (InterruptedException ignored) {}
        return client;
    }


    public void stop() {
        clients.forEach(client -> client.setStop(true));
        clients.forEach(client -> client.stop(true));
        stopWebSocket();
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    public static void main(Config config) throws IOException {
        CLIMain cliMain = new CLIMain(config);
        cliMain.start();
        config.save();
    }
}
