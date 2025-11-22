package librarysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberService {

    public List<User> getAllMembers() {
        List<User> members = new ArrayList<>();
        String sql = "SELECT member_id, name, email, role, is_active FROM members";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int memberId = rs.getInt("member_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");

                User u = new User(memberId, name, email, role, isActive);
                members.add(u);
            }

        } catch (SQLException e) {
            System.err.println("Error loading members: " + e.getMessage());
        }

        return members;
    }

    public List<User> searchMembers(String keyword) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT member_id, name, email, role, is_active " +
                     "FROM members " +
                     "WHERE name LIKE ? OR email LIKE ?";

        String like = "%" + keyword + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, like);
            stmt.setString(2, like);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int memberId = rs.getInt("member_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");

                members.add(new User(memberId, name, email, role, isActive));
            }

        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }

        return members;
    }

    public User getMemberById(int memberId) {
        String sql = "SELECT member_id, name, email, role, is_active FROM members WHERE member_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");

                return new User(memberId, name, email, role, isActive);
            }

        } catch (SQLException e) {
            System.err.println("Error loading member by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean setMemberActive(int memberId, boolean active) {
        String sql = "UPDATE members SET is_active = ? WHERE member_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, memberId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member active status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMemberBasicInfo(int memberId, String name, String email) {
        String sql = "UPDATE members SET name = ?, email = ? WHERE member_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, memberId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member info: " + e.getMessage());
            return false;
        }
    }
}
