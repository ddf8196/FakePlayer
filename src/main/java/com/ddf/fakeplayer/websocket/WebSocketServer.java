package com.ddf.fakeplayer.websocket;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.Main;
import com.ddf.fakeplayer.util.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
        logger.log("WebSocket已启动, 地址: " + getAddress().getAddress().getHostAddress() + ":" + getPort());
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
        String remoteAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        logger.log(remoteAddress + " 已连接到WebSocket");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String remoteAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        logger.log(remoteAddress + " 已断开WebSocket连接");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Message msg = Message.fromJson(message);
        String id = msg.getId();
        switch (msg.getType()) {
            case Message.TYPE_LIST: {
                Message response = new Message(Message.TYPE_LIST);
                if (id != null) { response.setId(id); }
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client -> nameList.add(client.getPlayerName()));
                response.getData().setList(nameList);
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_ADD: {
                Message response = new Message(Message.TYPE_ADD);
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
                switch (skin) {
                    default:
                        response.getData().setSuccess(false);
                        response.getData().setReason("无效的皮肤: " + skin);
                        conn.send(response.toString());
                        break;
                    case "steve":
                    case "alex":
                        main.addPlayer(name, skin);
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
                Client client = main.getClient(name);
                if (client == null) {
                    response.getData().setSuccess(false);
                    response.getData().setReason("假人不存在");
                    conn.send(response.toString());
                    break;
                }
                response.getData().setState(client.getState().ordinal());
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_DISCONNECT: {
                Message response = new Message(Message.TYPE_DISCONNECT);
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
                if (id != null) { response.setId(id); }
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client ->{
                    String name = client.getPlayerName();
                    main.removePlayer(name);
                    nameList.add(name);
                });
                response.getData().setList(nameList);
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_CONNECT_ALL:{
                Message response = new Message(Message.TYPE_CONNECT_ALL);
                if (id != null) { response.setId(id); }
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
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
                break;
            }
            case Message.TYPE_DISCONNECT_ALL:{
                Message response = new Message(Message.TYPE_CONNECT_ALL);
                if (id != null) { response.setId(id); }
                List<String> nameList = new ArrayList<>();
                main.getClients().forEach(client ->{
                    if (client.isConnected()) {
                        String name = client.getPlayerName();
                        client.stop();
                        nameList.add(name);
                    }
                });
                response.getData().setList(nameList);
                response.getData().setSuccess(true);
                response.getData().setReason("");
                conn.send(response.toString());
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logger.log("WebSocket: " + e);
    }

}
