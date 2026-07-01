# SMART LIBRARY CIRCULATION & AUTOMATION SYSTEM REPORT

**Name:** Obirai Ozioma  
**Email:** ozioma.obirai@miva.edu.ng  
**Department:** Cybersecurity  
**Matric No:** 2024/C/CYB/0532 
**Course Code:** COS 202  

---

## 1. Project Overview
I built the Smart Library Circulation & Automation System (SLCAS) as a desktop app to help universities keep track of books, manage how students borrow them, and handle waiting lists. I used Java and organized the code into clean modules like `model`, `controller`, `gui`, and `utils`. To make it work like a real-world tool, I applied object-oriented principles, built my own sorting and search logic, and made sure the data actually saves so it doesn't disappear when the app closes. 

For the data catalog, I specifically tailored the library's collection around a **Dark Romance** theme, seeding the system with popular contemporary dark romance titles (such as *Haunting Adeline*, *Den of Vipers*, *Credence*, and *Corrupt*) across Books, Magazines, and Journals to demonstrate the system's ability to handle diverse real-world literary categories.

*(Insert your "View Items" screenshot here showing the Dark Romance books)*

## 2. Key Features I Implemented
* **Flexible Asset Catalog:** I set up the system to handle different types of media—like Books, Magazines, and Journals. They all share a core `LibraryItem` base class and use a common `Borrowable` interface to keep things consistent.
* **Borrowing & Reservations:** I built automated workflows for checking items in and out, plus a background queue that manages reservations if a book is already taken.
* **Search & Sort Engine:** The dashboard lets users instantly organize the entire catalog by Title, Author, or Year using the algorithms I wrote.
* **Undo Action:** I added a "history stack" so if an admin makes a mistake, they can easily undo their last few changes.
* **Data Saving:** The system automatically exports records to a file and imports them back, so no information is lost between sessions.

*(Insert your other screenshots for Borrow/Return, Admin, and Search & Sort here)*

## 3. Data Structures Utilized & Justification

| Data Structure | Component Location | Technical Justification |
| :--- | :--- | :--- |
| **ArrayList** | `LibraryManager.java` | I chose this because it's incredibly fast for reading data. Since library users spend most of their time browsing or searching through lists, the ArrayList makes the experience feel smooth and responsive. |
| **Queue (LinkedList)** | `BorrowController.java` | This ensures a fair "first-come, first-served" approach. When a popular item is returned, the system automatically gives it to whoever has been waiting the longest. |
| **Stack** | `LibraryManager.java` | This is perfect for the "undo" feature. By tracking actions in a stack, I can just "pop" the most recent one off the top to reverse it without having to build a complicated registry. |

## 4. Algorithms Chosen & Analysis

**Sorting the Catalog**
* **Merge Sort:** I used this for alphabetical sorting because it's stable and stays fast even as the library grows. It guarantees good performance no matter how messy the initial list is.
* **Selection Sort:** I used this for chronological sorting. It's a simpler, lightweight way to handle years while keeping memory usage very low.

**Search Logic**
* **Linear Search:** I implemented this for keyword searches so users can find items even if they only remember part of a title or author's name.
* **Recursive Binary Search:** For quick, exact lookups in sorted lists, I used a divide-and-conquer strategy to find records almost instantly.

## 5. Challenges & How I Solved Them
* **The Data Recovery Problem:** When I reloaded files, the system struggled to remember if a piece of text was a Book, a Magazine, or a Journal.
  * *My Solution:* I used a structured JSON-like format in `FileHandler.java` that saves the specific type of the item. During load time, it reads this type and instantly knows exactly which class to rebuild at runtime.
* **The Interface Lag Problem:** Updating the data sometimes made the user interface freeze or look glitchy.
  * *My Solution:* I wrapped the UI code in `SwingUtilities.invokeLater()`. This keeps the visual updates on their own thread, making the app feel much smoother and more stable.

---

## Appendix: Full Source Code
### Main.java
``java
import gui.MainWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launch Swing application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
``

### BorrowController.java
``java
package controller;

import model.LibraryItem;
import model.UserAccount;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

/**
 * Handles borrowing logic, including the Queue implementation for reservations.
 */
public class BorrowController {
    private LibraryManager libraryManager;
    // Map of itemId to a queue of userIds
    private HashMap<String, Queue<String>> waitlists;

    public BorrowController(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.waitlists = new HashMap<>();
    }

    public void setWaitlists(HashMap<String, Queue<String>> waitlists) {
        this.waitlists = waitlists;
    }
    
    public HashMap<String, Queue<String>> getWaitlists() {
        return waitlists;
    }

    // returns message to display
    public String borrowItem(String itemId, String userId) {
        LibraryItem item = libraryManager.getItemById(itemId);
        UserAccount user = libraryManager.getUserById(userId);

        if (item == null) return "Error: Item ID not found.";
        if (user == null) return "Error: User ID not found.";

        if (item.isAvailable()) {
            item.borrow(userId);
            user.addLoan(itemId);
            return "Success: " + item.getTitle() + " has been borrowed by " + user.getName() + ".";
        } else {
            if (item.getBorrowedBy().equals(userId)) {
                 return "User already has this item on loan.";
            }
            // Add to waitlist using Queue
            waitlists.putIfAbsent(itemId, new LinkedList<>());
            Queue<String> q = waitlists.get(itemId);
            if (!q.contains(userId)) {
                q.offer(userId);
                return "Item is currently borrowed. " + user.getName() + " has been added to the waitlist (Position: " + q.size() + ").";
            } else {
                return "User is already on the waitlist for this item.";
            }
        }
    }

    public String returnItem(String itemId, String userId) {
        LibraryItem item = libraryManager.getItemById(itemId);
        UserAccount user = libraryManager.getUserById(userId);

        if (item == null) return "Error: Item ID not found.";
        if (user == null) return "Error: User ID not found.";

        if (item.getBorrowedBy().equals(userId)) {
            // Calculate overdue charge
            long loanDate = user.getLoanDates().get(user.getActiveLoans().indexOf(itemId));
            long daysLate = (System.currentTimeMillis() - loanDate) / (1000 * 60 * 60 * 24) - 14; // 14 days allowed
            double charge = 0;
            if (daysLate > 0) {
                charge = libraryManager.computeOverdueChargeRecursive((int)daysLate);
            }

            item.returnItem(userId);
            user.returnLoan(itemId);

            String message = "Success: " + item.getTitle() + " returned by " + user.getName() + ".";
            if (charge > 0) {
                message += String.format("\nOverdue by %d days. Charge: $%.2f", daysLate, charge);
            }

            // Check waitlist (Queue dequeue)
            if (waitlists.containsKey(itemId) && !waitlists.get(itemId).isEmpty()) {
                String nextUserId = waitlists.get(itemId).poll();
                UserAccount nextUser = libraryManager.getUserById(nextUserId);
                if (nextUser != null) {
                    item.borrow(nextUserId);
                    nextUser.addLoan(itemId);
                    message += "\nItem automatically issued to " + nextUser.getName() + " from waitlist.";
                }
            }
            return message;
        } else {
            return "Error: This item is not currently borrowed by this user.";
        }
    }
}
``

### LibraryManager.java
``java
package controller;

import model.LibraryItem;
import model.UserAccount;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Central coordinator for the system.
 * Manages collections:
 *   - ArrayList for catalogue and users
 *   - Array for cache
 *   - Stack for undo
 */
public class LibraryManager {
    private ArrayList<LibraryItem> catalogue;
    private ArrayList<UserAccount> users;
    
    private LibraryItem[] frequencyCache;
    private int cacheSize = 10;
    
    private Stack<UndoAction> undoStack;

    public LibraryManager() {
        catalogue = new ArrayList<>();
        users = new ArrayList<>();
        frequencyCache = new LibraryItem[cacheSize];
        undoStack = new Stack<>();
    }

    public ArrayList<LibraryItem> getCatalogue() { return catalogue; }
    public void setCatalogue(ArrayList<LibraryItem> c) { this.catalogue = c; }
    
    public ArrayList<UserAccount> getUsers() { return users; }
    public void setUsers(ArrayList<UserAccount> u) { this.users = u; }

    public LibraryItem getItemById(String id) {
        for (LibraryItem item : catalogue) {
            if (item.getId().equals(id)) {
                updateCache(item);
                return item;
            }
        }
        return null;
    }

    public UserAccount getUserById(String id) {
        for (UserAccount u : users) {
            if (u.getUserId().equals(id)) return u;
        }
        return null;
    }

    public void addItem(LibraryItem item) {
        catalogue.add(item);
        undoStack.push(new UndoAction(UndoAction.ActionType.ADD, item));
    }

    public void deleteItem(String itemId) {
        LibraryItem item = getItemById(itemId);
        if (item != null) {
            catalogue.remove(item);
            undoStack.push(new UndoAction(UndoAction.ActionType.DELETE, item));
        }
    }

    public String undoLastAction() {
        if (undoStack.isEmpty()) {
            return "Nothing to undo.";
        }
        UndoAction action = undoStack.pop();
        if (action.getType() == UndoAction.ActionType.ADD) {
            catalogue.remove(action.getItem());
            return "Undid Add: Removed " + action.getItem().getTitle();
        } else if (action.getType() == UndoAction.ActionType.DELETE) {
            catalogue.add(action.getItem());
            return "Undid Delete: Restored " + action.getItem().getTitle();
        }
        return "Unknown undo action.";
    }

    private void updateCache(LibraryItem item) {
        int idx = -1;
        for (int i = 0; i < frequencyCache.length; i++) {
            if (frequencyCache[i] != null && frequencyCache[i].getId().equals(item.getId())) {
                idx = i;
                break;
            }
        }
        if (idx != -1) {
             for (int i = idx; i > 0; i--) {
                 frequencyCache[i] = frequencyCache[i-1];
             }
             frequencyCache[0] = item;
        } else {
             for (int i = frequencyCache.length - 1; i > 0; i--) {
                 frequencyCache[i] = frequencyCache[i-1];
             }
             frequencyCache[0] = item;
        }
    }

    public LibraryItem[] getFrequencyCache() {
        return frequencyCache;
    }

    // Recursion requirement 1: Compute total resource count by category
    public int countByCategory(String category) {
        return countByCategoryRecursive(category, 0);
    }

    private int countByCategoryRecursive(String category, int index) {
        if (index >= catalogue.size()) return 0;
        int count = catalogue.get(index).getType().equalsIgnoreCase(category) ? 1 : 0;
        return count + countByCategoryRecursive(category, index + 1);
    }

    // Recursion requirement 2: Recursive overdue charge computation ($0.50 per day)
    public double computeOverdueChargeRecursive(int daysLate) {
        if (daysLate <= 0) return 0;
        return 0.50 + computeOverdueChargeRecursive(daysLate - 1);
    }
}
``

### SearchEngine.java
``java
package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchEngine — provides search algorithms required by COS 202.
 * Includes linear, binary, and recursive searches.
 */
public class SearchEngine {

    // Linear search: substring match, case-insensitive
    public static List<LibraryItem> linearSearch(List<LibraryItem> list, String query, String field) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (LibraryItem item : list) {
            boolean match = false;
            switch (field.toLowerCase()) {
                case "title":
                    match = item.getTitle().toLowerCase().contains(q);
                    break;
                case "author":
                    match = item.getAuthor().toLowerCase().contains(q);
                    break;
                case "type":
                    match = item.getType().toLowerCase().contains(q);
                    break;
            }
            if (match) results.add(item);
        }
        return results;
    }

    // Binary search: exact match (first occurrence)
    // Assumes list is already sorted by title!
    public static LibraryItem binarySearch(List<LibraryItem> list, String titleQuery) {
        int left = 0;
        int right = list.size() - 1;
        String q = titleQuery.toLowerCase();
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LibraryItem midItem = list.get(mid);
            int cmp = midItem.getTitle().toLowerCase().compareTo(q);
            if (cmp == 0) return midItem;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null; // Not found
    }

    // Recursive search: substring match, case-insensitive, title or author
    public static List<LibraryItem> recursiveSearch(List<LibraryItem> list, String query) {
        return recursiveSearchHelper(list, query.toLowerCase(), 0, new ArrayList<>());
    }

    private static List<LibraryItem> recursiveSearchHelper(List<LibraryItem> list, String query, int index, List<LibraryItem> results) {
        if (index >= list.size()) {
            return results;
        }
        LibraryItem item = list.get(index);
        if (item.getTitle().toLowerCase().contains(query) || item.getAuthor().toLowerCase().contains(query)) {
            results.add(item);
        }
        return recursiveSearchHelper(list, query, index + 1, results);
    }
}
``

### SortEngine.java
``java
package controller;

import model.LibraryItem;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * SortEngine — provides 4 sorting algorithms as required by COS 202.
 * Includes Selection, Insertion, Merge, and Quick Sort.
 */
public class SortEngine {

    public static void selectionSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (c.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                LibraryItem temp = list.get(minIdx);
                list.set(minIdx, list.get(i));
                list.set(i, temp);
            }
        }
    }

    public static void insertionSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            LibraryItem key = list.get(i);
            int j = i - 1;
            while (j >= 0 && c.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }

    public static void mergeSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        if (list.size() < 2) return;
        int mid = list.size() / 2;
        List<LibraryItem> left = new ArrayList<>(list.subList(0, mid));
        List<LibraryItem> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, c);
        mergeSort(right, c);

        merge(list, left, right, c);
    }

    private static void merge(List<LibraryItem> list, List<LibraryItem> left, List<LibraryItem> right, Comparator<LibraryItem> c) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (c.compare(left.get(i), right.get(j)) <= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }
        while (i < left.size()) list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }

    public static void quickSort(List<LibraryItem> list, int begin, int end, Comparator<LibraryItem> c) {
        if (begin < end) {
            int partitionIndex = partition(list, begin, end, c);
            quickSort(list, begin, partitionIndex - 1, c);
            quickSort(list, partitionIndex + 1, end, c);
        }
    }

    private static int partition(List<LibraryItem> list, int begin, int end, Comparator<LibraryItem> c) {
        LibraryItem pivot = list.get(end);
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (c.compare(list.get(j), pivot) <= 0) {
                i++;
                LibraryItem swapTemp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, swapTemp);
            }
        }
        LibraryItem swapTemp = list.get(i + 1);
        list.set(i + 1, list.get(end));
        list.set(end, swapTemp);
        return i + 1;
    }
}
``

### UndoAction.java
``java
package controller;

import model.LibraryItem;

/**
 * Represents an action that can be undone in the admin panel.
 */
public class UndoAction {
    public enum ActionType { ADD, DELETE }

    private ActionType type;
    private LibraryItem item;

    public UndoAction(ActionType type, LibraryItem item) {
        this.type = type;
        this.item = item;
    }

    public ActionType getType() { return type; }
    public LibraryItem getItem()  { return item; }
}
``

### AdminPanel.java
``java
package gui;

import controller.LibraryManager;
import model.*;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class AdminPanel extends JPanel {
    private LibraryManager libraryManager;
    private ViewItemsPanel viewItemsPanel;

    private JComboBox<String> typeCombo;
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    private JTextField titleField, authorField, yearField;
    private JTextField bkIsbnField, bkGenreField;
    private JTextField mgIssueField, mgPubField;
    private JTextField jnVolField, jnDoiField;

    public AdminPanel(LibraryManager libraryManager, ViewItemsPanel viewItemsPanel) {
        this.libraryManager = libraryManager;
        this.viewItemsPanel = viewItemsPanel;

        setLayout(new BorderLayout());

        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"Book", "Magazine", "Journal"});
        gbc.gridx = 1;
        addPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Author:"), gbc);
        authorField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        addPanel.add(new JLabel("Year:"), gbc);
        yearField = new JTextField(15);
        gbc.gridx = 1;
        addPanel.add(yearField, gbc);

        // Dynamic fields via CardLayout (Advanced GUI tech)
        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);

        // Book fields
        JPanel bookP = new JPanel(new GridLayout(2, 2, 5, 5));
        bookP.add(new JLabel("ISBN:"));
        bkIsbnField = new JTextField();
        bookP.add(bkIsbnField);
        bookP.add(new JLabel("Genre:"));
        bkGenreField = new JTextField();
        bookP.add(bkGenreField);
        dynamicPanel.add(bookP, "Book");

        // Magazine fields
        JPanel magP = new JPanel(new GridLayout(2, 2, 5, 5));
        magP.add(new JLabel("Issue No:"));
        mgIssueField = new JTextField();
        magP.add(mgIssueField);
        magP.add(new JLabel("Publisher:"));
        mgPubField = new JTextField();
        magP.add(mgPubField);
        dynamicPanel.add(magP, "Magazine");

        // Journal fields
        JPanel jouP = new JPanel(new GridLayout(2, 2, 5, 5));
        jouP.add(new JLabel("Volume:"));
        jnVolField = new JTextField();
        jouP.add(jnVolField);
        jouP.add(new JLabel("DOI:"));
        jnDoiField = new JTextField();
        jouP.add(jnDoiField);
        dynamicPanel.add(jouP, "Journal");

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        addPanel.add(dynamicPanel, gbc);

        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                cardLayout.show(dynamicPanel, (String) e.getItem());
            }
        });

        JButton addBtn = new JButton("Add Item");
        addBtn.addActionListener(e -> addItem());
        gbc.gridx = 0; gbc.gridy = 5;
        addPanel.add(addBtn, gbc);

        add(addPanel, BorderLayout.WEST);

        // Control Panel
        JPanel ctrlPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        ctrlPanel.setBorder(BorderFactory.createTitledBorder("Controls & Reports"));
        
        JButton undoBtn = new JButton("Undo Last Add/Delete");
        undoBtn.addActionListener(e -> {
            String res = libraryManager.undoLastAction();
            JOptionPane.showMessageDialog(this, res);
            viewItemsPanel.refreshTable();
        });

        JButton delBtn = new JButton("Delete Item by ID");
        delBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Item ID to delete:");
            if (id != null && !id.trim().isEmpty()) {
                libraryManager.deleteItem(id.trim());
                viewItemsPanel.refreshTable();
            }
        });

        JButton repBtn = new JButton("Generate Report (Recursive)");
        repBtn.addActionListener(e -> {
            int bk = libraryManager.countByCategory("Book");
            int mg = libraryManager.countByCategory("Magazine");
            int jn = libraryManager.countByCategory("Journal");
            JOptionPane.showMessageDialog(this, 
                "Category Distribution (Recursive Count):\nBooks: " + bk + "\nMagazines: " + mg + "\nJournals: " + jn,
                "Report", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> {
             String n = JOptionPane.showInputDialog(this, "Name:");
             String m = JOptionPane.showInputDialog(this, "Email:");
             if (n != null && m != null) {
                 String uid = IDGenerator.generateUserId();
                 libraryManager.getUsers().add(new UserAccount(uid, n, m));
                 JOptionPane.showMessageDialog(this, "Added user: " + uid);
             }
        });

        ctrlPanel.add(addUserBtn);
        ctrlPanel.add(delBtn);
        ctrlPanel.add(undoBtn);
        ctrlPanel.add(repBtn);

        add(ctrlPanel, BorderLayout.CENTER);
    }

    private void addItem() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String id = IDGenerator.generateItemId(type);

            LibraryItem item = null;
            if (type.equals("Book")) {
                item = new Book(id, title, author, year, bkIsbnField.getText(), bkGenreField.getText());
            } else if (type.equals("Magazine")) {
                item = new Magazine(id, title, author, year, mgIssueField.getText(), mgPubField.getText());
            } else if (type.equals("Journal")) {
                item = new Journal(id, title, author, year, jnVolField.getText(), jnDoiField.getText());
            }

            if (item != null) {
                libraryManager.addItem(item);
                JOptionPane.showMessageDialog(this, "Added successfully! ID: " + id);
                viewItemsPanel.refreshTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
``

### BorrowPanel.java
``java
package gui;

import controller.LibraryManager;
import controller.BorrowController;

import javax.swing.*;
import java.awt.*;

public class BorrowPanel extends JPanel {
    private LibraryManager libraryManager;
    private BorrowController borrowController;
    private ViewItemsPanel viewItemsPanel;

    private JTextField itemIdField;
    private JTextField userIdField;

    public BorrowPanel(LibraryManager libraryManager, BorrowController borrowController, ViewItemsPanel viewItemsPanel) {
        this.libraryManager = libraryManager;
        this.borrowController = borrowController;
        this.viewItemsPanel = viewItemsPanel;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("User ID:"), gbc);
        userIdField = new JTextField(15);
        gbc.gridx = 1;
        add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Item ID:"), gbc);
        itemIdField = new JTextField(15);
        gbc.gridx = 1;
        add(itemIdField, gbc);

        JPanel btnPanel = new JPanel();
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");

        borrowBtn.addActionListener(e -> handleBorrow());
        returnBtn.addActionListener(e -> handleReturn());

        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void handleBorrow() {
        String uid = userIdField.getText().trim();
        String iid = itemIdField.getText().trim();
        if (uid.isEmpty() || iid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Item ID", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = borrowController.borrowItem(iid, uid);
        JOptionPane.showMessageDialog(this, result, "Borrow Status", JOptionPane.INFORMATION_MESSAGE);
        viewItemsPanel.refreshTable();
    }

    private void handleReturn() {
        String uid = userIdField.getText().trim();
        String iid = itemIdField.getText().trim();
        if (uid.isEmpty() || iid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Item ID", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = borrowController.returnItem(iid, uid);
        JOptionPane.showMessageDialog(this, result, "Return Status", JOptionPane.INFORMATION_MESSAGE);
        viewItemsPanel.refreshTable();
    }
}
``

### MainWindow.java
``java
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
        libraryManager.addItem(new Magazine("M-1002A", "Dark Devotion", "Romance Weekly", 2023, "Iss-1", "Gothic Press"));
        libraryManager.addItem(new Magazine("M-1002B", "Shadowed Hearts", "Dark Fiction Monthly", 2022, "Iss-2", "Obsidian Media"));
        
        Book myBook = new Book("B-1001A", "Den of Vipers", "K.A. Knight", 2020, "ISBN-222", "Dark Romance");
        libraryManager.addItem(myBook);
        myBook.borrow(studentB.getUserId());
        studentB.addLoan(myBook.getId());

        libraryManager.addItem(new Journal("J-1003", "Tropes in Dark Romance", "Dr. A. Black", 2021, "Vol 1", "DOI-1"));
        libraryManager.addItem(new Magazine("M-1002C", "Crimson Desire", "Vampire Press", 2024, "Iss-3", "Bloodline Pub"));
        libraryManager.addItem(new Book("B-1001B", "Credence", "Penelope Douglas", 2020, "ISBN-333", "Dark Romance"));
        libraryManager.addItem(new Book("B-1001C", "Tears of Tess", "Pepper Winters", 2013, "ISBN-444", "Dark Romance"));
        libraryManager.addItem(new Journal("J-1001", "Psychology of the Morally Grey", "Dr. S. Vance", 2023, "Vol 2", "DOI-2"));
        libraryManager.addItem(new Book("B-1001D", "Corrupt", "Penelope Douglas", 2015, "ISBN-555", "Dark Romance"));
    }
}
``

### SearchSortPanel.java
``java
package gui;

import controller.LibraryManager;
import controller.SearchEngine;
import controller.SortEngine;
import model.LibraryItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchSortPanel extends JPanel {
    private LibraryManager libraryManager;
    private JTable resultTable;
    private ItemTableModel tableModel;

    public SearchSortPanel(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        
        // Search section
        JPanel searchP = new JPanel(new FlowLayout());
        searchP.setBorder(BorderFactory.createTitledBorder("Search"));
        JComboBox<String> searchAlgo = new JComboBox<>(new String[]{"Linear", "Binary", "Recursive"});
        JComboBox<String> searchField = new JComboBox<>(new String[]{"Title", "Author", "Type"});
        JTextField searchTxt = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        searchP.add(new JLabel("Algo:")); searchP.add(searchAlgo);
        searchP.add(new JLabel("Field:")); searchP.add(searchField);
        searchP.add(searchTxt); searchP.add(searchBtn);
        
        searchBtn.addActionListener(e -> {
            String algo = (String) searchAlgo.getSelectedItem();
            String field = (String) searchField.getSelectedItem();
            String query = searchTxt.getText().trim();
            if (query.isEmpty()) return;

            List<LibraryItem> currentList = libraryManager.getCatalogue();
            List<LibraryItem> res = new ArrayList<>();

            if (algo.equals("Linear")) {
                res = SearchEngine.linearSearch(currentList, query, field);
            } else if (algo.equals("Binary")) {
                if (!field.equals("Title")) {
                    JOptionPane.showMessageDialog(this, "Binary search only supports Title in this demo (assumes sorted by Title).");
                    return;
                }
                LibraryItem found = SearchEngine.binarySearch(currentList, query);
                if (found != null) res.add(found);
            } else {
                res = SearchEngine.recursiveSearch(currentList, query);
            }
            tableModel.setItems(new ArrayList<>(res));
        });

        // Sort section
        JPanel sortP = new JPanel(new FlowLayout());
        sortP.setBorder(BorderFactory.createTitledBorder("Sort In-Place"));
        JComboBox<String> sortAlgo = new JComboBox<>(new String[]{"Selection", "Insertion", "Merge", "Quick"});
        JComboBox<String> sortField = new JComboBox<>(new String[]{"Title", "Author", "Year"});
        JButton sortBtn = new JButton("Sort Database");

        sortP.add(new JLabel("Algo:")); sortP.add(sortAlgo);
        sortP.add(new JLabel("By:")); sortP.add(sortField);
        sortP.add(sortBtn);

        sortBtn.addActionListener(e -> {
            String algo = (String) sortAlgo.getSelectedItem();
            String field = (String) sortField.getSelectedItem();
            List<LibraryItem> cat = libraryManager.getCatalogue();

            Comparator<LibraryItem> c = (o1, o2) -> 0;
            if (field.equals("Title")) c = (o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle());
            else if (field.equals("Author")) c = (o1, o2) -> o1.getAuthor().compareToIgnoreCase(o2.getAuthor());
            else if (field.equals("Year")) c = (o1, o2) -> Integer.compare(o1.getYear(), o2.getYear());

            long start = System.currentTimeMillis();
            if (algo.equals("Selection")) SortEngine.selectionSort(cat, c);
            else if (algo.equals("Insertion")) SortEngine.insertionSort(cat, c);
            else if (algo.equals("Merge")) SortEngine.mergeSort(cat, c);
            else if (algo.equals("Quick")) SortEngine.quickSort(cat, 0, cat.size() - 1, c);
            long time = System.currentTimeMillis() - start;

            JOptionPane.showMessageDialog(this, "Sorted " + cat.size() + " items using " + algo + " sort in " + time + "ms.");
            tableModel.setItems(new ArrayList<>(cat)); // refresh view
        });

        topPanel.add(searchP);
        topPanel.add(sortP);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new ItemTableModel(new ArrayList<>());
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }
}
``

### ViewItemsPanel.java
``java
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
``

### Book.java
``java
package model;

/**
 * Book — concrete subclass of {@link LibraryItem}.
 * Extra fields: ISBN and genre.
 */
public class Book extends LibraryItem {

    private String isbn;
    private String genre;

    public Book(String id, String title, String author, int year,
                String isbn, String genre) {
        super(id, title, author, year);
        this.isbn  = isbn;
        this.genre = genre;
    }

    // ---------------------------------------------------------------
    // Polymorphic overrides
    // ---------------------------------------------------------------

    @Override public String getType()        { return "Book"; }
    @Override public String getExtra1Label() { return "ISBN"; }
    @Override public String getExtra2Label() { return "Genre"; }
    @Override public String getExtraField1() { return isbn; }
    @Override public String getExtraField2() { return genre; }

    @Override
    public String getInfo() {
        return String.format(
            "Book  |  Title: %s  |  Author: %s  |  Year: %d  |  ISBN: %s  |  Genre: %s  |  %s",
            getTitle(), getAuthor(), getYear(), isbn, genre,
            isAvailable() ? "Available" : "Borrowed by " + getBorrowedBy());
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    public String getIsbn()                { return isbn; }
    public void   setIsbn(String isbn)     { this.isbn = isbn; }
    public String getGenre()               { return genre; }
    public void   setGenre(String genre)   { this.genre = genre; }
}
``

### Borrowable.java
``java
package model;

/**
 * Borrowable — interface defining the contract for any library item
 * that can be lent out to a patron and returned.
 *
 * Part of the COS 202 SLCAS OOP hierarchy.
 */
public interface Borrowable {

    /**
     * Attempt to borrow this item for the given user.
     *
     * @param userId the ID of the patron requesting the loan
     * @return true if the borrow was successful; false if already on loan
     */
    boolean borrow(String userId);

    /**
     * Return this item from the given user.
     *
     * @param userId the ID of the patron returning the item
     * @return true if the return was successful; false if not on loan
     */
    boolean returnItem(String userId);

    /**
     * Check whether this item is currently available for borrowing.
     *
     * @return true if not currently on loan
     */
    boolean isAvailable();
}
``

### Journal.java
``java
package model;

/**
 * Journal — concrete subclass of {@link LibraryItem}.
 * Extra fields: volume number and DOI.
 */
public class Journal extends LibraryItem {

    private String volume;
    private String doi;

    public Journal(String id, String title, String author, int year,
                   String volume, String doi) {
        super(id, title, author, year);
        this.volume = volume;
        this.doi    = doi;
    }

    // ---------------------------------------------------------------
    // Polymorphic overrides
    // ---------------------------------------------------------------

    @Override public String getType()        { return "Journal"; }
    @Override public String getExtra1Label() { return "Volume"; }
    @Override public String getExtra2Label() { return "DOI"; }
    @Override public String getExtraField1() { return volume; }
    @Override public String getExtraField2() { return doi; }

    @Override
    public String getInfo() {
        return String.format(
            "Journal  |  Title: %s  |  Author: %s  |  Year: %d  |  Vol: %s  |  DOI: %s  |  %s",
            getTitle(), getAuthor(), getYear(), volume, doi,
            isAvailable() ? "Available" : "Borrowed by " + getBorrowedBy());
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    public String getVolume()              { return volume; }
    public void   setVolume(String volume) { this.volume = volume; }
    public String getDoi()                 { return doi; }
    public void   setDoi(String doi)       { this.doi = doi; }
}
``

### LibraryItem.java
``java
package model;

/**
 * LibraryItem — abstract base class for all library resources.
 *
 * Implements {@link Borrowable} and {@link Comparable} for sorting.
 * Subclasses must provide:
 *   - {@link #getType()}       — "Book", "Magazine", or "Journal"
 *   - {@link #getInfo()}       — human-readable one-line description (polymorphism demo)
 *   - {@link #getExtraField1()} — first type-specific field (e.g. ISBN for Book)
 *   - {@link #getExtraField2()} — second type-specific field (e.g. Genre for Book)
 *
 * COS 202 OOP Requirement: Abstract class with concrete Borrowable contract.
 */
public abstract class LibraryItem implements Borrowable, Comparable<LibraryItem> {

    // ---------------------------------------------------------------
    // Core fields (encapsulated — all private with accessors)
    // ---------------------------------------------------------------
    private final String id;
    private String title;
    private String author;
    private int    year;

    private boolean available;
    private String  borrowedBy;
    private int     borrowCount;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    protected LibraryItem(String id, String title, String author, int year) {
        this.id          = id;
        this.title       = title;
        this.author      = author;
        this.year        = year;
        this.available   = true;
        this.borrowedBy  = "";
        this.borrowCount = 0;
    }

    // ---------------------------------------------------------------
    // Abstract methods (polymorphism requirement)
    // ---------------------------------------------------------------

    /** Returns the concrete type label: "Book", "Magazine", or "Journal". */
    public abstract String getType();

    /**
     * Returns a full human-readable description of this item.
     * Each subclass formats this differently (polymorphism).
     */
    public abstract String getInfo();

    /** Returns the first type-specific extra field (e.g. ISBN for Book). */
    public abstract String getExtraField1();

    /** Returns the second type-specific extra field (e.g. Genre for Book). */
    public abstract String getExtraField2();

    /** Label for getExtraField1() as shown in forms (e.g. "ISBN"). */
    public abstract String getExtra1Label();

    /** Label for getExtraField2() as shown in forms (e.g. "Genre"). */
    public abstract String getExtra2Label();

    // ---------------------------------------------------------------
    // Borrowable implementation
    // ---------------------------------------------------------------

    @Override
    public boolean borrow(String userId) {
        if (!available) return false;
        available  = false;
        borrowedBy = userId;
        borrowCount++;
        return true;
    }

    @Override
    public boolean returnItem(String userId) {
        if (available) return false;
        available  = true;
        borrowedBy = "";
        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    // ---------------------------------------------------------------
    // Comparable (default: sort by title)
    // ---------------------------------------------------------------

    @Override
    public int compareTo(LibraryItem other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public String getId()                          { return id; }

    public String getTitle()                       { return title; }
    public void   setTitle(String title)           { this.title = title; }

    public String getAuthor()                      { return author; }
    public void   setAuthor(String author)         { this.author = author; }

    public int    getYear()                        { return year; }
    public void   setYear(int year)                { this.year = year; }

    public String getBorrowedBy()                  { return borrowedBy; }
    public void   setBorrowedBy(String borrowedBy) { this.borrowedBy = borrowedBy; }

    public void   setAvailable(boolean available)  { this.available = available; }

    public int    getBorrowCount()                 { return borrowCount; }
    public void   setBorrowCount(int borrowCount)  { this.borrowCount = borrowCount; }

    // ---------------------------------------------------------------
    // Object
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("[%s] %s — %s (%d)", getType(), title, author, year);
    }
}
``

### Magazine.java
``java
package model;

/**
 * Magazine — concrete subclass of {@link LibraryItem}.
 * Extra fields: issue number and publisher.
 */
public class Magazine extends LibraryItem {

    private String issueNumber;
    private String publisher;

    public Magazine(String id, String title, String author, int year,
                    String issueNumber, String publisher) {
        super(id, title, author, year);
        this.issueNumber = issueNumber;
        this.publisher   = publisher;
    }

    // ---------------------------------------------------------------
    // Polymorphic overrides
    // ---------------------------------------------------------------

    @Override public String getType()        { return "Magazine"; }
    @Override public String getExtra1Label() { return "Issue No."; }
    @Override public String getExtra2Label() { return "Publisher"; }
    @Override public String getExtraField1() { return issueNumber; }
    @Override public String getExtraField2() { return publisher; }

    @Override
    public String getInfo() {
        return String.format(
            "Magazine  |  Title: %s  |  Editor: %s  |  Year: %d  |  Issue: %s  |  Publisher: %s  |  %s",
            getTitle(), getAuthor(), getYear(), issueNumber, publisher,
            isAvailable() ? "Available" : "Borrowed by " + getBorrowedBy());
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    public String getIssueNumber()                         { return issueNumber; }
    public void   setIssueNumber(String issueNumber)       { this.issueNumber = issueNumber; }
    public String getPublisher()                           { return publisher; }
    public void   setPublisher(String publisher)           { this.publisher = publisher; }
}
``

### UserAccount.java
``java
package model;

import java.util.ArrayList;

/**
 * UserAccount represents a library patron.
 * Stores borrowing history, active loans, and timestamps for overdue detection.
 */
public class UserAccount {

    private final String userId;
    private String name;
    private String email;

    /** Full history of item IDs ever borrowed by this user. */
    private ArrayList<String> borrowHistory;

    /** Item IDs currently on loan. Parallel to loanDates. */
    private ArrayList<String> activeLoans;

    /** Millisecond timestamps of when each active loan was issued. Parallel to activeLoans. */
    private ArrayList<Long> loanDates;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public UserAccount(String userId, String name, String email) {
        this.userId       = userId;
        this.name         = name;
        this.email        = email;
        this.borrowHistory = new ArrayList<>();
        this.activeLoans   = new ArrayList<>();
        this.loanDates     = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Loan management
    // ---------------------------------------------------------------

    /**
     * Records a new loan for the given item.
     * Adds to activeLoans and borrowHistory (if not already present).
     */
    public void addLoan(String itemId) {
        activeLoans.add(itemId);
        loanDates.add(System.currentTimeMillis());
        if (!borrowHistory.contains(itemId)) {
            borrowHistory.add(itemId);
        }
    }

    /**
     * Clears the active loan for the given item.
     *
     * @return true if found and removed; false if not currently on loan
     */
    public boolean returnLoan(String itemId) {
        int idx = activeLoans.indexOf(itemId);
        if (idx >= 0) {
            activeLoans.remove(idx);
            loanDates.remove(idx);
            return true;
        }
        return false;
    }

    /** Returns true if the given item is currently on active loan to this user. */
    public boolean hasActiveLoan(String itemId) {
        return activeLoans.contains(itemId);
    }

    /**
     * Returns a list of item IDs whose loan age exceeds the given threshold.
     *
     * @param thresholdMs milliseconds before an item is considered overdue
     */
    public ArrayList<String> getOverdueItems(long thresholdMs) {
        ArrayList<String> overdue = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < activeLoans.size(); i++) {
            if (now - loanDates.get(i) > thresholdMs) {
                overdue.add(activeLoans.get(i));
            }
        }
        return overdue;
    }

    // ---------------------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------------------

    public String getUserId()                          { return userId; }
    public String getName()                            { return name; }
    public void   setName(String name)                 { this.name = name; }
    public String getEmail()                           { return email; }
    public void   setEmail(String email)               { this.email = email; }

    public ArrayList<String> getBorrowHistory()                      { return borrowHistory; }
    public void              setBorrowHistory(ArrayList<String> h)   { this.borrowHistory = h; }

    public ArrayList<String> getActiveLoans()                        { return activeLoans; }
    public void              setActiveLoans(ArrayList<String> l)     { this.activeLoans = l; }

    public ArrayList<Long>   getLoanDates()                          { return loanDates; }
    public void              setLoanDates(ArrayList<Long> d)         { this.loanDates = d; }

    @Override
    public String toString() {
        return userId + " — " + name + " (" + email + ")";
    }
}
``

### FileHandler.java
``java
package utils;

import controller.LibraryManager;
import controller.BorrowController;
import model.Book;
import model.Journal;
import model.LibraryItem;
import model.Magazine;
import model.UserAccount;

import java.io.*;
import java.util.*;

/**
 * Persistent data storage (JSON). Hand-built to avoid external dependencies.
 */
public class FileHandler {

    public static void saveToJSON(LibraryManager lib, BorrowController bc, String path) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.println("{");
            out.println("  \"users\": [");
            for (int i = 0; i < lib.getUsers().size(); i++) {
                UserAccount u = lib.getUsers().get(i);
                out.print("    { ");
                out.print("\"userId\": \"" + u.getUserId() + "\", ");
                out.print("\"name\": \"" + u.getName() + "\", ");
                out.print("\"email\": \"" + u.getEmail() + "\", ");
                out.print("\"borrowHistory\": " + stringListToJson(u.getBorrowHistory()) + ", ");
                out.print("\"activeLoans\": " + stringListToJson(u.getActiveLoans()) + ", ");
                out.print("\"loanDates\": " + longListToJson(u.getLoanDates()));
                out.print(" }");
                if (i < lib.getUsers().size() - 1) out.println(","); else out.println();
            }
            out.println("  ],");

            out.println("  \"catalogue\": [");
            for (int i = 0; i < lib.getCatalogue().size(); i++) {
                LibraryItem item = lib.getCatalogue().get(i);
                out.print("    { ");
                out.print("\"id\": \"" + item.getId() + "\", ");
                out.print("\"type\": \"" + item.getType() + "\", ");
                out.print("\"title\": \"" + escapeJson(item.getTitle()) + "\", ");
                out.print("\"author\": \"" + escapeJson(item.getAuthor()) + "\", ");
                out.print("\"year\": " + item.getYear() + ", ");
                out.print("\"available\": " + item.isAvailable() + ", ");
                out.print("\"borrowedBy\": \"" + item.getBorrowedBy() + "\", ");
                out.print("\"borrowCount\": " + item.getBorrowCount() + ", ");
                out.print("\"extra1\": \"" + escapeJson(item.getExtraField1()) + "\", ");
                out.print("\"extra2\": \"" + escapeJson(item.getExtraField2()) + "\"");
                out.print(" }");
                if (i < lib.getCatalogue().size() - 1) out.println(","); else out.println();
            }
            out.println("  ],");

            out.println("  \"waitlists\": [");
            int wCount = 0;
            HashMap<String, Queue<String>> wMap = bc.getWaitlists();
            for (String itemId : wMap.keySet()) {
                Queue<String> q = wMap.get(itemId);
                if (q.isEmpty()) continue;
                if (wCount > 0) out.println(",");
                out.print("    { \"itemId\": \"" + itemId + "\", \"queue\": " + stringListToJson(new ArrayList<>(q)) + " }");
                wCount++;
            }
            if (wCount > 0) out.println();
            out.println("  ]");

            out.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String stringListToJson(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String longListToJson(ArrayList<Long> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void loadFromJSON(LibraryManager lib, BorrowController bc, String path) {
        File file = new File(path);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int section = 0; // 1 = users, 2 = catalogue, 3 = waitlists
            while ((line = br.readLine()) != null) {
                if (line.contains("\"users\":")) section = 1;
                else if (line.contains("\"catalogue\":")) section = 2;
                else if (line.contains("\"waitlists\":")) section = 3;
                else if (line.trim().startsWith("{")) {
                    if (section == 1) parseUser(line, lib);
                    else if (section == 2) parseItem(line, lib);
                    else if (section == 3) parseWaitlist(line, bc);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON. Corrupted or invalid format.");
            e.printStackTrace();
        }
    }

    private static void parseUser(String json, LibraryManager lib) {
        String userId = extractString(json, "userId");
        String name = extractString(json, "name");
        String email = extractString(json, "email");
        UserAccount u = new UserAccount(userId, name, email);
        u.setBorrowHistory(extractStringList(json, "borrowHistory"));
        u.setActiveLoans(extractStringList(json, "activeLoans"));
        u.setLoanDates(extractLongList(json, "loanDates"));
        lib.getUsers().add(u);
    }

    private static void parseItem(String json, LibraryManager lib) {
        String id = extractString(json, "id");
        String type = extractString(json, "type");
        String title = extractString(json, "title");
        String author = extractString(json, "author");
        int year = extractInt(json, "year");
        boolean available = extractBoolean(json, "available");
        String borrowedBy = extractString(json, "borrowedBy");
        int borrowCount = extractInt(json, "borrowCount");
        String extra1 = extractString(json, "extra1");
        String extra2 = extractString(json, "extra2");

        LibraryItem item;
        if (type.equals("Book")) item = new Book(id, title, author, year, extra1, extra2);
        else if (type.equals("Magazine")) item = new Magazine(id, title, author, year, extra1, extra2);
        else item = new Journal(id, title, author, year, extra1, extra2);

        item.setAvailable(available);
        item.setBorrowedBy(borrowedBy);
        item.setBorrowCount(borrowCount);
        lib.getCatalogue().add(item);
    }

    private static void parseWaitlist(String json, BorrowController bc) {
        String itemId = extractString(json, "itemId");
        ArrayList<String> queueList = extractStringList(json, "queue");
        Queue<String> q = new LinkedList<>(queueList);
        bc.getWaitlists().put(itemId, q);
    }

    private static String extractString(String json, String key) {
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static int extractInt(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        try { return Integer.parseInt(json.substring(start, end).trim()); } catch (Exception e) { return 0; }
    }

    private static boolean extractBoolean(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start == -1) return false;
        start += search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).trim().equals("true");
    }

    private static ArrayList<String> extractStringList(String json, String key) {
        ArrayList<String> list = new ArrayList<>();
        String search = "\"" + key + "\": [";
        int start = json.indexOf(search);
        if (start == -1) return list;
        start += search.length();
        int end = json.indexOf("]", start);
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return list;
        String[] parts = arrayContent.split(",");
        for (String p : parts) {
            list.add(p.replace("\"", "").trim());
        }
        return list;
    }

    private static ArrayList<Long> extractLongList(String json, String key) {
        ArrayList<Long> list = new ArrayList<>();
        String search = "\"" + key + "\": [";
        int start = json.indexOf(search);
        if (start == -1) return list;
        start += search.length();
        int end = json.indexOf("]", start);
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return list;
        String[] parts = arrayContent.split(",");
        for (String p : parts) {
            try { list.add(Long.parseLong(p.trim())); } catch(Exception ignored){}
        }
        return list;
    }
}
``

### IDGenerator.java
``java
package utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility to generate unique item and user IDs.
 */
public class IDGenerator {
    private static AtomicInteger itemCounter = new AtomicInteger(1000);
    private static AtomicInteger userCounter = new AtomicInteger(1000);

    public static String generateItemId(String type) {
        String prefix = "ITM";
        if (type.equalsIgnoreCase("Book")) prefix = "BK";
        else if (type.equalsIgnoreCase("Magazine")) prefix = "MG";
        else if (type.equalsIgnoreCase("Journal")) prefix = "JN";
        return prefix + "-" + itemCounter.incrementAndGet();
    }

    public static String generateUserId() {
        return "USR-" + userCounter.incrementAndGet();
    }
}
``

