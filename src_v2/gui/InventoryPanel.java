package gui;

import controller.TravelLibrarySystem;
import model.*;
import utils.IdGenerator;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private TravelLibrarySystem system;
    private JTable table;
    private InventoryTableModel tableModel;

    public InventoryPanel(TravelLibrarySystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        // Top toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Resource");
        JButton btnRemove = new JButton("Remove Selected");
        JButton btnUndo = new JButton("Undo Last Action");
        
        toolbar.add(btnAdd);
        toolbar.add(btnRemove);
        toolbar.add(btnUndo);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Table
        tableModel = new InventoryTableModel(system.getInventory());
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Actions
        btnAdd.addActionListener(e -> showAddDialog());
        btnRemove.addActionListener(e -> removeSelected());
        btnUndo.addActionListener(e -> {
            String msg = system.undoLastCommand();
            JOptionPane.showMessageDialog(this, msg);
            refresh();
        });
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Resource", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        String[] types = {"Travel Guide", "Travel Map", "Travel Magazine"};
        JComboBox<String> cbType = new JComboBox<>(types);
        
        JTextField txtTitle = new JTextField();
        JTextField txtCreator = new JTextField();
        JTextField txtYear = new JTextField();
        JTextField txtE1 = new JTextField();
        JTextField txtE2 = new JTextField();
        
        dialog.add(new JLabel("Type:")); dialog.add(cbType);
        dialog.add(new JLabel("Title:")); dialog.add(txtTitle);
        dialog.add(new JLabel("Creator/Author:")); dialog.add(txtCreator);
        dialog.add(new JLabel("Year:")); dialog.add(txtYear);
        
        JLabel lblE1 = new JLabel("Country/Region:");
        JLabel lblE2 = new JLabel("Audience:");
        dialog.add(lblE1); dialog.add(txtE1);
        dialog.add(lblE2); dialog.add(txtE2);
        
        cbType.addActionListener(e -> {
            int idx = cbType.getSelectedIndex();
            if (idx == 0) { lblE1.setText("Country/Region:"); lblE2.setText("Audience:"); }
            else if (idx == 1) { lblE1.setText("Region:"); lblE2.setText("Scale:"); }
            else if (idx == 2) { lblE1.setText("Issue Month:"); lblE2.setText("Topic:"); }
        });
        
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                String title = txtTitle.getText();
                String creator = txtCreator.getText();
                int year = Integer.parseInt(txtYear.getText());
                String e1 = txtE1.getText();
                String e2 = txtE2.getText();
                String id = IdGenerator.generateResourceId();
                
                TravelResource res = null;
                switch (cbType.getSelectedIndex()) {
                    case 0: res = new TravelGuide(id, title, creator, year, e1, e2); break;
                    case 1: res = new TravelMap(id, title, creator, year, e1, e2); break;
                    case 2: res = new TravelMagazine(id, title, creator, year, e1, e2); break;
                }
                
                system.addResource(res);
                refresh();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Check year.");
            }
        });
        
        dialog.add(new JLabel()); dialog.add(btnSave);
        dialog.setVisible(true);
    }
    
    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            system.removeResource(id);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to remove.");
        }
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    // Inner class for Table Model
    class InventoryTableModel extends AbstractTableModel {
        private String[] columns = {"ID", "Type", "Title", "Creator", "Year", "Availability", "Extra Info"};
        private List<TravelResource> list;

        public InventoryTableModel(List<TravelResource> list) { this.list = list; }

        @Override public int getRowCount() { return list.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            TravelResource res = list.get(row);
            switch (col) {
                case 0: return res.getId();
                case 1: return res.getResourceType();
                case 2: return res.getTitle();
                case 3: return res.getCreator();
                case 4: return res.getPublicationYear();
                case 5: return res.isAvailable() ? "Available" : "Rented";
                case 6: return res.getExtraLabel1() + ": " + res.getExtraAttribute1() + " | " + 
                               res.getExtraLabel2() + ": " + res.getExtraAttribute2();
                default: return null;
            }
        }
    }
}
