package com.prince.easyprovider;

import com.prince.registry.LocalRegistry;
import com.prince.server.VertxHttpServer;
import com.prince.service.UserService;

public class ProviderExample {
    public static void main(String[] args){
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }
}
