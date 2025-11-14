package librarysystem;

public class FinalAuthTest {
    public static void main(String[] args) {
        System.out.println("=== FINAL AUTHENTICATION VERIFICATION ===");
        
        // Test 1: Direct PasswordUtils test
        System.out.println("\n1. Testing PasswordUtils directly:");
        testPasswordUtils();
        
        // Test 2: Database authentication test
        System.out.println("\n2. Testing Database Authentication:");
        testDatabaseAuth();
        
        // Test 3: Registration test
        System.out.println("\n3. Testing User Registration:");
        testRegistration();
    }
    
    private static void testPasswordUtils() {
        String password = "TestPassword123!";
        
        // Test hashing
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);
        
        System.out.println("  Password: " + password);
        System.out.println("  Hash 1: " + hash1.substring(0, 30) + "...");
        System.out.println("  Hash 2: " + hash2.substring(0, 30) + "...");
        System.out.println("  Hashes different (good): " + !hash1.equals(hash2));
        
        // Test verification
        boolean verify1 = PasswordUtils.checkPassword(password, hash1);
        boolean verify2 = PasswordUtils.checkPassword(password, hash2);
        boolean wrongVerify = PasswordUtils.checkPassword("wrong", hash1);
        
        System.out.println("  Verify 1: " + verify1);
        System.out.println("  Verify 2: " + verify2);
        System.out.println("  Wrong password: " + wrongVerify);
        
        // Test password strength
        System.out.println("  Strong password: " + PasswordUtils.isPasswordStrong("Secure123!"));
        System.out.println("  Weak password: " + PasswordUtils.isPasswordStrong("123"));
    }
    
    private static void testDatabaseAuth() {
        System.out.println("  Testing librarian login...");
        User librarian = AuthService.authenticate("librarian@library.com", "Librarian123!");
        System.out.println("  Librarian auth: " + (librarian != null ? "SUCCESS" : "FAILED"));
        
        System.out.println("  Testing member login...");
        User member = AuthService.authenticate("john@email.com", "Member123!");
        System.out.println("  Member auth: " + (member != null ? "SUCCESS" : "FAILED"));
        
        System.out.println("  Testing wrong password...");
        User wrong = AuthService.authenticate("librarian@library.com", "wrongpassword");
        System.out.println("  Wrong password: " + (wrong == null ? "CORRECTLY REJECTED" : "INCORRECTLY ACCEPTED"));
    }
    
    private static void testRegistration() {
        String testEmail = "testuser_" + System.currentTimeMillis() + "@test.com";
        boolean registered = AuthService.registerUser("Test User", testEmail, "TestPass123!", "MEMBER");
        System.out.println("  New registration: " + (registered ? "SUCCESS" : "FAILED"));
        
        if (registered) {
            User newUser = AuthService.authenticate(testEmail, "TestPass123!");
            System.out.println("  New user login: " + (newUser != null ? "SUCCESS" : "FAILED"));
        }
    }
}