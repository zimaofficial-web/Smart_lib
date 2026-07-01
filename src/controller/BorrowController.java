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
