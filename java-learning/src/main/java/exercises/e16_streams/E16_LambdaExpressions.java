package exercises.e16_streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

/**
 * Exercise 16: Lambda Expressions and Functional Interfaces
 *
 * Lambdas in Java are like arrow functions in TypeScript:
 *   TS:   (x) => x * 2
 *   Java: (x) -> x * 2
 *
 * But in Java, every lambda must match a @FunctionalInterface
 * (an interface with exactly one abstract method).
 */
public class E16_LambdaExpressions {

    public static void main(String[] args) {

        // === EXERCISE 1: Basic lambda ===
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Diana"));

        // TODO: Sort names alphabetically using a lambda:
        //       names.sort((a, b) -> a.compareTo(b));
        //       Print the sorted list

        // TODO: Sort names by length using a lambda:
        //       names.sort((a, b) -> Integer.compare(a.length(), b.length()));


        // === EXERCISE 2: Built-in functional interfaces ===

        // Predicate<T> â€” takes T, returns boolean (like a filter)
        // TODO: Create a Predicate<String> that checks if a string length > 3
        //       Predicate<String> isLong = s -> s.length() > 3;
        //       Test it with "Hi" and "Hello"


        // Function<T, R> â€” takes T, returns R (like a mapper)
        // TODO: Create a Function<String, Integer> that returns string length
        //       Apply it to "Hello World"


        // Consumer<T> â€” takes T, returns nothing (like forEach callback)
        // TODO: Create a Consumer<String> that prints in uppercase
        //       Use it with names.forEach(yourConsumer)


        // Supplier<T> â€” takes nothing, returns T (like a factory)
        // TODO: Create a Supplier<Double> that returns a random number
        //       Call .get() three times


        // === EXERCISE 3: Method references ===
        // TODO: Replace these lambdas with method references:
        //       names.forEach(s -> System.out.println(s))  â†’  names.forEach(System.out::println)
        //       names.sort((a, b) -> a.compareTo(b))       â†’  names.sort(String::compareTo)
        //       List<String> uppers = names.stream().map(s -> s.toUpperCase()).toList()
        //                                              â†’  .map(String::toUpperCase)


        // === EXERCISE 4: Composing functions ===
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> addTen = x -> x + 10;

        // TODO: Compose them:
        //       doubleIt.andThen(addTen) â†’ first double, then add 10
        //       doubleIt.compose(addTen) â†’ first add 10, then double
        //       Apply both to value 5 and print results


        // === EXERCISE 5: Custom functional interface ===
        // TODO: Create a @FunctionalInterface called 'StringProcessor'
        //       with method: String process(String input)
        //       Create lambdas for: trim, uppercase, add "!!!" suffix
        //       Chain them manually: result = addSuffix.process(toUpper.process(trim.process(input)))


        System.out.println("=== Exercise 16 Complete ===");
    }

    // TODO: Define StringProcessor interface here for Exercise 5
    // @FunctionalInterface
    // interface StringProcessor {
    //     String process(String input);
    // }
}
