package librarysystem;

public class LibrarySystem {
    public static void main(String[] args) {
        System.out.println("🚀 Library Management System - Clean Start");
        System.out.println("Using Java Built-in Security (SHA-256 with Salt)");
        
        try {
            // Step 1: Test database connection
            System.out.println("\n1. Testing database connection...");
            var conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("   ✅ Database connection successful!");
                conn.close();
            }
            
            // Step 2: Reset database with new password hashing
            System.out.println("\n2. Setting up database with secure password hashing...");
            DatabaseSetup.runFullSetup();
            
            // Step 3: Show success message
            System.out.println("\n🎉 SYSTEM READY!");
            System.out.println("✅ Database initialized with secure password hashing");
            System.out.println("✅ Authentication system using Java SHA-256 with salt");
            System.out.println("✅ Role-based access control ready");
            System.out.println("\nTest Credentials:");
            System.out.println("   Librarian: librarian@library.com / Librarian123!");
            System.out.println("   Member:    john@email.com / Member123!");
            
            // Step 4: Launch login system
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new LoginSystem().show();
                }
            });
            
        } catch (Exception e) {
            System.err.println("❌ System startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}