package com.prince;

import com.prince.config.RpcConfig;
import com.prince.constant.RpcConstant;
import com.prince.utils.ConfigUtils;

public class RpcApplication {

    private static volatile RpcConfig defaultConfig;

    public static void init(RpcConfig config) {
        defaultConfig = config;
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadRpcConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getDefaultConfig() {
        if (defaultConfig == null) {
            synchronized (RpcApplication.class) {
                if (defaultConfig == null) {
                    init();
                }
            }
        }
        return defaultConfig;
    }
}
