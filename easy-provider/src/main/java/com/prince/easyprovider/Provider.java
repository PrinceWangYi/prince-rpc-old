package com.prince.easyprovider;

import com.prince.RpcApplication;
import com.prince.registry.LocalRegistry;
import com.prince.server.VertxHttpServer;
import com.prince.service.UserService;

public class Provider {

    public static void main(String[] args) {
        RpcApplication.init();

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        VertxHttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getPort());
    }

}
