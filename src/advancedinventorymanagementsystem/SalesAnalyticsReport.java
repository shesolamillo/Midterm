package advancedinventorymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SalesAnalyticsReport extends JFrame {

    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JTextField txtFromDate, txtToDate;

    public SalesAnalyticsReport() {
        setTitle("Sales and Supplier Analytics Report");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout setup
        setLayout(new BorderLayout());

        // Filter Panel (Date range filter)
        JPanel filterPanel = new JPanel();
        JLabel lblFrom = new JLabel("From:");
        JLabel lblTo = new JLabel("To:");
        txtFromDate = new JTextField("2025-01-01", 10);  // Default to this date (yyyy-MM-dd format)
        txtToDate = new JTextField("2025-12-31", 10);    // Default to this date
        JButton btnFilter = new JButton("Filter");

        // Add components to filter panel
        filterPanel.add(lblFrom);
        filterPanel.add(txtFromDate);
        filterPanel.add(lblTo);
        filterPanel.add(txtToDate);
        filterPanel.add(btnFilter);

        add(filterPanel, BorderLayout.NORTH);  // Add filter panel at the top

        // Table to display sales and supplier data
        String[] columnNames = {"Product ID", "Product Name", "Total Revenue", "Supplier ID", "Supplier Name"};
        reportTableModel = new DefaultTableModel(columnNames, 0);
        reportTable = new JTable(reportTableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load report data when the window is opened
        loadReportData("2000-01-01", "2099-12-31"); // Load full data initially

        // Action Listener for the filter button
        btnFilter.addActionListener(e -> {
            String fromDate = txtFromDate.getText().trim();
            String toDate = txtToDate.getText().trim();
            loadReportData(fromDate, toDate);  // Reload the report based on the date range
        });
    }

    private void loadReportData(String fromDate, String toDate) {
        // Clear the current data in the table
        reportTableModel.setRowCount(0);

        // SQL query to retrieve sales and supplier analytics data
        String query = "SELECT p.ProductID, p.Name AS ProductName, SUM(s.TotalAmount) AS TotalRevenue, " +
                "sp.SupplierID, su.Name AS SupplierName " +
                "FROM Sales s " +
                "JOIN Products p ON s.ProductID = p.ProductID " +
                "JOIN SupplierProducts sp ON p.ProductID = sp.ProductID " +
                "JOIN Suppliers su ON sp.SupplierID = su.SupplierID " +
                "WHERE s.SaleDate BETWEEN ? AND ? " +
                "GROUP BY p.ProductID, su.SupplierID " +
                "ORDER BY TotalRevenue DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "12345");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the fromDate and toDate values in the prepared statement
            stmt.setString(1, fromDate);
            stmt.setString(2, toDate);

            // Execute the query and process the result set
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("ProductID");
                    String productName = rs.getString("ProductName");
                    double totalRevenue = rs.getDouble("TotalRevenue");
                    int supplierId = rs.getInt("SupplierID");
                    String supplierName = rs.getString("SupplierName");

                    // Add the report data to the table
                    reportTableModel.addRow(new Object[]{productId, productName, totalRevenue, supplierId, supplierName});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sales and supplier report.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesAnalyticsReport().setVisible(true));
    }
}



