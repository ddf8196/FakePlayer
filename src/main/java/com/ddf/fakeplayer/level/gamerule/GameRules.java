package com.ddf.fakeplayer.level.gamerule;

import java.util.ArrayList;

public class GameRules {
    private ArrayList<GameRule> mGameRules;

    public enum GameRulesIndex {
        INVALID_GAME_RULE(0xFFFFFFFF),
        COMMAND_BLOCK_OUTPUT(0x0),
        DO_DAYLIGHT_CYCLE(0x1),
        DO_ENTITY_DROPS(0x2),
        DO_FIRE_TICK(0x3),
        DO_MOB_LOOT(0x4),
        DO_MOB_SPAWNING(0x5),
        DO_TILE_DROPS(0x6),
        DO_WEATHER_CYCLE(0x7),
        DROWNING_DAMAGE(0x8),
        FALL_DAMAGE(0x9),
        FIRE_DAMAGE(0xA),
        KEEP_INVENTORY(0xB),
        MOB_GRIEFING(0xC),
        PVP(0xD),
        SHOW_COORDINATES(0xE),
        DO_NATURAL_REGENERATION(0xF),
        DO_TNT_EXPLODE(0x10),
        SEND_COMMAND_FEEDBACK(0x11),
        EXPERIMENTAL_GAMEPLAY(0x12),
        MAX_COMMAND_CHAIN_LENGTH(0x13),
        DO_INSOMNIA(0x14),
        COMMAND_BLOCKS_ENABLED(0x15),
        RANDOM_TICK_SPEED(0x16),
        DO_IMMEDIATE_RESPAWN(0x17),
        SHOW_DEATH_MESSAGES(0x18),
        FUNCTION_COMMAND_LIMIT(0x19),
        PLAYER_SPAWN_RADIUS(0x1A),
        SHOW_TAGS(0x1B),
        VANILLA_GAME_RULE_COUNT(0x1C),
        GLOBAL_MUTE(0x1C),
        ALLOW_DESTRUCTIVE_OBJECTS(0x1D),
        ALLOW_MOBS(0x1E),
        CODE_BUILDER(0x1F),
        EDU_GAME_RULE_COUNT(0x20);

        private final int value;

        GameRulesIndex(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static GameRulesIndex getByValue(int value) {
            for (GameRulesIndex gameRulesIndex : values()) {
                if (gameRulesIndex.getValue() == value) {
                    return gameRulesIndex;
                }
            }
            return null;
        }
    }

    public final boolean hasRule(int ruleType) {
        if (ruleType >= 0 ) {
            return ruleType < this.mGameRules.size();
        }
        return false;
    }

    public final GameRule getRule(int rule) {
        return this.mGameRules.get(rule);
    }

    public final boolean getBool(int ruleType) {
        GameRule rule = this.getRule(ruleType);
        return rule != null && rule.getBool();
    }
}
