package com.ddf.fakeplayer.level.gamemode;

import com.ddf.fakeplayer.actor.player.Player;

import java.util.function.Function;

public class SurvivalMode extends GameMode {
    boolean mIsTrialMode;
    boolean mHasDisplayedIntro;
    int mTrialEndedReminder;
    Function<Boolean, Void> mShowUpsellScreenCallback;

    public SurvivalMode(Player player) {
        super(player);
        this.mIsTrialMode = false;
        this.mHasDisplayedIntro = false;
        this.mTrialEndedReminder = 0;
        this.mShowUpsellScreenCallback = null;
    }
}
