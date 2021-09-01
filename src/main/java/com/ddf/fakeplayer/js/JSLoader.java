package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.Main;
import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.util.NotImplemented;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSLoader {
    private static Path scriptsDir;

    @NotImplemented
    private static boolean isVisibleToScripts(String fullClassName) {
        return true;
    }

    static {
        ContextFactory.getGlobal().addListener(new ContextFactory.Listener() {
            @Override
            public void contextCreated(Context cx) {
                cx.setClassShutter(JSLoader::isVisibleToScripts);
            }
            @Override
            public void contextReleased(Context cx) {
            }
        });
    }

    public static Path getScriptsDir() {
        return scriptsDir;
    }

    public static void init() throws IOException {
        JSLoader.scriptsDir = Main.getBaseDir().resolve("scripts");
        if (!Files.exists(JSLoader.scriptsDir)) {
            Files.createDirectories(JSLoader.scriptsDir);
        }
    }

    public static PlayerScript loadScript(String path, FakePlayer player) throws IOException {
        PlayerScript script = new PlayerScript(path);
        script.registerAPI("Script", new ScriptAPI(script, player));
        return script;
    }
}
