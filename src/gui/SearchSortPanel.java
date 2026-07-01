package gui;

import controller.LibraryManager;
import controller.SearchEngine;
import controller.SortEngine;
import model.LibraryItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchSortPanel extends JPanel {
    private LibraryManager libraryManager;
    private JTable resultTable;
    private ItemTableModel tableModel;

    public SearchSortPanel(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        
        // Search section
        JPanel searchP = new JPanel(new FlowLayout());
        searchP.setBorder(BorderFactory.createTitledBorder("Search"));
        JComboBox<String> searchAlgo = new JComboBox<>(new String[]{"Linear", "Binary", "Recursive"});
        JComboBox<String> searchField = new JComboBox<>(new String[]{"Title", "Author", "Type"});
        JTextField searchTxt = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        searchP.add(new JLabel("Algo:")); searchP.add(searchAlgo);
        searchP.add(new JLabel("Field:")); searchP.add(searchField);
        searchP.add(searchTxt); searchP.add(searchBtn);
        
        searchBtn.addActionListener(e -> {
            String algo = (String) searchAlgo.getSelectedItem();
            String field = (String) searchField.getSelectedItem();
            String query = searchTxt.getText().trim();
            if (query.isEmpty()) return;

            List<LibraryItem> currentList = libraryManager.getCatalogue();
            List<LibraryItem> res = new ArrayList<>();

            if (algo.equals("Linear")) {
                res = SearchEngine.linearSearch(currentList, query, field);
            } else if (algo.equals("Binary")) {
                if (!field.equals("Title")) {
                    JOptionPane.showMessageDialog(this, "Binary search only supports Title in this demo (assumes sorted by Title).");
                    return;
                }
                LibraryItem found = SearchEngine.binarySearch(currentList, query);
                if (found != null) res.add(found);
            } else {
                res = SearchEngine.recursiveSearch(currentList, query);
            }
            tableModel.setItems(new ArrayList<>(res));
        });

        // Sort section
        JPanel sortP = new JPanel(new FlowLayout());
        sortP.setBorder(BorderFactory.createTitledBorder("Sort In-Place"));
        JComboBox<String> sortAlgo = new JComboBox<>(new String[]{"Selection", "Insertion", "Merge", "Quick"});
        JComboBox<String> sortField = new JComboBox<>(new String[]{"Title", "Author", "Year"});
        JButton sortBtn = new JButton("Sort Database");

        sortP.add(new JLabel("Algo:")); sortP.add(sortAlgo);
        sortP.add(new JLabel("By:")); sortP.add(sortField);
        sortP.add(sortBtn);

        sortBtn.addActionListener(e -> {
            String algo = (String) sortAlgo.getSelectedItem();
            String field = (String) sortField.getSelectedItem();
            List<LibraryItem> cat = libraryManager.getCatalogue();

            Comparator<LibraryItem> c = (o1, o2) -> 0;
            if (field.equals("Title")) c = (o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle());
            else if (field.equals("Author")) c = (o1, o2) -> o1.getAuthor().compareToIgnoreCase(o2.getAuthor());
            else if (field.equals("Year")) c = (o1, o2) -> Integer.compare(o1.getYear(), o2.getYear());

            long start = System.currentTimeMillis();
            if (algo.equals("Selection")) SortEngine.selectionSort(cat, c);
            else if (algo.equals("Insertion")) SortEngine.insertionSort(cat, c);
            else if (algo.equals("Merge")) SortEngine.mergeSort(cat, c);
            else if (algo.equals("Quick")) SortEngine.quickSort(cat, 0, cat.size() - 1, c);
            long time = System.currentTimeMillis() - start;

            JOptionPane.showMessageDialog(this, "Sorted " + cat.size() + " items using " + algo + " sort in " + time + "ms.");
            tableModel.setItems(new ArrayList<>(cat)); // refresh view
        });

        topPanel.add(searchP);
        topPanel.add(sortP);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new ItemTableModel(new ArrayList<>());
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }
}
