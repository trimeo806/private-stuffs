package exercises.e27_http;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * Exercise 27: Java HttpClient and REST Concepts
 *
 * Java 11+ includes a built-in HTTP client â€” no external libraries needed.
 * This is important for understanding how Spring's RestTemplate/WebClient work.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - RestTemplate / WebClient â†’ calling external APIs
 *   - Understanding HTTP methods, headers, status codes
 *   - JSON request/response bodies
 *   - Building your own REST APIs with @RestController
 *
 * TypeScript comparison:
 *   fetch('https://api.example.com/users')    â†’ HttpClient.newHttpClient().send(request, ...)
 *     .then(res => res.json())
 */
public class E27_HttpClient_REST {

    // === EXERCISE 1: Simple GET request ===
    // TODO: Create an HttpClient and send a GET request:
    //
    //   HttpClient client = HttpClient.newHttpClient();
    //   HttpRequest request = HttpRequest.newBuilder()
    //       .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
    //       .header("Accept", "application/json")
    //       .timeout(Duration.ofSeconds(10))
    //       .GET()
    //       .build();
    //
    //   HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    //   System.out.println("Status: " + response.statusCode());
    //   System.out.println("Body: " + response.body());
    //
    // Notice the Builder pattern â€” same as E20_BuilderPattern!


    // === EXERCISE 2: GET with query parameters ===
    // TODO: Fetch multiple posts with a userId filter:
    //       URI: "https://jsonplaceholder.typicode.com/posts?userId=1"
    //       Print the response body (it's a JSON array)


    // === EXERCISE 3: POST request with JSON body ===
    // TODO: Send a POST request to create a new resource:
    //
    //   String jsonBody = """
    //       {
    //           "title": "My Post",
    //           "body": "Hello from Java HttpClient",
    //           "userId": 1
    //       }
    //       """;
    //
    //   HttpRequest postRequest = HttpRequest.newBuilder()
    //       .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
    //       .header("Content-Type", "application/json")
    //       .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    //       .build();
    //
    //   Print the status code (should be 201 Created)
    //   Print the response body (returns the created resource with an id)


    // === EXERCISE 4: PUT and DELETE requests ===
    // TODO: Send a PUT request to update post #1:
    //       Method: .PUT(HttpRequest.BodyPublishers.ofString(updatedJson))
    //       URI: "https://jsonplaceholder.typicode.com/posts/1"
    //       Print status code (should be 200)
    //
    // TODO: Send a DELETE request:
    //       Method: .DELETE()
    //       URI: "https://jsonplaceholder.typicode.com/posts/1"
    //       Print status code (should be 200)


    // === EXERCISE 5: Async requests ===
    // TODO: Send a request asynchronously (returns CompletableFuture):
    //
    //   client.sendAsync(request, BodyHandlers.ofString())
    //       .thenApply(HttpResponse::body)
    //       .thenAccept(body -> System.out.println("Async response: " + body))
    //       .join();  // wait for completion
    //
    // This is how Spring WebClient works â€” non-blocking HTTP calls!


    // === EXERCISE 6: Response handling pattern ===
    // TODO: Create a method that handles different HTTP status codes:
    //
    //   static void handleResponse(HttpResponse<String> response) {
    //       switch (response.statusCode()) {
    //           case 200 -> System.out.println("OK: " + response.body());
    //           case 201 -> System.out.println("Created: " + response.body());
    //           case 400 -> System.out.println("Bad Request");
    //           case 401 -> System.out.println("Unauthorized â€” need to login");
    //           case 403 -> System.out.println("Forbidden â€” no permission");
    //           case 404 -> System.out.println("Not Found");
    //           case 500 -> System.out.println("Server Error");
    //           default  -> System.out.println("Status: " + response.statusCode());
    //       }
    //   }
    //
    // In Spring Boot, you'll define these SAME status codes in your @RestController:
    //   @ResponseStatus(HttpStatus.NOT_FOUND)
    //   @GetMapping("/users/{id}")


    // === EXERCISE 7: Parse JSON response with Jackson ===
    // TODO: Combine HttpClient with Jackson (from E24):
    //
    //   1. Fetch "https://jsonplaceholder.typicode.com/users/1"
    //   2. Create a record: record User(int id, String name, String email, String phone) {}
    //   3. Parse the response body:
    //      ObjectMapper mapper = new ObjectMapper();
    //      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //      User user = mapper.readValue(response.body(), User.class);
    //   4. Print: "User: Alice (alice@test.com)"
    //
    // The FAIL_ON_UNKNOWN_PROPERTIES=false is important â€”
    // the API returns more fields than our record has.
    // In Spring Boot, this is configured globally in application.yml.


    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // === Test Exercise 1 ===
        // TODO: GET a single post

        // === Test Exercise 2 ===
        // TODO: GET posts filtered by userId

        // === Test Exercise 3 ===
        // TODO: POST a new resource

        // === Test Exercise 4 ===
        // TODO: PUT update, then DELETE

        // === Test Exercise 5 ===
        // TODO: Async request

        // === Test Exercise 6 ===
        // TODO: Test handleResponse with different status codes

        // === Test Exercise 7 ===
        // TODO: Parse JSON response into a Java object

        System.out.println("=== Exercise 27 Complete ===");
    }
}
