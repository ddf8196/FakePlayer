package com.ddf.fakeplayer.level;

import com.ddf.fakeplayer.actor.definition.ActorDefinitionIdentifier;
import com.ddf.fakeplayer.block.BlockSourceListener;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;

@NotImplemented
public interface LevelListener extends BlockSourceListener {
    default void levelSoundEvent(LevelSoundEvent type, final Vec3 pos, int data, final ActorDefinitionIdentifier entityType, boolean isBabyMob, boolean isGlobal) {
    }
}
