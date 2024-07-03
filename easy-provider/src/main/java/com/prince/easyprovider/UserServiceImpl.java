package com.prince.easyprovider;

import com.prince.model.User;
import com.prince.service.UserService;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }

    @Override
    public int queryUsername(User user) {
        return user.getName().length();
    }
}
