package model;

public class TravelMap extends TravelResource {
    private String region;
    private String scale;

    public TravelMap(String id, String title, String creator, int publicationYear, String region, String scale) {
        super(id, title, creator, publicationYear);
        this.region = region;
        this.scale = scale;
    }

    @Override public String getResourceType() { return "Map"; }
    @Override public String getExtraLabel1() { return "Region"; }
    @Override public String getExtraLabel2() { return "Scale"; }
    @Override public String getExtraAttribute1() { return region; }
    @Override public String getExtraAttribute2() { return scale; }
}
