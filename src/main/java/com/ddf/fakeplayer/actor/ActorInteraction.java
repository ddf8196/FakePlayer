package com.ddf.fakeplayer.actor;

public class ActorInteraction {
    private String mInteractText;
    private Runnable mInteraction;
    private boolean mNoCapture;

    public ActorInteraction(boolean noCapture) {
        this.mNoCapture = noCapture;
    }

    public final void setInteractText(final String text) {
        this.mInteractText = text;
    }

    public final boolean shouldCapture() {
        return !this.mNoCapture;
    }

    public final void capture(final Runnable a2) {
        this.mInteraction = a2;
    }

    public final void interact() {
        if (this.mInteraction != null)
            this.mInteraction.run();
    }
}
