package com.prince.config;

import lombok.Data;

@Data
public class RegistryConfig {

    private String registry = "etcd";

    private String address = "http://localhost:2379";

    private Long timeout = 10000L;

    private String username;

    private String password;

}
