package gui;

import controller.DamLibrarySystem;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    
    private DamLibrarySystem system;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    
    // Panels
    private InventoryPanel inventoryPanel;
    private RentalsPanel rentalsPanel;
    private OverviewPanel overviewPanel;

    public MainDashboard(DamLibrarySystem system) {
        this.system = system;
        
        setTitle("Dam's lib");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // Side Navigation Panel
        JPanel sideNav = new JPanel();
        sideNav.setLayout(new BoxLayout(sideNav, BoxLayout.Y_AXIS));
        sideNav.setBackground(new Color(211, 84, 0)); // Dark Orange
        sideNav.setPreferredSize(new Dimension(200, getHeight()));
        
        JLabel brandLabel = new JLabel("Dam's lib");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        sideNav.add(brandLabel);
        
        JLabel subtitleLabel = new JLabel("Motivational Library");
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(253, 227, 167)); // Light Gold/Cream
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 30, 10));
        sideNav.add(subtitleLabel);
        
        // Navigation Buttons
        JButton btnOverview = createNavButton("Overview");
        JButton btnInventory = createNavButton("Inventory");
        JButton btnRentals = createNavButton("Rentals");
        
        sideNav.add(btnOverview);
        sideNav.add(Box.createRigidArea(new Dimension(0, 5)));
        sideNav.add(btnInventory);
        sideNav.add(Box.createRigidArea(new Dimension(0, 5)));
        sideNav.add(btnRentals);
        
        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        overviewPanel = new OverviewPanel(system);
        inventoryPanel = new InventoryPanel(system);
        rentalsPanel = new RentalsPanel(system, this);
        
        mainContentPanel.add(overviewPanel, "Overview");
        mainContentPanel.add(inventoryPanel, "Inventory");
        mainContentPanel.add(rentalsPanel, "Rentals");
        
        // Actions
        btnOverview.addActionListener(e -> {
            overviewPanel.refresh();
            cardLayout.show(mainContentPanel, "Overview");
        });
        btnInventory.addActionListener(e -> {
            inventoryPanel.refresh();
            cardLayout.show(mainContentPanel, "Inventory");
        });
        btnRentals.addActionListener(e -> {
            rentalsPanel.refresh();
            cardLayout.show(mainContentPanel, "Rentals");
        });
        
        add(sideNav, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(230, 126, 34)); // Slightly lighter orange
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }
}
