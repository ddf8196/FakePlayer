package com.ddf.fakeplayer.json.skin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SkinData {
    public AnimatedImageData[] AnimatedImageData;
    public String ArmSize;
    public String CapeData;
    public String CapeId;
    public int CapeImageHeight;
    public int CapeImageWidth;
    public boolean CapeOnClassicSkin;
    public long ClientRandomId;
    public int CurrentInputMode;
    public int DefaultInputMode;
    public String DeviceId;
    public String DeviceModel;
    public int DeviceOS;
    public String GameVersion;
    public int GuiScale;
    public String LanguageCode;
    public PersonaPiece[] PersonaPieces;
    public boolean PersonaSkin;
    public PieceTintColor[] PieceTintColors;
    public String PlatformOfflineId;
    public String PlatformOnlineId;
    public boolean PremiumSkin;
    public String SelfSignedId;
    public String ServerAddress;
    public String SkinAnimationData;
    public String SkinColor;
    public String SkinData;
    public String SkinGeometryData;
    public String SkinId;
    public int SkinImageHeight;
    public int SkinImageWidth;
    public String SkinResourcePatch;
    public String ThirdPartyName;
    public boolean ThirdPartyNameOnly;
    public int UIProfile;

    public static SkinData createFromSkinJson(String skinDataJson) {
        Gson gson = new Gson();
        SkinData skinData = gson.fromJson(skinDataJson, SkinData.class);
        return skinData;
    }

    public String toJsonString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
