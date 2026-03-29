package exercises.e20_patterns;

/**
 * Exercise 20: Builder Pattern
 *
 * The Builder pattern is EVERYWHERE in Java â€” Spring, Lombok, HTTP clients, etc.
 * It solves the "telescoping constructor" problem:
 *   new User("name", null, null, 25, true, null, "city") â† which param is which?
 *
 * With Builder:
 *   User.builder().name("Alice").age(25).city("Hanoi").build() â† crystal clear
 */
public class E20_BuilderPattern {

    // === EXERCISE 1: Implement the Builder pattern ===
    // TODO: Create a 'HttpRequest' class with fields:
    //       - String url (required)
    //       - String method (default: "GET")
    //       - Map<String, String> headers
    //       - String body
    //       - int timeout (default: 30)
    //
    //       Make the constructor PRIVATE.
    //       Create a static inner class 'Builder' with:
    //       - Same fields as HttpRequest
    //       - Method per field that sets value and returns 'this' (fluent API)
    //       - build() method that returns new HttpRequest
    //       - validate in build(): url must not be null/empty
    //
    //       Usage should look like:
    //       HttpRequest request = HttpRequest.builder()
    //           .url("https://api.example.com")
    //           .method("POST")
    //           .header("Content-Type", "application/json")
    //           .body("{\"name\": \"test\"}")
    //           .timeout(10)
    //           .build();


    // === EXERCISE 2: Builder with required vs optional fields ===
    // TODO: Create a 'DatabaseConfig' class with Builder:
    //       Required: host, port, database
    //       Optional: username, password, maxConnections (default: 10), ssl (default: false)
    //
    //       Make required fields part of the builder() factory method:
    //       DatabaseConfig.builder("localhost", 5432, "mydb")
    //           .username("admin")
    //           .ssl(true)
    //           .build();


    public static void main(String[] args) {

        // === Test Exercise 1 ===
        // TODO: Build a GET request with custom headers
        // TODO: Build a POST request with a JSON body
        // TODO: Try building without a URL â€” should throw an exception

        // === Test Exercise 2 ===
        // TODO: Build a database config with required fields only
        // TODO: Build another with all optional fields set

        System.out.println("=== Exercise 20 Complete ===");
    }
}
