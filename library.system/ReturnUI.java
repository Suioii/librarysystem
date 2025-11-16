package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ReturnUI extends JFrame {

    private final BorrowingService borrowingService = new BorrowingService();
    private final JTextField bookIdField = new JTextField(10);
    private final JTextField userIdField = new JTextField(10);
    private final JButton returnButton = new JButton("Return Book");

    public ReturnUI() {
        setTitle("Return Book - Librarian");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ensure only Librarians can access this screen
        // SessionManager.getInstance().validateAccess("LIBRARIAN");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Book ID Input
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(bookIdField, gbc);

        // Member ID Input
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(userIdField, gbc);

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
            int userId = Integer.parseInt(userIdField.getText());

            // Call the core service logic
            String result = borrowingService.returnBook(bookId, userId);

            // Display result to the librarian
            JOptionPane.showMessageDialog(this, result,
                    "Return Status",
                    result.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }
}
