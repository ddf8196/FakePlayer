package com.ddf.fakeplayer.item.component;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.item.CooldownType;
import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.Vec3;

import java.util.ArrayList;

public class FoodItemComponent {
    private final Item mOwner;
    private int mNutrition = 0;
    private float mSaturationModifier = 1.0f;
    private String mUsingConvertsTo;
    private FoodItemComponent.OnUseAction mOnUseAction = OnUseAction.NONE_11;
    private Vec3 mOnUseRange = new Vec3(8.0f, 8.0f, 8.0f);
    private CooldownType mCoolDownType = CooldownType.TypeNone;
    private int mCooldownTime = 0;
    private boolean mCanAlwaysEat = false;
    private ArrayList<FoodItemComponent.Effect> mEffects = new ArrayList<>();
    private ArrayList</*unsigned int*/Long> mRemoveEffects = new ArrayList<>();

    public FoodItemComponent(Item owner) {
        this.mOwner = owner;
    }

    public int getCoolDownTime() {
        return this.mCooldownTime;
    }

    public CooldownType getCoolDownType() {
        return this.mCoolDownType;
    }

    public void use(ItemStack instance, Player player) {
        if (player.forceAllowEating() || player.isHungry() || this.mCanAlwaysEat) {
            player.startUsingItem(instance, instance.getMaxUseDuration());
        }
    }

    public final boolean useOn(ItemStack instance, Actor entity, final BlockPos blockPos, /*uint8_t FacingID*/int face, final Vec3 clickPos) {
        return false;
    }

    public static class Effect {
        private String descriptionId;
        private int id;
        private int duration;
        private int amplifier;
        private float chance;
    }

    public enum OnUseAction {
        NONE_11(0xFFFFFFFF),
        CHORUS_TELEPORT(0x0),
        SUSPICIOUS_STEW_EFFECT(0x1);

        private final int value;

        OnUseAction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static OnUseAction getByValue(int value) {
            for (OnUseAction onUseAction : values()) {
                if (onUseAction.getValue() == value) {
                    return onUseAction;
                }
            }
            return null;
        }
    }
}
