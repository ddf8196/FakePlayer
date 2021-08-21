package com.ddf.fakeplayer.actor.attribute;

import com.ddf.fakeplayer.util.MathUtil;

import java.util.ArrayList;

public class AttributeInstance {
    private BaseAttributeMap mAttributeMap;
    private Attribute mAttribute;
    private ArrayList<AttributeModifier> mModifierList;
    private ArrayList<TemporalAttributeBuff> mTemporalBuffs;
    private ArrayList<AttributeInstanceHandle> mListeners;
    private AttributeInstanceDelegate mDelegate;
    private AttributeInstance.DefaultValue _anon_0;
    private AttributeInstance.CurrentValue _anon_1;

   public AttributeInstance() {
        this.mAttributeMap = null;
        this.mAttribute = null;
        this.mModifierList = new ArrayList<>();
        this.mTemporalBuffs = new ArrayList<>();
        this.mListeners = new ArrayList<>();
        this.mDelegate = null;
        this._anon_0 = new DefaultValue();
        this._anon_0.set(2, 0.0f);
        this._anon_1 = new CurrentValue();
        this._anon_1.set(2, 0.0f);
    }

    public AttributeInstance(BaseAttributeMap attributeMap, final Attribute attribute) {
        this.mAttributeMap = attributeMap;
        this.mAttribute = attribute;
        this.mModifierList = new ArrayList<>();
        this.mTemporalBuffs = new ArrayList<>();
        this.mListeners = new ArrayList<>();
        this.mDelegate = null;
        this._anon_0 = new DefaultValue();
        this._anon_1 = new CurrentValue();
        this._anon_0.mDefaultMinValue = 0.0f;
        this._anon_0.mDefaultMaxValue = Float.MAX_VALUE;
        this._anon_1.mCurrentMinValue = 0.0f;
        this._anon_1.mCurrentMinValue = Float.MAX_VALUE;
    }

    public final Attribute getAttribute() {
        return this.mAttribute;
    }

    public final ArrayList<TemporalAttributeBuff> getBuffs() {
        return this.mTemporalBuffs;
    }

    public final float getCurrentValue() {
        return this._anon_1.mCurrentValue;
    }

    public final float getDefaultValue(int operand) {
        if (operand >= 0 && operand < AttributeOperands.TOTAL_OPERANDS.getValue())
            return this._anon_0.get(operand);
        else
            return 0.0f;
    }

    public final float getMaxValue() {
        return this._anon_1.mCurrentMaxValue;
    }

    public final float getMinValue() {
        return this._anon_1.mCurrentMinValue;
    }

    public final AttributeInstanceHandle getHandle() {
        AttributeInstanceHandle result = new AttributeInstanceHandle();
        result.mAttributeID = this.mAttribute.getIDValue();
        result.mAttributeMap = this.mAttributeMap;
        return result;
    }

    public final void setDelegate(AttributeInstanceDelegate delegate) {
        this.mDelegate = delegate;
    }

    public final ArrayList<AttributeModifier> _getAppliedModifiers(int operation) {
        return this.getModifiers(operation);
    }

    public final ArrayList<AttributeModifier> getModifiers(int operation) {
        ArrayList<AttributeModifier> retstr = new ArrayList<>();
        for (AttributeModifier mod : this.mModifierList) {
            if (mod.getOperation() == operation)
                retstr.add(mod);
        }
        return retstr;
    }

    public final void _setDirty() {
        this.mAttributeMap.onAttributeModified(this);
    }

    public final void setDefaultValue(float defaultValue, AttributeOperands operand) {
       setDefaultValue(defaultValue, operand.getValue());
    }

    public final void setDefaultValue(float defaultValue, int operand) {
        if ( operand >= 0 && operand < AttributeOperands.TOTAL_OPERANDS.getValue() && defaultValue != this._anon_0.get(operand)) {
            this._anon_0.set(operand, defaultValue);
            this._anon_1.set(operand, defaultValue);
            this._setDirty();
        }
    }

    public final void setDefaultValueOnly(float newDefaultValue) {
        if (newDefaultValue != this._anon_0.mDefaultValue) {
            this._anon_0.mDefaultValue = newDefaultValue;
            this._setDirty();
        }
    }

    public final void setMaxValue(float max) {
        this._anon_1.mCurrentMaxValue = max;
        this._anon_0.mDefaultValue = max;
        this._anon_1.mCurrentValue = MathUtil.clamp(
            this._anon_1.mCurrentValue,
            this._anon_1.mCurrentMinValue,
            this._anon_1.mCurrentMaxValue);
        this._setDirty();
    }

    public final void setMinValue(float min) {
        this._anon_1.mCurrentMinValue = min;
        this._anon_0.mDefaultMinValue = min;
        this._anon_1.mCurrentValue = MathUtil.clamp(
            this._anon_1.mCurrentValue,
            this._anon_1.mCurrentMinValue,
            this._anon_1.mCurrentMaxValue);
        this._setDirty();
    }

    public final void setRange(float min, float defaultValue, float max) {
        this.serializationSetRange(min, defaultValue, max);
        this._anon_1.mCurrentValue = defaultValue;
    }

    public final void serializationSetRange(float min, float base, float max) {
        this._anon_1.mCurrentMinValue = min;
        this._anon_0.mDefaultMinValue = min;
        this._anon_1.mCurrentMaxValue = max;
        this._anon_0.mDefaultMaxValue = max;
        this._anon_0.mDefaultValue = base;
        this._anon_1.mCurrentValue = this._sanitizeValue(this._anon_1.mCurrentValue);
        this._setDirty();
    }

    public final float _sanitizeValue(float value) {
        float modifiedMax = this._anon_1.mCurrentMaxValue;
        for (AttributeModifier modifier : this._getAppliedModifiers(AttributeModifierOperation.OPERATION_CAP.getValue())) {
            if (modifiedMax > modifier.getAmount())
                modifiedMax = modifier.getAmount();
        }
        return MathUtil.clamp(value, this._anon_1.mCurrentMinValue, modifiedMax);
    }

    public final void registerListener(final AttributeInstance listener) {
        this.mListeners.add(listener.getHandle());
    }

    public final void resetToMaxValue() {
        this._anon_1.mCurrentValue = this._anon_1.mCurrentMaxValue;
        this._setDirty();
    }

    public final void resetToDefaultValue() {
        this._anon_1.mCurrentValue = this._anon_0.mDefaultValue;
        this._setDirty();
    }

    public interface Value {
        float get(int index);
        void set(int index, float value);
    }

    public static class DefaultValue implements Value {
        float mDefaultMinValue;
        float mDefaultMaxValue;
        float mDefaultValue;

        @Override
        public final float get(int index) {
            switch (index) {
                case 0:
                    return mDefaultMinValue;
                case 1:
                    return mDefaultMaxValue;
                case 2:
                    return mDefaultValue;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public final void set(int index, float value) {
            switch (index) {
                case 0:
                    mDefaultMinValue = value;
                case 1:
                    mDefaultMaxValue = value;
                case 2:
                    mDefaultValue = value;
            }
        }
    }

    public static class CurrentValue implements Value {
        float mCurrentMinValue;
        float mCurrentMaxValue;
        float mCurrentValue;

        @Override
        public float get(int index) {
            switch (index) {
                case 0:
                    return mCurrentMinValue;
                case 1:
                    return mCurrentMaxValue;
                case 2:
                    return mCurrentValue;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void set(int index, float value) {
            switch (index) {
                case 0:
                    mCurrentMinValue = value;
                case 1:
                    mCurrentMaxValue = value;
                case 2:
                    mCurrentValue = value;
            }
        }
    }
}
