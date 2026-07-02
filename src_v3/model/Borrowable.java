package model;

/**
 * Borrowable — interface defining the contract for any library item
 * that can be lent out to a patron and returned.
 *
 * Part of the COS 202 SLCAS OOP hierarchy.
 */
public interface Borrowable {

    /**
     * Attempt to borrow this item for the given user.
     *
     * @param userId the ID of the patron requesting the loan
     * @return true if the borrow was successful; false if already on loan
     */
    boolean borrow(String userId);

    /**
     * Return this item from the given user.
     *
     * @param userId the ID of the patron returning the item
     * @return true if the return was successful; false if not on loan
     */
    boolean returnItem(String userId);

    /**
     * Check whether this item is currently available for borrowing.
     *
     * @return true if not currently on loan
     */
    boolean isAvailable();
}
