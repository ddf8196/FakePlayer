package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.util.Logger;

public class ScriptAPI {
    private final PlayerScript playerScript;
    private final FakePlayer player;
    private final Logger logger;

    ScriptAPI(PlayerScript playerScript, FakePlayer player) {
        this.playerScript = playerScript;
        this.player = player;
        this.logger = Logger.getLogger();
    }

    public void log(Object... log) {
        logger.log(log);
    }

    public FakePlayer getPlayer() {
        return player;
    }

    public void finish() {
        playerScript.finish();
    }
}
