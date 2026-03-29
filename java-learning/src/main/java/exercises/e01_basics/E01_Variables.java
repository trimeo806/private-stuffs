package exercises.e01_basics;

/**
 * Exercise 01: Variables and Data Types
 *
 * Java is statically typed â€” every variable must declare its type.
 * Think of it like TypeScript's strict mode, but enforced by the compiler.
 *
 * Primitive types: byte, short, int, long, float, double, char, boolean
 * Reference types: String, arrays, objects
 */
public class E01_Variables {

    public static void main(String[] args) {

        // === EXERCISE 1: Declare variables ===
        // TODO: Declare an int called 'age' with value 25
        // TODO: Declare a double called 'salary' with value 75000.50
        // TODO: Declare a boolean called 'isActive' with value true
        // TODO: Declare a String called 'name' with your name
        int age = 25;
        double saraly = 75000.50;
        boolean isActive = true;
        String name = "Tri";

        // === EXERCISE 2: Type casting ===
        // TODO: Cast the double 9.99 to an int and store in 'rounded'
        // What value do you expect? (hint: it truncates, not rounds)
        int rounded = (int) 9.99;
        System.out.println("Rounded value: " + rounded); // Expect 9

        // TODO: Cast the int 65 to a char and store in 'letter'
        // Print it â€” what character is it?
        char letter = (char) 65;
        System.out.println("Letter: " + letter); // Expect 'A'

        // === EXERCISE 3: var keyword (Java 10+) ===
        // TODO: Use 'var' to declare a variable 'message' = "Hello Java"
        // Then try: var x; â€” does it compile? Why not?
        var message = "Hello Java";
        // var x; // This won't compile because 'var' requires an initializer

        // === EXERCISE 4: Constants ===
        // TODO: Declare a constant PI using 'final' keyword with value 3.14159
        final double PI = 3.14159;

        // === EXERCISE 5: String formatting ===
        // TODO: Print "My name is {name}, I am {age} years old"
        // Use String.format() or printf()
        System.out.printf("My name is %s, I am %d years old%n", name, age);

        // === EXERCISE 6: Null handling ===
        // TODO: Declare a String variable 'nickname' as null
        // TODO: Try calling nickname.length() â€” what happens?
        // TODO: Write an if-check to safely print the length only if not null
        String nickname = null;
        // nickname.length(); // This would throw NullPointerException
        if (nickname != null) {
            System.out.println("Nickname length: " + nickname.length());
        } else {
            System.out.println("Nickname is null, cannot get length.");
        }

        System.out.println("=== Exercise 01 Complete ===");
    }
}
