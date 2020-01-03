package Main;

import java.util.ArrayList;

public class CustomerAcc extends Account implements Wishlist{
    private ProductList wishlist;

    public CustomerAcc(String username, String password) {
        super(username, password);
        wishlist = new ProductList(new ArrayList<Product>());
    }

    public CustomerAcc(String username, String password, ProductList wishlist) {
        super(username, password);
        this.wishlist = wishlist;
    }

    @Override
    public ProductList accessProducts() {
        return null;
    }
    public ProductList updateWishlist(ProductList allProducts) {
         return ProductList.compareLists(allProducts, wishlist);
    }



}


