package com.prince.registry;

import com.prince.spi.SpiLoader;

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getRegistry(String registry) {
        return SpiLoader.getInstance(Registry.class, registry);
    }
}
