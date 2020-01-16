package Main;

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

    public void setCode(MessageIndex code) {
        this.code = code;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
