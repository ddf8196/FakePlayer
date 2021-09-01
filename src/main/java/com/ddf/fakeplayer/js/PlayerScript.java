package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.util.Pair;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PlayerScript {
    private Context context;
    private Scriptable scope;
    private final String path;
    private String code;
    private Function onTick;
    private Function onPlayerChat;
    private OnEvaluateListener onEvaluateListener;
    private OnFinishListener onFinishListener;
    private OnErrorListener onErrorListener;
    private final List<Pair<String, Object>> apis = new ArrayList<>();
    private boolean evaluated = false;
    private boolean finished = false;

    public PlayerScript(String path) throws IOException {
        this.path = path;
        Path scriptPath = JSLoader.getScriptsDir().resolve(path);
        this.code = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
    }

    public void registerAPI(String name, Object api) {
        apis.add(new Pair<>(name, api));
    }

    public void evaluate() {
        if (evaluated)
            return;
        evaluated = true;
        onEvaluate();
        context = ContextFactory.getGlobal().enterContext();
        context.setOptimizationLevel(9);
        context.setLanguageVersion(Context.VERSION_ES6);
        scope = context.initStandardObjects();
        for (Pair<String, Object> pair : apis) {
            scope.put(pair.getKey(), scope, Context.javaToJS(pair.getValue(), scope));
        }
        context.evaluateString(scope, code, this.path, 1, null);
        code = null;
        onTick = JSUtil.tryGetFunction(scope, "onTick");
        onPlayerChat = JSUtil.tryGetFunction(scope, "onPlayerChat");
        if (onTick == null && onPlayerChat == null) {
            finish();
        }
    }

    public void finish() {
        finished = true;
        onFinish();
    }

    public boolean isFinished() {
        return finished;
    }

    public void onTick(long tick) {
        if (finished || !evaluated)
            return;
        try {
            if (onTick != null) {
                onTick.call(context, scope, scope, new Object[]{tick});
            }
        } catch (Throwable t) {
            onError(t);
            finish();
        }
    }

    public void onPlayerChat(String source, String message, String xuid, String platformChatId) {
        if (finished || !evaluated)
            return;
        try {
            if (onPlayerChat != null) {
                onPlayerChat.call(context, scope, scope, new Object[]{source, message, xuid, platformChatId});
            }
        } catch (Throwable t) {
            onError(t);
            finish();
        }
    }

    private void onEvaluate() {
        if (onEvaluateListener != null) {
            onEvaluateListener.onEvaluate(this);
        }
    }

    private void onFinish() {
        if (onFinishListener != null) {
            onFinishListener.onFinish(this);
        }
    }

    private void onError(Throwable throwable) {
        if (onErrorListener != null) {
            onErrorListener.onError(this, throwable);
        }
    }

    public String getPath() {
        return path;
    }

    public OnEvaluateListener getOnEvaluateListener() {
        return onEvaluateListener;
    }

    public void setOnEvaluateListener(OnEvaluateListener onEvaluateListener) {
        this.onEvaluateListener = onEvaluateListener;
    }

    public OnFinishListener getOnFinishListener() {
        return onFinishListener;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public OnErrorListener getOnErrorListener() {
        return onErrorListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public interface OnEvaluateListener {
        void onEvaluate(PlayerScript script);
    }

    public interface OnFinishListener {
        void onFinish(PlayerScript script);
    }

    public interface OnErrorListener {
        void onError(PlayerScript script, Throwable throwable);
    }
}
