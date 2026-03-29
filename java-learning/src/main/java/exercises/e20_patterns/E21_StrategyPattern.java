package exercises.e20_patterns;

import java.util.List;

/**
 * Exercise 21: Strategy Pattern
 *
 * Define a family of algorithms, encapsulate each one, and make them interchangeable.
 * In TypeScript, you'd pass callback functions. In Java, you use interfaces + lambdas.
 *
 * This pattern is used heavily in Spring (like different authentication strategies,
 * serialization strategies, etc.)
 */
public class E21_StrategyPattern {

    // === EXERCISE 1: Payment strategy ===
    // TODO: Create a functional interface 'PaymentStrategy' with:
    //       boolean pay(double amount)

    // TODO: Implement three strategies (as classes or lambdas):
    //       - CreditCardPayment: prints "Paid $X with credit card ending in XXXX"
    //       - PayPalPayment: prints "Paid $X via PayPal account: email"
    //       - CryptoPayment: prints "Paid $X in Bitcoin to wallet: address"

    // TODO: Create a 'ShoppingCart' class with:
    //       - List<Item> items (record Item(String name, double price))
    //       - Method: void checkout(PaymentStrategy strategy)
    //         â†’ calculates total, then calls strategy.pay(total)


    // === EXERCISE 2: Sorting strategy ===
    // TODO: Create a 'SortStrategy<T>' functional interface:
    //       List<T> sort(List<T> items)

    // TODO: Implement strategies using lambdas:
    //       SortStrategy<Integer> ascending = list -> list.stream().sorted().toList();
    //       SortStrategy<Integer> descending = list -> list.stream().sorted(Comparator.reverseOrder()).toList();
    //       SortStrategy<Integer> shuffled = list -> { var copy = new ArrayList<>(list); Collections.shuffle(copy); return copy; };

    // TODO: Create a method that takes a list and a strategy, applies it


    // === EXERCISE 3: Validation strategy ===
    // TODO: Create a Validator<T> functional interface: boolean validate(T value)
    //
    //       Create validators for a 'password' string:
    //       - minLength(8): password.length() >= 8
    //       - hasUppercase: password.matches(".*[A-Z].*")
    //       - hasDigit: password.matches(".*\\d.*")
    //       - hasSpecialChar: password.matches(".*[!@#$%^&*].*")
    //
    //       Create a method that takes a password and a List<Validator<String>>
    //       Returns true only if ALL validators pass
    //       Test with: "abc", "Hello123", "Hello123!"


    public static void main(String[] args) {

        // === Test Exercise 1 ===
        // TODO: Create a cart with items, checkout with different payment strategies

        // === Test Exercise 2 ===
        // TODO: Sort a list of numbers with each strategy

        // === Test Exercise 3 ===
        // TODO: Validate passwords with different validator combinations

        System.out.println("=== Exercise 21 Complete ===");
    }
}
