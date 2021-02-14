package com.ddf.fakeplayer.cli;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.Config;
import com.ddf.fakeplayer.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CLIMain {
    private boolean stopped = false;
    private final List<Client> clients = new ArrayList<>();
    private final Config config;

    public CLIMain(Config config) {
        this.config = config;
    }

    public void start() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        if (!config.isConfigured()) {
            System.out.println("请输入服务器地址，默认localhost，按回车键结束输入: ");
            String address = reader.readLine();
            if (!address.isEmpty()) {
                config.setServerAddress(address);
            }
            System.out.println("请输入服务器端口，默认19132，按回车键结束输入: ");
            String port = reader.readLine();
            if (!port.isEmpty()) {
                config.setServerPort(Integer.parseInt(port));
            }
            System.out.println("请输入服务器协议版本，默认408，按回车键结束输入: ");
            String protocolVersion = reader.readLine();
            if (!protocolVersion.isEmpty()) {
                config.setProtocolVersion(Integer.parseInt(protocolVersion));
            }
            System.out.println("配置完成");
            System.out.println("请将以下配置添加到BDS的server.properties中并重启BDS, 再重新运行假人客户端: ");
            System.out.println("trusted-key=" + config.getServerPublicKey());
            config.setConfigured(true);
            config.save();
            return;
        }

        config.getPlayers().forEach(playerData -> addClient(playerData.getName(), playerData.getSkin()));
        System.out.println("启动完成，输入help或?可查看帮助");

        while (!isStopped()) {
            String[] string = reader.readLine().split(" ");
            if (string.length <= 0) {
                continue;
            }
            switch (string[0]) {
                case "？":
                case "?":
                case "help":
                    System.out.println("add [假人名称] - 添加一个假人");
                    System.out.println("remove [假人名称] - 移除一个假人");
                    System.out.println("list - 列出所有假人");
                    System.out.println("publicKey - 显示当前使用的公钥");
                    System.out.println("stop - 断开所有连接并停止运行");
                    System.out.println("help - 显示此帮助信息");
                    break;
                case "add":
                    if (string.length >= 2) {
                        String playerName = string[1];
                        config.addPlayerData(playerName, "steve");
                        addClient(playerName, "steve");
                    }
                    break;
                case "remove":
                    if (string.length >= 2) {
                        String playerName = string[1];
                        config.removePlayerData(playerName);
                        removeClient(playerName);
                    }
                    break;
                case "list":
                    System.out.println("目前共有 " + clients.size() + " 个假人: ");
                    clients.forEach(client -> System.out.println(client.getPlayerName()));
                    break;
                case "publicKey":
                    System.out.println(config.getServerPublicKey());
                    break;
                case "stop":
                    System.out.println("正在停止...");
                    stop();
                    break;
            }
        }
        System.out.println("已停止");
    }

    public void addClient(String name, String skin) {
        Client client = new Client(name, config.getServerKeyPair());
        client.setPacketCodec(config.getPacketCodec());
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
        client.connect(config.getServerAddress(), config.getServerPort());
        clients.add(client);
        try {
            Thread.sleep(config.getPlayerConnectionDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(String name) {
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()) {
            Client client = iterator.next();
            if (client.getPlayerName().equals(name)) {
                client.shutdown();
                iterator.remove();
            }
        }
    }

    public void stop() {
        for (Client client : clients) {
            client.shutdown();
        }
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
