package com.prince.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String rpcName = "prince-rpc";

    private String version = "1.0";

    private String host = "localhost";

    private Integer port = 8080;

    private boolean mock = false;
}
