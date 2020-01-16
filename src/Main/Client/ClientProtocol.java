package Main.Client;
import Main.Account;
import Main.IO.Terminal;
import Main.ProductList;
import Main.Message;

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
                    return answer.getPayload();
                case ERROR:
                    terminal.showError(answer.getPayload().toString());
                    return null;
                case PRODUCTS:
                    return answer.getPayload();
                case LOGIN:
                    return answer.getPayload();
            }
        }catch (NullPointerException e){

        }
        return null;
    }
}
