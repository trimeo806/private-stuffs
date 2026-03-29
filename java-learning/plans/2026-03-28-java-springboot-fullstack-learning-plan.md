# KẾ HOẠCH HỌC JAVA + SPRING BOOT CHO FRONTEND DEVELOPER

**Ngày tạo**: 28/03/2026
**Đối tượng**: Frontend developer (TypeScript/React/Next.js) muốn trở thành Fullstack Developer
**Thời gian đề xuất**: **6 tháng** (24 tuần) - cam kết 15-25 giờ/tuần
**Nguồn tham khảo**: roadmap.sh/java, roadmap.sh/backend, Spring.io official docs

---

## Mục Lục

1. [Tại sao 6 tháng mà không phải 2 tuần?](#tai-sao-6-thang)
2. [Những gì bạn đã biết sẽ chuyển đổi được](#kien-thuc-chuyen-doi)
3. [Tháng 1: Java Cơ Bản](#thang-1)
4. [Tháng 2: OOP và Collections](#thang-2)
5. [Tháng 3: SQL và Database](#thang-3)
6. [Tháng 4: Spring Boot và REST API](#thang-4)
7. [Tháng 5: Testing và Security](#thang-5)
8. [Tháng 6: Full-Stack Integration và Deploy](#thang-6)
9. [Những điều cần chú ý để trở thành Fullstack Developer](#chu-y-fullstack)
10. [Tài liệu học tập](#tai-lieu)
11. [So sánh cú pháp TypeScript vs Java](#so-sanh-cu-phap)
12. [Spring Boot Annotations — Bảng tra cứu nhanh](#annotations)
13. [Tính năng mới của Java hiện đại (17-24)](#java-modern-features)

---

## Tại sao 6 tháng mà không phải 2 tuần? {#tai-sao-6-thang}

**Thực tế**: 2 tuần chỉ đủ để làm quen cú pháp Java cơ bản. Để thực sự build được backend feature với Spring Boot và deploy production, bạn cần tối thiểu **6 tháng** với cam kết học nghiêm túc.

| Thời gian | Bạn có thể làm được gì |
|-----------|----------------------|
| 2 tuần | Viết chương trình console đơn giản bằng Java |
| 1 tháng | Hiểu OOP, viết class, interface |
| 2 tháng | Viết SQL query, thiết kế database |
| 3 tháng | Build REST API đơn giản với Spring Boot |
| 4 tháng | Viết unit test, integration test |
| 5 tháng | Thêm authentication/authorization |
| **6 tháng** | **Deploy full-stack app (React + Spring Boot)** |

**Lời khuyên**: Đừng có rush. Java + Spring Boot có learning curve cao hơn việc học một frontend framework mới. Nhưng một khi đã thông, bạn sẽ có competitive advantage cực lớn trên thị trường.

---

## Những gì bạn đã biết sẽ chuyển đổi được {#kien-thuc-chuyen-doi}

Tin vui: bạn không bắt đầu từ số 0. Rất nhiều kiến thức frontend sẽ map trực tiếp sang backend.

| Bạn đã biết (TypeScript/React) | Map sang (Java/Spring Boot) | Mức độ chuyển đổi |
|-------------------------------|---------------------------|-------------------|
| TypeScript static typing | Java static typing | **Rất tốt** — Java nghiêm ngặt hơn, không có `any` |
| React components | Java classes, Spring beans | **Tốt** — cả hai đều là encapsulation |
| Props/Context API | Dependency Injection (`@Autowired`) | **Tốt** — cùng là truyền dependency |
| fetch/axios gọi API | `RestTemplate`, `WebClient` | **Tốt** — bạn hiểu HTTP rồi |
| React Router / Next.js routing | `@RequestMapping`, `@GetMapping` | **Tốt** — routing logic tương tự |
| Zod / io-ts validation | Bean Validation, `@Valid` | **Tốt** — cả hai validate input |
| Jest / Vitest testing | JUnit, Mockito | **Tốt** — cùng triết lý, khác syntax |
| npm / yarn / pnpm | Maven, Gradle | **Trung bình** — verbose hơn |
| async/await, Promise | `CompletableFuture`, threads | **Cần học thêm** — Java multi-thread |
| Prisma / TypeORM | Hibernate, JPA | **Cần học thêm** — ORM concepts tương tự |
| console.log debugging | Logback, SLF4J | **Cần học thêm** — structured logging |
| `.env` files | `application.properties` | **Dễ** — config tương tự |

### Điểm mạnh của bạn khi chuyển sang backend:

1. **Hiểu API từ phía consumer** → giờ bạn sẽ build API từ phía provider
2. **Hiểu component architecture** → Spring Boot cũng có layered architecture (Controller → Service → Repository)
3. **Quen với type system** → Java strict typing sẽ không xa lạ
4. **Biết testing** → chỉ cần học JUnit syntax, triết lý giống nhau

---

## Tháng 1: Java Cơ Bản (Tuần 1-4) {#thang-1}

> **Mục tiêu**: Viết được chương trình Java cơ bản, hiểu syntax và sự khác biệt với TypeScript.
> **Thời gian**: 15-20 giờ/tuần

### Tuần 1-2: Setup & Cú pháp cơ bản

**Việc cần làm:**
- [ ] Cài đặt JDK 21+ (LTS)
- [ ] Cài đặt IntelliJ IDEA Community (miễn phí) hoặc dùng VS Code + Extension Pack for Java
- [ ] Tạo project Hello World đầu tiên
- [ ] Học: Variables, data types (primitives vs objects), type casting
- [ ] Học: String handling (StringBuilder, String methods)
- [ ] Học: Control flow (if/else, switch, for, while, do-while)
- [ ] Học: Arrays và cách thao tác

**Bài tập thực hành:**
- Viết máy tính (Calculator) xử lý phép +, -, *, /
- Viết chương trình chuyển đổi nhiệt độ (Celsius ↔ Fahrenheit)
- Viết chương trình đếm từ trong chuỗi

**So sánh nhanh để hiểu:**
```
TypeScript:                          Java:
let name: string = "Tri"            String name = "Tri";
const age: number = 25              int age = 25;
let items: string[] = []            List<String> items = new ArrayList<>();
const greet = (n: string) => ...    public void greet(String n) { ... }
```

### Tuần 3-4: Exception Handling & Methods

**Việc cần làm:**
- [ ] Học: Methods (return types, parameters, overloading)
- [ ] Học: Exception handling (try/catch/finally, checked vs unchecked)
- [ ] Học: Java packages và imports
- [ ] Học: File I/O cơ bản
- [ ] Học: Java memory model cơ bản (Stack vs Heap)

**Bài tập thực hành:**
- Viết chương trình quản lý danh bạ (Contact Manager) trên console
- Viết chương trình đọc/ghi file text
- Viết todo list app trên console

**Điểm khác biệt quan trọng với TypeScript:**
- Java bắt buộc khai báo kiểu dữ liệu — không có `any`
- Java có **checked exceptions** — bạn PHẢI catch hoặc throw, không giống JS để catch gì cũng được
- Java có access modifiers thật sự (public, private, protected) — TypeScript chỉ có decorator
- Semicolons là bắt buộc trong Java

**Exit criteria**: Viết được chương trình console 200+ dòng không gặp lỗi cú pháp.

---

## Tháng 2: OOP và Collections (Tuần 5-8) {#thang-2}

> **Mục tiêu**: Thành thạo OOP patterns, làm việc với Collections framework và functional programming trong Java.
> **Thời gian**: 15-20 giờ/tuần

### Tuần 5-6: Object-Oriented Programming

**Việc cần làm:**
- [ ] Học: Classes, Objects, Constructors
- [ ] Học: Encapsulation (private fields + getters/setters)
- [ ] Học: Inheritance (extends) và khi nào nên dùng
- [ ] Học: Polymorphism (method overriding, method overloading)
- [ ] Học: Abstract classes vs Interfaces — khi nào dùng cái nào
- [ ] Học: Composition vs Inheritance — favor composition

**Bài tập thực hành:**
- Xây dựng hệ thống quản lý sinh viên (Student Management System)
  - Class: Student, Course, Enrollment
  - Interface: Gradable, Printable
  - Inheritance: UndergraduateStudent extends Student
- Xây dựng hệ thống hình học: Shape → Circle, Rectangle, Triangle (polymorphism)

**Lưu ý quan trọng:**
> Bạn đã biết OOP từ TypeScript classes và React components. Nhưng Java **bắt buộc** OOP
> nghiêm ngặt hơn. Mọi thứ trong Java đều phải ở trong một class. Không có top-level
> functions như TypeScript.

### Tuần 7-8: Collections & Functional Programming

**Việc cần làm:**
- [ ] Học: ArrayList, LinkedList — khi nào dùng loại nào
- [ ] Học: HashSet, TreeSet — unique elements
- [ ] Học: HashMap, TreeMap — key-value storage
- [ ] Học: Queue, Stack, Deque
- [ ] Học: Lambda expressions (`->` tương tự `=>` trong TS)
- [ ] Học: Stream API (`map`, `filter`, `reduce`, `collect`)
- [ ] Học: Optional<T> — xử lý null an toàn
- [ ] Học: Functional Interfaces (Predicate, Function, Consumer, Supplier)
- [ ] Học: Concurrency cơ bản — Thread, Runnable, ExecutorService
- [ ] Học: CompletableFuture — async operations (tương tự Promise trong JS)
- [ ] Học: Virtual Threads (Java 21) — tạo triệu threads nhẹ, thay thế async patterns
- [ ] Học: Thread safety cơ bản — synchronized, ReentrantLock, ConcurrentHashMap

**Bài tập thực hành:**
- Viết chương trình phân tích dữ liệu: đọc danh sách employees, lọc theo department, tính trung bình lương (dùng Streams)
- Implement một in-memory key-value store (mini Redis)
- Xử lý một file CSV lớn dùng Streams
- Viết web scraper đơn giản dùng Virtual Threads — fetch 100 URLs song song
- So sánh performance: Platform Threads vs Virtual Threads vs CompletableFuture

**Map từ TypeScript:**
```
TypeScript:                              Java:
users.filter(u => u.active)              users.stream().filter(u -> u.isActive())
users.map(u => u.name)                   users.stream().map(User::getName)
users.reduce((sum, u) => sum + u.age, 0) users.stream().mapToInt(User::getAge).sum()
value ?? "default"                       Optional.ofNullable(value).orElse("default")
await Promise.all([f1(), f2()])          CompletableFuture.allOf(cf1, cf2).join()
new Promise(resolve => ...)              CompletableFuture.supplyAsync(() -> ...)
```

**Exit criteria**: Viết được chương trình multi-class dùng collections, streams, và virtual threads.

---

## Tháng 3: SQL và Database (Tuần 9-12) {#thang-3}

> **Mục tiêu**: Thiết kế database schema, viết SQL queries, và hiểu ORM concepts.
> **Thời gian**: 15-20 giờ/tuần

### Tuần 9-10: SQL Cơ Bản

**Việc cần làm:**
- [ ] Cài đặt PostgreSQL (khuyên dùng) hoặc MySQL
- [ ] Học: CREATE TABLE, data types, constraints (NOT NULL, UNIQUE, PRIMARY KEY, FOREIGN KEY)
- [ ] Học: INSERT, SELECT, UPDATE, DELETE
- [ ] Học: WHERE, ORDER BY, GROUP BY, HAVING
- [ ] Học: JOINs (INNER, LEFT, RIGHT, FULL)
- [ ] Học: Subqueries và aggregate functions (COUNT, SUM, AVG, MIN, MAX)
- [ ] Học: Indexes — tại sao quan trọng cho performance

**Bài tập thực hành:**
- Thiết kế schema cho blog platform: users, posts, comments, tags
- Viết 20 SQL queries từ đơn giản đến phức tạp
- Tối ưu query bằng cách thêm index

### Tuần 11-12: JPA/Hibernate & Spring Data JPA

**Việc cần làm:**
- [ ] Học: JPA là gì? Hibernate là gì? Mối quan hệ giữa chúng
- [ ] Học: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`
- [ ] Học: Relationships: `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne`
- [ ] Học: Spring Data JPA — `JpaRepository`, tự động generate queries từ method names
- [ ] Học: Custom queries với `@Query` (JPQL)
- [ ] Học: Database migration với Flyway hoặc Liquibase (cơ bản)

**Bài tập thực hành:**
- Tạo Java project với Spring Data JPA, kết nối PostgreSQL
- Map các entity: User, Post, Comment với relationships
- Viết CRUD operations dùng JpaRepository
- Viết custom queries

**Map từ TypeScript (Prisma):**
```
Prisma schema:                           JPA Entity:
model User {                             @Entity
  id    Int     @id @default(autoincrement())  @Table(name = "users")
  email String  @unique                  public class User {
  name  String                               @Id @GeneratedValue
  posts Post[]                               private Long id;
}                                            @Column(unique = true)
                                             private String email;
                                             private String name;
                                             @OneToMany(mappedBy = "user")
                                             private List<Post> posts;
                                         }
```

**Exit criteria**: Map được Java objects sang database tables, viết được CRUD + custom queries.

---

## Tháng 4: Spring Boot và REST API (Tuần 13-18) {#thang-4}

> **Mục tiêu**: Build được REST API hoàn chỉnh với Spring Boot, hiểu kiến trúc 3 lớp.
> **Thời gian**: 20-25 giờ/tuần
> **Đây là tháng quan trọng nhất** — tất cả kiến thức trước đó hội tụ tại đây.

### Tuần 13-14: Spring Framework Cơ Bản

**Việc cần làm:**
- [ ] Học: Spring IoC Container — Inversion of Control là gì?
- [ ] Học: Dependency Injection — 3 loại: Constructor, Setter, Field injection
- [ ] Học: Spring Beans — lifecycle, scope (singleton, prototype)
- [ ] Học: Component scanning và auto-configuration
- [ ] Học: `application.properties` / `application.yml` — cấu hình project
- [ ] Tạo Spring Boot project đầu tiên bằng Spring Initializr (start.spring.io)

**Hiểu DI qua TypeScript tương đương:**
```
TypeScript (React Context):                Java (Spring DI):
// Provider                                @Service
const UserProvider = ({children}) => {     public class UserService {
  const userService = new UserService();       // Spring tự động inject
  return (                                     @Autowired
    <UserContext.Provider value={userService}>  private UserRepository repo;
      {children}                           }
    </UserContext.Provider>
  );                                       @RestController
};                                         public class UserController {
// Consumer                                    // Constructor injection (recommended)
const Component = () => {                      private final UserService service;
  const userService = useContext(UserContext);  public UserController(UserService s) {
  // sử dụng userService                           this.service = s;
};                                             }
                                           }
```

### Tuần 15-18: Build REST API

**Việc cần làm:**
- [ ] Học: `@RestController`, `@RequestMapping`
- [ ] Học: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- [ ] Học: `@RequestBody`, `@PathVariable`, `@RequestParam`
- [ ] Học: Response types: `ResponseEntity<T>` — control HTTP status codes
- [ ] Học: Input validation với `@Valid` và Bean Validation annotations (`@NotNull`, `@Size`, `@Email`)
- [ ] Học: Error handling với `@ExceptionHandler` và `@ControllerAdvice`
- [ ] Học: DTO pattern (Data Transfer Object) — tách entity khỏi API response
- [ ] Học: Pagination với `Pageable`
- [ ] Học: CORS configuration
- [ ] Học: `RestClient` (Spring Boot 3.2+) — HTTP client hiện đại thay RestTemplate
- [ ] Học: OpenAPI/Swagger documentation với `springdoc-openapi` — tự sinh API docs
- [ ] Học: Caching với `@Cacheable`, `@CacheEvict` — tăng performance
- [ ] Học: Problem Details RFC 9457 — chuẩn error response format (Spring Boot 3.x hỗ trợ sẵn)
- [ ] Bật Virtual Threads: `spring.threads.virtual.enabled=true` — tăng throughput

**Dự án thực hành — Build Task Management API:**

```
Endpoints:
POST   /api/auth/register     — Đăng ký user
POST   /api/auth/login         — Đăng nhập
GET    /api/tasks              — Lấy danh sách tasks (có pagination)
POST   /api/tasks              — Tạo task mới
GET    /api/tasks/{id}         — Lấy chi tiết task
PUT    /api/tasks/{id}         — Cập nhật task
DELETE /api/tasks/{id}         — Xóa task
GET    /api/tasks?status=DONE  — Lọc theo status
```

**Cấu trúc project:**
```
src/main/java/com/example/taskmanager/
├── TaskManagerApplication.java          # Entry point
├── controller/
│   ├── AuthController.java             # Authentication endpoints
│   └── TaskController.java             # Task CRUD endpoints
├── service/
│   ├── AuthService.java                # Auth business logic
│   └── TaskService.java                # Task business logic
├── repository/
│   └── TaskRepository.java             # Database access (interface)
│   └── UserRepository.java
├── model/
│   ├── Task.java                       # @Entity
│   └── User.java                       # @Entity
├── dto/
│   ├── TaskRequest.java                # Input DTO
│   ├── TaskResponse.java               # Output DTO
│   └── LoginRequest.java
├── exception/
│   ├── TaskNotFoundException.java
│   └── GlobalExceptionHandler.java     # @ControllerAdvice
└── config/
    └── CorsConfig.java
```

**So sánh với Next.js API route:**
```
Next.js:                                     Spring Boot:
// app/api/tasks/route.ts                    @RestController
export async function GET(req) {             @RequestMapping("/api/tasks")
  const tasks = await prisma.task.findMany() public class TaskController {
  return Response.json(tasks)                    @GetMapping
}                                                public List<TaskResponse> getAll() {
                                                     return taskService.findAll();
export async function POST(req) {                }
  const body = await req.json()                  @PostMapping
  const task = await prisma.task.create(...)      public ResponseEntity<TaskResponse>
  return Response.json(task, {status: 201})           create(@Valid @RequestBody TaskRequest r) {
}                                                        return ResponseEntity.status(201)
                                                             .body(taskService.create(r));
                                                     }
                                                 }
```

**Exit criteria**: Build được REST API CRUD hoàn chỉnh, test được bằng Postman/Insomnia, có validation và error handling.

---

## Tháng 5: Testing và Security (Tuần 19-22) {#thang-5}

> **Mục tiêu**: Viết unit test + integration test, thêm authentication cho API.
> **Thời gian**: 20-25 giờ/tuần

### Tuần 19-20: Testing

**Việc cần làm:**
- [ ] Học: JUnit 5 — `@Test`, `@BeforeEach`, `@AfterEach`, assertions
- [ ] Học: Mockito — mock dependencies, `@Mock`, `@InjectMocks`, `when().thenReturn()`
- [ ] Học: `@SpringBootTest` — integration test với full Spring context
- [ ] Học: `@WebMvcTest` — test chỉ controller layer
- [ ] Học: `@DataJpaTest` — test chỉ repository layer
- [ ] Học: MockMvc — test HTTP endpoints
- [ ] Học: Test database với H2 (in-memory) hoặc Testcontainers

**Bài tập thực hành:**
- Viết unit tests cho TaskService (mock repository)
- Viết integration tests cho TaskController (MockMvc)
- Đặt mục tiêu coverage: 70%+

**Map từ Jest:**
```
Jest:                                    JUnit + Mockito:
describe('TaskService', () => {          @ExtendWith(MockitoExtension.class)
  let service;                           class TaskServiceTest {
  let mockRepo;                              @Mock TaskRepository repo;
                                             @InjectMocks TaskService service;
  beforeEach(() => {
    mockRepo = { findAll: jest.fn() };       @Test
    service = new TaskService(mockRepo);     void shouldReturnAllTasks() {
  });                                            when(repo.findAll())
                                                     .thenReturn(List.of(task1));
  test('returns all tasks', () => {              var result = service.findAll();
    mockRepo.findAll.mockReturnValue([t1]);      assertEquals(1, result.size());
    expect(service.findAll()).toHaveLength(1);    }
  });                                        }
});
```

### Tuần 21-22: Spring Security & JWT

**Việc cần làm:**
- [ ] Học: Spring Security — FilterChain, SecurityConfig
- [ ] Học: Password hashing với BCrypt
- [ ] Học: JWT (JSON Web Token) — tạo token, verify token
- [ ] Học: Stateless authentication flow: Login → JWT → Authorize requests
- [ ] Học: Role-based access control (`@PreAuthorize`, `hasRole()`)
- [ ] Học: CORS + CSRF configuration cho React frontend

**Bài tập thực hành:**
- Thêm JWT authentication cho Task Management API
- Protected routes: chỉ user đã login mới truy cập được `/api/tasks`
- Admin routes: chỉ admin mới xóa được tasks

**Flow tương tự NextAuth.js:**
```
NextAuth.js:                             Spring Security + JWT:
1. User login → NextAuth tạo session    1. User login → Spring tạo JWT token
2. Session cookie gửi mỗi request       2. JWT gửi trong Authorization header
3. getServerSession() check auth         3. JwtAuthFilter check token mỗi request
4. middleware.ts protect routes          4. SecurityFilterChain protect endpoints
```

**Exit criteria**: API có authentication, tests pass, chỉ authenticated users truy cập được protected endpoints.

---

## Tháng 6: Full-Stack Integration & Deploy (Tuần 23-26) {#thang-6}

> **Mục tiêu**: Kết nối React/Next.js frontend với Spring Boot backend, deploy lên cloud.
> **Thời gian**: 20-30 giờ/tuần

### Tuần 23-24: Full-Stack Integration

**Việc cần làm:**
- [ ] Tạo Next.js frontend project
- [ ] Kết nối frontend với Spring Boot API (fetch/axios)
- [ ] Xử lý JWT token ở frontend (lưu localStorage hoặc httpOnly cookie)
- [ ] Implement: Login page, Register page, Task list, Task form
- [ ] Xử lý loading states, error states, empty states
- [ ] CORS configuration giữa 2 origins
- [ ] Environment variables cho API URL

**Kiến trúc Full-Stack:**
```
┌──────────────────────────┐     HTTP/REST     ┌──────────────────────────┐
│     Next.js Frontend     │ ──────────────── │   Spring Boot Backend    │
│                          │                   │                          │
│  React Components        │  GET /api/tasks   │  @RestController         │
│  State Management        │ ◄───────────────  │  @Service                │
│  React Query / SWR       │  POST /api/tasks  │  @Repository             │
│  Auth Context            │ ────────────────► │  Spring Security + JWT   │
│                          │  Authorization:   │                          │
│  localhost:3000           │  Bearer <jwt>     │  localhost:8080          │
└──────────────────────────┘                   └──────────┬───────────────┘
                                                          │
                                                          │ JPA/Hibernate
                                                          ▼
                                               ┌──────────────────────┐
                                               │    PostgreSQL DB     │
                                               │    localhost:5432    │
                                               └──────────────────────┘
```

### Tuần 25-26: Docker & Deployment

**Việc cần làm:**
- [ ] Viết Dockerfile cho Spring Boot app
- [ ] Viết Dockerfile cho Next.js app
- [ ] Viết `docker-compose.yml` chạy cả 3 services (frontend + backend + database)
- [ ] Deploy lên cloud (chọn 1): Railway, Render, Fly.io, hoặc AWS
- [ ] Setup CI/CD cơ bản với GitHub Actions
- [ ] Monitoring cơ bản: health check endpoint, structured logging

**docker-compose.yml mẫu:**
```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: taskmanager
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports: ["5432:5432"]

  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on: [db]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/taskmanager

  frontend:
    build: ./frontend
    ports: ["3000:3000"]
    depends_on: [backend]
    environment:
      NEXT_PUBLIC_API_URL: http://backend:8080
```

**Exit criteria**: Full-stack app chạy được trên Docker, deploy được lên cloud, có CI/CD.

---

## Những điều cần chú ý để trở thành Fullstack Developer {#chu-y-fullstack}

### 10 điều quan trọng nhất

| # | Điều cần chú ý | Tại sao |
|---|---------------|---------|
| 1 | **Hiểu kiến trúc 3 lớp** (Controller → Service → Repository) | Đây là nền tảng của mọi backend app. Tương tự component → hook → API call trong React |
| 2 | **SQL không thể thay thế** | ORM (JPA/Hibernate) giúp nhưng bạn vẫn phải hiểu SQL để debug và tối ưu |
| 3 | **Security là bắt buộc, không phải optional** | Authentication, authorization, input validation, CORS — thiếu 1 cái là có lỗ hổng |
| 4 | **Testing phải đi kèm code** | Unit test + integration test. Không có test = không tin tưởng được code |
| 5 | **Transactions và data consistency** | Frontend không có khái niệm này. `@Transactional` đảm bảo data không bị corrupt |
| 6 | **Error handling phải có chiến lược** | Không chỉ try/catch — cần `@ControllerAdvice`, error codes, và clear error messages cho frontend |
| 7 | **API design là giao tiếp** | API là contract giữa frontend và backend. Thiết kế tốt = frontend dev (bao gồm chính bạn) làm việc dễ |
| 8 | **Performance mindset** | N+1 queries, missing indexes, connection pooling — những vấn đề frontend không gặp |
| 9 | **Logging và monitoring** | `console.log` không đủ. Cần structured logging (Logback/SLF4J) để debug production |
| 10 | **Docker là kỹ năng bắt buộc** | Mọi fullstack dev cần biết Docker để setup local dev environment và deploy |

### Tư duy chuyển đổi từ Frontend sang Fullstack

```
Frontend mindset:                    Fullstack mindset:
"API trả về data"          →         "Tôi thiết kế API trả về data gì, format nào"
"UI render nhanh"          →         "API response < 200ms, database query < 50ms"
"User nhập gì cũng được"   →         "Validate ở cả frontend VÀ backend"
"Lỗi thì hiện toast"       →         "Log error, trả error code, hiện toast"
"Data từ API"              →         "Data từ database, qua cache, qua API, đến UI"
"Auth là getSession()"     →         "Auth là hash password, issue JWT, verify mỗi request"
```

### 5 sai lầm thường gặp của Frontend dev học Backend

| Sai lầm | Cách fix |
|---------|---------|
| Chỉ validate ở frontend | Luôn validate ở cả backend — frontend validation có thể bypass |
| Không xử lý concurrent requests | Học `@Transactional`, optimistic locking |
| Trả về quá nhiều data từ API | Dùng DTO, pagination, và chỉ trả những field cần thiết |
| Không viết test | Viết test TRƯỚC khi code (hoặc ít nhất là song song) |
| Bỏ qua security | Spring Security có sẵn, chỉ cần cấu hình — không có lý do bỏ qua |

---

## Tài liệu học tập {#tai-lieu}

### Java Cơ Bản
| Tài liệu | Loại | Link |
|---------|------|------|
| Java Brains (YouTube) | Video miễn phí | https://www.youtube.com/@JavaBrains |
| GeeksforGeeks Java | Bài viết | https://www.geeksforgeeks.org/advance-java/ |
| Oracle Java Tutorials | Docs chính thức | https://docs.oracle.com/javase/tutorial/ |

### Spring Boot
| Tài liệu | Loại | Link |
|---------|------|------|
| Spring.io Guides | Tutorials chính thức | https://spring.io/guides/ |
| Spring Boot Tutorial (official) | Docs | https://docs.spring.io/spring-boot/tutorial/index.html |
| Baeldung Spring Boot | Bài viết chi tiết | https://www.baeldung.com/spring-boot |
| Spring Office Hours Resources | Community | https://github.com/spring-office-hours/resources-learning-spring |

### REST API & JPA
| Tài liệu | Loại | Link |
|---------|------|------|
| Building REST with Spring | Official guide | https://spring.io/guides/tutorials/rest/ |
| Baeldung JPA Guide | Tutorial | https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa |
| FreeCodeCamp Spring Boot REST | Full guide | https://www.freecodecamp.org/news/how-to-build-a-rest-api-with-spring-boot-using-mysql-and-jpa/ |

### Full-Stack (React + Spring Boot)
| Tài liệu | Loại | Link |
|---------|------|------|
| Okta: React + Spring Boot CRUD | Tutorial | https://developer.okta.com/blog/2022/06/17/simple-crud-react-and-spring-boot |
| Spring Boot + React/Next.js 2025 | Guide | https://dev.to/dev_tips/spring-boot-reactnextjs-how-to-build-a-full-stack-app-in-2025-906 |
| GitHub Demo App | Source code | https://github.com/mr-ufukgurbuz/spring-boot-react-typescript-full-stack-app-demo |

### Docker & Deploy
| Tài liệu | Loại | Link |
|---------|------|------|
| Docker + Spring Boot | Official blog | https://www.docker.com/blog/kickstart-your-spring-boot-application-development/ |
| Dockerize Spring Boot | Tutorial | https://howtodoinjava.com/devops/dockerize-spring-boot-application/ |

### SQL & Database
| Tài liệu | Loại | Link |
|---------|------|------|
| PostgreSQL Docs | Official | https://www.postgresql.org/docs/ |
| FreeCodeCamp SQL | Video miễn phí | https://www.freecodecamp.org/ |

---

## So sánh cú pháp TypeScript vs Java {#so-sanh-cu-phap}

| Concept | TypeScript | Java |
|---------|-----------|------|
| Khai báo biến | `let name: string = "Tri"` | `String name = "Tri";` |
| Hằng số | `const MAX = 100` | `final int MAX = 100;` |
| Mảng | `let arr: string[] = []` | `List<String> arr = new ArrayList<>();` |
| Object/Map | `{key: "value"}` | `Map<String, String> map = new HashMap<>();` |
| Function | `function greet(n: string): void {}` | `public void greet(String n) {}` |
| Arrow/Lambda | `(x) => x * 2` | `x -> x * 2` |
| Class | `class User { constructor(public name: string) {} }` | `public class User { private String name; public User(String name) { this.name = name; } }` |
| Interface | `interface Printable { print(): void }` | `public interface Printable { void print(); }` |
| Enum | `enum Status { ACTIVE, INACTIVE }` | `public enum Status { ACTIVE, INACTIVE }` |
| Throw error | `throw new Error("msg")` | `throw new RuntimeException("msg");` |
| For-each | `for (const x of arr)` | `for (String x : arr)` |
| Null check | `user?.name ?? "default"` | `Optional.ofNullable(user).map(User::getName).orElse("default")` |
| Template string | `` `Hello ${name}` `` | `"Hello " + name` hoặc `String.format("Hello %s", name)` |
| Async | `async/await` | `CompletableFuture<T>` |
| Import | `import { User } from './user'` | `import com.example.model.User;` |

---

## Spring Boot Annotations — Bảng tra cứu nhanh {#annotations}

| Annotation | Chức năng | Dùng ở đâu |
|-----------|----------|-----------|
| `@SpringBootApplication` | Điểm bắt đầu của ứng dụng | Main class |
| `@RestController` | Class xử lý HTTP requests, trả về JSON | Controller class |
| `@RequestMapping("/api")` | Base URL cho controller | Controller class |
| `@GetMapping("/path")` | Xử lý GET request | Method |
| `@PostMapping("/path")` | Xử lý POST request | Method |
| `@PutMapping("/path")` | Xử lý PUT request | Method |
| `@DeleteMapping("/path")` | Xử lý DELETE request | Method |
| `@RequestBody` | Parse JSON body thành Java object | Method parameter |
| `@PathVariable` | Lấy giá trị từ URL path (`/users/{id}`) | Method parameter |
| `@RequestParam` | Lấy query parameter (`?status=DONE`) | Method parameter |
| `@Valid` | Kích hoạt validation trên input | Method parameter |
| `@Service` | Đánh dấu class chứa business logic | Service class |
| `@Repository` | Đánh dấu class/interface truy cập database | Repository interface |
| `@Component` | Đánh dấu class là Spring bean chung | Any class |
| `@Autowired` | Tự động inject dependency | Field/Constructor |
| `@Configuration` | Class cấu hình Spring | Config class |
| `@Bean` | Tạo bean từ method | Method trong @Configuration |
| `@Transactional` | Quản lý database transaction | Method/Class |
| `@Entity` | Đánh dấu class tương ứng với database table | Model class |
| `@Table(name="x")` | Chỉ định tên table | Model class |
| `@Id` | Đánh dấu primary key | Field trong @Entity |
| `@GeneratedValue` | Tự động generate ID | Field @Id |
| `@Column` | Tùy chỉnh column (nullable, unique, name) | Field trong @Entity |
| `@OneToMany` | Quan hệ 1-nhiều | Field trong @Entity |
| `@ManyToOne` | Quan hệ nhiều-1 | Field trong @Entity |
| `@ExceptionHandler` | Xử lý exception cụ thể | Method |
| `@ControllerAdvice` | Global exception handler | Class |
| `@PreAuthorize` | Kiểm tra quyền trước khi chạy method | Method |

---

## Tính năng mới của Java hiện đại (17-24) {#java-modern-features}

> **Khuyến nghị**: Dùng **Java 21 LTS** cho project mới. Spring Boot 3.x yêu cầu tối thiểu Java 17, nhưng Java 21 cho phép dùng tất cả tính năng bên dưới.

### Bảng tương thích Spring Boot & Java

| Spring Boot | Java tối thiểu | Java khuyến nghị | Java tối đa |
|---|---|---|---|
| 3.0 - 3.2 | Java 17 | Java 17 LTS | Java 21 |
| 3.3 - 3.4 | Java 17 | Java 21 LTS | Java 23 |
| 3.5.x (mới nhất 2025) | Java 17 | Java 21 LTS | Java 24 |
| 4.0 (tháng 11/2025) | Java 21+ | Java 21 LTS | TBD |

### Bảng tổng hợp: TypeScript tương đương → Java

| Bạn đã biết (TypeScript) | Tính năng Java mới | Phiên bản |
|---|---|---|
| `readonly` / `as const` immutability | **Records** — class bất biến 1 dòng | Java 16 |
| Union types + exhaustive switch | **Sealed classes** + Pattern matching | Java 17 + 21 |
| Type guards (`typeof`, `instanceof`) | **Pattern matching instanceof** | Java 16 |
| Switch + type narrowing | **Pattern matching switch** | Java 21 |
| Template literals `` `${var}` `` | **Text blocks** `"""..."""` | Java 15 |
| Template literals với expressions | ~~**String templates**~~ (đã bị rút khỏi Java 23, dùng `"".formatted()` hoặc text blocks) | — |
| async/await (event loop) | **Virtual threads** — triệu threads nhẹ | Java 21 |
| `arr[0]`, `arr.at(-1)` | **Sequenced Collections** — `.getFirst()`, `.getLast()` | Java 21 |
| `_` unused variable | **Unnamed variables** — `_` bỏ qua biến | Java 22 |

---

### 1. Records — Class bất biến 1 dòng (Java 16)

**Vấn đề**: Trong Java cũ, tạo một DTO cần 20-30 dòng code (constructor, getters, equals, hashCode, toString).

**Trước (Java 15):**
```java
// 25+ dòng cho 1 class đơn giản
public class UserResponse {
    private final Long id;
    private final String name;
    private final String email;

    public UserResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long id() { return id; }
    public String name() { return name; }
    public String email() { return email; }

    @Override
    public boolean equals(Object obj) { /* boilerplate */ }
    @Override
    public int hashCode() { /* boilerplate */ }
    @Override
    public String toString() { /* boilerplate */ }
}
```

**Sau (Java 16+):**
```java
// 1 dòng! Tự động có constructor, accessors, equals, hashCode, toString
public record UserResponse(Long id, String name, String email) {}

// Sử dụng
UserResponse user = new UserResponse(1L, "Tri", "tri@email.com");
System.out.println(user.name());   // "Tri"
System.out.println(user);          // UserResponse[id=1, name=Tri, email=tri@email.com]
```

**So sánh TypeScript:**
```
TypeScript:                                  Java Record:
type User = Readonly<{                       record User(
  id: number;                                    Long id,
  name: string;                                  String name,
  email: string;                                 String email
}>;                                          ) {}
```

**Dùng trong Spring Boot — DTO cho REST API:**
```java
// Request DTO với validation
public record UserRequest(
    @NotBlank String name,
    @Email String email
) {}

// Response DTO
public record UserResponse(Long id, String name, String email) {}

// Controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest req) {
        User saved = userService.create(req);
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail());
    }
}
```

**Record có thể có custom constructor để validate:**
```java
public record UserRequest(String name, String email) {
    public UserRequest {  // Compact constructor — chạy trước khi gán field
        Objects.requireNonNull(name, "name không được null");
        if (!email.contains("@")) throw new IllegalArgumentException("Email không hợp lệ");
    }
}
```

> **Tại sao quan trọng**: 90% code Spring Boot là DTO. Records giảm boilerplate cực kỳ nhiều, an toàn thread (immutable), và Jackson tự động serialize/deserialize.

---

### 2. Sealed Classes — Giới hạn kế thừa (Java 17)

**Vấn đề**: Trong Java cũ, bất kỳ ai cũng có thể extends class của bạn. Không kiểm soát được.

**Trước (Java 16):**
```java
// Ai cũng extend được — không kiểm soát
public class Payment { }
public class CreditCard extends Payment { }
public class PayPal extends Payment { }
// Ai đó thêm: public class WeirdPayment extends Payment { } — Oops!
```

**Sau (Java 17+):**
```java
// Sealed: chỉ cho phép 3 class này extends
public sealed class Payment
    permits CreditCard, PayPal, Bitcoin { }

public final class CreditCard extends Payment { String cardNumber; }
public final class PayPal extends Payment { String email; }
public final class Bitcoin extends Payment { String address; }

// Kết hợp với pattern matching switch (Java 21):
public String process(Payment p) {
    return switch (p) {
        case CreditCard cc -> "Charge card " + cc.cardNumber;
        case PayPal pp     -> "PayPal to " + pp.email;
        case Bitcoin bt    -> "Send BTC to " + bt.address;
        // Không cần default! Compiler biết chỉ có 3 loại.
        // Nếu thêm loại mới → compiler báo lỗi đến khi bạn handle.
    };
}
```

**So sánh TypeScript — Discriminated Union:**
```
TypeScript:                                  Java Sealed Class:
type Payment =                               sealed class Payment
  | { type: "card"; number: string }             permits CreditCard, PayPal, Bitcoin {}
  | { type: "paypal"; email: string }
  | { type: "btc"; address: string };        final class CreditCard extends Payment {}
                                             final class PayPal extends Payment {}
// Compiler check exhaustiveness             final class Bitcoin extends Payment {}
switch (p.type) {
    case "card": ...                         // Compiler check exhaustiveness
    case "paypal": ...                       switch (p) {
    case "btc": ...                              case CreditCard cc -> ...
}                                                case PayPal pp -> ...
                                                 case Bitcoin bt -> ...
                                             }
```

**Dùng trong Spring Boot — Domain Model:**
```java
// Sealed interface cho validation results
sealed interface ValidationResult
    permits Success, Failure, Warning {}

record Success(String message) implements ValidationResult {}
record Failure(List<String> errors) implements ValidationResult {}
record Warning(String warning) implements ValidationResult {}

// Service layer — exhaustive handling
@Service
public class UserValidationService {
    public ResponseEntity<?> validate(UserRequest req) {
        ValidationResult result = doValidate(req);
        return switch (result) {
            case Success(String msg) ->
                ResponseEntity.ok(Map.of("message", msg));
            case Failure(List<String> errors) ->
                ResponseEntity.badRequest().body(Map.of("errors", errors));
            case Warning(String warn) ->
                ResponseEntity.ok(Map.of("warning", warn));
        };
    }
}
```

> **Tại sao quan trọng**: Sealed classes + pattern matching = domain model an toàn. Compiler đảm bảo bạn xử lý hết mọi trường hợp. Thêm loại mới → bắt buộc update tất cả switch.

---

### 3. Pattern Matching — instanceof & switch (Java 16/21)

**instanceof (Java 16) — Tự động cast sau khi check kiểu:**

```java
// Trước: check xong phải cast thủ công
if (obj instanceof String) {
    String s = (String) obj;    // Cast thủ công — dài dòng
    System.out.println(s.toUpperCase());
}

// Sau: tự động tạo biến đã cast
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());  // s đã là String, không cần cast
}

// Kết hợp điều kiện
if (obj instanceof String s && s.length() > 5) {
    System.out.println("Chuỗi dài: " + s);
}
```

**switch (Java 21) — Switch theo kiểu dữ liệu + điều kiện:**

```java
// Trước: if-else chain dài dòng
public String classify(Object obj) {
    if (obj instanceof Integer i) return "Số: " + i;
    else if (obj instanceof String s) return "Chuỗi: " + s;
    else if (obj instanceof List<?> l) return "Danh sách: " + l.size();
    else return "Không biết";
}

// Sau: pattern matching switch — gọn và mạnh hơn
public String classify(Object obj) {
    return switch (obj) {
        case null                          -> "Null";
        case Integer i                     -> "Số: " + i;
        case String s when s.length() > 10 -> "Chuỗi dài: " + s;
        case String s                      -> "Chuỗi ngắn: " + s;
        case List<?> list                  -> "Danh sách: " + list.size();
        default                            -> "Không biết";
    };
}

// Destructure record trong switch!
record Point(int x, int y) {}
record Circle(Point center, int radius) {}

public String describe(Object shape) {
    return switch (shape) {
        case Point(int x, int y) ->
            "Điểm tại (%d, %d)".formatted(x, y);
        case Circle(Point(int cx, int cy), int r) ->
            "Hình tròn tâm (%d, %d) bán kính %d".formatted(cx, cy, r);
        default -> "Hình không xác định";
    };
}
```

**So sánh TypeScript — Type Guards + Narrowing:**
```
TypeScript:                                  Java 21:
if (typeof obj === "string") {               if (obj instanceof String s) {
    obj.toUpperCase(); // narrowed               s.toUpperCase(); // pattern var
}                                            }

// Switch với narrowing                      // Pattern matching switch
switch (result.type) {                       switch (result) {
    case "ok": result.data; break;               case Success(var msg) -> msg;
    case "err": result.error; break;             case Failure(var errs) -> errs;
}                                            }
```

> **Tại sao quan trọng**: Giảm 50% if-else chains trong service layer. Code dễ đọc hơn, compiler kiểm tra exhaustiveness, và destructure record trực tiếp.

---

### 4. Text Blocks — Chuỗi nhiều dòng (Java 15)

**Trước:**
```java
String json = "{\n" +
    "  \"name\": \"Tri\",\n" +
    "  \"email\": \"tri@email.com\"\n" +
    "}";

String sql = "SELECT id, name FROM users\n" +
    "WHERE status = 'ACTIVE'\n" +
    "ORDER BY created_at DESC";
```

**Sau:**
```java
String json = """
    {
      "name": "Tri",
      "email": "tri@email.com"
    }
    """;

String sql = """
    SELECT id, name FROM users
    WHERE status = 'ACTIVE'
    ORDER BY created_at DESC
    """;

// Kết hợp formatted() để chèn biến
String response = """
    {
      "status": "ok",
      "user": "%s",
      "timestamp": "%s"
    }
    """.formatted(userName, Instant.now());
```

**So sánh TypeScript:** Giống hệt template literals với backticks, nhưng Java dùng `"""`.

> **Tại sao quan trọng**: SQL queries, JSON fixtures, HTML templates đọc dễ hơn rất nhiều trong source code.

---

### 5. Virtual Threads — Triệu threads nhẹ (Java 21)

**Đây là tính năng game-changing nhất** cho backend developer.

**Vấn đề**: Java cũ dùng platform threads (1 thread = 1 OS thread, ~1MB RAM mỗi thread). 10.000 concurrent requests = 10GB RAM chỉ cho thread stacks.

**Giải pháp**: Virtual threads — mỗi thread chỉ tốn vài KB, JVM quản lý hàng triệu threads.

```java
// Bật virtual threads trong Spring Boot 3.2+ — CHỈ CẦN 1 DÒNG CONFIG:
// application.properties:
spring.threads.virtual.enabled=true

// Code KHÔNG CẦN THAY ĐỔI! Vẫn viết blocking code bình thường:
@RestController
public class UserController {
    @GetMapping("/users/{id}/profile")
    public UserProfile getProfile(@PathVariable Long id) {
        // Mỗi request chạy trên virtual thread
        // 10.000 concurrent requests → vẫn nhẹ nhàng
        User user = userService.findById(id);           // Blocking I/O — OK!
        List<Post> posts = postService.findByUser(id);  // Blocking I/O — OK!
        return new UserProfile(user, posts);
    }
}
```

**So sánh hiệu năng:**

| | Platform Threads (Java cũ) | Virtual Threads (Java 21) |
|---|---|---|
| Max concurrent requests | ~10.000 | 100.000+ |
| RAM cho thread stacks | ~10 GB | ~100 MB |
| Code style | Cần async/CompletableFuture | **Blocking code bình thường** |
| Phức tạp | Callback hell | **Đơn giản** |

**So sánh TypeScript:**
```
TypeScript (async/await):                    Java 21 (virtual threads):
async function getProfile(id) {              public UserProfile getProfile(Long id) {
    const user = await getUser(id);              User user = userService.findById(id);
    const posts = await getPosts(id);            List<Post> posts = postService.findByUser(id);
    return { user, posts };                      return new UserProfile(user, posts);
}                                            }
// Non-blocking nhờ event loop               // Non-blocking nhờ virtual threads
// Cần async/await keywords                  // KHÔNG cần async/await!
```

> **Tại sao quan trọng**: Virtual threads cho phép viết code đồng bộ đơn giản mà vẫn handle được hàng trăm ngàn concurrent requests. Không cần học reactive programming phức tạp.

**Lưu ý**: Tránh dùng `synchronized` blocks với virtual threads — sẽ "pin" carrier thread. Dùng `ReentrantLock` thay thế.

---

### 6. Sequenced Collections — API thống nhất (Java 21)

**Vấn đề**: Java cũ có cách lấy phần tử đầu/cuối khác nhau cho mỗi loại collection.

```java
// Trước: mỗi collection có API khác nhau — khó nhớ!
list.get(0);               // List: lấy đầu
list.get(list.size() - 1); // List: lấy cuối
deque.getFirst();           // Deque: lấy đầu — tên khác!
sorted.first();             // TreeSet: lại tên khác nữa!

// Sau (Java 21): API thống nhất cho tất cả!
list.getFirst();            // Lấy phần tử đầu
list.getLast();             // Lấy phần tử cuối
list.addFirst(element);     // Thêm vào đầu
list.addLast(element);      // Thêm vào cuối
list.removeFirst();         // Xóa đầu
list.removeLast();          // Xóa cuối
list.reversed();            // Trả về view đảo ngược
```

**So sánh TypeScript:**
```
TypeScript:                     Java 21:
arr[0]                          list.getFirst()
arr[arr.length - 1]            list.getLast()
arr.at(-1)                      list.getLast()
arr.reverse()                   list.reversed()
arr.unshift(x)                  list.addFirst(x)
arr.push(x)                     list.addLast(x)
```

---

### 7. Unnamed Variables — Bỏ qua biến không dùng (Java 22)

**Trước:**
```java
// Phải đặt tên biến dù không dùng
try {
    parseJson(input);
} catch (JsonException ex) {    // ex không dùng nhưng phải khai báo
    return defaultValue;
}

map.forEach((key, value) -> {   // key không dùng
    System.out.println(value);
});
```

**Sau (Java 22+):**
```java
// Dùng _ cho biến không cần
try {
    parseJson(input);
} catch (JsonException _) {     // Rõ ràng: không dùng exception
    return defaultValue;
}

map.forEach((_, value) -> {     // Rõ ràng: không dùng key
    System.out.println(value);
});

// Trong pattern matching
if (obj instanceof Point(int x, _)) {  // Chỉ cần x, bỏ y
    System.out.println("x = " + x);
}
```

**So sánh TypeScript:** Giống convention `_` trong TypeScript/JavaScript cho unused params.

---

### 8. ~~String Templates~~ — ĐÃ BỊ RÚT (Java 21-22 preview → removed Java 23)

> **⚠️ Lưu ý**: `STR."..."` processor đã được giới thiệu trong Java 21 (preview) và Java 22 (2nd preview), nhưng **đã bị rút hoàn toàn khỏi Java 23**. JEP bị withdrawn do thiết kế chưa đạt yêu cầu. **Không nên học tính năng này.**

**Thay thế hiện tại — dùng `formatted()` hoặc `String.format()`:**

```java
// Cách 1: String.formatted() (Java 15+) — khuyên dùng
String msg = "User %s có %d tuổi".formatted(name, age);

// Cách 2: Text block + formatted
String report = """
    Báo cáo User
    ============
    Tổng users: %d
    Active: %d
    Cập nhật: %s
    """.formatted(users.size(), activeCount, Instant.now());

// Cách 3: String concatenation (đơn giản)
String msg = "User " + name + " có " + age + " tuổi";
```

**So sánh TypeScript:**
```
TypeScript: `User ${name} có ${age} tuổi`
Java:       "User %s có %d tuổi".formatted(name, age)   // Dài hơn nhưng type-safe
```

---

### Khi nào học các tính năng này?

| Tính năng | Học ở tháng nào | Mức độ ưu tiên |
|-----------|----------------|----------------|
| **Records** | Tháng 3-4 (khi học DTO) | **Bắt buộc** — dùng hàng ngày |
| **Text Blocks** | Tháng 3 (khi viết SQL) | **Bắt buộc** — dùng hàng ngày |
| **Pattern matching instanceof** | Tháng 2 (khi học OOP) | **Cao** — rất hữu ích |
| **Virtual Threads** | Tháng 4 (khi setup Spring Boot) | **Cao** — chỉ cần 1 dòng config |
| **Sealed Classes** | Tháng 4 (khi thiết kế domain) | **Trung bình** — dùng cho domain phức tạp |
| **Pattern matching switch** | Tháng 4 (khi viết service) | **Trung bình** — kết hợp sealed classes |
| **Sequenced Collections** | Tháng 2 (khi học Collections) | **Thấp** — tiện nhưng không bắt buộc |
| **Unnamed Variables** | Bất kỳ lúc nào | **Thấp** — syntactic sugar |
| ~~**String Templates**~~ | Không học — đã bị rút | **Bỏ qua** — removed khỏi Java 23, dùng `formatted()` |

---

## Tổng kết lộ trình

```
Tháng 1          Tháng 2          Tháng 3          Tháng 4          Tháng 5          Tháng 6
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Java     │    │ OOP &    │    │ SQL &    │    │ Spring Boot  │  │ Testing &    │  │ Full-Stack   │
│ Cơ Bản   │───►│ Collect- │───►│ Database │───►│ REST API     │─►│ Security     │─►│ Integration  │
│          │    │ ions     │    │ JPA      │    │              │  │              │  │ Docker/Deploy│
└──────────┘    └──────────┘    └──────────┘    └──────────────┘  └──────────────┘  └──────────────┘
  Syntax          Classes        PostgreSQL       Controller        JUnit            React + Spring
  Variables       Interfaces     SQL Queries      Service           Mockito          Docker Compose
  Methods         Streams        Hibernate        Repository        JWT Auth         CI/CD
  Exceptions      Lambdas        JPA Entities     Validation        Spring Security  Cloud Deploy
```

**Sau 6 tháng, bạn có thể:**
- Build REST API hoàn chỉnh với Spring Boot
- Kết nối React/Next.js frontend với Spring Boot backend
- Viết tests (unit + integration)
- Bảo mật API với JWT + Spring Security
- Deploy full-stack app bằng Docker
- Tự tin nhận role Fullstack Developer

---

**Lời cuối**: Hành trình 6 tháng này không dễ, nhưng mỗi ngày bạn code là mỗi ngày bạn tiến gần hơn đến mục tiêu. Đừng sợ sai — bạn đã làm chủ frontend rồi, backend chỉ là nửa còn lại của bức tranh. Let's go! 🚀
