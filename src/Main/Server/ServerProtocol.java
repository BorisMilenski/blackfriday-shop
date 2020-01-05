package Main.Server;

import Main.*;
import Main.Exceptions.QuantityException;
import com.mysql.cj.result.SqlDateValueFactory;

import java.sql.SQLException;

/*
Client -> Server

LIST - to request allProducts
ACC <Account> - to login
CREATE <Account> - to create account
PURCHASE <ProductList> - to purchase
INS <ProductList> - to insert
EDIT <ProductList> - to edit
DEL <ProductList> - to delete
STARTBLACKFRIDAY --- ProductList - to start BlackFriday
ENDBLACKFRIDAY --- null - to end BlackFriday

*/
public class ServerProtocol {

    public static Object parseMessage(Message message, Account user, MultiThreadedServer server) throws QuantityException, SQLException {
        switch (message.getCode()) {
            case LIST:
                return server.allProducts;
            case ACC:
                if (DBA.ValidateAccount((Account) message.getToSend())) //TODO: Validate accounts and add to appropriate collection - admin or customer
                {
                    return "Logic successful";
                } else {
                    return "Logic unsuccessful. Please check your username and password and try again.";
                }
            case CREATE:
                //TODO: Create accounts
                break;
            case PURCHASE:
                ProductList toPurchase = (ProductList) message.getToSend();
                for (Product item : toPurchase.getProducts()) {
                    try {
                        ManageSales.executeSale(user, item, server.allProducts, new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"));
                    } catch (QuantityException e) {
                        throw new QuantityException(item.getName());
                    }
                }
                break;
            case INS:
                ProductList toInsert = (ProductList) message.getToSend();
                for (Product item : toInsert.getProducts()) {
                    try {
                        DBA.insertIntoProducts(new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"), item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                break;
            case EDIT:
                ProductList toEdit = (ProductList) message.getToSend();
                for (Product item : toEdit.getProducts()) {
                    try {
                        DBA.updateProduct(new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"), item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                break;
            case DEL:
                ProductList toDelete = (ProductList) message.getToSend();
                for (Product item : toDelete.getProducts()) {
                    try {
                        DBA.deleteFromProducts(new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"), item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                break;
            case STARTBLACKFRIDAY:
                ProductList promotionalItems = (ProductList) message.getToSend();
                BlackFriday.startCampaign(new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"), promotionalItems);
                break;
            case ENDBLACKFRIDAY:
                ProductList items = (ProductList) message.getToSend();
                BlackFriday.endCampaign(new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV"), items);
                break;
        }
        return null;
    }


}
