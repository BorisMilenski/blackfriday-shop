package Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ProductList implements Serializable {
    private ArrayList<Product> products;

    public ProductList(ArrayList<Product> products) {
        this.products = products;
    }

    public ProductList() {
        this.products = new ArrayList<>();
    }

    public boolean add(Product product) {
        return products.add(product);
    }

    public Product get(int index){
        return products.get(index);
    }

    public boolean edit(Product product) {
        return this.retrieveByID(product.getID()).modify(product);
    }

    public boolean remove(Product product) {
        return products.remove(product);
    }

    public Product retrieveByID(int ID) {
        Product product = null;
        for (Product p : products) {
            if (p.getID() == ID) {
                product = p;
            }
        }
        return product;
    }
//    public Help.Product retrieveByName(String name) {
//        Help.Product product = null;
//        for (Help.Product p : products) {
//            if (p.getName() == name) {
//                product = p;
//            }
//        }
//        return product;
//    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public static ProductList compareLists(ProductList list, ProductList sublist) {
        ProductList changedProducts = new ProductList(new ArrayList<Product>());
        ArrayList<Product> sub = new ArrayList<>(sublist.getProducts());
        for (Product product : sub) {
            if (!(list.getProducts().contains(product))) {
                changedProducts.add(product);
                sublist.remove(product);
            }
        }
        for (Product product : changedProducts.getProducts()) {
            product = list.retrieveByID(product.getID());
            sublist.add(product);
        }
        return changedProducts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductList that = (ProductList) o;
        return Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(products);
    }

}
