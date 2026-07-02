package model;

/**
 * Interface defining the rental contract for any travel resource.
 */
public interface Rentable {
    /**
     * Rents the item to a user.
     * @param userId The ID of the user renting the item.
     * @return true if successfully rented, false if not available.
     */
    boolean rentItem(String userId);

    /**
     * Returns the item to the library.
     * @param userId The ID of the user returning the item.
     * @return true if successfully returned, false if it was already available.
     */
    boolean returnItem(String userId);
}
