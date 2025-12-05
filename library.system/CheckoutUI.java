package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutUI extends JFrame {

    private final BorrowingService borrowingService = new BorrowingService();
    private final JTextField bookIdField = new JTextField(10);
    private final JTextField memberIdField = new JTextField(10); 
    private final JButton checkoutButton = new JButton("Check Out Book");

    public CheckoutUI() {
        setTitle("Check Out Book - Librarian");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(bookIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Member ID:"), gbc); 
        gbc.gridx = 1; gbc.gridy = 1;
        add(memberIdField, gbc); 

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
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText()); 

            String result = borrowingService.checkOutBook(bookId, memberId); 

            JOptionPane.showMessageDialog(this, result,
                    "Checkout Status",
                    result.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (result.startsWith("SUCCESS")) {
                bookIdField.setText("");
                memberIdField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
