package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class SearchBooksUI extends JFrame {

    private JTextField field;
    private DefaultTableModel model;

    public SearchBooksUI() {
        setTitle("Search Books");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        field = new JTextField();
        JButton btn = new JButton("Search");
        model = new DefaultTableModel(new String[]{"ID","ISBN","Title","Author","Category","Year"}, 0);
        JTable table = new JTable(model);

        add(new JLabel("Enter ISBN / Title / Author:"));
        add(field);
        add(btn);
        add(new JScrollPane(table));

        btn.addActionListener(e -> search());

        setVisible(true);
    }

    private void search() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "SELECT * FROM books WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + field.getText() + "%");
            pstmt.setString(2, "%" + field.getText() + "%");
            pstmt.setString(3, "%" + field.getText() + "%");

            ResultSet rs = pstmt.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("publication_year")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}
