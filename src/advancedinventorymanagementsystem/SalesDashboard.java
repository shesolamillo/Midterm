package advancedinventorymanagementsystem;

import javax.swing.*;

public class SalesDashboard extends JFrame {
    public SalesDashboard() {
        setTitle("Sales Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Add your UI components here
        JLabel label = new JLabel("Sales Dashboard Placeholder", SwingConstants.CENTER);
        add(label);
    }
}
