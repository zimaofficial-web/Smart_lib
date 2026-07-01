package controller;

import model.LibraryItem;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * SortEngine — provides 4 sorting algorithms as required by COS 202.
 * Includes Selection, Insertion, Merge, and Quick Sort.
 */
public class SortEngine {

    public static void selectionSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (c.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                LibraryItem temp = list.get(minIdx);
                list.set(minIdx, list.get(i));
                list.set(i, temp);
            }
        }
    }

    public static void insertionSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            LibraryItem key = list.get(i);
            int j = i - 1;
            while (j >= 0 && c.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }

    public static void mergeSort(List<LibraryItem> list, Comparator<LibraryItem> c) {
        if (list.size() < 2) return;
        int mid = list.size() / 2;
        List<LibraryItem> left = new ArrayList<>(list.subList(0, mid));
        List<LibraryItem> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, c);
        mergeSort(right, c);

        merge(list, left, right, c);
    }

    private static void merge(List<LibraryItem> list, List<LibraryItem> left, List<LibraryItem> right, Comparator<LibraryItem> c) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (c.compare(left.get(i), right.get(j)) <= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }
        while (i < left.size()) list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }

    public static void quickSort(List<LibraryItem> list, int begin, int end, Comparator<LibraryItem> c) {
        if (begin < end) {
            int partitionIndex = partition(list, begin, end, c);
            quickSort(list, begin, partitionIndex - 1, c);
            quickSort(list, partitionIndex + 1, end, c);
        }
    }

    private static int partition(List<LibraryItem> list, int begin, int end, Comparator<LibraryItem> c) {
        LibraryItem pivot = list.get(end);
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (c.compare(list.get(j), pivot) <= 0) {
                i++;
                LibraryItem swapTemp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, swapTemp);
            }
        }
        LibraryItem swapTemp = list.get(i + 1);
        list.set(i + 1, list.get(end));
        list.set(end, swapTemp);
        return i + 1;
    }
}
