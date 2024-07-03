package com.prince.service;

import com.prince.model.User;

import java.io.IOException;

public interface UserService {

    User getUser(User user) throws IOException;

    int queryUsername(User user);
}
