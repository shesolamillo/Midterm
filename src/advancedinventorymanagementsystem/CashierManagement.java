package advancedinventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CashierManagement extends JFrame {

    private JTextField txtCashierName;
    private JPasswordField txtPassword;
    private JComboBox<String> shiftComboBox;
    private JButton btnAdd, btnUpdate, btnDelete;

    public CashierManagement() {
        setTitle("Cashier Management");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        txtCashierName = new JTextField();
        txtPassword = new JPasswordField();
        shiftComboBox = new JComboBox<>(new String[] {"Morning", "Afternoon", "Evening"});

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        add(new JLabel("Cashier Name:"));
        add(txtCashierName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Shift:"));
        add(shiftComboBox);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);

        btnAdd.addActionListener(e -> addCashier());
        btnUpdate.addActionListener(e -> updateCashier());
        btnDelete.addActionListener(e -> deleteCashier());
    }

    private void addCashier() {
        String name = txtCashierName.getText().trim();
        String password = new String(txtPassword.getPassword());
        String shift = (String) shiftComboBox.getSelectedItem();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
            return;
        }

        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO cashiers (CashierName, ShiftName, PasswordHash) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, shift);
            stmt.setString(3, hashed);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Cashier added!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void updateCashier() {
        String name = txtCashierName.getText().trim();
        String password = new String(txtPassword.getPassword());
        String shift = (String) shiftComboBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter cashier name to update.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE cashiers SET ShiftName = ?, PasswordHash = ? WHERE CashierName = ?")) {

            String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
            stmt.setString(1, shift);
            stmt.setString(2, hashed);
            stmt.setString(3, name);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Cashier updated!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Cashier not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void deleteCashier() {
        String name = txtCashierName.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter cashier name to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cashiers WHERE CashierName = ?")) {
            stmt.setString(1, name);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "🗑️ Cashier deleted!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Cashier not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }
}
