import controller.TravelLibrarySystem;
import gui.MainDashboard;
import model.TravelGuide;
import model.TravelMagazine;
import model.TravelMap;
import model.Traveler;
import utils.IdGenerator;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        TravelLibrarySystem system = new TravelLibrarySystem();
        
        // Seed some initial data
        system.addResource(new TravelGuide(IdGenerator.generateResourceId(), "Europe on a Shoestring", "Lonely Planet", 2023, "Europe", "Budget Travelers"));
        system.addResource(new TravelMap(IdGenerator.generateResourceId(), "Tokyo Metro Map", "Tokyo Gov", 2024, "Tokyo, Japan", "1:10000"));
        system.addResource(new TravelMagazine(IdGenerator.generateResourceId(), "National Geographic Traveler", "NatGeo", 2023, "October", "Wildlife Safaris"));
        
        system.getTravelers().add(new Traveler(IdGenerator.generateTravelerId(), "Alice Smith", "alice@example.com"));
        system.getTravelers().add(new Traveler(IdGenerator.generateTravelerId(), "Bob Jones", "bob@example.com"));

        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard(system);
            dashboard.setVisible(true);
        });
    }
}
