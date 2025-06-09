package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CashierLoginDialog extends JDialog {

    private JTextField txtCashierName;
    private JTextField txtShiftName;  // Field to enter a custom shift name
    private JButton btnLogin;
    private String cashierName;
    private String shift;

    public CashierLoginDialog(JFrame parent) {
        super(parent, "Cashier Login", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Initialize components
        txtCashierName = new JTextField();
        txtShiftName = new JTextField();  // For custom shift name
        btnLogin = new JButton("Login");

        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("Cashier Name:"));
        add(txtCashierName);
        add(new JLabel("Shift Name:"));
        add(txtShiftName);  // Display input for shift name
        add(new JLabel()); // Empty label for alignment
        add(btnLogin);

        // Action when Login button is clicked
        btnLogin.addActionListener(e -> {
            cashierName = txtCashierName.getText().trim();
            shift = txtShiftName.getText().trim();  // Get custom shift name

            if (!cashierName.isEmpty() && !shift.isEmpty()) {
                dispose();  // Close the dialog
                // Open Sales Management with the cashier name and shift
                new SalesManagement(cashierName, shift).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid Cashier name and Shift name.");
            }
        });
    }

    // Getter for cashier name and shift after login
    public String getCashierName() {
        return cashierName;
    }

    public String getShift() {
        return shift;
    }
}
