package com.ddf.fakeplayer.websocket;

import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.main.Main;
import com.ddf.fakeplayer.VersionInfo;
import com.ddf.fakeplayer.main.config.PlayerData;
import com.ddf.fakeplayer.util.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private Logger logger;
    private Main main;

    public WebSocketServer(Main main, int port) {
        super(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
        logger = Logger.getLogger();
        this.main = main;
    }

    @Override
    public void onStart() {
        logger.log("WebSocket已启动, 地址: ", getAddress().getAddress().getHostAddress(), ":" + getPort());
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        logger.log("WebSocket已停止");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (!conn.getRemoteSocketAddress().getAddress().isLoopbackAddress()) {
            conn.close();
            return;
        }
        String remoteAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + ":" + conn.getRemoteSocketAddress().getPort();
        logger.log(remoteAddress, " 已连接到WebSocket");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String remoteAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + ":" + conn.getRemoteSocketAddress().getPort();
        logger.log(remoteAddress, " 已断开WebSocket连接");
    }

    public void sendAddPlayerMessage(Client client) {
        Message msg = new Message();
        msg.setData(new Message.Data());
        msg.setEvent(Message.EVENT_ADD);
        msg.getData().setName(client.getPlayerName());
        msg.getData().setState(client.getState().ordinal());
        for (WebSocket conn : this.getConnections())
            conn.send(msg.toString());
    }

    public void sendRemovePlayerMessage(String name) {
        Message msg = new Message();
        msg.setData(new Message.Data());
        msg.setEvent(Message.EVENT_REMOVE);
        msg.getData().setName(name);
        for (WebSocket conn : this.getConnections())
            conn.send(msg.toString());
    }

    public void sendConnectPlayerMessage(Client client) {
        Message msg = new Message();
        msg.setData(new Message.Data());
        msg.setEvent(Message.EVENT_CONNECT);
        msg.getData().setName(client.getPlayerName());
        msg.getData().setState(client.getState().ordinal());
        for (WebSocket conn : this.getConnections())
            conn.send(msg.toString());
    }

    public void sendDisconnectPlayerMessage(Client client) {
        Message msg = new Message();
        msg.setData(new Message.Data());
        msg.setEvent(Message.EVENT_DISCONNECT);
        msg.getData().setName(client.getPlayerName());
        msg.getData().setState(client.getState().ordinal());
        for (WebSocket conn : this.getConnections())
            conn.send(msg.toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Message msg = Message.fromJson(message);
        String id = msg.getId();
        switch (msg.getType()) {
            case Message.TYPE_LIST: {
                Message response = new Message(Message.TYPE_LIST);
                if (id != null) response.setId(id);
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client -> nameList.add(client.getPlayerName()));
                response.getData().setList(nameList);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_ADD: {
                Message response = new Message(Message.TYPE_ADD);
                Message.Data data = msg.getData();
                if (id != null) response.setId(id);
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                if (main.getClient(name) != null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人已存在");
                    conn.send(response.toString());
                    break;
                }
                String skin = data.getSkin();
                if (skin == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("皮肤不能为空");
                    conn.send(response.toString());
                    break;
                }
                Boolean allowCC = data.isAllowChatControl();
                if (allowCC == null) allowCC = false;
                switch (skin) {
                    default:
                        response.getData().setSuccess(false);
                        response.getData().setReason("无效的皮肤: " + skin);
                        conn.send(response.toString());
                        break;
                    case "steve":
                    case "alex":
                        main.addPlayer(new PlayerData(name, skin, allowCC));
                        response.getData().setSuccess(true);
                        response.getData().setReason("");
                        conn.send(response.toString());
                        break;
                }
                break;
            }
            case Message.TYPE_REMOVE: {
                Message response = new Message(Message.TYPE_REMOVE);
                Message.Data data = msg.getData();
                if (id != null) { response.setId(id); }
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                if (main.getClient(name) == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                main.removePlayer(name);
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_GET_STATE: {
                Message response = new Message(Message.TYPE_GET_STATE);
                Message.Data data = msg.getData();
                if (id != null) response.setId(id);
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                Client client = main.getClient(name);
                if (client == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                Boolean allowCC = main.getConfig().getPlayerData(name).isAllowChatMessageControl();
                response.getData().setState(client.getState().ordinal());
                response.getData().setAllowChatControl(allowCC);
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_GET_STATE_ALL: {
                Message response = new Message(Message.TYPE_GET_STATE_ALL);
                if (id != null) response.setId(id);
                Map<String, Message.PlayerData> playersData = new HashMap<>(Collections.emptyMap());
                main.getClients().forEach(client -> {
                    String name = client.getPlayerName();
                    Message.PlayerData data = new Message.PlayerData();
                    data.setState(client.getState().ordinal());
                    data.setAllowChatControl(main.getConfig().getPlayerData(name)
                            .isAllowChatMessageControl());
                    playersData.put(name, data);
                });
                response.getData().setPlayersData(playersData);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_DISCONNECT: {
                Message response = new Message(Message.TYPE_DISCONNECT);
                Message.Data data = msg.getData();
                if (id != null) response.setId(id);
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                Client client = main.getClient(name);
                if (client == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                client.stop();
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_CONNECT: {
                Message response = new Message(Message.TYPE_CONNECT);
                Message.Data data = msg.getData();
                if (id != null) response.setId(id);
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                Client client = main.getClient(name);
                if (client == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                client.connect(main.getConfig().getServerAddress(), main.getConfig().getServerPort());
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_REMOVE_ALL: {
                Message response = new Message(Message.TYPE_REMOVE_ALL);
                if (id != null) response.setId(id);
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client -> nameList.add(client.getPlayerName()));
                nameList.forEach(name -> main.removePlayer(name));
                response.getData().setList(nameList);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_CONNECT_ALL:{
                Message response = new Message(Message.TYPE_CONNECT_ALL);
                if (id != null) response.setId(id);
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client ->{
                    if (!client.isConnected()) {
                        String name = client.getPlayerName();
                        client.connect(main.getConfig().getServerAddress(),
                                main.getConfig().getServerPort());
                        nameList.add(name);
                    }
                });
                response.getData().setList(nameList);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_DISCONNECT_ALL:{
                Message response = new Message(Message.TYPE_DISCONNECT_ALL);
                if (id != null) response.setId(id);
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client -> {
                    if (!client.isStop()) {
                        String name = client.getPlayerName();
                        client.stop();
                        nameList.add(name);
                    }
                });
                response.getData().setList(nameList);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_GET_VERSION:{
                Message response = new Message(Message.TYPE_GET_VERSION);
                if (id != null) response.setId(id);
                response.getData().setVersion(VersionInfo.VERSION);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_SET_CHAT_CONTROL:{
                Message response = new Message(Message.TYPE_SET_CHAT_CONTROL);
                Message.Data data = msg.getData();
                if (id != null) response.setId(id);
                if (data == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("data不能为空");
                    conn.send(response.toString());
                    break;
                }
                String name = data.getName();
                response.getData().setName(name);
                if (name == null || name.isEmpty()) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("名称不能为空");
                    conn.send(response.toString());
                    break;
                }
                if (data.isAllowChatControl() == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("allowChatControl不能为空");
                    conn.send(response.toString());
                    break;
                }
                Client client = main.getClient(name);
                if (client == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                main.getConfig().getPlayerData(name)
                        .setAllowChatMessageControl(data.isAllowChatControl());
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logger.log("WebSocket: ", e);
    }

}
