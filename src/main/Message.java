package main;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageIndex code;
    private Object payload;

    public Message(MessageIndex code, Object payload) {
        this.code = code;
        this.payload = payload;
    }

    public MessageIndex getCode() {
        return code;
    }

    public Object getPayload() {
        return payload;
    }

    public enum MessageIndex implements Serializable {
        //Client -> Server
        PRODUCTS,
        LOGIN,
        CREATE,
        PURCHASE,
        INSERT,
        EDIT,
        DELETE,
        START_BLACK_FRIDAY,
        END_BLACK_FRIDAY,
        REVENUE,
        //Server -> Client
        VALUE,
        ERROR,
        SUCCESS
    }
}
