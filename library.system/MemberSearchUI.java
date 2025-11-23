package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberSearchUI extends JFrame {

    private MemberService memberService = new MemberService(); // PERSON4
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel model;

    public MemberSearchUI() {

        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn() || !session.getCurrentUser().isLibrarian()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Access denied. Librarian only.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Search Members");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        
        JLabel title = new JLabel("Search Members by Name or Email", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(clearBtn);

        add(topPanel, BorderLayout.PAGE_START);

        String[] cols = {"Member ID", "Name", "Email", "Role", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        resultsTable = new JTable(model);
        resultsTable.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(resultsTable);
        add(scroll, BorderLayout.CENTER);

        // الأحداث
        searchBtn.addActionListener(e -> doSearch());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            model.setRowCount(0);
        });
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        model.setRowCount(0);

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name or email to search.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<User> members = memberService.searchMembers(keyword);

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No members found.",
                    "Result",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (User u : members) {
            model.addRow(new Object[]{
                    u.getMemberId(),
                    u.getName(),
                    u.getEmail(),
                    u.getRole(),
                    u.isActive() ? "ACTIVE" : "INACTIVE"
            });
        }
    }
}
