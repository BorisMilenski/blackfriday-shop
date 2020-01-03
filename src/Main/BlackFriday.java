package Main;

import Main.Exceptions.NoDiscountException;

public class BlackFriday {
    public static void startCampaign(Account user, ProductList items) {
        for (Product item : items.getProducts()) {
            DBA.updateProduct(user, item);
        }
    }

    public static void endCampaign(Account user, ProductList items) {
        for (Product item : items.getProducts()) {
            try {
                item.setBlackfridayDiscount(0);
            } catch (NoDiscountException e) {
                System.out.println("Something is very wrong here. Abort. Abort.");
            }
            DBA.updateProduct(user, item);
        }
    }
}
