package exercises.e07_oop;

/**
 * Exercise 09: Interfaces and Abstract Classes
 *
 * Interface = contract (like TypeScript interface, but can have default methods)
 * Abstract class = partial implementation (can't be instantiated)
 *
 * Rule of thumb:
 *   - Use interface when you want "can do" behavior (Serializable, Comparable)
 *   - Use abstract class when you want "is a" relationship with shared code
 */
public class E09_InterfacesAndAbstract {

    // === EXERCISE 1: Define an interface ===
    // TODO: Create interface 'Drawable' with:
    //       - Method: void draw()
    //       - Method: double area()
    //       - Default method: void describe() that prints
    //         "This shape has area: " + area()
    //         (default methods have a body â€” like TypeScript's abstract class methods)


    // === EXERCISE 2: Define an abstract class ===
    // TODO: Create abstract class 'Shape' with:
    //       - Field: String color
    //       - Constructor that takes color
    //       - Abstract method: double perimeter()
    //       - Concrete method: String getColor() returns color


    // === EXERCISE 3: Implement both ===
    // TODO: Create class 'Circle' that:
    //       - extends Shape
    //       - implements Drawable
    //       - Has field: double radius
    //       - Implements all required methods


    // TODO: Create class 'Square' that:
    //       - extends Shape
    //       - implements Drawable
    //       - Has field: double side
    //       - Implements all required methods


    // === EXERCISE 4: Multiple interfaces ===
    // TODO: Create interface 'Resizable' with:
    //       - Method: void resize(double factor)
    // TODO: Make Circle implement both Drawable and Resizable


    // === EXERCISE 5: Interface as type ===
    // TODO: Create a method 'printAllAreas(Drawable[] shapes)'
    //       that loops through shapes and calls describe() on each
    static void printAllAreas(Drawable[] shapes) {
        // TODO: implement
    }


    public static void main(String[] args) {

        // TODO: Create a Circle("red", 5.0)
        // TODO: Create a Square("blue", 4.0)
        // TODO: Call draw() and describe() on each
        // TODO: Test printAllAreas with an array of both shapes

        System.out.println("=== Exercise 09 Complete ===");
    }
}
