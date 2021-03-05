package com.ddf.fakeplayer.json.skin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SkinData {
    private static final Gson GSON = new Gson();
    private AnimatedImageData[] AnimatedImageData;
    private String ArmSize;
    private String CapeData;
    private String CapeId;
    private int CapeImageHeight;
    private int CapeImageWidth;
    private boolean CapeOnClassicSkin;
    private long ClientRandomId;
    private int CurrentInputMode;
    private int DefaultInputMode;
    private String DeviceId;
    private String DeviceModel;
    private int DeviceOS;
    private String GameVersion;
    private int GuiScale;
    private String LanguageCode;
    private PersonaPiece[] PersonaPieces;
    private boolean PersonaSkin;
    private PieceTintColor[] PieceTintColors;
    private String PlatformOfflineId;
    private String PlatformOnlineId;
    private boolean PremiumSkin;
    private String SelfSignedId;
    private String ServerAddress;
    private String SkinAnimationData;
    private String SkinColor;
    private String SkinData;
    private String SkinGeometryData;
    private String SkinId;
    private int SkinImageHeight;
    private int SkinImageWidth;
    private String SkinResourcePatch;
    private String ThirdPartyName;
    private boolean ThirdPartyNameOnly;
    private int UIProfile;

    public static SkinData createFromSkinJson(String skinDataJson) {
        return GSON.fromJson(skinDataJson, SkinData.class);
    }

    public String toJsonString(){
        return GSON.toJson(this);
    }

    public AnimatedImageData[] getAnimatedImageData() {
        return AnimatedImageData;
    }

    public void setAnimatedImageData(AnimatedImageData[] animatedImageData) {
        AnimatedImageData = animatedImageData;
    }

    public String getArmSize() {
        return ArmSize;
    }

    public void setArmSize(String armSize) {
        ArmSize = armSize;
    }

    public String getCapeData() {
        return CapeData;
    }

    public void setCapeData(String capeData) {
        CapeData = capeData;
    }

    public String getCapeId() {
        return CapeId;
    }

    public void setCapeId(String capeId) {
        CapeId = capeId;
    }

    public int getCapeImageHeight() {
        return CapeImageHeight;
    }

    public void setCapeImageHeight(int capeImageHeight) {
        CapeImageHeight = capeImageHeight;
    }

    public int getCapeImageWidth() {
        return CapeImageWidth;
    }

    public void setCapeImageWidth(int capeImageWidth) {
        CapeImageWidth = capeImageWidth;
    }

    public boolean isCapeOnClassicSkin() {
        return CapeOnClassicSkin;
    }

    public void setCapeOnClassicSkin(boolean capeOnClassicSkin) {
        CapeOnClassicSkin = capeOnClassicSkin;
    }

    public long getClientRandomId() {
        return ClientRandomId;
    }

    public void setClientRandomId(long clientRandomId) {
        ClientRandomId = clientRandomId;
    }

    public int getCurrentInputMode() {
        return CurrentInputMode;
    }

    public void setCurrentInputMode(int currentInputMode) {
        CurrentInputMode = currentInputMode;
    }

    public int getDefaultInputMode() {
        return DefaultInputMode;
    }

    public void setDefaultInputMode(int defaultInputMode) {
        DefaultInputMode = defaultInputMode;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public int getDeviceOS() {
        return DeviceOS;
    }

    public void setDeviceOS(int deviceOS) {
        DeviceOS = deviceOS;
    }

    public String getGameVersion() {
        return GameVersion;
    }

    public void setGameVersion(String gameVersion) {
        GameVersion = gameVersion;
    }

    public int getGuiScale() {
        return GuiScale;
    }

    public void setGuiScale(int guiScale) {
        GuiScale = guiScale;
    }

    public String getLanguageCode() {
        return LanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public PersonaPiece[] getPersonaPieces() {
        return PersonaPieces;
    }

    public void setPersonaPieces(PersonaPiece[] personaPieces) {
        PersonaPieces = personaPieces;
    }

    public boolean isPersonaSkin() {
        return PersonaSkin;
    }

    public void setPersonaSkin(boolean personaSkin) {
        PersonaSkin = personaSkin;
    }

    public PieceTintColor[] getPieceTintColors() {
        return PieceTintColors;
    }

    public void setPieceTintColors(PieceTintColor[] pieceTintColors) {
        PieceTintColors = pieceTintColors;
    }

    public String getPlatformOfflineId() {
        return PlatformOfflineId;
    }

    public void setPlatformOfflineId(String platformOfflineId) {
        PlatformOfflineId = platformOfflineId;
    }

    public String getPlatformOnlineId() {
        return PlatformOnlineId;
    }

    public void setPlatformOnlineId(String platformOnlineId) {
        PlatformOnlineId = platformOnlineId;
    }

    public boolean isPremiumSkin() {
        return PremiumSkin;
    }

    public void setPremiumSkin(boolean premiumSkin) {
        PremiumSkin = premiumSkin;
    }

    public String getSelfSignedId() {
        return SelfSignedId;
    }

    public void setSelfSignedId(String selfSignedId) {
        SelfSignedId = selfSignedId;
    }

    public String getServerAddress() {
        return ServerAddress;
    }

    public void setServerAddress(String serverAddress) {
        ServerAddress = serverAddress;
    }

    public String getSkinAnimationData() {
        return SkinAnimationData;
    }

    public void setSkinAnimationData(String skinAnimationData) {
        SkinAnimationData = skinAnimationData;
    }

    public String getSkinColor() {
        return SkinColor;
    }

    public void setSkinColor(String skinColor) {
        SkinColor = skinColor;
    }

    public String getSkinData() {
        return SkinData;
    }

    public void setSkinData(String skinData) {
        SkinData = skinData;
    }

    public String getSkinGeometryData() {
        return SkinGeometryData;
    }

    public void setSkinGeometryData(String skinGeometryData) {
        SkinGeometryData = skinGeometryData;
    }

    public String getSkinId() {
        return SkinId;
    }

    public void setSkinId(String skinId) {
        SkinId = skinId;
    }

    public int getSkinImageHeight() {
        return SkinImageHeight;
    }

    public void setSkinImageHeight(int skinImageHeight) {
        SkinImageHeight = skinImageHeight;
    }

    public int getSkinImageWidth() {
        return SkinImageWidth;
    }

    public void setSkinImageWidth(int skinImageWidth) {
        SkinImageWidth = skinImageWidth;
    }

    public String getSkinResourcePatch() {
        return SkinResourcePatch;
    }

    public void setSkinResourcePatch(String skinResourcePatch) {
        SkinResourcePatch = skinResourcePatch;
    }

    public String getThirdPartyName() {
        return ThirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        ThirdPartyName = thirdPartyName;
    }

    public boolean isThirdPartyNameOnly() {
        return ThirdPartyNameOnly;
    }

    public void setThirdPartyNameOnly(boolean thirdPartyNameOnly) {
        ThirdPartyNameOnly = thirdPartyNameOnly;
    }

    public int getUIProfile() {
        return UIProfile;
    }

    public void setUIProfile(int UIProfile) {
        this.UIProfile = UIProfile;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
