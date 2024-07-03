package com.prince;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.prince.model.RpcRequest;
import com.prince.model.RpcResponse;
import com.prince.model.User;
import com.prince.serialize.JDKSerialize;
import com.prince.service.UserService;

import java.io.IOException;
import java.util.Arrays;

public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) throws IOException {
        JDKSerialize jdkSerialize = new JDKSerialize();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        HttpResponse response = HttpRequest.post("http://localhost:8080")
                .body(jdkSerialize.serialize(rpcRequest)).execute();
        byte[] result = response.bodyBytes();
        RpcResponse deserialize = jdkSerialize.deserialize(result, RpcResponse.class);
        return (User) deserialize.getData();
    }

    @Override
    public int queryUsername(User user) {
        return user.getName().length();
    }
}
