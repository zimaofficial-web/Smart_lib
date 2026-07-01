package gui;

import controller.LibraryManager;
import controller.BorrowController;
import utils.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.*;
import utils.IDGenerator;

public class MainWindow extends JFrame {
    private LibraryManager libraryManager;
    private BorrowController borrowController;
    private String dataFilePath = "library_data.json";

    public MainWindow() {
        libraryManager = new LibraryManager();
        borrowController = new BorrowController(libraryManager);

        FileHandler.loadFromJSON(libraryManager, borrowController, dataFilePath);

        if (libraryManager.getCatalogue().isEmpty()) {
            seedData();
            FileHandler.saveToJSON(libraryManager, borrowController, dataFilePath);
        }

        setTitle("Smart Library Circulation & Automation System (SLCAS)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        
        ViewItemsPanel viewItemsPanel = new ViewItemsPanel(libraryManager);
        BorrowPanel borrowPanel = new BorrowPanel(libraryManager, borrowController, viewItemsPanel);
        AdminPanel adminPanel = new AdminPanel(libraryManager, viewItemsPanel);
        SearchSortPanel searchSortPanel = new SearchSortPanel(libraryManager);

        tabbedPane.addTab("View Items", viewItemsPanel);
        tabbedPane.addTab("Borrow / Return", borrowPanel);
        tabbedPane.addTab("Admin", adminPanel);
        tabbedPane.addTab("Search & Sort", searchSortPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JLabel statusBar = new JLabel(" Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);

        setupMenuBar();
        setupOverdueTimer();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> {
            FileHandler.saveToJSON(libraryManager, borrowController, dataFilePath);
            JOptionPane.showMessageDialog(this, "Data saved successfully.");
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void setupOverdueTimer() {
        // Fires every 60 seconds to check for overdues
        Timer timer = new Timer(60000, e -> {
            boolean hasOverdue = false;
            for (model.UserAccount u : libraryManager.getUsers()) {
                if (!u.getOverdueItems(14L * 24 * 60 * 60 * 1000).isEmpty()) {
                    hasOverdue = true;
                    break;
                }
            }
            if (hasOverdue) {
                JOptionPane.showMessageDialog(this, 
                    "Reminder: There are users with overdue items in the system!", 
                    "Overdue Alert", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        timer.start();
    }

    private void seedData() {
        // Add a default user
        UserAccount studentB = new UserAccount("STUDENT_B", "Student B", "student@miva.edu.ng");
        libraryManager.getUsers().add(studentB);

        // Seed Books
        libraryManager.addItem(new Book("B-1003", "Haunting Adeline", "H.D. Carlton", 2021, "ISBN-111", "Dark Romance"));
        libraryManager.addItem(new Magazine("M-1002A", "Gothic Fiction Monthly", "Vampire Press", 2021, "Iss-1", "Vampire Press"));
        libraryManager.addItem(new Magazine("M-1002B", "Dark Romance Reader", "Dennis", 2023, "Iss-2", "Romance Pub"));
        
        Book myBook = new Book("B-1001A", "Corrupt", "Penelope Douglas", 2015, "ISBN-222", "Dark Romance");
        libraryManager.addItem(myBook);
        myBook.borrow(studentB.getUserId());
        studentB.addLoan(myBook.getId());

        libraryManager.addItem(new Journal("J-1003", "Journal of Gothic Studies", "don", 2024, "Vol 1", "DOI-1"));
        libraryManager.addItem(new Magazine("M-1002C", "Villain Era Weekly", "Condé Nast", 2025, "Iss-3", "Condé Nast"));
        libraryManager.addItem(new Book("B-1001B", "Den of Vipers", "K.A. Knight", 2020, "ISBN-333", "Dark Romance"));
        libraryManager.addItem(new Book("B-1001C", "The Ritual", "Shantel Tessier", 2021, "ISBN-444", "Dark Romance"));
        libraryManager.addItem(new Journal("J-1001", "Dark Tropes Review", "Dennis", 2026, "Vol 2", "DOI-2"));
        libraryManager.addItem(new Book("B-1001D", "Hooked", "Emily McIntire", 2021, "ISBN-555", "Dark Romance"));
    }
}
