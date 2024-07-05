package com.prince.procotol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;

    private final String serializer;

    ProtocolMessageSerializerEnum(int key, String serializer) {
        this.key = key;
        this.serializer = serializer;
    }

    public int getKey() {
        return key;
    }

    public String getSerializer() {
        return serializer;
    }

    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (serializerEnum.key == key) return serializerEnum;
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getEnumBySerializer(String serializer) {
        for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (serializerEnum.serializer.equals(serializer)) return serializerEnum;
        }
        return null;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(serializerEnum -> serializerEnum.serializer)
                .collect(Collectors.toList());
    }

}
