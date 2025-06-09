package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class LinkProductToSupplier extends JFrame {

    private JComboBox<String> productComboBox;
    private JComboBox<String> supplierComboBox;
    private JButton linkButton;

    private Map<String, Integer> productMap = new HashMap<>();
    private Map<String, Integer> supplierMap = new HashMap<>();

    public LinkProductToSupplier() {
        setTitle("Link Product to Supplier");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        productComboBox = new JComboBox<>();
        supplierComboBox = new JComboBox<>();
        linkButton = new JButton("Link");

        add(new JLabel("Select Product:"));
        add(productComboBox);
        add(new JLabel("Select Supplier:"));
        add(supplierComboBox);
        add(linkButton);

        loadProducts();
        loadSuppliers();

        linkButton.addActionListener(e -> linkProductToSupplier());

        setVisible(true);
    }

    private void loadProducts() {
        String query = "SELECT ProductID, Name FROM Products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("Name");
                productMap.put(name, id);
                productComboBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }

    private void loadSuppliers() {
        String query = "SELECT SupplierID, Name FROM Suppliers";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("SupplierID");
                String name = rs.getString("Name");
                supplierMap.put(name, id);
                supplierComboBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers.");
        }
    }

    private void linkProductToSupplier() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        String selectedSupplier = (String) supplierComboBox.getSelectedItem();

        if (selectedProduct == null || selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Please select both a product and a supplier.");
            return;
        }

        int productId = productMap.get(selectedProduct);
        int supplierId = supplierMap.get(selectedSupplier);

        // Check if the relationship already exists
        String checkQuery = "SELECT * FROM SupplierProducts WHERE ProductID = ? AND SupplierID = ?";
        String insertQuery = "INSERT INTO SupplierProducts (ProductID, SupplierID) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, productId);
            checkStmt.setInt(2, supplierId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "❌ This product is already linked to this supplier.");
            } else {
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, productId);
                insertStmt.setInt(2, supplierId);
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Product linked to supplier successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error linking product to supplier.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LinkProductToSupplier());
    }
}



