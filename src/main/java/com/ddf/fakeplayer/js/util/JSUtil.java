package com.ddf.fakeplayer.js.util;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class JSUtil {
    @SuppressWarnings("unchecked")
    public static <T extends BaseScriptableObject> T newObject(Class<T> c, Scriptable scope, Object... args) {
        if (c.isAnnotationPresent(ConstructableInJS.class))
            return (T) Context.getCurrentContext().newObject(scope, c.getSimpleName(), args);
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(Scriptable.class);
            return constructor.newInstance(scope);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    public static Function tryGetFunction(Scriptable scope, String functionName) {
        Object function = scope.get(functionName, scope);
        if (function instanceof Function) {
            return (Function) function;
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T tryCastArgument(Object arg, Class<T> type) {
        if (!type.isInstance(arg))
            throw new IllegalArgumentException();
        return (T) arg;
    }

    @SuppressWarnings("unchecked")
    public static  <T> List<T> tryCastArrayArgument(Object arg, Class<T> elementType) {
        NativeArray array = tryCastArgument(arg, NativeArray.class);
        ArrayList<T> arrayList = new ArrayList<T>(array);
        for (Object object : arrayList) {
            tryCastArgument(object, elementType);
        }
        return arrayList;
    }
}
