package exercises.e25_testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 25 â€” Part 1: JUnit 5 Basics
 *
 * Run with: mvn test -pl java-learning
 * Or right-click in IDE â†’ Run Tests
 *
 * JUnit Annotations Cheat Sheet:
 *   @Test             â†’ marks a test method (like it() in Jest)
 *   @BeforeEach       â†’ runs before every test (like beforeEach())
 *   @AfterEach        â†’ runs after every test
 *   @BeforeAll        â†’ runs once before all tests (static method)
 *   @DisplayName      â†’ custom readable name for the test
 *   @ParameterizedTest â†’ run same test with different inputs
 *   @Disabled         â†’ skip this test
 */
class CalculatorTest {

    Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    // === EXERCISE 1: Basic assertions ===
    @Test
    @DisplayName("add(2, 3) should return 5")
    void testAdd() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void testSubtract() {
        // TODO: Assert that subtract(10, 4) returns 6
    }

    @Test
    void testMultiply() {
        // TODO: Assert that multiply(3, 7) returns 21
    }

    // === EXERCISE 2: Testing exceptions ===
    @Test
    @DisplayName("divide by zero should throw ArithmeticException")
    void testDivideByZero() {
        // TODO: Use assertThrows to verify ArithmeticException is thrown
        //       assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    }

    @Test
    @DisplayName("divide(5, 2) should return 2.5")
    void testDivide() {
        // TODO: Assert divide returns 2.5
        //       Use assertEquals(2.5, calc.divide(5, 2), 0.001) for double comparison
    }

    // === EXERCISE 3: Grouped assertions ===
    @Test
    @DisplayName("All basic operations should work correctly")
    void testMultipleAssertions() {
        // TODO: Use assertAll to check multiple operations at once:
        //       assertAll("calculator",
        //           () -> assertEquals(4, calc.add(2, 2)),
        //           () -> assertEquals(0, calc.subtract(2, 2)),
        //           () -> assertEquals(4, calc.multiply(2, 2)),
        //           () -> assertEquals(1.0, calc.divide(2, 2))
        //       );
    }

    // === EXERCISE 4: Parameterized tests ===
    // Run the SAME test with DIFFERENT data â€” very useful for validation testing!
    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "2, 3, 5",
        "10, 20, 30",
        "-1, 1, 0",
        "0, 0, 0"
    })
    @DisplayName("add should handle various inputs")
    void testAddParameterized(int a, int b, int expected) {
        // TODO: Assert that add(a, b) equals expected
    }

    // === EXERCISE 5: Boolean assertions ===
    @Test
    void testAssertTrueAndFalse() {
        // TODO: Use assertTrue / assertFalse / assertNotNull / assertNull
        //       assertTrue(calc.add(1, 1) > 0);
        //       assertFalse(calc.add(1, 1) < 0);
        //       assertNotNull(calc);
    }
}
