package librarysystem;

import javax.swing.*;
import java.awt.*;

public class ManageBookCopiesUI extends JFrame {

    public ManageBookCopiesUI() {
        setTitle("Manage Book Copies");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Manage Book Copies");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label);

        setVisible(true);
    }
}
