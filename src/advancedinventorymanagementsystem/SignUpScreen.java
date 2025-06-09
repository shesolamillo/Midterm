package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpScreen extends JFrame {

    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton cancelButton;

    public SignUpScreen() {
        setTitle("Sign Up");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Role:");
        roleComboBox = new JComboBox<>(new String[] {"Admin", "Staff"});
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        usernameTextField.setPreferredSize(new Dimension(200, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        registerButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setPreferredSize(new Dimension(100, 40));

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        add(usernameLabel);
        add(usernameTextField);
        add(passwordLabel);
        add(passwordField);
        add(roleLabel);
        add(roleComboBox);
        add(registerButton);
        add(cancelButton);

        registerButton.addActionListener(e -> {
            String username = usernameTextField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required.");
                return;
            }

            // Attempt to register the user via UserManager
            boolean success = UserManager.signUp(username, password, role);
            if (success) {
                JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.");
                dispose(); // Close sign-up screen
            } else {
                JOptionPane.showMessageDialog(null, "Username already exists or an error occurred.");
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }
}



