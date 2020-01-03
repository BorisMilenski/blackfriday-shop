package Main;

public class PrivilegedAccount extends Account{
    public PrivilegedAccount(String username, String password) {
        super(username, password);
    }

    @Override
    public ProductList accessProducts() {
        return null;
    }
}
