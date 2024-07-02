package com.prince.server;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP 服务器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class VertxHttpServer implements HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("启动端口" + port);
            } else {
                System.out.println("启动失败" + result.cause());
            }
        });

    }
}
