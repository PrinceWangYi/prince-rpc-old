package com.prince.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTpcClient {

    public void doStart(int port) {
        Vertx.vertx().createNetClient().connect(port, "127.0.0.1", res -> {
            if (res.succeeded()) {
                NetSocket socket = res.result();
                socket.write("hello server");

                socket.handler(System.out::println);
            }
        });
    }

    public static void main(String[] args) {
        new VertxTpcClient().doStart(8080);
    }
}
