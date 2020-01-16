package Main;
import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    private int ID;
    private String name;
    private Category category;
    private int quantity;
    private double normalPrice;
    private double minPrice;
    private double blackfridayDiscount;
    private double actualPrice;
    private double popularity;

    public enum Category {
        tech,
        cookware,
        misc
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public Product(int ID, String name, Category category, int quantity, double normalPrice, double minPrice, double blackfridayDiscount) {
        this.minPrice = 0;
        this.normalPrice = 0;
        this.blackfridayDiscount = 0;
        this.quantity = 0;
        try {
            this.setID(ID);
            this.setName(name);
            this.setCategory(category);
            this.setMinPrice(minPrice);
            this.setNormalPrice(normalPrice);
            this.setBlackfridayDiscount(blackfridayDiscount);
            this.setQuantity(quantity);
        } catch (ArithmeticException a) {
            throw new ArithmeticException();
        }
    }

    public Product() {
        this.ID = -1;
        this.name = " ";
        this.category = Category.misc;
        this.normalPrice = 0;
        this.minPrice = 0;
        this.blackfridayDiscount = 0;
        this.quantity = 0;
        this.popularity = 0;
    }

    public boolean modify(Product product) {
        if (this.ID != product.getID()) {
            this.setID(product.getID());
        }
        if (!this.getName().equals(product.getName())) {
            this.setName(product.getName());
        }
        if (!this.getCategory().equals(product.getCategory())) {
            this.setCategory(product.getCategory());
        }
        if (this.getNormalPrice() != product.getNormalPrice()) {
            this.setNormalPrice(product.getNormalPrice());
        }
        if (this.getMinPrice() != product.getMinPrice()) {
            this.setMinPrice(product.getMinPrice());
        }
        if (this.getBlackfridayDiscount() != product.getBlackfridayDiscount()) {
            this.setBlackfridayDiscount(product.getBlackfridayDiscount());
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(double normalPrice) throws ArithmeticException {
        if (normalPrice < 0) {
            throw new ArithmeticException();
        }
        this.normalPrice = (normalPrice > this.minPrice) ? normalPrice : this.minPrice;
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

    public void setBlackfridayDiscount(double blackfridayDiscount) throws ArithmeticException {
        if (blackfridayDiscount < 0 || blackfridayDiscount >= 1) {
            throw new ArithmeticException();
        } else {
            this.blackfridayDiscount = blackfridayDiscount;
            this.actualPrice = this.normalPrice * (1 - this.getBlackfridayDiscount());
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
                Double.compare(product.getNormalPrice(), getNormalPrice()) == 0 &&
                Double.compare(product.getMinPrice(), getMinPrice()) == 0 &&
                Double.compare(product.getBlackfridayDiscount(), getBlackfridayDiscount()) == 0 &&
                getQuantity() == product.getQuantity() &&
                Double.compare(product.getPopularity(), getPopularity()) == 0 &&
                getName().equals(product.getName()) &&
                getCategory().equals(product.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getName(), getCategory(), getNormalPrice(), getMinPrice(), getBlackfridayDiscount(), getQuantity(), getPopularity());
    }
}
