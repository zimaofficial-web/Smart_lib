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
