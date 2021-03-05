package com.ddf.fakeplayer.json;

public class ExtraData {
    private String XUID;
    private String displayName;
    private String identity;
    private String titleId;

    public ExtraData() {}

    public ExtraData(String XUID, String displayName, String identity, String titleId) {
        this.XUID = XUID;
        this.displayName = displayName;
        this.identity = identity;
        this.titleId = titleId;
    }

    public String getXUID() {
        return XUID;
    }

    public void setXUID(String XUID) {
        this.XUID = XUID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }
}
