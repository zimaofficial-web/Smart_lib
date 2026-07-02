package gui;

import controller.TravelLibrarySystem;
import model.TravelResource;
import model.Traveler;

import javax.swing.*;
import java.awt.*;

public class RentalsPanel extends JPanel {
    
    private TravelLibrarySystem system;
    private MainDashboard dashboard;
    
    private JComboBox<Traveler> cbTravelers;
    private DefaultListModel<TravelResource> availableModel;
    private JList<TravelResource> availableList;
    private DefaultListModel<TravelResource> rentedModel;
    private JList<TravelResource> rentedList;

    public RentalsPanel(TravelLibrarySystem system, MainDashboard dashboard) {
        this.system = system;
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top: Traveler Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Traveler:"));
        cbTravelers = new JComboBox<>();
        for (Traveler t : system.getTravelers()) {
            cbTravelers.addItem(t);
        }
        cbTravelers.addActionListener(e -> refreshLists());
        topPanel.add(cbTravelers);
        
        JButton calculateFeeBtn = new JButton("Calculate Late Fee (1 day)");
        calculateFeeBtn.addActionListener(e -> {
             double fee = system.computeLateFeesRecursive(1);
             JOptionPane.showMessageDialog(this, "Late fee for 1 day is: $" + String.format("%.2f", fee));
        });
        topPanel.add(calculateFeeBtn);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center: Dual lists
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Left: Available Resources
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Resources"));
        availableModel = new DefaultListModel<>();
        availableList = new JList<>(availableModel);
        leftPanel.add(new JScrollPane(availableList), BorderLayout.CENTER);
        JButton btnRent = new JButton("Rent Selected ->");
        btnRent.addActionListener(e -> rentSelected());
        leftPanel.add(btnRent, BorderLayout.SOUTH);
        
        // Right: Rented by Traveler
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Currently Rented by Traveler"));
        rentedModel = new DefaultListModel<>();
        rentedList = new JList<>(rentedModel);
        rightPanel.add(new JScrollPane(rentedList), BorderLayout.CENTER);
        JButton btnReturn = new JButton("<- Return Selected");
        btnReturn.addActionListener(e -> returnSelected());
        rightPanel.add(btnReturn, BorderLayout.SOUTH);
        
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        refreshLists();
    }
    
    private void rentSelected() {
        TravelResource res = availableList.getSelectedValue();
        Traveler traveler = (Traveler) cbTravelers.getSelectedItem();
        
        if (res != null && traveler != null) {
            if (res.rentItem(traveler.getTravelerId())) {
                traveler.addRentedItem(res);
                refreshLists();
            } else {
                JOptionPane.showMessageDialog(this, "Item is not available.");
            }
        }
    }
    
    private void returnSelected() {
        TravelResource res = rentedList.getSelectedValue();
        Traveler traveler = (Traveler) cbTravelers.getSelectedItem();
        
        if (res != null && traveler != null) {
            if (res.returnItem(traveler.getTravelerId())) {
                traveler.removeRentedItem(res);
                refreshLists();
            }
        }
    }
    
    public void refresh() {
        // Refresh travelers dropdown just in case
        cbTravelers.removeAllItems();
        for (Traveler t : system.getTravelers()) {
            cbTravelers.addItem(t);
        }
        refreshLists();
    }
    
    private void refreshLists() {
        availableModel.clear();
        for (TravelResource r : system.getInventory()) {
            if (r.isAvailable()) {
                availableModel.addElement(r);
            }
        }
        
        rentedModel.clear();
        Traveler traveler = (Traveler) cbTravelers.getSelectedItem();
        if (traveler != null) {
            for (TravelResource r : traveler.getRentedItems()) {
                rentedModel.addElement(r);
            }
        }
    }
}
