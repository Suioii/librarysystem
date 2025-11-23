package librarysystem;

import javax.swing.*;
import java.awt.*;

public class MemberEditDialog extends JDialog {

    private MemberService memberService = new MemberService(); 
    private User member;

    private JTextField nameField;
    private JTextField emailField;
    private JCheckBox activeCheck;
    private JLabel roleLabel;

    public MemberEditDialog(JFrame parent, User member) {
        super(parent, "Edit Member", true); 
        this.member = member;

        setSize(400, 250);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        nameField = new JTextField(member.getName());
        emailField = new JTextField(member.getEmail());
        roleLabel = new JLabel(member.getRole());
        activeCheck = new JCheckBox("Active", member.isActive());

        form.add(new JLabel("Name:"));
        form.add(nameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Role:"));
        form.add(roleLabel);

        form.add(new JLabel("Status:"));
        form.add(activeCheck);

        add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            boolean newActive = activeCheck.isSelected();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Name and email cannot be empty.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean infoOk = memberService.updateMemberBasicInfo(member.getMemberId(), newName, newEmail);
            boolean activeOk = memberService.setMemberActive(member.getMemberId(), newActive);

            if (infoOk && activeOk) {
                JOptionPane.showMessageDialog(this,
                        "Member updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update member.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
