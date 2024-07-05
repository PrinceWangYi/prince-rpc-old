package com.prince.procotol;

public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getProtocolEnumByCode(int key) {
        for (ProtocolMessageTypeEnum protocolEnum : ProtocolMessageTypeEnum.values()) {
            if (protocolEnum.key == key) {
                return protocolEnum;
            }
        }
        return null;
    }
}
