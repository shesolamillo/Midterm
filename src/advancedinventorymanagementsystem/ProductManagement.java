package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProductManagement extends JFrame {

    private JTextField txtProductName, txtCategory, txtPrice;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private int selectedProductId = -1; // To store the selected product ID for updating/deleting

    public ProductManagement() {
        setTitle("Product Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Components for adding a new product
        JLabel lblProductName = new JLabel("Product Name:");
        JLabel lblCategory = new JLabel("Category:");
        JLabel lblPrice = new JLabel("Price:");

        txtProductName = new JTextField(20);
        txtCategory = new JTextField(20);
        txtPrice = new JTextField(20);
        JButton btnAddProduct = new JButton("Add Product");
        JButton btnModifyProduct = new JButton("Modify Product");
        JButton btnDeleteProduct = new JButton("Delete Product");
        JButton btnUpateProduct = new JButton ("Update Product");

        // Panel for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2));
        formPanel.add(lblProductName);
        formPanel.add(txtProductName);
        formPanel.add(lblCategory);
        formPanel.add(txtCategory);
        formPanel.add(lblPrice);
        formPanel.add(txtPrice);
        formPanel.add(new JLabel()); // Empty space for alignment
        formPanel.add(btnAddProduct);
        formPanel.add(btnModifyProduct);
        formPanel.add(btnDeleteProduct);
       // formPanel.add(btnUpdateProduct);

        // Table to display products
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(productTableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);

        // Main Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load existing products from the database
        loadProducts();
        

        // Action for adding a product
        btnAddProduct.addActionListener(e -> {
            String name = txtProductName.getText();
            String category = txtCategory.getText();
            double price;

            try {
                price = Double.parseDouble(txtPrice.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid price.");
                return;
            }

            // SQL insert logic for adding a product
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO Products (Name, Category, Price) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setDouble(3, price);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "✅ Product added successfully!");
                    loadProducts(); // Reload the product list
                    txtProductName.setText("");
                    txtCategory.setText("");
                    txtPrice.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Failed to add product.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });

        // Action for modifying a product
        btnModifyProduct.addActionListener(e -> {
            if (selectedProductId != -1) {
                String name = txtProductName.getText();
                String category = txtCategory.getText();
                double price;

                try {
                    price = Double.parseDouble(txtPrice.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid price.");
                    return;
                }

                // SQL update logic for modifying a product
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "UPDATE Products SET Name = ?, Category = ?, Price = ? WHERE ProductID = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, category);
                    stmt.setDouble(3, price);
                    stmt.setInt(4, selectedProductId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "✅ Product updated successfully!");
                        loadProducts(); // Reload the product list
                        txtProductName.setText("");
                        txtCategory.setText("");
                        txtPrice.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "❌ Failed to update product.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a product to modify.");
            }
        });

        // Action for deleting a product
        btnDeleteProduct.addActionListener(e -> {
            if (selectedProductId != -1) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "DELETE FROM Products WHERE ProductID = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, selectedProductId);

                        int rowsDeleted = stmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(null, "✅ Product deleted successfully!");
                            loadProducts(); // Reload the product list
                            txtProductName.setText("");
                            txtCategory.setText("");
                            txtPrice.setText("");
                            selectedProductId = -1; // Reset selected product ID
                        } else {
                            JOptionPane.showMessageDialog(null, "❌ Failed to delete product.");
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a product to delete.");
            }
        });

        // Table selection listener to load product data into the form
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedProductId = (int) productTableModel.getValueAt(selectedRow, 0);
                String productName = (String) productTableModel.getValueAt(selectedRow, 1);
                String category = (String) productTableModel.getValueAt(selectedRow, 2);
                double price = (double) productTableModel.getValueAt(selectedRow, 3);

                txtProductName.setText(productName);
                txtCategory.setText(category);
                txtPrice.setText(String.valueOf(price));
            }
        });

        setVisible(true);
    }

    // Method to load the list of products from the database
    private void loadProducts() {
        // Clear current data in the table
        productTableModel.setRowCount(0);

        // Fetch product data from the database
        String query = "SELECT * FROM Products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Process each row and add it to the table
            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String name = rs.getString("Name");
                String category = rs.getString("Category");
                double price = rs.getDouble("Price");

                productTableModel.addRow(new Object[]{productId, name, category, price});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductManagement().setVisible(true));
    }
}



