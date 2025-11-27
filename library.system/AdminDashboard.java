package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard - Library System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton manageBooksBtn = createStyledButton("Manage Books");
        manageBooksBtn.addActionListener((ActionEvent e) -> {
            new ManageBooksUI().setVisible(true);
        });

        JButton manageUsersBtn = createStyledButton("Manage Users");
        manageUsersBtn.addActionListener((ActionEvent e) -> {
            new ManageUsersUI().setVisible(true);
        });

        JButton reportsBtn = createStyledButton("View Reports");
        reportsBtn.addActionListener((ActionEvent e) -> {
            new ViewReportsUI().setVisible(true);
        });

        JButton finesBtn = createStyledButton("Manage Fines");
        finesBtn.addActionListener((ActionEvent e) -> {
            new ManageFinesUI().setVisible(true);
        });

        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.setBackground(new Color(255, 100, 100));
        logoutBtn.addActionListener(e -> {
            dispose();
        });

        buttonPanel.add(manageBooksBtn);
        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(finesBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Logged in as: Administrator", SwingConstants.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}