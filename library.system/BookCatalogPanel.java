package librarysystem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookCatalogPanel extends JPanel {

    private JTextField searchField;
    private JComboBox<String> searchType;
    private JTable resultTable;
    private DefaultTableModel model;
    private BookService bookService = new BookService();

    public BookCatalogPanel() {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Search Books", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchType = new JComboBox<>(new String[]{"All", "Title", "Author", "ISBN", "Category"});

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        JButton holdButton = new JButton("Place Hold");
        
        top.add(new JLabel("Search: "));
        top.add(searchField);
        top.add(searchType);
        top.add(searchBtn);
        top.add(clearBtn);
        top.add(holdButton);
        
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ISBN", "Title", "Author", "Category", "Available"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        resultTable = new JTable(model);
        resultTable.setAutoCreateRowSorter(true);

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            model.setRowCount(0);
        });
         holdButton.addActionListener(e -> {
    int row = resultTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a book first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int modelRow = resultTable.convertRowIndexToModel(row);
 
    String isbn = model.getValueAt(modelRow, 0).toString();

    Book book = null;
    try {
        java.util.List<Book> books = bookService.searchBooks(isbn, "ISBN");
        if (!books.isEmpty()) {
            book = books.get(0);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    if (book == null) {
        JOptionPane.showMessageDialog(this, "Cannot find book details.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    User current = SessionManager.getInstance().getCurrentUser();
    if (current == null) {
        JOptionPane.showMessageDialog(this, "Please log in first.", 
                "Not Logged In", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String result = ReservationService.placeHold(book.getBookId(), current.getMemberId());
    JOptionPane.showMessageDialog(this, result, 
            "Place Hold", JOptionPane.INFORMATION_MESSAGE);
});
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            public void changedUpdate(DocumentEvent e) { performSearch(); }
        });

        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && resultTable.getSelectedRow() != -1) {
                    int row = resultTable.convertRowIndexToModel(resultTable.getSelectedRow());
                    String isbn = model.getValueAt(row, 0).toString();
                    showBookDetails(isbn);
                }
            }
        });
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String type = (String) searchType.getSelectedItem();

        List<Book> books = bookService.searchBooks(keyword, type);
        model.setRowCount(0);

        for (Book b : books) {
            String avail = b.getAvailableCopies() > 0 ? "Available" : "Not Available";
            model.addRow(new Object[]{b.getIsbn(), b.getTitle(), b.getAuthor(), b.getCategory(), avail});
        }
    }

    private void showBookDetails(String isbn) {
        Book b = bookService.getBookByIsbn(isbn);

        if (b == null) {
            JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String text =
                "Title: " + b.getTitle() + "\n" +
                "Author: " + b.getAuthor() + "\n" +
                "Category: " + b.getCategory() + "\n" +
                "Year: " + b.getPublicationYear() + "\n" +
                "ISBN: " + b.getIsbn() + "\n" +
                "Total Copies: " + b.getTotalCopies() + "\n" +
                "Available: " + b.getAvailableCopies() + "\n\n" +
                "Description:\n" + (b.getDescription() == null ? "No Description" : b.getDescription());

        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.
                setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(450, 300));

        JOptionPane.showMessageDialog(this, sp, "Book Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
