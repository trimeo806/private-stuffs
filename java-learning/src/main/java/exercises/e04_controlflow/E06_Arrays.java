package exercises.e04_controlflow;

import java.util.Arrays;

/**
 * Exercise 06: Arrays
 *
 * Java arrays are fixed-size (unlike JS arrays which are dynamic).
 * For dynamic sizing, you'll use ArrayList (covered in Collections).
 * Arrays class provides useful utility methods.
 */
public class E06_Arrays {

    public static void main(String[] args) {

        // === EXERCISE 1: Array declaration and initialization ===
        // TODO: Declare an int array of size 5 (uninitialized)
        // int[] numbers = new int[5];
        // Print it â€” what are the default values?
        int[] numbers = new int[5];
        System.out.println(Arrays.toString(numbers));

        // TODO: Declare and initialize in one line:
        // int[] scores = {90, 85, 78, 92, 88};

        // === EXERCISE 2: Array access and modification ===
        int[] scores = { 90, 85, 78, 92, 88 };
        // TODO: Print the first and last elements
        System.out.println("First element: " + scores[0]);
        System.out.println("Last element: " + scores[scores.length - 1]);
        // TODO: Change the 3rd element to 100
        scores[2] = 100;
        // TODO: Print the array length
        System.out.println("Array length: " + scores.length);
        System.out.println("Updated array: " + Arrays.toString(scores));

        // === EXERCISE 3: Array iteration ===
        // TODO: Calculate the average of all scores
        // Use a for loop to sum, then divide by length
        int sum = 0;
        for (int i = 0; i < scores.length; i++) {
            sum += scores[i];
        }
        double average = (double) sum / scores.length;
        System.out.println("Average score: " + average);

        // === EXERCISE 4: Arrays utility class ===
        int[] data = { 5, 2, 8, 1, 9, 3 };
        // TODO: Sort the array using Arrays.sort()
        // TODO: Print the sorted array using Arrays.toString()
        // TODO: Search for value 8 using Arrays.binarySearch() (array must be sorted
        // first!)
        // TODO: Copy the first 3 elements to a new array using Arrays.copyOf()
        Arrays.sort(data);
        System.out.println("Sorted array: " + Arrays.toString(data));
        int index = Arrays.binarySearch(data, 8);
        System.out.println("Index of 8: " + index);
        int[] firstThree = Arrays.copyOf(data, 3);
        System.out.println("First three elements: " + Arrays.toString(firstThree));

        // === EXERCISE 5: 2D arrays ===
        // TODO: Create a 3x3 2D array representing a tic-tac-toe board
        // Fill it with 'X', 'O', and ' ' (space)
        // Print it as a grid
        // char[][] board = new char[3][3];
        char[][] board = {
                { 'X', 'O', 'X' },
                { ' ', 'X', 'O' },
                { 'O', ' ', 'X' }
        };
        System.out.println("Tic-Tac-Toe Board:");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

        // === EXERCISE 6: Find min and max ===
        int[] values = { 34, 12, 56, 78, 23, 89, 45 };
        // TODO: Find the minimum and maximum values WITHOUT using streams
        // Use a simple loop
        int min = values[0];
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
            if (values[i] > max) {
                max = values[i];
            }
        }
        System.out.println("Minimum value: " + min);
        System.out.println("Maximum value: " + max);
        System.out.println("=== Exercise 06 Complete ===");
    }
}
