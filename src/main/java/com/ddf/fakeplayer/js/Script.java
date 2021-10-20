package com.ddf.fakeplayer.js;

import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class Script {
    private Context context;
    private Scriptable scope;
    private final String path;
    private String code;
    private Function onTick;
    private Function onPlayerChat;
    private OnEvaluateListener onEvaluateListener;
    private OnErrorListener onErrorListener;

    private boolean evaluated = false;

    public Script(Context context, Scriptable scope, String path, String code) {
        this.context = context;
        this.scope = scope;
        this.path = path;
        this.code = code;
    }

    public void evaluate() {
        if (evaluated)
            return;
        evaluated = true;
        onEvaluate();
        context.evaluateString(scope, code, this.path, 1, null);
        code = null;
        onTick = JSUtil.tryGetFunction(scope, "onTick");
        onPlayerChat = JSUtil.tryGetFunction(scope, "onPlayerChat");
    }

    public void onTick(long tick) {
        if (!evaluated)
            return;
        try {
            if (onTick != null) {
                onTick.call(context, scope, scope, new Object[]{tick});
            }
        } catch (Throwable t) {
            onError(t);
            t.printStackTrace();
        }
    }

    public void onPlayerChat(String source, String message, String xuid, String platformChatId) {
        if (!evaluated)
            return;
        try {
            if (onPlayerChat != null) {
                onPlayerChat.call(context, scope, scope, new Object[]{source, message, xuid, platformChatId});
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    private void onEvaluate() {
        if (onEvaluateListener != null) {
            onEvaluateListener.onEvaluate(this);
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

    public OnErrorListener getOnErrorListener() {
        return onErrorListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public interface OnEvaluateListener {
        void onEvaluate(Script script);
    }

    public interface OnErrorListener {
        void onError(Script script, Throwable throwable);
    }
}
