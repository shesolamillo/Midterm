package advancedinventorymanagementsystem;

import javax.swing.SwingUtilities;

public class AdvancedInventoryManagementSystem {

    public static void main(String[] args) {
        // Start the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Show the Login screen (Staff or Admin login)
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            }
        });
    }
}

