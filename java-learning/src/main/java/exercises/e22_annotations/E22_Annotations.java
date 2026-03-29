package exercises.e22_annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Exercise 22: Annotations
 *
 * Annotations are metadata markers on classes, methods, and fields.
 * Spring Boot is BUILT on annotations: @RestController, @Service, @Autowired, etc.
 *
 * In TypeScript, the closest equivalent is decorators (@Component, @Injectable).
 * In Java, annotations are first-class citizens supported by the compiler and runtime.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - @Component, @Service, @Repository â†’ bean registration
 *   - @Autowired, @Qualifier â†’ dependency injection
 *   - @GetMapping, @PostMapping â†’ HTTP routing
 *   - @Valid, @NotNull â†’ input validation
 *   - Custom annotations â†’ your own framework behavior
 */
public class E22_Annotations {

    // === EXERCISE 1: Built-in annotations ===
    // Java provides several built-in annotations you'll use daily.

    // TODO: Create an interface 'OldApi' with a method: void connect()
    // TODO: Create a class 'LegacyService' that implements OldApi
    //       Mark connect() with @Override
    // TODO: Create a method oldMethod() in LegacyService, mark it @Deprecated
    //       Add a replacement method newMethod()
    // TODO: In main(), call the deprecated method.
    //       Notice the compiler warning. Use @SuppressWarnings("deprecation") to suppress it.


    // === EXERCISE 2: Create a custom annotation ===
    // Spring uses custom annotations everywhere. Let's learn how they're built.

    // TODO: Create an annotation @JsonField with:
    //       - @Retention(RetentionPolicy.RUNTIME) â€” so we can read it at runtime
    //       - @Target(ElementType.FIELD) â€” can only be placed on fields
    //       - A 'value' attribute (String) for the JSON key name
    //
    // Syntax:
    //   @Retention(RetentionPolicy.RUNTIME)
    //   @Target(ElementType.FIELD)
    //   @interface JsonField {
    //       String value();
    //   }


    // === EXERCISE 3: Read annotations via reflection ===
    // This is exactly how Spring reads @Autowired, @Value, etc.

    // TODO: Create a class 'User' with fields:
    //       @JsonField("user_name") String name
    //       @JsonField("user_email") String email
    //       int age  (no annotation â€” should be skipped)
    //
    // TODO: Write a method: static String toJson(Object obj)
    //       Use reflection to:
    //       1. Get all declared fields: obj.getClass().getDeclaredFields()
    //       2. For each field, check if it has @JsonField: field.isAnnotationPresent(JsonField.class)
    //       3. If yes, get the annotation value: field.getAnnotation(JsonField.class).value()
    //       4. Make field accessible: field.setAccessible(true)
    //       5. Build a JSON-like string: {"user_name": "Alice", "user_email": "alice@test.com"}
    //
    // This is a simplified version of what Jackson/Gson do!


    // === EXERCISE 4: Annotation with defaults ===
    // TODO: Create annotation @Route with:
    //       - String path() â€” required
    //       - String method() default "GET"
    //       - boolean auth() default false
    //
    // TODO: Create a class MockController with methods annotated like:
    //       @Route(path = "/users")
    //       void getUsers() {}
    //
    //       @Route(path = "/users", method = "POST", auth = true)
    //       void createUser() {}
    //
    // TODO: Write a method printRoutes(Class<?> clazz) that:
    //       Scans all methods for @Route annotations and prints:
    //       "GET /users [public]"
    //       "POST /users [auth required]"
    //
    // This is exactly how Spring discovers @GetMapping and @PostMapping!


    // === EXERCISE 5: Marker annotation + validation ===
    // TODO: Create a marker annotation @NotEmpty (no attributes)
    //       @Retention(RUNTIME), @Target(FIELD)
    //
    // TODO: Create a class 'RegistrationForm' with fields:
    //       @NotEmpty String username
    //       @NotEmpty String password
    //       String nickname  (optional)
    //
    // TODO: Write a method: static List<String> validate(Object obj)
    //       Returns a list of field names that are @NotEmpty but have null or "" values
    //       This mimics Spring's @Valid + @NotEmpty validation!
    //
    // Test: validate(new RegistrationForm(null, "", "tri"))
    //       â†’ should return ["username", "password"]


    public static void main(String[] args) {

        // === Test Exercise 1 ===
        // TODO: Create LegacyService, call both old and new methods

        // === Test Exercise 3 ===
        // TODO: Create a User("Alice", "alice@test.com", 30)
        //       Call toJson(user) and print the result
        //       Expected: {"user_name": "Alice", "user_email": "alice@test.com"}

        // === Test Exercise 4 ===
        // TODO: Call printRoutes(MockController.class)

        // === Test Exercise 5 ===
        // TODO: Test the validate method with valid and invalid forms

        System.out.println("=== Exercise 22 Complete ===");
    }
}
