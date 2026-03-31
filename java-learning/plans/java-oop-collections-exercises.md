# BÀI TẬP THÁNG 2: OOP & COLLECTIONS

**Tương ứng**: Tháng 2 trong lộ trình (Tuần 5-8)
**Mục tiêu**: Thành thạo OOP, Collections framework, Stream API, Lambda
**Quy ước**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Phần A: Classes & Encapsulation (10 bài)

### A1. Class đầu tiên — Sản phẩm ⭐

Tạo class `Product` với:
- Fields: `name` (String), `price` (double), `quantity` (int)
- Constructor nhận cả 3 tham số
- Getters/Setters cho tất cả fields
- Method `getTotal()` trả về `price * quantity`
- Method `toString()` trả về thông tin sản phẩm

```java
Product p = new Product("Laptop", 25000000, 2);
System.out.println(p);           // Product{name='Laptop', price=25000000.0, quantity=2}
System.out.println(p.getTotal()); // 50000000.0
p.setPrice(23000000);
System.out.println(p.getTotal()); // 46000000.0
```

<details>
<summary>Gợi ý</summary>

```java
public class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public double getTotal() {
        return price * quantity;
    }

    // Getter
    public String getName() { return name; }
    public double getPrice() { return price; }

    // Setter — có thể thêm validation
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Giá không được âm");
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{name='%s', price=%.1f, quantity=%d}".formatted(name, price, quantity);
    }
}
```

**Khác TypeScript:**
```typescript
// TS: ngắn hơn nhiều
class Product {
    constructor(
        public name: string,       // Tự tạo field + gán
        public price: number,
        public quantity: number
    ) {}
    getTotal() { return this.price * this.quantity; }
}
```
Java không có shorthand constructor như TS — phải khai báo field, constructor, getter/setter riêng.
</details>

---

### A2. Tài khoản ngân hàng — Encapsulation ⭐⭐

Tạo class `BankAccount` với:
- Private fields: `accountNumber`, `holderName`, `balance`
- Constructor: chỉ nhận `accountNumber` và `holderName`, `balance` = 0
- Methods:
  - `deposit(double amount)` — nạp tiền, từ chối nếu amount <= 0
  - `withdraw(double amount)` — rút tiền, từ chối nếu không đủ
  - `getBalance()` — xem số dư (không có setBalance!)
  - `transfer(BankAccount target, double amount)` — chuyển khoản

```java
BankAccount acc1 = new BankAccount("001", "Trí");
BankAccount acc2 = new BankAccount("002", "Minh");
acc1.deposit(5000000);
acc1.transfer(acc2, 2000000);
System.out.println(acc1.getBalance()); // 3000000
System.out.println(acc2.getBalance()); // 2000000
acc1.withdraw(10000000);               // → Lỗi: Số dư không đủ
```

<details>
<summary>Gợi ý</summary>

```java
public class BankAccount {
    private final String accountNumber;  // final = không đổi được
    private final String holderName;
    private double balance;

    public BankAccount(String accountNumber, String holderName) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = 0;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Số tiền phải > 0");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Số tiền phải > 0");
        if (amount > balance) throw new IllegalStateException("Số dư không đủ");
        balance -= amount;
    }

    public void transfer(BankAccount target, double amount) {
        this.withdraw(amount);   // Rút từ tài khoản này
        target.deposit(amount);  // Nạp vào tài khoản kia
    }

    public double getBalance() { return balance; }
    // KHÔNG có setBalance() → bảo vệ data integrity
}
```

**Bài học**: Encapsulation = kiểm soát cách data bị thay đổi. Không expose `setBalance()` vì balance chỉ thay đổi qua deposit/withdraw.
</details>

---

### A3. static — Đếm số object đã tạo ⭐

Tạo class `User` có:
- `static int totalUsers = 0` — đếm tổng users đã tạo
- Constructor tăng `totalUsers` mỗi lần tạo user mới
- `static int getTotalUsers()` — lấy tổng

```java
User u1 = new User("A");
User u2 = new User("B");
User u3 = new User("C");
System.out.println(User.getTotalUsers()); // 3 — gọi trên CLASS, không phải object
```

<details>
<summary>Gợi ý</summary>

```java
public class User {
    private static int totalUsers = 0;  // Thuộc về CLASS, không phải instance
    private String name;

    public User(String name) {
        this.name = name;
        totalUsers++;  // Tăng mỗi lần tạo object mới
    }

    public static int getTotalUsers() {
        return totalUsers;
    }
}
```

**Khác TypeScript:**
```typescript
class User {
    static totalUsers = 0;
    constructor(public name: string) { User.totalUsers++; }
}
```
Giống nhau! Cả TS và Java đều có `static`.
</details>

---

### A4. Builder Pattern — Tạo object phức tạp ⭐⭐⭐

Tạo class `Pizza` dùng Builder pattern (thay vì constructor 10 tham số):

```java
Pizza pizza = new Pizza.Builder("Large")
    .crust("Thin")
    .cheese(true)
    .pepperoni(true)
    .mushrooms(true)
    .olives(false)
    .build();

System.out.println(pizza);
// Pizza{size='Large', crust='Thin', cheese=true, pepperoni=true, mushrooms=true, olives=false}
```

<details>
<summary>Gợi ý</summary>

```java
public class Pizza {
    private final String size;
    private final String crust;
    private final boolean cheese;
    private final boolean pepperoni;
    private final boolean mushrooms;
    private final boolean olives;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.crust = builder.crust;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
        this.mushrooms = builder.mushrooms;
        this.olives = builder.olives;
    }

    public static class Builder {
        private final String size;     // Bắt buộc
        private String crust = "Regular";  // Mặc định
        private boolean cheese = false;
        private boolean pepperoni = false;
        private boolean mushrooms = false;
        private boolean olives = false;

        public Builder(String size) { this.size = size; }

        public Builder crust(String crust) { this.crust = crust; return this; }
        public Builder cheese(boolean v) { this.cheese = v; return this; }
        public Builder pepperoni(boolean v) { this.pepperoni = v; return this; }
        public Builder mushrooms(boolean v) { this.mushrooms = v; return this; }
        public Builder olives(boolean v) { this.olives = v; return this; }

        public Pizza build() { return new Pizza(this); }
    }
}
```

**Rất phổ biến trong Spring Boot**: nhiều config objects dùng Builder pattern.
</details>

---

### A5. Record — DTO hiện đại (Java 16+) ⭐

Tạo 3 records:
1. `record Point(int x, int y)` — với method `distanceTo(Point other)`
2. `record Money(double amount, String currency)` — với method `add(Money other)`
3. `record UserDTO(Long id, String name, String email)` — dùng như DTO

```java
Point p1 = new Point(0, 0);
Point p2 = new Point(3, 4);
System.out.println(p1.distanceTo(p2)); // 5.0

Money m1 = new Money(100, "USD");
Money m2 = new Money(50, "USD");
System.out.println(m1.add(m2)); // Money[amount=150.0, currency=USD]
```

<details>
<summary>Gợi ý</summary>

```java
public record Point(int x, int y) {
    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

public record Money(double amount, String currency) {
    // Compact constructor — validate
    public Money {
        if (amount < 0) throw new IllegalArgumentException("Số tiền không được âm");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Không cùng đơn vị tiền");
        return new Money(this.amount + other.amount, this.currency);
    }
}
```

Record = class bất biến 1 dòng. Tự có `toString()`, `equals()`, `hashCode()`. **Dùng cho DTOs trong Spring Boot.**
</details>

---

## Phần B: Inheritance & Polymorphism (10 bài)

### B1. Hệ thống hình học — Inheritance cơ bản ⭐⭐

Tạo class hierarchy:
```
Shape (abstract)
├── Circle
├── Rectangle
└── Triangle
```

- `Shape`: abstract class với abstract method `area()` và `perimeter()`, field `color`
- Mỗi subclass implement `area()` và `perimeter()`
- Tạo `ArrayList<Shape>`, thêm 3 hình khác nhau, duyệt và in thông tin

```java
List<Shape> shapes = List.of(
    new Circle("Đỏ", 5),
    new Rectangle("Xanh", 4, 6),
    new Triangle("Vàng", 3, 4, 5)
);

for (Shape s : shapes) {
    System.out.printf("%s: diện tích=%.2f, chu vi=%.2f%n", s.getClass().getSimpleName(), s.area(), s.perimeter());
}
// Circle: diện tích=78.54, chu vi=31.42
// Rectangle: diện tích=24.00, chu vi=20.00
// Triangle: diện tích=6.00, chu vi=12.00
```

<details>
<summary>Gợi ý</summary>

```java
public abstract class Shape {
    protected String color;

    public Shape(String color) { this.color = color; }

    public abstract double area();
    public abstract double perimeter();
}

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() { return Math.PI * radius * radius; }

    @Override
    public double perimeter() { return 2 * Math.PI * radius; }
}
```
</details>

---

### B2. Hệ thống nhân viên — Polymorphism ⭐⭐

```
Employee (abstract)
├── FullTimeEmployee   → lương = baseSalary + bonus
├── PartTimeEmployee   → lương = hoursWorked * hourlyRate
└── ContractEmployee   → lương = contractAmount / contractMonths
```

Tạo `PayrollSystem`:
- Nhận `List<Employee>`, tính tổng lương
- In bảng lương chi tiết

```java
List<Employee> employees = List.of(
    new FullTimeEmployee("Trí", 20000000, 5000000),
    new PartTimeEmployee("Minh", 160, 100000),
    new ContractEmployee("An", 120000000, 12)
);

PayrollSystem.printPayroll(employees);
// Trí (Full-time): 25,000,000 VNĐ
// Minh (Part-time): 16,000,000 VNĐ
// An (Contract): 10,000,000 VNĐ
// TỔNG: 51,000,000 VNĐ
```

<details>
<summary>Gợi ý</summary>

```java
public abstract class Employee {
    protected String name;
    public Employee(String name) { this.name = name; }
    public abstract double calculateSalary();  // Mỗi loại tính khác nhau

    @Override
    public String toString() {
        return "%s: %,.0f VNĐ".formatted(name, calculateSalary());
    }
}

// Polymorphism: gọi calculateSalary() trên Employee reference
// nhưng chạy implementation của subclass
public static void printPayroll(List<Employee> employees) {
    double total = 0;
    for (Employee e : employees) {
        System.out.println(e);           // Gọi toString() → calculateSalary()
        total += e.calculateSalary();     // Polymorphic call
    }
    System.out.printf("TỔNG: %,.0f VNĐ%n", total);
}
```
</details>

---

### B3. Interface — Hệ thống thanh toán ⭐⭐

Tạo interface `Payable` và `Printable`:

```java
interface Payable {
    double getPaymentAmount();
}

interface Printable {
    String toPrintFormat();
}
```

Class `Invoice` và `Employee` đều implement `Payable`. Viết method nhận `List<Payable>` và tính tổng.

```java
List<Payable> items = List.of(
    new Invoice("INV-001", 5000000),
    new Invoice("INV-002", 3000000),
    new SalariedEmployee("Trí", 25000000)
);

double total = calculateTotal(items); // 33,000,000
```

<details>
<summary>Gợi ý</summary>

```java
public interface Payable {
    double getPaymentAmount();
}

public class Invoice implements Payable, Printable {
    private String invoiceNumber;
    private double amount;

    @Override
    public double getPaymentAmount() { return amount; }

    @Override
    public String toPrintFormat() {
        return "Hóa đơn %s: %,.0f VNĐ".formatted(invoiceNumber, amount);
    }
}

// Một class có thể implement nhiều interfaces — KHÔNG thể extends nhiều classes
// Đây là lý do interfaces quan trọng hơn abstract classes trong Java hiện đại
```

**Khác TypeScript:**
```typescript
// TS: interface chỉ là type, không bắt buộc implement
interface Payable { getPaymentAmount(): number; }
// Java: interface là CONTRACT, compiler bắt buộc implement tất cả methods
```
</details>

---

### B4. Sealed Classes — Domain Model an toàn (Java 17) ⭐⭐⭐

Tạo sealed class hierarchy cho trạng thái đơn hàng:

```java
sealed interface OrderStatus
    permits Pending, Processing, Shipped, Delivered, Cancelled {}

record Pending(LocalDateTime createdAt) implements OrderStatus {}
record Processing(String warehouse) implements OrderStatus {}
record Shipped(String trackingNumber, LocalDateTime shippedAt) implements OrderStatus {}
record Delivered(LocalDateTime deliveredAt, String signature) implements OrderStatus {}
record Cancelled(String reason, LocalDateTime cancelledAt) implements OrderStatus {}
```

Viết method `getStatusMessage(OrderStatus status)` dùng pattern matching switch:

```java
String msg = getStatusMessage(new Shipped("VN123456", LocalDateTime.now()));
// "Đã gửi hàng - Mã vận đơn: VN123456"
```

<details>
<summary>Gợi ý</summary>

```java
public String getStatusMessage(OrderStatus status) {
    return switch (status) {
        case Pending(var created) ->
            "Đang chờ xử lý - Tạo lúc: " + created;
        case Processing(var warehouse) ->
            "Đang xử lý tại kho: " + warehouse;
        case Shipped(var tracking, var shipped) ->
            "Đã gửi hàng - Mã vận đơn: " + tracking;
        case Delivered(var delivered, var sig) ->
            "Đã giao - Người nhận: " + sig;
        case Cancelled(var reason, var at) ->
            "Đã hủy - Lý do: " + reason;
    };
    // Không cần default! Compiler biết hết các trường hợp.
}
```
</details>

---

### B5. Composition vs Inheritance ⭐⭐

Refactor thiết kế sai thành thiết kế đúng:

**Sai** (Inheritance lạm dụng):
```java
class Stack extends ArrayList<Integer> { }
// → Stack KHÔNG PHẢI là ArrayList! Nó CHỈ DÙNG ArrayList bên trong.
```

**Đúng** (Composition):
Tạo class `Stack<T>` dùng `ArrayList<T>` bên trong (HAS-A, không phải IS-A):
- `push(T item)`, `pop()`, `peek()`, `isEmpty()`, `size()`
- Không expose `add()`, `remove()`, `get()` của ArrayList

```java
Stack<Integer> stack = new Stack<>();
stack.push(1);
stack.push(2);
stack.push(3);
System.out.println(stack.peek()); // 3
System.out.println(stack.pop());  // 3
System.out.println(stack.size()); // 2
```

<details>
<summary>Gợi ý</summary>

```java
public class Stack<T> {
    private final ArrayList<T> items = new ArrayList<>();  // Composition: HAS-A

    public void push(T item) { items.add(item); }

    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return items.remove(items.size() - 1);
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return items.get(items.size() - 1);
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public int size() { return items.size(); }
}
```

**Nguyên tắc**: "Prefer composition over inheritance" — chỉ dùng inheritance khi thật sự IS-A.
</details>

---

### B6-B10: Thêm 5 bài OOP nâng cao

**B6** ⭐⭐ — **Comparable & Comparator**: Tạo class `Student` implement `Comparable<Student>` (sort theo GPA). Viết thêm `Comparator` sort theo tên, tuổi.

**B7** ⭐⭐ — **Abstract class vs Interface**: Tạo hệ thống `Animal` (abstract class: `eat()`, `sleep()`) với interfaces `Swimmable`, `Flyable`. Class `Duck` extends `Animal` implements cả hai.

**B8** ⭐⭐⭐ — **Design Pattern: Strategy**: Tạo hệ thống tính phí ship dùng Strategy pattern — `FreeShipping`, `StandardShipping`, `ExpressShipping`. Inject strategy vào `Order`.

**B9** ⭐⭐⭐ — **Design Pattern: Observer**: Tạo `EventEmitter` (giống Node.js EventEmitter) — `on(event, listener)`, `emit(event, data)`. Dùng `Map<String, List<Consumer<Object>>>`.

**B10** ⭐⭐⭐ — **Mini Project: Hệ thống thư viện**: Class `Book`, `Member`, `Library`. Member có thể `borrow()`, `return()`. Library quản lý sách có sẵn, sách đang mượn. Giới hạn 3 sách/member.

---

## Phần C: Collections Framework (10 bài)

### C1. ArrayList — Quản lý danh sách ⭐

Tạo `TodoList` dùng `ArrayList<String>`:
- `add(task)`, `remove(index)`, `markDone(index)`, `listAll()`
- Task đã done thêm prefix `[✓]`, chưa done: `[ ]`

```java
TodoList todo = new TodoList();
todo.add("Học Java");
todo.add("Viết code");
todo.add("Review PR");
todo.markDone(0);
todo.listAll();
// 1. [✓] Học Java
// 2. [ ] Viết code
// 3. [ ] Review PR
```

---

### C2. HashMap — Đếm tần suất từ ⭐⭐

Cho chuỗi text, đếm tần suất xuất hiện mỗi từ dùng `HashMap<String, Integer>`:

```java
String text = "java spring boot java spring java boot boot boot";
// {java=3, spring=2, boot=4}
```

Mở rộng: sắp xếp theo tần suất giảm dần.

<details>
<summary>Gợi ý</summary>

```java
Map<String, Integer> freq = new HashMap<>();
for (String word : text.split("\\s+")) {
    freq.merge(word, 1, Integer::sum);  // Nếu có rồi → cộng 1, chưa có → đặt 1
}

// Sắp xếp theo value giảm dần
freq.entrySet().stream()
    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
    .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
```

**TypeScript tương đương:**
```typescript
const freq: Record<string, number> = {};
for (const word of text.split(/\s+/)) {
    freq[word] = (freq[word] || 0) + 1;
}
```
</details>

---

### C3. HashSet — Tìm phần tử chung ⭐

Cho 2 mảng, tìm: phần tử chung (intersection), hợp (union), khác biệt (difference).

```java
int[] a = {1, 2, 3, 4, 5};
int[] b = {3, 4, 5, 6, 7};
// Chung: [3, 4, 5]
// Hợp: [1, 2, 3, 4, 5, 6, 7]
// A - B: [1, 2]
```

<details>
<summary>Gợi ý</summary>

```java
Set<Integer> setA = new HashSet<>(List.of(1, 2, 3, 4, 5));
Set<Integer> setB = new HashSet<>(List.of(3, 4, 5, 6, 7));

Set<Integer> intersection = new HashSet<>(setA);
intersection.retainAll(setB);  // Giữ lại phần tử chung

Set<Integer> union = new HashSet<>(setA);
union.addAll(setB);            // Gộp tất cả

Set<Integer> diff = new HashSet<>(setA);
diff.removeAll(setB);          // Xóa phần tử có trong B
```
</details>

---

### C4. Queue & Stack — Xử lý đúng thứ tự ⭐⭐

1. **Queue** (FIFO): Mô phỏng hàng đợi in tài liệu. Thêm 5 tài liệu, xử lý từng cái.
2. **Stack** (LIFO): Kiểm tra dấu ngoặc `()[]{}` cân bằng. Input: `"([{}])"` → true, `"([)]"` → false.

<details>
<summary>Gợi ý — Kiểm tra ngoặc</summary>

```java
public static boolean isBalanced(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');

    for (char c : s.toCharArray()) {
        if ("([{".indexOf(c) != -1) {
            stack.push(c);
        } else if (pairs.containsKey(c)) {
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
        }
    }
    return stack.isEmpty();
}
```
</details>

---

### C5. TreeMap — Bảng xếp hạng ⭐⭐

Tạo hệ thống xếp hạng game dùng `TreeMap` (tự sắp xếp theo key):

```java
Leaderboard board = new Leaderboard();
board.addScore("Trí", 1500);
board.addScore("Minh", 2300);
board.addScore("An", 1800);
board.addScore("Trí", 2000);  // Cập nhật nếu điểm cao hơn

board.printTop(3);
// 1. Minh: 2300
// 2. Trí: 2000
// 3. An: 1800
```

---

### C6-C10: Thêm 5 bài Collections

**C6** ⭐⭐ — **LinkedList vs ArrayList**: Benchmark thời gian `add(0, element)` 100.000 lần trên cả hai. Giải thích kết quả.

**C7** ⭐⭐ — **HashMap nâng cao**: Tạo `PhoneBook` dùng `Map<String, List<String>>` — mỗi người nhiều SĐT. Implement `add`, `remove`, `search`, `listAll`.

**C8** ⭐⭐⭐ — **LRU Cache**: Implement `LRUCache<K,V>` dùng `LinkedHashMap` với capacity giới hạn. Override `removeEldestEntry()`.

**C9** ⭐⭐ — **Collections.unmodifiable**: Tạo immutable list, map, set. Thử modify → bắt `UnsupportedOperationException`.

**C10** ⭐⭐ — **Iterable & Iterator**: Tạo class `Range(start, end)` implement `Iterable<Integer>` để dùng trong for-each: `for (int n : new Range(1, 5))`.

---

## Phần D: Stream API & Lambda (10 bài)

### D1. Stream cơ bản — filter, map, collect ⭐

Cho danh sách:
```java
List<String> names = List.of("Trí", "Minh", "An", "Bình", "Cường", "Dũng", "Anh");
```

Dùng Streams:
1. Lọc tên có 3+ ký tự
2. Chuyển tất cả sang chữ hoa
3. Sắp xếp theo ABC
4. Nối thành chuỗi cách bằng ", "

```
Output: "ANH, BÌNH, CƯỜNG, DŨNG, MINH, TRÍ"
```

<details>
<summary>Gợi ý</summary>

```java
String result = names.stream()
    .filter(n -> n.length() >= 3)
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.joining(", "));
```

**TypeScript tương đương:**
```typescript
const result = names
    .filter(n => n.length >= 3)
    .map(n => n.toUpperCase())
    .sort()
    .join(", ");
```
Gần giống! Java thêm `.stream()` ở đầu và `.collect()` ở cuối.
</details>

---

### D2. Stream với Objects — Xử lý danh sách Employee ⭐⭐

```java
record Employee(String name, String department, double salary, int age) {}

List<Employee> employees = List.of(
    new Employee("Trí", "Engineering", 30000000, 28),
    new Employee("Minh", "Engineering", 25000000, 24),
    new Employee("An", "Marketing", 22000000, 30),
    new Employee("Bình", "Marketing", 28000000, 32),
    new Employee("Cường", "HR", 20000000, 26),
    new Employee("Dũng", "Engineering", 35000000, 35)
);
```

Dùng Streams tính:
1. Tổng lương tất cả nhân viên
2. Lương trung bình theo department
3. Nhân viên lương cao nhất
4. Danh sách tên nhân viên Engineering, sắp theo lương giảm dần
5. Số nhân viên trên 30 tuổi
6. Nhóm nhân viên theo department (`groupingBy`)

<details>
<summary>Gợi ý</summary>

```java
// 1. Tổng lương
double total = employees.stream().mapToDouble(Employee::salary).sum();

// 2. Lương TB theo department
Map<String, Double> avgByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));

// 3. Lương cao nhất
Employee highest = employees.stream()
    .max(Comparator.comparingDouble(Employee::salary))
    .orElseThrow();

// 6. Nhóm theo department
Map<String, List<Employee>> grouped = employees.stream()
    .collect(Collectors.groupingBy(Employee::department));
```
</details>

---

### D3. Optional — Xử lý null an toàn ⭐⭐

Viết methods trả về `Optional`:

```java
Optional<User> findUserByEmail(String email);
Optional<String> getPhoneNumber(User user);
Optional<Address> getAddress(User user);
```

Chain chúng lại để lấy thành phố của user từ email — xử lý mọi trường hợp null:

```java
String city = findUserByEmail("tri@email.com")
    .flatMap(this::getAddress)
    .map(Address::city)
    .orElse("Không rõ");
```

<details>
<summary>Gợi ý</summary>

```java
// ĐỪNG làm thế này:
if (user != null) {
    Address addr = user.getAddress();
    if (addr != null) {
        return addr.getCity();
    }
}
return "Không rõ";

// HÃY làm thế này:
return Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Không rõ");
```

**TypeScript tương đương:**
```typescript
const city = user?.address?.city ?? "Không rõ";
```
Java `Optional` dài hơn nhưng rõ ràng hơn. Spring Boot dùng Optional rất nhiều (ví dụ: `findById()` trả `Optional<Entity>`).
</details>

---

### D4-D10: Thêm 7 bài Stream nâng cao

**D4** ⭐⭐ — **reduce**: Tính tích tất cả số trong list. Nối list strings thành 1 chuỗi. Tìm max không dùng `max()`.

**D5** ⭐⭐ — **flatMap**: Cho `List<List<Integer>>`, gộp thành `List<Integer>` phẳng. Tương đương `arr.flat()` trong JS.

**D6** ⭐⭐ — **Collectors.toMap**: Chuyển `List<Employee>` thành `Map<String, Double>` (tên → lương). Xử lý trùng key.

**D7** ⭐⭐⭐ — **Parallel Streams**: So sánh performance `stream()` vs `parallelStream()` khi xử lý 1 triệu phần tử.

**D8** ⭐⭐ — **Custom Collector**: Viết collector tách list thành 2 phần: chẵn và lẻ (dùng `partitioningBy`).

**D9** ⭐⭐⭐ — **Stream Pipeline thực tế**: Đọc file CSV (1000 dòng), parse thành objects, filter, group, tính statistics, xuất report — toàn bộ bằng 1 stream pipeline.

**D10** ⭐⭐ — **Method References**: Refactor tất cả lambdas trong bài D2 thành method references nơi có thể. Giải thích 4 loại: `Class::staticMethod`, `obj::instanceMethod`, `Class::instanceMethod`, `Class::new`.

---

## Phần E: Concurrency & Virtual Threads (10 bài)

> **Tại sao quan trọng**: Backend xử lý nhiều requests đồng thời. Hiểu concurrency là bắt buộc cho Spring Boot developer. Virtual Threads (Java 21) là game-changer — viết blocking code đơn giản mà vẫn handle triệu requests.

### E1. Thread cơ bản — Chạy code song song ⭐

Tạo 3 threads, mỗi thread in tên + đếm từ 1-5 (mỗi lần sleep 100ms):

```java
// Output (thứ tự có thể khác nhau):
// Thread-A: 1
// Thread-B: 1
// Thread-C: 1
// Thread-A: 2
// Thread-B: 2
// ...
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: Implement Runnable (khuyên dùng)
Runnable task = () -> {
    for (int i = 1; i <= 5; i++) {
        System.out.println(Thread.currentThread().getName() + ": " + i);
        try { Thread.sleep(100); } catch (InterruptedException e) { break; }
    }
};

Thread t1 = new Thread(task, "Thread-A");
Thread t2 = new Thread(task, "Thread-B");
Thread t3 = new Thread(task, "Thread-C");
t1.start(); t2.start(); t3.start();
t1.join(); t2.join(); t3.join();  // Đợi tất cả xong
```

**Khác TypeScript:** JS là single-threaded (event loop). Java có true multi-threading — code THẬT SỰ chạy song song trên nhiều CPU cores.
</details>

---

### E2. ExecutorService — Thread Pool ⭐⭐

Dùng `ExecutorService` (thread pool) thay vì tạo threads thủ công:

```java
// Tạo pool 3 threads, submit 10 tasks
ExecutorService pool = Executors.newFixedThreadPool(3);
for (int i = 1; i <= 10; i++) {
    int taskId = i;
    pool.submit(() -> {
        System.out.println("Task " + taskId + " on " + Thread.currentThread().getName());
        Thread.sleep(200);
    });
}
pool.shutdown();
pool.awaitTermination(10, TimeUnit.SECONDS);
```

Bài tập: Tạo web scraper giả lập — fetch 20 URLs (mỗi URL sleep random 100-500ms), dùng pool 5 threads.

---

### E3. Virtual Threads — Triệu threads nhẹ (Java 21) ⭐⭐

So sánh Platform Threads vs Virtual Threads:

```java
// Platform threads: nặng ~1MB/thread, max ~10.000
Instant start = Instant.now();
try (var executor = Executors.newFixedThreadPool(200)) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> Thread.sleep(Duration.ofMillis(100)));
    }
}
System.out.println("Platform: " + Duration.between(start, Instant.now()));

// Virtual threads: nhẹ ~vài KB/thread, handle 100.000+
start = Instant.now();
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> Thread.sleep(Duration.ofMillis(100)));
    }
}
System.out.println("Virtual: " + Duration.between(start, Instant.now()));
```

Bài tập: Benchmark cả hai với 100.000 tasks. Ghi nhận thời gian và RAM usage.

<details>
<summary>Gợi ý</summary>

```java
// Tạo virtual thread trực tiếp
Thread.startVirtualThread(() -> {
    System.out.println("Running on: " + Thread.currentThread());
});

// Hoặc dùng builder
Thread.ofVirtual().name("vt-", 0).start(() -> {
    System.out.println("Virtual thread!");
});
```

**So sánh TypeScript:**
```typescript
// JS: single-threaded, async/await = cooperative scheduling
await Promise.all(urls.map(url => fetch(url)));

// Java 21: virtual threads = mỗi task 1 thread, nhưng nhẹ như Promise
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    urls.forEach(url -> executor.submit(() -> httpClient.send(url)));
}
```
</details>

---

### E4. CompletableFuture — Async như Promise ⭐⭐

Tương đương `Promise` trong JavaScript:

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchUserName(userId))      // ~ Promise.resolve()
    .thenApply(name -> name.toUpperCase())          // ~ .then()
    .thenCombine(
        CompletableFuture.supplyAsync(() -> fetchUserAge(userId)),
        (name, age) -> name + " (" + age + ")"     // ~ Promise.all()
    )
    .exceptionally(ex -> "Error: " + ex.getMessage()); // ~ .catch()

System.out.println(future.join());
```

Bài tập:
1. Fetch user info + user posts song song, combine kết quả
2. Implement timeout: nếu > 2s thì fallback
3. Chain 3 async operations: fetch → transform → save

---

### E5. Thread Safety — Shared Data ⭐⭐⭐

Tạo counter KHÔNG thread-safe, chứng minh race condition, rồi fix:

```java
// BUG: Race condition — kết quả không phải 100.000
int counter = 0;
var executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100_000; i++) {
    executor.submit(() -> counter++);  // Không atomic!
}
// Fix bằng: AtomicInteger, synchronized, ReentrantLock
```

Bài tập: Implement thread-safe `BankAccount` — 10 threads cùng deposit/withdraw, balance luôn đúng.

<details>
<summary>Gợi ý</summary>

```java
// Fix 1: AtomicInteger (đơn giản nhất)
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();

// Fix 2: ReentrantLock (khuyên dùng với Virtual Threads)
private final ReentrantLock lock = new ReentrantLock();
public void deposit(double amount) {
    lock.lock();
    try { balance += amount; }
    finally { lock.unlock(); }
}

// Fix 3: synchronized (TRÁNH với Virtual Threads — gây pinning)
public synchronized void deposit(double amount) {
    balance += amount;
}
```

**Quan trọng**: Với Virtual Threads, dùng `ReentrantLock` thay `synchronized` — vì `synchronized` sẽ "pin" carrier thread.
</details>

---

### E6-E10: 5 bài Concurrency nâng cao

**E6** ⭐⭐ — **ConcurrentHashMap**: Tạo thread-safe word counter — 5 threads đọc 5 files song song, đếm từ vào shared map.

**E7** ⭐⭐ — **Producer-Consumer**: Dùng `BlockingQueue` — 2 producer threads tạo tasks, 3 consumer threads xử lý.

### E8. Structured Concurrency — Quản lý concurrent tasks an toàn (Java 21+ Preview) ⭐⭐⭐

> **Preview feature**: Cần `--enable-preview` khi compile/run. Dự kiến finalize trong Java 25-26.
> **Tại sao quan trọng**: Virtual Threads giải quyết throughput, nhưng Structured Concurrency giải quyết **quản lý lifecycle** — cancel tasks khi 1 task fail, tránh resource leak.

```java
// Vấn đề: CompletableFuture không cancel task còn lại khi 1 task fail
// → resource leak, unnecessary work

// Giải pháp: StructuredTaskScope — tất cả subtasks thuộc về 1 scope
import java.util.concurrent.StructuredTaskScope;

record UserProfile(String user, List<String> posts, List<String> friends) {}

public UserProfile fetchProfile(Long userId) throws Exception {
    // ShutdownOnFailure: nếu 1 task fail → cancel tất cả tasks còn lại
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        // Fork subtasks — mỗi task chạy trên virtual thread
        var userTask = scope.fork(() -> userService.findById(userId));
        var postsTask = scope.fork(() -> postService.findByUser(userId));
        var friendsTask = scope.fork(() -> friendService.findByUser(userId));

        scope.join();            // Đợi tất cả xong (hoặc 1 fail)
        scope.throwIfFailed();   // Ném exception nếu có task fail

        // Tất cả thành công → lấy kết quả
        return new UserProfile(
            userTask.get(),
            postsTask.get(),
            friendsTask.get()
        );
    }
    // Scope tự cleanup: nếu 1 fail, các task khác bị cancel
}
```

**So sánh:**
```
CompletableFuture.allOf(cf1, cf2, cf3)     // Task fail → các task khác VẪN CHẠY (leak!)
StructuredTaskScope.ShutdownOnFailure()     // Task fail → cancel tất cả → clean!

Promise.all([p1, p2, p3])                  // JS: tương tự ShutdownOnFailure
Promise.allSettled([p1, p2, p3])            // JS: tương tự ShutdownOnSuccess (chờ hết)
```

Bài tập:
1. Fetch user info + posts + notifications song song, dùng `ShutdownOnFailure`
2. Thử `ShutdownOnSuccess` — lấy kết quả nhanh nhất (tương tự `Promise.race`)
3. Thêm timeout: `scope.joinUntil(Instant.now().plusSeconds(3))`

---

### E9. Scoped Values — Thay thế ThreadLocal cho Virtual Threads (Java 21+ Preview) ⭐⭐

> **Tại sao quan trọng**: `ThreadLocal` không hợp với Virtual Threads (1 triệu threads = 1 triệu copies). `ScopedValue` là lightweight alternative — immutable, tự cleanup.

```java
import java.lang.ScopedValue;

// Khai báo scoped value cho current user (thay ThreadLocal)
private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

// Bind value cho scope — tất cả code trong scope đều thấy
public void handleRequest(String userId) {
    ScopedValue.runWhere(CURRENT_USER, userId, () -> {
        // Tất cả methods gọi trong đây đều access được CURRENT_USER
        processOrder();
        sendNotification();
    });
    // Ra ngoài scope → value tự biến mất, không cần cleanup!
}

// Bất kỳ đâu trong call stack
private void processOrder() {
    String user = CURRENT_USER.get();  // "user-123" — không cần truyền parameter
    System.out.println("Processing order for: " + user);
}
```

**So sánh:**
```
ThreadLocal:                                ScopedValue:
- Mutable (set/get/remove)                 - Immutable (bind once per scope)
- Phải tự remove() → dễ leak              - Tự cleanup khi ra scope
- 1M threads = 1M copies                   - Shared, lightweight
- Không compose well                        - Kết hợp tốt với StructuredTaskScope
```

Bài tập:
1. Tạo `ScopedValue<RequestContext>` chứa userId + requestId, bind khi nhận request
2. Access từ service → repository layer mà không truyền qua parameters
3. So sánh memory usage: ThreadLocal vs ScopedValue với 100.000 virtual threads

---

**E10** ⭐⭐ — **Parallel Streams vs Virtual Threads**: So sánh performance xử lý 1 triệu items: `parallelStream()` vs virtual thread pool. Khi nào dùng cái nào?

**E11** ⭐⭐⭐ — **Mini Project: Concurrent Web Crawler**: Crawl website — BFS queue URLs, dùng virtual threads fetch song song, ConcurrentHashMap track visited URLs, giới hạn max depth = 3.

---

## Checklist theo dõi

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — Classes & Encapsulation | 5 + 5 mở rộng | ☐ |
| B — Inheritance & Polymorphism | 5 + 5 mở rộng | ☐ |
| C — Collections Framework | 5 + 5 mở rộng | ☐ |
| D — Stream API & Lambda | 3 + 7 mở rộng | ☐ |
| E — Concurrency & Virtual Threads | 5 + 6 mở rộng | ☐ |
| **Tổng** | **51 bài** | |
