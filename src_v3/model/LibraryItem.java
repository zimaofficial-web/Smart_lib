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
