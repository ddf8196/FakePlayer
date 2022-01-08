package com.ddf.fakeplayer.state;

import java.util.HashMap;

public class VanillaStates {
    public static HashMap<String, ItemState> STRING_TO_BLOCK_STATE_MAP = new HashMap<>();

    public static void registerStates() {
        ItemState.forEachState(blockState -> {
            STRING_TO_BLOCK_STATE_MAP.put(blockState.getName(), blockState);
            return true;
        });
    }
}
