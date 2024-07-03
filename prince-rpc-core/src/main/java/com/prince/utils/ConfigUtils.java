package com.prince.utils;

import cn.hutool.setting.dialect.Props;

public class ConfigUtils {

    public static <T> T loadRpcConfig(Class<T> tClass, String prefix) {
        Props props = new Props("application.properties");
        return props.toBean(tClass, prefix);
    }

}
