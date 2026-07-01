package gui;

import controller.LibraryManager;
import controller.BorrowController;

import javax.swing.*;
import java.awt.*;

public class BorrowPanel extends JPanel {
    private LibraryManager libraryManager;
    private BorrowController borrowController;
    private ViewItemsPanel viewItemsPanel;

    private JTextField itemIdField;
    private JTextField userIdField;

    public BorrowPanel(LibraryManager libraryManager, BorrowController borrowController, ViewItemsPanel viewItemsPanel) {
        this.libraryManager = libraryManager;
        this.borrowController = borrowController;
        this.viewItemsPanel = viewItemsPanel;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("User ID:"), gbc);
        userIdField = new JTextField(15);
        gbc.gridx = 1;
        add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Item ID:"), gbc);
        itemIdField = new JTextField(15);
        gbc.gridx = 1;
        add(itemIdField, gbc);

        JPanel btnPanel = new JPanel();
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");

        borrowBtn.addActionListener(e -> handleBorrow());
        returnBtn.addActionListener(e -> handleReturn());

        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void handleBorrow() {
        String uid = userIdField.getText().trim();
        String iid = itemIdField.getText().trim();
        if (uid.isEmpty() || iid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Item ID", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = borrowController.borrowItem(iid, uid);
        JOptionPane.showMessageDialog(this, result, "Borrow Status", JOptionPane.INFORMATION_MESSAGE);
        viewItemsPanel.refreshTable();
    }

    private void handleReturn() {
        String uid = userIdField.getText().trim();
        String iid = itemIdField.getText().trim();
        if (uid.isEmpty() || iid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Item ID", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = borrowController.returnItem(iid, uid);
        JOptionPane.showMessageDialog(this, result, "Return Status", JOptionPane.INFORMATION_MESSAGE);
        viewItemsPanel.refreshTable();
    }
}
