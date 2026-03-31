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
    // - Field: String name, int age (protected â€” accessible by subclasses)
    // - Constructor: takes name and age
    // - Method: speak() â€” prints "..."
    // - Method: toString() â€” returns "Animal(name, age)"
    static class Animal {
        String name;
        int age;

        public Animal(String name, int age) {
            this.age = age;
            this.name = name;
        }

        public void speak() {
            System.out.println("...");
        }

        @Override
        public String toString() {
            return String.format("Animal(%s, %d)", this.name, this.age);
        }

    }

    // === EXERCISE 2: Subclasses ===
    // TODO: Create 'Dog extends Animal' with:
    // - Extra field: String breed
    // - Constructor: calls super(name, age) then sets breed
    // - Override speak() to print "Woof! My name is {name}"
    // - Override toString() to include breed
    public static class Dog extends Animal {
        String breed;

        public Dog(String name, int age, String breed) {
            super(name, age);
            this.breed = breed;
        }

        @Override
        public void speak() {
            System.out.printf("Woof! My name is %s", this.name);
        }

        @Override
        public String toString() {
            return "Dog{name='%s', breed='%s'}".formatted(name, breed);
        }
    }

    // TODO: Create 'Cat extends Animal' withW:
    // - Override speak() to print "Meow! I'm {name}"
    public static class Cat extends Animal {
        public Cat(String name, int age) {
            super(name, age);
        }

        @Override
        public void speak() {
            System.out.printf("Meow! I'm %s", this.name);
        }
    }

    public static void main(String[] args) {

        // === EXERCISE 3: Polymorphism ===
        // TODO: In main(), create an Animal[] array containing a Dog and a Cat
        // Loop through and call speak() on each â€” observe dynamic dispatch
        Dog dog = new Dog("Dog", 2, "breed");
        Cat cat = new Cat("Cat", 2);
        Animal[] animals = { dog, cat };
        for (int i = 0; i < animals.length; i++) {
            animals[i].speak();
        }

        // === EXERCISE 4: instanceof and casting ===
        // TODO: For each animal in the array:
        // - If it's a Dog, print its breed
        // - Use: if (animal instanceof Dog dog) { ... } // Java 16+ pattern matching
        for (int i = 0; i < animals.length; i++) {
            Animal animal = animals[i];
            if (animal instanceof Dog d) {
                System.out.println(d.breed);
            }
        }

        // === EXERCISE 5: The @Override annotation ===
        // TODO: Try removing @Override from Dog.speak() and misspelling it as 'speek()'
        // What happens? (Answer: it compiles but doesn't override â€” @Override catches
        // this)

        // TODO: Create a Dog("Rex", 5, "Labrador")
        // TODO: Create a Cat("Whiskers", 3)
        // TODO: Test Exercise 3 â€” polymorphic array
        // TODO: Test Exercise 4 â€” instanceof check

        System.out.println("=== Exercise 08 Complete ===");
    }
}
