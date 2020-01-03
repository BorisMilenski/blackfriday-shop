package Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ProductList {
    private ArrayList<Product> products;

    public ProductList(ArrayList<Product> products) {
        this.products = products;
    }

    public boolean add(Product product) {
        return products.add(product);
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

    public void sort(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "id":
                products.sort(Comparator.comparing(Product::getID));
                break;
            case "name":
                products.sort(Comparator.comparing(Product::getName));
                break;
            case "category":
                products.sort(Comparator.comparing(Product::getCategory));
                break;
            case "minprice":
                products.sort(Comparator.comparing(Product::getMinPrice));
                break;
            case "currprice":
                products.sort(Comparator.comparing(Product::getPrice));
                break;
            case "blackfridaydiscount":
                products.sort(Comparator.comparing(Product::getBlackfridayDiscount));
                break;
            case "quantity":
                products.sort(Comparator.comparing(Product::getQuantity));
                break;
            case "popularity":
                products.sort(Comparator.comparing(Product::getPopularity));
                break;
            default:
                break;
        }
    }

    public static ProductList compareLists(ProductList list, ProductList sublist) {
        ProductList changedProducts = new ProductList(new ArrayList<Product>());
        ArrayList<Product> sub = new ArrayList<>(sublist.getProducts()); //TODO: Remember - lists are reference-based! Careful with trying to copy them by using =
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
