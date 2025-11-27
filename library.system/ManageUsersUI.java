package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class ManageUsersUI extends JFrame {

    private JTextField nameField, emailField, passwordField;
    private JComboBox<String> roleCombo;
    private JTable usersTable;
    private DefaultTableModel tableModel;

    public ManageUsersUI() {
        setTitle("Manage Users");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        inputPanel.add(new JLabel("Role:"));
        String[] roles = {"MEMBER", "LIBRARIAN"};
        roleCombo = new JComboBox<>(roles);
        inputPanel.add(roleCombo);

        JButton addBtn = new JButton("Add User");
        JButton deleteBtn = new JButton("Delete Selected");
        
        inputPanel.add(addBtn);
        inputPanel.add(deleteBtn);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Role", "Active"};
        tableModel = new DefaultTableModel(columns, 0);
        usersTable = new JTable(tableModel);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        loadUsersData();

        addBtn.addActionListener(e -> addUser());
        deleteBtn.addActionListener(e -> deleteUser());
    }

    private void loadUsersData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("member_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("role"));
                row.add(rs.getBoolean("is_active"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void addUser() {
        String sql = "INSERT INTO members (name, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, true)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, emailField.getText());
            pstmt.setString(3, passwordField.getText()); 
            pstmt.setString(4, roleCombo.getSelectedItem().toString());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Added Successfully!");
            
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            loadUsersData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM members WHERE member_id = ?")) {
            
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Deleted!");
            loadUsersData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }
}
