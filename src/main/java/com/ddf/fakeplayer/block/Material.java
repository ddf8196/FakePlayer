package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.util.mc.Color;

import java.util.ArrayList;

public final class Material {
    private static final ArrayList<Material> mMaterials = new ArrayList<>();
    private static boolean mInitialized = false;
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
    private Color mMaterialColor;

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
        this.mMaterialColor = Color.NIL;
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

    public static void initMaterials() {
        Material.teardownMaterials();
        Material.mInitialized = true;
        Material.teardownMaterials();
        Material._setupSurfaceMaterials();
    }

    public static void teardownMaterials() {
        Material.mMaterials.clear();
    }

    public static void _setupSurfaceMaterials() {
        registerMaterial(MaterialType.Air, Settings.Gas, 1.0f);

        registerMaterial(MaterialType.Dirt)
                ._setMapColor(Color.fromARGB(9923917));

        registerMaterial(MaterialType.Wood_0)
                ._setFlammable()
                ._setMapColor(Color.fromARGB(9402184));

        registerMaterial(MaterialType.Stone_0)
                ._setNotAlwaysDestroyable()
                ._setMapColor(Color.fromARGB(7368816));

        registerMaterial(MaterialType.Metal)
                ._setNotAlwaysDestroyable()
                ._setMapColor(Color.fromARGB(10987431));

        registerMaterial(MaterialType.Water_0, Settings.Liquid, 1.0f)
                ._setReplaceable()
                ._setMapColor(Color.fromARGB(4210943));

        registerMaterial(MaterialType.Lava_1, Settings.Liquid, 1.0f)
                ._setReplaceable()
                ._setSuperHot()._setMapColor(Color.fromARGB(16711680));

        registerMaterial(MaterialType.Leaves, Settings.Normal_6, 0.5f)
                ._setNeverBuildable()
                ._setFlammable()
                ._setMapColor(Color.fromARGB(31744));

        registerMaterial(MaterialType.Plant, Settings.Decoration_0, 1.0f)
                ._setMapColor(Color.fromARGB(31744));

        registerMaterial(MaterialType.ReplaceablePlant, Settings.Decoration_0, 1.0f)
                ._setFlammable()
                ._setReplaceable()
                ._setMapColor(Color.fromARGB(31744));

        registerMaterial(MaterialType.Sponge)
                ._setMapColor(Color.fromARGB(15066419));

        registerMaterial(MaterialType.Cloth, Settings.Normal_6, 0.80000001f)
                ._setFlammable()
                ._setMapColor(Color.fromARGB(13092807));

        registerMaterial(MaterialType.Bed, Settings.Normal_6, 0.1f)
                ._setMapColor(Color.fromARGB(10040115));

        registerMaterial(MaterialType.Fire_0, Settings.Gas, 1.0f)
                ._setSuperHot()
                ._setMapColor(Color.fromARGB(16711680));

        registerMaterial(MaterialType.Sand)
                ._setMapColor(Color.fromARGB(16247203));

        registerMaterial(MaterialType.Decoration, Settings.Decoration_0, 1.0f);

        registerMaterial(MaterialType.Glass_0, Settings.Normal_6, 1.0f)
                ._setNeverBuildable();

        registerMaterial(MaterialType.Explosive)
                ._setFlammable()
                ._setNeverBuildable()
                ._setMapColor(Color.fromARGB(16711680));

        registerMaterial(MaterialType.Ice_0)
                ._setNeverBuildable()
                ._setMapColor(Color.fromARGB(10526975));

        registerMaterial(MaterialType.PackedIce)
                ._setMapColor(Color.fromARGB(10526975));

        registerMaterial(MaterialType.TopSnow, Settings.Decoration_0, 0.88999999f)
                ._setReplaceable()
                ._setNeverBuildable()
                ._setNotAlwaysDestroyable()
                ._setMapColor(Color.fromARGB(0xFFFFFF));

        registerMaterial(MaterialType.Snow_0)
                ._setNotAlwaysDestroyable()
                ._setMapColor(Color.fromARGB(0xFFFFFF));

        registerMaterial(MaterialType.Cactus, Settings.Normal_6, 0.5f)
                ._setNeverBuildable()
                ._setMapColor(Color.fromARGB(31744));

        registerMaterial(MaterialType.Clay)
                ._setMapColor(Color.fromARGB(10791096));

        registerMaterial(MaterialType.Vegetable)
                ._setMapColor(Color.fromARGB(31744));

        registerMaterial(MaterialType.Portal_1, Settings.Portal_2);

        registerMaterial(MaterialType.Cake, Settings.Normal_6, 0.80000001f);

        registerMaterial(MaterialType.Web, Settings.Normal_6, 0.80000001f)
                ._setNotBlockingMotion()
                ._setNotBlockingPrecipitation()
                ._setNotAlwaysDestroyable()
                ._setNotSolid()
                ._setMapColor(Color.fromARGB(13092807));

        registerMaterial(MaterialType.RedstoneWire, Settings.Normal_6, 0.80000001f)
                ._setNotBlockingMotion()
                ._setNotBlockingPrecipitation()
                ._setNotSolid();

        registerMaterial(MaterialType.Carpet, Settings.Normal_6, 0.80000001f)
                ._setFlammable()
                ._setNotSolid()
                ._setNotBlockingMotion()
                ._setNotBlockingPrecipitation()
                ._setMapColor(Color.fromARGB(13092807));

        registerMaterial(MaterialType.BuildableGlass, Settings.Normal_6, 1.0f);

        registerMaterial(MaterialType.Slime_1, Settings.Normal_6, 0.1f);

        registerMaterial(MaterialType.Piston, Settings.Normal_6)
                ._setMapColor(Color.fromARGB(7368816));

        registerMaterial(MaterialType.Allow)
                ._setMapColor(Color.fromARGB(9401161));

        registerMaterial(MaterialType.Deny_0)
                ._setMapColor(Color.fromARGB(8750469));

        registerMaterial(MaterialType.Netherwart)
                ._setMapColor(Color.fromARGB(9402184));

        registerMaterial(MaterialType.StoneDecoration, Settings.Decoration_0, 1.0f);

        registerMaterial(MaterialType.Bubble_0, Settings.Decoration_0, 1.0f)
                ._setReplaceable()
                ._setNotSolid()
                ._setNotBlockingMotion()
                ._setNotBlockingPrecipitation();

        registerMaterial(MaterialType.Egg);

        registerMaterial(MaterialType.Barrier, Settings.Normal_6, 1.0f)
                ._setNotBlockingPrecipitation();

        registerMaterial(MaterialType.DecorationFlammable, Settings.Decoration_0, 1.0f)
                ._setFlammable();
    }

    public static Material registerMaterial(MaterialType materialType) {
        return registerMaterial(materialType, Settings.Normal_6, 0.0f);
    }

    public static Material registerMaterial(MaterialType materialType, Material.Settings settings) {
        return registerMaterial(materialType, settings, 0.0f);
    }

    public static Material registerMaterial(MaterialType materialType, Material.Settings settings, float translucency) {
        Material material = new Material(materialType, settings, translucency);
        addMaterial(material);
        return material;
    }

    public static void addMaterial(Material mat) {
        Material.mMaterials.add(mat);
    }

    public static Material getMaterial(MaterialType surface) {
        if (surface.ordinal() > Material.mMaterials.size())
            return Material.getMaterial(MaterialType.Dirt);
        return Material.mMaterials.get(surface.ordinal());
    }

    public final Color getColor() {
        return this.mMaterialColor;
    }

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

    public final boolean isLiquid() {
        return this.mLiquid;
    }

    private Material _setFlammable() {
        this.mFlammable = true;
        return this;
    }

    private void _setMapColor(final Color color) {
        this.mMaterialColor = color;
    }

    private Material _setNeverBuildable() {
        this.mNeverBuildable = true;
        return this;
    }

    private Material _setNotAlwaysDestroyable() {
        this.mAlwaysDestroyable = false;
        return this;
    }

    private Material _setNotBlockingMotion() {
        this.mBlocksMotion = false;
        return this;
    }

    private Material _setNotBlockingPrecipitation() {
        this.mBlocksPrecipitation = false;
        return this;
    }

    private Material _setNotSolid() {
        this.mSolid = false;
        return this;
    }

    private Material _setReplaceable() {
        this.mReplaceable = true;
        return this;
    }

    private Material _setSuperHot() {
        this.mSuperHot = true;
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
