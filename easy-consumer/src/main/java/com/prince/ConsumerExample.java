package com.prince;

import cn.hutool.json.JSONUtil;
import com.prince.model.User;
import com.prince.proxy.ServiceProxyFactory;
import com.prince.service.UserService;

import java.io.IOException;

public class ConsumerExample {

    public static void main(String[] args) throws IOException {
        /*UserServiceProxy userServiceProxy = new UserServiceProxy();*/
        User user = new User();
        user.setName("prince");
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user1 = userService.getUser(user);
        int i = userService.queryUsername(user);
        System.out.println(i);
        /*userServiceProxy.getUser(user);*/

        System.out.println(JSONUtil.toJsonStr(user1));

    }
}
