package model;

public class TravelGuide extends TravelResource {
    private String country;
    private String audience;

    public TravelGuide(String id, String title, String creator, int publicationYear, String country, String audience) {
        super(id, title, creator, publicationYear);
        this.country = country;
        this.audience = audience;
    }

    @Override public String getResourceType() { return "Travel Guide"; }
    @Override public String getExtraLabel1() { return "Country/Region"; }
    @Override public String getExtraLabel2() { return "Audience"; }
    @Override public String getExtraAttribute1() { return country; }
    @Override public String getExtraAttribute2() { return audience; }
}
