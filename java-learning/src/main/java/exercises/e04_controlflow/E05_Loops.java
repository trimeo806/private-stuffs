package exercises.e04_controlflow;

/**
 * Exercise 05: Loops
 *
 * for, while, do-while, and enhanced for-each.
 * Java doesn't have for...of or for...in like JavaScript.
 * The enhanced for loop (for-each) is the closest equivalent.
 */
public class E05_Loops {

    public static void main(String[] args) {

        // === EXERCISE 1: Classic for loop ===
        // TODO: Print numbers 1 to 10 on the same line, separated by spaces
        for (int i = 1; i <= 10; i++) {
            System.out.print(i + " ");
        }

        // === EXERCISE 2: While loop ===
        // TODO: Starting from 1, keep doubling until the value exceeds 1000
        // Print each value
        int value = 1;
        while (value <= 1000) {
            System.out.println(value);
            value *= 2;
        }

        // === EXERCISE 3: Do-while ===
        // TODO: Simulate a simple menu that runs at least once:
        // Print "Menu shown: attempt X" where X counts from 1
        // Stop after 3 attempts
        // (do-while guarantees the body runs at least once)
        int attempts = 1;
        do {
            System.out.println("Menu shown: attempt " + attempts);
            attempts++;
        } while (attempts <= 3);

        // === EXERCISE 4: Enhanced for-each ===
        String[] fruits = { "apple", "banana", "cherry", "date", "elderberry" };
        // TODO: Print each fruit using enhanced for loop
        // Syntax: for (String fruit : fruits) { ... }
        for (String fruit : fruits) {
            System.out.println(fruit);
        }

        // === EXERCISE 5: Nested loops â€” multiplication table ===
        // TODO: Print a 5x5 multiplication table
        // Expected output:
        // 1 2 3 4 5
        // 2 4 6 8 10
        // 3 6 9 12 15
        // 4 8 12 16 20
        // 5 10 15 20 25
        // Hint: use System.out.printf("%4d", value) for alignment
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                System.out.printf("%4d", i * j);
            }
            System.out.println(); // New line after each row
        }

        // === EXERCISE 6: Break and Continue ===
        // TODO: Loop from 1 to 20
        // Skip even numbers (use 'continue')
        // Stop the loop when you reach 15 (use 'break')
        // Print each number that passes both conditions
        for (int i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                continue; // Skip even numbers
            }
            if (i > 15) {
                break; // Stop loop when i exceeds 15
            }
            System.out.println(i); // Print odd numbers up to 15
        }
        // === EXERCISE 7: Labeled break (Java-specific) ===
        // TODO: Use a labeled break to exit BOTH loops when i*j == 12
        outer: for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (i * j == 12)
                    break outer;
            }
        }

        System.out.println("=== Exercise 05 Complete ===");
    }
}
