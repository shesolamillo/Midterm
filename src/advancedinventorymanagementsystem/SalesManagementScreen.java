

package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class SalesManagementScreen extends JFrame {

    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JComboBox<String> shiftComboBox;
    private JButton loginButton;

    public SalesManagementScreen() {
        setTitle("Sales Management - Staff Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Form panel for Sales Management
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel shiftLabel = new JLabel("Select Shift:");
        
        // Shift options (Morning, Afternoon, Evening)
        shiftComboBox = new JComboBox<>(new String[] {"Morning", "Afternoon", "Evening"});
        
        loginButton = new JButton("Login");

        // Sizes
        usernameTextField.setPreferredSize(new Dimension(100, 20));
        passwordField.setPreferredSize(new Dimension(100, 20));
        shiftComboBox.setPreferredSize(new Dimension(100, 20));
        loginButton.setPreferredSize(new Dimension(80, 20));

        formPanel.add(usernameLabel);
        formPanel.add(usernameTextField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(shiftLabel);
        formPanel.add(shiftComboBox);
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String username = usernameTextField.getText().trim();
            String password = new String(passwordField.getPassword());
            String shift = (String) shiftComboBox.getSelectedItem();

            // Handle login validation and shift assignment
            if (validateStaffLogin(username, password, shift)) {
                JOptionPane.showMessageDialog(this, "Login Successful as Cashier for " + shift + " shift.");
                dispose();
                new StaffDashboard(username, shift).setVisible(true); // Pass info to dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or shift mismatch.");
            }
        });
    }

    private boolean validateStaffLogin(String cashierName, String password, String shift) {
        String query = "SELECT PasswordHash FROM cashiers WHERE CashierName = ? AND ShiftName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cashierName);
            stmt.setString(2, shift);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    return org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHash);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating cashier login.");
        }

        return false;
    }
    
    
    
    public class CashierUpdateDialog extends JDialog {

    private JTextField txtNewName;
    private JPasswordField txtNewPassword;
    private JButton btnUpdate;

    public CashierUpdateDialog(JFrame parent, String cashierName) {
        super(parent, "Update Cashier Information", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);

        txtNewName = new JTextField();
        txtNewPassword = new JPasswordField();
        btnUpdate = new JButton("Update");

        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("New Cashier Name:"));
        add(txtNewName);
        add(new JLabel("New Password:"));
        add(txtNewPassword);
        add(new JLabel());
        add(btnUpdate);

        btnUpdate.addActionListener(e -> attemptUpdate(cashierName));
    }

    private void attemptUpdate(String cashierName) {
        String newName = txtNewName.getText().trim();
        String newPassword = new String(txtNewPassword.getPassword()).trim();

        if (newName.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            String sql = "UPDATE cashiers SET CashierName = ?, PasswordHash = ? WHERE CashierName = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newName);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, cashierName);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Cashier information updated!");
                dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesManagementScreen().setVisible(true));
    }
    
    
    
}

