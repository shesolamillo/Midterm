package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class StaffLoginPrompt extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> shiftComboBox;
    private JButton btnLogin;

    public StaffLoginPrompt(JFrame parent) {
        super(parent, "Staff Login", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel("Shift:"));
        shiftComboBox = new JComboBox<>(new String[]{"Morning", "Afternoon", "Evening"});
        add(shiftComboBox);

        btnLogin = new JButton("Login");
        add(new JLabel()); // spacer
        add(btnLogin);

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String shift = (String) shiftComboBox.getSelectedItem();

            if (validateStaff(username, password)) {
                dispose(); // close the login popup
                new SalesManagement(username, shift).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid staff credentials.");
            }
        });

        setVisible(true);
    }

    private boolean validateStaff(String username, String password) {
        String query = "SELECT u.Password FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.Username = ? AND r.RoleName = 'Staff'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("Password");
                return BCrypt.checkpw(password, hash);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
