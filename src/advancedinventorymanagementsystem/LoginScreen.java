package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginScreen extends JFrame {

    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton signUpButton;

    public LoginScreen() {
        setTitle("Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========= HEADER PANEL ========= //
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel welcomeLabel1 = new JLabel("Welcome to Sari-sari Store", SwingConstants.CENTER);
        welcomeLabel1.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel1.setForeground(Color.BLUE);
        welcomeLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel2 = new JLabel("Advanced Inventory Management System", SwingConstants.CENTER);
        welcomeLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalStrut(15)); // Space above
        headerPanel.add(welcomeLabel1);
        headerPanel.add(welcomeLabel2);
        headerPanel.add(Box.createVerticalStrut(10)); // Space below

        // ========= FORM PANEL ========= //
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Select Role:");
        
        // Initially, only Admin role should be visible
        roleComboBox = new JComboBox<>(new String[] {"Admin"});
        
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        cancelButton = new JButton("Cancel");

        // Sizes
        usernameTextField.setPreferredSize(new Dimension(100, 20));
        passwordField.setPreferredSize(new Dimension(100, 20));
        roleComboBox.setPreferredSize(new Dimension(100, 20));
        loginButton.setPreferredSize(new Dimension(80, 20));
        cancelButton.setPreferredSize(new Dimension(80, 20));
        signUpButton.setPreferredSize(new Dimension(80, 20));

        formPanel.add(usernameLabel);
        formPanel.add(usernameTextField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);
        formPanel.add(loginButton);
        formPanel.add(signUpButton);
        formPanel.add(cancelButton);

        // Add panels to main frame
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // ========= ACTIONS ========= //
        loginButton.addActionListener(e -> {
            String username = usernameTextField.getText().trim();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (validateLogin(username, password, selectedRole)) {
                JOptionPane.showMessageDialog(null, "Login Successful as " + selectedRole + "!");
                if ("Admin".equalsIgnoreCase(selectedRole)) {
                    new AdminDashboard().setVisible(true);  // Show Admin Dashboard
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials or role mismatch.");
            }
        });

        cancelButton.addActionListener(e -> dispose());
        signUpButton.addActionListener(e -> new SignUpScreen().setVisible(true));
    }

    private boolean validateLogin(String username, String password, String role) {
        String query = "SELECT u.Username, u.Password, r.RoleName " +
                       "FROM Users u " +
                       "JOIN Roles r ON u.RoleID = r.RoleID " +
                       "WHERE u.Username = ? AND r.RoleName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("Password");
                    return BCrypt.checkpw(password, storedPasswordHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while logging in.");
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
