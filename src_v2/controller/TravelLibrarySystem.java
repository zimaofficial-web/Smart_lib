package controller;

import model.TravelResource;
import model.Traveler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Core controller managing the inventory and operations for the travel library.
 */
public class TravelLibrarySystem {
    private List<TravelResource> inventory;
    private List<Traveler> travelers;
    
    // Requirement: Array for caching recently accessed items
    private TravelResource[] accessCache;
    private int cacheCapacity = 10;
    
    // Requirement: Stack for undo
    private Stack<UndoCommand> commandHistory;

    public TravelLibrarySystem() {
        this.inventory = new ArrayList<>();
        this.travelers = new ArrayList<>();
        this.accessCache = new TravelResource[cacheCapacity];
        this.commandHistory = new Stack<>();
    }

    public List<TravelResource> getInventory() { return inventory; }
    public void setInventory(List<TravelResource> inventory) { this.inventory = inventory; }
    
    public List<Traveler> getTravelers() { return travelers; }
    public void setTravelers(List<Traveler> travelers) { this.travelers = travelers; }

    public TravelResource getResourceById(String id) {
        for (TravelResource res : inventory) {
            if (res.getId().equals(id)) {
                updateCache(res);
                return res;
            }
        }
        return null;
    }

    public Traveler getTravelerById(String id) {
        for (Traveler t : travelers) {
            if (t.getTravelerId().equals(id)) return t;
        }
        return null;
    }

    public void addResource(TravelResource resource) {
        inventory.add(resource);
        commandHistory.push(new UndoCommand(UndoCommand.CommandType.ADD_RESOURCE, resource));
    }

    public void removeResource(String resourceId) {
        TravelResource resource = getResourceById(resourceId);
        if (resource != null) {
            inventory.remove(resource);
            commandHistory.push(new UndoCommand(UndoCommand.CommandType.REMOVE_RESOURCE, resource));
        }
    }

    public String undoLastCommand() {
        if (commandHistory.isEmpty()) {
            return "No actions to undo.";
        }
        UndoCommand lastCommand = commandHistory.pop();
        if (lastCommand.getType() == UndoCommand.CommandType.ADD_RESOURCE) {
            inventory.remove(lastCommand.getResource());
            return "Undid Add: Removed " + lastCommand.getResource().getTitle();
        } else if (lastCommand.getType() == UndoCommand.CommandType.REMOVE_RESOURCE) {
            inventory.add(lastCommand.getResource());
            return "Undid Remove: Restored " + lastCommand.getResource().getTitle();
        }
        return "Unknown command.";
    }

    private void updateCache(TravelResource resource) {
        int foundIndex = -1;
        for (int i = 0; i < accessCache.length; i++) {
            if (accessCache[i] != null && accessCache[i].getId().equals(resource.getId())) {
                foundIndex = i;
                break;
            }
        }
        
        if (foundIndex != -1) {
             for (int i = foundIndex; i > 0; i--) {
                 accessCache[i] = accessCache[i-1];
             }
        } else {
             for (int i = accessCache.length - 1; i > 0; i--) {
                 accessCache[i] = accessCache[i-1];
             }
        }
        accessCache[0] = resource;
    }

    public TravelResource[] getAccessCache() {
        return accessCache;
    }

    // Recursion requirement 1: Compute total resource count by category recursively
    public int tallyResourcesByCategory(String category) {
        return countCategoryRecursive(category, 0);
    }

    private int countCategoryRecursive(String category, int index) {
        if (index >= inventory.size()) return 0;
        int count = inventory.get(index).getResourceType().equalsIgnoreCase(category) ? 1 : 0;
        return count + countCategoryRecursive(category, index + 1);
    }

    // Recursion requirement 2: Recursive late fee computation ($1.50 per day for travel resources)
    public double computeLateFeesRecursive(int daysLate) {
        if (daysLate <= 0) return 0;
        return 1.50 + computeLateFeesRecursive(daysLate - 1);
    }
}
