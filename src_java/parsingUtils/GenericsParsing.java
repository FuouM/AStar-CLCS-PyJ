package parsingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GenericsParsing {
    @SafeVarargs
    public static <T> List<T> getSigma(List<T>... inputs) {
        // Create a new HashSet to store unique items
        HashSet<T> set = new HashSet<>();
        for (List<T> input : inputs) {
            // Add items from each sequence to the set
            // Duplicates are automatically handled by the HashSet
            set.addAll(input);
        }
        // Convert the set back to a list and return
        return new ArrayList<>(set);
    }

    public static <T> Map<T, Integer> getItemToIndexMap(List<T> list) {
        // Create a new HashMap to store the mapping
        Map<T, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            // Map each item to its index
            map.put(list.get(i), i);
        }
        return map;
    }

    public static <T> Map<Integer, T> getIndexToItemMap(List<T> list) {
        // Create a new HashMap to store the mapping
        Map<Integer, T> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            // Map each index to its corresponding item
            map.put(i, list.get(i));
        }
        return map;
    }

    public static <T, U> List<U> mapItems(List<T> items, Map<T, U> map) {
        // Create a new ArrayList to store the mapped items
        List<U> mappedItems = new ArrayList<>();
        for (T item : items) {
            // Add the mapped item to the list
            mappedItems.add(map.get(item));
        }
        return mappedItems;
    }
}
