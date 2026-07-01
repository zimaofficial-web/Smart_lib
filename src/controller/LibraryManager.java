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
