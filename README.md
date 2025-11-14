# Library Management System
Java Swing Application with MySQL Database

## 👥 Team Members
- Deem Albassam : Authentication & Core Setup ✅ COMPLETE
- Atheer Alotaibi: Book Catalog & Search
- Rayda Almanie: Borrowing & Returns  
- Ghala nasser: Reservations & User Management
- Person 5: Admin Features & Reporting

## 🚀 Quick Start

### Prerequisites
- Java 8+
- MySQL Database
- MySQL Connector/J

### Setup
1. Clone this repository
2. Create MySQL database: `library_db`
3. Update database credentials in `DatabaseConnection.java`
4. Run `DatabaseSetup.runFullSetup()` once
5. Run `LibrarySystem.main()`

## 📁 Project Structure
library-system/
├── .gitignore
├── README.md
└── librarysystem/
├── AuthService.java # Login/authentication logic
├── DatabaseConnection.java # MySQL database configuration
├── DatabaseSetup.java # Database schema & sample data
├── PasswordUtils.java # Secure password hashing
├── SessionManager.java # User session management
├── User.java # User data model
├── LoginSystem.java # Login GUI interface
├── MainDashboard.java # Main application dashboard
├── LibrarySystem.java # Application entry point
├── PasswordChangeDialog.java # Password change interface
├── FinalAuthTest.java # Comprehensive authentication tests
├── PasswordChangeTest.java # Password change functionality tests
├── SimplePasswordTest.java # Basic password utility tests
└── BCryptTest.java # Legacy hash testing
