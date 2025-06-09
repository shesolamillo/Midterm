package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SupplierManagement extends JFrame {
    
    private JTable supplierTable;
    private DefaultTableModel supplierTableModel;
    private JTextField txtSupplierName, txtContactInfo;

    public SupplierManagement() {
        setTitle("Supplier Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout
        setLayout(new BorderLayout());

        // Supplier Form (for adding suppliers)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2));

        JLabel lblName = new JLabel("Supplier Name:");
        txtSupplierName = new JTextField();
        JLabel lblContact = new JLabel("Contact Info:");
        txtContactInfo = new JTextField();

        JButton btnAddSupplier = new JButton("Add Supplier");
        formPanel.add(lblName);
        formPanel.add(txtSupplierName);
        formPanel.add(lblContact);
        formPanel.add(txtContactInfo);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(btnAddSupplier);

        // Table to display suppliers
        String[] columnNames = {"Supplier ID", "Name", "Contact Info"};
        supplierTableModel = new DefaultTableModel(columnNames, 0);
        supplierTable = new JTable(supplierTableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);

        // Button to view which products a supplier provides
        JButton btnViewProducts = new JButton("View Products for Supplier");

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnViewProducts, BorderLayout.SOUTH);

        // Load suppliers when the screen opens
        loadSuppliers();

        // Add Supplier action
        btnAddSupplier.addActionListener(e -> {
            String name = txtSupplierName.getText();
            String contactInfo = txtContactInfo.getText();
            if (!name.isEmpty() && !contactInfo.isEmpty()) {
                addSupplier(name, contactInfo);
                loadSuppliers(); // Reload suppliers list
                JOptionPane.showMessageDialog(this, "Supplier added successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.");
            }
        });

        // View products for a supplier
        btnViewProducts.addActionListener(e -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow != -1) {
                int supplierId = (int) supplierTableModel.getValueAt(selectedRow, 0);
                new ViewSupplierProducts(supplierId).setVisible(true); // Open window to view products for the selected supplier
            } else {
                JOptionPane.showMessageDialog(this, "Please select a supplier first.");
            }
        });
        setVisible(true);
    }

    private void loadSuppliers() {
        // Clear the current data in the table
        supplierTableModel.setRowCount(0);

        // Fetch supplier data from the database
        String query = "SELECT * FROM Suppliers";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "12345");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int supplierId = rs.getInt("SupplierID");
                String name = rs.getString("Name");
                String contactInfo = rs.getString("ContactInfo");
                supplierTableModel.addRow(new Object[]{supplierId, name, contactInfo});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers.");
        }
    }

    private void addSupplier(String name, String contactInfo) {
        String query = "INSERT INTO Suppliers (Name, ContactInfo) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "12345");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding supplier.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupplierManagement().setVisible(true));
    }
}



