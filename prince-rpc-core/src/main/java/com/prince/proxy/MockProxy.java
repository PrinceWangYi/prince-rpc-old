package com.prince.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return getDefaultObject(method.getReturnType());
    }

    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }
        // 对象类型
        return null;
    }
}
