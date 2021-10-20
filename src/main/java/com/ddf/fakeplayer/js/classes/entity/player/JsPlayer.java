package com.ddf.fakeplayer.js.classes.entity.player;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.js.classes.entity.JsEntity;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

public class JsPlayer extends JsEntity {
    private Player player;

    public JsPlayer() {}

    public JsPlayer(Scriptable scope) {
        this(scope, getPrototype(JsPlayer.class, scope));
    }

    public JsPlayer(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsPlayer.class, scope, prototype);
    }

    @Override
    public Player getActor() {
        return player;
    }

    @Override
    public void setActor(Actor actor) {
        if (!(actor instanceof Player))
            return;
        player = (Player) actor;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public String name() {
        return "";
    }
}
