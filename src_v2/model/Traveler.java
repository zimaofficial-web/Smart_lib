package model;

import java.util.ArrayList;
import java.util.List;

public class Traveler {
    private String travelerId;
    private String name;
    private String email;
    private List<TravelResource> rentedItems;

    public Traveler(String travelerId, String name, String email) {
        this.travelerId = travelerId;
        this.name = name;
        this.email = email;
        this.rentedItems = new ArrayList<>();
    }

    public String getTravelerId() { return travelerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    public List<TravelResource> getRentedItems() { return rentedItems; }
    
    public void addRentedItem(TravelResource resource) {
        rentedItems.add(resource);
    }
    
    public void removeRentedItem(TravelResource resource) {
        rentedItems.remove(resource);
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
