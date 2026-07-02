package gui;

import controller.DamLibrarySystem;
import model.LibraryItem;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {
    private DamLibrarySystem system;
    private JLabel lblTotalResources;
    private JLabel lblTotalUsers;
    private JTextArea txtRecentAccess;

    public OverviewPanel(DamLibrarySystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        title.setForeground(new Color(211, 84, 0)); // Dark Orange
        add(title, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        lblTotalResources = createStatLabel("Total Resources: 0");
        lblTotalUsers = createStatLabel("Total Readers: 0");
        
        statsPanel.add(lblTotalResources);
        statsPanel.add(lblTotalUsers);
        centerPanel.add(statsPanel);
        
        // Cache Panel (Recent Access)
        JPanel cachePanel = new JPanel(new BorderLayout());
        cachePanel.setBackground(Color.WHITE);
        cachePanel.setBorder(BorderFactory.createTitledBorder("Recently Accessed Items (Cache)"));
        
        txtRecentAccess = new JTextArea();
        txtRecentAccess.setEditable(false);
        txtRecentAccess.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cachePanel.add(new JScrollPane(txtRecentAccess), BorderLayout.CENTER);
        
        centerPanel.add(cachePanel);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setOpaque(true);
        label.setBackground(new Color(253, 235, 208)); // Pale Orange
        label.setBorder(BorderFactory.createLineBorder(new Color(211, 84, 0), 1));
        return label;
    }
    
    public void refresh() {
        lblTotalResources.setText("Total Resources: " + system.getInventory().size());
        lblTotalUsers.setText("Total Readers: " + system.getUsers().size());
        
        StringBuilder sb = new StringBuilder();
        for (LibraryItem r : system.getAccessCache()) {
            if (r != null) {
                sb.append(r.getInfo()).append("\n");
            }
        }
        if (sb.length() == 0) sb.append("No recent access.");
        txtRecentAccess.setText(sb.toString());
    }
}
