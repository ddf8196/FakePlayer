package com.ddf.fakeplayer.actor.mob.effect;

import com.ddf.fakeplayer.actor.attribute.Attribute;
import com.ddf.fakeplayer.actor.attribute.AttributeBuff;
import com.ddf.fakeplayer.actor.attribute.AttributeModifier;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Pair;
import com.ddf.fakeplayer.util.mc.Color;

import java.util.ArrayList;

@NotImplemented
public class MobEffect {
    private final int mId;
    private boolean mIsHarmful;
    private Color mColor;
    private String mDescriptionId;
    private int mIcon;
    private float mDurationModifier;
    private boolean mIsDisabled;
    private String mResourceName;
    private String mIconName;
    private boolean mEffectVisible;
    private String mComponentName;
    private Amplifier mValueAmplifier;
    private Amplifier mDurationAmplifier;
    private ArrayList<Pair<Attribute, AttributeBuff>> mAttributeBuffs;
    private ArrayList<Pair<Attribute, AttributeModifier>> mAttributeModifiers;

    public MobEffect(int id, final String resourceName, final String locName, boolean isHarmful, int color, int icon, final String iconName, boolean drawParticles) {
        this.mId = id;
        this.mIsHarmful = isHarmful;
        this.mColor = Color.fromRGB(color);
        this.mDescriptionId = locName;
        this.mIcon = icon;
        this.mIsDisabled = false;
        this.mResourceName = resourceName;
        this.mIconName = iconName;
        this.mEffectVisible = drawParticles;
        this.mComponentName = "minecraft:effect." + resourceName;
        this.mAttributeBuffs = new ArrayList<>();
        this.mAttributeModifiers = new ArrayList<>();
        if (isHarmful)
            this.mDurationModifier = 0.5f;
        else
            this.mDurationModifier = 1.0f;
    }
}
