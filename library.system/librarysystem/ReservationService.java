package librarysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    
    public static String placeHold(int bookId, int memberId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String bookSql =
                    "SELECT " +
                    " (SELECT COUNT(*) FROM copies c WHERE c.book_id = b.book_id AND c.status='AVAILABLE') AS available_copies " +
                    "FROM books b WHERE b.book_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(bookSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "FAIL: Book not found.";
                    }
                    int available = rs.getInt("available_copies");
                    if (available > 0) {
                        return "INFO: Book is currently available. You can check it out instead of placing a hold.";
                    }
                }
            }

            String duplicateSql =
                    "SELECT COUNT(*) FROM holds " +
                    "WHERE book_id = ? AND member_id = ? AND status IN ('PENDING','READY')";
            try (PreparedStatement ps = conn.prepareStatement(duplicateSql)) {
                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        return "FAIL: You already have an active hold for this book.";
                    }
                }
            }

            String insertSql =
                    "INSERT INTO holds (book_id, member_id, status, notification_sent) " +
                    "VALUES (?, ?, 'PENDING', FALSE)";
            int newId;
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        newId = keys.getInt(1);
                    } else {
                        return "FAIL: Could not create hold.";
                    }
                }
            }

            int position = getQueuePositionForHold(conn, newId);

            return "SUCCESS: Hold placed successfully. Your position in queue is #" + position;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return "FAIL: Database error while placing hold.";
        }
    }

        private static int getQueuePositionForHold(Connection conn, int holdId) throws SQLException {
        String sql =
                "SELECT COUNT(*) AS position " +
                "FROM holds h2 " +
                "WHERE h2.book_id = (SELECT book_id FROM holds WHERE hold_id = ?) " +
                "AND h2.status = 'PENDING' " +
                "AND h2.place_date <= (SELECT place_date FROM holds WHERE hold_id = ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, holdId);
            ps.setInt(2, holdId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("position");
                }
            }
        }
        return -1;
    }

    
    public static boolean cancelHold(int holdId, int memberId) {
        String sql = "UPDATE holds SET status='CANCELLED' " +
                     "WHERE hold_id=? AND member_id=? AND status IN ('PENDING','READY')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, holdId);
            ps.setInt(2, memberId);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

   
    public static Hold promoteNextHoldToReady(int bookId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String selectSql =
                    "SELECT hold_id, member_id, place_date, status, notification_sent " +
                    "FROM holds " +
                    "WHERE book_id=? AND status='PENDING' " +
                    "ORDER BY place_date ASC " +
                    "LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null; 
                    }

                    int holdId = rs.getInt("hold_id");
                    int memberId = rs.getInt("member_id");
                    Timestamp placeDate = rs.getTimestamp("place_date");
                    String status = rs.getString("status");
                    boolean notif = rs.getBoolean("notification_sent");

                    String updateSql = "UPDATE holds SET status='READY' WHERE hold_id=?";
                    try (PreparedStatement ups = conn.prepareStatement(updateSql)) {
                        ups.setInt(1, holdId);
                        ups.executeUpdate();
                    }

                    int pos = getQueuePositionForHold(conn, holdId);

                    return new Hold(holdId, bookId, memberId, placeDate, status, notif, pos);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

   
    public static List<Hold> getQueueForBook(int bookId) {
        List<Hold> holds = new ArrayList<>();

        String sql =
                "SELECT hold_id, member_id, place_date, status, notification_sent, " +
                "       ROW_NUMBER() OVER (ORDER BY place_date ASC) AS queue_pos " +
                "FROM holds WHERE book_id=? AND status IN ('PENDING','READY') " +
                "ORDER BY place_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    holds.add(
                            new Hold(
                                    rs.getInt("hold_id"),
                                    bookId,
                                    rs.getInt("member_id"),
                                    rs.getTimestamp("place_date"),
                                    rs.getString("status"),
                                    rs.getBoolean("notification_sent"),
                                    rs.getInt("queue_pos")
                            )
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return holds;
    }
    // PERSON4: إرجاع بيانات الحجوزات لعضو معيّن على شكل جدول
public static String[][] getHoldsForMemberTableData(int memberId) {
    java.util.List<String[]> rows = new java.util.ArrayList<>();

    String sql = "SELECT h.hold_id, b.title, h.status, h.place_date " +
                 "FROM holds h " +
                 "JOIN books b ON h.book_id = b.book_id " +
                 "WHERE h.member_id = ? " +
                 "ORDER BY h.place_date DESC";

    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, memberId);

        try (java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String holdId = String.valueOf(rs.getInt("hold_id"));
                String title  = rs.getString("title");
                String status = rs.getString("status");
                String placed = String.valueOf(rs.getTimestamp("place_date"));

                rows.add(new String[]{holdId, title, status, placed});
            }
        }

    } catch (java.sql.SQLException e) {
        System.err.println("Error loading holds for member: " + e.getMessage());
    }

    return rows.toArray(new String[0][]);
}

}
