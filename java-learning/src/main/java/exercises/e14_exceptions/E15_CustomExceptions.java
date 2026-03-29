package exercises.e14_exceptions;

/**
 * Exercise 15: Custom Exceptions
 *
 * Create your own exception types for domain-specific errors.
 * This is common in Spring Boot projects for API error handling.
 */
public class E15_CustomExceptions {

    // === EXERCISE 1: Custom unchecked exception ===
    // TODO: Create a 'InsufficientFundsException' that extends RuntimeException
    //       - Field: double amount (how much was short)
    //       - Constructor: takes a message and the amount
    //       - Method: getDeficit() returns the amount


    // === EXERCISE 2: Custom checked exception ===
    // TODO: Create a 'UserNotFoundException' that extends Exception
    //       - Field: String userId
    //       - Constructor: takes userId, builds message "User not found: {userId}"
    //       - Method: getUserId() returns the userId


    // === EXERCISE 3: Use custom exceptions in a service ===
    // TODO: Create a 'BankService' class with:
    //       - A HashMap<String, Double> for account balances
    //       - Method: void deposit(String accountId, double amount)
    //       - Method: void withdraw(String accountId, double amount)
    //         â†’ throws InsufficientFundsException if balance too low
    //       - Method: double getBalance(String accountId)
    //         â†’ throws UserNotFoundException if account doesn't exist


    // === EXERCISE 4: Catching custom exceptions ===
    public static void main(String[] args) {

        // TODO: Create a BankService, add some accounts
        // TODO: Try to withdraw more than the balance â€” catch InsufficientFundsException
        // TODO: Try to get balance of non-existent account â€” catch UserNotFoundException
        // TODO: Use multi-catch: catch (InsufficientFundsException | UserNotFoundException e)


        System.out.println("=== Exercise 15 Complete ===");
    }
}
