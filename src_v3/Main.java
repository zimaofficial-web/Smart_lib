import controller.DamLibrarySystem;
import gui.MainDashboard;
import model.Book;
import model.Magazine;
import model.Journal;
import model.UserAccount;
import utils.IdGenerator;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        DamLibrarySystem system = new DamLibrarySystem();
        
        // Seed some initial data focused on motivation
        system.addResource(new Book(IdGenerator.generateResourceId(), "Think and Grow Rich", "Napoleon Hill", 1937, "978-1585424337", "Business Success"));
        system.addResource(new Journal(IdGenerator.generateResourceId(), "The 5-Minute Journal", "Intelligent Change", 2024, "Daily Motivation", "Quarterly"));
        system.addResource(new Magazine(IdGenerator.generateResourceId(), "SUCCESS Magazine", "SUCCESS", 2023, "Vol 1", "SUCCESS Enterprises"));
        
        system.getUsers().add(new UserAccount(IdGenerator.generateTravelerId(), "Alice Smith", "alice@example.com"));
        system.getUsers().add(new UserAccount(IdGenerator.generateTravelerId(), "Bob Jones", "bob@example.com"));

        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard(system);
            dashboard.setVisible(true);
        });
    }
}
