package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginSystem {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel errorLabel;
    
    public LoginSystem() {
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        // Create the frame
        frame = new JFrame("Library System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center window
        frame.setResizable(false);
        
        // Create components
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signupButton = new JButton("Create New Account");
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        
        // Create main panel with layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Library System Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, gbc);
        
        // Email row
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(emailField, gbc);
        
        // Password row
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);
        
        // Error label
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(errorLabel, gbc);
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        mainPanel.add(buttonPanel, gbc);
        
        // Add main panel to frame
        frame.add(mainPanel);
        
        // Add event listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        // Enter key support for login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
private void handleLogin() {
    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());
    
    // Basic validation
    if (email.isEmpty() || password.isEmpty()) {
        errorLabel.setText("Please enter email and password");
        return;
    }
    
    // Authenticate user
    User user = AuthService.authenticate(email, password);
    
    if (user != null) {
        // Start new session
        SessionManager session = SessionManager.getInstance();
        session.startSession(user);
        
        errorLabel.setText("Login successful! Welcome, " + user.getName());
        errorLabel.setForeground(Color.BLUE);
        
        System.out.println("Login successful: " + user.getName() + " (" + user.getRole() + ")");
        System.out.println("Session: " + session.getSessionInfo());
        
        // Close login window and open main application
        frame.dispose();
        openMainApplication(user);
        
    } else {
        errorLabel.setText("Invalid email or password");
        errorLabel.setForeground(Color.RED);
        passwordField.setText(""); // Clear password field
    }
}    
    private void handleSignup() {
        // Simple registration dialog
        String name = JOptionPane.showInputDialog(frame, "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;
        
        String email = JOptionPane.showInputDialog(frame, "Enter your email:");
        if (email == null || email.trim().isEmpty()) return;
        
        String password = JOptionPane.showInputDialog(frame, "Enter your password:");
        if (password == null || password.trim().isEmpty()) return;
        
        boolean registered = AuthService.registerUser(name, email, password, "MEMBER");
        
        if (registered) {
            JOptionPane.showMessageDialog(frame, "Registration successful! You can now login.");
        } else {
            JOptionPane.showMessageDialog(frame, "Registration failed. Email may already exist.");
        }
    }
    
private void openMainApplication(User user) {
    // Close login window and open main dashboard
    frame.dispose();
    new MainDashboard().show(); // No parameter needed - gets user from session
}
    
    public void show() {
        frame.setVisible(true);
    }
    
    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginSystem().show();
            }
        });
    }
}