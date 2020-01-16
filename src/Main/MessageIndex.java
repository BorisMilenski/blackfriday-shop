package Main;

import java.io.Serializable;

public enum MessageIndex implements Serializable {
    //Client -> Server
    PRODUCTS,
    LOGIN,
    CREATE,
    PURCHASE,
    INSERT,
    EDIT,
    DELETE,
    STARTBLACKFRIDAY,
    ENDBLACKFRIDAY,
    //Server -> Client
    VALUE,
    ERROR,
    SUCCESS

}
