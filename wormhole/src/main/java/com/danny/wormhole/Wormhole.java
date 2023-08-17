package com.danny.wormhole;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Danny 姜
 */
public class Wormhole {
    private static final String TAG = Wormhole.class.getSimpleName();

    public static final String WORMHOLE_PACKAGE_NAME = "com.danny.wormhole.";
    public static final String STUB_SUFFIX = "Bridge";
    public static final String FIELD_VALUE = "value";

    private Map<Class, InvocationHandler> mInvocationHandlerMap = new HashMap<>();

    public static Wormhole getDefault() {
        return Holder.instance;
    }

    static class Holder {
        static Wormhole instance = new Wormhole();
    }

    private Wormhole() {}

    public <T> T create(Class<T> stub) {
        InvocationHandler handler = null;
        handler = findHandler(stub);
        if (handler != null) {
            T result = (T) Proxy.newProxyInstance(stub.getClassLoader(), new Class[]{stub}, handler);
            return result;
        }
        return null;
    }

    private  InvocationHandler findHandler(Class stub) {
        if (mInvocationHandlerMap.keySet().contains(stub)) {
            return mInvocationHandlerMap.get(stub);
        }
        try {
            String simpleName = stub.getSimpleName();
            Log.i(TAG, "findHandler simpleName: " + simpleName);
            Class<?> declarationMiddle = Class.forName(WORMHOLE_PACKAGE_NAME + simpleName + STUB_SUFFIX);
            String value = (String) declarationMiddle.getDeclaredField(FIELD_VALUE).get(null);
            Class<?> shadowMiddle = Class.forName(WORMHOLE_PACKAGE_NAME + value);
            String targetValue = (String) shadowMiddle.getDeclaredField(FIELD_VALUE).get(null);
            if (targetValue == null || targetValue.equals("")
                    || targetValue.equals("null")) {
                Log.e(TAG, "error! targetClazzName null");
                return null;
            }
            Object obj;
            final Class clazz = Class.forName(targetValue);
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            obj = constructor.newInstance();
            final Object action = obj;
            InvocationHandler handler = (proxy, method, args) -> method.invoke(action, args);
            mInvocationHandlerMap.put(stub, handler);
            return handler;
        } catch (ClassNotFoundException | NoSuchFieldException
                | InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
            Log.e(TAG, "findHandler, e: ", e);
        }
        return null;
    }
}
