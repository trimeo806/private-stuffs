package exercises.e26_di;

import java.util.*;

/**
 * Exercise 26: Dependency Injection (DI) and Inversion of Control (IoC)
 *
 * This is THE most important concept in Spring Boot.
 * If you understand DI, Spring will make perfect sense.
 * If you don't, every @Autowired will feel like magic.
 *
 * DI = instead of a class creating its own dependencies, they're GIVEN to it.
 * IoC = the framework controls when objects are created, not your code.
 *
 * TypeScript comparison (NestJS):
 *   @Injectable() class UserService { constructor(private repo: UserRepo) {} }
 *   The framework creates UserRepo and passes it to UserService â€” that's DI!
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   @Service, @Repository, @Component â†’ Spring creates instances (beans)
 *   @Autowired / constructor injection â†’ Spring wires them together
 *   You NEVER call 'new UserService(...)' â€” Spring does it for you
 */
public class E26_DependencyInjection {

    // =====================================================================
    // PART A: The problem DI solves
    // =====================================================================

    // === EXERCISE 1: Tight coupling (the BAD way) ===
    // TODO: Create these classes that are TIGHTLY COUPLED:
    //
    //   class MySQLDatabase {
    //       String query(String sql) { return "MySQL result for: " + sql; }
    //   }
    //
    //   class UserRepository_Bad {
    //       private MySQLDatabase db = new MySQLDatabase(); // â† HARD dependency!
    //       String findUser(String id) { return db.query("SELECT * FROM users WHERE id=" + id); }
    //   }
    //
    // PROBLEM: What if you want to use PostgreSQL? Or a mock for testing?
    //          You'd have to CHANGE UserRepository_Bad code. That violates Open/Closed Principle.


    // === EXERCISE 2: Loose coupling with DI (the GOOD way) ===
    // TODO: Create an interface:
    //   interface Database {
    //       String query(String sql);
    //   }
    //
    // TODO: Create implementations:
    //   class MySQLDatabaseImpl implements Database { ... }
    //   class PostgresDatabaseImpl implements Database { ... }
    //   class MockDatabase implements Database { ... returns "MOCK: " + sql }
    //
    // TODO: Create UserRepository that accepts Database via CONSTRUCTOR:
    //   class UserRepository {
    //       private final Database database;
    //       UserRepository(Database database) { this.database = database; } // â† DI!
    //       String findUser(String id) { return database.query("SELECT ..."); }
    //   }
    //
    // Now you can swap databases without changing UserRepository!
    // In Spring Boot, @Autowired does this constructor injection automatically.


    // =====================================================================
    // PART B: Build a mini IoC container (understand how Spring works)
    // =====================================================================

    // === EXERCISE 3: Simple DI Container ===
    // This is a SIMPLIFIED version of what Spring's ApplicationContext does!

    // TODO: Create a class 'SimpleContainer' that:
    //       - Has a Map<Class<?>, Object> to store "beans"
    //       - Method: <T> void register(Class<T> type, T instance)
    //       - Method: <T> T get(Class<T> type) â€” returns the registered instance
    //         If not found, throw RuntimeException("No bean of type: " + type)
    //
    // TODO: Use the container:
    //       SimpleContainer container = new SimpleContainer();
    //       container.register(Database.class, new PostgresDatabaseImpl());
    //       Database db = container.get(Database.class);
    //       // Same concept: Spring scans for @Component classes and puts them in the container!


    // === EXERCISE 4: Container with auto-wiring ===
    // TODO: Extend SimpleContainer with a method:
    //       <T> T createAndWire(Class<T> type)
    //       That:
    //       1. Gets the first constructor of the class
    //       2. For each parameter type, looks it up in the container
    //       3. Creates a new instance using that constructor
    //
    //       This is a simplified version of @Autowired constructor injection!
    //
    // Example:
    //       container.register(Database.class, new PostgresDatabaseImpl());
    //       UserRepository repo = container.createAndWire(UserRepository.class);
    //       // Container sees UserRepository(Database db) â†’ finds Database in registry â†’ creates instance


    // =====================================================================
    // PART C: Service layer pattern (Spring Boot architecture)
    // =====================================================================

    // === EXERCISE 5: Full layered architecture ===
    // This is exactly how a Spring Boot app is structured!

    // TODO: Create the layers:
    //
    // REPOSITORY layer (data access):
    //   interface UserRepository {
    //       Optional<User> findById(String id);
    //       List<User> findAll();
    //       void save(User user);
    //       void deleteById(String id);
    //   }
    //   record User(String id, String name, String email) {}
    //
    //   class InMemoryUserRepository implements UserRepository {
    //       private final Map<String, User> store = new HashMap<>();
    //       // implement all methods using the map
    //   }
    //
    // SERVICE layer (business logic):
    //   class UserService {
    //       private final UserRepository repository;     // injected!
    //       UserService(UserRepository repository) { ... } // constructor DI
    //
    //       User getUser(String id) {
    //           return repository.findById(id)
    //               .orElseThrow(() -> new RuntimeException("User not found: " + id));
    //       }
    //       List<User> getAllUsers() { ... }
    //       void createUser(String id, String name, String email) { ... }
    //       void deleteUser(String id) { ... }
    //   }
    //
    // CONTROLLER layer (presentation â€” simulated with method calls):
    //   class UserController {
    //       private final UserService service;            // injected!
    //       UserController(UserService service) { ... }   // constructor DI
    //
    //       void handleGetUser(String id) { print(service.getUser(id)); }
    //       void handleListUsers() { service.getAllUsers().forEach(System.out::println); }
    //       void handleCreateUser(String id, String name, String email) { ... }
    //   }
    //
    // WIRING (in Spring Boot, the framework does this; here we do it manually):
    //   UserRepository repo = new InMemoryUserRepository();
    //   UserService service = new UserService(repo);
    //   UserController controller = new UserController(service);
    //   controller.handleCreateUser("1", "Alice", "alice@test.com");
    //   controller.handleGetUser("1");


    public static void main(String[] args) {

        // === Test Exercise 1 ===
        // TODO: Show the tight coupling problem

        // === Test Exercise 2 ===
        // TODO: Show how DI solves it â€” swap databases easily
        //       Create repo with MockDatabase for "testing"
        //       Create repo with PostgresDatabaseImpl for "production"

        // === Test Exercise 3 ===
        // TODO: Use SimpleContainer to register and retrieve beans

        // === Test Exercise 4 ===
        // TODO: Use auto-wiring container

        // === Test Exercise 5 ===
        // TODO: Build the full Controller â†’ Service â†’ Repository pipeline
        //       Create users, list them, get by ID, delete one
        //       This is your first "Spring Boot app" â€” without Spring!

        System.out.println("=== Exercise 26 Complete ===");
    }
}
