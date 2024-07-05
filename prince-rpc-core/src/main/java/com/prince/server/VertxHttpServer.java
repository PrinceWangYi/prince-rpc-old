package com.prince.server;

import com.prince.RpcApplication;
import com.prince.model.RpcRequest;
import com.prince.model.RpcResponse;
import com.prince.registry.LocalRegistry;
import com.prince.serialize.Serializer;
import com.prince.serialize.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Vertx HTTP 服务器
 *
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

        /*httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("启动端口" + port);
            } else {
                System.out.println("启动失败" + result.cause());
            }
        });*/

        httpServer.requestHandler(request -> {
            request.bodyHandler(body -> {
                byte[] requestData = body.getBytes();
                Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
                try {
                    RpcRequest rpcRequest = serializer.deserialize(requestData, RpcRequest.class);
                    RpcResponse rpcResponse = new RpcResponse();

                    if (rpcRequest == null) {
                        rpcResponse.setMessage("rpcRequest is null");
                        doResponse(request, rpcResponse, serializer);
                        return;                    }

                    Class<?> aClass = LocalRegistry.get(rpcRequest.getServiceName());
                    Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                    Object result = method.invoke(aClass.newInstance(), rpcRequest.getArgs());

                    rpcResponse.setData(result);
                    rpcResponse.setDataType(method.getReturnType());
                    rpcResponse.setMessage("ok");

                    doResponse(request, rpcResponse, serializer);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        }).listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("启动端口" + port);
            } else {
                System.out.println("启动失败" + result.cause());
            }
        });

    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
