package com.prince.server.tcp;

import com.prince.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

import java.util.Arrays;

public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        NetServer netServer = Vertx.vertx().createNetServer();

        // 处理请求
        netServer.connectHandler(socket -> {
            // 处理连接
            socket.handler(buffer ->{
                byte[] request = buffer.getBytes();
                byte[] response = handleRequest(request);
                // 发送响应
                socket.write(Buffer.buffer(response));
            });
        }).listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP Server started on port " + port);
            } else {
                System.out.println("Failed to start TCP Server");
            }
        });
    }

    public byte[] handleRequest(byte[] requestData) {
        System.out.println(Arrays.toString(requestData));
        // 处理request请求
        return (Arrays.toString(requestData) + " from TCP Server").getBytes();
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8080);
    }
}
