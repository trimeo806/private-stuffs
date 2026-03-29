package exercises.e25_testing;

import java.util.List;
import java.util.Optional;

/**
 * Classes used by E25 JUnit 5 / Mockito tests.
 * These are the "production" code that you'll write tests for.
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return (double) a / b;
    }
}
