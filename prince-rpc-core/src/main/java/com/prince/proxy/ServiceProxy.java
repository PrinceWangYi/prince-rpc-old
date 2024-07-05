package com.prince.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.prince.RpcApplication;
import com.prince.config.RegistryConfig;
import com.prince.config.RpcConfig;
import com.prince.constant.RpcConstant;
import com.prince.model.RpcRequest;
import com.prince.model.RpcResponse;
import com.prince.model.ServiceMetaInfo;
import com.prince.registry.Registry;
import com.prince.registry.RegistryFactory;
import com.prince.registry.RegistryKeys;
import com.prince.serialize.Serializer;
import com.prince.serialize.SerializerFactory;
import com.prince.server.tcp.VertxTcpServer;
import com.prince.server.tcp.VertxTpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
//        registry.init(rpcConfig.getRegistryConfig());
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(serviceName).serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION).build();
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 选择服务
        ServiceMetaInfo metaInfo = serviceMetaInfos.get(0);

        RpcResponse response = VertxTpcClient.doRequest(rpcRequest, metaInfo);
        return response.getData();
    }
}
