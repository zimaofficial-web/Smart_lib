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
