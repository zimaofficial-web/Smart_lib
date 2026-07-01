package gui;

import controller.LibraryManager;
import model.LibraryItem;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class ViewItemsPanel extends JPanel {
    private LibraryManager libraryManager;
    private JTable table;
    private ItemTableModel tableModel;

    public ViewItemsPanel(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setToolTipText("Reload data from memory");
        refreshBtn.addActionListener(e -> refreshTable());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new ItemTableModel(libraryManager.getCatalogue());
        table = new JTable(tableModel);
        
        // Custom cell renderer requirement
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 4);
                if (status.equals("Available")) {
                    c.setBackground(new Color(230, 255, 230));
                } else {
                    c.setBackground(new Color(255, 230, 230));
                }
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });
        
        table.getTableHeader().setToolTipText("Click on Search & Sort tab to sort rows");

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setItems(libraryManager.getCatalogue());
    }
}

class ItemTableModel extends AbstractTableModel {
    private String[] columnNames = {"ID", "Type", "Title", "Author", "Status", "Borrowed By"};
    private ArrayList<LibraryItem> items;

    public ItemTableModel(ArrayList<LibraryItem> items) {
        this.items = new ArrayList<>(items);
    }

    public void setItems(ArrayList<LibraryItem> items) {
        this.items = new ArrayList<>(items);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return items.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public String getColumnName(int col) { return columnNames[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        LibraryItem item = items.get(row);
        switch (col) {
            case 0: return item.getId();
            case 1: return item.getType();
            case 2: return item.getTitle();
            case 3: return item.getAuthor();
            case 4: return item.isAvailable() ? "Available" : "Borrowed";
            case 5: return item.getBorrowedBy();
            default: return null;
        }
    }
}
