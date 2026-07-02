package gui;

import controller.DamLibrarySystem;
import model.LibraryItem;
import model.UserAccount;
import utils.IdGenerator;

import javax.swing.*;
import java.awt.*;

public class RentalsPanel extends JPanel {
    private DamLibrarySystem system;
    private MainDashboard parentDashboard;
    
    private DefaultListModel<UserAccount> userListModel;
    private JList<UserAccount> userList;
    private DefaultListModel<LibraryItem> availableResourcesModel;
    private JList<LibraryItem> availableResourcesList;
    
    private JTextArea userDetailsArea;

    public RentalsPanel(DamLibrarySystem system, MainDashboard parentDashboard) {
        this.system = system;
        this.parentDashboard = parentDashboard;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Rentals & Readers");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        title.setForeground(new Color(211, 84, 0));
        add(title, BorderLayout.NORTH);
        
        JPanel mainContent = new JPanel(new GridLayout(1, 3, 10, 10));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 1. Readers List
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBackground(Color.WHITE);
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.addListSelectionListener(e -> showUserDetails());
        
        usersPanel.add(new JLabel("Readers"), BorderLayout.NORTH);
        usersPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        
        JButton btnAddUser = createButton("Add Reader");
        btnAddUser.addActionListener(e -> addUser());
        usersPanel.add(btnAddUser, BorderLayout.SOUTH);
        
        // 2. Reader Details & Actions
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.add(new JLabel("Reader Details"), BorderLayout.NORTH);
        
        userDetailsArea = new JTextArea();
        userDetailsArea.setEditable(false);
        detailsPanel.add(new JScrollPane(userDetailsArea), BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new GridLayout(2, 1));
        JButton btnReturn = createButton("Return Selected Resource (Simulate)");
        btnReturn.addActionListener(e -> returnResource());
        
        JButton btnComputeFine = createButton("Compute Overdue Fine (Recursion)");
        btnComputeFine.addActionListener(e -> computeFine());
        
        actionPanel.add(btnReturn);
        actionPanel.add(btnComputeFine);
        detailsPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // 3. Available Resources to Rent
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBackground(Color.WHITE);
        availableResourcesModel = new DefaultListModel<>();
        availableResourcesList = new JList<>(availableResourcesModel);
        
        availablePanel.add(new JLabel("Available Resources"), BorderLayout.NORTH);
        availablePanel.add(new JScrollPane(availableResourcesList), BorderLayout.CENTER);
        
        JButton btnRent = createButton("Rent to Selected Reader");
        btnRent.addActionListener(e -> rentResource());
        availablePanel.add(btnRent, BorderLayout.SOUTH);
        
        mainContent.add(usersPanel);
        mainContent.add(detailsPanel);
        mainContent.add(availablePanel);
        
        add(mainContent, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(230, 126, 34));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
    
    private void addUser() {
        String name = JOptionPane.showInputDialog(this, "Enter Reader Name:");
        if (name != null && !name.trim().isEmpty()) {
            String email = JOptionPane.showInputDialog(this, "Enter Email:");
            UserAccount t = new UserAccount(IdGenerator.generateTravelerId(), name, email);
            system.getUsers().add(t);
            refresh();
        }
    }
    
    private void rentResource() {
        UserAccount t = userList.getSelectedValue();
        LibraryItem r = availableResourcesList.getSelectedValue();
        
        if (t != null && r != null) {
            if (r.isAvailable()) {
                r.borrow(t.getUserId());
                t.addLoan(r.getId());
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Resource not available.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a reader and an available resource.");
        }
    }
    
    private void returnResource() {
        UserAccount t = userList.getSelectedValue();
        if (t != null && !t.getActiveLoans().isEmpty()) {
            String itemId = t.getActiveLoans().get(0); // For demo, just return the first one
            LibraryItem r = system.getResourceById(itemId);
            if (r != null) {
                r.returnItem(t.getUserId());
            }
            t.returnLoan(itemId);
            refresh();
            JOptionPane.showMessageDialog(this, "Returned item: " + itemId);
        } else {
            JOptionPane.showMessageDialog(this, "No items to return for selected reader.");
        }
    }
    
    private void computeFine() {
        String daysStr = JOptionPane.showInputDialog(this, "Enter number of days overdue:");
        try {
            int days = Integer.parseInt(daysStr);
            double fine = system.computeLateFeesRecursive(days);
            JOptionPane.showMessageDialog(this, "Overdue Fine for " + days + " days is: $" + fine + "\n(Calculated recursively at $1.50/day)");
        } catch (NumberFormatException ex) {
            // Cancelled or invalid
        }
    }
    
    private void showUserDetails() {
        UserAccount t = userList.getSelectedValue();
        if (t != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Name: ").append(t.getName()).append("\n");
            sb.append("Email: ").append(t.getEmail()).append("\n");
            sb.append("ID: ").append(t.getUserId()).append("\n\n");
            sb.append("Rented Items:\n");
            for (String itemId : t.getActiveLoans()) {
                LibraryItem r = system.getResourceById(itemId);
                if (r != null) {
                    sb.append("- ").append(r.getTitle()).append("\n");
                } else {
                    sb.append("- Unknown Item (ID: ").append(itemId).append(")\n");
                }
            }
            userDetailsArea.setText(sb.toString());
        } else {
            userDetailsArea.setText("");
        }
    }
    
    public void refresh() {
        userListModel.clear();
        for (UserAccount t : system.getUsers()) {
            userListModel.addElement(t);
        }
        
        availableResourcesModel.clear();
        for (LibraryItem r : system.getInventory()) {
            if (r.isAvailable()) {
                availableResourcesModel.addElement(r);
            }
        }
        
        showUserDetails();
    }
}
