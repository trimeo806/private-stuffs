# BÀI TẬP JAVA CƠ BẢN — Luyện Cú Pháp

**Mục tiêu**: Thực hành cú pháp Java cơ bản cho frontend developer đã biết TypeScript.
**Cách dùng**: Làm từ trên xuống. Mỗi bài có mức độ khó, input/output mẫu, và gợi ý.
**Quy ước mức độ**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Mục Lục

1. [Phần A: Biến, kiểu dữ liệu, toán tử (10 bài)](#phan-a)
2. [Phần B: Điều kiện & vòng lặp (10 bài)](#phan-b)
3. [Phần C: Chuỗi (String) (10 bài)](#phan-c)
4. [Phần D: Mảng (Array) & ArrayList (10 bài)](#phan-d)
5. [Phần E: Methods (Hàm) (10 bài)](#phan-e)
6. [Phần F: Exception Handling (5 bài)](#phan-f)
7. [Phần G: Mini Projects tổng hợp (5 bài)](#phan-g)

---

## Phần A: Biến, Kiểu Dữ Liệu, Toán Tử {#phan-a}

### A1. Khai báo và in biến ⭐

Tạo các biến sau và in ra console:
- Tên (String)
- Tuổi (int)
- Chiều cao mét (double)
- Đang là sinh viên (boolean)
- Điểm trung bình (float)

```
Output mẫu:
Tên: Nguyễn Thanh Trí
Tuổi: 25
Chiều cao: 1.72m
Sinh viên: true
Điểm TB: 8.5
```

<details>
<summary>Gợi ý</summary>

```java
String name = "Nguyễn Thanh Trí";
int age = 25;
double height = 1.72;
boolean isStudent = true;
float gpa = 8.5f;  // Chú ý: float cần hậu tố f

System.out.println("Tên: " + name);
// ... tiếp tục cho các biến khác
```

**TypeScript tương đương:**
```typescript
let name: string = "Nguyễn Thanh Trí";
let age: number = 25;
```
</details>

---

### A2. Chuyển đổi kiểu (Type Casting) ⭐

Cho biến `double price = 49.99;`:
1. Chuyển sang `int` (ép kiểu) và in kết quả
2. Cho biến `int quantity = 5;`, chuyển sang `double` và in
3. Cho biến `String numberStr = "123";`, chuyển sang `int` và in
4. Cho biến `int num = 65;`, chuyển sang `char` và in (ký tự ASCII)

```
Output mẫu:
int price: 49
double quantity: 5.0
Parsed number: 123
Ký tự ASCII 65: A
```

<details>
<summary>Gợi ý</summary>

```java
int intPrice = (int) price;                    // Ép kiểu (narrowing)
double doubleQty = (double) quantity;          // Tự động (widening)
int parsed = Integer.parseInt(numberStr);       // Parse string
char ch = (char) num;                          // int → char
```

**TypeScript tương đương:**
```typescript
let intPrice = Math.floor(price);        // JS không có ép kiểu trực tiếp
let parsed = parseInt(numberStr);
```
</details>

---

### A3. Toán tử số học ⭐

Viết chương trình nhận 2 số `int a = 17, b = 5;` và in ra:
- Tổng, hiệu, tích
- Chia lấy nguyên (`/`)
- Chia lấy dư (`%`)
- Chia thực (kết quả `double`)

```
Output mẫu:
a + b = 22
a - b = 12
a * b = 85
a / b = 3 (chia nguyên)
a % b = 2 (chia dư)
a / b = 3.4 (chia thực)
```

<details>
<summary>Gợi ý</summary>

```java
// Chia nguyên: cả 2 là int → kết quả int
System.out.println(a / b);          // 3

// Chia thực: cast 1 trong 2 sang double
System.out.println((double) a / b); // 3.4
```

**Khác TypeScript**: JS chỉ có 1 kiểu `number`, chia luôn ra số thực. Java phân biệt int/double.
</details>

---

### A4. Hoán đổi 2 biến ⭐

Cho `int x = 10, y = 20;`. Hoán đổi giá trị của x và y (x = 20, y = 10).

Làm 2 cách:
1. Dùng biến tạm (temp)
2. Không dùng biến tạm (dùng phép cộng/trừ)

```
Output mẫu:
Trước: x=10, y=20
Sau: x=20, y=10
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: biến tạm
int temp = x;
x = y;
y = temp;

// Cách 2: không biến tạm
x = x + y;  // x = 30
y = x - y;  // y = 10
x = x - y;  // x = 20
```
</details>

---

### A5. Tính diện tích & chu vi ⭐

Viết chương trình tính diện tích và chu vi cho:
1. Hình chữ nhật (dài = 8, rộng = 5)
2. Hình tròn (bán kính = 7)
3. Tam giác (đáy = 10, cao = 6)

```
Output mẫu:
--- Hình chữ nhật ---
Diện tích: 40
Chu vi: 26

--- Hình tròn ---
Diện tích: 153.94
Chu vi: 43.98

--- Tam giác ---
Diện tích: 30.0
```

<details>
<summary>Gợi ý</summary>

```java
// Dùng Math.PI cho số pi
double area = Math.PI * radius * radius;
double circumference = 2 * Math.PI * radius;

// Format số thập phân
System.out.printf("Diện tích: %.2f%n", area);  // 2 chữ số sau dấu phẩy
```
</details>

---

### A6. Chuyển đổi nhiệt độ ⭐

Viết chương trình chuyển đổi:
1. Celsius → Fahrenheit: `F = C × 9/5 + 32`
2. Fahrenheit → Celsius: `C = (F - 32) × 5/9`

Cho `celsius = 37.5` và `fahrenheit = 98.6`, tính và in kết quả.

```
Output mẫu:
37.5°C = 99.5°F
98.6°F = 37.0°C
```

<details>
<summary>Gợi ý</summary>

```java
double celsius = 37.5;
double toF = celsius * 9.0 / 5 + 32;  // Chú ý: 9.0 không phải 9 (tránh chia nguyên)

double fahrenheit = 98.6;
double toC = (fahrenheit - 32) * 5.0 / 9;
```
</details>

---

### A7. Tính tiền thanh toán ⭐⭐

Sản phẩm giá `245000` VNĐ, mua `3` cái, giảm giá `15%`, thuế VAT `10%`.
Tính: tổng trước giảm giá, số tiền giảm, tổng sau giảm giá, thuế, tổng thanh toán.

```
Output mẫu:
Tổng gốc:       735,000 VNĐ
Giảm giá (15%): -110,250 VNĐ
Sau giảm:        624,750 VNĐ
Thuế VAT (10%):  +62,475 VNĐ
Thanh toán:      687,225 VNĐ
```

<details>
<summary>Gợi ý</summary>

```java
long price = 245000;
int quantity = 3;
double discount = 0.15;
double vat = 0.10;

long subtotal = price * quantity;
double discountAmount = subtotal * discount;
double afterDiscount = subtotal - discountAmount;
double taxAmount = afterDiscount * vat;
double total = afterDiscount + taxAmount;

// Format số với dấu phẩy ngàn
System.out.printf("Tổng gốc: %,d VNĐ%n", subtotal);
```
</details>

---

### A8. Tách chữ số ⭐⭐

Cho số nguyên 3 chữ số `int number = 456;`. Tách và in ra từng chữ số (hàng trăm, chục, đơn vị).

Tính tổng các chữ số.

```
Output mẫu:
Số: 456
Hàng trăm: 4
Hàng chục: 5
Hàng đơn vị: 6
Tổng các chữ số: 15
```

<details>
<summary>Gợi ý</summary>

```java
int hundreds = number / 100;
int tens = (number / 10) % 10;  // hoặc (number % 100) / 10
int units = number % 10;
```
</details>

---

### A9. Toán tử so sánh & logic ⭐⭐

Cho `int a = 15, b = 20, c = 15;`. In kết quả (true/false) của:
1. `a == b`, `a == c`, `a != b`
2. `a > b`, `a >= c`, `b <= c`
3. `(a == c) && (b > a)` — cả hai đúng
4. `(a > b) || (a == c)` — ít nhất một đúng
5. `!(a == c)` — phủ định

Thêm: Giải thích sự khác biệt giữa `==` và `.equals()` cho String.

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");

// Dự đoán kết quả trước khi chạy:
System.out.println(s1 == s2);        // ?
System.out.println(s1 == s3);        // ?
System.out.println(s1.equals(s3));   // ?
```

<details>
<summary>Gợi ý</summary>

```java
s1 == s2     // true — cùng tham chiếu trong String pool
s1 == s3     // false — s3 là object mới trên heap
s1.equals(s3) // true — so sánh nội dung

// QUAN TRỌNG: Luôn dùng .equals() để so sánh String trong Java!
// Khác TypeScript: JS dùng === cho cả primitive và object string
```
</details>

---

### A10. Toán tử tăng/giảm & gán ⭐⭐

Dự đoán output của đoạn code sau (viết ra giấy trước khi chạy):

```java
int a = 5;
int b = a++;   // b = ? , a = ?
int c = ++a;   // c = ? , a = ?
int d = a--;   // d = ? , a = ?
int e = --a;   // e = ? , a = ?

System.out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d + " e=" + e);

// Phần 2: toán tử gán kết hợp
int x = 10;
x += 5;    // x = ?
x -= 3;    // x = ?
x *= 2;    // x = ?
x /= 4;   // x = ?
x %= 3;   // x = ?
```

<details>
<summary>Đáp án</summary>

```
a++ : gán trước, tăng sau → b=5, a=6
++a : tăng trước, gán sau → a=7, c=7
a-- : gán trước, giảm sau → d=7, a=6
--a : giảm trước, gán sau → a=5, e=5

Output: a=5 b=5 c=7 d=7 e=5

x += 5  → x = 15
x -= 3  → x = 12
x *= 2  → x = 24
x /= 4  → x = 6
x %= 3  → x = 0
```

**Khác TypeScript**: Cú pháp giống hệt, hành vi giống hệt. Cái này chuyển qua dễ.
</details>

---

## Phần B: Điều Kiện & Vòng Lặp {#phan-b}

### B1. Kiểm tra chẵn/lẻ ⭐

Cho `int number = 7;`. In ra số đó là chẵn hay lẻ.

Mở rộng: kiểm tra thêm số đó có chia hết cho 3 không, và có phải số âm không.

```
Output mẫu:
7 là số lẻ
7 không chia hết cho 3
7 là số dương
```

<details>
<summary>Gợi ý</summary>

```java
if (number % 2 == 0) {
    System.out.println(number + " là số chẵn");
} else {
    System.out.println(number + " là số lẻ");
}
```
</details>

---

### B2. Xếp loại học lực ⭐

Cho `double score = 7.5;`. Xếp loại:
- >= 9.0: Xuất sắc
- >= 8.0: Giỏi
- >= 6.5: Khá
- >= 5.0: Trung bình
- < 5.0: Yếu

```
Output mẫu:
Điểm: 7.5 → Xếp loại: Khá
```

<details>
<summary>Gợi ý</summary>

```java
String rank;
if (score >= 9.0) {
    rank = "Xuất sắc";
} else if (score >= 8.0) {
    rank = "Giỏi";
} // ... tiếp tục
```
</details>

---

### B3. Máy tính đơn giản ⭐⭐

Cho `double a = 10, b = 3;` và `char operator = '/';`.
Dùng `switch` để thực hiện phép tính và in kết quả.
Xử lý trường hợp chia cho 0 và toán tử không hợp lệ.

```
Output mẫu:
10.0 / 3.0 = 3.33
```

<details>
<summary>Gợi ý</summary>

```java
// Java 21: switch expression (arrow syntax)
String result = switch (operator) {
    case '+' -> a + " + " + b + " = " + (a + b);
    case '-' -> a + " - " + b + " = " + (a - b);
    case '*' -> a + " * " + b + " = " + (a * b);
    case '/' -> {
        if (b == 0) yield "Lỗi: chia cho 0!";
        yield a + " / " + b + " = " + String.format("%.2f", a / b);
    }
    default -> "Toán tử không hợp lệ: " + operator;
};

// Java cũ: switch statement
switch (operator) {
    case '+':
        System.out.println(a + b);
        break;
    // ...
}
```
</details>

---

### B4. Bảng cửu chương ⭐

In bảng cửu chương của số `n = 7` (từ 1 đến 10).

```
Output mẫu:
7 x 1 = 7
7 x 2 = 14
7 x 3 = 21
...
7 x 10 = 70
```

Mở rộng: In bảng cửu chương từ 2 đến 9 (lồng 2 vòng for).

<details>
<summary>Gợi ý</summary>

```java
int n = 7;
for (int i = 1; i <= 10; i++) {
    System.out.println(n + " x " + i + " = " + (n * i));
}

// Mở rộng: printf cho căn chỉnh đẹp
System.out.printf("%d x %2d = %3d%n", n, i, n * i);
```
</details>

---

### B5. Tổng & đếm ⭐

1. Tính tổng các số từ 1 đến 100
2. Tính tổng các số **chẵn** từ 1 đến 100
3. Đếm có bao nhiêu số chia hết cho 3 trong khoảng 1-100

```
Output mẫu:
Tổng 1→100: 5050
Tổng số chẵn: 2550
Số chia hết cho 3: 33
```

<details>
<summary>Gợi ý</summary>

```java
int sum = 0;
int evenSum = 0;
int countDiv3 = 0;

for (int i = 1; i <= 100; i++) {
    sum += i;
    if (i % 2 == 0) evenSum += i;
    if (i % 3 == 0) countDiv3++;
}
```
</details>

---

### B6. Số nguyên tố ⭐⭐

Kiểm tra số `n = 29` có phải số nguyên tố không.

Mở rộng: In tất cả số nguyên tố từ 2 đến 100.

```
Output mẫu:
29 là số nguyên tố

Số nguyên tố từ 2-100:
2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97
```

<details>
<summary>Gợi ý</summary>

```java
boolean isPrime = true;
if (n < 2) isPrime = false;
for (int i = 2; i <= Math.sqrt(n); i++) {  // Chỉ cần kiểm tra đến căn bậc 2
    if (n % i == 0) {
        isPrime = false;
        break;
    }
}
```
</details>

---

### B7. Dãy Fibonacci ⭐⭐

In ra `n = 15` số Fibonacci đầu tiên.

```
Output mẫu:
0 1 1 2 3 5 8 13 21 34 55 89 144 233 377
```

<details>
<summary>Gợi ý</summary>

```java
int n = 15;
long a = 0, b = 1;
System.out.print(a + " " + b);
for (int i = 2; i < n; i++) {
    long next = a + b;
    System.out.print(" " + next);
    a = b;
    b = next;
}
```

**Chú ý**: Dùng `long` thay vì `int` vì Fibonacci tăng rất nhanh.
</details>

---

### B8. Hình tam giác sao ⭐⭐

In các hình sau với `n = 5`:

```
Hình 1 (tam giác phải):      Hình 2 (tam giác trái):     Hình 3 (kim tự tháp):
*                                 *                            *
**                               **                           ***
***                             ***                          *****
****                           ****                         *******
*****                         *****                        *********
```

<details>
<summary>Gợi ý</summary>

```java
int n = 5;

// Hình 1: tam giác phải
for (int i = 1; i <= n; i++) {
    for (int j = 0; j < i; j++) {
        System.out.print("*");
    }
    System.out.println();
}

// Hình 2: tam giác trái — cần in khoảng trắng trước
for (int i = 1; i <= n; i++) {
    for (int j = 0; j < n - i; j++) System.out.print(" ");
    for (int j = 0; j < i; j++) System.out.print("*");
    System.out.println();
}

// Hình 3: kim tự tháp — khoảng trắng + số sao lẻ
for (int i = 1; i <= n; i++) {
    for (int j = 0; j < n - i; j++) System.out.print(" ");
    for (int j = 0; j < 2 * i - 1; j++) System.out.print("*");
    System.out.println();
}
```
</details>

---

### B9. Đoán số ⭐⭐

Tạo trò chơi đoán số:
1. Máy random 1 số từ 1-100
2. Người chơi nhập số (dùng `Scanner`)
3. Máy gợi ý "Lớn hơn" hoặc "Nhỏ hơn"
4. Đếm số lần đoán
5. Khi đoán đúng → in ra số lần đoán

```
Output mẫu:
Đoán số (1-100): 50
→ Nhỏ hơn!
Đoán số (1-100): 75
→ Lớn hơn!
Đoán số (1-100): 63
→ Chính xác! Bạn đoán đúng sau 3 lần.
```

<details>
<summary>Gợi ý</summary>

```java
import java.util.Scanner;
import java.util.Random;

Scanner scanner = new Scanner(System.in);
Random random = new Random();
int secret = random.nextInt(100) + 1;  // 1-100
int attempts = 0;

while (true) {
    System.out.print("Đoán số (1-100): ");
    int guess = scanner.nextInt();
    attempts++;

    if (guess < secret) {
        System.out.println("→ Lớn hơn!");
    } else if (guess > secret) {
        System.out.println("→ Nhỏ hơn!");
    } else {
        System.out.println("→ Chính xác! Bạn đoán đúng sau " + attempts + " lần.");
        break;
    }
}
scanner.close();
```

**Khác TypeScript**: Java cần `Scanner` để đọc input từ console. TypeScript dùng `readline` hoặc `prompt()`.
</details>

---

### B10. FizzBuzz ⭐

In các số từ 1 đến 50:
- Chia hết cho 3 → in "Fizz"
- Chia hết cho 5 → in "Buzz"
- Chia hết cho cả 3 và 5 → in "FizzBuzz"
- Còn lại → in số

```
Output mẫu:
1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz 16 ...
```

<details>
<summary>Gợi ý</summary>

```java
for (int i = 1; i <= 50; i++) {
    if (i % 15 == 0) System.out.print("FizzBuzz ");     // Kiểm tra 15 TRƯỚC
    else if (i % 3 == 0) System.out.print("Fizz ");
    else if (i % 5 == 0) System.out.print("Buzz ");
    else System.out.print(i + " ");
}
```

**Mẹo**: Kiểm tra chia hết 15 trước (3×5), vì nếu kiểm tra 3 trước sẽ bắt luôn trường hợp 15.
</details>

---

## Phần C: Chuỗi (String) {#phan-c}

### C1. Thao tác String cơ bản ⭐

Cho `String s = "  Hello, Java World!  ";`. Thực hiện và in:
1. Độ dài chuỗi
2. Chữ hoa, chữ thường
3. Cắt khoảng trắng đầu cuối (trim)
4. Lấy ký tự tại vị trí 7
5. Tìm vị trí từ "Java"
6. Thay "Java" bằng "Spring"
7. Kiểm tra bắt đầu bằng "Hello" (sau khi trim)
8. Cắt substring từ vị trí 2 đến 7

```
Output mẫu:
Độ dài: 22
Hoa: "  HELLO, JAVA WORLD!  "
Thường: "  hello, java world!  "
Trim: "Hello, Java World!"
Ký tự [7]: 'J'
Vị trí "Java": 8
Thay thế: "  Hello, Spring World!  "
Starts with "Hello": true
Substring(2,7): "llo, "
```

<details>
<summary>Gợi ý</summary>

```java
s.length()                  // Độ dài
s.toUpperCase()             // Chữ hoa
s.toLowerCase()             // Chữ thường
s.trim()                    // Cắt khoảng trắng — .strip() tốt hơn (Java 11+)
s.charAt(7)                 // Ký tự tại index
s.indexOf("Java")           // Tìm vị trí
s.replace("Java", "Spring") // Thay thế
s.trim().startsWith("Hello") // Bắt đầu bằng
s.substring(2, 7)           // Cắt chuỗi con [2, 7)
```

**Khác TypeScript**: Hầu hết giống, ngoại trừ:
- Java: `charAt()` trả về `char`, TS: `charAt()` trả về `string`
- Java: `substring(start, end)`, TS: `substring()` hoặc `slice()`
- Java: không có template literals, dùng `+` hoặc `String.format()`
</details>

---

### C2. Đếm nguyên âm và phụ âm ⭐⭐

Cho `String text = "Hello World";`. Đếm số nguyên âm (a, e, i, o, u), phụ âm, số, khoảng trắng, và ký tự khác.

```
Output mẫu:
Text: "Hello World"
Nguyên âm: 3
Phụ âm: 7
Số: 0
Khoảng trắng: 1
Khác: 0
```

<details>
<summary>Gợi ý</summary>

```java
String text = "Hello World";
int vowels = 0, consonants = 0, digits = 0, spaces = 0, others = 0;

for (int i = 0; i < text.length(); i++) {
    char ch = Character.toLowerCase(text.charAt(i));
    if ("aeiou".indexOf(ch) != -1) vowels++;
    else if (Character.isLetter(ch)) consonants++;
    else if (Character.isDigit(ch)) digits++;
    else if (ch == ' ') spaces++;
    else others++;
}
```

**Phương pháp khác — dùng for-each trên char array:**
```java
for (char ch : text.toLowerCase().toCharArray()) {
    // ...
}
```
</details>

---

### C3. Đảo ngược chuỗi ⭐

Cho `String s = "Hello Java";`. Đảo ngược chuỗi.

Làm 3 cách:
1. Dùng vòng lặp
2. Dùng `StringBuilder`
3. Dùng đệ quy (recursive)

```
Output mẫu:
Gốc: "Hello Java"
Đảo: "avaJ olleH"
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: vòng lặp
String reversed = "";
for (int i = s.length() - 1; i >= 0; i--) {
    reversed += s.charAt(i);
}

// Cách 2: StringBuilder (hiệu quả hơn)
String reversed2 = new StringBuilder(s).reverse().toString();

// Cách 3: đệ quy
public static String reverse(String s) {
    if (s.isEmpty()) return s;
    return reverse(s.substring(1)) + s.charAt(0);
}
```

**QUAN TRỌNG**: Đừng nối String bằng `+=` trong vòng lặp lớn! String trong Java là immutable, mỗi lần `+=` tạo object mới. Dùng `StringBuilder` thay thế.
</details>

---

### C4. Kiểm tra Palindrome ⭐⭐

Kiểm tra chuỗi có phải palindrome (đọc xuôi = đọc ngược) không.

Test cases: `"racecar"` → true, `"hello"` → false, `"A man a plan a canal Panama"` → true (bỏ qua khoảng trắng và hoa/thường).

```
Output mẫu:
"racecar" → Palindrome: true
"hello" → Palindrome: false
"A man a plan a canal Panama" → Palindrome: true
```

<details>
<summary>Gợi ý</summary>

```java
public static boolean isPalindrome(String s) {
    // Bỏ ký tự không phải chữ/số, đưa về thường
    String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    String reversed = new StringBuilder(cleaned).reverse().toString();
    return cleaned.equals(reversed);
}
```

**Cách 2 — so sánh 2 đầu:**
```java
public static boolean isPalindrome2(String s) {
    String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    int left = 0, right = cleaned.length() - 1;
    while (left < right) {
        if (cleaned.charAt(left) != cleaned.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}
```
</details>

---

### C5. Đếm từ trong câu ⭐

Cho `String sentence = "  Java  là   ngôn ngữ   lập trình  ";`. Đếm số từ (bỏ qua khoảng trắng thừa).

```
Output mẫu:
Câu: "  Java  là   ngôn ngữ   lập trình  "
Số từ: 6
Các từ: [Java, là, ngôn, ngữ, lập, trình]
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: split bằng regex
String[] words = sentence.trim().split("\\s+");  // \\s+ = 1 hoặc nhiều khoảng trắng
System.out.println("Số từ: " + words.length);

// In mảng:
System.out.println("Các từ: " + Arrays.toString(words));  // cần import java.util.Arrays
```

**Khác TypeScript:**
```typescript
// TS: gần giống
const words = sentence.trim().split(/\s+/);
```
</details>

---

### C6. Viết hoa chữ cái đầu mỗi từ ⭐⭐

Cho `String s = "hello world from java";`. Viết hoa chữ cái đầu mỗi từ → `"Hello World From Java"`.

```
Output mẫu:
Gốc: "hello world from java"
Title: "Hello World From Java"
```

<details>
<summary>Gợi ý</summary>

```java
String[] words = s.split(" ");
StringBuilder result = new StringBuilder();

for (String word : words) {
    if (!word.isEmpty()) {
        result.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1))
              .append(" ");
    }
}

String titleCase = result.toString().trim();
```
</details>

---

### C7. Nén chuỗi (Run-Length Encoding) ⭐⭐⭐

Nén chuỗi bằng cách đếm ký tự lặp liên tiếp.

```
Input:  "aaabbbccddddee"
Output: "a3b3c2d4e2"

Input:  "abcde"
Output: "abcde" (không nén được ngắn hơn → giữ nguyên)
```

<details>
<summary>Gợi ý</summary>

```java
public static String compress(String s) {
    StringBuilder sb = new StringBuilder();
    int count = 1;

    for (int i = 1; i < s.length(); i++) {
        if (s.charAt(i) == s.charAt(i - 1)) {
            count++;
        } else {
            sb.append(s.charAt(i - 1));
            if (count > 1) sb.append(count);
            count = 1;
        }
    }
    // Đừng quên ký tự cuối cùng
    sb.append(s.charAt(s.length() - 1));
    if (count > 1) sb.append(count);

    String compressed = sb.toString();
    return compressed.length() < s.length() ? compressed : s;
}
```
</details>

---

### C8. Kiểm tra Anagram ⭐⭐

Kiểm tra 2 chuỗi có phải anagram (cùng các ký tự, khác thứ tự) không.

```
"listen" & "silent" → true
"hello" & "world" → false
"Astronomer" & "Moon starer" → true (bỏ khoảng trắng, hoa/thường)
```

<details>
<summary>Gợi ý</summary>

```java
public static boolean isAnagram(String a, String b) {
    // Chuẩn hóa: bỏ khoảng trắng, lowercase
    char[] arr1 = a.replaceAll("\\s", "").toLowerCase().toCharArray();
    char[] arr2 = b.replaceAll("\\s", "").toLowerCase().toCharArray();

    // Sort rồi so sánh
    Arrays.sort(arr1);
    Arrays.sort(arr2);

    return Arrays.equals(arr1, arr2);
}
```
</details>

---

### C9. Tìm từ dài nhất ⭐⭐

Cho `String text = "Spring Boot framework provides powerful features";`.
Tìm từ dài nhất và vị trí của nó.

```
Output mẫu:
Từ dài nhất: "framework" (9 ký tự, vị trí 3)
```

<details>
<summary>Gợi ý</summary>

```java
String[] words = text.split("\\s+");
String longest = "";
int position = 0;

for (int i = 0; i < words.length; i++) {
    if (words[i].length() > longest.length()) {
        longest = words[i];
        position = i;
    }
}
```
</details>

---

### C10. StringBuilder Performance Test ⭐⭐

So sánh hiệu năng giữa nối String bằng `+` vs `StringBuilder` khi nối 100.000 chuỗi.

In thời gian chạy (ms) của mỗi cách.

```
Output mẫu:
String + : 3245 ms
StringBuilder: 2 ms
Nhanh hơn: 1622 lần!
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: String + (chậm)
long start1 = System.currentTimeMillis();
String s = "";
for (int i = 0; i < 100000; i++) {
    s += "a";  // Mỗi lần tạo String object mới!
}
long time1 = System.currentTimeMillis() - start1;

// Cách 2: StringBuilder (nhanh)
long start2 = System.currentTimeMillis();
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 100000; i++) {
    sb.append("a");  // Chỉ append vào buffer
}
String result = sb.toString();
long time2 = System.currentTimeMillis() - start2;

System.out.println("String + : " + time1 + " ms");
System.out.println("StringBuilder: " + time2 + " ms");
```

**BÀI HỌC QUAN TRỌNG**: String trong Java là **immutable**. Mỗi lần `+=` tạo object mới → O(n²). StringBuilder dùng mutable buffer → O(n). **Luôn dùng StringBuilder cho vòng lặp nối chuỗi**.
</details>

---

## Phần D: Mảng (Array) & ArrayList {#phan-d}

### D1. Thao tác mảng cơ bản ⭐

Tạo mảng `int[] scores = {85, 92, 78, 90, 88, 76, 95, 89};`. Thực hiện:
1. In mảng
2. Tìm giá trị lớn nhất, nhỏ nhất
3. Tính trung bình
4. Đếm số phần tử >= 85

```
Output mẫu:
Mảng: [85, 92, 78, 90, 88, 76, 95, 89]
Max: 95
Min: 76
Trung bình: 86.63
Số điểm >= 85: 5
```

<details>
<summary>Gợi ý</summary>

```java
int[] scores = {85, 92, 78, 90, 88, 76, 95, 89};

// In mảng (cần import)
System.out.println(Arrays.toString(scores));

// Tìm max/min
int max = scores[0], min = scores[0];
int sum = 0;
int count85 = 0;

for (int score : scores) {      // for-each — giống TS: for (const s of scores)
    if (score > max) max = score;
    if (score < min) min = score;
    sum += score;
    if (score >= 85) count85++;
}

double avg = (double) sum / scores.length;
```

**Khác TypeScript:**
```typescript
// TS: dùng Math.max/min với spread
const max = Math.max(...scores);
const avg = scores.reduce((a, b) => a + b) / scores.length;
// Java: phải viết vòng lặp (hoặc dùng Streams ở phần sau)
```
</details>

---

### D2. Sắp xếp mảng ⭐⭐

Cho `int[] arr = {64, 34, 25, 12, 22, 11, 90};`.

1. Sắp xếp tăng dần bằng **Bubble Sort** (tự viết, không dùng thư viện)
2. Sắp xếp bằng `Arrays.sort()`
3. In mảng trước và sau khi sắp xếp

```
Output mẫu:
Trước: [64, 34, 25, 12, 22, 11, 90]
Sau (Bubble): [11, 12, 22, 25, 34, 64, 90]
```

<details>
<summary>Gợi ý</summary>

```java
// Bubble Sort
int[] arr = {64, 34, 25, 12, 22, 11, 90};
int n = arr.length;

for (int i = 0; i < n - 1; i++) {
    for (int j = 0; j < n - i - 1; j++) {
        if (arr[j] > arr[j + 1]) {
            // Hoán đổi
            int temp = arr[j];
            arr[j] = arr[j + 1];
            arr[j + 1] = temp;
        }
    }
}

// Hoặc: Arrays.sort(arr);  — 1 dòng, O(n log n)
```
</details>

---

### D3. Tìm kiếm trong mảng ⭐⭐

Cho `int[] arr = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};` (đã sắp xếp).

1. **Linear Search**: tìm số 23 bằng vòng lặp
2. **Binary Search**: tìm số 23 bằng chia đôi (tự viết)
3. Dùng `Arrays.binarySearch()`

In vị trí tìm thấy và số lần so sánh.

<details>
<summary>Gợi ý</summary>

```java
// Binary Search
public static int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    int comparisons = 0;

    while (left <= right) {
        comparisons++;
        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            System.out.println("Tìm thấy sau " + comparisons + " lần so sánh");
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;  // Không tìm thấy
}
```
</details>

---

### D4. Xóa phần tử trùng ⭐⭐

Cho `int[] arr = {1, 3, 5, 3, 7, 1, 9, 5, 3};`. Loại bỏ phần tử trùng và in mảng kết quả.

```
Output mẫu:
Gốc: [1, 3, 5, 3, 7, 1, 9, 5, 3]
Sau: [1, 3, 5, 7, 9]
```

<details>
<summary>Gợi ý</summary>

```java
// Cách 1: dùng LinkedHashSet (giữ thứ tự)
import java.util.LinkedHashSet;
import java.util.Set;

int[] arr = {1, 3, 5, 3, 7, 1, 9, 5, 3};
Set<Integer> unique = new LinkedHashSet<>();
for (int num : arr) {
    unique.add(num);  // Set tự bỏ trùng
}
System.out.println(unique);  // [1, 3, 5, 7, 9]

// Cách 2: thuần mảng (không dùng Set)
// → Dùng 2 vòng lặp kiểm tra trùng
```

**TypeScript tương đương:**
```typescript
const unique = [...new Set(arr)];  // 1 dòng
```
</details>

---

### D5. ArrayList — Danh sách động ⭐

Tạo `ArrayList<String>` chứa danh sách trái cây. Thực hiện:
1. Thêm 5 loại trái cây
2. In danh sách và kích thước
3. Kiểm tra có chứa "Xoài" không
4. Xóa phần tử tại vị trí 2
5. Thay phần tử đầu tiên bằng "Sầu riêng"
6. Sắp xếp theo ABC
7. Duyệt và in bằng for-each

```
Output mẫu:
Trái cây: [Cam, Táo, Xoài, Chuối, Nho]
Kích thước: 5
Có Xoài: true
Sau xóa vị trí 2: [Cam, Táo, Chuối, Nho]
Sau thay thế: [Sầu riêng, Táo, Chuối, Nho]
Sắp xếp: [Chuối, Nho, Sầu riêng, Táo]
```

<details>
<summary>Gợi ý</summary>

```java
import java.util.ArrayList;
import java.util.Collections;

ArrayList<String> fruits = new ArrayList<>();
fruits.add("Cam");
fruits.add("Táo");
fruits.add("Xoài");
fruits.add("Chuối");
fruits.add("Nho");

System.out.println(fruits);             // [Cam, Táo, Xoài, Chuối, Nho]
System.out.println(fruits.size());      // 5
System.out.println(fruits.contains("Xoài")); // true
fruits.remove(2);                       // Xóa index 2
fruits.set(0, "Sầu riêng");            // Thay index 0
Collections.sort(fruits);              // Sắp xếp

for (String fruit : fruits) {
    System.out.println("- " + fruit);
}
```

**So sánh TypeScript:**
```typescript
const fruits: string[] = [];
fruits.push("Cam");          // → fruits.add("Cam")
fruits.includes("Xoài");    // → fruits.contains("Xoài")
fruits.splice(2, 1);        // → fruits.remove(2)
fruits[0] = "Sầu riêng";   // → fruits.set(0, "Sầu riêng")
fruits.sort();               // → Collections.sort(fruits)
```
</details>

---

### D6. Mảng 2 chiều — Bảng điểm ⭐⭐

Tạo mảng 2 chiều lưu điểm 3 môn của 4 sinh viên:

```java
double[][] scores = {
    {8.5, 7.0, 9.0},   // SV1: Toán, Lý, Hóa
    {6.5, 8.0, 7.5},   // SV2
    {9.0, 9.5, 8.5},   // SV3
    {7.0, 6.0, 7.0},   // SV4
};
```

1. In bảng điểm dạng table
2. Tính điểm trung bình mỗi sinh viên
3. Tìm sinh viên có điểm TB cao nhất
4. Tính điểm trung bình từng môn

```
Output mẫu:
     Toán   Lý   Hóa    TB
SV1:  8.5  7.0  9.0   8.17
SV2:  6.5  8.0  7.5   7.33
SV3:  9.0  9.5  8.5   9.00
SV4:  7.0  6.0  7.0   6.67
TB:   7.75 7.63 8.00

SV có điểm TB cao nhất: SV3 (9.00)
```

<details>
<summary>Gợi ý</summary>

```java
String[] subjects = {"Toán", "Lý", "Hóa"};

for (int i = 0; i < scores.length; i++) {
    double sum = 0;
    System.out.printf("SV%d:", i + 1);
    for (int j = 0; j < scores[i].length; j++) {
        System.out.printf("%6.1f", scores[i][j]);
        sum += scores[i][j];
    }
    double avg = sum / scores[i].length;
    System.out.printf("  %5.2f%n", avg);
}
```
</details>

---

### D7. Đảo ngược mảng ⭐

Cho `int[] arr = {1, 2, 3, 4, 5};`. Đảo ngược mảng **tại chỗ** (in-place, không tạo mảng mới).

```
Output: [5, 4, 3, 2, 1]
```

<details>
<summary>Gợi ý</summary>

```java
// Two pointers: hoán đổi đầu-cuối
int left = 0, right = arr.length - 1;
while (left < right) {
    int temp = arr[left];
    arr[left] = arr[right];
    arr[right] = temp;
    left++;
    right--;
}
```
</details>

---

### D8. Trộn 2 mảng đã sắp xếp ⭐⭐⭐

Cho 2 mảng đã sắp xếp:
```java
int[] a = {1, 3, 5, 7, 9};
int[] b = {2, 4, 6, 8, 10};
```

Trộn thành 1 mảng duy nhất vẫn sắp xếp, **không dùng sort**.

```
Output: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
```

<details>
<summary>Gợi ý</summary>

```java
int[] merged = new int[a.length + b.length];
int i = 0, j = 0, k = 0;

while (i < a.length && j < b.length) {
    if (a[i] <= b[j]) {
        merged[k++] = a[i++];
    } else {
        merged[k++] = b[j++];
    }
}

// Copy phần còn lại
while (i < a.length) merged[k++] = a[i++];
while (j < b.length) merged[k++] = b[j++];
```

**Đây là thuật toán Merge** — nền tảng của Merge Sort, O(n).
</details>

---

### D9. Ma trận xoắn ốc ⭐⭐⭐

In các phần tử của ma trận 3x3 theo chiều xoắn ốc (spiral order):

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
```

```
Output: 1 2 3 6 9 8 7 4 5
```

<details>
<summary>Gợi ý</summary>

```java
int top = 0, bottom = matrix.length - 1;
int left = 0, right = matrix[0].length - 1;

while (top <= bottom && left <= right) {
    // Đi phải
    for (int i = left; i <= right; i++) System.out.print(matrix[top][i] + " ");
    top++;
    // Đi xuống
    for (int i = top; i <= bottom; i++) System.out.print(matrix[i][right] + " ");
    right--;
    // Đi trái
    if (top <= bottom) {
        for (int i = right; i >= left; i--) System.out.print(matrix[bottom][i] + " ");
        bottom--;
    }
    // Đi lên
    if (left <= right) {
        for (int i = bottom; i >= top; i--) System.out.print(matrix[i][left] + " ");
        left++;
    }
}
```
</details>

---

### D10. ArrayList vs Array — Khi nào dùng cái nào ⭐

Viết bình luận giải thích khi nào dùng `int[]` và khi nào dùng `ArrayList<Integer>`, rồi viết đoạn code minh hoạ cả hai cho cùng 1 bài toán: lưu danh sách số nguyên, thêm phần tử, xóa phần tử, tìm kiếm.

<details>
<summary>Đáp án</summary>

```java
// === ARRAY: kích thước cố định ===
// Dùng khi: biết trước số phần tử, cần hiệu năng cao, kiểu primitive
int[] arr = new int[5];
arr[0] = 10;
arr[1] = 20;
// arr.length = 5 (không đổi được!)
// Không thể add/remove — phải tạo mảng mới

// === ARRAYLIST: kích thước động ===
// Dùng khi: không biết trước số phần tử, cần add/remove, cần methods tiện
ArrayList<Integer> list = new ArrayList<>();  // <Integer> không phải <int>!
list.add(10);
list.add(20);
list.add(30);
list.remove(1);           // Xóa index 1 → [10, 30]
list.contains(30);         // true
list.size();               // 2 (không phải .length!)

// QUAN TRỌNG: ArrayList chỉ chứa object (Integer, String...), không chứa primitive (int)
// Java tự autobox: int ↔ Integer
```

**TypeScript tương đương:**
```typescript
// TS chỉ có array dynamic — giống ArrayList
const arr: number[] = [];
arr.push(10);    // → list.add(10)
arr.splice(1,1); // → list.remove(1)
arr.length;      // → list.size()
```
</details>

---

## Phần E: Methods (Hàm) {#phan-e}

### E1. Method cơ bản ⭐

Viết các methods sau:
1. `greet(String name)` → in "Xin chào, {name}!"
2. `add(int a, int b)` → trả về tổng
3. `isEven(int n)` → trả về true nếu chẵn
4. `max(int a, int b, int c)` → trả về số lớn nhất

Gọi tất cả trong `main()`.

<details>
<summary>Gợi ý</summary>

```java
public class Methods {
    // void: không trả về gì
    public static void greet(String name) {
        System.out.println("Xin chào, " + name + "!");
    }

    // int: trả về số nguyên
    public static int add(int a, int b) {
        return a + b;
    }

    // boolean: trả về true/false
    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    public static int max(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    public static void main(String[] args) {
        greet("Trí");
        System.out.println(add(3, 5));
        System.out.println(isEven(4));
        System.out.println(max(3, 7, 5));
    }
}
```

**Khác TypeScript:**
- Java: phải khai báo return type (`int`, `void`, `boolean`)
- Java: `static` = gọi được không cần tạo object
- TS: function tự suy luận return type
</details>

---

### E2. Method Overloading ⭐⭐

Viết method `area()` tính diện tích với các tham số khác nhau (overloading):
1. `area(double radius)` — hình tròn
2. `area(double length, double width)` — hình chữ nhật
3. `area(double base, double height, boolean isTriangle)` — tam giác

```
Output mẫu:
Tròn (r=5): 78.54
Chữ nhật (8x5): 40.00
Tam giác (10x6): 30.00
```

<details>
<summary>Gợi ý</summary>

```java
public static double area(double radius) {
    return Math.PI * radius * radius;
}

public static double area(double length, double width) {
    return length * width;
}

public static double area(double base, double height, boolean isTriangle) {
    return 0.5 * base * height;
}

// Java chọn method dựa trên số lượng/kiểu tham số — TypeScript KHÔNG có overloading kiểu này
```

**Khác TypeScript**: TS dùng union types hoặc optional params thay vì overloading. Java overloading là feature cốt lõi.
</details>

---

### E3. Đệ quy — Giai thừa & Lũy thừa ⭐⭐

Viết methods đệ quy:
1. `factorial(int n)` → n! (5! = 120)
2. `power(int base, int exp)` → base^exp (2^10 = 1024)
3. `sumDigits(int n)` → tổng chữ số (1234 → 10)

<details>
<summary>Gợi ý</summary>

```java
public static long factorial(int n) {
    if (n <= 1) return 1;          // Base case
    return n * factorial(n - 1);   // Recursive case
}

public static long power(int base, int exp) {
    if (exp == 0) return 1;
    return base * power(base, exp - 1);
}

public static int sumDigits(int n) {
    if (n < 10) return n;
    return (n % 10) + sumDigits(n / 10);
}
```
</details>

---

### E4. Varargs — Số tham số không cố định ⭐⭐

Viết method `average(int... numbers)` nhận bất kỳ số lượng tham số nào và trả về trung bình.

```java
System.out.println(average(1, 2, 3));           // 2.0
System.out.println(average(10, 20, 30, 40, 50)); // 30.0
System.out.println(average(5));                  // 5.0
```

<details>
<summary>Gợi ý</summary>

```java
// int... = varargs — giống rest params trong TS (...numbers: number[])
public static double average(int... numbers) {
    int sum = 0;
    for (int n : numbers) {
        sum += n;
    }
    return (double) sum / numbers.length;
}
```

**TypeScript tương đương:**
```typescript
function average(...numbers: number[]): number {
    return numbers.reduce((a, b) => a + b) / numbers.length;
}
```
</details>

---

### E5. Callback giả lập — Functional Interface ⭐⭐⭐

Viết method `processArray(int[] arr, IntUnaryOperator transform)` nhận mảng và hàm biến đổi, trả về mảng mới.

```java
int[] numbers = {1, 2, 3, 4, 5};

int[] doubled = processArray(numbers, n -> n * 2);        // [2, 4, 6, 8, 10]
int[] squared = processArray(numbers, n -> n * n);        // [1, 4, 9, 16, 25]
int[] plusTen = processArray(numbers, n -> n + 10);       // [11, 12, 13, 14, 15]
```

<details>
<summary>Gợi ý</summary>

```java
import java.util.function.IntUnaryOperator;

public static int[] processArray(int[] arr, IntUnaryOperator transform) {
    int[] result = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
        result[i] = transform.applyAsInt(arr[i]);
    }
    return result;
}
```

**TypeScript tương đương:**
```typescript
function processArray(arr: number[], transform: (n: number) => number): number[] {
    return arr.map(transform);
}
const doubled = processArray([1,2,3], n => n * 2);
```

Java dùng `IntUnaryOperator` (functional interface) thay cho function type trong TS.
</details>

---

### E6. Viết method tiện ích cho mảng ⭐⭐

Tạo class `ArrayUtils` chứa các static methods:
1. `int sum(int[] arr)` — tổng
2. `double average(int[] arr)` — trung bình
3. `int[] reverse(int[] arr)` — đảo ngược (trả mảng mới)
4. `boolean contains(int[] arr, int target)` — kiểm tra chứa
5. `int indexOf(int[] arr, int target)` — vị trí (-1 nếu không có)
6. `String join(int[] arr, String separator)` — nối thành chuỗi

```java
int[] nums = {5, 3, 8, 1, 9};

ArrayUtils.sum(nums);                // 26
ArrayUtils.average(nums);            // 5.2
ArrayUtils.reverse(nums);            // [9, 1, 8, 3, 5]
ArrayUtils.contains(nums, 8);        // true
ArrayUtils.indexOf(nums, 1);         // 3
ArrayUtils.join(nums, " - ");        // "5 - 3 - 8 - 1 - 9"
```

<details>
<summary>Gợi ý</summary>

```java
public class ArrayUtils {
    public static int sum(int[] arr) {
        int total = 0;
        for (int n : arr) total += n;
        return total;
    }

    public static String join(int[] arr, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(sep);
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    // ... viết tiếp các methods còn lại
}
```
</details>

---

### E7. Pass by Value — Hiểu cách Java truyền tham số ⭐⭐

Dự đoán output (viết ra giấy trước khi chạy):

```java
public static void main(String[] args) {
    int x = 10;
    changeValue(x);
    System.out.println("x = " + x);        // x = ?

    int[] arr = {1, 2, 3};
    changeArray(arr);
    System.out.println(Arrays.toString(arr)); // arr = ?

    String s = "Hello";
    changeString(s);
    System.out.println("s = " + s);         // s = ?
}

static void changeValue(int n) { n = 99; }
static void changeArray(int[] a) { a[0] = 99; }
static void changeString(String str) { str = "World"; }
```

<details>
<summary>Đáp án</summary>

```
x = 10      ← primitive truyền bản sao, thay đổi trong method không ảnh hưởng ngoài
arr = [99, 2, 3]  ← object (mảng) truyền bản sao REFERENCE, cùng trỏ đến object → thay đổi được
s = "Hello"       ← String là immutable, tạo object mới trong method, reference cũ không đổi
```

**QUAN TRỌNG**: Java luôn **pass by value**. Nhưng với objects, value đó là **reference** (con trỏ). Nên object bên ngoài bị thay đổi, nhưng gán lại reference thì không ảnh hưởng.

**Khác TypeScript**: Hành vi giống hệt JS/TS. Primitives copy, objects pass reference.
</details>

---

### E8. Generic Method đơn giản ⭐⭐⭐

Viết generic method `<T> void printArray(T[] arr)` in được mảng của bất kỳ kiểu nào.

```java
String[] names = {"Java", "Spring", "Boot"};
Integer[] numbers = {1, 2, 3, 4, 5};
Double[] prices = {9.99, 19.99, 29.99};

printArray(names);    // [Java, Spring, Boot]
printArray(numbers);  // [1, 2, 3, 4, 5]
printArray(prices);   // [9.99, 19.99, 29.99]
```

<details>
<summary>Gợi ý</summary>

```java
public static <T> void printArray(T[] arr) {
    System.out.print("[");
    for (int i = 0; i < arr.length; i++) {
        System.out.print(arr[i]);
        if (i < arr.length - 1) System.out.print(", ");
    }
    System.out.println("]");
}
```

**TypeScript tương đương:**
```typescript
function printArray<T>(arr: T[]): void {
    console.log(`[${arr.join(", ")}]`);
}
```

Java generics dùng `<T>` giống TS, nhưng bị **type erasure** lúc runtime (kiểu biến mất → chỉ là Object).
</details>

---

### E9. Method chaining (Builder pattern đơn giản) ⭐⭐⭐

Tạo class `QueryBuilder` cho phép method chaining để xây SQL query:

```java
String sql = new QueryBuilder()
    .select("name", "email")
    .from("users")
    .where("age > 18")
    .orderBy("name")
    .limit(10)
    .build();

System.out.println(sql);
// Output: SELECT name, email FROM users WHERE age > 18 ORDER BY name LIMIT 10
```

<details>
<summary>Gợi ý</summary>

```java
public class QueryBuilder {
    private String selectClause = "*";
    private String fromClause = "";
    private String whereClause = "";
    private String orderByClause = "";
    private int limitValue = -1;

    public QueryBuilder select(String... columns) {
        this.selectClause = String.join(", ", columns);
        return this;  // Trả về this → cho phép chaining
    }

    public QueryBuilder from(String table) {
        this.fromClause = table;
        return this;
    }

    // ... viết tiếp where(), orderBy(), limit()

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(selectClause);
        sb.append(" FROM ").append(fromClause);
        if (!whereClause.isEmpty()) sb.append(" WHERE ").append(whereClause);
        if (!orderByClause.isEmpty()) sb.append(" ORDER BY ").append(orderByClause);
        if (limitValue > 0) sb.append(" LIMIT ").append(limitValue);
        return sb.toString();
    }
}
```

**Giống TypeScript**: Method chaining pattern giống hệt (jQuery, Lodash, Prisma query builder).
</details>

---

### E10. Lambda & Predicate — Lọc danh sách ⭐⭐⭐

Viết method `filter(List<Integer> list, Predicate<Integer> condition)` lọc danh sách theo điều kiện.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> evens = filter(numbers, n -> n % 2 == 0);       // [2, 4, 6, 8, 10]
List<Integer> greaterThan5 = filter(numbers, n -> n > 5);      // [6, 7, 8, 9, 10]
List<Integer> primes = filter(numbers, Main::isPrime);          // [2, 3, 5, 7] (method reference)
```

<details>
<summary>Gợi ý</summary>

```java
import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;

public static List<Integer> filter(List<Integer> list, Predicate<Integer> condition) {
    List<Integer> result = new ArrayList<>();
    for (int n : list) {
        if (condition.test(n)) {
            result.add(n);
        }
    }
    return result;
}

// Hoặc dùng Streams (cách Java hiện đại):
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
```

**TypeScript tương đương:**
```typescript
const evens = numbers.filter(n => n % 2 === 0);
// Java Streams gần giống Array.filter() của JS!
```
</details>

---

## Phần F: Exception Handling {#phan-f}

### F1. Try-Catch cơ bản ⭐

Viết chương trình:
1. Chia 2 số — bắt `ArithmeticException` khi chia cho 0
2. Truy cập mảng ngoài phạm vi — bắt `ArrayIndexOutOfBoundsException`
3. Parse chuỗi sang số — bắt `NumberFormatException`

```java
// Test cases:
divide(10, 0);              // → "Lỗi: không thể chia cho 0"
getElement(new int[]{1,2,3}, 5); // → "Lỗi: index 5 ngoài phạm vi"
parseNumber("abc");          // → "Lỗi: 'abc' không phải số"
```

<details>
<summary>Gợi ý</summary>

```java
public static int divide(int a, int b) {
    try {
        return a / b;
    } catch (ArithmeticException e) {
        System.out.println("Lỗi: không thể chia cho 0");
        return 0;
    }
}

// Nhiều catch blocks:
try {
    int[] arr = {1, 2, 3};
    System.out.println(arr[5]);
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Lỗi: " + e.getMessage());
} catch (Exception e) {  // Catch chung — luôn đặt CUỐI
    System.out.println("Lỗi không xác định: " + e);
}
```

**Khác TypeScript**: Java có **checked exceptions** — compiler bắt buộc bạn catch hoặc khai báo `throws`. TS/JS chỉ có unchecked.
</details>

---

### F2. Try-Catch-Finally & Try-with-Resources ⭐⭐

Viết chương trình đọc file text:
1. Cách cũ: try-catch-finally (đóng resource trong finally)
2. Cách mới: try-with-resources (tự động đóng)
3. Xử lý file không tồn tại

```java
// Tạo file test.txt với nội dung:
// Dòng 1
// Dòng 2
// Dòng 3

readFile("test.txt");       // → in 3 dòng
readFile("noexist.txt");    // → "File không tồn tại: noexist.txt"
```

<details>
<summary>Gợi ý</summary>

```java
import java.io.*;

// Cách mới: try-with-resources (Java 7+) — KHUYÊN DÙNG
public static void readFile(String filename) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    } catch (FileNotFoundException e) {
        System.out.println("File không tồn tại: " + filename);
    } catch (IOException e) {
        System.out.println("Lỗi đọc file: " + e.getMessage());
    }
    // Không cần finally — resource tự đóng!
}
```

**Khác TypeScript**: JS không có `try-with-resources`. Phải tự đóng resources hoặc dùng `using` (TC39 proposal).
</details>

---

### F3. Custom Exception ⭐⭐

Tạo exception tùy chỉnh cho hệ thống ngân hàng:
1. `InsufficientFundsException` — khi rút nhiều hơn số dư
2. `InvalidAmountException` — khi số tiền <= 0

```java
BankAccount account = new BankAccount("Trí", 1000000);
account.withdraw(500000);    // OK → Số dư: 500,000
account.withdraw(800000);    // → InsufficientFundsException: Số dư không đủ
account.withdraw(-100);      // → InvalidAmountException: Số tiền phải > 0
```

<details>
<summary>Gợi ý</summary>

```java
// Custom exception kế thừa Exception (checked) hoặc RuntimeException (unchecked)
public class InsufficientFundsException extends Exception {
    private final double balance;
    private final double amount;

    public InsufficientFundsException(double balance, double amount) {
        super(String.format("Số dư không đủ. Hiện có: %.0f, cần: %.0f", balance, amount));
        this.balance = balance;
        this.amount = amount;
    }
}

// Sử dụng trong method:
public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
    if (amount <= 0) throw new InvalidAmountException("Số tiền phải > 0");
    if (amount > balance) throw new InsufficientFundsException(balance, amount);
    balance -= amount;
}
```
</details>

---

### F4. Checked vs Unchecked Exceptions ⭐⭐

Giải thích sự khác biệt bằng code:

```java
// Bài tập: Phân loại các exception sau:
// IOException, NullPointerException, FileNotFoundException,
// ArithmeticException, ClassNotFoundException, ArrayIndexOutOfBoundsException,
// SQLException, IllegalArgumentException, NumberFormatException

// Checked (phải catch hoặc throws): ???
// Unchecked (RuntimeException, không bắt buộc catch): ???
```

Viết method minh họa mỗi loại.

<details>
<summary>Đáp án</summary>

```java
// CHECKED (compiler bắt buộc xử lý — kế thừa Exception):
// IOException, FileNotFoundException, ClassNotFoundException, SQLException
// → Phải try-catch HOẶC khai báo throws

public void readFile(String path) throws IOException {  // PHẢI khai báo throws
    FileReader fr = new FileReader(path);  // Nếu không catch → compile error
}

// UNCHECKED (không bắt buộc — kế thừa RuntimeException):
// NullPointerException, ArithmeticException, ArrayIndexOutOfBoundsException,
// IllegalArgumentException, NumberFormatException
// → Có thể catch hoặc không

public int divide(int a, int b) {  // Không cần throws
    return a / b;  // ArithmeticException nếu b=0, nhưng compiler không ép catch
}
```

**QUAN TRỌNG cho Spring Boot**: Hầu hết exceptions trong Spring là **unchecked** (RuntimeException). Nhưng khi làm việc với I/O, database (JDBC), bạn sẽ gặp checked exceptions.
</details>

---

### F5. Exception trong thực tế — Validate Input ⭐⭐⭐

Viết class `UserValidator` với method `validate(String name, String email, int age)`:
- Tên: không null, không rỗng, 2-50 ký tự
- Email: không null, phải chứa "@" và "."
- Tuổi: 0-150

Ném `IllegalArgumentException` khi không hợp lệ. Thu thập **tất cả** lỗi (không dừng ở lỗi đầu tiên).

```java
validate("", "invalid-email", -5);
// → IllegalArgumentException: [Tên không được rỗng, Email không hợp lệ, Tuổi phải từ 0-150]
```

<details>
<summary>Gợi ý</summary>

```java
public static void validate(String name, String email, int age) {
    List<String> errors = new ArrayList<>();

    if (name == null || name.trim().isEmpty()) {
        errors.add("Tên không được rỗng");
    } else if (name.length() < 2 || name.length() > 50) {
        errors.add("Tên phải từ 2-50 ký tự");
    }

    if (email == null || !email.contains("@") || !email.contains(".")) {
        errors.add("Email không hợp lệ");
    }

    if (age < 0 || age > 150) {
        errors.add("Tuổi phải từ 0-150");
    }

    if (!errors.isEmpty()) {
        throw new IllegalArgumentException(errors.toString());
    }
}
```

**Đây là pattern thực tế** — trong Spring Boot bạn sẽ dùng `@Valid` + Bean Validation thay vì validate thủ công, nhưng hiểu nguyên lý rất quan trọng.
</details>

---

## Phần G: Mini Projects Tổng Hợp {#phan-g}

### G1. Máy tính BMI ⭐

Viết chương trình tính chỉ số BMI:
- Nhập: cân nặng (kg) và chiều cao (m) từ bàn phím (Scanner)
- Tính: BMI = cân nặng / (chiều cao²)
- Phân loại: Gầy (<18.5), Bình thường (18.5-24.9), Thừa cân (25-29.9), Béo phì (>=30)

```
Output mẫu:
Nhập cân nặng (kg): 70
Nhập chiều cao (m): 1.75
BMI: 22.86
Phân loại: Bình thường
```

---

### G2. Quản lý danh bạ (Contact Manager) ⭐⭐

Viết chương trình console cho phép:
1. Thêm contact (tên + SĐT)
2. Xem tất cả contacts
3. Tìm contact theo tên
4. Xóa contact theo tên
5. Thoát

Dùng `ArrayList<String[]>` hoặc `HashMap<String, String>`.

```
Output mẫu:
=== QUẢN LÝ DANH BẠ ===
1. Thêm contact
2. Xem tất cả
3. Tìm kiếm
4. Xóa
5. Thoát
Chọn: 1
Tên: Nguyễn Văn A
SĐT: 0901234567
→ Đã thêm!
```

---

### G3. Hangman (Đoán từ) ⭐⭐

Trò chơi đoán từ:
- Máy chọn ngẫu nhiên 1 từ từ danh sách
- Hiện các ô trống `_ _ _ _ _`
- Người chơi đoán từng chữ cái
- Sai 6 lần → thua
- Đoán hết → thắng

```
Output mẫu:
Từ: _ _ _ _ _
Đoán: a
→ Đúng! _ a _ a _
Đoán: z
→ Sai! Còn 5 lượt.
...
```

---

### G4. Hệ thống ATM đơn giản ⭐⭐⭐

Viết chương trình mô phỏng ATM:
- Đăng nhập bằng PIN (3 lần thử)
- Xem số dư
- Rút tiền (kiểm tra số dư, chỉ cho rút bội số 50.000)
- Nạp tiền
- Chuyển khoản (tạo 2 tài khoản)
- In lịch sử giao dịch (lưu vào ArrayList)
- Đăng xuất

Dùng custom exceptions từ bài F3.

---

### G5. Quản lý học sinh — Tổng hợp tất cả ⭐⭐⭐

Viết chương trình quản lý học sinh với đầy đủ tính năng:

**Yêu cầu:**
- Class `Student` với: id, tên, ngày sinh, điểm toán/lý/hóa
- Methods: tính điểm TB, xếp loại, toString()
- Lưu danh sách trong `ArrayList<Student>`
- Menu console: Thêm, Sửa, Xóa, Tìm kiếm, Sắp xếp (theo tên/điểm), Thống kê
- Xử lý exception cho input không hợp lệ
- Ghi/đọc dữ liệu ra file text

**Bài tập này tổng hợp:**
- Variables & data types (Phần A)
- Control flow & loops (Phần B)
- String manipulation (Phần C)
- ArrayList (Phần D)
- Methods (Phần E)
- Exception handling (Phần F)

---

## Checklist theo dõi tiến độ

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — Biến, kiểu dữ liệu, toán tử | 10 bài | ☐ |
| B — Điều kiện & vòng lặp | 10 bài | ☐ |
| C — Chuỗi (String) | 10 bài | ☐ |
| D — Mảng & ArrayList | 10 bài | ☐ |
| E — Methods | 10 bài | ☐ |
| F — Exception Handling | 5 bài | ☐ |
| G — Mini Projects | 5 bài | ☐ |
| **Tổng** | **60 bài** | |

**Mục tiêu**: Hoàn thành tất cả trong **Tháng 1-2** của lộ trình học.
Sau khi xong 60 bài này, bạn đã sẵn sàng bước sang OOP (Tháng 2) và Database (Tháng 3).
