package Main;

import Main.Exceptions.NoDiscountException;

import java.util.Objects;

public class Product {

    private int ID;
    private String name;
    private String category;
    private double price;
    private double minPrice;
    private double blackfridayDiscount;
    private int quantity;
    private double popularity;

    public Product(int ID, String name, String category, double price, double minPrice, double blackfridayDiscount, int quantity) throws NoDiscountException {
        this.minPrice = 0;
        this.price = 0;
        this.blackfridayDiscount = 0;
        this.quantity = 0;
        try {
            this.setID(ID);
            this.setName(name);
            this.setCategory(category);
            this.setMinPrice(minPrice);
            this.setPrice(price);
            this.setBlackfridayDiscount(blackfridayDiscount);
            this.setQuantity(quantity);
        }
        catch (ArithmeticException a){
            throw new ArithmeticException();
        }
        catch (NoDiscountException e){
            throw new NoDiscountException(this.getName());
        }
    }

    public boolean modify(Product product) {
        //TODO: Check user input for inapplicable data (possibly do checks or throw exceptions in setters). Currently a user can clear fields.
        if (this.getName().equals(product.getName())) {
            this.setName(product.getName());
        }
        if (this.getCategory().equals(product.getCategory())) {
            this.setCategory(product.getCategory());
        }
        if (this.getPrice() != product.getPrice()) {
            this.setPrice(product.getPrice());
        }
        if (this.getMinPrice() != product.getMinPrice()) {
            this.setMinPrice(product.getMinPrice());
        }
        if (this.getBlackfridayDiscount() != product.getBlackfridayDiscount()) {
            try {
                this.setBlackfridayDiscount(product.getBlackfridayDiscount());
            }
            catch (NoDiscountException e){
                System.out.println(this.getName() + "'s discount percentage is either invalid or too high");
            }
        }
        if (this.getQuantity() != product.getQuantity()) {
            this.setQuantity(product.getQuantity());
        }
        return true;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws ArithmeticException {
        if (price < 0) {
            throw new ArithmeticException();
        }
        this.price = !(price > this.minPrice) ? price : this.minPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) throws ArithmeticException {
        if (minPrice > 0) {
            this.minPrice = minPrice;
        } else {
            throw new ArithmeticException();
        }
    }

    public double getBlackfridayDiscount() {
        return blackfridayDiscount;
    }

    public void setBlackfridayDiscount(double blackfridayDiscount) throws ArithmeticException, NoDiscountException {
        if (blackfridayDiscount < 0 || blackfridayDiscount > 1) {
            throw new ArithmeticException();
        } else if (blackfridayDiscount * this.price >= this.minPrice) {
            throw new NoDiscountException(this.getName());
        } else {
            this.blackfridayDiscount = blackfridayDiscount;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws ArithmeticException {
        if (quantity >= 0) {
            this.quantity = quantity;
        } else {
            throw new ArithmeticException();
        }
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getID() == product.getID() &&
                Double.compare(product.getPrice(), getPrice()) == 0 &&
                Double.compare(product.getMinPrice(), getMinPrice()) == 0 &&
                Double.compare(product.getBlackfridayDiscount(), getBlackfridayDiscount()) == 0 &&
                getQuantity() == product.getQuantity() &&
                Double.compare(product.getPopularity(), getPopularity()) == 0 &&
                getName().equals(product.getName()) &&
                getCategory().equals(product.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getName(), getCategory(), getPrice(), getMinPrice(), getBlackfridayDiscount(), getQuantity(), getPopularity());
    }
}
