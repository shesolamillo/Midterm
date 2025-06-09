package advancedinventorymanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";  // Replace with your DB details
    private static final String USERNAME = "root";  // Replace with your DB username
    private static final String PASSWORD = "12345";  // Replace with your DB password

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error connecting to the database.", e);
        }
    }
}


