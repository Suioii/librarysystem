package librarysystem;

public class PasswordChangeTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Password Change Functionality ===");
        
        // Test 1: Verify current password
        System.out.println("\n1. Testing current password verification:");
        boolean verifyCorrect = AuthService.verifyCurrentPassword("librarian@library.com", "Librarian123!");
        boolean verifyWrong = AuthService.verifyCurrentPassword("librarian@library.com", "wrongpassword");
        System.out.println("   Correct password: " + verifyCorrect);
        System.out.println("   Wrong password: " + verifyWrong);
        
        // Test 2: Test password change
        System.out.println("\n2. Testing password change:");
        boolean changeSuccess = AuthService.changePassword("librarian@library.com", "NewSecurePass123!");
        System.out.println("   Password change: " + (changeSuccess ? "SUCCESS" : "FAILED"));
        
        if (changeSuccess) {
            // Test login with new password
            System.out.println("\n3. Testing login with new password:");
            User user = AuthService.authenticate("librarian@library.com", "NewSecurePass123!");
            System.out.println("   Login with new password: " + (user != null ? "SUCCESS" : "FAILED"));
            
            // Change back to original password
            System.out.println("\n4. Changing back to original password...");
            AuthService.changePassword("librarian@library.com", "Librarian123!");
            User user2 = AuthService.authenticate("librarian@library.com", "Librarian123!");
            System.out.println("   Back to original: " + (user2 != null ? "SUCCESS" : "FAILED"));
        }
    }
}