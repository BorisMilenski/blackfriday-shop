package main.client;
import main.IO.Terminal;
import main.Message;

/*
Server -> Client
        VALUE <double> - revenue
        ERROR <String> - to be printed - errors (low on quantity, unauthorized actions), verifications (purchase successful),
        PRODUCTS <ProductList> - sending allProducts
        LOGIN <Account>- login
*/
public class ClientProtocol {

    public static Object parseMessage(Message answer, Terminal terminal) {
        try {
            switch (answer.getCode()) {
                case VALUE:
                case PRODUCTS:
                case LOGIN:
                    return answer.getPayload();
                case ERROR:
                    terminal.showError(answer.getPayload().toString());
            }
        }catch (NullPointerException e){

        }
        return null;
    }
}
