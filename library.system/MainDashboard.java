package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard {
    private JFrame frame;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private Timer sessionTimer;
    
    public MainDashboard() {
        // Get user from session manager
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
            // If no active session, redirect to login
            JOptionPane.showMessageDialog(null, "Please log in first.", "Session Expired", JOptionPane.WARNING_MESSAGE);
            new LoginSystem().show();
            return;
        }
        
        this.currentUser = session.getCurrentUser();
        createAndShowGUI();
        startSessionTimer();
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("Library Management System - Welcome " + currentUser.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header with user info and session details
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed interface based on user role
        tabbedPane = new JTabbedPane();
        
        if (currentUser.isLibrarian()) {
            createLibrarianTabs();
        } else {
            createMemberTabs();
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add status bar at bottom
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(240, 240, 240));
        
        SessionManager session = SessionManager.getInstance();
        
        // Welcome message with session info
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // User role and session info
        String sessionInfo = "Role: " + currentUser.getRole() + " | Session: " + 
                            session.getSessionDurationMinutes() + " min";
        JLabel sessionLabel = new JLabel(sessionInfo);
        sessionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        sessionLabel.setForeground(Color.DARK_GRAY);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 80, 80));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.add(welcomeLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(sessionLabel);
        
        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(220, 220, 220));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(Color.DARK_GRAY);
        
        // Session timer label that updates every minute
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        timerLabel.setForeground(Color.DARK_GRAY);
        
        // Update timer every second
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SessionManager session = SessionManager.getInstance();
                long minutes = session.getSessionDurationMinutes();
                timerLabel.setText("Session: " + minutes + " min");
                
                // Check for session expiration (8 hours)
                if (session.isSessionExpired()) {
                    handleSessionExpired();
                }
            }
        });
        timer.start();
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(timerLabel);
        
        return statusPanel;
    }
    
    private void startSessionTimer() {
        // Timer to update session duration in header
        sessionTimer = new Timer(60000, new ActionListener() { // Update every minute
            @Override
            public void actionPerformed(ActionEvent e) {
                // Refresh the header to show updated session duration
                SessionManager session = SessionManager.getInstance();
                
                // Check for session expiration (8 hours)
                if (session.isSessionExpired()) {
                    handleSessionExpired();
                }
            }
        });
        sessionTimer.start();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to logout?", "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (sessionTimer != null) {
                sessionTimer.stop();
            }
            SessionManager.getInstance().endSession();
            frame.dispose();
            new LoginSystem().show();
        }
    }
    
    private void handleSessionExpired() {
        if (sessionTimer != null) {
            sessionTimer.stop();
        }
        
        JOptionPane.showMessageDialog(frame, 
            "Your session has expired for security reasons. Please log in again.", 
            "Session Expired", 
            JOptionPane.WARNING_MESSAGE);
            
        SessionManager.getInstance().endSession();
        frame.dispose();
        new LoginSystem().show();
    }
    
    private void createLibrarianTabs() {
        // Tab 1: Book Management
        JPanel bookManagementPanel = createBookManagementPanel();
        tabbedPane.addTab("📚 Book Management", bookManagementPanel);
        
        // Tab 2: Member Management
        JPanel memberManagementPanel = createMemberManagementPanel();
        tabbedPane.addTab("👥 Member Management", memberManagementPanel);
        
        // Tab 3: Loan Management
        JPanel loanManagementPanel = createLoanManagementPanel();
        tabbedPane.addTab("📖 Loan Management", loanManagementPanel);
        
        // Tab 4: Reports
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("📊 Reports", reportsPanel);
        
        // Tab 5: System Admin
        JPanel adminPanel = createAdminPanel();
        tabbedPane.addTab("⚙️ System Admin", adminPanel);
    }
    
    private void createMemberTabs() {
        // Tab 1: Book Search
        JPanel searchPanel = createSearchPanel();
        tabbedPane.addTab("🔍 Search Books", searchPanel);
        
        // Tab 2: My Loans
        JPanel myLoansPanel = createMyLoansPanel();
        tabbedPane.addTab("📚 My Loans", myLoansPanel);
        
        // Tab 3: My Holds
        JPanel myHoldsPanel = createMyHoldsPanel();
        tabbedPane.addTab("⏳ My Holds", myHoldsPanel);
        
        // Tab 4: My Account
        JPanel myAccountPanel = createMyAccountPanel();
        tabbedPane.addTab("👤 My Account", myAccountPanel);
    }
    
    // Librarian Panels
    
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Book Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Management buttons
        JButton addBookBtn = createStyledButton("Add New Book", new Color(70, 130, 180));
        JButton searchBooksBtn = createStyledButton("Search Books", new Color(60, 179, 113));
        JButton manageCopiesBtn = createStyledButton("Manage Book Copies", new Color(218, 165, 32));
        JButton viewAllBooksBtn = createStyledButton("View All Books", new Color(186, 85, 211));
        
        // Add action listeners
        addBookBtn.addActionListener(e -> showComingSoon("Add New Book"));
        searchBooksBtn.addActionListener(e -> showComingSoon("Search Books"));
        manageCopiesBtn.addActionListener(e -> showComingSoon("Manage Copies"));
        viewAllBooksBtn.addActionListener(e -> showComingSoon("View All Books"));
        
        contentPanel.add(addBookBtn);
        contentPanel.add(searchBooksBtn);
        contentPanel.add(manageCopiesBtn);
        contentPanel.add(viewAllBooksBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMemberManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Member Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton addMemberBtn = createStyledButton("Add New Member", new Color(70, 130, 180));
        JButton viewMembersBtn = createStyledButton("View All Members", new Color(60, 179, 113));
        JButton searchMembersBtn = createStyledButton("Search Members", new Color(218, 165, 32));
        JButton manageFinesBtn = createStyledButton("Manage Fines", new Color(186, 85, 211));
        
        addMemberBtn.addActionListener(e -> showComingSoon("Add New Member"));
        viewMembersBtn.addActionListener(e -> showComingSoon("View All Members"));
        searchMembersBtn.addActionListener(e -> showComingSoon("Search Members"));
        manageFinesBtn.addActionListener(e -> showComingSoon("Manage Fines"));
        
        contentPanel.add(addMemberBtn);
        contentPanel.add(viewMembersBtn);
        contentPanel.add(searchMembersBtn);
        contentPanel.add(manageFinesBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoanManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Loan Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton checkoutBtn = createStyledButton("Check Out Book", new Color(70, 130, 180));
        JButton returnBtn = createStyledButton("Return Book", new Color(60, 179, 113));
        JButton renewBtn = createStyledButton("Renew Loan", new Color(218, 165, 32));
        JButton viewLoansBtn = createStyledButton("View All Loans", new Color(186, 85, 211));
        
        checkoutBtn.addActionListener(e -> showComingSoon("Check Out Book"));
        returnBtn.addActionListener(e -> showComingSoon("Return Book"));
        renewBtn.addActionListener(e -> showComingSoon("Renew Loan"));
        viewLoansBtn.addActionListener(e -> showComingSoon("View All Loans"));
        
        contentPanel.add(checkoutBtn);
        contentPanel.add(returnBtn);
        contentPanel.add(renewBtn);
        contentPanel.add(viewLoansBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Reports & Analytics", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton overdueReportBtn = createStyledButton("Overdue Books Report", new Color(70, 130, 180));
        JButton popularBooksBtn = createStyledButton("Popular Books Report", new Color(60, 179, 113));
        JButton finesReportBtn = createStyledButton("Fines Report", new Color(218, 165, 32));
        JButton exportBtn = createStyledButton("Export to CSV", new Color(186, 85, 211));
        
        overdueReportBtn.addActionListener(e -> showComingSoon("Overdue Books Report"));
        popularBooksBtn.addActionListener(e -> showComingSoon("Popular Books Report"));
        finesReportBtn.addActionListener(e -> showComingSoon("Fines Report"));
        exportBtn.addActionListener(e -> showComingSoon("Export to CSV"));
        
        contentPanel.add(overdueReportBtn);
        contentPanel.add(popularBooksBtn);
        contentPanel.add(finesReportBtn);
        contentPanel.add(exportBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("System Administration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton systemSettingsBtn = createStyledButton("System Settings", new Color(70, 130, 180));
        JButton userManagementBtn = createStyledButton("User Management", new Color(60, 179, 113));
        JButton databaseBtn = createStyledButton("Database Tools", new Color(218, 165, 32));
        JButton backupBtn = createStyledButton("Backup & Restore", new Color(186, 85, 211));
        
        systemSettingsBtn.addActionListener(e -> showComingSoon("System Settings"));
        userManagementBtn.addActionListener(e -> showComingSoon("User Management"));
        databaseBtn.addActionListener(e -> showComingSoon("Database Tools"));
        backupBtn.addActionListener(e -> showComingSoon("Backup & Restore"));
        
        contentPanel.add(systemSettingsBtn);
        contentPanel.add(userManagementBtn);
        contentPanel.add(databaseBtn);
        contentPanel.add(backupBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    //  عدلت عليه
    
    private JPanel createSearchPanel() {
    return new BookCatalogPanel();
}
    /*LLLLLLLLLLLLlllllllllllllllllllllllllllllllllllllllll*/
    private JPanel createMyLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Current Loans", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create a table for loans
        String[] columnNames = {"Book Title", "Author", "Due Date", "Status"};
        Object[][] data = {
            {"No current loans", "-", "-", "-"}
        };
        
        JTable loansTable = new JTable(data, columnNames);
        loansTable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(loansTable);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JLabel("You have 0 books currently on loan."), BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMyHoldsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Holds/Reservations", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create a table for holds
        String[] columnNames = {"Book Title", "Author", "Position in Queue", "Status"};
        Object[][] data = {
            {"No active holds", "-", "-", "-"}
        };
        
        JTable holdsTable = new JTable(data, columnNames);
        holdsTable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(holdsTable);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JLabel("You have 0 active holds."), BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }
    
private JPanel createMyAccountPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("My Account Information", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    panel.add(titleLabel, BorderLayout.NORTH);
    
    JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    
    SessionManager session = SessionManager.getInstance();
    
    infoPanel.add(new JLabel("Name:"));
    infoPanel.add(new JLabel(currentUser.getName()));
    infoPanel.add(new JLabel("Email:"));
    infoPanel.add(new JLabel(currentUser.getEmail()));
    infoPanel.add(new JLabel("Member ID:"));
    infoPanel.add(new JLabel(String.valueOf(currentUser.getMemberId())));
    infoPanel.add(new JLabel("Role:"));
    infoPanel.add(new JLabel(currentUser.getRole()));
    infoPanel.add(new JLabel("Account Status:"));
    infoPanel.add(new JLabel(currentUser.isActive() ? "Active" : "Inactive"));
    infoPanel.add(new JLabel("Session Duration:"));
    infoPanel.add(new JLabel(session.getSessionDurationMinutes() + " minutes"));
    
    panel.add(infoPanel, BorderLayout.CENTER);
    
    // Add change password button - SIMPLE VERSION
    JButton changePasswordBtn = new JButton("Change Password");
    changePasswordBtn.setFont(new Font("Arial", Font.BOLD, 14));
    changePasswordBtn.setBackground(new Color(70, 130, 180));
    changePasswordBtn.setForeground(Color.WHITE);
    changePasswordBtn.setFocusPainted(false);
    changePasswordBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    changePasswordBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("=== CHANGE PASSWORD BUTTON CLICKED ===");
            System.out.println("Current User: " + currentUser.getName());
            System.out.println("Frame: " + frame);
            
            try {
                PasswordChangeDialog dialog = new PasswordChangeDialog(frame, currentUser);
                System.out.println("Dialog created successfully");
                dialog.setVisible(true);
                System.out.println("Dialog is now visible");
            } catch (Exception ex) {
                System.err.println("ERROR creating dialog: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, 
                    "Error opening password change: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(changePasswordBtn);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    
    return panel;
}

private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }
    
    private void showComingSoon(String feature) {
        String assignee = "Team Member";
        
        if (feature.contains("Book")) {
            assignee = "Person 2: Book Catalog & Search";
        } else if (feature.contains("Member") || feature.contains("Hold")) {
            assignee = "Person 4: Reservations & User Management";
        } else if (feature.contains("Loan") || feature.contains("Return") || feature.contains("Renew")) {
            assignee = "Person 3: Borrowing & Returns";
        } else if (feature.contains("Report") || feature.contains("Export") || feature.contains("Fine")) {
            assignee = "Person 5: Admin Features & Reporting";
        } else if (feature.contains("Password")) {
            assignee = "Person 1: Authentication (You!) - Coming next!";
        }
        
        JOptionPane.showMessageDialog(frame, 
            feature + " feature is coming soon!\n\n" +
            "This will be implemented by:\n" + assignee, 
            "Feature Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void show() {
        if (frame != null) {
            frame.setVisible(true);
        }
    }
}
