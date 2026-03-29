# BÀI TẬP THÁNG 4: SPRING BOOT & REST API

**Tương ứng**: Tháng 4 trong lộ trình (Tuần 13-18)
**Mục tiêu**: Build REST API hoàn chỉnh với Spring Boot — kiến trúc 3 lớp
**Quy ước**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Phần A: Spring Core & Dependency Injection (8 bài)

### A1. Hello Spring Boot ⭐

1. Tạo project tại start.spring.io (dependencies: Spring Web, Spring Data JPA, PostgreSQL, Lombok, Validation)
2. Tạo endpoint `GET /api/hello` trả về `{"message": "Xin chào Spring Boot!"}`
3. Chạy app, test bằng browser hoặc curl

```java
@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Xin chào Spring Boot!");
    }
}
```

---

### A2. Dependency Injection — 3 kiểu ⭐⭐

Tạo `GreetingService` và inject vào `GreetingController` bằng 3 cách. Giải thích cách nào nên dùng.

```java
// 1. Field injection (KHÔNG khuyến khích)
@RestController
public class Controller1 {
    @Autowired
    private GreetingService service;
}

// 2. Setter injection
@RestController
public class Controller2 {
    private GreetingService service;

    @Autowired
    public void setService(GreetingService service) {
        this.service = service;
    }
}

// 3. Constructor injection (KHUYẾN KHÍCH — mặc định trong Spring Boot)
@RestController
public class Controller3 {
    private final GreetingService service;

    public Controller3(GreetingService service) {  // Không cần @Autowired nếu chỉ 1 constructor
        this.service = service;
    }
}
```

Bài tập: Tạo interface `NotificationService` với 2 implementation: `EmailNotification`, `SmsNotification`. Dùng `@Primary` hoặc `@Qualifier` để chọn.

---

### A3. Bean Scope & Lifecycle ⭐⭐

Tạo 2 beans với scope khác nhau, in ra để so sánh:

```java
@Component
@Scope("singleton")    // Mặc định — chỉ 1 instance
public class SingletonBean {
    public SingletonBean() { System.out.println("Singleton created: " + this); }
}

@Component
@Scope("prototype")    // Mỗi lần inject → instance mới
public class PrototypeBean {
    public PrototypeBean() { System.out.println("Prototype created: " + this); }
}
```

Inject cả hai vào 1 controller, gọi endpoint 3 lần → kiểm tra address có giống không.

---

### A4. application.properties ⭐

Cấu hình project qua properties:

```properties
# application.properties
server.port=8080
spring.application.name=task-manager

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=app
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Custom properties
app.name=Task Manager
app.version=1.0
app.max-tasks-per-user=50
```

Bài tập: Đọc custom properties bằng `@Value` và `@ConfigurationProperties`:

```java
@RestController
public class AppInfoController {
    @Value("${app.name}")
    private String appName;

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of("app", appName, "version", appVersion);
    }
}
```

---

### A5. Profiles — Dev vs Prod ⭐⭐

Tạo `application-dev.properties` và `application-prod.properties` riêng.

```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:devdb
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.root=DEBUG

# application-prod.properties
spring.datasource.url=jdbc:postgresql://prod-server:5432/proddb
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
```

Chạy với: `java -jar app.jar --spring.profiles.active=dev`

---

### A6-A8: Thêm 3 bài Spring Core

**A6** ⭐⭐ — **@PostConstruct & @PreDestroy**: Tạo bean với init/cleanup logic. Log lifecycle events.

**A7** ⭐⭐⭐ — **Custom Auto-Configuration**: Tạo `@ConditionalOnProperty` bean — chỉ tạo nếu property bật.

**A8** ⭐⭐ — **Event System**: Dùng `ApplicationEventPublisher` publish event khi user đăng ký. Listener log event.

---

## Phần B: REST API — CRUD (12 bài)

### B1. Controller đầu tiên — Product CRUD ⭐⭐

Tạo đầy đủ CRUD cho Product:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products
    @GetMapping
    public List<ProductResponse> getAll() { ... }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) { ... }

    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) { ... }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { ... }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
}
```

Bài tập: Implement tất cả, test bằng Postman/curl.

---

### B2. Service Layer ⭐⭐

Tách business logic ra `ProductService`:

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getDescription());
    }
}
```

---

### B3. DTO Pattern — Request/Response ⭐⭐

Tạo DTOs tách biệt khỏi Entity:

```java
// Request DTO — input từ client
public record ProductRequest(
    @NotBlank(message = "Tên không được trống")
    @Size(min = 2, max = 200, message = "Tên phải từ 2-200 ký tự")
    String name,

    @NotNull(message = "Giá không được null")
    @Positive(message = "Giá phải > 0")
    BigDecimal price,

    @Size(max = 1000)
    String description,

    Long categoryId
) {}

// Response DTO — output cho client
public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    String description,
    String categoryName,
    LocalDateTime createdAt
) {}
```

**Tại sao cần DTO?**
- Entity có thể chứa fields nhạy cảm (password, internal IDs)
- Request và response thường khác cấu trúc entity
- Validation chỉ cần trên Request DTO
- Tránh expose database structure cho client

---

### B4. Validation ⭐⭐

Thêm validation đầy đủ và test các trường hợp lỗi:

```java
// Request
public record UserRequest(
    @NotBlank String name,
    @Email String email,
    @Size(min = 8, max = 50) String password,
    @Min(0) @Max(150) Integer age,
    @Pattern(regexp = "^0\\d{9}$", message = "SĐT phải bắt đầu bằng 0, đủ 10 số")
    String phone
) {}

// Controller
@PostMapping
public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
    // Nếu validation fail → Spring tự trả 400 Bad Request
    return ResponseEntity.status(201).body(userService.create(request));
}
```

Test cases:
1. POST với name trống → 400
2. POST với email sai format → 400
3. POST với age = -1 → 400
4. POST hợp lệ → 201

---

### B5. Error Handling — @ControllerAdvice ⭐⭐⭐

Tạo global exception handler:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(
            new ErrorResponse(404, ex.getMessage(), LocalDateTime.now())
        );
    }

    // 400: Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        return ResponseEntity.badRequest().body(
            new ErrorResponse(400, "Validation failed", errors, LocalDateTime.now())
        );
    }

    // 500: Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(
            new ErrorResponse(500, "Internal server error", LocalDateTime.now())
        );
    }
}

record ErrorResponse(int status, String message, Object errors, LocalDateTime timestamp) {
    ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, null, timestamp);
    }
}
```

---

### B6. ResponseEntity — HTTP Status Codes ⭐⭐

Viết controller trả đúng status code cho mỗi operation:

```java
// 200 OK — GET thành công
// 201 Created — POST thành công
// 204 No Content — DELETE thành công
// 400 Bad Request — Validation lỗi
// 404 Not Found — Resource không tồn tại
// 409 Conflict — Duplicate (email đã tồn tại)

@PostMapping
public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest req) {
    ProductResponse created = service.create(req);
    URI location = URI.create("/api/products/" + created.id());
    return ResponseEntity.created(location).body(created);  // 201 + Location header
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();  // 204
}
```

---

### B7. Pagination & Filtering ⭐⭐

```java
// GET /api/products?page=0&size=10&sort=price,desc&category=electronics&minPrice=1000000

@GetMapping
public Page<ProductResponse> search(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(required = false) BigDecimal maxPrice,
    @RequestParam(required = false) String keyword,
    Pageable pageable  // Spring tự parse page, size, sort từ query params
) {
    return productService.search(category, minPrice, maxPrice, keyword, pageable);
}
```

---

### B8. @RequestParam vs @PathVariable vs @RequestBody ⭐

Viết endpoints minh hoạ rõ sự khác nhau:

```java
// @PathVariable: lấy từ URL path
// GET /api/users/123
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) { ... }

// @RequestParam: lấy từ query string
// GET /api/users?name=Tri&age=25
@GetMapping("/users")
public List<User> search(@RequestParam String name, @RequestParam(defaultValue = "0") int age) { ... }

// @RequestBody: lấy từ JSON body
// POST /api/users  body: {"name": "Tri", "email": "tri@co.vn"}
@PostMapping("/users")
public User create(@RequestBody UserRequest request) { ... }
```

**So sánh Next.js:**
```typescript
// Next.js                              Spring Boot
// params.id (dynamic route)            @PathVariable
// searchParams.get("name")             @RequestParam
// await req.json()                     @RequestBody
```

---

### B9. CORS Configuration ⭐⭐

Cấu hình cho React frontend (localhost:3000) gọi được Spring Boot API (localhost:8080):

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

Test: tạo trang HTML đơn giản fetch API từ origin khác → xác nhận CORS hoạt động.

---

### B10. RestClient — HTTP Client hiện đại (Spring Boot 3.2+) ⭐⭐

`RestClient` thay thế `RestTemplate` (synchronous) — API fluent, dễ đọc hơn:

```java
@Service
public class ExternalApiService {

    private final RestClient restClient;

    public ExternalApiService(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("https://jsonplaceholder.typicode.com")
            .defaultHeader("Accept", "application/json")
            .build();
    }

    // GET
    public List<Post> getPosts() {
        return restClient.get()
            .uri("/posts")
            .retrieve()
            .body(new ParameterizedTypeReference<List<Post>>() {});
    }

    // POST
    public Post createPost(PostRequest request) {
        return restClient.post()
            .uri("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(Post.class);
    }

    // Error handling
    public Post getPost(Long id) {
        return restClient.get()
            .uri("/posts/{id}", id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (req, res) -> { throw new ResourceNotFoundException("Post not found: " + id); })
            .body(Post.class);
    }
}
```

**So sánh:**
```
RestTemplate (cũ):  restTemplate.getForObject(url, Post.class)    // ít control
WebClient (reactive): webClient.get().uri(url).retrieve().bodyToMono()  // reactive
RestClient (mới):   restClient.get().uri(url).retrieve().body()    // fluent + sync ✓
```

Bài tập: Tạo service gọi external API (JSONPlaceholder), dùng `RestClient` cho GET/POST/PUT/DELETE.

---

### B11. OpenAPI / Swagger Documentation ⭐⭐

Thêm dependency `springdoc-openapi-starter-webmvc-ui`, tự động sinh API docs:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

```java
// Swagger UI tự động có tại: http://localhost:8080/swagger-ui.html
// OpenAPI JSON tại: http://localhost:8080/v3/api-docs

// Thêm annotations để enrich docs:
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    @Operation(summary = "Lấy tất cả products", description = "Hỗ trợ pagination và filtering")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @GetMapping
    public Page<ProductResponse> getAll(Pageable pageable) { ... }

    @Operation(summary = "Tạo product mới")
    @ApiResponse(responseCode = "201", description = "Tạo thành công")
    @ApiResponse(responseCode = "400", description = "Validation lỗi")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest req) { ... }
}
```

Bài tập:
1. Thêm springdoc, mở Swagger UI, test API trực tiếp từ browser
2. Thêm `@Operation`, `@ApiResponse`, `@Tag` cho tất cả endpoints
3. Cấu hình security scheme cho JWT trong Swagger

---

### B12. Caching — Tăng performance với @Cacheable ⭐⭐

```java
// 1. Thêm dependency
// spring-boot-starter-cache (có sẵn trong spring-boot-starter-web)

// 2. Bật caching
@SpringBootApplication
@EnableCaching
public class Application { }

// 3. Dùng annotations
@Service
public class ProductService {

    @Cacheable(value = "products", key = "#id")  // Cache kết quả theo id
    public ProductResponse findById(Long id) {
        log.info("Querying database for product {}", id);  // Chỉ log lần đầu
        return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
    }

    @CacheEvict(value = "products", key = "#id")  // Xóa cache khi update
    public ProductResponse update(Long id, ProductRequest request) { ... }

    @CacheEvict(value = "products", allEntries = true)  // Xóa hết cache
    public void delete(Long id) { ... }
}
```

Bài tập:
1. Bật caching, gọi `GET /api/products/1` 3 lần → confirm chỉ query DB 1 lần
2. Update product → confirm cache bị evict → query DB lại
3. Cấu hình cache TTL (time-to-live) trong `application.properties`

---

### B13. Problem Details — Chuẩn Error Response RFC 9457 ⭐⭐

Spring Boot 3.x hỗ trợ sẵn chuẩn Problem Details cho error responses:

```properties
# application.properties — bật Problem Details
spring.mvc.problemdetails.enabled=true
```

```java
// Trả về chuẩn RFC 9457 thay vì custom ErrorResponse:
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}

// Response format chuẩn:
// {
//   "type": "about:blank",
//   "title": "Resource Not Found",
//   "status": 404,
//   "detail": "Product not found: 99",
//   "instance": "/api/products/99",
//   "timestamp": "2026-03-29T10:00:00Z"
// }
```

Bài tập: Refactor `GlobalExceptionHandler` từ B5 sang dùng `ProblemDetail`. So sánh response format cũ vs mới.

---

### B14-B16: 3 bài tổng hợp

**B14** ⭐⭐⭐ — **File Upload**: Endpoint `POST /api/files/upload` nhận multipart file, lưu vào disk, trả URL. Giới hạn 5MB, chỉ jpg/png.

**B15** ⭐⭐⭐ — **API Versioning**: Implement 2 version API (`/api/v1/products`, `/api/v2/products`) với response format khác nhau.

**B16** ⭐⭐⭐ — **Mini Project: Task Management API**: Build đầy đủ API từ plan Tháng 4 — tất cả endpoints, validation, error handling, pagination, CORS, OpenAPI docs, caching, RestClient gọi external service. Đây là capstone project của Tháng 4.

---

## Checklist theo dõi

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — Spring Core & DI | 8 bài | ☐ |
| B — REST API CRUD + Modern Features | 16 bài | ☐ |
| **Tổng** | **24 bài** | |
