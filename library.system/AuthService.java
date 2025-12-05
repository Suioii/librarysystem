package librarysystem;

import java.sql.*;
import java.util.regex.Pattern;

public class AuthService {
    
    public static User authenticate(String email, String password) {
        String sql = "SELECT * FROM members WHERE email = ? AND is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String userName = rs.getString("name");
                
                System.out.println("🔐 Authentication Debug:");
                System.out.println("  User: " + userName + " (" + email + ")");
                System.out.println("  Input password: " + password);
                System.out.println("  Stored hash: " + storedHash);
                System.out.println("  Hash length: " + (storedHash != null ? storedHash.length() : "null"));
                
                // Use proper password verification
                if (PasswordUtils.checkPassword(password, storedHash)) {
                    System.out.println("  ✅ Password verification: SUCCESS");
                    return new User(
                        rs.getInt("member_id"),
                        userName,
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("is_active")
                    );
                } else {
                    System.out.println("  ❌ Password verification: FAILED");
                }
            } else {
                System.out.println("  ❌ User not found or inactive: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null; // Authentication failed
    }
    
    public static boolean registerUser(String name, String email, String password, String role) {
        // Validate inputs
        if (!isValidEmail(email)) {
            System.err.println("Invalid email format");
            return false;
        }
        
        if (!PasswordUtils.isPasswordStrong(password)) {
            System.err.println("Password does not meet requirements");
            return false;
        }
        
        // Check if email already exists
        if (emailExists(email)) {
            System.err.println("Email already registered");
            return false;
        }
        
        String sql = "INSERT INTO members (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Hash the password before storing
            String hashedPassword = PasswordUtils.hashPassword(password);
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword); // Store the hashed password
            pstmt.setString(4, "MEMBER");
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM members WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
    
    private static boolean isValidPassword(String password) {
        // At least 8 characters, one uppercase, one number, one special character
        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }
    
 // Verify the current password for a user

public static boolean verifyCurrentPassword(String email, String currentPassword) {
    String sql = "SELECT password_hash FROM members WHERE email = ? AND is_active = true";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String storedHash = rs.getString("password_hash");
            return PasswordUtils.checkPassword(currentPassword, storedHash);
        }
    } catch (SQLException e) {
        System.err.println("Error verifying current password: " + e.getMessage());
    }
    return false;
}

 //Change user password
 
public static boolean changePassword(String email, String newPassword) {
    // Validate new password strength
    if (!PasswordUtils.isPasswordStrong(newPassword)) {
        System.err.println("New password does not meet strength requirements");
        return false;
    }
    
    String sql = "UPDATE members SET password_hash = ? WHERE email = ? AND is_active = true";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        // Hash the new password
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        
        pstmt.setString(1, hashedPassword);
        pstmt.setString(2, email);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Password changed successfully for: " + email);
            return true;
        } else {
            System.err.println("No user found or user inactive: " + email);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("Error changing password: " + e.getMessage());
        return false;
    }
}
}

