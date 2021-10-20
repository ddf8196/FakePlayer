package com.ddf.fakeplayer.js.functions;

import com.ddf.fakeplayer.js.classes.entity.player.JsFakePlayer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class GetPlayer extends BaseFunction {
    private final JsFakePlayer player;

    public GetPlayer(Scriptable scope, JsFakePlayer player) {
        super(scope);
        this.player = player;
    }

    @Override
    public String getFunctionName() {
        return "getPlayer";
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return player;
    }
}
