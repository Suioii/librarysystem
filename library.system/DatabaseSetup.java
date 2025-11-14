package librarysystem;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DatabaseSetup {
    
    public static void runFullSetup() {
        createTables();
        insertSampleData();
        System.out.println("=== FULL LIBRARY DATABASE SETUP COMPLETED ===");
    }
    
    private static void createTables() {
        String[] createTablesSQL = {
            // Drop tables in correct order (due to foreign keys)
            "DROP TABLE IF EXISTS holds",
            "DROP TABLE IF EXISTS fines", 
            "DROP TABLE IF EXISTS loans",
            "DROP TABLE IF EXISTS copies",
            "DROP TABLE IF EXISTS books",
            "DROP TABLE IF EXISTS members",

            // Create members table
            "CREATE TABLE members (" +
            "member_id INT PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(100) NOT NULL," +
            "email VARCHAR(100) UNIQUE NOT NULL," + 
            "password_hash VARCHAR(255) NOT NULL," +
            "role ENUM('LIBRARIAN', 'MEMBER') NOT NULL," +
            "registration_date DATE DEFAULT (CURRENT_DATE)," +
            "is_active BOOLEAN DEFAULT TRUE)",

            // Create books table
            "CREATE TABLE books (" +
            "book_id INT PRIMARY KEY AUTO_INCREMENT," +
            "isbn VARCHAR(20) UNIQUE NOT NULL," +
            "title VARCHAR(200) NOT NULL," +
            "author VARCHAR(100) NOT NULL," +
            "category VARCHAR(50)," +
            "publication_year INT," +
            "description TEXT)",

            // Create copies table
            "CREATE TABLE copies (" +
            "copy_id INT PRIMARY KEY AUTO_INCREMENT," +
            "book_id INT NOT NULL," +
            "status ENUM('AVAILABLE', 'CHECKED_OUT', 'MAINTENANCE') DEFAULT 'AVAILABLE'," +
            "location VARCHAR(50) DEFAULT 'Main Shelf'," +
            "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE)",

            // Create loans table
            "CREATE TABLE loans (" +
            "loan_id INT PRIMARY KEY AUTO_INCREMENT," +
            "copy_id INT NOT NULL," +
            "member_id INT NOT NULL," +
            "checkout_date DATE DEFAULT (CURRENT_DATE)," +
            "due_date DATE NOT NULL," +
            "return_date DATE NULL," +
            "renewed_count INT DEFAULT 0," +
            "FOREIGN KEY (copy_id) REFERENCES copies(copy_id)," +
            "FOREIGN KEY (member_id) REFERENCES members(member_id))",

            // Create fines table
            "CREATE TABLE fines (" +
            "fine_id INT PRIMARY KEY AUTO_INCREMENT," +
            "member_id INT NOT NULL," +
            "loan_id INT NULL," +
            "amount DECIMAL(8,2) NOT NULL," +
            "reason VARCHAR(100) NOT NULL," +
            "issue_date DATE DEFAULT (CURRENT_DATE)," +
            "paid_date DATE NULL," +
            "status ENUM('UNPAID', 'PAID', 'WAIVED') DEFAULT 'UNPAID'," +
            "FOREIGN KEY (member_id) REFERENCES members(member_id)," +
            "FOREIGN KEY (loan_id) REFERENCES loans(loan_id))",

            // Create holds table
            "CREATE TABLE holds (" +
            "hold_id INT PRIMARY KEY AUTO_INCREMENT," +
            "book_id INT NOT NULL," +
            "member_id INT NOT NULL," +
            "place_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "status ENUM('PENDING', 'READY', 'CANCELLED') DEFAULT 'PENDING'," +
            "notification_sent BOOLEAN DEFAULT FALSE," +
            "FOREIGN KEY (book_id) REFERENCES books(book_id)," +
            "FOREIGN KEY (member_id) REFERENCES members(member_id))"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Creating database tables...");
            for (String sql : createTablesSQL) {
                stmt.execute(sql);
            }
            System.out.println("✓ All tables created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void insertSampleData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Inserting sample data...");
            
            insertMembers(conn);
            insertBooks(conn);
            insertCopies(conn);
            insertLoans(conn);
            insertFines(conn);
            insertHolds(conn);
            
            System.out.println("✓ Sample data inserted successfully!");
            
        } catch (Exception e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
private static void insertMembers(Connection conn) throws Exception {
    String sql = "INSERT INTO members (name, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Generate real hashes using our new PasswordUtils
        String librarianHash = PasswordUtils.hashPassword("Librarian123!");
        String memberHash = PasswordUtils.hashPassword("Member123!");
        
        System.out.println("Generated librarian hash: " + librarianHash.substring(0, 20) + "...");
        System.out.println("Generated member hash: " + memberHash.substring(0, 20) + "...");
        
        // Librarians
        insertMember(pstmt, "Admin Librarian", "librarian@library.com", 
                    librarianHash, "LIBRARIAN", true);
        insertMember(pstmt, "Sarah Johnson", "sarah@library.com", 
                    librarianHash, "LIBRARIAN", true);
        
        // Regular Members
        insertMember(pstmt, "John Student", "john@email.com", 
                    memberHash, "MEMBER", true);
        insertMember(pstmt, "Maria Garcia", "maria@email.com", 
                    memberHash, "MEMBER", true);
        insertMember(pstmt, "Bob Smith", "bob@email.com", 
                    memberHash, "MEMBER", true);
        insertMember(pstmt, "Alice Chen", "alice@email.com", 
                    memberHash, "MEMBER", true);
        insertMember(pstmt, "Tom Wilson", "tom@email.com", 
                    memberHash, "MEMBER", true);
        
        // Inactive member for testing
        insertMember(pstmt, "Inactive User", "inactive@email.com", 
                    memberHash, "MEMBER", false);
    }
    System.out.println("✓ Members inserted with Java SHA-256 hashed passwords");
}
    
    private static void insertMember(PreparedStatement pstmt, String name, String email, String passwordHash, String role, boolean active) throws Exception {
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, passwordHash);
        pstmt.setString(4, role);
        pstmt.setBoolean(5, active);
        pstmt.executeUpdate();
    }
    
    private static void insertBooks(Connection conn) throws Exception {
        String sql = "INSERT INTO books (isbn, title, author, category, publication_year, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            insertBook(pstmt, "9780439708180", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "Fantasy", 1997, "First book in series");
            insertBook(pstmt, "9780439064873", "Harry Potter and the Chamber of Secrets", "J.K. Rowling", "Fantasy", 1998, "Second book in series");
            insertBook(pstmt, "9780451524935", "1984", "George Orwell", "Science Fiction", 1949, "Dystopian novel");
        }
        System.out.println("✓ Books inserted");
    }
    
    private static void insertBook(PreparedStatement pstmt, String isbn, String title, String author, String category, int year, String description) throws Exception {
        pstmt.setString(1, isbn);
        pstmt.setString(2, title);
        pstmt.setString(3, author);
        pstmt.setString(4, category);
        pstmt.setInt(5, year);
        pstmt.setString(6, description);
        pstmt.executeUpdate();
    }
    
    private static void insertCopies(Connection conn) throws Exception {
        String sql = "INSERT INTO copies (book_id, status, location) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            insertCopy(pstmt, 1, "AVAILABLE", "Fantasy Section");
            insertCopy(pstmt, 1, "CHECKED_OUT", "Checked Out");
            insertCopy(pstmt, 2, "AVAILABLE", "Fantasy Section");
            insertCopy(pstmt, 3, "AVAILABLE", "Sci-Fi Section");
        }
        System.out.println("✓ Copies inserted");
    }
    
    private static void insertCopy(PreparedStatement pstmt, int bookId, String status, String location) throws Exception {
        pstmt.setInt(1, bookId);
        pstmt.setString(2, status);
        pstmt.setString(3, location);
        pstmt.executeUpdate();
    }
    
    private static void insertLoans(Connection conn) throws Exception {
        String sql = "INSERT INTO loans (copy_id, member_id, checkout_date, due_date) VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY))";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // John borrows a book
            pstmt.setInt(1, 2); // copy_id 2 (Harry Potter 1 - checked out)
            pstmt.setInt(2, 3); // member_id 3 (John Student)
            pstmt.executeUpdate();
        }
        System.out.println("✓ Loans inserted");
    }
    
    private static void insertFines(Connection conn) throws Exception {
        // Skip for now - we'll add this later
        System.out.println("✓ Fines skipped for now");
    }
    
    private static void insertHolds(Connection conn) throws Exception {
        // Skip for now - we'll add this later
        System.out.println("✓ Holds skipped for now");
    }
}