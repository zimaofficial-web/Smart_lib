package gui;

import controller.LibraryManager;
import model.*;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class AdminPanel extends JPanel {
    private LibraryManager libraryManager;
    private ViewItemsPanel viewItemsPanel;

    private JComboBox<String> typeCombo;
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    private JTextField titleField, authorField, yearField;
    private JTextField bkIsbnField, bkGenreField;
    private JTextField mgIssueField, mgPubField;
    private JTextField jnVolField, jnDoiField;

    public AdminPanel(LibraryManager libraryManager, ViewItemsPanel viewItemsPanel) {
        this.libraryManager = libraryManager;
        this.viewItemsPanel = viewItemsPanel;

        setLayout(new BorderLayout());

        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"Book", "Magazine", "Journal"});
        gbc.gridx = 1;
        addPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Author:"), gbc);
        authorField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        addPanel.add(new JLabel("Year:"), gbc);
        yearField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(yearField, gbc);

        // Dynamic fields via CardLayout (Advanced GUI tech)
        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);

        // Book fields
        JPanel bookP = new JPanel(new GridLayout(2, 2, 5, 5));
        bookP.add(new JLabel("ISBN:"));
        bkIsbnField = new JTextField();
        bookP.add(bkIsbnField);
        bookP.add(new JLabel("Genre:"));
        bkGenreField = new JTextField();
        bookP.add(bkGenreField);
        dynamicPanel.add(bookP, "Book");

        // Magazine fields
        JPanel magP = new JPanel(new GridLayout(2, 2, 5, 5));
        magP.add(new JLabel("Issue No:"));
        mgIssueField = new JTextField();
        magP.add(mgIssueField);
        magP.add(new JLabel("Publisher:"));
        mgPubField = new JTextField();
        magP.add(mgPubField);
        dynamicPanel.add(magP, "Magazine");

        // Journal fields
        JPanel jouP = new JPanel(new GridLayout(2, 2, 5, 5));
        jouP.add(new JLabel("Volume:"));
        jnVolField = new JTextField();
        jouP.add(jnVolField);
        jouP.add(new JLabel("DOI:"));
        jnDoiField = new JTextField();
        jouP.add(jnDoiField);
        dynamicPanel.add(jouP, "Journal");

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        addPanel.add(dynamicPanel, gbc);

        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                cardLayout.show(dynamicPanel, (String) e.getItem());
            }
        });

        JButton addBtn = new JButton("Add Item");
        addBtn.addActionListener(e -> addItem());
        gbc.gridx = 0; gbc.gridy = 5;
        addPanel.add(addBtn, gbc);

        add(addPanel, BorderLayout.WEST);

        // Control Panel
        JPanel ctrlPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        ctrlPanel.setBorder(BorderFactory.createTitledBorder("Controls & Reports"));
        
        JButton undoBtn = new JButton("Undo Last Add/Delete");
        undoBtn.addActionListener(e -> {
            String res = libraryManager.undoLastAction();
            JOptionPane.showMessageDialog(this, res);
            viewItemsPanel.refreshTable();
        });

        JButton delBtn = new JButton("Delete Item by ID");
        delBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Item ID to delete:");
            if (id != null && !id.trim().isEmpty()) {
                libraryManager.deleteItem(id.trim());
                viewItemsPanel.refreshTable();
            }
        });

        JButton repBtn = new JButton("Generate Report (Recursive)");
        repBtn.addActionListener(e -> {
            int bk = libraryManager.countByCategory("Book");
            int mg = libraryManager.countByCategory("Magazine");
            int jn = libraryManager.countByCategory("Journal");
            JOptionPane.showMessageDialog(this, 
                "Category Distribution (Recursive Count):\nBooks: " + bk + "\nMagazines: " + mg + "\nJournals: " + jn,
                "Report", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> {
             String n = JOptionPane.showInputDialog(this, "Name:");
             String m = JOptionPane.showInputDialog(this, "Email:");
             if (n != null && m != null) {
                 String uid = IDGenerator.generateUserId();
                 libraryManager.getUsers().add(new UserAccount(uid, n, m));
                 JOptionPane.showMessageDialog(this, "Added user: " + uid);
             }
        });

        ctrlPanel.add(addUserBtn);
        ctrlPanel.add(delBtn);
        ctrlPanel.add(undoBtn);
        ctrlPanel.add(repBtn);

        add(ctrlPanel, BorderLayout.CENTER);
    }

    private void addItem() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String id = IDGenerator.generateItemId(type);

            LibraryItem item = null;
            if (type.equals("Book")) {
                item = new Book(id, title, author, year, bkIsbnField.getText(), bkGenreField.getText());
            } else if (type.equals("Magazine")) {
                item = new Magazine(id, title, author, year, mgIssueField.getText(), mgPubField.getText());
            } else if (type.equals("Journal")) {
                item = new Journal(id, title, author, year, jnVolField.getText(), jnDoiField.getText());
            }

            if (item != null) {
                libraryManager.addItem(item);
                JOptionPane.showMessageDialog(this, "Added successfully! ID: " + id);
                viewItemsPanel.refreshTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
