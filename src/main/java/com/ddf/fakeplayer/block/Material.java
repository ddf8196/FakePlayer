package com.ddf.fakeplayer.block;

public class Material {
    private MaterialType mType;
    private boolean mFlammable;
    private boolean mNeverBuildable;
    private boolean mAlwaysDestroyable;
    private boolean mReplaceable;
    private boolean mLiquid;
    private float mTranslucency;
    private boolean mBlocksMotion;
    private boolean mBlocksPrecipitation;
    private boolean mSolid;
    private boolean mSuperHot;
//    private Color mMaterialColor;

    public Material(MaterialType type, Material.Settings settings, float translucency) {
        this.mType = type;
        this.mFlammable = false;
        this.mNeverBuildable = false;
        this.mAlwaysDestroyable = true;
        this.mReplaceable = false;
        this.mLiquid = false;
        this.mTranslucency = translucency;
        this.mBlocksMotion = true;
        this.mBlocksPrecipitation = true;
        this.mSolid = true;
        this.mSuperHot = false;
        //this.mMaterialColor = Color::NIL;
        if (settings == Settings.Gas) {
            this.mSolid = false;
            this.mBlocksMotion = false;
            this.mBlocksPrecipitation = false;
            this._setReplaceable();
        } else if (settings == Settings.Liquid) {
            this.mSolid = false;
            this.mLiquid = true;
            this.mBlocksMotion = false;
            this._setReplaceable();
        } else if ((settings.ordinal() - 3) < 2 ){
            this.mSolid = false;
            this.mBlocksMotion = false;
            this.mBlocksPrecipitation = false;
        }
    }

//    public Color getColor() {
//        return this.mMaterialColor;
//    }

    public final float getTranslucency() {
        return this.mTranslucency;
    }

    public final boolean isType(MaterialType surface) {
        if (surface != MaterialType.Any_1)
            return this.mType == surface;
        return true;
    }

    public final boolean isAlwaysDestroyable() {
        return this.mAlwaysDestroyable;
    }

    public final Material _setReplaceable() {
        this.mReplaceable = true;
        return this;
    }

    public enum Settings {
        Normal_6,
        Gas,
        Liquid,
        Decoration_0,
        Portal_2
    }
}
