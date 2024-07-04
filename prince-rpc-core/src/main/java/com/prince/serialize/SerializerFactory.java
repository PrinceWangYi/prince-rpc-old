package com.prince.serialize;

import com.prince.spi.SpiLoader;

import java.util.Map;

public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    public static Serializer getSerializer(String serializer) {
        return SpiLoader.getInstance(Serializer.class, serializer);
    }

    /*private static final Map<String, Serializer> SERIALIZER_MAP = Map.of(
            SerializerKeys.JDK, new JDKSerialize(),
            SerializerKeys.KRYO, new KryoSerializer(),
            SerializerKeys.HESSIAN, new HessianSerializer(),
            SerializerKeys.JSON, new JsonSerializer()
    );

    public static Serializer getSerializer(String serializer) {
        return SERIALIZER_MAP.getOrDefault(serializer, DEFAULT_SERIALIZER);
    }

    public static Serializer DEFAULT_SERIALIZER = SERIALIZER_MAP.get(SerializerKeys.JDK);*/

}
