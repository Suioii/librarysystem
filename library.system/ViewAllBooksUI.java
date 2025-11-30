package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ViewAllBooksUI extends JFrame {

    public ViewAllBooksUI() {
        setTitle("All Books");
        setSize(700, 500);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID","ISBN","Title","Author","Category","Year"}, 0);

        JTable table = new JTable(model);
        add(new JScrollPane(table));

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

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
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        setVisible(true);
    }
}
