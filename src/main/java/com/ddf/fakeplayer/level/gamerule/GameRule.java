package com.ddf.fakeplayer.level.gamerule;

import java.util.ArrayList;
import java.util.function.Function;

public class GameRule {
    private boolean mShouldSave;
    private GameRule.Type mType;
    private GameRule.Value mValue;
    private String mName;
    private boolean mAllowUseInCommand;
    private boolean mIsDefaultSet;
    private boolean mRequiresCheats;
    private Function<GameRule, Void> mTagNotFoundCallback;
    private GameRule.ValidateValueCallback mValidateValueCallback;

    public enum Type {
        Invalid,
        Bool,
        Int,
        Float
    }

    public static class Value {
        private boolean boolVal;
        private int intVal;
        private float floatVal;
    }

    public static class ValidationError {
        private boolean mSuccess;
        private String mErrorDescription;
        private ArrayList<String> mErrorParameters;
    }

    @FunctionalInterface
    public interface ValidateValueCallback {
        boolean call(final GameRule.Value value, GameRule.ValidationError error);
    }
}
