package librarysystem;

import javax.swing.*;
import java.awt.*;

public class MemberRegistrationUI extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;

    public MemberRegistrationUI() {
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn() || !session.getCurrentUser().isLibrarian()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Access denied. Librarian only.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Register New Member");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // الحقول
        nameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        confirmField = new JPasswordField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmField);

        add(formPanel, BorderLayout.CENTER);

        JButton registerBtn = new JButton("Register Member");
        registerBtn.addActionListener(e -> handleRegister());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(registerBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = AuthService.registerUser(name, email, password, "MEMBER");

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Member registered successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // نفضي الحقول
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmField.setText("");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Check console for details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
