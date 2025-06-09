package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryViewer extends JFrame {

    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;

    public InventoryViewer() {
        setTitle("Inventory Viewer");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout setup
        setLayout(new BorderLayout());

        // Table to display product and quantity data
        String[] columnNames = {"Product ID", "Product Name", "Remaining Quantity"};
        inventoryTableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(inventoryTableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        add(scrollPane, BorderLayout.CENTER);

        // Load inventory data when the window is opened
        loadInventoryData();
    }

    private void loadInventoryData() {
        // Clear the current data in the table
        inventoryTableModel.setRowCount(0);

        // SQL query to retrieve inventory data
        String query = "SELECT " +
               "p.ProductID, " +
               "p.Name AS ProductName, " +
               "COALESCE(SUM(s.QuantityAdded), 0) - COALESCE(SUM(sa.QuantitySold), 0) AS RemainingQuantity " +
               "FROM Products p " +
               "LEFT JOIN Stock s ON p.ProductID = s.ProductID " +
               "LEFT JOIN Sales sa ON p.ProductID = sa.ProductID " +
               "GROUP BY p.ProductID, p.Name";


        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "12345");
             
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Process each row from the result set
            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String productName = rs.getString("ProductName");
                int remainingQuantity = rs.getInt("RemainingQuantity");

                // Add the inventory data to the table
                inventoryTableModel.addRow(new Object[]{productId, productName, remainingQuantity});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading inventory data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryViewer().setVisible(true));
    }
}



