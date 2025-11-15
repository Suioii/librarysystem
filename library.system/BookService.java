
package librarysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    public List<Book> searchBooks(String keyword, String type) {
        List<Book> results = new ArrayList<>();

        String queryBase =
                "SELECT book_id, isbn, title, author, category, publication_year, description, " +
                " (SELECT COUNT(*) FROM copies c WHERE c.book_id = b.book_id AND c.status='AVAILABLE') AS available_copies, " +
                " (SELECT COUNT(*) FROM copies c2 WHERE c2.book_id = b.book_id) AS total_copies " +
                " FROM books b ";

        String where = "";
        if (keyword == null) keyword = "";
        keyword = keyword.trim();

        if (!keyword.isEmpty()) {
            if ("All".equalsIgnoreCase(type)) {
                where = " WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR category LIKE ?";
            } else {
                String col = switch (type) {
                    case "Title" -> "title";
                    case "Author" -> "author";
                    case "ISBN" -> "isbn";
                    default -> "category";
                };
                where = " WHERE " + col + " LIKE ?";
            }
        }

        String finalQuery = queryBase + where + " ORDER BY title LIMIT 500";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(finalQuery)) {

            if (!where.isEmpty()) {
                String p = "%" + keyword + "%";
                if (where.contains("OR")) {
                    ps.setString(1, p);
                    ps.setString(2, p);
                    ps.setString(3, p);
                    ps.setString(4, p);
                } else {
                    ps.setString(1, p);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(
                        new Book(
                                rs.getInt("book_id"),
                                rs.getString("isbn"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("category"),
                                rs.getInt("publication_year"),
                                rs.getString("description"),
                                rs.getInt("total_copies"),
                                rs.getInt("available_copies")
                        )
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    public Book getBookByIsbn(String isbn) {

        String q =
                "SELECT book_id, isbn, title, author, category, publication_year, description, " +
                " (SELECT COUNT(*) FROM copies c WHERE c.book_id = b.book_id AND c.status='AVAILABLE') AS available_copies, " +
                " (SELECT COUNT(*) FROM copies c2 WHERE c2.book_id = b.book_id) AS total_copies " +
                " FROM books b WHERE isbn = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setString(1, isbn);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getInt("book_id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("publication_year"),
                        rs.getString("description"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}


