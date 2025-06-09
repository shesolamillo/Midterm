package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class CashierSignUpDialog extends JDialog {

    private JTextField txtCashierName;
    private JPasswordField txtPassword;
    private JTextField txtShiftName;
    private JButton btnRegister;

    public CashierSignUpDialog(JFrame parent) {
        super(parent, "Cashier Sign-Up", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);

        txtCashierName = new JTextField();
        txtPassword = new JPasswordField();
        txtShiftName = new JTextField();
        btnRegister = new JButton("Register");

        setLayout(new GridLayout(4, 2, 10, 10));
        add(new JLabel("Cashier Name:"));
        add(txtCashierName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Shift Name:"));
        add(txtShiftName);
        add(new JLabel());
        add(btnRegister);

        btnRegister.addActionListener(e -> attemptRegister());
    }

    private void attemptRegister() {
        String cashierName = txtCashierName.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String shift = txtShiftName.getText().trim();

        if (cashierName.isEmpty() || password.isEmpty() || shift.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO cashiers (CashierName, ShiftName, PasswordHash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cashierName);
            stmt.setString(2, shift);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Cashier registered successfully!");
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
