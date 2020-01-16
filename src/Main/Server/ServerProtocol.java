package Main.Server;

import Main.*;
import Main.Exceptions.InvalidAccountException;
import Main.Exceptions.QuantityException;

import java.net.InetAddress;
import java.sql.SQLException;

/*
Client -> Server

PRODUCTS - to request allProducts
LOGIN <Account> - to login
CREATE <Account> - to create account
PURCHASE <ProductList> - to purchase
INSERT <ProductList> - to insert
EDIT <ProductList> - to edit
DELETE <ProductList> - to delete
STARTBLACKFRIDAY <ProductList> - to start BlackFriday on products in list
ENDBLACKFRIDAY - to end BlackFriday for all products

*/
public class ServerProtocol {

    public static Message parseMessage(Message message, Account user, InetAddress inetAddress) throws QuantityException, SQLException, InvalidAccountException {
        switch (message.getCode()) {
            case PRODUCTS:
                return new Message(MessageIndex.PRODUCTS, Server.getProducts());
            case LOGIN:
                Account account = (Account) message.getPayload();
                if ((account = DBA.checkForExistingAccount(account)) != null)
                {
                    if (account.isEmployee() && !Server.getActiveEmployees().containsKey(inetAddress)) {
                        Server.addActiveEmployee(inetAddress, account);
                    } else if (!account.isEmployee() && !Server.getActiveUsers().containsKey(inetAddress)) {
                        Server.addActiveUser(inetAddress, account);
                    }
                }
                else{
                    throw new InvalidAccountException("Account not found");
                }
                return new Message(MessageIndex.LOGIN, account);
            case CREATE:
                DBA.createAccount((Account) message.getPayload());
                return new Message(MessageIndex.CREATE, message.getPayload());
            case PURCHASE:
                ProductList toPurchase = (ProductList) message.getPayload();
                for (Product item : toPurchase.getProducts()) {
                    try {
                        ManageSales.executeSale(user, item, Server.getProducts());
                    } catch (QuantityException e) {
                        throw new QuantityException(item.getName());
                    }
                }
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
            case INSERT:
                ProductList toInsert = (ProductList) message.getPayload();
                for (Product item : toInsert.getProducts()) {
                    try {
                        DBA.insertIntoProducts(item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
            case EDIT:
                ProductList toEdit = (ProductList) message.getPayload();
                for (Product item : toEdit.getProducts()) {
                    try {
                        DBA.updateProduct(item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
            case DELETE:
                ProductList toDelete = (ProductList) message.getPayload();
                for (Product item : toDelete.getProducts()) {
                    try {
                        DBA.deleteFromProducts(item);
                    } catch (SQLException e) {
                        throw new SQLException(e.getCause());
                    }
                }
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
            case STARTBLACKFRIDAY:
                ProductList promotionalItems = (ProductList) message.getPayload();
                BlackFriday.startCampaign(promotionalItems);
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
            case ENDBLACKFRIDAY:
                ProductList items = (ProductList) message.getPayload();
                BlackFriday.endCampaign(items);
                Server.updateProducts();
                return new Message(MessageIndex.SUCCESS, true);
        }
        return null;
    }


}
