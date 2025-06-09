package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SalesManagement extends JFrame {

    private String cashierName;
    private String shift;

    // Other UI components like product selection, quantity, etc.
    private JComboBox<String> productComboBox;
    private JTextField txtQuantitySold;
    private JTextField txtPayment;
    private JButton btnRecordSale;

    private Map<String, Integer> productMap = new HashMap<>();
    private Map<Integer, Double> productPriceMap = new HashMap<>();

    public SalesManagement(String cashierName, String shift) {
        this.cashierName = cashierName;
        this.shift = shift;
        
        setTitle("Sales Management");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblProduct = new JLabel("Product:");
        JLabel lblQuantity = new JLabel("Quantity Sold:");
        JLabel lblPayment = new JLabel("Payment:");

        productComboBox = new JComboBox<>();
        txtQuantitySold = new JTextField();
        btnRecordSale = new JButton("Record Sale");
        txtPayment = new JTextField();

        add(lblProduct); add(productComboBox);
        add(lblQuantity); add(txtQuantitySold);
        add(lblPayment); add(txtPayment);
        add(new JLabel()); add(btnRecordSale);

        loadProducts();

        btnRecordSale.addActionListener(e -> recordSale());

        setVisible(true);
    }

    private void loadProducts() {
        String query = "SELECT ProductID, Name, Price FROM Products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");

                productMap.put(name, id);
                productPriceMap.put(id, price);
                productComboBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }

    private void recordSale() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        String quantityStr = txtQuantitySold.getText().trim();
        String paymentStr = txtPayment.getText().trim();

        if (selectedProduct == null || quantityStr.isEmpty() || paymentStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields.");
            return;
        }

        int quantity;
        double payment;
        try {
            quantity = Integer.parseInt(quantityStr);
            payment = Double.parseDouble(paymentStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and payment must be valid numbers.");
            return;
        }

        int productId = productMap.get(selectedProduct);
        double unitPrice = productPriceMap.get(productId);
        double totalAmount = unitPrice * quantity;

        if (payment < totalAmount) {
            JOptionPane.showMessageDialog(this, "❌ Payment is less than total amount.");
            return;
        }
        double change = payment - totalAmount;
        LocalDate saleDate = LocalDate.now();

        // Log the sale with cashier and shift details
        String saleQuery = "INSERT INTO Sales (ProductID, QuantitySold, SaleDate, TotalAmount, Payment, `Change`, CashierName, Shift) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement saleStmt = conn.prepareStatement(saleQuery)) {

            saleStmt.setInt(1, productId);
            saleStmt.setInt(2, quantity);
            saleStmt.setDate(3, Date.valueOf(saleDate));
            saleStmt.setDouble(4, totalAmount);
            saleStmt.setDouble(5, payment);
            saleStmt.setDouble(6, change);
            saleStmt.setString(7, cashierName);
            saleStmt.setString(8, shift);
            saleStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Sale recorded! Change: ₱" + change);
            txtQuantitySold.setText("");
            txtPayment.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
