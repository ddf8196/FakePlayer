package com.ddf.fakeplayer.actor.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ability {
    private Type mType;
    private Value mValue;
    private List<Options> mOptions;

    public Ability(boolean value, Options... ops) {
        this.mOptions = new ArrayList<>();
        this.mType = Type.Bool_0;
        this.mValue = new Value(value);
        this.mOptions.addAll(Arrays.asList(ops));
    }

    public Ability(float value, Options... ops) {
        this.mOptions = new ArrayList<>();
        this.mType = Type.Float_0;
        this.mValue = new Value(value);
        this.mOptions.addAll(Arrays.asList(ops));
    }

    public Type getType() {
        return this.mType;
    }

    public void reset(Type abilityType) {
        this.mOptions.clear();
        this.mOptions.add(Options.None_2);
        this.mType = abilityType;
        switch ( abilityType ) {
            case Bool_0:
                this.mValue.mBoolVal = false;
                break;
            case Float_0:
                this.mValue.mFloatVal = 0.0f;
                break;
        }
    }

    public boolean getBool() {
        if (this.mType == Type.Unset || this.mType == Type.Float_0)
            return false;
        else
            return this.mValue.mBoolVal;
    }

    public void setBool(boolean val) {
        if (this.mType == Type.Unset)
            this.reset(Type.Bool_0);
        this.mValue.mBoolVal = val;
    }

    public float getFloat() {
        if (this.mType == Type.Unset || this.mType == Type.Bool_0)
            return 0.0f;
        else
            return this.mValue.mFloatVal;
    }

    public void setFloat(float val) {
        if (this.mType == Type.Unset)
            this.reset(Type.Float_0);
        this.mValue.mFloatVal = val;
    }
    public boolean hasOption(Options... op) {
        for (Options options : op) {
            if (!mOptions.contains(options)) {
                return false;
            }
        }
        return true;
    }

    public Ability set(Ability ability) {
        this.mType = ability.mType;
        this.mOptions = ability.mOptions;
        this.mValue = ability.mValue;
        return this;
    }

    public static class Value {
        boolean mBoolVal;
        float mFloatVal;

        Value(boolean val) {
            this.mBoolVal = val;
        }

        Value(float val) {
            this.mFloatVal = val;
        }
    }

    public enum Type {
        Invalid_2,
        Unset,
        Bool_0,
        Float_0
    }

    public enum Options {
        None_2(0x0),
        NoSave(0x1),
        CommandExposed(0x2),
        PermissionsInterfaceExposed(0x4),
        WorldbuilderOverrides(0x8);

        private final int value;

        Options(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
