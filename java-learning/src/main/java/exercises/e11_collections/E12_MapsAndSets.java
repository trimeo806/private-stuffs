package exercises.e11_collections;

import java.util.*;

/**
 * Exercise 12: Maps and Sets
 *
 * HashMap = like JavaScript's Map or plain object (unordered)
 * TreeMap = sorted by key (like a sorted Map)
 * LinkedHashMap = insertion order preserved
 *
 * HashSet = like JavaScript's Set (unique values, unordered)
 * TreeSet = sorted set
 */
public class E12_MapsAndSets {

    public static void main(String[] args) {

        // === EXERCISE 1: HashMap basics ===
        // TODO: Create a HashMap<String, Integer> for student scores
        //       Add: "Alice"->95, "Bob"->87, "Charlie"->92
        //       Print the map


        // === EXERCISE 2: Map operations ===
        // TODO: Using the map:
        //       - Get Alice's score
        //       - Check if "Diana" exists (containsKey)
        //       - Get Diana's score with a default: getOrDefault("Diana", 0)
        //       - Remove Bob
        //       - Print all keys (keySet())
        //       - Print all values (values())
        //       - Print all key-value pairs (entrySet())


        // === EXERCISE 3: Map iteration ===
        Map<String, String> capitals = new HashMap<>();
        capitals.put("Vietnam", "Hanoi");
        capitals.put("Japan", "Tokyo");
        capitals.put("France", "Paris");
        capitals.put("USA", "Washington D.C.");

        // TODO: Iterate using entrySet():
        //       for (Map.Entry<String, String> entry : capitals.entrySet()) { ... }
        // TODO: Iterate using forEach lambda:
        //       capitals.forEach((country, capital) -> ...)


        // === EXERCISE 4: Word frequency counter ===
        String text = "the quick brown fox jumps over the lazy dog the fox";
        // TODO: Count frequency of each word using a HashMap<String, Integer>
        //       Split by space, then for each word:
        //       map.merge(word, 1, Integer::sum)  // or use getOrDefault
        //       Print the frequency map


        // === EXERCISE 5: HashSet ===
        // TODO: Create a HashSet<String> with some duplicate values
        //       List.of("apple", "banana", "apple", "cherry", "banana")
        //       Print it â€” are duplicates kept?


        // === EXERCISE 6: Set operations ===
        Set<Integer> setA = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Set.of(4, 5, 6, 7, 8));
        // TODO: Find the intersection (elements in both): retainAll
        // TODO: Find the union (all elements): addAll
        // TODO: Find the difference (in A but not B): removeAll
        //       CAREFUL: these methods modify the original set!
        //       Create copies first: new HashSet<>(setA)


        // === EXERCISE 7: Map.of() â€” immutable map (Java 9+) ===
        // TODO: Create an immutable map: Map.of("key1", "val1", "key2", "val2")
        //       Try to modify it â€” what happens?


        System.out.println("=== Exercise 12 Complete ===");
    }
}
