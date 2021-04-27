package org.maple.ch12.protocol;

public enum  MessageType {

    BUSINESS_REQ((byte)0),
    BUSINESS_RESP((byte)1),
    BUSINESS_REQ_RESP((byte)2),
    LOGIN_REQ((byte)3),
    LOGIN_RESP((byte)4),
    HEARTBEAT_REQ((byte)5),
    HEARTBEAT_RESP((byte)6),
    ;

    public byte value;

    MessageType(byte v){
        this.value = v;
    }

    public byte value(){
        return value;
    }
}
