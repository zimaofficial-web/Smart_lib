package model;

public abstract class TravelResource implements Rentable {
    private String id;
    private String title;
    private String creator;
    private int publicationYear;
    private boolean available;
    private String rentedBy;

    public TravelResource(String id, String title, String creator, int publicationYear) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.publicationYear = publicationYear;
        this.available = true;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public int getPublicationYear() { return publicationYear; }
    public boolean isAvailable() { return available; }
    
    public abstract String getResourceType();
    public abstract String getExtraLabel1();
    public abstract String getExtraLabel2();
    public abstract String getExtraAttribute1();
    public abstract String getExtraAttribute2();

    @Override
    public boolean rentItem(String userId) {
        if (!available) return false;
        available = false;
        rentedBy = userId;
        return true;
    }

    @Override
    public boolean returnItem(String userId) {
        if (available) return false;
        available = true;
        rentedBy = null;
        return true;
    }

    @Override
    public String toString() {
        return getResourceType() + " - " + title + " (" + publicationYear + ")";
    }
}
