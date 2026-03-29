package exercises.e28_database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 28: JDBC and SQL with H2 In-Memory Database
 *
 * JDBC = Java Database Connectivity â€” the foundation for ALL database access in Java.
 * Even JPA/Hibernate uses JDBC under the hood.
 *
 * We use H2, an in-memory database, so you don't need to install anything.
 * Spring Boot uses H2 for testing too!
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - Spring Data JPA generates SQL from method names â†’ need to understand SQL
 *   - @Query annotation â†’ write raw SQL/JPQL
 *   - Debugging â†’ you need to read SQL in logs
 *   - Flyway/Liquibase â†’ manage DB migrations with SQL scripts
 *   - Understanding JDBC helps debug JPA/Hibernate issues
 *
 * TypeScript comparison (with Prisma/TypeORM):
 *   prisma.user.findMany({ where: { age: { gt: 25 } } })
 *   â†’ SELECT * FROM users WHERE age > 25
 */
public class E28_JDBC_SQL {

    // H2 in-memory database URL â€” data lives only while the program runs
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    // === EXERCISE 1: Connect and create tables ===
    // TODO: Create a method: static Connection getConnection()
    //       return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    //
    // TODO: Create a method: static void createTables(Connection conn)
    //       Execute this SQL using conn.createStatement().execute(sql):
    //
    //       CREATE TABLE users (
    //           id BIGINT AUTO_INCREMENT PRIMARY KEY,
    //           name VARCHAR(100) NOT NULL,
    //           email VARCHAR(150) UNIQUE NOT NULL,
    //           age INT,
    //           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    //       );
    //
    //       CREATE TABLE orders (
    //           id BIGINT AUTO_INCREMENT PRIMARY KEY,
    //           user_id BIGINT NOT NULL,
    //           product VARCHAR(200) NOT NULL,
    //           amount DECIMAL(10,2) NOT NULL,
    //           order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    //           FOREIGN KEY (user_id) REFERENCES users(id)
    //       );
    //
    // This is what Flyway migration scripts look like in Spring Boot!


    // === EXERCISE 2: INSERT â€” using PreparedStatement (SAFE from SQL injection!) ===
    // TODO: Create a method: static long insertUser(Connection conn, String name, String email, int age)
    //
    //       String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
    //       PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    //       ps.setString(1, name);     // 1st ?
    //       ps.setString(2, email);    // 2nd ?
    //       ps.setInt(3, age);         // 3rd ?
    //       ps.executeUpdate();
    //
    //       // Get the auto-generated ID
    //       ResultSet keys = ps.getGeneratedKeys();
    //       keys.next();
    //       return keys.getLong(1);
    //
    // CRITICAL: NEVER concatenate user input into SQL strings!
    //   BAD:  "SELECT * FROM users WHERE name = '" + name + "'"  â† SQL INJECTION!
    //   GOOD: "SELECT * FROM users WHERE name = ?" + ps.setString(1, name)  â† SAFE!

    // TODO: Create a method: static void insertOrder(Connection conn, long userId, String product, double amount)


    // === EXERCISE 3: SELECT â€” reading data ===
    // TODO: Create a method: static void findAllUsers(Connection conn)
    //       Execute: SELECT * FROM users ORDER BY name
    //       Use ResultSet to iterate:
    //
    //       ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY name");
    //       while (rs.next()) {
    //           long id = rs.getLong("id");
    //           String name = rs.getString("name");
    //           String email = rs.getString("email");
    //           int age = rs.getInt("age");
    //           System.out.printf("  [%d] %s (%s) age=%d%n", id, name, email, age);
    //       }

    // TODO: Create a method: static String findUserByEmail(Connection conn, String email)
    //       Use PreparedStatement with WHERE email = ?
    //       Return the user name, or "Not found"

    // TODO: Create a method: static List<String> findUsersByMinAge(Connection conn, int minAge)
    //       SELECT name FROM users WHERE age >= ? ORDER BY age


    // === EXERCISE 4: UPDATE and DELETE ===
    // TODO: Create a method: static int updateUserEmail(Connection conn, long id, String newEmail)
    //       UPDATE users SET email = ? WHERE id = ?
    //       Return the number of affected rows (ps.executeUpdate() returns this)

    // TODO: Create a method: static int deleteUser(Connection conn, long id)
    //       DELETE FROM users WHERE id = ?


    // === EXERCISE 5: JOIN queries (relations) ===
    // TODO: Create a method: static void findOrdersWithUserName(Connection conn)
    //       Execute:
    //       SELECT u.name, o.product, o.amount
    //       FROM orders o
    //       JOIN users u ON o.user_id = u.id
    //       ORDER BY u.name, o.order_date
    //
    //       Print each row: "Alice ordered Laptop ($999.99)"
    //
    // This is what JPA does when you define @ManyToOne relationships!


    // === EXERCISE 6: Aggregate queries ===
    // TODO: Create a method: static void printOrderStats(Connection conn)
    //       Execute these queries and print results:
    //
    //       -- Total orders per user
    //       SELECT u.name, COUNT(*) as order_count, SUM(o.amount) as total
    //       FROM orders o JOIN users u ON o.user_id = u.id
    //       GROUP BY u.name
    //       ORDER BY total DESC
    //
    //       -- Average order amount
    //       SELECT AVG(amount) FROM orders


    // === EXERCISE 7: Transaction ===
    // Transactions ensure all-or-nothing: either ALL operations succeed, or NONE do.
    // Spring Boot uses @Transactional to manage this automatically.

    // TODO: Create a method: static void transferCredit(Connection conn, long fromId, long toId, double amount)
    //       1. conn.setAutoCommit(false);
    //       2. Deduct amount from 'from' user's latest order
    //       3. Add amount to 'to' user as a new order ("Credit transfer")
    //       4. If anything fails: conn.rollback();
    //       5. If all good: conn.commit();
    //       6. Finally: conn.setAutoCommit(true);
    //
    //   try {
    //       conn.setAutoCommit(false);
    //       // ... operations ...
    //       conn.commit();
    //   } catch (SQLException e) {
    //       conn.rollback();
    //       throw e;
    //   } finally {
    //       conn.setAutoCommit(true);
    //   }


    // === EXERCISE 8: DAO pattern (pre-JPA) ===
    // TODO: Create a record: record User(long id, String name, String email, int age) {}
    //
    // TODO: Create a class UserDao with:
    //       private final Connection conn;
    //       UserDao(Connection conn) { this.conn = conn; }
    //
    //       Optional<User> findById(long id)
    //       List<User> findAll()
    //       User save(String name, String email, int age)  â†’ returns User with generated id
    //       boolean deleteById(long id)
    //
    // This is exactly what Spring Data JPA's CrudRepository generates for you!
    // In Spring Boot:
    //   interface UserRepository extends JpaRepository<User, Long> {}
    //   â†’ auto-generates findById, findAll, save, deleteById


    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // === Test Exercise 1 ===
            // TODO: Create tables
            System.out.println("--- Creating tables ---");

            // === Test Exercise 2 ===
            // TODO: Insert users:
            //       Alice (alice@test.com, 30)
            //       Bob (bob@test.com, 25)
            //       Charlie (charlie@test.com, 35)
            // TODO: Insert orders:
            //       Alice: "Laptop" ($999.99), "Mouse" ($29.99)
            //       Bob: "Keyboard" ($79.99)
            //       Charlie: "Monitor" ($399.99), "Headphones" ($149.99)
            System.out.println("--- Inserting data ---");

            // === Test Exercise 3 ===
            System.out.println("\n--- All users ---");
            // TODO: findAllUsers(conn)
            // TODO: findUserByEmail(conn, "bob@test.com")
            // TODO: findUsersByMinAge(conn, 28)

            // === Test Exercise 4 ===
            System.out.println("\n--- Update & Delete ---");
            // TODO: Update Bob's email
            // TODO: Delete Charlie

            // === Test Exercise 5 ===
            System.out.println("\n--- Orders with user names ---");
            // TODO: findOrdersWithUserName(conn)

            // === Test Exercise 6 ===
            System.out.println("\n--- Order statistics ---");
            // TODO: printOrderStats(conn)

            // === Test Exercise 7 ===
            System.out.println("\n--- Transaction test ---");
            // TODO: Test transferCredit

            // === Test Exercise 8 ===
            System.out.println("\n--- DAO pattern ---");
            // TODO: Use UserDao to find, save, delete

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Exercise 28 Complete ===");
    }
}
