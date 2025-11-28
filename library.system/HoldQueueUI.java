package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HoldQueueUI extends JFrame {

    private JTextField bookIdField;
    private JTable table;
    private DefaultTableModel model;

    public HoldQueueUI() {
        setTitle("Hold Queue Management");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookIdField = new JTextField(8);
        JButton loadBtn = new JButton("Load Queue");

        top.add(new JLabel("Book ID:"));
        top.add(bookIdField);
        top.add(loadBtn);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Queue Pos", "Hold ID", "Member ID", "Placed At", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(24);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> loadQueue());
    }

    private void loadQueue() {
        String txt = bookIdField.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a book ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(txt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid book ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Hold> queue = ReservationService.getQueueForBook(bookId);
        model.setRowCount(0);

        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No holds for this book.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Hold h : queue) {
            model.addRow(new Object[]{
                    h.getQueuePosition(), 
                    h.getHoldId(),        
                    h.getMemberId(),   
                    h.getPlaceDate(),     
                    h.getStatus()         
            });
        }
    }
}

