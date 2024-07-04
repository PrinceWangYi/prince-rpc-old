package com.prince.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.prince.RpcApplication;
import com.prince.model.RpcRequest;
import com.prince.model.RpcResponse;
import com.prince.serialize.Serializer;
import com.prince.serialize.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        HttpRequest post = HttpRequest.post("http://localhost:" + RpcApplication.getRpcConfig().getPort());
        HttpResponse response = post.body(serializer.serialize(rpcRequest)).execute();

        byte[] result = response.bodyBytes();
        RpcResponse deserialize = serializer.deserialize(result, RpcResponse.class);
        return deserialize.getData();
    }
}
