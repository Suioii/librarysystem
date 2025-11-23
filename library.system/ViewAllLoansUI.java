package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewAllLoansUI extends JFrame {

    private final BorrowingService borrowingService = new BorrowingService();

    public ViewAllLoansUI() {
        setTitle("View All Loans - Librarian");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ADDED: Verify librarian access
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn() || !session.isLibrarian()) {
            JOptionPane.showMessageDialog(null, "Access denied: Librarian privileges required.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("All Current and Past Loans", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshLoansTable());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshLoansTable(); // Load initial data

        setVisible(true);
    }

    private void refreshLoansTable() {
        String[] columnNames = {"Loan ID", "Book Title", "Member Name", "Loan Date", "Due Date", "Status"};
        String[][] data = borrowingService.getAllLoansData();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        JTable loansTable = new JTable(model);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // ADDED: Better table styling
        loansTable.setRowHeight(25);
        loansTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(loansTable);
        
        // Replace existing table with refreshed one
        getContentPane().removeAll();
        
        JLabel titleLabel = new JLabel("All Current and Past Loans", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshLoansTable());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        if (data.length == 0) {
            JOptionPane.showMessageDialog(this, "No loan records found in the database.", "No Data", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
