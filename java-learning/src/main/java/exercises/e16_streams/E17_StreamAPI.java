package exercises.e16_streams;

import java.util.*;
import java.util.stream.*;

/**
 * Exercise 17: Stream API
 *
 * Streams are Java's equivalent of JavaScript's array methods:
 *   .filter()  â†’ .filter()
 *   .map()     â†’ .map()
 *   .reduce()  â†’ .reduce()
 *   .find()    â†’ .findFirst()
 *   .some()    â†’ .anyMatch()
 *   .every()   â†’ .allMatch()
 *
 * Key difference: Java streams are lazy and single-use.
 */
public class E17_StreamAPI {

    record Person(String name, int age, String city) {}

    public static void main(String[] args) {

        List<Person> people = List.of(
            new Person("Alice", 30, "Hanoi"),
            new Person("Bob", 25, "Tokyo"),
            new Person("Charlie", 35, "Hanoi"),
            new Person("Diana", 28, "Paris"),
            new Person("Eve", 22, "Tokyo"),
            new Person("Frank", 40, "Hanoi"),
            new Person("Grace", 33, "Paris")
        );

        // === EXERCISE 1: filter + collect ===
        // TODO: Get all people older than 28
        //       .stream().filter(p -> p.age() > 28).collect(Collectors.toList())


        // === EXERCISE 2: map + collect ===
        // TODO: Get a list of all names (List<String>)
        //       .stream().map(Person::name).toList()


        // === EXERCISE 3: filter + map chain ===
        // TODO: Get names of people from Hanoi who are older than 25


        // === EXERCISE 4: Sorting ===
        // TODO: Sort people by age ascending, then by name alphabetically
        //       .sorted(Comparator.comparingInt(Person::age).thenComparing(Person::name))


        // === EXERCISE 5: Reduction ===
        // TODO: Calculate the average age of all people
        //       .stream().mapToInt(Person::age).average().orElse(0)

        // TODO: Find the oldest person
        //       .stream().max(Comparator.comparingInt(Person::age))

        // TODO: Calculate total age using reduce:
        //       .stream().map(Person::age).reduce(0, Integer::sum)


        // === EXERCISE 6: Grouping ===
        // TODO: Group people by city â†’ Map<String, List<Person>>
        //       Collectors.groupingBy(Person::city)

        // TODO: Count people per city â†’ Map<String, Long>
        //       Collectors.groupingBy(Person::city, Collectors.counting())


        // === EXERCISE 7: Matching ===
        // TODO: Is anyone from "London"? â†’ anyMatch
        // TODO: Is everyone older than 18? â†’ allMatch
        // TODO: Is no one older than 50? â†’ noneMatch


        // === EXERCISE 8: FlatMap ===
        List<List<Integer>> nested = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6, 7, 8, 9)
        );
        // TODO: Flatten into a single List<Integer> using flatMap
        //       .stream().flatMap(Collection::stream).toList()


        // === EXERCISE 9: Collectors.joining ===
        // TODO: Join all names with ", " separator
        //       .stream().map(Person::name).collect(Collectors.joining(", "))


        // === EXERCISE 10: Create a stream from scratch ===
        // TODO: Generate the first 10 Fibonacci numbers using Stream.iterate
        //       Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
        //             .limit(10)
        //             .map(f -> f[0])
        //             .toList()


        System.out.println("=== Exercise 17 Complete ===");
    }
}
