package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;



public class StaffDashboard extends JFrame {

    private List<Product> products = new ArrayList<>();
    private JTextArea receiptTextArea;
    private JPanel detailsPanel;
    private JLabel productLabel, quantityLabel, totalPriceLabel, paymentLabel, changeLabel;
    private JTextField quantityField, paymentField;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JLabel dateTimeLabel;


    

    public StaffDashboard() {
        setTitle("Staff Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the products list
      /*  products.add(new Product(1, "Shampoo", 5.00));
        products.add(new Product(2, "Toothpaste", 7.00));
        products.add(new Product(3, "Safeguard", 12.00));
        products.add(new Product(4, "Noodles", 15.00));
        products.add(new Product(5, "Sardines", 22.00));
        products.add(new Product(6, "Wings Powder", 7.00));*/
        products = loadProductsFromDatabase();


        // Main panel with "MENU" title
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        TitledBorder title = BorderFactory.createTitledBorder("MENU");
        title.setTitleFont(new Font("Arial", Font.BOLD, 20));
        title.setTitleJustification(TitledBorder.CENTER);
        mainPanel.setBorder(title);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        for (Product product : products) {
            JButton productButton = new JButton("Sell " + product.getName() + " - ₱" + String.format("%.2f", product.getPrice()));
            productButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            productButton.addActionListener(e -> sellProduct(product));
            buttonPanel.add(productButton);
        }

        // Checkout Button
        JButton checkoutButton = new JButton("🧾 Checkout");
        checkoutButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        checkoutButton.addActionListener(e -> processCheckout());
       // buttonPanel.add(checkoutButton);

        // Cash Payment Button
        JButton cashPaymentButton = new JButton("💵💰 Cash Payment");
        cashPaymentButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        cashPaymentButton.addActionListener(e -> handleCashPayment());
        //buttonPanel.add(cashPaymentButton);
        

        // Receipt Area
        receiptTextArea = new JTextArea(12, 40);
        receiptTextArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        receiptTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Receipt"));

        // Bottom Panel to hold the receipt area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // Details Panel for displaying product info dynamically
        /*detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
*/
        // Add components to the main panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
      //  mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

// Create new panel for buttons (Cash Payment + Logout)
        JPanel bottomButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomButtonsPanel.add(cashPaymentButton);
        

// Add the bottom buttons panel to the SOUTH of bottomPanel
        bottomPanel.add(bottomButtonsPanel, BorderLayout.SOUTH);

        // Add JTable for Cart
        String[] columnNames = {"Product", "Quantity", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(tableModel);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScrollPane = new JScrollPane(cartTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));

        //mainPanel.add(tableScrollPane, BorderLayout.WEST); // Add table to the left side of the main panel
        
        // Panel to hold cart table and total
        JPanel cartPanel = new JPanel(new BorderLayout());

        // Add cart table to cartPanel
        cartPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add total label at the bottom of the cart
        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        cartPanel.add(totalLabel, BorderLayout.SOUTH);

        // Add cart panel to the main panel
        mainPanel.add(cartPanel, BorderLayout.WEST);

        
        JButton cashButton = new JButton("Process Payment");
        cashButton.addActionListener(e -> handleCashPayment()); // Call the cash payment method
        
        
        // Logout Button
         // Logout Button
        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close StaffDashboard
                new LoginScreen().setVisible(true); // Reopen LoginScreen
            }
        });
       // buttonPanel.add(logoutButton);
       bottomButtonsPanel.add(logoutButton);
       
       JPanel timeAndButtonsPanel = new JPanel(new BorderLayout());
       // Time label
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        dateTimeLabel.setForeground(Color.RED);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Add both components to bottomPanel
        timeAndButtonsPanel.add(dateTimeLabel, BorderLayout.WEST);

        timeAndButtonsPanel.add(bottomButtonsPanel, BorderLayout.EAST);
        // Add bottomPanel to main frame
        bottomPanel.add(timeAndButtonsPanel, BorderLayout.SOUTH);

        // Timer to update label every second
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();





        // Add to frame
        add(mainPanel);
    }

    private void sellProduct(Product product) {
        // Check if the product exists in the database before proceeding
        String checkProductQuery = "SELECT ProductID FROM products WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkProductQuery)) {
            checkStmt.setInt(1, product.getProductID());
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                // If the product does not exist, show an error message
                JOptionPane.showMessageDialog(this, "Product with ID " + product.getProductID() + " does not exist in the database.");
                return; // Exit if the product doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return;
        }

        // Proceed with selling the product if it exists
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity for " + product.getName() + ":");
        if (qtyStr == null || !qtyStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a whole number.");
            return;
        }

        int qty = Integer.parseInt(qtyStr);
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
            return;
        }

        // Add the product to the cart table
        double total = qty * product.getPrice();
        tableModel.addRow(new Object[]{product.getName(), qty, product.getPrice(), String.format("%.2f", total)});
        updateCartTotal();


        JOptionPane.showMessageDialog(this, "✅ " + qty + " x " + product.getName() + " added to cart.");
    }

    // Checkout method
    private void processCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        double total = 0;
        StringBuilder receipt = new StringBuilder("=== RECEIPT ===\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = (String) tableModel.getValueAt(i, 0);
            int quantity = (int) tableModel.getValueAt(i, 1);
            double unitPrice = (double) tableModel.getValueAt(i, 2);
            double subtotal = quantity * unitPrice;
            receipt.append(quantity)
                    .append(" x ")
                    .append(productName)
                    .append(" @ ₱")
                    .append(String.format("%.2f", unitPrice))
                    .append(" = ₱")
                    .append(String.format("%.2f", subtotal))
                    .append("\n");
            total += subtotal;
        }

        receipt.append("---------------\nTotal: ₱").append(String.format("%.2f", total)).append("\n");

        // Ask for payment
        String payStr = JOptionPane.showInputDialog(this, "Total: ₱" + total + "\nEnter payment:");
        if (payStr == null) return;

        try {
            double payment = Double.parseDouble(payStr);
            if (payment < total) {
                JOptionPane.showMessageDialog(this, "❌ Insufficient payment.");
                return;
            }
            double change = payment - total;
            receipt.append("Payment: ₱").append(String.format("%.2f", payment)).append("\n");
            receipt.append("Change: ₱").append(String.format("%.2f", change)).append("\n");
            receipt.append("Date: ").append(java.time.LocalDate.now()).append("\n");
            receipt.append("======================\n\n");

            receiptTextArea.append(receipt.toString());

            // Clear the table after checkout
            tableModel.setRowCount(0); // Clears the table
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid payment.");
        }
    }

    private void handleCashPayment() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        double total = 0;
        StringBuilder receipt = new StringBuilder();
        String date = java.time.LocalDate.now().toString(); // Current date in yyyy-MM-dd format

        // Append company name at the top
        receipt.append("=== Sheilas Store ===\n");
        receipt.append("-----------------------------\n");

        // Append the current date
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("-----------------------------\n");

        // Append product details in column format
        receipt.append(String.format("%-25s%-10s%-10s%-10s\n", "Product", "Price", "Qty", "Amount"));
        receipt.append("-----------------------------\n");

        // Iterate through all products in the cart to calculate the total and generate receipt details
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = (String) tableModel.getValueAt(i, 0);
            int quantity = (int) tableModel.getValueAt(i, 1);

            // Get the price from the table and safely convert to Double
            Object priceObj = tableModel.getValueAt(i, 2);
            double unitPrice = 0.0;

            if (priceObj instanceof String) {
                try {
                    unitPrice = Double.parseDouble((String) priceObj); // Parse String to double
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid price format in the table.");
                    return;
                }
            } else if (priceObj instanceof Double) {
                unitPrice = (double) priceObj;
            }

            double subtotal = quantity * unitPrice;
            total += subtotal;
            

            // Append each product's details in column format
            receipt.append(String.format("%-25s%-10s%-10s%-10s\n", productName, "₱" + String.format("%.2f", unitPrice), quantity, "₱" + String.format("%.2f", subtotal)));
        }

        // Ask for cash payment input
        String cashStr = JOptionPane.showInputDialog(this, "Total: ₱" + String.format("%.2f", total) + "\nEnter Cash Payment:");
        if (cashStr == null) return;

        try {
            double cash = Double.parseDouble(cashStr);
            if (cash < total) {
                JOptionPane.showMessageDialog(this, "❌ Insufficient cash.");
                return;
            }
            double change = cash - total;
            
            if (cash < 0) {
                JOptionPane.showMessageDialog(this, "❌ Negative cash amount is not allowed.");
                return;
            }


            // Append the total, payment, and change to the receipt
            receipt.append("-----------------------------\n");
            receipt.append(String.format("%-25s%-10s\n", "Total", "₱" + String.format("%.2f", total)));
            receipt.append(String.format("%-25s%-10s\n", "Payment", "₱" + String.format("%.2f", cash)));
            receipt.append(String.format("%-25s%-10s\n", "Change", "₱" + String.format("%.2f", change)));
            receipt.append("============================= \n");

            // Display the receipt in the receiptTextArea
            receiptTextArea.append(receipt.toString());

            // Clear the table after payment
            tableModel.setRowCount(0);  // Clear the cart

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid payment amount.");
        }
    }


    // Method to retrieve the product ID from the product name
    private int getProductIdByName(String productName) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(productName)) {
                return product.getProductID();
            }
        }
        return -1; // Return -1 if not found
    }
    
    private void updateCartTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object totalObj = tableModel.getValueAt(i, 3);
            try {
                if (totalObj instanceof String) {
                    total += Double.parseDouble((String) totalObj);
                } else if (totalObj instanceof Double) {
                    total += (double) totalObj;
                }
            } catch (NumberFormatException e) {
                // Ignore and continue
            }
        }
        totalLabel.setText("Total: ₱" + String.format("%.2f", total));
    }
    
    private List<Product> loadProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT ProductID, Name, Price FROM Products"; // Modify this if you have additional columns

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String productName = rs.getString("Name");
                double productPrice = rs.getDouble("Price");
                products.add(new Product(productId, productName, productPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products from database.");
        }
        return products;
    }
    
    
    public StaffDashboard(String cashierName, String shift) {
        this(); // Call default constructor
        setTitle("Staff Dashboard - " + cashierName + " (" + shift + " Shift)");
        // You can also store cashierName and shift as instance variables if needed
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm:ss a");
        dateTimeLabel.setText("📅 " + now.format(formatter));
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffDashboard().setVisible(true));
    }
}
