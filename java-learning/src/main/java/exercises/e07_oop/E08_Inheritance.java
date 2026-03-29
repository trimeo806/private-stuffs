package exercises.e07_oop;

/**
 * Exercise 08: Inheritance and Polymorphism
 *
 * Java uses 'extends' for class inheritance (single inheritance only).
 * Think of it like TypeScript's 'extends' but more strict â€” Java has
 * no multiple class inheritance (use interfaces instead).
 */
public class E08_Inheritance {

    // === EXERCISE 1: Base class ===
    // TODO: Create a static class 'Animal' with:
    //       - Field: String name, int age (protected â€” accessible by subclasses)
    //       - Constructor: takes name and age
    //       - Method: speak() â€” prints "..."
    //       - Method: toString() â€” returns "Animal(name, age)"


    // === EXERCISE 2: Subclasses ===
    // TODO: Create 'Dog extends Animal' with:
    //       - Extra field: String breed
    //       - Constructor: calls super(name, age) then sets breed
    //       - Override speak() to print "Woof! My name is {name}"
    //       - Override toString() to include breed

    // TODO: Create 'Cat extends Animal' with:
    //       - Override speak() to print "Meow! I'm {name}"


    // === EXERCISE 3: Polymorphism ===
    // TODO: In main(), create an Animal[] array containing a Dog and a Cat
    //       Loop through and call speak() on each â€” observe dynamic dispatch


    // === EXERCISE 4: instanceof and casting ===
    // TODO: For each animal in the array:
    //       - If it's a Dog, print its breed
    //       - Use: if (animal instanceof Dog dog) { ... }  // Java 16+ pattern matching


    // === EXERCISE 5: The @Override annotation ===
    // TODO: Try removing @Override from Dog.speak() and misspelling it as 'speek()'
    //       What happens? (Answer: it compiles but doesn't override â€” @Override catches this)


    public static void main(String[] args) {

        // TODO: Create a Dog("Rex", 5, "Labrador")
        // TODO: Create a Cat("Whiskers", 3)
        // TODO: Test Exercise 3 â€” polymorphic array
        // TODO: Test Exercise 4 â€” instanceof check

        System.out.println("=== Exercise 08 Complete ===");
    }
}
