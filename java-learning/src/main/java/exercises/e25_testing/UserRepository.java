package exercises.e25_testing;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface used by E25 JUnit 5 / Mockito tests.
 */
public interface UserRepository {
    Optional<String> findById(String id);
    List<String> findAll();
    void save(String id, String name);
}
