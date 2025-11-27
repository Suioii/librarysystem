package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ManageFinesUI extends JFrame {

    private JTable finesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> memberCombo;
    private JTextField amountField;
    private JTextField reasonField;

    public ManageFinesUI() {
        setTitle("Manage Fines");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Issue New Fine"));
        
        inputPanel.add(new JLabel("Select Member:"));
        memberCombo = new JComboBox<>();
        loadMembersToCombo();
        inputPanel.add(memberCombo);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Reason:"));
        reasonField = new JTextField();
        inputPanel.add(reasonField);

        JButton issueBtn = new JButton("Issue Fine");
        issueBtn.setBackground(new Color(255, 200, 200));
        issueBtn.addActionListener(e -> issueFine());
        inputPanel.add(issueBtn);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Fine ID", "Member Name", "Amount", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        finesTable = new JTable(tableModel);
        
        finesTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        add(new JScrollPane(finesTable), BorderLayout.CENTER);

        JButton payBtn = new JButton("Mark Selected as PAID");
        payBtn.setFont(new Font("Arial", Font.BOLD, 14));
        payBtn.setBackground(new Color(144, 238, 144));
        payBtn.addActionListener(e -> payFine());
        add(payBtn, BorderLayout.SOUTH);

        loadFines();
    }

    private void loadMembersToCombo() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT member_id, name FROM members")) {

            while (rs.next()) {
                String item = rs.getInt("member_id") + " - " + rs.getString("name");
                memberCombo.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFines() {
        String sql = "SELECT f.fine_id, m.name, f.amount, f.reason, f.status " +
                     "FROM fines f JOIN members m ON f.member_id = m.member_id " +
                     "ORDER BY f.status DESC, f.fine_id DESC";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("fine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("reason"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading fines: " + e.getMessage());
        }
    }

    private void issueFine() {
        try {
            String selectedItem = (String) memberCombo.getSelectedItem();
            if (selectedItem == null) return;
            
            int memberId = Integer.parseInt(selectedItem.split(" - ")[0]);
            double amount = Double.parseDouble(amountField.getText());
            String reason = reasonField.getText();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason.");
                return;
            }

            String sql = "INSERT INTO fines (member_id, amount, reason, status) VALUES (?, ?, ?, 'UNPAID')";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, memberId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, reason);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Fine Issued Successfully!");
                loadFines();
                
                amountField.setText("");
                reasonField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void payFine() {
        int selectedRow = finesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to pay.");
            return;
        }

        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        if ("PAID".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This fine is already paid.");
            return;
        }

        int fineId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE fines SET status = 'PAID', paid_date = CURRENT_DATE WHERE fine_id = ?")) {
            
            pstmt.setInt(1, fineId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Fine marked as PAID!");
            loadFines();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
        }
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            
            if (!isSelected) {
                if ("PAID".equals(status)) {
                    c.setForeground(new Color(0, 150, 0));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
            }
            return c;
        }
    }
}