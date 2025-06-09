package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewSupplierProducts extends JFrame {
    
    private JTable productsTable;
    private DefaultTableModel productsTableModel;

    public ViewSupplierProducts(int supplierId) {
        setTitle("Products for Supplier");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout
        setLayout(new BorderLayout());

        // Table to display products
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price"};
        productsTableModel = new DefaultTableModel(columnNames, 0);
        productsTable = new JTable(productsTableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);

        add(scrollPane, BorderLayout.CENTER);

        // Load products for this supplier
        loadProductsForSupplier(supplierId);
    }

    private void loadProductsForSupplier(int supplierId) {
        // Clear the current data in the table
        productsTableModel.setRowCount(0);

        String query = "SELECT p.ProductID, p.Name, p.Category, p.Price " +
                       "FROM Products p " +
                       "JOIN SupplierProducts sp ON p.ProductID = sp.ProductID " +
                       "WHERE sp.SupplierID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("ProductID");
                    String name = rs.getString("Name");
                    String category = rs.getString("Category");
                    double price = rs.getDouble("Price");
                    productsTableModel.addRow(new Object[]{productId, name, category, price});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewSupplierProducts(1).setVisible(true)); // Example with SupplierID = 1
    }
    
}


