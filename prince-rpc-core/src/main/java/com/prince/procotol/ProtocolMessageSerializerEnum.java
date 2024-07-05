package com.prince.procotol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolMessageSerializerEnum {
    JDK(0, "JDK"),
    JSON(1, "JSON"),
    KRYO(2, "KRYO"),
    HESSIAN(3, "HESSIAN");

    private final int key;

    private final String serializer;

    ProtocolMessageSerializerEnum(int key, String serializer) {
        this.key = key;
        this.serializer = serializer;
    }

    public static ProtocolMessageSerializerEnum getSerializerByKey(int key) {
        for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (serializerEnum.key == key) return serializerEnum;
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getKeyBySerializer(String serializer) {
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
