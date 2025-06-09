package advancedinventorymanagementsystem;

import java.sql.*;

public class ProductManager {

    // Method to add a product to the database
    public static void addProduct(String name, String category, double price) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Products (Name, Category, Price) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            // Set values for the prepared statement
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();

            // Check if product was added successfully
            if (rowsAffected > 0) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    
    }
    public static void updateProduct(String name, String category, double price) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Products (Name, Category, Price) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            // Set values for the prepared statement
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();

            // Check if product was added successfully
            if (rowsAffected > 0) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    
    }
    
}

