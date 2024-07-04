package com.prince.easyprovider;

import com.prince.RpcApplication;
import com.prince.config.RegistryConfig;
import com.prince.config.RpcConfig;
import com.prince.constant.RpcConstant;
import com.prince.model.ServiceMetaInfo;
import com.prince.registry.LocalRegistry;
import com.prince.registry.Registry;
import com.prince.registry.RegistryFactory;
import com.prince.server.VertxHttpServer;
import com.prince.service.UserService;

/**
 * 注册中心启动
 */
public class AdvanceProvider {

    public static void main(String[] args) {
        // 初始化RPC框架
        RpcApplication.init();

        String serviceName = UserService.class.getName();

        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        try {
            registry.register(ServiceMetaInfo.builder().serviceName(serviceName).serviceHost(rpcConfig.getHost()).servicePort(rpcConfig.getPort())
                    .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(rpcConfig.getPort());
    }
}
