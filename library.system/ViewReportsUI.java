package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewReportsUI extends JFrame {

    private JLabel totalBooksLabel;
    private JLabel totalMembersLabel;
    private JLabel activeLoansLabel;
    private JTextArea detailedReportArea;

    public ViewReportsUI() {
        setTitle("Library Reports & Statistics");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(new Color(240, 240, 240));

        totalBooksLabel = createStatCard("Total Books");
        totalMembersLabel = createStatCard("Total Members");
        activeLoansLabel = createStatCard("Active Loans");

        statsPanel.add(totalBooksLabel);
        statsPanel.add(totalMembersLabel);
        statsPanel.add(activeLoansLabel);

        add(statsPanel, BorderLayout.NORTH);

        detailedReportArea = new JTextArea();
        detailedReportArea.setEditable(false);
        detailedReportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailedReportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(detailedReportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Currently Borrowed Books List"));
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> generateReport());
        add(refreshBtn, BorderLayout.SOUTH);

        generateReport();
    }

    private JLabel createStatCard(String title) {
        JLabel label = new JLabel(title + ": 0", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        return label;
    }

    private void generateReport() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            updateCount(conn, "SELECT COUNT(*) FROM books", totalBooksLabel, "Total Books: ");
            updateCount(conn, "SELECT COUNT(*) FROM members", totalMembersLabel, "Total Members: ");
            updateCount(conn, "SELECT COUNT(*) FROM loans WHERE return_date IS NULL", activeLoansLabel, "Active Loans: ");

            StringBuilder report = new StringBuilder();
            report.append(String.format("%-30s %-20s %-15s\n", "Book Title", "Borrowed By", "Due Date"));
            report.append("------------------------------------------------------------------\n");

            String query = "SELECT b.title, m.name, l.due_date " +
                           "FROM loans l " +
                           "JOIN books b ON l.copy_id = b.book_id " + 
                           "JOIN members m ON l.member_id = m.member_id " +
                           "WHERE l.return_date IS NULL";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    String title = rs.getString("title");
                    if (title.length() > 28) title = title.substring(0, 25) + "...";
                    
                    report.append(String.format("%-30s %-20s %-15s\n",
                            title,
                            rs.getString("name"),
                            rs.getString("due_date")));
                }
            }
            detailedReportArea.setText(report.toString());

        } catch (Exception e) {
            e.printStackTrace();
            detailedReportArea.setText("Error generating report: " + e.getMessage());
        }
    }

    private void updateCount(Connection conn, String sql, JLabel label, String prefix) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                label.setText(prefix + rs.getInt(1));
            }
        }
    }

}
