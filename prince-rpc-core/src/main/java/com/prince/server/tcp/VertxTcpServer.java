package com.prince.server.tcp;

import com.prince.server.HttpServer;
import com.prince.server.HttpServerHandler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

import java.util.Arrays;

public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        NetServer netServer = Vertx.vertx().createNetServer();

        // 处理请求
        netServer.connectHandler(new TcpServerHandler()).listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP Server started on port " + port);
            } else {
                System.out.println("Failed to start TCP Server");
            }
        });
    }

}
