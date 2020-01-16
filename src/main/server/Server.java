package main.server;

import main.Account;
import main.ProductList;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;

public class Server {
    private static final HashMap<InetAddress, Account> ACTIVE_USERS = new HashMap<>();
    private static final HashMap<InetAddress, Account> ACTIVE_EMPLOYEES = new HashMap<>();
    private static ProductList allProducts;

    public static void main(String[] args) {
        MultiThreadedServer server = new MultiThreadedServer(50121);
        updateProducts();
        server.run();
    }

    public static synchronized void updateProducts() {
        try {
            allProducts = DBA.loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ProductList getProducts() {
        return allProducts;
    }

    public synchronized static HashMap<InetAddress, Account> getActiveUsers() {
        return ACTIVE_USERS;
    }

    public synchronized static HashMap<InetAddress, Account> getActiveEmployees() {
        return ACTIVE_EMPLOYEES;
    }

    public static synchronized void addActiveEmployee(InetAddress inetAddress, Account account) {
        ACTIVE_EMPLOYEES.put(inetAddress, account);
    }

    public static synchronized void addActiveUser(InetAddress inetAddress, Account account) {
        ACTIVE_USERS.put(inetAddress, account);
    }

}
