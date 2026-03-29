package exercises.e25_testing;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Exercise 25 â€” Part 2: Mockito
 *
 * Mockito lets you create FAKE implementations of interfaces.
 * In Spring Boot testing, you mock repositories when testing services,
 * and mock services when testing controllers.
 *
 * Mockito Cheat Sheet:
 *   @Mock              â†’ creates a mock (fake) object
 *   @InjectMocks       â†’ creates real object, injects @Mock into it
 *   when(...).thenReturn(...)  â†’ define mock behavior
 *   verify(mock).method(...)   â†’ assert that a method was called
 *   never()            â†’ verify method was NOT called
 *   times(n)           â†’ verify method was called exactly n times
 *   any(), anyString() â†’ argument matchers
 */
class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === EXERCISE 6: Mock return values ===
    @Test
    @DisplayName("getUser should return user when found")
    void testGetUser_found() {
        // TODO: Configure mock to return "Alice" when findById("1") is called:
        //       when(repository.findById("1")).thenReturn(Optional.of("Alice"));
        //       Then assert service.getUser("1") equals "Alice"
    }

    // === EXERCISE 7: Test exception paths ===
    @Test
    @DisplayName("getUser should throw when user not found")
    void testGetUser_notFound() {
        // TODO: Configure mock to return empty Optional for findById("999")
        //       Assert that service.getUser("999") throws RuntimeException
    }

    // === EXERCISE 8: Verify interactions ===
    @Test
    @DisplayName("createUser should call repository.save")
    void testCreateUser_callsSave() {
        // TODO: Call service.createUser("1", "Alice")
        //       Verify repository.save("1", "Alice") was called exactly once:
        //       verify(repository).save("1", "Alice");
        //       verify(repository, times(1)).save(anyString(), anyString());
    }

    // === EXERCISE 9: Verify negative case ===
    @Test
    @DisplayName("createUser with blank name should NOT call save")
    void testCreateUser_blankName_throws() {
        // TODO: Assert createUser("1", "") throws IllegalArgumentException
        //       Verify repository.save was NEVER called:
        //       verify(repository, never()).save(anyString(), anyString());
    }

    // === EXERCISE 10: Test collections ===
    @Test
    @DisplayName("getAllUsers should return all users from repository")
    void testGetAllUsers() {
        // TODO: Configure mock: when findAll() â†’ return List.of("Alice", "Bob", "Charlie")
        //       Call service.getAllUsers()
        //       Assert size is 3
        //       Assert list contains "Bob"
    }
}
