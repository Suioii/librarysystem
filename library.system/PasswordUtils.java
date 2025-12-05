package librarysystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    
    
    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 not available, using simple hash");
            return simpleHash(password);
        }
    }
    
   
    public static boolean checkPassword(String password, String storedHash) {
        try {
            
            if (storedHash != null && storedHash.startsWith("$2a$")) {
                System.err.println("Found old BCrypt hash - password needs reset");
                return false;
            }
            
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            byte[] salt = new byte[16];
            byte[] storedPasswordHash = new byte[combined.length - 16];
            
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, storedPasswordHash, 0, storedPasswordHash.length);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            return MessageDigest.isEqual(hashedPassword, storedPasswordHash);
            
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
   
    private static String simpleHash(String password) {
        return Integer.toString(password.hashCode());
    }
    
   
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
