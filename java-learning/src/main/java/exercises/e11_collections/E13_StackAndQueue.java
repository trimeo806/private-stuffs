package exercises.e11_collections;

import java.util.*;

/**
 * Exercise 13: Stack, Queue, and Deque
 *
 * Stack = LIFO (Last In, First Out) â€” like a stack of plates
 * Queue = FIFO (First In, First Out) â€” like a line at a store
 * Deque = Double-Ended Queue â€” can act as both stack and queue
 *
 * In modern Java, use ArrayDeque instead of the legacy Stack class.
 */
public class E13_StackAndQueue {

    public static void main(String[] args) {

        // === EXERCISE 1: Stack behavior with ArrayDeque ===
        // TODO: Create an ArrayDeque<String> and use it as a stack
        //       push("A"), push("B"), push("C")
        //       peek() â€” what's on top?
        //       pop() three times â€” in what order do they come out?
        Deque<String> stack = new ArrayDeque<>();


        // === EXERCISE 2: Queue behavior with LinkedList ===
        // TODO: Create a Queue<String> using LinkedList
        //       offer("Customer 1"), offer("Customer 2"), offer("Customer 3")
        //       poll() â€” who is served first?
        Queue<String> queue = new LinkedList<>();


        // === EXERCISE 3: Bracket validator (Stack application) ===
        // TODO: Write a method that checks if brackets are balanced
        //       Input: "({[]})" â†’ true
        //       Input: "({[}])" â†’ false
        //       Input: "((("     â†’ false
        //       Algorithm:
        //         For each char:
        //           If opening bracket â†’ push
        //           If closing bracket â†’ pop and check if it matches
        //         At the end, stack should be empty

        System.out.println("isValid(\"({[]})\"): " + isBalanced("({[]})"));   // true
        System.out.println("isValid(\"({[}])\"): " + isBalanced("({[}])"));   // false
        System.out.println("isValid(\"(((\"): " + isBalanced("((("));         // false


        // === EXERCISE 4: PriorityQueue ===
        // TODO: Create a PriorityQueue<Integer> and add: 5, 1, 3, 9, 2
        //       Poll all elements â€” in what order do they come out?
        //       (PriorityQueue = min-heap by default)

        // TODO: Create a max-heap PriorityQueue:
        //       new PriorityQueue<>(Collections.reverseOrder())


        // === EXERCISE 5: Reverse a string using a Stack ===
        // TODO: Push each character of "Hello" onto a stack
        //       Pop all characters to build the reversed string


        System.out.println("=== Exercise 13 Complete ===");
    }

    // TODO: Implement this method for Exercise 3
    static boolean isBalanced(String input) {
        // Your implementation here
        return false; // placeholder
    }
}
