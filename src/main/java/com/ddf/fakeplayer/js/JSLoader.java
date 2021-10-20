package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.js.classes.Console;
import com.ddf.fakeplayer.js.classes.block.JsBlockPermutation;
import com.ddf.fakeplayer.js.classes.block.JsBlockType;
import com.ddf.fakeplayer.js.classes.block.JsMinecraftBlockTypes;
import com.ddf.fakeplayer.js.classes.container.JsContainer;
import com.ddf.fakeplayer.js.classes.entity.JsEntity;
import com.ddf.fakeplayer.js.classes.entity.component.JsEntityInventoryComponent;
import com.ddf.fakeplayer.js.classes.entity.effect.JsEffect;
import com.ddf.fakeplayer.js.classes.entity.effect.JsEffectType;
import com.ddf.fakeplayer.js.classes.entity.effect.JsMinecraftEffectTypes;
import com.ddf.fakeplayer.js.classes.entity.player.JsFakePlayer;
import com.ddf.fakeplayer.js.classes.entity.player.JsPlayer;
import com.ddf.fakeplayer.js.classes.entity.player.JsScriptNavigationResult;
import com.ddf.fakeplayer.js.classes.entity.player.JsScriptPlayerHeadRotation;
import com.ddf.fakeplayer.js.classes.item.JsItemStack;
import com.ddf.fakeplayer.js.classes.item.JsItemType;
import com.ddf.fakeplayer.js.classes.item.JsMinecraftItemTypes;
import com.ddf.fakeplayer.js.classes.location.JsBlockLocation;
import com.ddf.fakeplayer.js.classes.location.JsLocation;
import com.ddf.fakeplayer.js.functions.GetPlayer;
import com.ddf.fakeplayer.js.functions.RegisterChatCommand;
import com.ddf.fakeplayer.js.functions.fs.ReadFile;
import com.ddf.fakeplayer.js.functions.fs.ReadFileSync;
import com.ddf.fakeplayer.js.functions.fs.WriteFile;
import com.ddf.fakeplayer.js.functions.fs.WriteFileSync;
import com.ddf.fakeplayer.js.util.JSUtil;
import com.ddf.fakeplayer.main.Main;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScope;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class JSLoader {
    private static Path scriptsDir;
    private static ModuleScriptProvider moduleScriptProvider;

    public static Path getScriptsDir() {
        return scriptsDir;
    }

    public static void init() throws IOException {
        JSLoader.scriptsDir = Main.getBaseDir().resolve("scripts");
        if (!Files.exists(JSLoader.scriptsDir)) {
            Files.createDirectories(JSLoader.scriptsDir);
        }
        JSLoader.scriptsDir = JSLoader.scriptsDir.toRealPath();
        moduleScriptProvider = new SoftCachingModuleScriptProvider(new ModuleSourceProvider() {
            @Override
            public ModuleSource loadSource(String moduleId, Scriptable paths, Object validator) throws IOException, URISyntaxException {
                Path modulePath = scriptsDir.resolve(moduleId);
                return getModuleSource(modulePath, validator);
            }

            @Override
            public ModuleSource loadSource(URI uri, URI baseUri, Object validator) throws IOException, URISyntaxException {
                Path modulePath = Paths.get(uri);
                return getModuleSource(modulePath, validator);
            }

            private ModuleSource getModuleSource(Path modulePath, Object validator) throws IOException {
                String fileName = modulePath.getFileName().toString();
                if (!fileName.endsWith(".js") && !Files.exists(modulePath)) {
                    modulePath = modulePath.resolveSibling(fileName + ".js");
                }
                if (!Files.exists(modulePath))
                    return null;
                modulePath = modulePath.toRealPath();
                Reader reader = Files.newBufferedReader(modulePath);
                byte[] hash = sha256(Files.readAllBytes(modulePath));
                if (hash != null && validator instanceof byte[] && Arrays.equals(hash, (byte[]) validator)) {
                    return NOT_MODIFIED;
                }
                return new ModuleSource(reader, null, modulePath.toUri(), scriptsDir.toUri(), hash);
            }

            private byte[] sha256(byte[] input) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    return messageDigest.digest(input);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    public static Script loadScript(String path, FakePlayer player) throws IOException {
        return loadScript(Context.getCurrentContext(), path, player);
    }

    public static Script loadScript(Context context, String path, FakePlayer player) throws IOException {
        Path scriptPath = JSLoader.getScriptsDir().resolve(path);
        if (Files.exists(scriptPath)) {
            scriptPath = scriptPath.toRealPath();
            return new Script(context, initScope(context, scriptPath, player), path, new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8));
        } else {
            return null;
        }
    }

    public static Context initContext() {
        Context context = Context.getCurrentContext();
        if (context != null)
            return context;
        Context ctx = ContextFactory.getGlobal().enterContext();
        ctx.setLanguageVersion(Context.VERSION_ES6);
        ctx.setOptimizationLevel(9);
        return ctx;
    }

    public static ScriptableObject initScope(Context ctx, Path scriptPath, FakePlayer fakePlayer) {
        ScriptableObject scope = new ModuleScope(ctx.initSafeStandardObjects(), scriptPath.toUri(), scriptsDir.toUri());
        try {
            /*block*/ {
                ScriptableObject.defineClass(scope, JsBlockPermutation.class);
                ScriptableObject.defineClass(scope, JsBlockType.class);
                ScriptableObject.defineClass(scope, JsBlockType.class);
                ScriptableObject.defineClass(scope, JsMinecraftBlockTypes.class);
            }
            /*container*/ {
                ScriptableObject.defineClass(scope, JsContainer.class);
            }
            /*entity*/ {
                /*component*/ {
                    ScriptableObject.defineClass(scope, JsEntityInventoryComponent.class);
                }
                /*effect*/ {
                    ScriptableObject.defineClass(scope, JsEffect.class);
                    ScriptableObject.defineClass(scope, JsEffectType.class);
                    ScriptableObject.defineClass(scope, JsMinecraftEffectTypes.class);
                }
                /*player*/ {
                    ScriptableObject.defineClass(scope, JsFakePlayer.class);
                    ScriptableObject.defineClass(scope, JsPlayer.class);
                    ScriptableObject.defineClass(scope, JsScriptPlayerHeadRotation.class);
                    ScriptableObject.defineClass(scope, JsScriptNavigationResult.class);
                }
                ScriptableObject.defineClass(scope, JsEntity.class);
            }
            /*item*/ {
                ScriptableObject.defineClass(scope, JsItemStack.class);
                ScriptableObject.defineClass(scope, JsItemType.class);
                ScriptableObject.defineClass(scope, JsMinecraftItemTypes.class);
            }
            /*location*/ {
                ScriptableObject.defineClass(scope, JsLocation.class);
                ScriptableObject.defineClass(scope, JsBlockLocation.class);
            }
            ScriptableObject.defineClass(scope, Console.class);
            Console console = JSUtil.newObject(Console.class, scope);

            ScriptableObject.putProperty(scope, "global", scope);
            ScriptableObject.putProperty(scope, "console", console);

            JsFakePlayer player = JSUtil.newObject(JsFakePlayer.class, scope);
            player.setActor(fakePlayer);

            GetPlayer getPlayer = new GetPlayer(scope, player);
            RegisterChatCommand registerChatCommand = new RegisterChatCommand(scope, fakePlayer);
            Require require = new RequireBuilder()
                    .setModuleScriptProvider(moduleScriptProvider)
                    .setSandboxed(true)
                    .createRequire(ctx, scope);

            getPlayer.install(scope);
            registerChatCommand.install(scope);
            require.install(scope);

            new ReadFile(scope).install(scope);
            new ReadFileSync(scope).install(scope);
            new WriteFile(scope).install(scope);
            new WriteFileSync(scope).install(scope);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return scope;
    }
}
