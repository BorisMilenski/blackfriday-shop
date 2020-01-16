package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ProductList implements Serializable {
    private final ArrayList<Product> products;

    public ProductList(ArrayList<Product> products) {
        this.products = products;
    }

    public ProductList() {
        this.products = new ArrayList<>();
    }

    public void add(Product product) {
        products.add(product);
    }

    public Product get(int index) {
        return products.get(index);
    }

    public void remove(Product product) {
        products.remove(product);
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

    public ArrayList<Product> getProducts() {
        return products;
    }

    public static double calculateTotal(ProductList products) {
        double total = 0;
        for (Product p: products.getProducts()) {
            total += p.getQuantity() * p.getActualPrice();
        }
        return total;
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
