package Main.Server;

import Main.MessageIndex;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageIndex code;
    private Object toSend;

    public Message(MessageIndex code, Object toSend) {
        this.code = code;
        this.toSend = toSend;
    }

    public MessageIndex getCode() {
        return code;
    }

    public void setCode(MessageIndex code) {
        this.code = code;
    }

    public Object getToSend() {
        return toSend;
    }

    public void setToSend(Object toSend) {
        this.toSend = toSend;
    }
}
