package Main;

import Main.Exceptions.QuantityException;

import java.util.GregorianCalendar;

public class ManageSales {
    public static boolean checkAvaliability(ProductList list, int productID, int toPurchase){
        return (list.retrieveByID(productID).getQuantity() >  toPurchase);
    }

    public static boolean executeSale(Product product, int quantity, ProductList avaliable, Account user) throws QuantityException {
        boolean canSell = checkAvaliability(avaliable, product.getID(), quantity);
        if (canSell){
            product.setQuantity(product.getQuantity() - quantity);
            DBA.updateProduct(user, product);
            DBA.insertIntoSales(user, product, quantity);
        }
        else{
            throw new QuantityException();
        }
        return canSell;
    }

    public static double revenueInPeriod(Account user, GregorianCalendar start, GregorianCalendar end){
        return DBA.revenueInPeriod(user, start, end);
    }
}
