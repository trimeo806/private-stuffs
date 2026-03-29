# BÀI TẬP THÁNG 3: SQL & DATABASE + JPA

**Tương ứng**: Tháng 3 trong lộ trình (Tuần 9-12)
**Mục tiêu**: Thiết kế schema, viết SQL, dùng JPA/Hibernate với Spring Data
**Quy ước**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Phần A: SQL Cơ Bản (15 bài)

> Chạy trên PostgreSQL hoặc MySQL. Dùng pgAdmin, DBeaver, hoặc terminal.

### Bài tập dùng schema sau:

```sql
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    salary DECIMAL(12,2),
    hire_date DATE,
    department_id INT REFERENCES departments(id),
    manager_id INT REFERENCES employees(id)
);

CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    budget DECIMAL(15,2),
    start_date DATE,
    end_date DATE
);

CREATE TABLE employee_projects (
    employee_id INT REFERENCES employees(id),
    project_id INT REFERENCES projects(id),
    role VARCHAR(50),
    PRIMARY KEY (employee_id, project_id)
);

-- Insert data mẫu
INSERT INTO departments VALUES
(1, 'Engineering', 'HCM'), (2, 'Marketing', 'HN'), (3, 'HR', 'HCM'), (4, 'Finance', 'DN');

INSERT INTO employees VALUES
(1, 'Trí', 'tri@co.vn', 30000000, '2022-01-15', 1, NULL),
(2, 'Minh', 'minh@co.vn', 25000000, '2022-06-01', 1, 1),
(3, 'An', 'an@co.vn', 22000000, '2023-03-10', 2, NULL),
(4, 'Bình', 'binh@co.vn', 28000000, '2021-08-20', 2, 3),
(5, 'Cường', 'cuong@co.vn', 20000000, '2023-09-01', 3, NULL),
(6, 'Dũng', 'dung@co.vn', 35000000, '2020-05-15', 1, 1),
(7, 'Hà', 'ha@co.vn', 18000000, '2024-01-10', 3, 5),
(8, 'Khánh', 'khanh@co.vn', 32000000, '2021-11-01', 4, NULL);

INSERT INTO projects VALUES
(1, 'Website Redesign', 500000000, '2024-01-01', '2024-06-30'),
(2, 'Mobile App', 800000000, '2024-03-01', '2024-12-31'),
(3, 'Marketing Campaign', 200000000, '2024-02-01', '2024-04-30');

INSERT INTO employee_projects VALUES
(1, 1, 'Lead'), (2, 1, 'Developer'), (6, 1, 'Senior Dev'),
(1, 2, 'Architect'), (2, 2, 'Developer'), (6, 2, 'Lead'),
(3, 3, 'Lead'), (4, 3, 'Coordinator');
```

---

### A1. SELECT cơ bản ⭐

```sql
-- 1. Lấy tất cả nhân viên
-- 2. Lấy tên và email nhân viên
-- 3. Lấy nhân viên lương > 25 triệu
-- 4. Lấy nhân viên tuyển sau 2023-01-01
-- 5. Lấy nhân viên tên chứa 'nh' (LIKE)
```

<details>
<summary>Đáp án</summary>

```sql
SELECT * FROM employees;
SELECT name, email FROM employees;
SELECT * FROM employees WHERE salary > 25000000;
SELECT * FROM employees WHERE hire_date > '2023-01-01';
SELECT * FROM employees WHERE name LIKE '%nh%';  -- Khánh, Bình, Minh
```
</details>

---

### A2. ORDER BY, LIMIT, DISTINCT ⭐

```sql
-- 1. Sắp xếp nhân viên theo lương giảm dần
-- 2. Top 3 lương cao nhất
-- 3. Lấy danh sách department_id không trùng
-- 4. Sắp xếp theo department_id ASC, rồi theo salary DESC
-- 5. Nhân viên được tuyển gần nhất
```

<details>
<summary>Đáp án</summary>

```sql
SELECT * FROM employees ORDER BY salary DESC;
SELECT * FROM employees ORDER BY salary DESC LIMIT 3;
SELECT DISTINCT department_id FROM employees;
SELECT * FROM employees ORDER BY department_id ASC, salary DESC;
SELECT * FROM employees ORDER BY hire_date DESC LIMIT 1;
```
</details>

---

### A3. Aggregate Functions ⭐

```sql
-- 1. Tổng lương tất cả nhân viên
-- 2. Lương trung bình
-- 3. Lương cao nhất, thấp nhất
-- 4. Số nhân viên
-- 5. Số nhân viên có manager (manager_id IS NOT NULL)
```

<details>
<summary>Đáp án</summary>

```sql
SELECT SUM(salary) AS total_salary FROM employees;
SELECT AVG(salary) AS avg_salary FROM employees;
SELECT MAX(salary) AS max_sal, MIN(salary) AS min_sal FROM employees;
SELECT COUNT(*) AS total_employees FROM employees;
SELECT COUNT(manager_id) AS has_manager FROM employees;
```
</details>

---

### A4. GROUP BY & HAVING ⭐⭐

```sql
-- 1. Số nhân viên mỗi phòng ban
-- 2. Lương trung bình mỗi phòng ban
-- 3. Phòng ban có lương trung bình > 25 triệu
-- 4. Phòng ban có > 2 nhân viên
-- 5. Năm tuyển dụng và số nhân viên tuyển năm đó
```

<details>
<summary>Đáp án</summary>

```sql
SELECT department_id, COUNT(*) AS count FROM employees GROUP BY department_id;

SELECT department_id, AVG(salary) AS avg_sal
FROM employees GROUP BY department_id;

SELECT department_id, AVG(salary) AS avg_sal
FROM employees GROUP BY department_id HAVING AVG(salary) > 25000000;

SELECT department_id, COUNT(*) AS cnt
FROM employees GROUP BY department_id HAVING COUNT(*) > 2;

SELECT EXTRACT(YEAR FROM hire_date) AS year, COUNT(*) AS hired
FROM employees GROUP BY EXTRACT(YEAR FROM hire_date) ORDER BY year;
```
</details>

---

### A5. JOINs ⭐⭐

```sql
-- 1. Lấy nhân viên kèm tên phòng ban (INNER JOIN)
-- 2. Lấy TẤT CẢ phòng ban, kể cả phòng chưa có nhân viên (LEFT JOIN)
-- 3. Lấy nhân viên kèm tên manager (self-join)
-- 4. Lấy nhân viên và projects họ tham gia (qua bảng nối)
-- 5. Phòng ban nào chưa có nhân viên?
```

<details>
<summary>Đáp án</summary>

```sql
-- 1. INNER JOIN
SELECT e.name, e.salary, d.name AS department
FROM employees e INNER JOIN departments d ON e.department_id = d.id;

-- 2. LEFT JOIN (phòng Finance chưa có ai → vẫn hiện)
SELECT d.name, COUNT(e.id) AS emp_count
FROM departments d LEFT JOIN employees e ON d.id = e.department_id
GROUP BY d.name;

-- 3. Self-join
SELECT e.name AS employee, m.name AS manager
FROM employees e LEFT JOIN employees m ON e.manager_id = m.id;

-- 4. Many-to-many join
SELECT e.name, p.name AS project, ep.role
FROM employees e
JOIN employee_projects ep ON e.id = ep.employee_id
JOIN projects p ON ep.project_id = p.id;

-- 5. Phòng không có nhân viên
SELECT d.name FROM departments d
LEFT JOIN employees e ON d.id = e.department_id
WHERE e.id IS NULL;
```
</details>

---

### A6. Subqueries ⭐⭐

```sql
-- 1. Nhân viên có lương cao hơn lương trung bình
-- 2. Nhân viên thuộc phòng Engineering (dùng subquery, không JOIN)
-- 3. Nhân viên có lương cao nhất mỗi phòng ban
-- 4. Nhân viên KHÔNG tham gia project nào
-- 5. Phòng ban có tổng lương cao nhất
```

<details>
<summary>Đáp án</summary>

```sql
-- 1.
SELECT * FROM employees WHERE salary > (SELECT AVG(salary) FROM employees);

-- 2.
SELECT * FROM employees
WHERE department_id = (SELECT id FROM departments WHERE name = 'Engineering');

-- 3. (dùng correlated subquery)
SELECT * FROM employees e
WHERE salary = (SELECT MAX(salary) FROM employees WHERE department_id = e.department_id);

-- 4.
SELECT * FROM employees
WHERE id NOT IN (SELECT DISTINCT employee_id FROM employee_projects);

-- 5.
SELECT d.name, SUM(e.salary) AS total
FROM departments d JOIN employees e ON d.id = e.department_id
GROUP BY d.name ORDER BY total DESC LIMIT 1;
```
</details>

---

### A7. UPDATE & DELETE ⭐

```sql
-- 1. Tăng lương 10% cho nhân viên Engineering
-- 2. Cập nhật location phòng HR thành 'DN'
-- 3. Xóa nhân viên có lương < 19 triệu
-- 4. Xóa project đã kết thúc (end_date < NOW())
```

---

### A8. INSERT nâng cao & UPSERT ⭐⭐

```sql
-- 1. Insert nhiều rows cùng lúc
-- 2. Insert from SELECT (copy nhân viên Engineering sang bảng tạm)
-- 3. UPSERT: Insert hoặc update nếu email đã tồn tại (ON CONFLICT)
```

<details>
<summary>Đáp án</summary>

```sql
-- 3. UPSERT (PostgreSQL)
INSERT INTO employees (name, email, salary, department_id)
VALUES ('Trí', 'tri@co.vn', 32000000, 1)
ON CONFLICT (email) DO UPDATE SET salary = EXCLUDED.salary;
```
</details>

---

### A9. INDEX & EXPLAIN ⭐⭐

```sql
-- 1. Tạo index trên email
-- 2. Tạo composite index trên (department_id, salary)
-- 3. Dùng EXPLAIN ANALYZE so sánh query CÓ và KHÔNG CÓ index
-- 4. Khi nào KHÔNG nên tạo index?
```

---

### A10. Thiết kế Schema — Blog Platform ⭐⭐⭐

Thiết kế schema cho blog:
- Users (đăng ký, profile)
- Posts (bài viết, draft/published)
- Comments (bình luận, nested replies)
- Tags (tags cho posts, many-to-many)
- Likes (user like post)

Yêu cầu:
- Vẽ ER diagram (có thể dùng text)
- Viết CREATE TABLE với constraints
- Viết 5 query phức tạp (top posts, comments count, tag cloud...)

---

### A11-A15: Thêm 5 bài SQL nâng cao

**A11** ⭐⭐ — **Window Functions**: `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()` — xếp hạng lương trong mỗi phòng.

**A12** ⭐⭐ — **CTE (Common Table Expression)**: Dùng `WITH` để viết query đọc dễ hơn subquery.

**A13** ⭐⭐⭐ — **Recursive CTE**: Lấy cây manager → employee (org chart) dùng recursive WITH.

**A14** ⭐⭐ — **CASE WHEN**: Phân loại lương (Cao/Trung bình/Thấp) trong SELECT.

**A15** ⭐⭐⭐ — **Stored Procedure/Function**: Viết function tính bonus dựa trên thâm niên + performance.

---

## Phần B: JPA/Hibernate & Spring Data JPA (15 bài)

### B1. Entity cơ bản ⭐

Tạo JPA entity `Product`:

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean active = true;

    // Constructors, getters, setters...
}
```

Bài tập: Tạo entities `Category`, `Brand` tương tự. Cấu hình `application.properties` kết nối PostgreSQL. Chạy app → kiểm tra tables được tạo tự động.

---

### B2. Repository cơ bản — CRUD ⭐

Tạo `ProductRepository`:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository cung cấp sẵn:
    // save(), findById(), findAll(), deleteById(), count()...
}
```

Viết test trong `main()` hoặc `CommandLineRunner`:
1. Lưu 5 products
2. Tìm theo ID
3. Lấy tất cả
4. Cập nhật giá
5. Xóa 1 product
6. Đếm tổng

```java
@Bean
CommandLineRunner demo(ProductRepository repo) {
    return args -> {
        // 1. Save
        repo.save(new Product("iPhone 15", new BigDecimal("25000000"), "Apple smartphone"));
        repo.save(new Product("MacBook Pro", new BigDecimal("55000000"), "Apple laptop"));

        // 2. Find by ID
        Product p = repo.findById(1L).orElseThrow();
        System.out.println(p.getName()); // iPhone 15

        // 3. Find all
        repo.findAll().forEach(System.out::println);

        // 4. Update
        p.setPrice(new BigDecimal("24000000"));
        repo.save(p);  // save() = insert nếu chưa có, update nếu có ID

        // 5. Delete
        repo.deleteById(2L);

        // 6. Count
        System.out.println("Total: " + repo.count());
    };
}
```

---

### B3. Query Methods — Tự động từ tên method ⭐⭐

Thêm các methods vào `ProductRepository` — Spring Data tự tạo query từ tên method:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Bài tập: viết method name đúng để Spring tự generate query

    // 1. Tìm theo tên chính xác
    // SELECT * FROM products WHERE name = ?
    List<Product> findByName(String name);

    // 2. Tìm theo tên chứa (LIKE %keyword%)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // 3. Tìm sản phẩm giá trong khoảng
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    // 4. Tìm sản phẩm active, sắp theo giá giảm
    List<Product> findByActiveTrueOrderByPriceDesc();

    // 5. Đếm sản phẩm theo category
    long countByCategoryId(Long categoryId);

    // 6. Tìm sản phẩm giá cao nhất
    Optional<Product> findFirstByOrderByPriceDesc();

    // 7. Kiểm tra tên đã tồn tại
    boolean existsByName(String name);

    // 8. Xóa theo category
    void deleteByCategoryId(Long categoryId);
}
```

Test tất cả methods trên.

---

### B4. @Query — Custom JPQL ⭐⭐

Viết custom queries khi tên method quá phức tạp:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. JPQL: tìm sản phẩm theo category name
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String name);

    // 2. JPQL: tìm sản phẩm giá cao hơn trung bình
    @Query("SELECT p FROM Product p WHERE p.price > (SELECT AVG(p2.price) FROM Product p2)")
    List<Product> findAboveAveragePrice();

    // 3. Native SQL: query phức tạp
    @Query(value = "SELECT * FROM products WHERE price > :minPrice ORDER BY price DESC LIMIT :limit",
           nativeQuery = true)
    List<Product> findTopExpensive(@Param("minPrice") BigDecimal min, @Param("limit") int limit);

    // 4. JPQL: cập nhật hàng loạt
    @Modifying
    @Query("UPDATE Product p SET p.price = p.price * :factor WHERE p.category.id = :catId")
    int updatePriceByCategory(@Param("catId") Long categoryId, @Param("factor") double factor);
}
```

---

### B5. Relationships — @OneToMany / @ManyToOne ⭐⭐

Tạo quan hệ Category ↔ Product (1 category có nhiều products):

```java
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}

@Entity
public class Product {
    // ... các fields khác

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
```

Bài tập:
1. Tạo Category "Electronics", thêm 3 Products vào
2. Lấy Category → in danh sách Products
3. Lấy Product → in tên Category
4. Xóa Category → Products tự xóa theo (cascade)

---

### B6. @ManyToMany — Tags ⭐⭐⭐

Tạo quan hệ Product ↔ Tag (nhiều-nhiều):

```java
@Entity
public class Tag {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Product> products = new HashSet<>();
}

@Entity
public class Product {
    @ManyToMany
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Helper methods
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }
}
```

Bài tập: Thêm/xóa tags cho products. Query products theo tag name.

---

### B7. Pagination & Sorting ⭐⭐

```java
// Repository — đã có sẵn từ JpaRepository
Page<Product> findAll(Pageable pageable);
Page<Product> findByCategoryId(Long catId, Pageable pageable);

// Sử dụng
Pageable pageable = PageRequest.of(0, 10, Sort.by("price").descending());
Page<Product> page = productRepo.findAll(pageable);

System.out.println("Tổng: " + page.getTotalElements());
System.out.println("Trang: " + page.getNumber() + "/" + page.getTotalPages());
page.getContent().forEach(System.out::println);
```

Bài tập: Implement endpoint `GET /api/products?page=0&size=10&sort=price,desc`.

---

### B8-B15: Thêm 8 bài JPA nâng cao

**B8** ⭐⭐ — **DTO Projection**: Tạo interface projection và record DTO, so sánh performance với entity.

**B9** ⭐⭐ — **Auditing**: Thêm `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy` tự động.

**B10** ⭐⭐⭐ — **Specification**: Viết dynamic queries dùng `Specification<Product>` cho filter phức tạp (price range + category + keyword).

**B11** ⭐⭐ — **Flyway Migration**: Tạo 3 migration files (`V1__create_tables.sql`, `V2__add_columns.sql`, `V3__seed_data.sql`).

**B12** ⭐⭐⭐ — **N+1 Problem**: Tái tạo N+1 query problem, detect bằng logging, fix bằng `@EntityGraph` và `JOIN FETCH`.

**B13** ⭐⭐ — **Transactions**: Viết transfer tiền giữa 2 accounts dùng `@Transactional`. Test rollback khi exception.

**B14** ⭐⭐⭐ — **Optimistic Locking**: Thêm `@Version` vào entity, test concurrent update → catch `OptimisticLockException`.

**B15** ⭐⭐⭐ — **Mini Project: E-commerce Database**: Thiết kế đầy đủ schema + entities cho: User, Product, Category, Order, OrderItem, Review, Address. Tất cả relationships, queries, pagination.

---

## Checklist theo dõi

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — SQL Cơ Bản | 15 bài | ☐ |
| B — JPA/Hibernate | 15 bài | ☐ |
| **Tổng** | **30 bài** | |
