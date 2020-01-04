package Main;

import Main.Exceptions.QuantityException;

import java.sql.SQLException;
import java.util.GregorianCalendar;

public class ManageSales {
    public static boolean checkAvaliability(Product toPurchase, ProductList available){
        return (available.retrieveByID(toPurchase.getID()).getQuantity() >  toPurchase.getQuantity());
    }

    public static void executeSale(Account customer, Product toPurchase, ProductList allProducts, Account DBM) throws QuantityException, SQLException {
        boolean canSell = checkAvaliability(toPurchase, allProducts);
        if (canSell){
            Product sold = allProducts.retrieveByID(toPurchase.getID());
            sold.setQuantity(sold.getQuantity() - toPurchase.getQuantity());
            DBA.updateProduct(DBM, toPurchase);
            DBA.insertIntoSales(DBM, customer, toPurchase);
        }
        else{
            throw new QuantityException(toPurchase.getName());
        }
    }

    public static double revenueInPeriod(Account user, GregorianCalendar start, GregorianCalendar end) throws SQLException {
        return DBA.revenueInPeriod(user, start, end);
    }
}
