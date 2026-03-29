package exercises.e25_testing;

// ============================================================================
// IMPORTANT: This file goes in src/TEST/java/exercises/testing/
// But we put the exercise INSTRUCTIONS here in src/main so you can read them.
// You'll need to create the actual test file in the test directory.
// ============================================================================

/**
 * Exercise 25: JUnit 5 and Mockito
 *
 * Testing is non-negotiable in Spring Boot development.
 * JUnit 5 = the test framework (like Jest/Vitest in TypeScript)
 * Mockito = mocking library (like jest.mock() / vi.mock())
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - @SpringBootTest â†’ integration test with full context
 *   - @WebMvcTest â†’ test only controllers (mock services)
 *   - @DataJpaTest â†’ test only repositories (in-memory DB)
 *   - All of these use JUnit 5 + Mockito under the hood
 *
 * TypeScript comparison:
 *   describe('UserService', () => {        â†’  @Test void testGetUser() {
 *     it('should return user', () => {     â†’      assertEquals(expected, actual);
 *       expect(result).toBe('Alice');      â†’  }
 *     });
 *   });
 */
public class E25_JUnit5Testing_README {

    // =====================================================================
    // INSTRUCTIONS: Create these test files in:
    //   src/test/java/exercises/testing/
    // =====================================================================

    // === STEP 1: Create the classes to test ===
    // In src/main/java/exercises/testing/, create:

    // --- Calculator.java ---
    // public class Calculator {
    //     int add(int a, int b) { return a + b; }
    //     int subtract(int a, int b) { return a - b; }
    //     int multiply(int a, int b) { return a * b; }
    //     double divide(int a, int b) {
    //         if (b == 0) throw new ArithmeticException("Cannot divide by zero");
    //         return (double) a / b;
    //     }
    // }

    // --- UserRepository.java (interface) ---
    // public interface UserRepository {
    //     Optional<String> findById(String id);
    //     List<String> findAll();
    //     void save(String id, String name);
    // }

    // --- UserService.java ---
    // public class UserService {
    //     private final UserRepository repository;
    //     public UserService(UserRepository repository) { this.repository = repository; }
    //     public String getUser(String id) {
    //         return repository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("User not found: " + id));
    //     }
    //     public List<String> getAllUsers() { return repository.findAll(); }
    //     public void createUser(String id, String name) {
    //         if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
    //         repository.save(id, name);
    //     }
    // }


    // === STEP 2: Create CalculatorTest.java in src/test/java/exercises/testing/ ===
    //
    // import org.junit.jupiter.api.*;
    // import static org.junit.jupiter.api.Assertions.*;
    //
    // class CalculatorTest {
    //     Calculator calc;
    //
    //     @BeforeEach                          // Runs before each test (like beforeEach() in Jest)
    //     void setUp() { calc = new Calculator(); }
    //
    //     // EXERCISE 1: Basic assertions
    //     @Test
    //     void testAdd() { assertEquals(5, calc.add(2, 3)); }
    //
    //     @Test
    //     void testSubtract() { /* TODO */ }
    //
    //     @Test
    //     void testMultiply() { /* TODO */ }
    //
    //     // EXERCISE 2: Testing exceptions
    //     @Test
    //     void testDivideByZero() {
    //         assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    //     }
    //
    //     // EXERCISE 3: Grouped assertions
    //     @Test
    //     void testMultipleAssertions() {
    //         assertAll("calculator operations",
    //             () -> assertEquals(4, calc.add(2, 2)),
    //             () -> assertEquals(0, calc.subtract(2, 2)),
    //             () -> assertEquals(4, calc.multiply(2, 2))
    //         );
    //     }
    //
    //     // EXERCISE 4: Parameterized tests
    //     @ParameterizedTest
    //     @CsvSource({"1,1,2", "2,3,5", "10,20,30", "-1,1,0"})
    //     void testAddParameterized(int a, int b, int expected) {
    //         assertEquals(expected, calc.add(a, b));
    //     }
    //
    //     // EXERCISE 5: Display names
    //     @Test
    //     @DisplayName("Division should return correct decimal result")
    //     void testDivide() { assertEquals(2.5, calc.divide(5, 2), 0.001); }
    // }


    // === STEP 3: Create UserServiceTest.java in src/test/java/exercises/testing/ ===
    //
    // import org.junit.jupiter.api.*;
    // import org.mockito.*;
    // import static org.mockito.Mockito.*;
    // import static org.junit.jupiter.api.Assertions.*;
    //
    // class UserServiceTest {
    //     @Mock UserRepository repository;     // Creates a mock (fake) repository
    //     @InjectMocks UserService service;     // Injects the mock into the service
    //
    //     @BeforeEach
    //     void setUp() { MockitoAnnotations.openMocks(this); }
    //
    //     // EXERCISE 6: Mock return values
    //     @Test
    //     void testGetUser_found() {
    //         when(repository.findById("1")).thenReturn(Optional.of("Alice"));
    //         assertEquals("Alice", service.getUser("1"));
    //     }
    //
    //     // EXERCISE 7: Test exception case
    //     @Test
    //     void testGetUser_notFound() {
    //         when(repository.findById("999")).thenReturn(Optional.empty());
    //         assertThrows(RuntimeException.class, () -> service.getUser("999"));
    //     }
    //
    //     // EXERCISE 8: Verify interactions
    //     @Test
    //     void testCreateUser_callsSave() {
    //         service.createUser("1", "Alice");
    //         verify(repository).save("1", "Alice");     // verify mock was called
    //         verify(repository, times(1)).save(anyString(), anyString());
    //     }
    //
    //     // EXERCISE 9: Test validation
    //     @Test
    //     void testCreateUser_blankName_throws() {
    //         assertThrows(IllegalArgumentException.class,
    //             () -> service.createUser("1", ""));
    //         verify(repository, never()).save(anyString(), anyString()); // save NOT called
    //     }
    //
    //     // EXERCISE 10: Test getAllUsers
    //     @Test
    //     void testGetAllUsers() {
    //         when(repository.findAll()).thenReturn(List.of("Alice", "Bob"));
    //         List<String> users = service.getAllUsers();
    //         assertEquals(2, users.size());
    //         assertTrue(users.contains("Alice"));
    //     }
    // }

    public static void main(String[] args) {
        System.out.println("=== Exercise 25: READ THE INSTRUCTIONS ABOVE ===");
        System.out.println("Create the actual test files in src/test/java/exercises/testing/");
        System.out.println("Run with: mvn test");
        System.out.println("Or right-click the test file in your IDE â†’ Run Tests");
    }
}
