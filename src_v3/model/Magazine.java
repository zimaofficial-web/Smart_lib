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
