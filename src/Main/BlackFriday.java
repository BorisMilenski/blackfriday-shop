package Main;

import Main.Exceptions.NoDiscountException;

import java.sql.SQLException;

public class BlackFriday {
    public static void startCampaign(Account DBM, ProductList items) throws SQLException {
        for (Product item : items.getProducts()) {
            DBA.updateProduct(DBM, item);
        }
    }

    public static void endCampaign(Account DBM, ProductList items) throws SQLException {
        for (Product item : items.getProducts()) {
            try {
                item.setBlackfridayDiscount(0);
            } catch (NoDiscountException e) {
                System.out.println("Something is very wrong here. Abort. Abort.");
            }
            DBA.updateProduct(DBM, item);
        }
    }
}
