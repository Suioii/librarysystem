package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainDashboard {
    private JFrame frame;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private Timer sessionTimer;
    
    public MainDashboard() {
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
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
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        
        if (currentUser.isLibrarian()) {
            createLibrarianTabs();
        } else {
            createMemberTabs();
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(240, 240, 240));
        
        SessionManager session = SessionManager.getInstance();
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        String sessionInfo = "Role: " + currentUser.getRole() + " | Session: " + 
                            session.getSessionDurationMinutes() + " min";
        JLabel sessionLabel = new JLabel(sessionInfo);
        sessionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        sessionLabel.setForeground(Color.DARK_GRAY);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 80, 80));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());
        
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
        
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        timerLabel.setForeground(Color.DARK_GRAY);
        
        Timer timer = new Timer(1000, e -> {
            SessionManager session = SessionManager.getInstance();
            long minutes = session.getSessionDurationMinutes();
            timerLabel.setText("Session: " + minutes + " min");
            
            if (session.isSessionExpired()) {
                handleSessionExpired();
            }
        });
        timer.start();
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(timerLabel);
        
        return statusPanel;
    }
    
    private void startSessionTimer() {
        sessionTimer = new Timer(60000, e -> {
            SessionManager session = SessionManager.getInstance();
            if (session.isSessionExpired()) {
                handleSessionExpired();
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
        JPanel bookManagementPanel = createBookManagementPanel();
        tabbedPane.addTab("Book Management", bookManagementPanel);
        
        JPanel memberManagementPanel = createMemberManagementPanel();
        tabbedPane.addTab("Member Management", memberManagementPanel);
        
        JPanel loanManagementPanel = createLoanManagementPanel();
        tabbedPane.addTab("Loan Management", loanManagementPanel);
        
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("Reports", reportsPanel);
        
        JPanel adminPanel = createAdminPanel();
        tabbedPane.addTab("System Admin", adminPanel);
    }
    
    private void createMemberTabs() {
        JPanel searchPanel = createSearchPanel();
        tabbedPane.addTab("Search Books", searchPanel);
        
        JPanel myLoansPanel = createMyLoansPanel();
        tabbedPane.addTab("My Loans", myLoansPanel);
        
        JPanel myHoldsPanel = createMyHoldsPanel();
        tabbedPane.addTab("My Holds", myHoldsPanel);
        
        JPanel myAccountPanel = createMyAccountPanel();
        tabbedPane.addTab("My Account", myAccountPanel);
    }
    
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Book Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton addBookBtn = createStyledButton("Add New Book", new Color(70, 130, 180));
        JButton searchBooksBtn = createStyledButton("Search Books", new Color(60, 179, 113));
        JButton manageCopiesBtn = createStyledButton("Manage Book Copies", new Color(218, 165, 32));
        JButton viewAllBooksBtn = createStyledButton("View All Books", new Color(186, 85, 211));
        
        addBookBtn.addActionListener(e -> new ManageBooksUI().setVisible(true));
        
        searchBooksBtn.addActionListener(e -> new SearchBooksUI().setVisible(true));
    
       manageCopiesBtn.addActionListener(e -> new ManageBookCopiesUI().setVisible(true));
    
    viewAllBooksBtn.addActionListener(e -> new ViewAllBooksUI().setVisible(true));

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
        
        addMemberBtn.addActionListener(e -> new MemberRegistrationUI()); 
        viewMembersBtn.addActionListener(e -> new MemberListUI()); 
        searchMembersBtn.addActionListener(e -> new MemberSearchUI()); 

        manageFinesBtn.addActionListener(e -> new ManageFinesUI().setVisible(true));
        
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
        JButton viewHoldQueueBtn = createStyledButton("View Hold Queue", new Color(123, 104, 238));
  
        checkoutBtn.addActionListener(e -> new CheckoutUI());
        returnBtn.addActionListener(e -> new ReturnUI());

        renewBtn.addActionListener(e -> {
            String loanIdInput = JOptionPane.showInputDialog(frame, "Enter Loan ID to renew:");
            if (loanIdInput != null && !loanIdInput.trim().isEmpty()) {
                try {
                    int loanId = Integer.parseInt(loanIdInput);
                    BorrowingService service = new BorrowingService();
                    String result = service.renewLoan(loanId);
                    JOptionPane.showMessageDialog(frame, result, "Renewal Status", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Loan ID format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewLoansBtn.addActionListener(e -> new ViewAllLoansUI());
        viewHoldQueueBtn.addActionListener(e -> new HoldQueueUI());

        contentPanel.add(checkoutBtn);
        contentPanel.add(returnBtn);
        contentPanel.add(renewBtn);
        contentPanel.add(viewLoansBtn);
        contentPanel.add(viewHoldQueueBtn);

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
        
        overdueReportBtn.addActionListener(e -> new ViewReportsUI().setVisible(true));
        popularBooksBtn.addActionListener(e -> new ViewReportsUI().setVisible(true));
        finesReportBtn.addActionListener(e -> new ViewReportsUI().setVisible(true));
        exportBtn.addActionListener(e -> new ViewReportsUI().setVisible(true));
        
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
        userManagementBtn.addActionListener(e -> new ManageUsersUI().setVisible(true));
        databaseBtn.addActionListener(e -> showComingSoon("Database Tools"));
        backupBtn.addActionListener(e -> showComingSoon("Backup & Restore"));
        
        contentPanel.add(systemSettingsBtn);
        contentPanel.add(userManagementBtn);
        contentPanel.add(databaseBtn);
        contentPanel.add(backupBtn);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSearchPanel() {
        return new BookCatalogPanel();
    }
    
    private JPanel createMyLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Current Loans", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
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

        JLabel titleLabel = new JLabel("My Holds / Reservations", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        SessionManager session = SessionManager.getInstance();
        User current = session.getCurrentUser();

        if (current == null) {
            panel.add(new JLabel("You must be logged in to view your holds.", JLabel.CENTER),
                    BorderLayout.CENTER);
            return panel;
        }

        int memberId = current.getMemberId();

        String[][] data = ReservationService.getHoldsForMemberTableData(memberId);

        String[] columnNames = {"Hold ID", "Book Title", "Status", "Placed At"};

        JTable holdsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        holdsTable.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(holdsTable);

        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel infoLabel;

        if (data.length == 0) {
            infoLabel = new JLabel("You have 0 active holds.", JLabel.LEFT);
        } else {
            infoLabel = new JLabel("You currently have " + data.length + " hold(s).", JLabel.LEFT);
        }

        infoPanel.add(infoLabel, BorderLayout.NORTH);
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
        
        JButton changePasswordBtn = new JButton("Change Password");
        changePasswordBtn.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordBtn.setBackground(new Color(70, 130, 180));
        changePasswordBtn.setForeground(Color.WHITE);
        changePasswordBtn.setFocusPainted(false);
        changePasswordBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        changePasswordBtn.addActionListener(e -> {
            try {
                PasswordChangeDialog dialog = new PasswordChangeDialog(frame, currentUser);
                dialog.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Error opening password change: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(frame, feature + " feature is coming soon!", "Feature Pending", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void show() {
        if (frame != null) {
            frame.setVisible(true);
        }
    }
}
