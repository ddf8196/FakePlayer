package com.ddf.fakeplayer.websocket;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;

public class Message {
    private static final Gson GSON = new Gson();
    public static final String TYPE_LIST = "list";
    public static final String TYPE_ADD = "add";
    public static final String TYPE_REMOVE = "remove";
    public static final String TYPE_GET_STATE = "getState";
    public static final String TYPE_GET_STATE_ALL = "getState_all";
    public static final String TYPE_DISCONNECT = "disconnect";
    public static final String TYPE_CONNECT = "connect";
    public static final String TYPE_REMOVE_ALL = "remove_all";
    public static final String TYPE_CONNECT_ALL = "connect_all";
    public static final String TYPE_DISCONNECT_ALL = "disconnect_all";
    public static final String TYPE_GET_VERSION = "getVersion";
    public static final String TYPE_SET_CHAT_CONTROL = "setChatControl";

    public static final String EVENT_REMOVE = "remove";
    public static final String EVENT_ADD = "add";
    public static final String EVENT_DISCONNECT = "disconnect";
    public static final String EVENT_CONNECT = "connect";

    private String id;
    private String type;
    private String event;
    private Data data;

    public Message() {}
    public Message(String type) {
        this.type = type;
        this.data = new Data();
    }

    public static Message fromJson(String json) {
        return GSON.fromJson(json, Message.class);
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class PlayerData {
        private Integer state;
        private Boolean allowChatControl;

        public Boolean isAllowChatControl() {
            return allowChatControl;
        }

        public void setAllowChatControl(Boolean allowChatControl) {
            this.allowChatControl = allowChatControl;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }
    }

    public static class Data {
        private String name;
        private String skin;
        private Boolean success;
        private String reason;
        private Integer state;
        private String version;
        private Boolean allowChatControl;
        private List<String> list;
        private Map<String, PlayerData> playersData;

        public Map<String, PlayerData> getPlayersData() {
            return playersData;
        }

        public void setPlayersData(Map<String, PlayerData> playersData) {
            this.playersData = playersData;
        }

        public Boolean isAllowChatControl() {
            return allowChatControl;
        }

        public void setAllowChatControl(Boolean allowChatControl) {
            this.allowChatControl = allowChatControl;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSkin() {
            return skin;
        }

        public void setSkin(String skin) {
            this.skin = skin;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
