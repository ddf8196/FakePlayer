package com.ddf.fakeplayer.level;

public enum GameType {
    Undefined(0xFFFFFFFF),
    Survival(0x0),
    Creative(0x1),
    Adventure(0x2),
    SurvivalViewer(0x3),
    CreativeViewer(0x4),
    Default(0x5),
    WorldDefault(0x0);

    private final int value;

    GameType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameType getByValue(int value) {
        for (GameType gameType : values()) {
            if (gameType.getValue() == value) {
                return gameType;
            }
        }
        return null;
    }
}
