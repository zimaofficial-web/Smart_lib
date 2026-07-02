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
