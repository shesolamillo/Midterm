package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import advancedinventorymanagementsystem.ProductManagement;
import advancedinventorymanagementsystem.SupplierManagement;
import advancedinventorymanagementsystem.SalesAnalyticsReport;


public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Admin Dashboard options
        JButton btnManageProducts = new JButton("Manage Products");
        JButton btnManageSuppliers = new JButton("Manage Suppliers");
        JButton btnViewSales = new JButton("View Sales");
        JButton btnViewSalesHistory = new JButton("Sales History"); // Added Sales History button
        JButton btnLinkProductSupplier = new JButton("Link Product to Supplier");
        JButton btnStockManagement = new JButton("Stock Management");
        JButton btnSalesManagement = new JButton("Sales Management");
        JButton btnViewInventory = new JButton("View Inventory");
        JButton btnCashierManagement = new JButton("Cashier Management");
        // ➕ Logout / Back Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        
        

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(btnManageProducts);
        panel.add(btnManageSuppliers);
        panel.add(btnViewSales);
        panel.add(btnViewSalesHistory);  // Added this line to display Sales History button
        panel.add(btnLinkProductSupplier);
        panel.add(btnStockManagement);
        panel.add(btnSalesManagement);
        panel.add(btnViewInventory); // Add the new button here
        panel.add(btnCashierManagement);
        
        // Create a new panel for the logout button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);
        

        add(panel);
        add(bottomPanel, BorderLayout.SOUTH);
        

        // Button actions
        btnManageProducts.addActionListener(e -> {
            new ProductManagement().setVisible(true);
        });

        btnManageSuppliers.addActionListener(e -> {
            new SupplierManagement().setVisible(true);
        });

        btnViewSales.addActionListener(e -> {
            new SalesAnalyticsReport().setVisible(true);
        });
        btnLinkProductSupplier.addActionListener(e -> new LinkProductToSupplier().setVisible(true));
        
        btnStockManagement.addActionListener(e -> new StockManagement().setVisible(true));
        
        btnSalesManagement.addActionListener(e -> {
            new SalesManagementScreen().setVisible(true);  // Open staff login + shift selection screen
        });


        
        // Action for View Inventory button
        btnViewInventory.addActionListener(e -> {
            new InventoryViewer().setVisible(true);  // Opens the InventoryViewer window
        });
        
        
        // Add action for logout
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close AdminDashboard
                new LoginScreen().setVisible(true); // Open LoginScreen
            }
        });
        
        
        btnCashierManagement.addActionListener(e -> new CashierManagement().setVisible(true));
        
        btnViewSalesHistory.addActionListener(e -> {
            try {
                new SalesHistory().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to open Sales History: " + ex.getMessage());
            }
        });




        
        
    }
}


