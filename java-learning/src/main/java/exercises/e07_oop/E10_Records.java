package exercises.e07_oop;

/**
 * Exercise 10: Records, Enums, and Sealed Classes (Modern Java)
 *
 * Records (Java 16+) = immutable data classes â€” like TypeScript's readonly types
 * Enums = fixed set of constants â€” like TypeScript's const enum
 * Sealed classes (Java 17+) = restricted inheritance â€” like discriminated unions
 */
public class E10_Records {

    // === EXERCISE 1: Records ===
    // TODO: Create a record 'Point' with fields: double x, double y
    //       Records auto-generate: constructor, getters, equals, hashCode, toString
    //       Syntax: record Point(double x, double y) {}

    // TODO: Add a method 'distanceTo(Point other)' to the record
    //       Formula: Math.sqrt((x-other.x)^2 + (y-other.y)^2)


    // === EXERCISE 2: Enums ===
    // TODO: Create an enum 'Season' with values: SPRING, SUMMER, AUTUMN, WINTER
    //       Add a field 'avgTemp' (int) and a constructor
    //       Example: SPRING(15), SUMMER(30), AUTUMN(12), WINTER(0)
    //       Add a method: boolean isWarm() â€” returns true if avgTemp > 20


    // === EXERCISE 3: Enum with behavior ===
    // TODO: Create an enum 'Operation' with values: ADD, SUBTRACT, MULTIPLY, DIVIDE
    //       Each has an abstract method: double apply(double a, double b)
    //       Implement it per-constant:
    //       ADD { double apply(double a, double b) { return a + b; } }


    // === EXERCISE 4: Sealed classes (Java 17+) ===
    // TODO: Create a sealed interface 'PaymentMethod' that permits:
    //       CreditCard, DebitCard, Cash
    //       sealed interface PaymentMethod permits CreditCard, DebitCard, Cash {}
    //       record CreditCard(String number, String expiry) implements PaymentMethod {}
    //       record DebitCard(String number) implements PaymentMethod {}
    //       record Cash(double amount) implements PaymentMethod {}


    // === EXERCISE 5: Pattern matching with sealed types ===
    // TODO: Create a method that takes a PaymentMethod and returns a description string
    //       Use switch with pattern matching:
    //       switch (payment) {
    //           case CreditCard cc -> "Credit card ending in " + cc.number().substring(12);
    //           case DebitCard dc -> ...
    //           case Cash c -> ...
    //       }


    public static void main(String[] args) {

        // === Test Records ===
        // TODO: Create two Points, print them, check equality, calculate distance

        // === Test Enums ===
        // TODO: Loop through Season.values() and print name + avgTemp
        // TODO: Use Operation.ADD.apply(10, 5) and print result

        // === Test Sealed ===
        // TODO: Create different payment methods and describe each

        System.out.println("=== Exercise 10 Complete ===");
    }
}
