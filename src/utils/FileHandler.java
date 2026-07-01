package utils;

import controller.LibraryManager;
import controller.BorrowController;
import model.Book;
import model.Journal;
import model.LibraryItem;
import model.Magazine;
import model.UserAccount;

import java.io.*;
import java.util.*;

/**
 * Persistent data storage (JSON). Hand-built to avoid external dependencies.
 */
public class FileHandler {

    public static void saveToJSON(LibraryManager lib, BorrowController bc, String path) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.println("{");
            out.println("  \"users\": [");
            for (int i = 0; i < lib.getUsers().size(); i++) {
                UserAccount u = lib.getUsers().get(i);
                out.print("    { ");
                out.print("\"userId\": \"" + u.getUserId() + "\", ");
                out.print("\"name\": \"" + u.getName() + "\", ");
                out.print("\"email\": \"" + u.getEmail() + "\", ");
                out.print("\"borrowHistory\": " + stringListToJson(u.getBorrowHistory()) + ", ");
                out.print("\"activeLoans\": " + stringListToJson(u.getActiveLoans()) + ", ");
                out.print("\"loanDates\": " + longListToJson(u.getLoanDates()));
                out.print(" }");
                if (i < lib.getUsers().size() - 1) out.println(","); else out.println();
            }
            out.println("  ],");

            out.println("  \"catalogue\": [");
            for (int i = 0; i < lib.getCatalogue().size(); i++) {
                LibraryItem item = lib.getCatalogue().get(i);
                out.print("    { ");
                out.print("\"id\": \"" + item.getId() + "\", ");
                out.print("\"type\": \"" + item.getType() + "\", ");
                out.print("\"title\": \"" + escapeJson(item.getTitle()) + "\", ");
                out.print("\"author\": \"" + escapeJson(item.getAuthor()) + "\", ");
                out.print("\"year\": " + item.getYear() + ", ");
                out.print("\"available\": " + item.isAvailable() + ", ");
                out.print("\"borrowedBy\": \"" + item.getBorrowedBy() + "\", ");
                out.print("\"borrowCount\": " + item.getBorrowCount() + ", ");
                out.print("\"extra1\": \"" + escapeJson(item.getExtraField1()) + "\", ");
                out.print("\"extra2\": \"" + escapeJson(item.getExtraField2()) + "\"");
                out.print(" }");
                if (i < lib.getCatalogue().size() - 1) out.println(","); else out.println();
            }
            out.println("  ],");

            out.println("  \"waitlists\": [");
            int wCount = 0;
            HashMap<String, Queue<String>> wMap = bc.getWaitlists();
            for (String itemId : wMap.keySet()) {
                Queue<String> q = wMap.get(itemId);
                if (q.isEmpty()) continue;
                if (wCount > 0) out.println(",");
                out.print("    { \"itemId\": \"" + itemId + "\", \"queue\": " + stringListToJson(new ArrayList<>(q)) + " }");
                wCount++;
            }
            if (wCount > 0) out.println();
            out.println("  ]");

            out.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String stringListToJson(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String longListToJson(ArrayList<Long> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void loadFromJSON(LibraryManager lib, BorrowController bc, String path) {
        File file = new File(path);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int section = 0; // 1 = users, 2 = catalogue, 3 = waitlists
            while ((line = br.readLine()) != null) {
                if (line.contains("\"users\":")) section = 1;
                else if (line.contains("\"catalogue\":")) section = 2;
                else if (line.contains("\"waitlists\":")) section = 3;
                else if (line.trim().startsWith("{")) {
                    if (section == 1) parseUser(line, lib);
                    else if (section == 2) parseItem(line, lib);
                    else if (section == 3) parseWaitlist(line, bc);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON. Corrupted or invalid format.");
            e.printStackTrace();
        }
    }

    private static void parseUser(String json, LibraryManager lib) {
        String userId = extractString(json, "userId");
        String name = extractString(json, "name");
        String email = extractString(json, "email");
        UserAccount u = new UserAccount(userId, name, email);
        u.setBorrowHistory(extractStringList(json, "borrowHistory"));
        u.setActiveLoans(extractStringList(json, "activeLoans"));
        u.setLoanDates(extractLongList(json, "loanDates"));
        lib.getUsers().add(u);
    }

    private static void parseItem(String json, LibraryManager lib) {
        String id = extractString(json, "id");
        String type = extractString(json, "type");
        String title = extractString(json, "title");
        String author = extractString(json, "author");
        int year = extractInt(json, "year");
        boolean available = extractBoolean(json, "available");
        String borrowedBy = extractString(json, "borrowedBy");
        int borrowCount = extractInt(json, "borrowCount");
        String extra1 = extractString(json, "extra1");
        String extra2 = extractString(json, "extra2");

        LibraryItem item;
        if (type.equals("Book")) item = new Book(id, title, author, year, extra1, extra2);
        else if (type.equals("Magazine")) item = new Magazine(id, title, author, year, extra1, extra2);
        else item = new Journal(id, title, author, year, extra1, extra2);

        item.setAvailable(available);
        item.setBorrowedBy(borrowedBy);
        item.setBorrowCount(borrowCount);
        lib.getCatalogue().add(item);
    }

    private static void parseWaitlist(String json, BorrowController bc) {
        String itemId = extractString(json, "itemId");
        ArrayList<String> queueList = extractStringList(json, "queue");
        Queue<String> q = new LinkedList<>(queueList);
        bc.getWaitlists().put(itemId, q);
    }

    private static String extractString(String json, String key) {
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static int extractInt(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        try { return Integer.parseInt(json.substring(start, end).trim()); } catch (Exception e) { return 0; }
    }

    private static boolean extractBoolean(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start == -1) return false;
        start += search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).trim().equals("true");
    }

    private static ArrayList<String> extractStringList(String json, String key) {
        ArrayList<String> list = new ArrayList<>();
        String search = "\"" + key + "\": [";
        int start = json.indexOf(search);
        if (start == -1) return list;
        start += search.length();
        int end = json.indexOf("]", start);
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return list;
        String[] parts = arrayContent.split(",");
        for (String p : parts) {
            list.add(p.replace("\"", "").trim());
        }
        return list;
    }

    private static ArrayList<Long> extractLongList(String json, String key) {
        ArrayList<Long> list = new ArrayList<>();
        String search = "\"" + key + "\": [";
        int start = json.indexOf(search);
        if (start == -1) return list;
        start += search.length();
        int end = json.indexOf("]", start);
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return list;
        String[] parts = arrayContent.split(",");
        for (String p : parts) {
            try { list.add(Long.parseLong(p.trim())); } catch(Exception ignored){}
        }
        return list;
    }
}
