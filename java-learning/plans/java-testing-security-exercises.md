# BÀI TẬP THÁNG 5: TESTING & SECURITY

**Tương ứng**: Tháng 5 trong lộ trình (Tuần 19-22)
**Mục tiêu**: Viết unit/integration tests, thêm JWT authentication
**Quy ước**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Phần A: Testing với JUnit 5 & Mockito (12 bài)

### A1. JUnit 5 cơ bản ⭐

Viết tests cho class `Calculator`:

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int subtract(int a, int b) { return a - b; }
    public int multiply(int a, int b) { return a * b; }
    public double divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Không thể chia cho 0");
        return (double) a / b;
    }
}
```

```java
class CalculatorTest {

    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    @Test
    void shouldAddTwoNumbers() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void shouldThrowWhenDivideByZero() {
        assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    }

    // Bài tập: viết thêm tests cho subtract, multiply, divide
    // Dùng: assertEquals, assertNotEquals, assertTrue, assertFalse, assertNull, assertThrows
}
```

**So sánh Jest:**
```
Jest:                                    JUnit 5:
describe('Calculator', () => { })        class CalculatorTest { }
beforeEach(() => { })                    @BeforeEach void setUp() { }
test('adds', () => { })                  @Test void shouldAdd() { }
expect(result).toBe(5)                   assertEquals(5, result)
expect(() => fn()).toThrow()             assertThrows(Exception.class, () -> fn())
```

---

### A2. Assertions nâng cao ⭐

Viết tests dùng các assertion phong phú hơn:

```java
@Test
void assertionExamples() {
    // Bài tập: viết test cho mỗi assertion

    // 1. assertAll — kiểm tra nhiều điều kiện cùng lúc (không dừng ở lỗi đầu)
    assertAll("user",
        () -> assertEquals("Trí", user.getName()),
        () -> assertEquals("tri@co.vn", user.getEmail()),
        () -> assertTrue(user.isActive())
    );

    // 2. assertIterableEquals — so sánh list
    assertIterableEquals(List.of(1, 2, 3), actualList);

    // 3. assertTimeout — đảm bảo chạy trong thời gian
    assertTimeout(Duration.ofMillis(100), () -> service.fastMethod());

    // 4. assertDoesNotThrow
    assertDoesNotThrow(() -> service.safeMethod());
}
```

---

### A3. @ParameterizedTest — Test nhiều case cùng lúc ⭐⭐

```java
// Thay vì viết 10 test methods riêng biệt:
@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "0, 0, 0",
    "-1, 1, 0",
    "100, 200, 300",
    "Integer.MAX_VALUE, 1, overflow"   // Edge case
})
void shouldAddNumbers(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

@ParameterizedTest
@ValueSource(strings = {"", " ", "  "})
void shouldRejectBlankNames(String name) {
    assertThrows(IllegalArgumentException.class, () -> new User(name, "email@co.vn"));
}

@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "\t", "\n"})
void shouldRejectInvalidEmails(String email) {
    assertThrows(IllegalArgumentException.class, () -> new User("Trí", email));
}
```

Bài tập: Viết parameterized tests cho email validation, password strength checker, age validation.

---

### A4. Mockito — Mock dependencies ⭐⭐

Test `ProductService` mà KHÔNG cần database:

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {
        // Arrange: giả lập repository trả về data
        List<Product> mockProducts = List.of(
            new Product(1L, "iPhone", new BigDecimal("25000000")),
            new Product(2L, "MacBook", new BigDecimal("55000000"))
        );
        when(productRepository.findAll()).thenReturn(mockProducts);

        // Act: gọi service
        List<ProductResponse> result = productService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("iPhone", result.get(0).name());

        // Verify: đảm bảo repository được gọi đúng 1 lần
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
    }

    // Bài tập: viết thêm tests cho create, update, delete
}
```

**So sánh Jest:**
```
Jest:                                    Mockito:
jest.fn()                                @Mock Repository repo
mockFn.mockReturnValue(data)             when(repo.findAll()).thenReturn(data)
expect(mockFn).toHaveBeenCalled()        verify(repo).findAll()
expect(mockFn).toHaveBeenCalledTimes(1)  verify(repo, times(1)).findAll()
```

---

### A5. @WebMvcTest — Test Controller layer ⭐⭐⭐

Test HTTP endpoints mà không cần start full app:

> **⚠️ Spring Boot 3.4+**: `@MockBean` và `@SpyBean` đã deprecated. Dùng `@MockitoBean` và `@MockitoSpyBean` thay thế.

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean    // Spring Boot 3.4+: thay thế @MockBean (deprecated)
    private ProductService productService;

    @Test
    void shouldReturnProducts() throws Exception {
        // Arrange
        when(productService.findAll()).thenReturn(List.of(
            new ProductResponse(1L, "iPhone", new BigDecimal("25000000"), null, null, null)
        ));

        // Act & Assert
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("iPhone"))
            .andExpect(jsonPath("$[0].price").value(25000000));
    }

    @Test
    void shouldReturn201WhenCreated() throws Exception {
        ProductRequest request = new ProductRequest("iPad", new BigDecimal("20000000"), null, null);

        when(productService.create(any())).thenReturn(
            new ProductResponse(1L, "iPad", new BigDecimal("20000000"), null, null, null)
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "iPad", "price": 20000000}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("iPad"));
    }

    @Test
    void shouldReturn400WhenNameBlank() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "", "price": 20000000}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(productService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/products/99"))
            .andExpect(status().isNotFound());
    }
}
```

Bài tập: Viết tests đầy đủ cho tất cả endpoints (GET, POST, PUT, DELETE) + edge cases.

---

### A6. @DataJpaTest — Test Repository layer ⭐⭐

```java
@DataJpaTest    // Chỉ load JPA components, dùng H2 in-memory DB
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persist(new Product("iPhone", new BigDecimal("25000000")));
        em.persist(new Product("MacBook", new BigDecimal("55000000")));
        em.persist(new Product("AirPods", new BigDecimal("5000000")));
        em.flush();
    }

    @Test
    void shouldFindByPriceRange() {
        List<Product> result = repo.findByPriceBetween(
            new BigDecimal("10000000"), new BigDecimal("30000000"));
        assertEquals(1, result.size());
        assertEquals("iPhone", result.get(0).getName());
    }

    @Test
    void shouldFindByNameContaining() {
        List<Product> result = repo.findByNameContainingIgnoreCase("mac");
        assertEquals(1, result.size());
    }
}
```

---

### A7-A12: Thêm 6 bài Testing

**A7** ⭐⭐ — **@SpringBootTest**: Full integration test — start toàn bộ app, gọi API thật bằng `TestRestTemplate`.

**A8** ⭐⭐ — **Test Lifecycle**: `@BeforeAll`, `@AfterAll`, `@BeforeEach`, `@AfterEach`, `@Nested`, `@DisplayName`, `@Disabled`.

**A9** ⭐⭐⭐ — **Testcontainers**: Test với PostgreSQL thật (Docker container) thay vì H2. Cấu hình `@Container` và `@ServiceConnection` (Spring Boot 3.1+).

**A10** ⭐⭐ — **Test Coverage**: Chạy `mvn test jacoco:report`, mở báo cáo HTML, đạt mục tiêu 70%+ trên service layer.

**A11** ⭐⭐⭐ — **BDD Style**: Viết tests theo Behavior-Driven Development: `given_when_then` naming convention.

**A12** ⭐⭐⭐ — **Test tất cả layers**: Viết test suite đầy đủ cho Task Management API — Controller + Service + Repository. Mỗi layer ít nhất 5 tests.

**A13** ⭐⭐ — **@RestClientTest**: Test HTTP client (RestClient / @HttpExchange) mà không cần external server:

```java
@RestClientTest(ExternalApiService.class)
class ExternalApiServiceTest {

    @Autowired
    private ExternalApiService service;

    @Autowired
    private MockRestServiceServer server;  // Mock external server

    @Test
    void shouldFetchPosts() {
        server.expect(requestTo("/posts"))
            .andRespond(withSuccess("""
                [{"id": 1, "title": "Test Post"}]
                """, MediaType.APPLICATION_JSON));

        List<Post> posts = service.getPosts();
        assertEquals(1, posts.size());
        assertEquals("Test Post", posts.get(0).title());
    }
}
```

---

## Phần B: Spring Security & JWT (8 bài)

### B1. Spring Security cơ bản ⭐⭐

Thêm dependency `spring-boot-starter-security`. Khi thêm → TẤT CẢ endpoints tự động yêu cầu login.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Tắt CSRF cho API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()   // Không cần auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()                  // Còn lại phải auth
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Bài tập: Cấu hình xong → test:
- `GET /api/public/hello` → 200 (không cần token)
- `GET /api/products` → 401 Unauthorized

---

### B2. User Entity & Registration ⭐⭐

```java
@Entity
@Table(name = "users")
public class AppUser {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // BCrypt hashed

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // ...
}

public enum Role { USER, ADMIN }
```

Implement `POST /api/auth/register`:
- Nhận email + password
- Hash password bằng BCrypt
- Lưu vào DB
- Trả về user info (KHÔNG trả password!)

---

### B3. JWT — Tạo & Verify Token ⭐⭐⭐

Tạo `JwtService`:

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;  // milliseconds

    public String generateToken(String email) {
        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
```

Bài tập: Viết unit tests cho `generateToken`, `extractEmail`, `isTokenValid`, `isTokenExpired`.

---

### B4. Login Endpoint ⭐⭐⭐

Implement `POST /api/auth/login`:

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Tìm user theo email
        // 2. So sánh password (BCrypt matches)
        // 3. Tạo JWT token
        // 4. Trả về token

        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }
}

record LoginRequest(@Email String email, @NotBlank String password) {}
record AuthResponse(String token, String type) {}
```

Test flow:
1. Register → 201
2. Login với password đúng → 200 + JWT
3. Login với password sai → 401

---

### B5. JWT Filter — Protect Endpoints ⭐⭐⭐

Tạo `JwtAuthenticationFilter` kiểm tra token mỗi request:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // Set authentication vào SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

Test flow đầy đủ:
1. `GET /api/products` không có header → 401
2. `GET /api/products` với `Authorization: Bearer <valid-jwt>` → 200
3. `GET /api/products` với token hết hạn → 401

---

### B6. Role-Based Access Control ⭐⭐⭐

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() { ... }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) { ... }
}
```

Test:
1. User role USER → `GET /api/admin/users` → 403 Forbidden
2. User role ADMIN → `GET /api/admin/users` → 200

---

### B7. Lấy Current User ⭐⭐

```java
@GetMapping("/me")
public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    return userService.findByEmail(email);
}

// Hoặc tạo custom annotation:
@GetMapping("/my-tasks")
public List<TaskResponse> getMyTasks(@CurrentUser AppUser user) {
    return taskService.findByUserId(user.getId());
}
```

---

### B8. Refresh Token — Production Auth Flow ⭐⭐⭐

Access token ngắn hạn (15 phút) + Refresh token dài hạn (7 ngày):

```java
// Entity lưu refresh token
@Entity
public class RefreshToken {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String token;
    @ManyToOne
    private AppUser user;
    private Instant expiryDate;  // 7 ngày

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}

// Login trả về cả access + refresh token
record AuthResponse(String accessToken, String refreshToken, String type, long expiresIn) {}

// Endpoint refresh token
@PostMapping("/auth/refresh")
public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
    RefreshToken stored = refreshTokenRepo.findByToken(request.refreshToken())
        .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

    if (stored.isExpired()) {
        refreshTokenRepo.delete(stored);
        throw new InvalidTokenException("Refresh token expired — login lại");
    }

    // Tạo access token mới, giữ refresh token cũ (hoặc rotate)
    String newAccessToken = jwtService.generateToken(stored.getUser().getEmail());
    return ResponseEntity.ok(new AuthResponse(newAccessToken, stored.getToken(), "Bearer", 900));
}
```

**Flow:**
```
1. Login → accessToken (15min) + refreshToken (7 days)
2. API calls dùng accessToken
3. accessToken hết hạn → gọi /auth/refresh với refreshToken → accessToken mới
4. refreshToken hết hạn → phải login lại
```

Bài tập:
1. Implement RefreshToken entity + repository
2. Login trả cả access + refresh token
3. Endpoint `POST /auth/refresh` tạo access token mới
4. Endpoint `POST /auth/logout` xóa refresh token
5. Test: access token hết hạn → refresh → tiếp tục dùng API

---

### B9. OAuth2 Resource Server — JWT Validation chuẩn Spring ⭐⭐⭐

Thay vì tự viết `JwtAuthenticationFilter`, dùng `spring-boot-starter-oauth2-resource-server` — Spring tự validate JWT:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            // Thay JwtAuthenticationFilter tự viết → Spring tự handle
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")
        ).build();
    }
}
```

> **Tại sao dùng?** Tự viết JWT filter dễ bỏ sót edge cases (token expired, malformed, wrong algorithm). `oauth2-resource-server` handle hết, production-tested.

Bài tập:
1. Refactor từ custom `JwtAuthenticationFilter` (B5) sang `oauth2-resource-server`
2. So sánh code lượng trước/sau — gọn hơn bao nhiêu?
3. Test: token hợp lệ → 200, token sai → 401, token expired → 401

---

### B10. OAuth2 Login — Social Login (Google/GitHub) ⭐⭐⭐

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email
```

Bài tập:
1. Đăng ký OAuth2 app trên Google Cloud Console
2. Cấu hình `spring-boot-starter-oauth2-client`
3. Login bằng Google → tự tạo user trong DB nếu chưa có
4. Merge account: user đã register bằng email → link Google account

---

### B11. Capstone — Secure Task Management API ⭐⭐⭐

Áp dụng tất cả vào Task Management API:
1. Register/Login endpoints (access + refresh tokens)
2. JWT authentication (oauth2-resource-server)
3. Users chỉ thấy tasks của mình
4. Admin thấy tất cả tasks
5. CORS cho React frontend
6. (Optional) OAuth2 social login
7. Viết integration tests cho toàn bộ auth flow

---

## Checklist theo dõi

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — Testing (JUnit + Mockito) | 13 bài | ☐ |
| B — Security (JWT + Spring Security + OAuth2) | 11 bài | ☐ |
| **Tổng** | **24 bài** | |
