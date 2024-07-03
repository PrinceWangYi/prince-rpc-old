package com.prince;

import com.prince.model.User;
import com.prince.proxy.ServiceProxyFactory;
import com.prince.service.UserService;

import java.io.IOException;

public class Consumer {
    public static void main(String[] args) throws IOException {
        UserService proxy = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("prince");
        User user1 = proxy.getUser(user);
        System.out.println(user1);
    }

}
