package com.prince.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {

    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public static void register(String interfaceName, Class<?> implClass) {
        map.put(interfaceName, implClass);
    }

    /**
     * 获取服务
     */
    public static Class<?> get(String interfaceName) {
        return map.get(interfaceName);
    }

    /**
     * 删除服务
     */
    public static void remove(String interfaceName) {
        map.remove(interfaceName);
    }
}
