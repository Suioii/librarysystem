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



        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("All Current and Past Loans", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);


        String[] columnNames = {"Loan ID", "Book Title", "Member Name", "Loan Date", "Due Date", "Status"};
        String[][] data = borrowingService.getAllLoansData();


        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable loansTable = new JTable(model);


        loansTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(loansTable);


        add(scrollPane, BorderLayout.CENTER);

        if (data.length == 0) {
            JOptionPane.showMessageDialog(this, "No loan records found in the database.", "No Data", JOptionPane.INFORMATION_MESSAGE);
        }

        setVisible(true);
    }
}
