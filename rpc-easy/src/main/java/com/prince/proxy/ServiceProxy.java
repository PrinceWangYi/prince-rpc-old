package com.prince.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.prince.model.RpcRequest;
import com.prince.model.RpcResponse;
import com.prince.serialize.JDKSerialize;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JDKSerialize jdkSerialize = new JDKSerialize();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        HttpRequest post = HttpRequest.post("http://localhost:8080");
        HttpResponse response = post.body(jdkSerialize.serialize(rpcRequest)).execute();

        byte[] result = response.bodyBytes();
        RpcResponse deserialize = jdkSerialize.deserialize(result, RpcResponse.class);
        return deserialize.getData();
    }
}
