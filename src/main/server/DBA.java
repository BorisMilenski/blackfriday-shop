package main.server;

import main.Account;
import main.Product;
import main.ProductList;

import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DBA {
    private static final String DATABASE_NAME = "blackfriday";
    private static final Account DBM = new Account("DBM", "Tn65z6&dDObh@YJRRt39OwhV", true);

    public static ProductList loadProducts() throws SQLException {
        ProductList products = new ProductList(new ArrayList<>());
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                Statement stmt = conn.createStatement()
        ) {
            System.out.print("Connecting to database");
            String strSelect = "SELECT * FROM products";
            System.out.println("The SQL statement is: " + strSelect); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect);
            int rowCount = 0;
            while (rset.next()) {
                try {
                    products.add(new Product(rset.getInt("idproducts"),
                            rset.getString("name"),
                            Product.Category.valueOf(rset.getString("category")),
                            rset.getInt("quantity"),
                            rset.getDouble("price"),
                            rset.getDouble("minPrice"),
                            rset.getDouble("blackfridayDiscount")
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Database contains incorrect values");
                }
                ++rowCount;
            }
            System.out.println("Total number of records = " + rowCount + "\n");

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }

        return products;
    }

    public static void insertIntoProducts(Product product) throws SQLException {
        String strInsert = "INSERT INTO blackfriday.products (name, category, price, minPrice, blackfridayDiscount, quantity)" +
                " values (?, ?, ?, ?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strInsert)
        ) {
            preparedStmt.setString(1, product.getName());
            preparedStmt.setString(2, product.getCategory().toString());
            preparedStmt.setDouble(3, product.getNormalPrice());
            preparedStmt.setDouble(4, product.getMinPrice());
            preparedStmt.setDouble(5, product.getBlackfridayDiscount());
            preparedStmt.setInt(6, product.getQuantity());
            System.out.print("Connecting to database");
            System.out.println("The SQL statement is: " + strInsert); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe Echo
            System.out.println("Products Inserted" + "\n"); // Echo For debugging
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static void updateProduct(Product product) throws SQLException {
        String strUpdate = "UPDATE blackfriday.products set name = ?, category = ?, price = ?, minPrice = ?, blackfridayDiscount = ?, quantity = ? where idproducts = ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());   // For MySQL only
                PreparedStatement preparedStmt = conn.prepareStatement(strUpdate)
        ) {
            preparedStmt.setString(1, product.getName());
            preparedStmt.setString(2, product.getCategory().toString());
            preparedStmt.setDouble(3, product.getNormalPrice());
            preparedStmt.setDouble(4, product.getMinPrice());
            preparedStmt.setDouble(5, product.getBlackfridayDiscount());
            preparedStmt.setInt(6, product.getQuantity());
            preparedStmt.setInt(7, product.getID());
            System.out.print("Connecting to database");
            System.out.println("The SQL statement is: " + strUpdate); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
            System.out.println("Products Updated" + "\n"); // Echo For debugging
//
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static void deleteFromProducts(Product product) throws SQLException {
        String strDelete = "DELETE FROM blackfriday.products where idproducts = ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());   // For MySQL only
                PreparedStatement preparedStmt = conn.prepareStatement(strDelete)
        ) {
            preparedStmt.setFloat(1, product.getID());
            System.out.println("Connecting to database: The SQL statement is: " + strDelete + "\n");
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe echo the results
            System.out.println("Records deleted");
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }catch (NullPointerException e){
            System.out.println("No product to delete");
            return;
        }
    }

    public static double revenueInPeriod(GregorianCalendar start, GregorianCalendar end) throws SQLException {
        double revenue = 0;
        String strSelect = "SELECT price, quantity FROM blackfriday.sales WHERE date >= ? AND date < ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strSelect)
        ) {
            System.out.println("Connecting to database: The SQL statement is: " + strSelect + "\n");
            preparedStmt.setObject(1, new java.sql.Timestamp(start.getTime().getTime()));
            preparedStmt.setObject(2, new java.sql.Timestamp(end.getTime().getTime()));
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
            if (rset != null) {
                while (rset.next()) {
                    revenue += rset.getDouble(1) * rset.getInt(2);
                }
            }
            System.out.println("Revenue = " + revenue);

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }

        return revenue;
    }

    public static void insertIntoSales(Account customer, Product toSell) throws SQLException {
        String strInsert = "INSERT INTO blackfriday.sales (idproduct, price, quantity, customer)" +
                " VALUES (?, ?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strInsert)
        ) {
            preparedStmt.setInt(1, toSell.getID());
            preparedStmt.setDouble(2, toSell.getActualPrice());
            preparedStmt.setInt(3, toSell.getQuantity());
            preparedStmt.setString(4, customer.getUsername());
            System.out.println("The SQL statement is: " + strInsert + "\n"); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe Echo

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static Account checkForExistingAccount(Account account) throws SQLException {
        String strSelect = "SELECT username, password, employee FROM blackfriday.accounts WHERE username = ? AND password = ?";
        Account out = null;
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strSelect)
        ) {
            System.out.println("Connecting to database:The SQL statement is: " + strSelect + " \n");

            preparedStmt.setString(1, account.getUsername());
            preparedStmt.setString(2, account.getPassword());
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
            if (rset != null && rset.next()) {
                out = new Account(rset.getString("username"), rset.getString("password"), rset.getBoolean("employee"));
            }
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
        return out;
    }

    public static void createAccount(Account account) throws SQLException {
        String strInsert = "INSERT INTO blackfriday.accounts(username, password, employee) VALUES (?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strInsert)
        ) {
            System.out.println("Connecting to database:The SQL statement is: " + strInsert + " \n");

            preparedStmt.setString(1, account.getUsername());
            preparedStmt.setString(2, account.getPassword());
            preparedStmt.setBoolean(3, account.isEmployee());
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }
}
