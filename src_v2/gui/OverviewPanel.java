package gui;

import controller.TravelLibrarySystem;
import model.TravelResource;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {
    private TravelLibrarySystem system;
    
    private JLabel lblTotal;
    private JLabel lblGuides;
    private JLabel lblMaps;
    private JLabel lblMagazines;
    private JPanel recentPanel;

    public OverviewPanel(TravelLibrarySystem system) {
        this.system = system;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        
        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        
        lblTotal = new JLabel("0", SwingConstants.CENTER);
        lblGuides = new JLabel("0", SwingConstants.CENTER);
        lblMaps = new JLabel("0", SwingConstants.CENTER);
        lblMagazines = new JLabel("0", SwingConstants.CENTER);
        
        statsPanel.add(createStatCard("Total Resources", lblTotal));
        statsPanel.add(createStatCard("Travel Guides", lblGuides));
        statsPanel.add(createStatCard("Travel Maps", lblMaps));
        statsPanel.add(createStatCard("Magazines", lblMagazines));
        
        centerPanel.add(statsPanel);
        
        // Recent Access Panel
        recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recently Accessed (Cache)"));
        
        centerPanel.add(new JScrollPane(recentPanel));
        
        add(centerPanel, BorderLayout.CENTER);
        
        refresh();
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(236, 240, 241));
        card.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(new Color(41, 128, 185)); // Blueish
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
    
    public void refresh() {
        int total = system.getInventory().size();
        lblTotal.setText(String.valueOf(total));
        
        // Using the recursive count requirement!
        lblGuides.setText(String.valueOf(system.tallyResourcesByCategory("Travel Guide")));
        lblMaps.setText(String.valueOf(system.tallyResourcesByCategory("Map")));
        lblMagazines.setText(String.valueOf(system.tallyResourcesByCategory("Magazine")));
        
        recentPanel.removeAll();
        TravelResource[] cache = system.getAccessCache();
        for (TravelResource res : cache) {
            if (res != null) {
                JLabel lbl = new JLabel(res.toString());
                lbl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recentPanel.add(lbl);
            }
        }
        recentPanel.revalidate();
        recentPanel.repaint();
    }
}
