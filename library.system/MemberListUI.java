package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberListUI extends JFrame {

    private MemberService memberService = new MemberService(); 
    private JTable membersTable;
    private DefaultTableModel model;
    private JTextField searchField;

    public MemberListUI() {

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

        setTitle("Member Management - All Members");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadAllMembers();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Registered Members", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");

        topPanel.add(new JLabel("Search (Name or Email):"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(clearBtn);
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.PAGE_START);

        String[] columns = {"Member ID", "Name", "Email", "Role", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        membersTable = new JTable(model);
        membersTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(membersTable);

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editBtn = new JButton("Edit");
        JButton activateBtn = new JButton("Activate");
        JButton deactivateBtn = new JButton("Deactivate");
        
        bottomPanel.add(editBtn);
        bottomPanel.add(activateBtn);
        bottomPanel.add(deactivateBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> openEditDialog()); 
        searchBtn.addActionListener(e -> searchMembers());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadAllMembers();
        });
        refreshBtn.addActionListener(e -> loadAllMembers());

        activateBtn.addActionListener(e -> changeActiveStatus(true));
        deactivateBtn.addActionListener(e -> changeActiveStatus(false));
    }

    private void loadAllMembers() {
        model.setRowCount(0);
        List<User> members = memberService.getAllMembers(); 

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

    private void searchMembers() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMembers();
            return;
        }

        model.setRowCount(0);
        List<User> members = memberService.searchMembers(keyword); 

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No members found matching your search.",
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
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

    private void changeActiveStatus(boolean active) {
        int selected = membersTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = membersTable.convertRowIndexToModel(selected);
        int memberId = (int) model.getValueAt(modelRow, 0);
        String memberName = model.getValueAt(modelRow, 1).toString();

        String actionText = active ? "activate" : "deactivate";
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to " + actionText + " member: " + memberName + " (ID: " + memberId + ")?",
                "Confirm Action",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = memberService.setMemberActive(memberId, active); 

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Member " + memberName + " has been " + (active ? "activated" : "deactivated") + ".",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadAllMembers(); 
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update member status.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void openEditDialog() {
    int selected = membersTable.getSelectedRow();
    if (selected == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a member to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int modelRow = membersTable.convertRowIndexToModel(selected);
    int memberId = (int) model.getValueAt(modelRow, 0);

    User member = memberService.getMemberById(memberId);
    if (member == null) {
        JOptionPane.showMessageDialog(this,
                "Failed to load member details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    MemberEditDialog dialog = new MemberEditDialog(this, member);
    dialog.setVisible(true);

    loadAllMembers();
}
}

