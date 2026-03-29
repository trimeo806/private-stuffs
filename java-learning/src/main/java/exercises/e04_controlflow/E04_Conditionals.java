package exercises.e04_controlflow;

/**
 * Exercise 04: Conditionals (if/else, switch)
 *
 * Similar to TypeScript but switch has some Java-specific features.
 * Java 21+ has pattern matching in switch â€” very powerful.
 */
public class E04_Conditionals {

    public static void main(String[] args) {

        // === EXERCISE 1: Grade calculator ===
        // TODO: Given a score (0-100), print the grade:
        // 90-100 = "A", 80-89 = "B", 70-79 = "C", 60-69 = "D", below 60 = "F"
        int score = 85;
        String grade;
        if (score >= 90) {
            grade = "A";
        } else if (score >= 80) {
            grade = "B";
        } else if (score >= 70) {
            grade = "C";
        } else if (score >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }
        System.out.println("Grade: " + grade);

        // === EXERCISE 2: Switch statement ===
        // TODO: Given a day string, print "Weekday" or "Weekend"
        // Use a switch statement (not if/else)
        String day = "Saturday";
        switch (day) {
            case "Saturday", "Sunday":
                System.out.println("Weekend");
                break;
            case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday":
                System.out.println("Weekday");
                break;
            default:
                System.out.println("Invalid day");
                break;
        }

        // === EXERCISE 3: Switch expression (Java 14+) ===
        // TODO: Rewrite Exercise 2 using switch EXPRESSION with arrow syntax:
        // String type = switch (day) {
        // case "Monday", "Tuesday" -> "Weekday";
        // ...
        // };
        String type = switch (day) {
            case "Saturday", "Sunday" -> "Weekend";
            case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
            default -> "Invalid day";
        };
        System.out.println("Type: " + type);

        // === EXERCISE 4: FizzBuzz ===
        // TODO: For numbers 1 to 30:
        // - Print "FizzBuzz" if divisible by both 3 and 5
        // - Print "Fizz" if divisible by 3 only
        // - Print "Buzz" if divisible by 5 only
        // - Otherwise print the number
        for (int i = 1; i <= 30; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                System.out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");
            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);
            }
        }

        // === EXERCISE 5: Nested conditions ===
        // TODO: Given age and hasLicense, determine if a person can drive:
        // - age >= 18 AND hasLicense = true â†’ "Can drive"
        // - age >= 18 AND hasLicense = false â†’ "Need a license"
        // - age < 18 â†’ "Too young"
        int age = 20;
        boolean hasLicense = false;
        if (age >= 18) {
            if (hasLicense) {
                System.out.println("Can drive");
            } else {
                System.out.println("Need a license");
            }
        } else {
            System.out.println("Too young");
        }
        System.out.println("=== Exercise 04 Complete ===");
    }
}
