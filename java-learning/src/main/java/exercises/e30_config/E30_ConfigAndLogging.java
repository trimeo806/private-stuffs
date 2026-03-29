package exercises.e30_config;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Exercise 30: Configuration, Properties, and Logging
 *
 * Every Spring Boot app uses externalized configuration (application.yml)
 * and structured logging. Understanding the underlying mechanics makes
 * Spring's "magic" completely transparent.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - application.properties / application.yml â†’ externalized config
 *   - @Value("${server.port}") â†’ inject config values
 *   - Profiles (dev, test, prod) â†’ environment-specific config
 *   - @ConfigurationProperties â†’ type-safe config binding
 *   - SLF4J + Logback â†’ Spring Boot's default logging framework
 *   - Actuator â†’ exposes config and health info at runtime
 *
 * TypeScript comparison:
 *   process.env.PORT          â†’ @Value("${server.port}")
 *   dotenv.config()           â†’ application.properties
 *   console.log('message')    â†’ log.info("message")
 */
public class E30_ConfigAndLogging {

    // === EXERCISE 1: Properties file basics ===
    // Java has built-in support for .properties files â€” this is what Spring uses internally.

    // TODO: Create a file "app.properties" with this content (in the project root):
    //       app.name=MyApp
    //       app.version=1.0.0
    //       server.port=8080
    //       database.url=jdbc:h2:mem:testdb
    //       database.username=sa
    //       database.password=
    //       feature.cache.enabled=true
    //       feature.cache.ttl=3600
    //
    // TODO: Load and read it:
    //       Properties props = new Properties();
    //       props.load(Files.newInputStream(Path.of("app.properties")));
    //       String appName = props.getProperty("app.name");
    //       String port = props.getProperty("server.port", "3000"); // with default
    //       int portNum = Integer.parseInt(port);
    //       Print all properties


    // === EXERCISE 2: Type-safe configuration class ===
    // This is what @ConfigurationProperties does in Spring Boot!

    // TODO: Create a class AppConfig that loads from Properties:
    //
    //   class AppConfig {
    //       private final String appName;
    //       private final String appVersion;
    //       private final int serverPort;
    //       private final String databaseUrl;
    //       private final String databaseUsername;
    //       private final boolean cacheEnabled;
    //       private final int cacheTtl;
    //
    //       AppConfig(Properties props) {
    //           this.appName = props.getProperty("app.name", "DefaultApp");
    //           this.serverPort = Integer.parseInt(props.getProperty("server.port", "8080"));
    //           // ... map all fields
    //       }
    //
    //       // Getters
    //       String getAppName() { return appName; }
    //       // ...
    //
    //       @Override
    //       public String toString() { return "AppConfig{name=%s, port=%d, ...}"; }
    //   }
    //
    // In Spring Boot:
    //   @ConfigurationProperties(prefix = "app")
    //   record AppConfig(String name, String version) {}
    //   â†’ Spring auto-maps app.name â†’ name, app.version â†’ version


    // === EXERCISE 3: Profiles ===
    // Spring Boot supports profiles: dev, test, prod â€” each with different config.

    // TODO: Create 3 properties files:
    //       app-dev.properties   â†’ server.port=8080, database.url=jdbc:h2:mem:devdb
    //       app-test.properties  â†’ server.port=8081, database.url=jdbc:h2:mem:testdb
    //       app-prod.properties  â†’ server.port=80, database.url=jdbc:postgresql://prod-server/mydb
    //
    // TODO: Create a method that loads config based on a "profile" argument:
    //   static Properties loadConfig(String profile) {
    //       String filename = "app-" + profile + ".properties";
    //       Properties props = new Properties();
    //       // Load base config first
    //       props.load(Files.newInputStream(Path.of("app.properties")));
    //       // Override with profile-specific config
    //       Path profilePath = Path.of(filename);
    //       if (Files.exists(profilePath)) {
    //           props.load(Files.newInputStream(profilePath));
    //       }
    //       return props;
    //   }
    //
    // TODO: Test with different profiles â€” show how the same code
    //       can run with different databases just by changing the profile.
    //
    // In Spring Boot: java -jar app.jar --spring.profiles.active=prod


    // === EXERCISE 4: Environment variables ===
    // Spring Boot resolves: ${PORT:8080} â†’ env var PORT, or default 8080.

    // TODO: Read environment variables:
    //       String javaHome = System.getenv("JAVA_HOME");
    //       String path = System.getenv("PATH");
    //       String customVar = System.getenv("MY_APP_SECRET");  // probably null
    //
    // TODO: Create a method that resolves config with env var override:
    //   static String resolveProperty(Properties props, String key) {
    //       // 1. Check environment variable (convert key: server.port â†’ SERVER_PORT)
    //       String envKey = key.toUpperCase().replace('.', '_');
    //       String envValue = System.getenv(envKey);
    //       if (envValue != null) return envValue;
    //       // 2. Fall back to properties file
    //       return props.getProperty(key);
    //   }
    //
    // This is exactly how Spring Boot resolves config values!
    // Priority: env vars > application.properties > defaults


    // === EXERCISE 5: java.util.logging (JUL) basics ===
    // Java has built-in logging. Spring Boot replaces it with SLF4J + Logback,
    // but the concepts are the same.

    // TODO: Create and use a logger:
    //       Logger logger = Logger.getLogger(E30_ConfigAndLogging.class.getName());
    //
    //       logger.severe("This is an ERROR message");    // ERROR in SLF4J
    //       logger.warning("This is a WARNING message");  // WARN
    //       logger.info("This is an INFO message");       // INFO
    //       logger.fine("This is a DEBUG message");       // DEBUG (hidden by default)
    //       logger.finest("This is a TRACE message");     // TRACE (hidden by default)
    //
    //       // Parameterized logging (avoids string concatenation when log level is off):
    //       logger.info("User {0} logged in from {1}",
    //           new Object[]{"Alice", "192.168.1.1"});

    // In Spring Boot:
    //   private static final Logger log = LoggerFactory.getLogger(MyService.class);
    //   log.info("User {} logged in from {}", username, ip);
    //   log.error("Failed to process order {}", orderId, exception);


    // === EXERCISE 6: SLF4J + Logback (Spring Boot's logging) ===
    // We added logback-classic in pom.xml â€” this IS Spring Boot's logger.

    // TODO: Use SLF4J (import org.slf4j.Logger; import org.slf4j.LoggerFactory;):
    //
    //   private static final org.slf4j.Logger log =
    //       org.slf4j.LoggerFactory.getLogger(E30_ConfigAndLogging.class);
    //
    //   log.trace("Trace message");    // most verbose, usually off
    //   log.debug("Debug message");    // development debugging
    //   log.info("Info message");      // normal operation
    //   log.warn("Warning message");   // potential issue
    //   log.error("Error message");    // something broke
    //
    //   // With parameters (no string concatenation â€” better performance!):
    //   log.info("User {} created order {} for ${}", "Alice", 42, 99.99);
    //
    //   // With exception:
    //   try { int x = 1/0; }
    //   catch (Exception e) { log.error("Calculation failed", e); }
    //
    // In Spring Boot, you just use @Slf4j (Lombok) and log is auto-created.


    // === EXERCISE 7: Structured logging pattern ===
    // TODO: Create a simple "RequestLogger" that logs API-style events:
    //
    //   class RequestLogger {
    //       private static final org.slf4j.Logger log = ...;
    //
    //       static void logRequest(String method, String path, int statusCode, long durationMs) {
    //           if (statusCode >= 500) {
    //               log.error("{} {} â†’ {} ({}ms)", method, path, statusCode, durationMs);
    //           } else if (statusCode >= 400) {
    //               log.warn("{} {} â†’ {} ({}ms)", method, path, statusCode, durationMs);
    //           } else {
    //               log.info("{} {} â†’ {} ({}ms)", method, path, statusCode, durationMs);
    //           }
    //       }
    //   }
    //
    //   RequestLogger.logRequest("GET", "/api/users", 200, 45);
    //   RequestLogger.logRequest("POST", "/api/users", 400, 12);
    //   RequestLogger.logRequest("GET", "/api/orders/999", 500, 230);
    //
    // This is what Spring Boot's access logs look like!


    public static void main(String[] args) throws IOException {

        // === Test Exercise 1 ===
        System.out.println("--- Properties ---");
        // TODO: Create and load app.properties

        // === Test Exercise 2 ===
        System.out.println("\n--- Type-safe config ---");
        // TODO: Create AppConfig from properties

        // === Test Exercise 3 ===
        System.out.println("\n--- Profiles ---");
        // TODO: Load dev, test, prod configs and compare

        // === Test Exercise 4 ===
        System.out.println("\n--- Environment variables ---");
        // TODO: Read env vars, show override behavior

        // === Test Exercise 5 ===
        System.out.println("\n--- java.util.logging ---");
        // TODO: Log messages at different levels

        // === Test Exercise 6 ===
        System.out.println("\n--- SLF4J + Logback ---");
        // TODO: Log with SLF4J parameterized messages

        // === Test Exercise 7 ===
        System.out.println("\n--- Structured request logging ---");
        // TODO: Log simulated HTTP requests

        System.out.println("\n=== Exercise 30 Complete ===");
    }
}
