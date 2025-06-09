package advancedinventorymanagementsystem;

import java.sql.*;

public class SalesManager {

    public static void recordSale(String productName, int quantitySold, double totalAmount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get ProductID for the selected product
            String productQuery = "SELECT ProductID FROM Products WHERE Name = ?";
            PreparedStatement productStmt = conn.prepareStatement(productQuery);
            productStmt.setString(1, productName);
            ResultSet rs = productStmt.executeQuery();

            int productID = 0;
            if (rs.next()) {
                productID = rs.getInt("ProductID");
            }

            // Insert the sale into the Sales table
            String saleQuery = "INSERT INTO Sales (ProductID, QuantitySold, SaleDate, TotalAmount) VALUES (?, ?, NOW(), ?)";
            PreparedStatement saleStmt = conn.prepareStatement(saleQuery);
            saleStmt.setInt(1, productID);
            saleStmt.setInt(2, quantitySold);
            saleStmt.setDouble(3, totalAmount);
            saleStmt.executeUpdate();

            // Update stock after the sale
            String stockUpdateQuery = "UPDATE Stock SET QuantityAdded = QuantityAdded - ? WHERE ProductID = ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockUpdateQuery);
            stockStmt.setInt(1, quantitySold);
            stockStmt.setInt(2, productID);
            stockStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



