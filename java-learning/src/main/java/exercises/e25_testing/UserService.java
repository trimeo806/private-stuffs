package exercises.e25_testing;

import java.util.List;

/**
 * Service class used by E25 JUnit 5 / Mockito tests.
 * Demonstrates constructor-based dependency injection.
 */
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public String getUser(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public List<String> getAllUsers() {
        return repository.findAll();
    }

    public void createUser(String id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        repository.save(id, name);
    }
}
