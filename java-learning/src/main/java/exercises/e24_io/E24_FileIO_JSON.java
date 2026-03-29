package exercises.e24_io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

/**
 * Exercise 24: File I/O and JSON Serialization
 *
 * Every Spring Boot app reads config files and serializes/deserializes JSON.
 * Jackson (ObjectMapper) is the default JSON library in Spring Boot.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - @RequestBody deserializes JSON â†’ Java object (Jackson)
 *   - @ResponseBody serializes Java object â†’ JSON (Jackson)
 *   - application.yml/properties â†’ config file reading
 *   - Logging writes to files
 *
 * TypeScript comparison:
 *   JSON.parse(str)  â†’ objectMapper.readValue(str, MyClass.class)
 *   JSON.stringify(obj) â†’ objectMapper.writeValueAsString(obj)
 */
public class E24_FileIO_JSON {

    // === EXERCISE 1: Read and write text files ===
    // Modern Java uses java.nio.file â€” much simpler than old java.io

    // TODO: Write a string to a file:
    //       Files.writeString(Path.of("output.txt"), "Hello, Spring Boot!");
    //
    // TODO: Read it back:
    //       String content = Files.readString(Path.of("output.txt"));
    //       Print the content
    //
    // TODO: Write a list of lines:
    //       Files.write(Path.of("languages.txt"), List.of("Java", "TypeScript", "Go"));
    //
    // TODO: Read all lines:
    //       List<String> lines = Files.readAllLines(Path.of("languages.txt"));


    // === EXERCISE 2: Jackson basics â€” POJO to/from JSON ===
    // POJO = Plain Old Java Object. Spring Boot converts these to/from JSON automatically.

    // TODO: Create a class 'Product' with:
    //       - String name
    //       - double price
    //       - String category
    //       - A no-arg constructor (Jackson REQUIRES this!)
    //       - A constructor with all fields
    //       - Getters and setters (Jackson uses these!)
    //       - toString()

    // TODO: Serialize a Product to JSON string:
    //       ObjectMapper mapper = new ObjectMapper();
    //       mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
    //       String json = mapper.writeValueAsString(product);
    //       Print: {"name":"Laptop","price":999.99,"category":"Electronics"}

    // TODO: Deserialize JSON string back to Product:
    //       Product p = mapper.readValue(json, Product.class);
    //       Print: p.getName()

    // IMPORTANT: Without no-arg constructor or getters â†’ Jackson throws an exception!
    // This is the #1 gotcha for Spring Boot beginners.


    // === EXERCISE 3: Jackson with collections ===
    // TODO: Serialize a List<Product> to JSON:
    //       String json = mapper.writeValueAsString(products);
    //
    // TODO: Deserialize JSON to List<Product>:
    //       List<Product> list = mapper.readValue(json,
    //           mapper.getTypeFactory().constructCollectionType(List.class, Product.class));
    //
    // TODO: Serialize/deserialize a Map<String, Object>:
    //       This is how Spring Boot handles flexible JSON payloads.


    // === EXERCISE 4: Jackson with records ===
    // Records work with Jackson out of the box (Java 16+)!

    // TODO: Create a record: record OrderItem(String product, int quantity, double price) {}
    // TODO: Serialize and deserialize an OrderItem
    //       Records don't need no-arg constructor or getters â€” Jackson handles them!
    //       This is why modern Spring Boot apps prefer records for DTOs.


    // === EXERCISE 5: Jackson annotations ===
    // These annotations control how Spring Boot serializes your API responses.

    // TODO: Create a class 'UserProfile' with:
    //       @JsonProperty("user_name") String name    â€” renames the JSON key
    //       @JsonIgnore String password                â€” excludes from JSON
    //       String email
    //       @JsonProperty("created_at") String createdAt â€” snake_case in JSON
    //
    //       (Import: com.fasterxml.jackson.annotation.JsonProperty, JsonIgnore)
    //
    // TODO: Serialize a UserProfile â€” verify password is NOT in the output
    //       and field names use snake_case


    // === EXERCISE 6: Read JSON from a file ===
    // TODO: Write a JSON array to a file, then read it back as List<Product>
    //       This simulates reading a JSON config or test fixture.
    //
    //       1. Create List<Product> with 3 products
    //       2. Write to "products.json": mapper.writeValue(Path.of("products.json").toFile(), products)
    //       3. Read back: mapper.readValue(Path.of("products.json").toFile(), ...)
    //       4. Print each product


    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // === Test Exercise 1 ===
        // TODO: Write and read text files

        // === Test Exercise 2 ===
        // TODO: Serialize/deserialize a Product

        // === Test Exercise 3 ===
        // TODO: Work with collections

        // === Test Exercise 4 ===
        // TODO: Serialize/deserialize records

        // === Test Exercise 5 ===
        // TODO: Test annotation-controlled serialization

        // === Test Exercise 6 ===
        // TODO: JSON file round-trip

        System.out.println("=== Exercise 24 Complete ===");
    }
}
