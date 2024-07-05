package com.prince.procotol;

public enum ProtocolMessageStatueEnum {

    OK("OK", 20),
    BAD_REQUEST("BAD_REQUEST", 40),
    BAD_RESPONSE("BAD_RESPONSE", 50),
    ;

    private final String text;

    private final int code;
    
    ProtocolMessageStatueEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    public static ProtocolMessageStatueEnum getProtocolEnumByCode(int code) {
        for (ProtocolMessageStatueEnum protocolEnum : ProtocolMessageStatueEnum.values()) {
            if (protocolEnum.code == code) {
                return protocolEnum;
            }
        }
        return null;
    }
}
