package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class ManageBooksUI extends JFrame {

    private JTextField isbnField, titleField, authorField, categoryField, yearField;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public ManageBooksUI() {
        setTitle("Manage Books");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        inputPanel.add(isbnField);

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Publication Year:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        JButton addBtn = new JButton("Add Book");
        JButton deleteBtn = new JButton("Delete Selected");
        
        inputPanel.add(addBtn);
        inputPanel.add(deleteBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Table to show books
        String[] columns = {"ID", "ISBN", "Title", "Author", "Category", "Year"};
        tableModel = new DefaultTableModel(columns, 0);
        booksTable = new JTable(tableModel);
        add(new JScrollPane(booksTable), BorderLayout.CENTER);

        // Load data immediately when window opens
        loadBooksData();

        // Button Actions
        addBtn.addActionListener(e -> addBook());
        deleteBtn.addActionListener(e -> deleteBook());
    }

    private void loadBooksData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            tableModel.setRowCount(0); // Clear table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("book_id"));
                row.add(rs.getString("isbn"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getString("category"));
                row.add(rs.getInt("publication_year"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void addBook() {
        String sql = "INSERT INTO books (isbn, title, author, category, publication_year) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbnField.getText());
            pstmt.setString(2, titleField.getText());
            pstmt.setString(3, authorField.getText());
            pstmt.setString(4, categoryField.getText());
            pstmt.setInt(5, Integer.parseInt(yearField.getText()));
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book Added Successfully!");
            
            // Clear fields and reload table
            isbnField.setText("");
            titleField.setText("");
            authorField.setText("");
            categoryField.setText("");
            yearField.setText("");
            loadBooksData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book Deleted!");
            loadBooksData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage());
        }
    }
}
