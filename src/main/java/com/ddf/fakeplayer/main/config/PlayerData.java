package com.ddf.fakeplayer.main.config;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private String name = "";
    private String skin = "steve";
    private String customSkin = "";
    private boolean allowChatMessageControl = true;
    private boolean onlineStatus = false;
    private List<String> scripts = new ArrayList<>();

    public PlayerData() {}

    public PlayerData(String name) {
        this.name = name;
    }

    public PlayerData(String name, String skin) {
        this(name, skin, false);
    }

    public PlayerData(String name, String skin, boolean allowChatMessageControl) {
        this.name = name;
        this.skin = skin;
        this.allowChatMessageControl = allowChatMessageControl;
    }

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

    public String getCustomSkin() {
        return customSkin;
    }

    public void setCustomSkin(String customSkin) {
        this.customSkin = customSkin;
    }

    public boolean isAllowChatMessageControl() {
        return allowChatMessageControl;
    }

    public void setAllowChatMessageControl(boolean allowChatMessageControl) {
        this.allowChatMessageControl = allowChatMessageControl;
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean status) {
        this.onlineStatus = status;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void addScript(String path) {
        scripts.add(path);
    }
}
