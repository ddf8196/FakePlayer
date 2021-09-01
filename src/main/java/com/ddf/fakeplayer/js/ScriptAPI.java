package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.actor.player.FakePlayer;

public class ScriptAPI {
    private final PlayerScript playerScript;
    private final FakePlayer player;

    ScriptAPI(PlayerScript playerScript, FakePlayer player) {
        this.playerScript = playerScript;
        this.player = player;
    }

    public FakePlayer getPlayer() {
        return player;
    }

    public void finish() {
        playerScript.finish();
    }
}
