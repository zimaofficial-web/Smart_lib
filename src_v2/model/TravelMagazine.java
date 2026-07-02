package model;

public class TravelMagazine extends TravelResource {
    private String issueMonth;
    private String topic;

    public TravelMagazine(String id, String title, String creator, int publicationYear, String issueMonth, String topic) {
        super(id, title, creator, publicationYear);
        this.issueMonth = issueMonth;
        this.topic = topic;
    }

    @Override public String getResourceType() { return "Magazine"; }
    @Override public String getExtraLabel1() { return "Issue Month"; }
    @Override public String getExtraLabel2() { return "Topic"; }
    @Override public String getExtraAttribute1() { return issueMonth; }
    @Override public String getExtraAttribute2() { return topic; }
}
