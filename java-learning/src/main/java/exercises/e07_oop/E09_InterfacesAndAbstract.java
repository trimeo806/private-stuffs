package exercises.e07_oop;

import exercises.e07_oop.E09_InterfacesAndAbstract.Drawable;
import exercises.e07_oop.E09_InterfacesAndAbstract.Shape;

/**
 * Exercise 09: Interfaces and Abstract Classes
 *
 * Interface = contract (like TypeScript interface, but can have default
 * methods)
 * Abstract class = partial implementation (can't be instantiated)
 *
 * Rule of thumb:
 * - Use interface when you want "can do" behavior (Serializable, Comparable)
 * - Use abstract class when you want "is a" relationship with shared code
 */
public class E09_InterfacesAndAbstract {

    // === EXERCISE 1: Define an interface ===
    // TODO: Create interface 'Drawable' with:
    // - Method: void draw()
    // - Method: double area()
    // - Default method: void describe() that prints
    // "This shape has area: " + area()
    // (default methods have a body â€” like TypeScript's abstract class methods)
    public static interface Drawable {
        void draw();

        double area();

        default void describe() {
            System.out.println("This shape has area: " + area());
        };
    }

    // === EXERCISE 2: Define an abstract class ===
    // TODO: Create abstract class 'Shape' with:
    // - Field: String color
    // - Constructor that takes color
    // - Abstract method: double perimeter()
    // - Concrete method: String getColor() returns color
    abstract static class Shape {
        String color;

        public Shape(String color) {
            this.color = color;
        }

        abstract double perimeter();

        public String getColor() {
            return this.color;
        }
    }

    // === EXERCISE 3: Implement both ===
    // TODO: Create class 'Circle' that:
    // - extends Shape
    // - implements Drawable
    // - Has field: double radius
    // - Implements all required methods

    // === EXERCISE 4: Multiple interfaces ===
    // TODO: Create interface 'Resizable' with:
    // - Method: void resize(double factor)
    // TODO: Make Circle implement both Drawable and Resizable
    public interface Resizable {
        void resize(double factor);
    }

    static class Circle extends Shape implements Drawable, Resizable {
        double radius;

        public Circle(String color, double radius) {
            super(color);
            this.radius = radius;
        }

        public void draw() {
            System.out.println("Circle is drawn");
        }

        public double area() {
            return this.radius * this.radius * 3.14;
        }

        public double perimeter() {
            return 2 * this.radius * 3.14;
        }

        public void resize(double factor) {
            this.radius = this.radius * factor;
        }
    }

    // TODO: Create class 'Square' that:
    // - extends Shape
    // - implements Drawable
    // - Has field: double side
    // - Implements all required methods
    static class Square extends Shape implements Drawable {
        double side;

        public Square(String color, double side) {
            super(color);
            this.side = side;
        }

        public void draw() {
            System.out.println("Square is drawn");
        }

        public double area() {
            return this.side * this.side;
        }

        public double perimeter() {
            return 4 * this.side;
        }
    }

    // === EXERCISE 5: Interface as type ===
    // TODO: Create a method 'printAllAreas(Drawable[] shapes)'
    // that loops through shapes and calls describe() on each
    static void printAllAreas(Drawable[] shapes) {
        // TODO: implement
        for (Drawable drawable : shapes) {
            drawable.describe();
        }
    }

    public static void main(String[] args) {

        // TODO: Create a Circle("red", 5.0)
        // TODO: Create a Square("blue", 4.0)
        // TODO: Call draw() and describe() on each
        // TODO: Test printAllAreas with an array of both shapes
        Circle circle = new Circle("red", 5.0);
        Square square = new Square("blue", 4.0);
        // circle.draw();
        // circle.describe();
        // square.draw();
        // square.describe();
        circle.area();
        circle.describe();
        circle.resize(2.0);
        circle.area();
        circle.describe();
        // Drawable[] shapes = { circle, square, circleWithResizable };

        // printAllAreas(shapes);

        System.out.println("=== Exercise 09 Complete ===");
    }
}
