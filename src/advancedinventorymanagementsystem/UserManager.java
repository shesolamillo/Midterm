package advancedinventorymanagementsystem;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.JOptionPane;

public class UserManager {

    // Validate login credentials (both Admin and Staff)
    public static boolean validateLogin(String username, String password, String role) {
        String query = "SELECT u.Username, u.Password, r.RoleName " +
                       "FROM Users u " +
                       "JOIN Roles r ON u.RoleID = r.RoleID " +
                       "WHERE u.Username = ? AND r.RoleName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters for the query
            stmt.setString(1, username);
            stmt.setString(2, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve stored password hash from the database
                    String storedPasswordHash = rs.getString("Password");

                    // Compare password with the hashed password from the database
                    if (BCrypt.checkpw(password, storedPasswordHash)) {
                        // Password matches
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while logging in.");
        }

        return false;
    }

    // Sign up method to create a new user (Admin/Staff)
    public static boolean signUp(String username, String password, String roleName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, get the RoleID based on the role name (Admin/Staff)
            String getRoleQuery = "SELECT RoleID FROM Roles WHERE RoleName = ?";
            PreparedStatement roleStmt = conn.prepareStatement(getRoleQuery);
            roleStmt.setString(1, roleName);
            ResultSet rs = roleStmt.executeQuery();

            if (rs.next()) {
                int roleID = rs.getInt("RoleID");

                // Check if the username already exists
                String checkUserQuery = "SELECT * FROM Users WHERE Username = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
                checkStmt.setString(1, username);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    // Username already exists
                    return false;
                }

                // Hash the password using BCrypt before saving
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                // Insert the new user into the Users table
                String insertUserQuery = "INSERT INTO Users (Username, Password, RoleID) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery);
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);  // Save hashed password
                insertStmt.setInt(3, roleID);

                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0; // If one row was affected, the sign-up was successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if any error occurred or role not found
    }
}



