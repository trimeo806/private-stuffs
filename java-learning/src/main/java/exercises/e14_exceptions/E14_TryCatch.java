package exercises.e14_exceptions;

/**
 * Exercise 14: Exception Handling
 *
 * Java has checked exceptions (must be handled) and unchecked exceptions (optional).
 * This is very different from TypeScript/JavaScript which only has unchecked.
 *
 * Checked: IOException, SQLException â€” compiler forces you to handle
 * Unchecked: NullPointerException, ArrayIndexOutOfBoundsException â€” runtime errors
 */
public class E14_TryCatch {

    public static void main(String[] args) {

        // === EXERCISE 1: Basic try-catch ===
        // TODO: Wrap this in try-catch and handle ArithmeticException
        //       Print a friendly error message instead of crashing
        int result = 10 / 0;


        // === EXERCISE 2: Multiple catch blocks ===
        // TODO: Write code that might throw different exceptions:
        //       - Access array index out of bounds â†’ ArrayIndexOutOfBoundsException
        //       - Parse "abc" as integer â†’ NumberFormatException
        //       Catch each separately with a different message
        String[] data = {"10", "abc", "30"};


        // === EXERCISE 3: Finally block ===
        // TODO: Open a resource (simulate with a print "Opening resource")
        //       Do some work that might fail
        //       Use 'finally' to ensure "Closing resource" always prints
        //       (In real code, this pattern manages DB connections, file handles, etc.)


        // === EXERCISE 4: Try-with-resources (Java 7+) ===
        // TODO: This is Java's equivalent of a finally-close pattern
        //       Read a file using try-with-resources:
        //       try (var scanner = new java.util.Scanner(new java.io.File("nonexistent.txt"))) {
        //           // read file
        //       } catch (java.io.FileNotFoundException e) {
        //           System.out.println("File not found: " + e.getMessage());
        //       }
        //       The scanner is auto-closed when the block exits


        // === EXERCISE 5: Throw your own exception ===
        // TODO: Write a method 'validateAge(int age)' that:
        //       - Throws IllegalArgumentException if age < 0
        //       - Throws IllegalArgumentException if age > 150
        //       - Prints "Valid age: X" otherwise
        //       Call it with -5, 200, and 25

        // validateAge(-5);
        // validateAge(200);
        // validateAge(25);


        // === EXERCISE 6: Exception chaining ===
        // TODO: Catch an exception and wrap it in a new one:
        //       try {
        //           Integer.parseInt("not_a_number");
        //       } catch (NumberFormatException e) {
        //           throw new RuntimeException("Failed to process input", e);
        //       }
        //       The original exception is preserved as the "cause"


        System.out.println("=== Exercise 14 Complete ===");
    }

    // TODO: Implement for Exercise 5
    static void validateAge(int age) {
        // Your implementation here
    }
}
