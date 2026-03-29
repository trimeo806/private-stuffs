package exercises.e01_basics;

/**
 * Exercise 03: String Methods
 *
 * Strings in Java are immutable objects (like TypeScript).
 * Every method returns a NEW string â€” the original is never modified.
 */
public class E03_StringMethods {

    public static void main(String[] args) {

        String text = "  Hello, World! Welcome to Java  ";

        // === EXERCISE 1: Basic methods ===
        // TODO: Print the length of 'text'
        // TODO: Print 'text' in all uppercase
        // TODO: Print 'text' in all lowercase
        // TODO: Print 'text' with leading/trailing spaces removed (trim)
        text.length();
        text.toUpperCase();
        text.toLowerCase();
        text.trim();

        // === EXERCISE 2: Searching ===
        // TODO: Check if 'text' contains "Java" â€” print true/false
        // TODO: Find the index of "World" in 'text'
        // TODO: Check if 'text' starts with "Hello" (careful â€” leading spaces!)
        if (text.contains("Java")) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        // === EXERCISE 3: Extracting ===
        // TODO: Extract "World" from 'text' using substring()
        // TODO: Split "apple,banana,cherry" by comma into a String array
        // Print each fruit on a new line using a for-each loop
        String world = text.substring(text.indexOf("W") + "World".length());
        String fruits = "apple,banana,cherry";
        String[] fruitArray = fruits.split(",");

        // === EXERCISE 4: Replacing ===
        // TODO: Replace "World" with "Developer" in 'text'
        // TODO: Remove all spaces from 'text' using replace()
        String replacedText = text.replace("World", "Developer");
        String noSpaces = text.replace(" ", "");

        // === EXERCISE 5: StringBuilder â€” mutable strings ===
        // When concatenating in a loop, StringBuilder is much faster than +
        // TODO: Use StringBuilder to build the string "1-2-3-4-5"
        // Hint: append() in a loop, then toString()
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i);
            if (i < 5) {
                sb.append("-");
            }
        }
        String result = sb.toString();
        System.out.println(result);


        // === EXERCISE 6: String comparison pitfall ===
        String s1 = "hello"; // string literal â€” goes to String Pool
        String s2 = "hello"; // same literal â€” reuses from String Pool
        String s3 = new String("hello"); // explicit new object â€” different reference

        // TODO: Print s1 == s2 (same pool reference?)
        // TODO: Print s1 == s3 (different reference?)
        // TODO: Print s1.equals(s3) (same content?)
        // TAKEAWAY: Always use .equals() for String comparison
        System.out.println(s1 == s2); // true
        System.out.println(s1 == s3); // false
        System.out.println(s1.equals(s3)); // true

        System.out.println("=== Exercise 03 Complete ===");
    }
}
