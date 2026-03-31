# Java Learning Exercises — Mục lục

> Hoàn thành E01 → E30 theo thứ tự. Sau khi xong E30, bạn đủ nền tảng để bắt đầu Spring Boot.

---

## Phase 1 — Java Core (E01–E06)

| #  | Bài                  | Thư mục            | File                        |
|----|----------------------|--------------------|-----------------------------|
| 01 | Variables & Types    | `e01_basics/`      | `E01_Variables.java`        |
| 02 | Operators            | `e01_basics/`      | `E02_Operators.java`        |
| 03 | String Methods       | `e01_basics/`      | `E03_StringMethods.java`    |
| 04 | Conditionals         | `e04_controlflow/` | `E04_Conditionals.java`     |
| 05 | Loops                | `e04_controlflow/` | `E05_Loops.java`            |
| 06 | Arrays               | `e04_controlflow/` | `E06_Arrays.java`           |

## Phase 2 — OOP (E07–E10)

| #  | Bài                          | Thư mục      | File                              |
|----|------------------------------|--------------|-----------------------------------|
| 07 | Classes & Objects            | `e07_oop/`   | `E07_ClassesAndObjects.java`      |
| 07.1 | Access Modifier Matrix     | `e07_oop/`   | `E07_1_AccessModifierMatrix.java` |
| 07.2 | Cross-Package Inheritance  | `e07_oop/`   | `E07_2_CrossPackageInheritance.java` |
| 07.3 | Static Members and State   | `e07_oop/`   | `E07_3_StaticMembersAndState.java` |
| 07.4 | Static Nested vs Inner     | `e07_oop/`   | `E07_4_StaticNestedVsInnerClass.java` |
| 07.5 | Design Decision Drills     | `e07_oop/`   | `E07_5_DesignDecisionDrills.java` |
| 08 | Inheritance & Polymorphism   | `e07_oop/`   | `E08_Inheritance.java`            |
| 09 | Interfaces & Abstract        | `e07_oop/`   | `E09_InterfacesAndAbstract.java`  |
| 10 | Records, Enums, Sealed       | `e07_oop/`   | `E10_Records.java`                |

## Phase 3 — Collections & Exceptions (E11–E15)

| #  | Bài                    | Thư mục             | File                        |
|----|------------------------|---------------------|-----------------------------|
| 11 | List & ArrayList       | `e11_collections/`  | `E11_ListsAndArrayList.java`|
| 12 | Maps & Sets            | `e11_collections/`  | `E12_MapsAndSets.java`      |
| 13 | Stack, Queue, Deque    | `e11_collections/`  | `E13_StackAndQueue.java`    |
| 14 | Try-Catch              | `e14_exceptions/`   | `E14_TryCatch.java`         |
| 15 | Custom Exceptions      | `e14_exceptions/`   | `E15_CustomExceptions.java` |

## Phase 4 — Functional Java (E16–E19)

| #  | Bài                    | Thư mục          | File                        |
|----|------------------------|------------------|-----------------------------|
| 16 | Lambda Expressions     | `e16_streams/`   | `E16_LambdaExpressions.java`|
| 17 | Stream API             | `e16_streams/`   | `E17_StreamAPI.java`        |
| 18 | Optional               | `e16_streams/`   | `E18_OptionalHandling.java` |
| 19 | Generics               | `e19_generics/`  | `E19_Generics.java`         |

## Phase 5 — Design Patterns (E20–E21)

| #  | Bài               | Thư mục          | File                       |
|----|--------------------|------------------|----------------------------|
| 20 | Builder Pattern    | `e20_patterns/`  | `E20_BuilderPattern.java`  |
| 21 | Strategy Pattern   | `e20_patterns/`  | `E21_StrategyPattern.java` |

---

## Phase 6 — Spring Boot Prerequisites (E22–E30) 🚀

> Các bài mới — lấp gap giữa Java core và Spring Boot.

| #  | Bài                              | Thư mục              | File                          | Gap được lấp                      |
|----|----------------------------------|----------------------|-------------------------------|-----------------------------------|
| 22 | Annotations                      | `e22_annotations/`   | `E22_Annotations.java`        | Custom annotations, reflection    |
| 23 | Concurrency                      | `e23_concurrency/`   | `E23_Concurrency.java`        | Threads, CompletableFuture, @Async|
| 24 | File I/O & JSON (Jackson)        | `e24_io/`            | `E24_FileIO_JSON.java`        | JSON serialization, @RequestBody  |
| 25 | JUnit 5 & Mockito                | `e25_testing/`       | `E25_JUnit5Testing_README.java`| Unit testing, mocking            |
| 26 | Dependency Injection             | `e26_di/`            | `E26_DependencyInjection.java`| IoC, @Autowired, @Service         |
| 27 | HTTP Client & REST               | `e27_http/`          | `E27_HttpClient_REST.java`    | REST verbs, status codes, API call|
| 28 | JDBC & SQL (H2)                  | `e28_database/`      | `E28_JDBC_SQL.java`           | SQL, PreparedStatement, JPA nền   |
| 29 | Input Validation                 | `e29_validation/`    | `E29_Validation.java`         | @Valid, @NotNull, sanitization    |
| 30 | Config, Properties & Logging     | `e30_config/`        | `E30_ConfigAndLogging.java`   | application.yml, profiles, SLF4J  |

---

## Cách chạy

```bash
# Chạy từng bài bằng IDE: Mở file → Run main()
# Hoặc dùng terminal:
cd java-learning
javac -cp "." src/main/java/exercises/e01_basics/E01_Variables.java
java -cp src/main/java exercises.e01_basics.E01_Variables

# Chạy tests (E25):
mvn test
```

## Sau khi xong E30 → Spring Boot

Bạn sẽ sẵn sàng cho:
- `@RestController`, `@Service`, `@Repository` (đã hiểu qua E22, E26)
- `@RequestBody` / JSON handling (E24)
- `@Valid` validation (E29)
- JPA/Hibernate (nền từ E28)
- Testing `@WebMvcTest` (nền từ E25)
- `application.yml` & profiles (E30)
