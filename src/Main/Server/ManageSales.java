package Main.Server;

import Main.Account;
import Main.Exceptions.QuantityException;
import Main.Product;
import Main.ProductList;

import java.sql.SQLException;
import java.util.GregorianCalendar;

public class ManageSales {
    public static boolean checkAvaliability(Product toPurchase, ProductList available){
        return (available.retrieveByID(toPurchase.getID()).getQuantity() >=  toPurchase.getQuantity());
    }

    public static synchronized void executeSale(Account customer, Product toPurchase, ProductList allProducts) throws QuantityException, SQLException {
        boolean canSell = checkAvaliability(toPurchase, allProducts);
        if (canSell){
            Product sold = allProducts.retrieveByID(toPurchase.getID());
            sold.setQuantity(sold.getQuantity() - toPurchase.getQuantity());
            DBA.updateProduct(sold);
            DBA.insertIntoSales(customer, toPurchase);
        }
        else{
            throw new QuantityException(toPurchase.getName());
        }
    }

    public static synchronized double revenueInPeriod(Account user, GregorianCalendar start, GregorianCalendar end) throws SQLException {
        return DBA.revenueInPeriod(start, end);
    }
}
