package exercises.e19_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 19: Generics
 *
 * Generics in Java are like TypeScript generics:
 *   TS:   function identity<T>(arg: T): T { return arg; }
 *   Java: <T> T identity(T arg) { return arg; }
 *
 * Key difference: Java uses TYPE ERASURE â€” generic types are removed at runtime.
 * You can't do 'new T()' or 'instanceof T' at runtime.
 */
public class E19_Generics {

    // === EXERCISE 1: Generic method ===
    // TODO: Write a generic method 'printArray' that takes any type of array
    //       and prints each element
    //       static <T> void printArray(T[] array) { ... }


    // === EXERCISE 2: Generic class â€” Pair ===
    // TODO: Create a generic class Pair<A, B> that holds two values of different types
    //       - Fields: A first, B second
    //       - Constructor, getters
    //       - toString: "(first, second)"
    //       Example: Pair<String, Integer> nameAge = new Pair<>("Alice", 30);


    // === EXERCISE 3: Generic class â€” Box with constraint ===
    // TODO: Create a class NumberBox<T extends Number> that:
    //       - Stores a value of type T
    //       - Has method doubleValue() that returns value.doubleValue() * 2
    //       This works because we KNOW T is a Number, so .doubleValue() exists
    //       Try creating NumberBox<String> â€” does it compile?


    // === EXERCISE 4: Wildcards ===
    // TODO: Write a method that prints any list â€” regardless of element type:
    //       static void printList(List<?> list) { ... }
    //       ? means "unknown type"

    // TODO: Write a method that sums any list of numbers:
    //       static double sumList(List<? extends Number> list) { ... }
    //       <? extends Number> means "any type that IS a Number"

    // TODO: Write a method that adds integers to a list:
    //       static void addNumbers(List<? super Integer> list) { ... }
    //       <? super Integer> means "any type that is Integer or its parent"


    // === EXERCISE 5: Generic interface ===
    // TODO: Create a generic interface 'Repository<T, ID>' with:
    //       - T findById(ID id)
    //       - List<T> findAll()
    //       - void save(T entity)
    //       - void deleteById(ID id)
    //       This is similar to Spring Data's CrudRepository!

    // TODO: Create a 'UserRepository implements Repository<User, String>'
    //       where User is a simple record: record User(String id, String name)
    //       Store users in a HashMap internally


    public static void main(String[] args) {

        // === Test Exercise 1 ===
        Integer[] intArr = {1, 2, 3, 4, 5};
        String[] strArr = {"Hello", "World"};
        // TODO: Call printArray with both arrays

        // === Test Exercise 2 ===
        // TODO: Create Pair<String, Integer> and Pair<String, Boolean>
        //       Print both

        // === Test Exercise 3 ===
        // TODO: Create NumberBox<Integer> and NumberBox<Double>
        //       Print doubleValue() for each

        // === Test Exercise 5 ===
        // TODO: Create UserRepository, save some users, find them

        System.out.println("=== Exercise 19 Complete ===");
    }
}
