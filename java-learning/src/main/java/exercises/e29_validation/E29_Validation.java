package exercises.e29_validation;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Exercise 29: Input Validation Patterns
 *
 * In Spring Boot, you validate incoming data with @Valid + Jakarta Bean Validation.
 * Before relying on the framework, you need to understand the PATTERNS.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - @Valid on @RequestBody â†’ triggers bean validation
 *   - @NotNull, @NotBlank, @Size, @Email, @Min, @Max â†’ standard constraints
 *   - @Pattern(regexp) â†’ regex validation
 *   - Custom validators with @Constraint
 *   - BindingResult â†’ collecting validation errors
 *   - @ControllerAdvice â†’ global validation error handling
 *
 * TypeScript comparison (Zod):
 *   z.object({ email: z.string().email(), age: z.number().min(0).max(150) })
 *   â†’ Java: @Email String email; @Min(0) @Max(150) int age;
 */
public class E29_Validation {

    // === EXERCISE 1: Manual validation with predicates ===
    // Before Spring's @Valid, understand the mechanics.

    // TODO: Create a functional interface:
    //   @FunctionalInterface
    //   interface ValidationRule<T> {
    //       Optional<String> validate(T value);  // returns error message or empty
    //   }

    // TODO: Create common validation rules as static methods:
    //   static ValidationRule<String> notBlank(String fieldName) {
    //       return value -> (value == null || value.isBlank())
    //           ? Optional.of(fieldName + " must not be blank")
    //           : Optional.empty();
    //   }
    //
    //   static ValidationRule<String> minLength(String fieldName, int min) { ... }
    //   static ValidationRule<String> maxLength(String fieldName, int max) { ... }
    //   static ValidationRule<String> matchesEmail(String fieldName) {
    //       return value -> {
    //           Pattern emailPattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    //           return (value != null && emailPattern.matcher(value).matches())
    //               ? Optional.empty()
    //               : Optional.of(fieldName + " must be a valid email");
    //       };
    //   }
    //   static ValidationRule<Integer> between(String fieldName, int min, int max) { ... }


    // === EXERCISE 2: Composable validator ===
    // TODO: Create a class Validator<T> that chains rules:
    //
    //   class Validator<T> {
    //       private final List<ValidationRule<T>> rules = new ArrayList<>();
    //
    //       Validator<T> addRule(ValidationRule<T> rule) {
    //           rules.add(rule);
    //           return this;  // fluent API â€” method chaining
    //       }
    //
    //       List<String> validate(T value) {
    //           return rules.stream()
    //               .map(rule -> rule.validate(value))
    //               .filter(Optional::isPresent)
    //               .map(Optional::get)
    //               .toList();
    //       }
    //   }
    //
    // Usage:
    //   Validator<String> emailValidator = new Validator<String>()
    //       .addRule(notBlank("email"))
    //       .addRule(minLength("email", 5))
    //       .addRule(matchesEmail("email"));
    //
    //   List<String> errors = emailValidator.validate("");
    //   // â†’ ["email must not be blank", "email must be at least 5 characters", ...]


    // === EXERCISE 3: Object-level validation ===
    // This mimics how Spring's @Valid validates a whole DTO at once.

    // TODO: Create a record:
    //   record RegistrationRequest(String username, String email, String password, int age) {}

    // TODO: Create a class RegistrationValidator:
    //
    //   static List<String> validate(RegistrationRequest req) {
    //       List<String> errors = new ArrayList<>();
    //
    //       // Username: not blank, 3-20 chars, alphanumeric only
    //       errors.addAll(new Validator<String>()
    //           .addRule(notBlank("username"))
    //           .addRule(minLength("username", 3))
    //           .addRule(maxLength("username", 20))
    //           .validate(req.username()));
    //
    //       // Email: not blank, valid format
    //       // Password: not blank, min 8 chars
    //       // Age: between 13 and 150
    //
    //       return errors;
    //   }
    //
    // Test cases:
    //   validate(new RegistrationRequest("", "bad", "123", -1))
    //   â†’ ["username must not be blank", "email must be a valid email", "password min 8", "age between 13-150"]
    //
    //   validate(new RegistrationRequest("alice", "alice@test.com", "Str0ng!Pass", 25))
    //   â†’ [] (no errors)


    // === EXERCISE 4: Validation result pattern ===
    // Instead of returning a List<String>, return a typed result.

    // TODO: Create a sealed interface:
    //   sealed interface ValidationResult {
    //       record Valid<T>(T value) implements ValidationResult {}
    //       record Invalid(List<String> errors) implements ValidationResult {}
    //   }
    //
    // TODO: Create a method:
    //   static ValidationResult validateRegistration(RegistrationRequest req) {
    //       List<String> errors = validate(req);
    //       return errors.isEmpty()
    //           ? new ValidationResult.Valid<>(req)
    //           : new ValidationResult.Invalid(errors);
    //   }
    //
    // TODO: Use pattern matching to handle it:
    //   switch (result) {
    //       case ValidationResult.Valid<?> v -> System.out.println("Welcome, " + req.username());
    //       case ValidationResult.Invalid inv -> inv.errors().forEach(System.out::println);
    //   }
    //
    // In Spring Boot, @Valid + BindingResult works similarly â€”
    // if validation fails, errors are collected; otherwise, processing continues.


    // === EXERCISE 5: Password strength validator ===
    // TODO: Create validation rules for passwords (reuse your Validator class):
    //   - minLength(8)
    //   - hasUppercase: must contain at least one uppercase letter
    //   - hasLowercase: must contain at least one lowercase letter
    //   - hasDigit: must contain at least one digit
    //   - hasSpecialChar: must contain one of !@#$%^&*
    //
    // TODO: Create a method that returns the password "strength":
    //   static String passwordStrength(String password) {
    //       int score = 0;
    //       if (password.length() >= 8) score++;
    //       if (password.matches(".*[A-Z].*")) score++;
    //       if (password.matches(".*[a-z].*")) score++;
    //       if (password.matches(".*\\d.*")) score++;
    //       if (password.matches(".*[!@#$%^&*].*")) score++;
    //       return switch (score) {
    //           case 0, 1 -> "WEAK";
    //           case 2, 3 -> "MEDIUM";
    //           case 4    -> "STRONG";
    //           case 5    -> "VERY STRONG";
    //           default   -> "UNKNOWN";
    //       };
    //   }
    //
    // Test: "abc" â†’ WEAK, "Hello123" â†’ STRONG, "Hello123!" â†’ VERY STRONG


    // === EXERCISE 6: Sanitization ===
    // Spring Boot auto-handles some sanitization, but you should understand it.

    // TODO: Create static methods:
    //   static String sanitizeHtml(String input) {
    //       // Prevent XSS â€” replace < > & " ' with HTML entities
    //       return input
    //           .replace("&", "&amp;")
    //           .replace("<", "&lt;")
    //           .replace(">", "&gt;")
    //           .replace("\"", "&quot;")
    //           .replace("'", "&#x27;");
    //   }
    //
    //   static String sanitizeSql(String input) {
    //       // In real apps, ALWAYS use PreparedStatement instead!
    //       // This is just to understand what SQL injection looks like.
    //       return input.replace("'", "''").replace(";", "");
    //   }
    //
    //   static String trimAndNormalize(String input) {
    //       return (input == null) ? "" : input.trim().toLowerCase();
    //   }
    //
    // Test: sanitizeHtml("<script>alert('xss')</script>")
    //     â†’ "&lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;"


    public static void main(String[] args) {

        // === Test Exercise 1 & 2 ===
        // TODO: Create validators and test with valid/invalid inputs
        System.out.println("--- String validation ---");

        // === Test Exercise 3 ===
        // TODO: Validate registration requests
        System.out.println("\n--- Registration validation ---");

        // === Test Exercise 4 ===
        // TODO: Use ValidationResult pattern matching
        System.out.println("\n--- Typed validation result ---");

        // === Test Exercise 5 ===
        // TODO: Test password strength
        System.out.println("\n--- Password strength ---");

        // === Test Exercise 6 ===
        // TODO: Test sanitization
        System.out.println("\n--- Sanitization ---");

        System.out.println("\n=== Exercise 29 Complete ===");
    }
}
