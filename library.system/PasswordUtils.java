package librarysystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    
    /**
     * Hash a password with salt using SHA-256 (built-in Java security)
     * This creates a secure hash that's different every time due to random salt
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt (16 bytes = 128 bits)
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Create SHA-256 hash of salt + password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt + hash for storage
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Encode as Base64 for storage
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if SHA-256 not available (should never happen in modern Java)
            System.err.println("SHA-256 not available, using simple hash");
            return simpleHash(password);
        }
    }
    
    /**
     * Verify a password against stored hash
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            // If stored hash starts with $2a$ it's a BCrypt hash from previous attempts
            // We'll treat these as invalid and force password reset
            if (storedHash != null && storedHash.startsWith("$2a$")) {
                System.err.println("Found old BCrypt hash - password needs reset");
                return false;
            }
            
            // Decode the stored hash from Base64
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extract salt (first 16 bytes) and stored hash (remaining bytes)
            byte[] salt = new byte[16];
            byte[] storedPasswordHash = new byte[combined.length - 16];
            
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, storedPasswordHash, 0, storedPasswordHash.length);
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Compare the hashes
            return MessageDigest.isEqual(hashedPassword, storedPasswordHash);
            
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Simple fallback hashing (only used if SHA-256 is unavailable)
     */
    private static String simpleHash(String password) {
        return Integer.toString(password.hashCode());
    }
    
    /**
     * Validate password strength
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasDigit && hasSpecial;
    }
    
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters with:\n" +
               "- One uppercase letter\n" +
               "- One number\n" +
               "- One special character";
    }
}