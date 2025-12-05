package librarysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BorrowingService {

    private static final int LOAN_PERIOD_DAYS = 14;

    
    public String checkOutBook(int bookId, int memberId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // 1. Find an available copy of the book
            String findCopySql = "SELECT copy_id FROM copies WHERE book_id = ? AND status = 'AVAILABLE' LIMIT 1";
            PreparedStatement findCopyStmt = conn.prepareStatement(findCopySql);
            findCopyStmt.setInt(1, bookId);
            ResultSet copyRs = findCopyStmt.executeQuery();
            
            if (!copyRs.next()) {
                return "FAIL: No available copies of this book.";
            }
            
            int copyId = copyRs.getInt("copy_id");
            
            // 2. Check if member already has this book checked out
            String checkActiveLoanSql = "SELECT loan_id FROM loans l " +
                                      "JOIN copies c ON l.copy_id = c.copy_id " +
                                      "WHERE c.book_id = ? AND l.member_id = ? AND l.return_date IS NULL";
            PreparedStatement checkLoanStmt = conn.prepareStatement(checkActiveLoanSql);
            checkLoanStmt.setInt(1, bookId);
            checkLoanStmt.setInt(2, memberId);
            if (checkLoanStmt.executeQuery().next()) {
                return "FAIL: Member already has this book checked out.";
            }
            
            // 3. Create the loan record
            Date dueDate = calculateDueDate();
            String insertLoanSql = "INSERT INTO loans (copy_id, member_id, checkout_date, due_date) VALUES (?, ?, CURRENT_DATE, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertLoanSql);
            insertStmt.setInt(1, copyId);
            insertStmt.setInt(2, memberId);
            insertStmt.setDate(3, new java.sql.Date(dueDate.getTime()));
            insertStmt.executeUpdate();
            
            // 4. Update copy status
            String updateCopySql = "UPDATE copies SET status = 'CHECKED_OUT' WHERE copy_id = ?";
            PreparedStatement updateCopyStmt = conn.prepareStatement(updateCopySql);
            updateCopyStmt.setInt(1, copyId);
            updateCopyStmt.executeUpdate();
            
            return "SUCCESS: Book checked out. Due date: " + dueDate;
            
        } catch (SQLException e) {
            System.err.println("Database error during checkout: " + e.getMessage());
            return "ERROR: Database access failed.";
        }
    }
    
   
    public String returnBook(int bookId, int memberId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // 1. Find the active loan
            String findLoanSql = "SELECT l.loan_id, l.copy_id FROM loans l " +
                               "JOIN copies c ON l.copy_id = c.copy_id " +
                               "WHERE c.book_id = ? AND l.member_id = ? AND l.return_date IS NULL";
            PreparedStatement findLoanStmt = conn.prepareStatement(findLoanSql);
            findLoanStmt.setInt(1, bookId);
            findLoanStmt.setInt(2, memberId);
            ResultSet loanRs = findLoanStmt.executeQuery();
            
            if (!loanRs.next()) {
                return "FAIL: No active loan found for this book and member.";
            }
            
            int loanId = loanRs.getInt("loan_id");
            int copyId = loanRs.getInt("copy_id");
            
            // 2. Update loan with return date
            String updateLoanSql = "UPDATE loans SET return_date = CURRENT_DATE WHERE loan_id = ?";
            PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanSql);
            updateLoanStmt.setInt(1, loanId);
            updateLoanStmt.executeUpdate();
            
            // 3. Update copy status back to available
            String updateCopySql = "UPDATE copies SET status = 'AVAILABLE' WHERE copy_id = ?";
            PreparedStatement updateCopyStmt = conn.prepareStatement(updateCopySql);
            updateCopyStmt.setInt(1, copyId);
            updateCopyStmt.executeUpdate();
            
            Hold nextHold = ReservationService.promoteNextHoldToReady(bookId);
            if (nextHold != null) {
              System.out.println("Next hold for book #" + bookId +
                       " is now READY for member #" + nextHold.getMemberId());
              }
            
            return "SUCCESS: Book returned successfully.";
            
        } catch (SQLException e) {
            System.err.println("Database error during return: " + e.getMessage());
            return "ERROR: Database access failed.";
        }
    }
    
    
    public String renewLoan(int loanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // 1. Check if loan exists and is active
            String checkLoanSql = "SELECT due_date FROM loans WHERE loan_id = ? AND return_date IS NULL";
            PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanSql);
            checkLoanStmt.setInt(1, loanId);
            ResultSet rs = checkLoanStmt.executeQuery();
            
            if (!rs.next()) {
                return "FAIL: Loan ID is invalid or book already returned.";
            }
            
            // 2. Calculate new due date
            Date currentDueDate = rs.getDate("due_date");
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDueDate);
            cal.add(Calendar.DAY_OF_YEAR, LOAN_PERIOD_DAYS);
            Date newDueDate = cal.getTime();
            
            // 3. Update due date
            String updateLoanSql = "UPDATE loans SET due_date = ?, renewed_count = renewed_count + 1 WHERE loan_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateLoanSql);
            updateStmt.setDate(1, new java.sql.Date(newDueDate.getTime()));
            updateStmt.setInt(2, loanId);
            
            if (updateStmt.executeUpdate() > 0) {
                return "SUCCESS: Loan renewed. New due date: " + newDueDate;
            } else {
                return "FAIL: Could not renew loan.";
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during renewal: " + e.getMessage());
            return "ERROR: Database access failed.";
        }
    }
    
   
    public String[][] getAllLoansData() {
        List<String[]> loanData = new ArrayList<>();
        
        String sql = "SELECT l.loan_id, b.title, m.name, l.checkout_date, l.due_date, " +
                    "CASE WHEN l.return_date IS NULL THEN 'Active' ELSE 'Returned' END as status " +
                    "FROM loans l " +
                    "JOIN copies c ON l.copy_id = c.copy_id " +
                    "JOIN books b ON c.book_id = b.book_id " +
                    "JOIN members m ON l.member_id = m.member_id " +
                    "ORDER BY l.checkout_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("loan_id")),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getDate("checkout_date").toString(),
                    rs.getDate("due_date").toString(),
                    rs.getString("status")
                };
                loanData.add(row);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error retrieving loans: " + e.getMessage());
        }
        
        return loanData.toArray(new String[0][]);
    }
    
    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, LOAN_PERIOD_DAYS);
        return cal.getTime();
    }
}
