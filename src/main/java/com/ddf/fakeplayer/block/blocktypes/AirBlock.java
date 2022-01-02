package com.ddf.fakeplayer.block.blocktypes;

import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.BlockProperty;
import com.ddf.fakeplayer.block.BlockRenderLayer;
import com.ddf.fakeplayer.block.Material;

public class AirBlock extends BlockLegacy {
    public AirBlock(String nameId, int id, Material material) {
        super(nameId, id, material);
        this.setSolid(false);
        this.mThickness = 0.0f;
        this.mRenderLayer = BlockRenderLayer.RENDERLAYER_OPAQUE;
        this.mProperties = BlockProperty.None_43.getValue();
        this.mCanSlide = false;
        this.mCanInstatick = false;
        this.mTranslucency = Math.max(0.80000001f, this.mMaterial.getTranslucency());
        this.mExplosionResistance = 0.0f;
        this.mFriction = 0.89999998f;
    }
}
