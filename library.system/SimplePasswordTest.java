package librarysystem;

public class SimplePasswordTest {
    public static void main(String[] args) {
        System.out.println("=== Testing PasswordUtils ===");
        
        // Test basic functionality
        String testPass = "Test123!";
        String hash = PasswordUtils.hashPassword(testPass);
        
        System.out.println("Password: " + testPass);
        System.out.println("Hashed: " + hash);
        System.out.println("Verify correct: " + PasswordUtils.checkPassword(testPass, hash));
        System.out.println("Verify wrong: " + PasswordUtils.checkPassword("wrong", hash));
        System.out.println("Is strong: " + PasswordUtils.isPasswordStrong(testPass));
    }
}