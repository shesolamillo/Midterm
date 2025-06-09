package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class SalesHistory extends JFrame {

    private JTable salesTable;
    private JScrollPane scrollPane;

    public SalesHistory() {
        setTitle("Sales History");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Setup table and scroll pane
        salesTable = new JTable();
        scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch and populate the sales data
        populateSalesData();
    }

    private void populateSalesData() {
        // Your SQL query to fetch sales data
        String query = "SELECT s.SaleID, s.SaleDate, s.SaleTime, sh.Status AS Shift, c.CashierName, p.Name AS Product, " +
                       "sd.QuantitySold, s.TotalAmount, sd.Change FROM sales s " +
                       "JOIN salesdetails sd ON s.SaleID = sd.SaleID " +
                       "JOIN shifts sh ON s.ShiftID = sh.ShiftID " +
                       "JOIN cashiers c ON s.CashierID = c.CashierID " +
                       "JOIN products p ON sd.ProductID = p.ProductID ORDER BY s.SaleDate DESC";

        // Database connection (update with your actual DB details)
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "12345");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Create a table model to hold the sales data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("SaleID");
            model.addColumn("SaleDate");
            model.addColumn("SaleTime");
            model.addColumn("Shift");
            model.addColumn("Cashier");
            model.addColumn("Product");
            model.addColumn("Quantity Sold");
            model.addColumn("Total Amount");
            model.addColumn("Change");

            // Populate table rows with sales data
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getInt("SaleID");
                row[1] = rs.getDate("SaleDate");
                row[2] = rs.getTime("SaleTime");
                row[3] = rs.getString("Shift");
                row[4] = rs.getString("CashierName");
                row[5] = rs.getString("Product");
                row[6] = rs.getInt("QuantitySold");
                row[7] = rs.getDouble("TotalAmount");
                row[8] = rs.getDouble("Change");
                model.addRow(row);
            }

            // Set the model for the JTable
            salesTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching sales data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SalesHistory().setVisible(true);
            }
        });
    }
}
