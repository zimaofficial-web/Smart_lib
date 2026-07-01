package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchEngine — provides search algorithms required by COS 202.
 * Includes linear, binary, and recursive searches.
 */
public class SearchEngine {

    // Linear search: substring match, case-insensitive
    public static List<LibraryItem> linearSearch(List<LibraryItem> list, String query, String field) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (LibraryItem item : list) {
            boolean match = false;
            switch (field.toLowerCase()) {
                case "title":
                    match = item.getTitle().toLowerCase().contains(q);
                    break;
                case "author":
                    match = item.getAuthor().toLowerCase().contains(q);
                    break;
                case "type":
                    match = item.getType().toLowerCase().contains(q);
                    break;
            }
            if (match) results.add(item);
        }
        return results;
    }

    // Binary search: exact match (first occurrence)
    // Assumes list is already sorted by title!
    public static LibraryItem binarySearch(List<LibraryItem> list, String titleQuery) {
        int left = 0;
        int right = list.size() - 1;
        String q = titleQuery.toLowerCase();
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LibraryItem midItem = list.get(mid);
            int cmp = midItem.getTitle().toLowerCase().compareTo(q);
            if (cmp == 0) return midItem;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null; // Not found
    }

    // Recursive search: substring match, case-insensitive, title or author
    public static List<LibraryItem> recursiveSearch(List<LibraryItem> list, String query) {
        return recursiveSearchHelper(list, query.toLowerCase(), 0, new ArrayList<>());
    }

    private static List<LibraryItem> recursiveSearchHelper(List<LibraryItem> list, String query, int index, List<LibraryItem> results) {
        if (index >= list.size()) {
            return results;
        }
        LibraryItem item = list.get(index);
        if (item.getTitle().toLowerCase().contains(query) || item.getAuthor().toLowerCase().contains(query)) {
            results.add(item);
        }
        return recursiveSearchHelper(list, query, index + 1, results);
    }
}
