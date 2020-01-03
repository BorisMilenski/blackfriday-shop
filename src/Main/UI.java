package Main;

import Main.Exceptions.QuantityException;

import java.util.GregorianCalendar;

public class UI {
    public static void main(String[] args) {
        PrivilegedAccount root = new PrivilegedAccount("root", "vWmfTmtCFwhpdm&72N8A0@yl");
        ProductList products = DBA.loadProducts(root);
        for (int i = 0; i < products.getProducts().size(); i++) {


            Product product = products.getProducts().get(i);
            try {
                ManageSales.executeSale(product, i + 1, products, root);
            } catch (QuantityException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(ManageSales.revenueInPeriod(root, new GregorianCalendar(2019, 1, 2), new GregorianCalendar(1900 + 2020, 1, 3)));
    }
}
