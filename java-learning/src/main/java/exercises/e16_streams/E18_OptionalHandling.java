package exercises.e16_streams;

import java.util.*;

/**
 * Exercise 18: Optional
 *
 * Optional<T> = a container that may or may not hold a value.
 * It's Java's way to avoid NullPointerException â€” similar to
 * TypeScript's strict null checks (string | undefined).
 *
 * Rule: NEVER call .get() without checking .isPresent() first.
 * Better: use .orElse(), .orElseThrow(), .map(), .ifPresent()
 */
public class E18_OptionalHandling {

    record User(String name, String email, String phone) {}

    // Simulates a database lookup
    static Optional<User> findUserByEmail(String email) {
        Map<String, User> db = Map.of(
            "alice@test.com", new User("Alice", "alice@test.com", "123-456"),
            "bob@test.com", new User("Bob", "bob@test.com", null)
        );
        return Optional.ofNullable(db.get(email));
    }

    public static void main(String[] args) {

        // === EXERCISE 1: Creating Optionals ===
        // TODO: Create an Optional with a value: Optional.of("Hello")
        // TODO: Create an empty Optional: Optional.empty()
        // TODO: Create from nullable: Optional.ofNullable(someValue)
        //       What happens with Optional.of(null)? Try it!


        // === EXERCISE 2: Extracting values safely ===
        Optional<User> user = findUserByEmail("alice@test.com");

        // TODO: Use ifPresent to print the user's name if found
        //       user.ifPresent(u -> System.out.println(u.name()));

        // TODO: Use orElse to get a default value
        //       String name = user.map(User::name).orElse("Unknown");

        // TODO: Use orElseThrow for required values
        //       User u = user.orElseThrow(() -> new RuntimeException("User not found"));


        // === EXERCISE 3: Chaining with map ===
        // TODO: Get Alice's phone number, or "N/A" if user not found OR phone is null
        //       findUserByEmail("alice@test.com")
        //           .map(User::phone)
        //           .orElse("N/A")

        // TODO: Try the same with "unknown@test.com" â€” what do you get?
        // TODO: Try with "bob@test.com" â€” Bob has null phone. Does it work?
        //       (Hint: map() handles null by returning Optional.empty())


        // === EXERCISE 4: flatMap for nested Optionals ===
        // TODO: If findUserByEmail returns Optional<User> and
        //       getPhoneOptional(User) returns Optional<String>,
        //       use flatMap to chain them without nested Optionals

        // findUserByEmail("alice@test.com")
        //     .flatMap(u -> Optional.ofNullable(u.phone()))
        //     .orElse("No phone");


        // === EXERCISE 5: Optional with streams ===
        List<String> emails = List.of(
            "alice@test.com", "unknown@test.com", "bob@test.com", "nobody@test.com"
        );

        // TODO: For each email, look up the user and collect found users only
        //       emails.stream()
        //           .map(E18_OptionalHandling::findUserByEmail)
        //           .filter(Optional::isPresent)
        //           .map(Optional::get)
        //           .toList()

        // TODO: Better way (Java 9+):
        //       .flatMap(Optional::stream)


        // === EXERCISE 6: When NOT to use Optional ===
        // Optional should be used for RETURN TYPES, not:
        //   - Method parameters (just accept nullable or overload)
        //   - Fields (just use null with proper checks)
        //   - Collections (return empty collection instead)
        // TODO: Explain in a comment why each anti-pattern is bad


        System.out.println("=== Exercise 18 Complete ===");
    }
}
