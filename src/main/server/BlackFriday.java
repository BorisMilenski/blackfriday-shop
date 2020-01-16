package main.server;
import main.Product;
import main.ProductList;

import java.sql.SQLException;

public class BlackFriday {
    public static void startCampaign(ProductList items) throws SQLException {
        for (Product item : items.getProducts()) {
            DBA.updateProduct(item);
        }
    }

    public static void endCampaign(ProductList items) throws SQLException {
        for (Product item : items.getProducts()) {
            item.setBlackfridayDiscount(0);
            DBA.updateProduct(item);
        }
    }
}
