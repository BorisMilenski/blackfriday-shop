package Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DBA {
    private static String databaseName = "blackfriday";

    public static ProductList loadProducts(Account DBM) throws SQLException {
        ProductList products = new ProductList(new ArrayList<Product>());
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                Statement stmt = conn.createStatement()
        ) {
            System.out.println("Connecting to database");
            String strSelect = "SELECT * FROM products";
            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect);
            System.out.println("The records selected are:");
            int rowCount = 0;
            while (rset.next()) {
                try {
                    products.add(new Product(rset.getInt("idproducts"),
                            rset.getString("name"),
                            rset.getString("category"),
                            rset.getDouble("price"),
                            rset.getDouble("minPrice"),
                            rset.getDouble("blackfridayDiscount"),
                            rset.getInt("quantity")));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Database contains incorrect values"); //TODO:
                }
                ++rowCount;
            }
            System.out.println("Total number of records = " + rowCount);

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }

        return products;
    }

    public static void insertIntoProducts(Account DBM, Product product) throws SQLException {
        String strInsert = "INSERT INTO blackfriday.products (name, category, price, minPrice, blackfridayDiscount, quantity)" +
                " values (?, ?, ?, ?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strInsert)
        ) {
            preparedStmt.setString(1, product.getName().toLowerCase());
            preparedStmt.setString(2, product.getCategory().toLowerCase());
            preparedStmt.setDouble(3, product.getPrice());
            preparedStmt.setDouble(4, product.getMinPrice());
            preparedStmt.setDouble(5, product.getBlackfridayDiscount());
            preparedStmt.setInt(6, product.getQuantity());
            System.out.println("Connecting to database");
            System.out.println("The SQL statement is: " + strInsert + "\n"); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe Echo

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static void updateProduct(Account DBM, Product product) throws SQLException {
        String strUpdate = "UPDATE blackfriday.products set name = ?, category = ?, price = ?, minPrice = ?, blackfridayDiscount = ?, quantity = ? where idproducts = ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());   // For MySQL only
                PreparedStatement preparedStmt = conn.prepareStatement(strUpdate)
        ) {
            preparedStmt.setString(1, product.getName());
            preparedStmt.setString(2, product.getCategory());
            preparedStmt.setDouble(3, product.getPrice());
            preparedStmt.setDouble(4, product.getMinPrice());
            preparedStmt.setDouble(5, product.getBlackfridayDiscount());
            preparedStmt.setInt(6, product.getQuantity());
            preparedStmt.setInt(7, product.getID());
            System.out.println("Connecting to database");
            System.out.println("The SQL statement is: " + strUpdate + "\n"); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
//
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static void deleteFromProducts(Account DBM, Product product) throws SQLException {
        String strDelete = "DELETE FROM blackfriday.products where idproducts = ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());   // For MySQL only
                PreparedStatement preparedStmt = conn.prepareStatement(strDelete)
        ) {
            preparedStmt.setFloat(1, product.getID());
            System.out.println("Connecting to database");
            System.out.println("The SQL statement is: " + strDelete + "\n"); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe echo the results
        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }

    public static double revenueInPeriod(Account DBM, GregorianCalendar start, GregorianCalendar end) throws SQLException {
        double revenue = 0;
        String strSelect = "SELECT price, quantity FROM sales WHERE date >= ? AND date < ?";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strSelect)
        ) {
            System.out.println("Connecting to database");
            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            preparedStmt.setObject(1, new java.sql.Timestamp(start.getTime().getTime()));
            preparedStmt.setObject(2, new java.sql.Timestamp(end.getTime().getTime()));
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null;
            System.out.println("The records selected are:");
            int rowCount = 0;
            while (rset.next()) {
                revenue += rset.getDouble(1) * rset.getInt(2);
            }
            System.out.println("Total number of records = " + rowCount);

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }

        return revenue;
    }


    public static void insertIntoSales(Account DBM, Account customer, Product toSell) throws SQLException {
        String strInsert = "INSERT INTO blackfriday.sales (idproduct, price, quantity, customer)" +
                " VALUES (?, ?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DBM.getUsername(), DBM.getPassword());
                PreparedStatement preparedStmt = conn.prepareStatement(strInsert)
        ) {
            preparedStmt.setInt(1, toSell.getID());
            preparedStmt.setDouble(2, toSell.getPrice() * (1 - toSell.getBlackfridayDiscount()));
            preparedStmt.setInt(3, toSell.getQuantity());
            preparedStmt.setString(4, customer.getUsername());
            System.out.println("The SQL statement is: " + strInsert + "\n"); // Echo For debugging
            ResultSet rset = (preparedStmt.execute()) ? preparedStmt.getResultSet() : null; //TODO: Maybe Echo

        } catch (SQLException e) {
            throw new SQLException(e.getCause());
        }
    }


   /* public static void createAccount(Account user, String databaseName) { //TODO: Consider changing from creating MySQL accounts to something else
        String crtAcc = "{CALL create_customer(?,?)}";

        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + databaseName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        "createusers", "6O8c%rwfLqcfNRnuANgY2J5o");
                CallableStatement preparedStmt = conn.prepareCall(crtAcc)
        ) {
            preparedStmt.setString(1, user.getUsername());
            preparedStmt.setString(2, user.getPassword());
            System.out.println("The SQL statement is: " + crtAcc + "\n"); // Echo For debugging
            ResultSet rs = preparedStmt.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(printAuthorizationErrorMessage());
        }
    }*/


    private static String printAuthorizationErrorMessage() {
        return "You are not authorized to perform such queries";
    }

    public static boolean ValidateAccount(Account account) {//TODO: Validate accounts through DB
        return false;
    }
}
