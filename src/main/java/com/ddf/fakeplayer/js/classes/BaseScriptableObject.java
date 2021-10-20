package com.ddf.fakeplayer.js.classes;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseScriptableObject extends ScriptableObject {
    private static Map<Class<?>, Map<Scriptable, WeakReference<Scriptable>>> prototypeMapMap = new ConcurrentHashMap<>();

    public BaseScriptableObject() {}

    public BaseScriptableObject(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static Map<Scriptable, WeakReference<Scriptable>> getPrototypeMap(Class<?> c) {
        return prototypeMapMap.get(c);
    }

    public static Scriptable getPrototype(Class<?> c, Scriptable scope) {
        return getPrototypeMap(c).get(scope).get();
    }

    public static void putPrototype(Class<?> c, Scriptable scope, Scriptable prototype) {
        Map<Scriptable, WeakReference<Scriptable>> map = getPrototypeMap(c);
        if (map == null) {
            map = Collections.synchronizedMap(new WeakHashMap<>());
            prototypeMapMap.put(c, map);
        }
        map.put(scope, new WeakReference<>(prototype));
    }

    @Override
    public String getClassName() {
        String name = getClass().getSimpleName();
        if (name.startsWith("Js")) {
            return name.substring(2);
        }
        return name;
    }

    @Override
    public String toString() {
        return Context.toString(this);
    }
}
