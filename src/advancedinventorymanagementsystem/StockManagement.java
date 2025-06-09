package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StockManagement extends JFrame {

    private JComboBox<String> productComboBox;
    private JComboBox<String> supplierComboBox;
    private JTextField txtQuantity;
    private JButton btnAddStock;

    private Map<String, Integer> productMap = new HashMap<>();
    private Map<String, Integer> supplierMap = new HashMap<>();

    public StockManagement() {
        setTitle("Stock Management");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        JLabel lblProduct = new JLabel("Product:");
        JLabel lblSupplier = new JLabel("Supplier:");
        JLabel lblQuantity = new JLabel("Quantity:");

        productComboBox = new JComboBox<>();
        supplierComboBox = new JComboBox<>();
        txtQuantity = new JTextField();
        btnAddStock = new JButton("Add Stock");

        add(lblProduct); add(productComboBox);
        add(lblSupplier); add(supplierComboBox);
        add(lblQuantity); add(txtQuantity);
        add(new JLabel()); add(btnAddStock);

        loadProducts();
        loadSuppliers();

        btnAddStock.addActionListener(e -> addStock());

        setVisible(true);
    }

    private void loadProducts() {
        String query = "SELECT ProductID, Name FROM Products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    private void addStock() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        String selectedSupplier = (String) supplierComboBox.getSelectedItem();
        String quantityStr = txtQuantity.getText().trim();

        if (selectedProduct == null || selectedSupplier == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
            return;
        }

        int productId = productMap.get(selectedProduct);
        int supplierId = supplierMap.get(selectedSupplier);
        LocalDate dateAdded = LocalDate.now();

        String query = "INSERT INTO Stock (ProductID, SupplierID, QuantityAdded, DateAdded) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, supplierId);
            stmt.setInt(3, quantity);
            stmt.setDate(4, Date.valueOf(dateAdded));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "✅ Stock added successfully!");
                txtQuantity.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add stock.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockManagement());
    }
}



