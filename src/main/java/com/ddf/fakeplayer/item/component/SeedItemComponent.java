package com.ddf.fakeplayer.item.component;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;

@NotImplemented
public class SeedItemComponent {
    @NotImplemented
    public final boolean useOn(ItemStack instance, Actor entity, final BlockPos blockPos, /*uint8_t FacingID*/int face, final Vec3 clickPos) {
        return false;
    }

    @NotImplemented
    public final boolean isPlanting() {
        return false;
    }
}
