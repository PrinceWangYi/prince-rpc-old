package com.prince;

import com.prince.config.RegistryConfig;
import com.prince.config.RpcConfig;
import com.prince.constant.RpcConstant;
import com.prince.registry.Registry;
import com.prince.registry.RegistryFactory;
import com.prince.utils.ConfigUtils;

public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig config) {
        rpcConfig = config;
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
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

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
