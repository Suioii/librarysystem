package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CheckoutUI extends JFrame {

    private final BorrowingService borrowingService = new BorrowingService();
    private final JTextField bookIdField = new JTextField(10);
    private final JTextField userIdField = new JTextField(10);
    private final JButton checkoutButton = new JButton("Check Out Book");

    public CheckoutUI() {
        setTitle("Check Out Book - Librarian");
        setSize(350, 200);
        // Use DISPOSE_ON_CLOSE to only close this window, not the whole application
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ensure only Librarians can access this screen (Assumed role is "LIBRARIAN")
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

        // Checkout Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCheckout();
            }
        });
        add(checkoutButton, gbc);

        setVisible(true);
    }

    private void handleCheckout() {
        try {
            // Validate inputs are numbers
            int bookId = Integer.parseInt(bookIdField.getText());
            int userId = Integer.parseInt(userIdField.getText());

            // Call the core service logic
            String result = borrowingService.checkOutBook(bookId, userId);

            // Display result to the librarian
            JOptionPane.showMessageDialog(this, result,
                    "Checkout Status",
                    result.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }
}
