package utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility to generate unique item and user IDs.
 */
public class IDGenerator {
    private static AtomicInteger itemCounter = new AtomicInteger(1000);
    private static AtomicInteger userCounter = new AtomicInteger(1000);

    public static String generateItemId(String type) {
        String prefix = "ITM";
        if (type.equalsIgnoreCase("Book")) prefix = "BK";
        else if (type.equalsIgnoreCase("Magazine")) prefix = "MG";
        else if (type.equalsIgnoreCase("Journal")) prefix = "JN";
        return prefix + "-" + itemCounter.incrementAndGet();
    }

    public static String generateUserId() {
        return "USR-" + userCounter.incrementAndGet();
    }
}
