package Main.Server;

import Main.Account;
import Main.ProductList;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;

public class Server {
    private static HashMap<InetAddress, Account> activeUsers = new HashMap<>();
    private static HashMap<InetAddress, Account> activeEmployees = new HashMap<>();
    private static ProductList allProducts;
    public static void main(String[] args){
        MultiThreadedServer server = new MultiThreadedServer(50121);
        updateProducts();
        server.run();
    }
    public static synchronized void updateProducts(){
        try {
            allProducts = DBA.loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ProductList getProducts(){
        return allProducts;
    }

    public static HashMap<InetAddress, Account> getActiveUsers() {
        return activeUsers;
    }

    public static HashMap<InetAddress, Account> getActiveEmployees() {
        return activeEmployees;
    }

    public static synchronized void addActiveEmployee(InetAddress inetAddress,Account account) {
        activeEmployees.put(inetAddress, account);
    }

    public static synchronized void addActiveUser(InetAddress inetAddress,Account account) {
        activeUsers.put(inetAddress, account);
    }

}
