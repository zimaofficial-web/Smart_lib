package utils;

import java.util.UUID;

/**
 * Utility for generating unique IDs for travel resources and travelers.
 */
public class IdGenerator {
    
    public static String generateResourceId() {
        return "TRV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static String generateTravelerId() {
        return "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
