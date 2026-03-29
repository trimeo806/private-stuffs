package exercises.e11_collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Exercise 11: Lists (ArrayList, LinkedList)
 *
 * ArrayList = dynamic array (like JavaScript's Array)
 * LinkedList = doubly-linked list (better for frequent insert/remove at head)
 *
 * Both implement the List interface â€” program to the interface!
 */
public class E11_ListsAndArrayList {

    public static void main(String[] args) {

        // === EXERCISE 1: ArrayList basics ===
        // TODO: Create an ArrayList<String> of programming languages
        //       Add: "Java", "TypeScript", "Python", "Go", "Rust"
        //       Print the list


        // === EXERCISE 2: List operations ===
        // TODO: Using the list above:
        //       - Get the element at index 2
        //       - Remove "Python" by value
        //       - Insert "Kotlin" at index 1
        //       - Check if "Java" is in the list (contains)
        //       - Print the size


        // === EXERCISE 3: List.of() â€” immutable list (Java 9+) ===
        // TODO: Create an immutable list: List.of("a", "b", "c")
        //       Try adding to it â€” what exception do you get?


        // === EXERCISE 4: Sorting ===
        List<Integer> numbers = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3, 7));
        // TODO: Sort the list in ascending order using Collections.sort()
        // TODO: Sort in descending order using Collections.sort() with Collections.reverseOrder()
        // TODO: Print both results


        // === EXERCISE 5: Iterating ===
        List<String> names = new ArrayList<>(List.of("Alice", "Bob", "Charlie", "Diana"));
        // TODO: Iterate using:
        //       1. for-each loop
        //       2. for loop with index
        //       3. forEach() with lambda: names.forEach(name -> ...)


        // === EXERCISE 6: List to Array and back ===
        // TODO: Convert names List to a String[] array: names.toArray(new String[0])
        // TODO: Convert a String[] back to a List: List.of(array) or Arrays.asList(array)


        // === EXERCISE 7: ArrayList vs LinkedList ===
        // TODO: Create both an ArrayList<Integer> and LinkedList<Integer>
        //       Add 100,000 elements to each
        //       Time how long it takes to insert at index 0 for 1000 iterations
        //       Which is faster for head insertions? Why?
        //       Use System.nanoTime() for timing


        System.out.println("=== Exercise 11 Complete ===");
    }
}
