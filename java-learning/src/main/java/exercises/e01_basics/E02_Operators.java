package exercises.e01_basics;

/**
 * Exercise 02: Operators
 *
 * Arithmetic, comparison, logical, and bitwise operators.
 * Mostly identical to JavaScript/TypeScript, with a few gotchas.
 */
public class E02_Operators {

    public static void main(String[] args) {

        // === EXERCISE 1: Integer division ===
        // TODO: What does 7 / 2 return in Java? Print it.
        System.out.println("7 / 2 = " + (7 / 2));
        // TODO: How do you get 3.5 instead? (hint: cast one operand)
        System.out.println("7 / 2 = " + ((double) 7 / 2));

        // === EXERCISE 2: Modulo ===
        // TODO: Use % to check if 17 is odd or even. Print the result.
        int number = 17;
        String parity = (number % 2 == 0) ? "even" : "odd";
        System.out.println(number + " is " + parity);

        // === EXERCISE 3: Comparison â€” == vs .equals() ===
        String a = new String("hello");
        String b = new String("hello");

        // TODO: Print (a == b) â€” what does it return and why?
        System.out.println("a == b: " + (a == b)); // Expect false, different objects
        // TODO: Print (a.equals(b)) â€” what does it return and why?
        System.out.println("a.equals(b): " + a.equals(b)); // Expect true, same content
        // KEY INSIGHT: == compares references (memory address), .equals() compares
        // content

        // === EXERCISE 4: Ternary operator ===
        int score = 85;
        // TODO: Use ternary to assign "Pass" or "Fail" to a String 'result'
        // Pass if score >= 60
        String result = (score >= 60) ? "Pass" : "Fail";
        System.out.println("Score: " + score + ", Result: " + result);

        // === EXERCISE 5: Short-circuit evaluation ===
        // TODO: Given x = 0, what does (x != 0 && 10 / x > 2) evaluate to?
        // Does it throw a division-by-zero error? Why not?
        int x = 0;
        boolean shortCircuit = (x != 0 && 10 / x > 2);
        System.out.println("Short-circuit result: " + shortCircuit);
        // KEY INSIGHT: The second part (10 / x > 2) is never evaluated because x != 0 is false, so it doesn't throw an error.

        // === EXERCISE 6: Increment/Decrement ===
        int count = 5;
        // TODO: What is the difference between count++ and ++count?
        // Print both in separate statements to observe the difference.
        int val1 = count++; // post-increment
        int val2 = ++count; // pre-increment
        System.out.println("Post-increment (val1): " + val1);
        System.out.println("Pre-increment (val2): " + val2);

        System.out.println("=== Exercise 02 Complete ===");
    }
}
