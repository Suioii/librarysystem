package librarysystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;

public class BorrowingService {

    // Define the standard loan period
    private static final int LOAN_PERIOD_DAYS = 14;

    /**
     * Calculates the due date by adding 14 days to the current date.
     */
    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, LOAN_PERIOD_DAYS);
        return cal.getTime();
    }

    /**
     * Processes the checkout of a book to a member.
     * Decrements available copies and creates a new loan record.
     */
    public String checkOutBook(int bookId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Check book availability
            String checkBookSql = "SELECT availableCopies FROM Books WHERE bookId = ?";
            PreparedStatement checkBookStmt = conn.prepareStatement(checkBookSql);
            checkBookStmt.setInt(1, bookId);
            ResultSet rs = checkBookStmt.executeQuery();

            if (!rs.next() || rs.getInt("availableCopies") <= 0) {
                return "FAIL: Book is currently not available or ID is invalid.";
            }

            // 2. Check for active loan (prevent duplicate checkout)
            String checkActiveLoanSql = "SELECT loanId FROM Loans WHERE bookId = ? AND userId = ? AND isReturned = 0";
            PreparedStatement checkLoanStmt = conn.prepareStatement(checkActiveLoanSql);
            checkLoanStmt.setInt(1, bookId);
            checkLoanStmt.setInt(2, userId);
            if (checkLoanStmt.executeQuery().next()) {
                return "FAIL: Member already has an active loan for this book.";
            }

            // 3. Insert the new loan record
            Date dueDate = calculateDueDate();
            String insertLoanSql = "INSERT INTO Loans (bookId, userId, dueDate) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertLoanSql);
            insertStmt.setInt(1, bookId);
            insertStmt.setInt(2, userId);
            insertStmt.setTimestamp(3, new Timestamp(dueDate.getTime()));
            insertStmt.executeUpdate();

            // 4. Decrement available copies in the Books table
            String updateCopiesSql = "UPDATE Books SET availableCopies = availableCopies - 1 WHERE bookId = ?";
            PreparedStatement updateCopiesStmt = conn.prepareStatement(updateCopiesSql);
            updateCopiesStmt.setInt(1, bookId);
            updateCopiesStmt.executeUpdate();

            return "SUCCESS: Book checked out. Due date: " + dueDate;

        } catch (SQLException e) {
            System.err.println("Database error during checkout: " + e.getMessage());
            return "ERROR: Database access failed. Check server connection and DB data.";
        }
    }

    /**
     * Records the return of a book by a member.
     * Updates the loan record and increments available copies.
     */
    public String returnBook(int bookId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Find the active loan
            String selectLoanSql = "SELECT loanId, dueDate FROM Loans WHERE bookId = ? AND userId = ? AND isReturned = 0";
            PreparedStatement selectLoanStmt = conn.prepareStatement(selectLoanSql);
            selectLoanStmt.setInt(1, bookId);
            selectLoanStmt.setInt(2, userId);
            ResultSet rs = selectLoanStmt.executeQuery();

            if (!rs.next()) {
                return "FAIL: No active loan found for this book and user combination.";
            }

            int loanId = rs.getInt("loanId");
            // Timestamp dueDate = rs.getTimestamp("dueDate");
            Date returnDate = new Date();
            long fine = 0;

            // 2. Update the loan record (set returnDate and isReturned=1)
            String updateLoanSql = "UPDATE Loans SET returnDate = ?, isReturned = 1 WHERE loanId = ?";
            PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanSql);
            updateLoanStmt.setTimestamp(1, new Timestamp(returnDate.getTime()));
            updateLoanStmt.setInt(2, loanId);
            updateLoanStmt.executeUpdate();

            // 3. Increment available copies in the Books table
            String updateCopiesSql = "UPDATE Books SET availableCopies = availableCopies + 1 WHERE bookId = ?";
            PreparedStatement updateCopiesStmt = conn.prepareStatement(updateCopiesSql);
            updateCopiesStmt.setInt(1, bookId);
            updateCopiesStmt.executeUpdate();

            return "SUCCESS: Book returned. Fine calculated: " + fine;

        } catch (SQLException e) {
            System.err.println("Database error during return: " + e.getMessage());
            return "ERROR: Database access failed.";
        }
    }

    /**
     * Renews an active loan, extending the due date by 14 days.
     */
    public String renewLoan(int loanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Check if the loan is active and exists
            String checkLoanSql = "SELECT bookId, dueDate FROM Loans WHERE loanId = ? AND isReturned = 0";
            PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanSql);
            checkLoanStmt.setInt(1, loanId);
            ResultSet rs = checkLoanStmt.executeQuery();

            if (!rs.next()) {
                return "FAIL: Loan ID is invalid or loan is already returned.";
            }

            // TODO: Integration point for Person 4: Check if the book has any active reservations.

            // 2. Calculate the new due date
            Date newDueDate = calculateDueDate();

            // 3. Update the dueDate column
            String updateLoanSql = "UPDATE Loans SET dueDate = ? WHERE loanId = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateLoanSql);
            updateStmt.setTimestamp(1, new Timestamp(newDueDate.getTime()));
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
        // Uses a fixed size array as a simple approach for small projects.
        String[][] data = new String[100][6];
        int row = 0;

        // SQL to join loans, books, and users tables
        String selectAllLoansSql = "SELECT l.loanId, b.title, u.name, l.loanDate, l.dueDate, l.isReturned " +
                "FROM Loans l JOIN Books b ON l.bookId = b.bookId " +
                "JOIN Users u ON l.userId = u.userId ORDER BY l.loanDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectAllLoansSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next() && row < data.length) {
                String status = rs.getBoolean("isReturned") ? "Returned" : "Active";

                // Populate row data: Loan ID, Title, Member, Loan Date, Due Date, Status
                data[row] = new String[]{
                        String.valueOf(rs.getInt("loanId")),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getTimestamp("loanDate").toString().substring(0, 10),
                        rs.getTimestamp("dueDate").toString().substring(0, 10),
                        status
                };
                row++;
            }

            // Return array resized to actual data length
            String[][] actualData = new String[row][6];
            System.arraycopy(data, 0, actualData, 0, row);
            return actualData;

        } catch (SQLException e) {
            System.err.println("Database error retrieving all loans: " + e.getMessage());
            return new String[0][0]; // Return empty array on error
        }
    }
}