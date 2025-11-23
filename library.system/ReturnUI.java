package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReturnUI extends JFrame {

    private final BorrowingService borrowingService = new BorrowingService();
    private final JTextField bookIdField = new JTextField(10);
    private final JTextField memberIdField = new JTextField(10); // CHANGED: userIdField → memberIdField
    private final JButton returnButton = new JButton("Return Book");

    public ReturnUI() {
        setTitle("Return Book - Librarian");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ADDED: Verify librarian access
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn() || !session.isLibrarian()) {
            JOptionPane.showMessageDialog(null, "Access denied: Librarian privileges required.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Book ID Input
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(bookIdField, gbc);

        // Member ID Input - CHANGED: "User ID" → "Member ID"
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Member ID:"), gbc); // CHANGED: Label text
        gbc.gridx = 1; gbc.gridy = 1;
        add(memberIdField, gbc); // CHANGED: Field variable

        // Return Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReturn();
            }
        });
        add(returnButton, gbc);

        setVisible(true);
    }

    private void handleReturn() {
        try {
            // Validate inputs are numbers
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText()); // CHANGED: userId → memberId

            // Call the core service logic
            String result = borrowingService.returnBook(bookId, memberId); // CHANGED: parameter name

            // Display result to the librarian
            JOptionPane.showMessageDialog(this, result,
                    "Return Status",
                    result.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            // Clear fields on success
            if (result.startsWith("SUCCESS")) {
                bookIdField.setText("");
                memberIdField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
