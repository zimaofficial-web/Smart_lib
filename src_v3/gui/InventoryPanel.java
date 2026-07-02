package gui;

import controller.DamLibrarySystem;
import model.Book;
import model.Journal;
import model.Magazine;
import model.LibraryItem;
import utils.IdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private DamLibrarySystem system;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;

    public InventoryPanel(DamLibrarySystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Motivational Resources Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        title.setForeground(new Color(211, 84, 0));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Type", "Title", "Author/Creator", "Year", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(25);
        inventoryTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = createButton("Add Resource");
        JButton btnDelete = createButton("Delete Selected");
        JButton btnUndo = createButton("Undo Last Action");
        JButton btnRecursion = createButton("Category Count (Recursion Demo)");
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnUndo.addActionListener(e -> {
            String res = system.undoLastCommand();
            JOptionPane.showMessageDialog(this, res);
            refresh();
        });
        btnRecursion.addActionListener(e -> demoRecursionCount());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnUndo);
        bottomPanel.add(btnRecursion);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(230, 126, 34));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Motivational Resource", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Book", "Journal", "Magazine"});
        JTextField txtTitle = new JTextField();
        JTextField txtCreator = new JTextField();
        JTextField txtYear = new JTextField();
        JTextField txtExtra1 = new JTextField();
        JTextField txtExtra2 = new JTextField();

        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Title:"));
        dialog.add(txtTitle);
        dialog.add(new JLabel("Author/Creator:"));
        dialog.add(txtCreator);
        dialog.add(new JLabel("Year:"));
        dialog.add(txtYear);
        
        JLabel lblExtra1 = new JLabel("ISBN/Field/Issue:");
        JLabel lblExtra2 = new JLabel("Genre/Frequency/Publisher:");
        dialog.add(lblExtra1);
        dialog.add(txtExtra1);
        dialog.add(lblExtra2);
        dialog.add(txtExtra2);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                String id = IdGenerator.generateResourceId();
                String title = txtTitle.getText();
                String creator = txtCreator.getText();
                int year = Integer.parseInt(txtYear.getText());
                String e1 = txtExtra1.getText();
                String e2 = txtExtra2.getText();
                
                LibraryItem res = null;
                switch (typeCombo.getSelectedIndex()) {
                    case 0: res = new Book(id, title, creator, year, e1, e2); break;
                    case 1: res = new Journal(id, title, creator, year, e1, e2); break;
                    case 2: res = new Magazine(id, title, creator, year, e1, e2); break;
                }
                
                if (res != null) {
                    system.addResource(res);
                    refresh();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Check year format.");
            }
        });

        dialog.add(new JLabel());
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = inventoryTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            system.removeResource(id);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Select a resource to delete.");
        }
    }
    
    private void demoRecursionCount() {
        String type = JOptionPane.showInputDialog(this, "Enter type to count (Book, Journal, Magazine):", "Book");
        if (type != null) {
            int count = system.tallyResourcesByCategory(type);
            JOptionPane.showMessageDialog(this, "Total " + type + "s: " + count + " (Calculated recursively)");
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (LibraryItem res : system.getInventory()) {
            tableModel.addRow(new Object[]{
                res.getId(),
                res.getType(),
                res.getTitle(),
                res.getAuthor(),
                res.getYear(),
                res.isAvailable() ? "Available" : "Rented"
            });
        }
    }
}
