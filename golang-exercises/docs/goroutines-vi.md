# Goroutine trong Go: Hướng dẫn toàn diện

**Ngày**: 27-03-2026

---

## Mục lục

1. [Tại sao Goroutine xuất hiện](#1-tại-sao-goroutine-xuất-hiện)
2. [CSP - Nền tảng lý thuyết của Goroutine](#2-csp---nền-tảng-lý-thuyết-của-goroutine)
3. [Goroutine giải quyết vấn đề gì](#3-goroutine-giải-quyết-vấn-đề-gì)
4. [Cơ chế hoạt động bên trong của Goroutine](#4-cơ-chế-hoạt-động-bên-trong-của-goroutine)
5. [Những khía cạnh quan trọng cần chú ý](#5-những-khía-cạnh-quan-trọng-cần-chú-ý)
6. [Các mẫu đồng thời phổ biến](#6-các-mẫu-đồng-thời-phổ-biến)
7. [Ưu điểm](#7-ưu-điểm)
8. [Nhược điểm](#8-nhược-điểm)
9. [Rủi ro và bẫy](#9-rủi-ro-và-bẫy)
10. [So sánh Goroutine với các giải pháp khác](#10-so-sánh-goroutine-với-các-giải-pháp-khác)
11. [Cách sử dụng Goroutine hiệu quả](#11-cách-sử-dụng-goroutine-hiệu-quả)
12. [Ứng dụng thực tế của Goroutine - Ưu nhược điểm theo Use Case](#12-ứng-dụng-thực-tế-của-goroutine---ưu-nhược-điểm-theo-use-case)
13. [Nguồn tham khảo](#13-nguồn-tham-khảo)

---

## 1. Tại sao Goroutine xuất hiện

### 1.1 Khủng hoảng đồng thời (1990-2007)

Trước khi Go ra đời năm 2009, lập trình đồng thời (concurrent programming) đối mặt với nhiều thách thức mang tính hệ thống:

**Vấn đề với mô hình Thread:**
- **Chi phí bộ nhớ cố định**: Thread trong Java/C++ cần tối thiểu ~1MB stack cho mỗi thread
- **Lập lịch ở kernel**: Chuyển đổi ngữ cảnh (context switch) cần chuyển xuống kernel, lưu thanh ghi, ảnh hưởng TLB/cache
- **Trần lập lịch**: Hệ thống sản xuất chỉ chịu được 1.000-10.000 thread trước khi bị quá tải
- **Thiết kế cho đơn nhân**: Các công cụ đồng bộ (mutex, semaphore, monitor) kế thừa từ thời kỳ đơn luồng

**Vấn đề với mô hình Async (JavaScript, v.v.):**
- Callback lồng nhau tạo ra "kim tự tháp của địa ngục" (pyramid of doom) khó bảo trì
- Xử lý lỗi bị phân tán qua nhiều ngữ cảnh callback
- Nguy cơ rò rỉ tài nguyên từ chuỗi promise không được xử lý
- Khó suy luận về luồng điều khiển

**Hạn chế của ngôn ngữ bậc cao (Java):**
- Tạo thread cần giao tiếp với kernel, tốn kém
- Không có bộ lập lịch tác vụ nhẹ
- Đồng thời là chủ đề "nâng cao" (mutex, semaphore)
- Rủi ro deadlock từ các phương pháp đồng bộ cổ điển

### 1.2 Di sản từ Bell Labs (1978-2000)

Mô hình đồng thời của Go bắt nguồn từ hàng thập kỷ nghiên cứu:

**Lý thuyết CSP (1978)** - Tony Hoare:
- Các tiến trình giao tiếp chỉ thông qua truyền thông điệp (channel)
- Không chia sẻ trạng thái biến đổi ("bộ nhớ chia sẻ là xấu")
- Phát hiện deadlock thông qua phương pháp hình thức
- Hoare chứng minh rằng đồng thời dựa trên truyền thông điệp tương đương về mặt toán học với đồng thời dựa trên bộ nhớ chia sẻ, nhưng **dễ suy luận hơn đáng kể**

**Newsqueak (1988-1989)** - Rob Pike & Luca Cardelli:
- Ngôn ngữ đầu tiên coi channel là đối tượng hạng nhất
- Channel có thể lưu vào biến, truyền làm tham số hàm, gửi qua channel khác
- Chứng minh khái niệm: channel như cơ chế đồng thời thực tế

**Alef (đầu những năm 1990)**:
- Tinh chỉnh ngữ nghĩa channel cho lập trình hệ thống
- **Điểm yếu chí mạng**: Không có thu gom rác (quản lý bộ nhớ thủ công rất khó khăn)
- Được sử dụng cho phần mềm hệ thống thực tế tại Bell Labs

**Limbo (giữa những năm 1990)**:
- Có thu gom rác (giải quyết điểm yếu của Alef)
- Chứng minh đồng thời dựa trên CSP có thể mở rộng trong triển khai thực tế
- Lộ trình trực tiếp đến thiết kế của Go

### 1.3 Quyết định thiết kế của Go (2007)

**Bối cảnh**: CPU đa nhân trở nên phổ biến (2 nhân -> 4 nhân -> 8 nhân). Mô hình thread của Java vẫn giả định rằng chỉ có một nhân. C/C++ không có thư viện đồng thời tiêu chuẩn.

Những người tạo Go (Rob Pike, Ken Thompson, Robert Griesemer) đã biến đồng thời thành **tính năng hạng nhất**, không phải là ý nghĩ đến sau. Nhận định chính của họ: *"Đồng thời không phải là song song"* (Rob Pike, 2012). Đồng thời là tổ hợp các tính toán thực thi độc lập; song song là thực thi chúng đồng thời. Go cần đồng thời xuất sắc để cho phép song song hiệu quả trên đa nhân.

Go đặt cược toàn bộ thiết kế ngôn ngữ vào CSP: goroutine = tiến trình nhẹ, channel = truyền thông điệp, `select` = ghép kênh.

---

## 2. CSP - Nền tảng lý thuyết của Goroutine

### 2.1 CSP là gì?

**CSP (Communicating Sequential Processes)** là lý thuyết hình thức do **Tony Hoare** đề xuất năm 1978, được chính thức hóa trong cuốn sách năm 1985. Đây là nền tảng toán học đằng sau mô hình đồng thời của Go.

**Ý tưởng cốt lõi**: Các tiến trình độc lập giao tiếp với nhau **chỉ thông qua truyền thông điệp** (message passing), không thông qua bộ nhớ chia sẻ.

### 2.2 Ba trụ cột của CSP

#### Trụ cột 1: Tiến trình tuần tự (Sequential Processes)
Mỗi tiến trình tự nó là **tuần tự** — thực thi từng lệnh một từ trên xuống dưới. Không có gì đặc biệt về mỗi tiến trình riêng lẻ. Sự phức tạp chỉ xuất hiện khi chúng **tổ hợp** với nhau.

```go
// Mỗi goroutine là một tiến trình tuần tự
go func() {
    a := fetchData()     // Bước 1
    b := processData(a)  // Bước 2
    saveResult(b)        // Bước 3
}()
```

#### Trụ cột 2: Giao tiếp qua Channel (Communication)
Các tiến trình **không chia sẻ bộ nhớ**. Thay vào đó, chúng gửi và nhận thông điệp qua **channel** — đường ống có định kiểu dữ liệu.

```go
// Tiến trình A gửi dữ liệu cho Tiến trình B qua channel
ch := make(chan string)

go func() { ch <- "Xin chào từ A" }()  // A gửi
go func() { msg := <-ch }()             // B nhận
```

**Tại sao điều này quan trọng?**
- Không cần mutex, semaphore, hay volatile
- Không thể có data race trên dữ liệu được gửi qua channel (quyền sở hữu chuyển giao)
- Dễ suy luận hơn: "Dữ liệu di chuyển giữa các tiến trình" thay vì "nhiều tiến trình truy cập cùng một vùng nhớ"

#### Trụ cột 3: Đồng bộ hóa ẩm (Synchronization)
Trong CSP, giao tiếp **đồng thời là đồng bộ hóa**:
- Gửi lên unbuffered channel = chờ cho đến khi người nhận sẵn sàng
- Nhận từ unbuffered channel = chờ cho đến khi người gửi gửi dữ liệu
- Điều này tạo ra điểm đồng bộ tự nhiên — **không cần lock**

```go
ch := make(chan int)  // Unbuffered = đồng bộ hóa ẩm

go func() {
    result := tinhToan()
    ch <- result  // Chờ cho đến khi main() nhận
}()

value := <-ch  // Chờ cho đến khi goroutine gửi
// Tại đây, đảm bảo result đã được tính xong
```

### 2.3 CSP so với Bộ nhớ chia sẻ (Shared Memory)

| Khía cạnh | CSP (Channel) | Bộ nhớ chia sẻ (Mutex) |
|-----------|---------------|----------------------|
| **Triết lý** | "Chia sẻ bộ nhớ bằng giao tiếp" | "Giao tiếp bằng chia sẻ bộ nhớ" |
| **Dữ liệu** | Chuyển giao quyền sở hữu qua channel | Nhiều goroutine truy cập cùng dữ liệu |
| **Đồng bộ** | Ẩm (qua gửi/nhận) | Tường minh (Lock/Unlock) |
| **Dễ suy luận** | Có (luồng dữ liệu rõ ràng) | Khó (phải theo dõi mọi nơi truy cập) |
| **Deadlock** | Có thể (channel chờ nhau) | Có thể (khóa chờ nhau) |
| **Hiệu suất** | Tốt cho I/O, truyền thông điệp | Tốt cho bộ đệm chia sẻ, counter |
| **Debug** | Dễ theo dõi luồng dữ liệu | Khó phát hiện data race |

### 2.4 Các phép toán CSP trong Go

| Phép toán CSP | Go tương ứng | Mô tả |
|---------------|-------------|-------|
| **Process** | `go func()` | Tiến trình tuần tự độc lập |
| **Channel** | `make(chan T)` | Kênh giao tiếp có kiểu |
| **Send** | `ch <- value` | Gửi thông điệp |
| **Receive** | `<-ch` | Nhận thông điệp |
| **Choice** | `select { case... }` | Chọn giữa nhiều kênh |
| **Guard** | `case <-ctx.Done():` | Điều kiện để chọn nhánh |
| **Parallel** | Nhiều lệnh `go` | Chạy song song |

### 2.5 Ví dụ thực tế: CSP giải quyết vấn đề của bộ nhớ chia sẻ

**Vấn đề: Counter an toàn đồng thời**

**Cách bộ nhớ chia sẻ (dễ gặp lỗi):**
```go
var counter int
var mu sync.Mutex

// 1000 goroutine tăng counter
for i := 0; i < 1000; i++ {
    go func() {
        mu.Lock()         // Phải nhớ lock
        counter++
        mu.Unlock()       // Phải nhớ unlock
    }()
    // Quên lock/unlock = data race
    // Quên unlock = deadlock
}
```

**Cách CSP (an toàn tự nhiên):**
```go
counter := make(chan int)
result := make(chan int)

// Một goroutine sở hữu dữ liệu — không ai khác truy cập
go func() {
    count := 0
    for range counter {
        count++
    }
    result <- count
}()

// 1000 goroutine gửi tín hiệu tăng
for i := 0; i < 1000; i++ {
    go func() { counter <- 1 }()
}
close(counter)

fmt.Println(<-result)  // Luôn đúng, không cần mutex
```

### 2.6 Khi nào dùng CSP (Channel) vs Shared Memory (Mutex)

| Sử dụng Channel khi | Sử dụng Mutex khi |
|---------------------|-------------------|
| Truyền dữ liệu giữa goroutine | Bảo vệ trạng thái nội bộ của struct |
| Phối hợp luồng công việc (pipeline) | Counter/gauge đơn giản |
| Báo hiệu sự kiện (done, cancel) | Cache đọc nhiều, ghi ít (RWMutex) |
| Giới hạn đồng thời (semaphore) | Thao tác nguyên tử (atomic) |
| Fan-out/fan-in | Trạng thái chia sẻ trong phạm vi nhỏ |

**Nguyên tắc vàng**: Bắt đầu với channel. Chỉ chuyển sang mutex khi channel làm code phức tạp hơn cần thiết hoặc khi đo hiệu suất cho thấy cần.

### 2.7 Hạn chế của CSP

- **Không phải mọi bài toán đều phù hợp**: Counter đơn giản với mutex nhanh hơn với channel
- **Deadlock vẫn có thể xảy ra**: Channel chờ nhau theo vòng = deadlock
- **Hiệu suất**: Channel có overhead từ scheduling; mutex + atomic nhanh hơn cho shared state đơn giản
- **Đường cong học tập**: Cần thay đổi tư duy từ "dữ liệu ở đâu" sang "dữ liệu đi đâu"

---

## 3. Goroutine giải quyết vấn đề gì

### 3.1 Chi phí Thread quá cao

| Khía cạnh | OS Thread | Goroutine |
|-----------|-----------|-----------|
| **Kích thước stack** | ~1 MB (cố định) | 2-8 KB (tăng động) |
| **Bộ nhớ cho 1 triệu đơn vị** | ~1 TB (bất khả thi) | ~2-3 GB (khả thi) |
| **Thời gian tạo** | ~1-5 mili giây | ~200-500 nano giây |
| **Chuyển đổi ngữ cảnh** | ~1-20 us (xuyên qua kernel) | ~100 ns (chỉ trong user-space) |
| **Số lượng đồng thời tối đa** | ~1.000-10.000 | ~1.000.000+ |

### 3.2 Địa ngục Callback (Pyramid of Doom)

**Vấn đề (JavaScript/Node.js):**
```javascript
request(url1, (err, res1) => {
  if (err) handle(err);
  request(url2, (err, res2) => {
    if (err) handle(err);
    request(url3, (err, res3) => {
      // ... lồng 10 tầng
      // Xử lý lỗi bị phân tán
    });
  });
});
```

**Giải pháp của Go (trông đồng bộ, đồng thời bên dưới):**
```go
ch1 := make(chan Result)
ch2 := make(chan Result)
ch3 := make(chan Result)

go func() { ch1 <- fetch(url1) }()
go func() { ch2 <- fetch(url2) }()
go func() { ch3 <- fetch(url3) }()

res1, res2, res3 := <-ch1, <-ch2, <-ch3
// Luồng điều khiển tuyến tính, xử lý lỗi rõ ràng
```

### 3.3 API Threading phức tạp

**Truyền thống (Java, C++):**
- Thread là tài nguyên khan hiếm; phải tạo pool (ThreadPoolExecutor)
- Đồng bộ thông qua monitor, lock, semaphore, volatile
- Khả năng hiển thị bộ nhớ quản lý tường minh (volatile, synchronized)
- Có thể bị deadlock với nhiều lock

**Mô hình của Go:**
- Goroutine rẻ; tạo thoải mái
- Đồng bộ thông qua truyền thông điệp (channel)
- Phát hiện race condition tích hợp trong `go test -race`
- Mô hình bộ nhớ đơn giản: *"Đừng giao tiếp bằng chia sẻ bộ nhớ; hãy chia sẻ bộ nhớ bằng giao tiếp"*

### 3.4 Tiếp cận đồng thời dễ dàng hơn

Trước Go: Đồng thời là chủ đề "chuyên gia" — cần hiểu pthreads, futexes, rào bộ nhớ, ThreadLocal, volatile, synchronized.

Sau Go: Đồng thời là hạng nhất và dễ tiếp cận:
```go
go func() { /* công việc đồng thời */ }()  // Khởi tạo
ch <- value                                 // Gửi
result := <-ch                              // Nhận
```

Các mẫu (pipeline, fan-out/fan-in, ghép kênh) có thể dạy trong vài giờ, không phải vài tuần.

---

## 4. Cơ chế hoạt động bên trong của Goroutine

### 4.1 Mô hình Scheduler GMP

Scheduler của Go ghép M goroutine lên N OS thread sử dụng mô hình GMP:

#### G (Goroutine)
- "Green thread" nhẹ trong user-space
- Trạng thái tối thiểu: con trỏ chương trình, con trỏ stack, thanh ghi
- Kích thước mô tả: ~200-400 byte (cộng thêm stack)
- Không cấp phát tài nguyên kernel cho mỗi goroutine
- Stack: 2-8 KB ban đầu, tăng động

#### M (Machine = OS Thread)
- Thread cấp hệ điều hành thực sự (pthreads trên Linux, Windows thread trên Windows)
- Thực thi lệnh cho goroutine
- Không có P thì không thể chạy mã Go; ngồi chờ
- Khi một goroutine bị chặn, M có thể được tái sử dụng cho G khác
- Số lượng M thường <= số P + thread đang chờ

#### P (Processor = Ngữ cảnh lập lịch)
- Không phải là nhân CPU; không phải là thread
- Ngữ cảnh lập lịch: chứa hàng đợi cục bộ, bộ cấp phát bộ nhớ, profiler
- Số P = `GOMAXPROCS` (mặc định: số nhân CPU)
- Mỗi P chạy tối đa một M tại một thời điểm
- M phải có được P để thực thi mã Go

**Quá trình ghép kênh:**
```
Hàng đợi toàn cục (chia sẻ, được hỏi 1/61 chu kỳ lập lịch)
    |
Hàng đợi cục bộ (theo P, tối đa 256 goroutine mỗi hàng)
    |
Liên kết M:P (M lấy P, lấy G từ hàng đợi)
    |
Thực thi CPU
```

### 4.2 Quản lý Stack của Goroutine

**Stack ban đầu**: 2 KB (từ Go 1.4; trước đó là 4 KB)

**Cách stack tăng trưởng:**
1. Kiểm tra tăng trưởng stack được chèn vào đầu hàm: `if SP < limit { call runtime.morestack() }`
2. Runtime cấp phát stack lớn hơn từ heap (gấp đôi: 2KB -> 4KB -> 8KB -> ...)
3. Nội dung stack cũ được sao chép sang stack mới
4. Con trỏ trong stack được sao chép được điều chỉnh
5. Stack cũ được giải phóng về heap
6. Goroutine tiếp tục với stack mới, lớn hơn

**Nhận định quan trọng**: Hầu hết goroutine không bao giờ tăng quá 2-8 KB, dẫn đến tiết kiệm bộ nhớ 100-500 lần so với Java thread.

### 4.3 Scheduler Work-Stealing

Scheduler của Go sử dụng work-stealing (giới thiệu trong Go 1.1 bởi Dmitry Vyukov):

**Quyết định lập lịch (P tìm G tiếp theo):**
1. Lấy từ hàng đợi cục bộ của chính nó (rẻ nhất, thân thiện với cache)
2. Nếu trống, hỏi hàng đợi toàn cục (1/61 thời gian để tránh tranh chấp)
3. Nếu toàn cục cũng trống, lấy nửa hàng đợi của một P ngẫu nhiên
4. Nếu tất cả P đều trống, hỏi I/O mạng (epoll/kqueue/IOCP)
5. Nếu không có gì, ngủ

**Ví dụ Work Stealing:**
```
P0: [G1 G2 G3 G4 G5] (5 goroutine)
P1: []                (trống - đang thiếu việc)

P1 lấy nửa hàng đợi của P0:
P0: [G1 G2]           (giữ lại 2)
P1: [G3 G4 G5]        (lấy 3)
```

Không có khóa scheduler trung tâm; mỗi P hoạt động độc lập. Mở rộng được đến hàng ngàn bộ xử lý.

### 4.4 Lập lịch Preemptive (Go 1.14+)

**Trước Go 1.14 (Hợp tác):**
- Preemption chỉ tại điểm an toàn (gọi hàm, thao tác channel, cấp phát bộ nhớ)
- Vòng lặp bận (`for {}`) có thể bóc đột các goroutine khác vô thời hạn

**Sau Go 1.14 (Preemptive):**
- Goroutine `sysmon`: chạy không cần P, giám sát tất cả P liên tục
- Mỗi ~10ms: kiểm tra xem G nào đã chạy > 10ms
- Cơ chế preemption:
  1. sysmon đặt cờ preempt trên G
  2. Gửi tín hiệu SIGURG đến M (OS thread)
  3. Bộ xử lý tín hiệu buộc G dừng lại
  4. G nhường; G tiếp theo trong hàng đợi chạy
- Kết quả: Phân phối CPU công bằng ngay cả với vòng lặp tính toán nặng

### 4.5 Tích hợp I/O Mạng

- Tất cả thao tác mạng sử dụng epoll (Linux), kqueue (BSD), IOCP (Windows)
- Khi goroutine gọi `net.Dial()`, OS thread không bị chặn
- Socket được thêm vào epoll, goroutine được xếp đậu, P được gán lại cho M khác
- Khi dữ liệu mạng đến, goroutine quay lại hàng đợi chạy
- **1 triệu kết nối đồng thời không cần 1 triệu thread**

### 4.6 Xử lý Syscall

- Nếu goroutine bị chặn bởi syscall (ví dụ: I/O đĩa), M bị chặn
- Scheduler phát hiện M bị chặn, gán lại P cho M rảnh
- M gốc tiếp tục syscall; khi xong, lấy lại P hoặc dừng
- Không mất thông lượng; các P khác tiếp tục chạy các G khác

---

## 5. Những khía cạnh quan trọng cần chú ý

### 5.1 Vòng đời Goroutine

| Giai đoạn | Mô tả | Ghi chú |
|-----------|-------|---------|
| **Tạo** | `go func() { }()` | Chạy ngay trong nền (không chặn) |
| **Thực thi** | Goroutine chạy đến khi hoàn thành hoặc bị chặn | Scheduler ghép kênh qua các OS thread |
| **Bị chặn** | Chờ channel, I/O, mutex, v.v. | Các goroutine khác tiếp tục trên cùng OS thread |
| **Kết thúc** | Hàm trả về hoặc panic lan truyền | Dọn dẹp tài nguyên là quan trọng |

**Các trạng thái**: Runnable -> Running -> Blocked -> Dead

### 5.2 Giao tiếp qua Channel

#### Channel không đệm (Unbuffered)
```go
ch := make(chan int)  // dung lượng 0
// Người gửi bị chặn cho đến khi người nhận sẵn sàng
// Người nhận bị chặn cho đến khi người gửi có giá trị
// Tạo một "bắt tay" - cả hai phải có mặt đồng thời
```

#### Channel có đệm (Buffered)
```go
ch := make(chan int, 10)  // dung lượng 10
// Gửi chỉ bị chặn khi bộ đệm đầy
// Nhận chỉ bị chặn khi bộ đệm trống
// Tách rời thời gian của người gửi và người nhận
```

#### Channel có hướng (Directional)
```go
func producer(ch chan<- int) { ch <- 42 }    // Chỉ gửi
func consumer(ch <-chan int) { val := <-ch }  // Chỉ nhận
// An toàn kiểu: trình biên dịch ngăn chặn sử dụng sai
```

#### Ngữ nghĩa đóng Channel
- **Người gửi đóng, người nhận không đóng** (quy ước)
- Nhận từ channel đã đóng trả về giá trị không (zero value)
- **Gửi đến channel đã đóng gây PANIC**
- Kiểm tra: `val, ok := <-ch` (ok == false nghĩa là đã đóng)

### 5.3 Các Primitive đồng bộ

| Primitive | Mục đích | Khi nào sử dụng |
|-----------|----------|-----------------|
| `sync.WaitGroup` | Chờ N goroutine hoàn thành | Semaphore đếm |
| `sync.Mutex` | Khóa độc quyền (một người giữ) | Bảo vệ dữ liệu chia sẻ |
| `sync.RWMutex` | Khóa đọc/ghi | Nhiều đọc, ít ghi |
| `sync.Once` | Thực thi một lần, an toàn luồng | Khởi tạo singleton |
| `sync.Map` | Map an toàn đồng thời | Cache ghi một lần, khóa tách biệt |
| `sync.Pool` | Pool tái sử dụng đối tượng | Giảm áp lực GC |

**Mẫu WaitGroup (phổ biến nhất):**
```go
var wg sync.WaitGroup
for i := 0; i < 10; i++ {
    wg.Add(1)
    go func() {
        defer wg.Done()
        // Làm việc
    }()
}
wg.Wait()  // Chặn cho đến khi tất cả Done()
```

**Mẫu Mutex:**
```go
var mu sync.Mutex
var counter int

mu.Lock()
defer mu.Unlock()
counter++
```

### 5.4 Lệnh Select

Ghép kênh qua nhiều thao tác channel:
```go
select {
case msg := <-ch1:
    fmt.Println("Nhận từ ch1:", msg)
case ch2 <- value:
    fmt.Println("Gửi đến ch2")
case <-time.After(5 * time.Second):
    fmt.Println("Hết thời gian!")
case <-ctx.Done():
    fmt.Println("Đã hủy:", ctx.Err())
default:
    fmt.Println("Không có channel sẵn sàng (không chặn)")
}
```

### 5.5 Package Context

Truyền tín hiệu hủy, thời gian chờ, thời hạn và giá trị phạm vi yêu cầu:

| Hàm | Mục đích |
|-----|----------|
| `context.Background()` | Context gốc (không bao giờ bị hủy) |
| `context.WithCancel(parent)` | Hủy thủ công |
| `context.WithTimeout(parent, duration)` | Tự động hủy sau khoảng thời gian |
| `context.WithDeadline(parent, time)` | Tự động hủy tại thời điểm cụ thể |
| `context.WithValue(parent, key, value)` | Đính kèm dữ liệu phạm vi yêu cầu |

```go
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()  // LUÔN defer cancel

go func() {
    select {
    case <-ctx.Done():
        fmt.Println("Đã hủy:", ctx.Err())
        return
    case result := <-doWork():
        fmt.Println("Kết quả:", result)
    }
}()
```

**Thực hành tốt nhất:**
- Truyền context làm tham số đầu tiên
- Luôn defer cancel()
- Kiểm tra `ctx.Done()` trong vòng lặp dài
- Không lưu context trong struct
- Truyền context cho goroutine con

### 5.6 Xử lý lỗi trong Goroutine

**Vấn đề**: Một panic trong goroutine làm sập toàn bộ chương trình.

**Giải pháp 1: Recover trong mỗi goroutine**
```go
go func() {
    defer func() {
        if r := recover(); r != nil {
            log.Printf("Đã phục hồi: %v", r)
        }
    }()
    congViecRuiRo()
}()
```

**Giải pháp 2: Sử dụng errgroup**
```go
eg, ctx := errgroup.WithContext(context.Background())

eg.Go(func() error { return tacVu1(ctx) })
eg.Go(func() error { return tacVu2(ctx) })

if err := eg.Wait(); err != nil {
    log.Fatal(err)  // Lỗi đầu tiên hủy tất cả các goroutine khác
}
```

### 5.7 Rò rỉ Goroutine

Goroutine không bao giờ thoát (bị chặn mãi mãi), tiêu thụ bộ nhớ vô thời hạn.

**Nguyên nhân phổ biến:**
- Nhận từ channel bị chặn mà không có người gửi
- Vòng lặp vô hạn không có điều kiện thoát
- Quên hủy context
- I/O không có thời gian chờ

**Phát hiện:**
```go
fmt.Println(runtime.NumGoroutine())  // Theo dõi theo thời gian
// Nên ổn định, không tăng không giới hạn
```

**Phòng ngừa:**
- Luôn sử dụng context để hủy
- Đóng channel từ phía người gửi
- Sử dụng giới hạn đồng thời (worker pool)
- Kiểm tra với `runtime.NumGoroutine()` trước và sau

---

## 6. Các mẫu đồng thời phổ biến

### 6.1 Fan-Out / Fan-In

**Fan-Out**: Phân phối công việc cho nhiều worker. **Fan-In**: Gộp kết quả lại.

```go
// Fan-Out: phân phối tác vụ cho worker
tasks := make(chan int, len(input))
results := make(chan int, len(input))

// Khởi động worker
for i := 0; i < numWorkers; i++ {
    go func() {
        for task := range tasks {
            results <- process(task)
        }
    }()
}

// Gửi tác vụ
for _, v := range input {
    tasks <- v
}
close(tasks)

// Fan-In: thu thập kết quả
for i := 0; i < len(input); i++ {
    output = append(output, <-results)
}
```

### 6.2 Worker Pool

N worker cố định lấy từ hàng đợi tác vụ. Ngăn tạo goroutine không giới hạn.

```go
func WorkerPool(numWorkers int, jobs <-chan Job) {
    var wg sync.WaitGroup
    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for job := range jobs {
                job.Execute()
            }
        }()
    }
    wg.Wait()
}
```

### 6.3 Pipeline

Các giai đoạn được nối với nhau bởi channel. Mỗi giai đoạn xử lý đồng thời.

```go
// Giai đoạn 1: Tạo số
numbers := make(chan int)
go func() {
    defer close(numbers)
    for i := 1; i <= 100; i++ {
        numbers <- i
    }
}()

// Giai đoạn 2: Lọc (số chẵn)
evens := make(chan int)
go func() {
    defer close(evens)
    for n := range numbers {
        if n%2 == 0 {
            evens <- n
        }
    }
}()

// Giai đoạn 3: Biến đổi (bình phương)
squares := make(chan int)
go func() {
    defer close(squares)
    for n := range evens {
        squares <- n * n
    }
}()

// Tiêu thụ
for s := range squares {
    fmt.Println(s)
}
```

### 6.4 Generator

Hàm trả về channel tạo giá trị theo kiểu lazy.

```go
func Fibonacci(n int) <-chan int {
    out := make(chan int)
    go func() {
        defer close(out)
        a, b := 0, 1
        for i := 0; i < n; i++ {
            out <- a
            a, b = b, a+b
        }
    }()
    return out
}

for val := range Fibonacci(10) {
    fmt.Println(val)
}
```

### 6.5 Semaphore (Giới hạn tốc độ)

Giới hạn goroutine đồng thời bằng buffered channel.

```go
sem := make(chan struct{}, 5)  // Tối đa 5 đồng thời

for i := 0; i < 100; i++ {
    sem <- struct{}{}  // Lấy (chặn nếu đã có 5 đang chạy)
    go func(id int) {
        defer func() { <-sem }()  // Trả
        lamViec(id)
    }(i)
}
```

### 6.6 Tắt máy chủ an toàn (Graceful Shutdown)

```go
ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
defer stop()

server := &http.Server{Addr: ":8080"}

go func() {
    <-ctx.Done()
    shutdownCtx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()
    server.Shutdown(shutdownCtx)
}()

server.ListenAndServe()
```

---

## 7. Ưu điểm

### 7.1 Cực kỳ nhẹ
- Stack ban đầu: 2-8 KB (so với 1-8 MB cho OS thread) -- nhỏ hơn 500-4000 lần
- 1 triệu goroutine ~ 2-3 GB RAM
- 10.000 OS thread ~ 10-20 GB RAM

### 7.2 Khả năng mở rộng lớn
- Hàng triệu goroutine trong một tiến trình duy nhất
- Kubernetes: độ trễ API dưới 1 giây với 100K+ container
- Fasthttp: 25,8 triệu request/giây

### 7.3 Cú pháp đơn giản
- `go func(){}()` -- một từ khóa, không cần thư viện thread pool, không cần executor service

### 7.4 Scheduler tích hợp
- Mô hình GMP xử lý mọi thứ: ghép kênh, cân bằng tải, preemption
- Không cần thư viện bên ngoài (khác với ThreadPoolExecutor của Java)

### 7.5 Giao tiếp dựa trên Channel
- "Chia sẻ bộ nhớ bằng giao tiếp" -- an toàn hơn bộ nhớ chia sẻ
- Channel có kiểu, có hướng
- Select tích hợp để ghép kênh

### 7.6 Vòng đời được Garbage Collect
- Không cần quản lý vòng đời thread thủ công
- GC dọn dẹp khi goroutine thoát

### 7.7 Tạo và hủy nhanh
- Tạo: ~1 micro giây (so với ~1 mili giây cho OS thread)
- Có thể tạo một goroutine cho mỗi request

### 7.8 Lập lịch Preemptive (Go 1.14+)
- sysmon phát hiện goroutine chạy >10ms
- Preemption dựa trên SIGURG đảm bảo công bằng
- Không còn deadlock scheduler từ vòng lặp bận

---

## 8. Nhược điểm

### 8.1 Không có giá trị trả về trực tiếp
Goroutine không trả về giá trị. Phải sử dụng channel hoặc trạng thái chia sẻ:
```go
// Không thể: result := go compute()
// Phải làm:
ch := make(chan int)
go func() { ch <- compute() }()
result := <-ch
```

### 8.2 Độ phức tạp khi debug
- Kết xuất goroutine có thể dài hàng triệu dòng
- Không có ID goroutine tích hợp (theo thiết kế)
- Stack trace phức tạp trong mã đồng thời
- Kết xuất race detector có thể nhiễu nhiều

### 8.3 Không thể buộc dừng Goroutine
Phải thiết kế goroutine để lắng nghe tín hiệu hủy:
```go
// Không có goroutine.Kill()
// Phải sử dụng hủy context:
ctx, cancel := context.WithCancel(context.Background())
go func() {
    for {
        select {
        case <-ctx.Done():
            return  // Thoát đúng cách
        default:
            lamViec()
        }
    }
}()
cancel()  // Báo hiệu goroutine dừng
```

### 8.4 Nguy cơ rò rỉ Goroutine
Goroutine bị chặn không bao giờ thoát tích lũy bộ nhớ theo thời gian, cuối cùng gây OOM.

### 8.5 Đường cong học tập
Ngữ nghĩa channel, lệnh select, quyền sở hữu (ai đóng?), suy luận deadlock đòi hỏi sự chuyển đổi tư duy từ threading truyền thống.

### 8.6 Không có lập lịch ưu tiên
GMP đối xử với tất cả goroutine như nhau. Không có cách tích hợp để ưu tiên goroutine nào đó.

---

## 9. Rủi ro và bẫy

### 9.1 Race Condition (Điều kiện cạnh tranh)

Đọc/ghi đồng thời vào bộ nhớ chia sẻ mà không đồng bộ:
```go
// LỖI: data race
var counter int
for i := 0; i < 1000; i++ {
    go func() { counter++ }()  // Ghi không đồng bộ
}
```

**Phát hiện**: `go run -race ./main.go` hoặc `go test -race ./...`

**Phòng ngừa**: sync.Mutex, sync/atomic, channel

### 9.2 Rò rỉ Goroutine

```go
// RÒ RỈ: goroutine bị chặn mãi mãi (không ai đọc từ ch)
ch := make(chan int)
go func() { ch <- 42 }()
// ... quên nhận từ ch
```

**Phòng ngừa**: Luôn cung cấp điều kiện thoát, sử dụng context.WithCancel/WithTimeout, theo dõi `runtime.NumGoroutine()`

### 9.3 Deadlock (Khóa chết)

Hai hoặc nhiều goroutine chờ nhau, không ai tiến được:
```go
// DEADLOCK: phụ thuộc vòng
ch1 := make(chan int)
ch2 := make(chan int)

go func() { ch1 <- <-ch2 }()  // Chờ ch2
go func() { ch2 <- <-ch1 }()  // Chờ ch1
// Cả hai bị chặn mãi mãi
```

**Phòng ngừa**: Thứ tự khóa nhất quán, tránh khóa lồng nhau, sử dụng thời gian chờ, sử dụng context

### 9.4 Sử dụng Channel sai

| Thao tác | Kết quả |
|----------|---------|
| Gửi đến channel đã đóng | **PANIC** |
| Nhận từ channel nil | Chặn mãi mãi |
| Gửi đến channel nil | Chặn mãi mãi |
| Nhận từ channel đã đóng | Trả về giá trị không (zero value) |

### 9.5 Lỗi bắt biến đóng (Closure Variable Capture)

```go
// LỖI (trước Go 1.22): in "5" năm lần
for i := 0; i < 5; i++ {
    go func() {
        fmt.Println(i)  // Bắt biến, không phải giá trị
    }()
}
```

**Giải pháp:**
1. Truyền làm tham số: `go func(i int) { fmt.Println(i) }(i)`
2. Sao chép: `i := i; go func() { fmt.Println(i) }()`
3. Sử dụng Go 1.22+ (biến tự động theo mỗi lần lặp)

### 9.6 Bóc đột (Starvation)

Goroutine sử dụng nhiều CPU chiếm hết CPU trước khi những goroutine khác được chạy. Được giảm nhẹ bởi preemption của Go 1.14+ (ngưỡng 10ms), nhưng công việc nặng CPU nên sử dụng `runtime.Gosched()` nếu sự công bằng là quan trọng.

### 9.7 Vi phạm Happens-Before

Không có đồng bộ, việc ghi trong một goroutine có thể không hiển thị cho những goroutine khác:
```go
var ready bool
var data string

go func() {
    data = "hello"
    ready = true  // Không đảm bảo goroutine khác nhìn thấy
}()

// SỬA: sử dụng channel, mutex, hoặc atomic
```

### 9.8 Lan truyền Panic

Một panic không được recover trong bất kỳ goroutine nào sẽ làm sập toàn bộ chương trình. Luôn sử dụng `defer recover()` trong các goroutine có thể panic.

---

## 10. So sánh Goroutine với các giải pháp khác

### 10.1 So với OS Thread

| Khía cạnh | Goroutine | OS Thread |
|-----------|-----------|-----------|
| Bộ nhớ | 2-8 KB | 1-8 MB |
| Tạo | ~1 us | ~1 ms |
| Chuyển đổi ngữ cảnh | ~100 ns | ~1000+ ns |
| Đồng thời tối đa | 1M+ | 1-10K |
| Scheduler | User-space (Go runtime) | Kernel |

**Kết luận**: Goroutine vượt trội cho đồng thời cao. OS thread tốt hơn cho công việc nặng CPU hoặc tương tác C.

### 10.2 So với Java Virtual Thread (Project Loom)

| Khía cạnh | Goroutine | Java Virtual Thread |
|-----------|-----------|---------------------|
| Bộ nhớ | ~2 KB | ~300 byte |
| Đồng thời tối đa | ~1M | ~1M |
| Có từ | Go 1.0 (2012) | Java 21 (2023) |
| Mô hình đồng bộ | Channel (CSP) | Lock/atomic (bộ nhớ chia sẻ) |
| Đường cong học | Channel là khác biệt | Mô hình thread quen thuộc |

**Khác biệt chính**: Goroutine khuyến khích truyền thông điệp (channel); virtual thread hỗ trợ mô hình thread quen thuộc (bộ nhớ chia sẻ với lock).

### 10.3 So với Async/Await (Node.js, Python, Rust)

| Khía cạnh | Goroutine | Async/Await |
|-----------|-----------|-------------|
| Mô hình | Preemptive (M:N) | Hợp tác (event loop) |
| Cú pháp | `go func()` (trông đồng bộ) | `async/await` (tường minh) |
| Sử dụng CPU | Tốt hơn (preemption) | Có thể bị tắc với công việc CPU |
| Stack trace | Rõ ràng (đồng bộ) | Bị phân mảnh |
| Đa nhân | Tự nhiên | Cần worker/thread |

**Kết luận**: Goroutine đơn giản hơn cho developer (mã trông đồng bộ). Async/await tường minh hơn nhưng có nguy cơ phức tạp callback.

### 10.4 Hiệu suất thực tế

| Chỉ số | Giá trị |
|--------|---------|
| Twitch | Giảm 40-50% CPU với goroutine |
| Giảm độ trễ | 800ms -> 150ms (5 lần) tại tải đỉnh |
| Tăng thông lượng | +300% trong microservice |
| Fasthttp | 25,8 triệu request/giây |
| Kubernetes | Độ trễ API dưới 1 giây với 100K+ container |
| Chuyển đổi ngữ cảnh | ~100ns (goroutine) so với ~1000ns+ (OS thread) |

---

## 11. Cách sử dụng Goroutine hiệu quả

### 11.1 Khi nào sử dụng Goroutine

| Kịch bản | Sử dụng Goroutine? | Tại sao |
|----------|---------------------|---------|
| Công việc I/O (HTTP, DB, file) | Có | Một goroutine chờ I/O trong khi những goroutine khác chạy |
| Nhiều nguồn I/O | Có | Ghép kênh với channel |
| Tác vụ nền (ghi log, dọn dẹp) | Có | Không chặn |
| Tác vụ định kỳ (polling, heartbeat) | Có | Chạy trong nền |
| Tính toán nặng CPU | Có thể | Chỉ lợi khi `GOMAXPROCS > 1` |
| Logic tuần tự đơn giản | Không | Chi phí không cần thiết |

### 11.2 Kích thước Worker Pool

| Kiểu công việc | Kích thước khuyến nghị |
|----------------|----------------------|
| I/O | Số kết nối đồng thời + 10% |
| CPU | `runtime.NumCPU()` |
| Hỗn hợp | Bắt đầu với `NumCPU() * 2`, điều chỉnh |

### 11.3 Thực hành tốt với Channel

1. **Người gửi đóng, người nhận không đóng** -- luôn luôn
2. **Sử dụng channel có hướng trong chữ ký hàm** -- an toàn kiểu
3. **Ưu tiên `range` để nhận** -- tự động break khi đóng
4. **Chỉ dùng buffer khi biết kích thước tối đa** -- không đệm an toàn hơn
5. **Không bao giờ gửi đến channel đã đóng** -- sẽ gây panic

### 11.4 Quản lý bộ nhớ

| Mối quan tâm | Chiến lược |
|--------------|-----------|
| Chi phí stack | Bắt đầu 2KB, tăng khi cần; không đáng kể |
| Áp lực GC | Sử dụng `sync.Pool` cho cấp phát tần suất cao |
| Dữ liệu lớn | Truyền con trỏ để tránh sao chép |
| Phòng chống rò rỉ | Hủy context, giới hạn đồng thời |

### 11.5 Profiling và giám sát

```go
// Đếm goroutine đơn giản:
fmt.Println("Goroutine:", runtime.NumGoroutine())

// Profile chi tiết với pprof:
import _ "net/http/pprof"
go http.ListenAndServe("localhost:6060", nil)
// Truy cập: http://localhost:6060/debug/pprof/goroutine
```

```bash
# Profiling dòng lệnh:
go tool pprof http://localhost:6060/debug/pprof/goroutine
go tool pprof http://localhost:6060/debug/pprof/heap
```

### 11.6 Kiểm thử tính đúng đắn

```bash
# Luôn chạy với race detector khi phát triển:
go test -race ./...
go run -race ./main.go
```

```go
// Kiểm thử rò rỉ goroutine:
func TestKhongRoRi(t *testing.T) {
    before := runtime.NumGoroutine()
    lamViec()
    time.Sleep(100 * time.Millisecond)
    after := runtime.NumGoroutine()
    if after > before {
        t.Errorf("Rò rỉ: %d trước, %d sau", before, after)
    }
}
```

### 11.7 Ma trận quyết định: Chọn Primitive nào?

| Cần gì | Sử dụng |
|--------|---------|
| Chờ N goroutine | `sync.WaitGroup` |
| Bảo vệ dữ liệu chia sẻ | `sync.Mutex` |
| Nhiều đọc, ít ghi | `sync.RWMutex` |
| Khởi tạo một lần | `sync.Once` |
| Map an toàn đồng thời | `sync.Map` |
| Giảm áp lực GC | `sync.Pool` |
| Phối hợp goroutine | Channel |
| Lỗi từ nhóm goroutine | `errgroup.Group` |
| Hủy/thời gian chờ | `context.Context` |

---

## 12. Ứng dụng thực tế của Goroutine - Ưu nhược điểm theo Use Case

### 12.1 Web Server / HTTP Handler (mỗi request một goroutine)

Mô hình phổ biến nhất: `net/http` mặc định tạo một goroutine cho mỗi kết nối đến.

```go
func main() {
    mux := http.NewServeMux()

    mux.HandleFunc("/api/users", func(w http.ResponseWriter, r *http.Request) {
        ctx := r.Context()

        // Mỗi request tự động chạy trong goroutine riêng
        user, err := fetchUser(ctx, r.URL.Query().Get("id"))
        if err != nil {
            http.Error(w, err.Error(), http.StatusInternalServerError)
            return
        }

        json.NewEncoder(w).Encode(user)
    })

    server := &http.Server{
        Addr:         ":8080",
        Handler:      mux,
        ReadTimeout:  5 * time.Second,
        WriteTimeout: 10 * time.Second,
        IdleTimeout:  120 * time.Second,
    }

    log.Fatal(server.ListenAndServe())
}
```

**Ưu điểm:**
- Tự nhiên và đơn giản -- `net/http` xử lý goroutine cho bạn
- Scale tốt đến hàng trăm ngàn kết nối đồng thời
- Mã trông đồng bộ, dễ đọc và bảo trì

**Nhược điểm:**
- Cần giới hạn số lượng connection (dùng `Server.MaxHeaderBytes`, reverse proxy)
- Goroutine leak nếu client chậm hoặc không đóng kết nối -- cần `ReadTimeout`/`WriteTimeout`
- Không có giới hạn goroutine mặc định -- server có thể tạo quá nhiều goroutine dưới tải cao

---

### 12.2 Microservice / gRPC Service

**Ưu điểm:**
- Xử lý hàng ngàn RPC đồng thời, mỗi RPC một goroutine
- Fan-out gọi nhiều service downstream song song, giảm latency tổng
- Context propagation tự nhiên qua gRPC metadata

**Nhược điểm:**
- Cần context propagation cẩn thận -- quên truyền context = mất tracing, mất timeout
- Distributed tracing phức tạp -- cần OpenTelemetry hoặc Jaeger
- Circuit breaker và retry logic cần thư viện bên ngoài (ví dụ: `go-kit`, `grpc-middleware`)

---

### 12.3 Real-time Chat / WebSocket

**Ưu điểm:**
- Một goroutine per connection rất hiệu quả -- hàng trăm ngàn kết nối đồng thời
- Channel phù hợp tự nhiên cho broadcast message đến nhiều client
- Mô hình "đọc một goroutine, ghi một goroutine" per connection rất sạch

**Nhược điểm:**
- Quản lý connection lifecycle phức tạp -- cần ping/pong, reconnect logic
- Memory tăng khi nhiều connection idle -- cần monitor `runtime.NumGoroutine()`
- Broadcast đến N client = N lần ghi channel, cần fan-out cẩn thận

---

### 12.4 Data Pipeline / ETL

Pipeline pattern rất tự nhiên với goroutine và channel:

```go
func pipeline(ctx context.Context, input <-chan RawData) <-chan CleanData {
    // Stage 1: Validate
    validated := make(chan RawData, 100)
    go func() {
        defer close(validated)
        for data := range input {
            if data.IsValid() {
                select {
                case validated <- data:
                case <-ctx.Done():
                    return
                }
            }
        }
    }()

    // Stage 2: Transform
    output := make(chan CleanData, 100)
    go func() {
        defer close(output)
        for data := range validated {
            select {
            case output <- transform(data):
            case <-ctx.Done():
                return
            }
        }
    }()

    return output
}
```

**Ưu điểm:**
- Pipeline pattern tự nhiên -- mỗi stage là một goroutine, kết nối bằng channel
- Backpressure qua buffered channel -- stage chậm tự động làm chậm stage trước
- Dễ thêm/bớt stage mà không ảnh hưởng phần còn lại

**Nhược điểm:**
- Error propagation qua nhiều stage phức tạp -- cần context cancellation hoặc error channel riêng
- Retry logic cho từng stage khó implement đúng
- Debug pipeline nhiều stage khó -- cần logging cẩn thận ở mỗi stage

---

### 12.5 Web Scraper / Crawler

**Ưu điểm:**
- Scrape song song hàng ngàn URL, tận dụng I/O wait time
- Semaphore pattern giới hạn tốc độ dễ dàng (`make(chan struct{}, maxConcurrent)`)
- Kết hợp với `errgroup` để thu thập lỗi từ nhiều goroutine

**Nhược điểm:**
- Cần rate limiting cẩn thận -- gửi quá nhanh sẽ bị ban IP
- Xử lý timeout cho từng request quan trọng -- dùng `context.WithTimeout`
- Tránh bị ban IP cần rotating proxy, exponential backoff

---

### 12.6 Background Job / Task Queue

Worker pool pattern xử lý job từ queue:

```go
func startWorkerPool(ctx context.Context, queue <-chan Job, numWorkers int) {
    var wg sync.WaitGroup

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func(workerID int) {
            defer wg.Done()
            for {
                select {
                case job, ok := <-queue:
                    if !ok {
                        return // Queue đã đóng
                    }
                    if err := job.Execute(ctx); err != nil {
                        log.Printf("Worker %d: job %s thất bại: %v", workerID, job.ID, err)
                        // Đẩy lại vào retry queue
                    }
                case <-ctx.Done():
                    return
                }
            }
        }(i)
    }

    wg.Wait()
}
```

**Ưu điểm:**
- Worker pool xử lý job hiệu quả, scale worker dễ dàng bằng cách thay đổi `numWorkers`
- Channel làm task queue in-memory đơn giản, không cần infrastructure phức tạp
- Graceful shutdown tự nhiên với context cancellation

**Nhược điểm:**
- Channel không persistent -- job mất khi process crash, cần persistent queue (Redis, RabbitMQ) cho production
- Job deduplication cần logic riêng -- channel không hỗ trợ sẵn
- Monitoring và retry cho failed job cần tự implement

---

### 12.7 Database Connection Pool

**Ưu điểm:**
- Goroutine chờ connection không tốn CPU -- bị park, không chiếm OS thread
- Context timeout tự nhiên: `context.WithTimeout` cho mỗi query
- `database/sql` tích hợp connection pooling sẵn, tương thích tốt với goroutine

**Nhược điểm:**
- Connection leak nếu không `defer rows.Close()` hoặc `defer conn.Close()`
- Pool exhaustion khi quá nhiều goroutine chờ connection -- cần `SetMaxOpenConns`
- Transaction trong goroutine cần cẩn thận -- không chia sẻ `*sql.Tx` giữa goroutine

---

### 12.8 CLI Tool với I/O Song song

**Ưu điểm:**
- Download/upload nhiều file đồng thời, tận dụng bandwidth tối đa
- Progress tracking per-file với channel hoặc atomic counter
- Semaphore giới hạn số file xử lý đồng thời

**Nhược điểm:**
- Error handling phức tạp -- partial failure (một số file thành công, một số thất bại)
- Partial failure recovery cần logic retry riêng cho từng file
- Terminal output từ nhiều goroutine cần serialization (dùng mutex hoặc channel)

---

### 12.9 Game Server / Simulation

**Ưu điểm:**
- Mỗi entity (player, NPC, room) một goroutine -- mô hình trực quan
- Tick-based update qua channel: server gửi tick signal, mỗi entity xử lý độc lập
- Tận dụng đa nhân tự nhiên cho game logic phức tạp

**Nhược điểm:**
- GC pause ảnh hưởng latency -- cần tuning GOGC, giảm allocation
- Cần object pooling (`sync.Pool`) để giảm áp lực GC
- Đồng bộ trạng thái giữa nhiều entity goroutine có thể phức tạp

---

### 12.10 IoT / Sensor Data Collection

**Ưu điểm:**
- Một goroutine per sensor đọc dữ liệu liên tục
- Fan-in aggregation: nhiều sensor goroutine gửi dữ liệu vào một channel trung tâm
- Buffered channel làm bộ đệm tự nhiên khi processing chậm hơn collection

**Nhược điểm:**
- Resource constraint trên edge device -- goroutine nhẹ nhưng vẫn tốn memory
- Memory management quan trọng trên thiết bị giới hạn RAM
- Network không ổn định -- cần reconnect logic và local buffering cho mỗi sensor goroutine

---

## 13. Nguồn tham khảo

### Chính thức
- [The Go Blog: Concurrency](https://go.dev/blog/pipelines)
- [Go Memory Model](https://go.dev/ref/mem)
- [context package](https://pkg.go.dev/context)
- [sync package](https://pkg.go.dev/sync)
- [Diagnostics](https://go.dev/doc/diagnostics)

### Lịch sử & Lý thuyết
- [Bài báo CSP (Hoare, 1978)](https://www.cs.cmu.edu/~crary/819-f09/Hoare78.pdf)
- [Đồng thời không phải Song song (Rob Pike)](https://gist.github.com/samueljoli/a03af6128c88d3a4b807b9221efdc0bf)
- [Di sản của Go (spf13)](https://spf13.com/presentation/the-legacy-of-go/)

### Nội bộ
- [Go Runtime Scheduler](https://internals-for-interns.com/posts/go-runtime-scheduler/)
- [Mô hình GMP](https://dev.to/aceld/understanding-the-golang-goroutine-scheduler-gpm-model-4l1g)
- [Cách xử lý Stack trong Go (Cloudflare)](https://blog.cloudflare.com/how-stacks-are-handled-in-go/)
- [Work-Stealing trong Go Scheduler](https://rakyll.org/scheduler/)
- [Lập lịch trong Go (Ardan Labs)](https://www.ardanlabs.com/blog/2018/08/scheduling-in-go-part2.html)
- [Preemption trong Go](https://hidetatz.github.io/goroutine_preemption/)

### Mẫu & Thực hành tốt
- [Mẫu đồng thời Go (talks)](https://go.dev/talks/2012/concurrency.slide)
- [Goroutine: 7 bẫy chí mạng](https://medium.com/@harshithgowdakt/go-goroutines-7-critical-pitfalls-every-developer-must-avoid-with-real-world-solutions-a436ac0fb4bb)
- [Phát hiện rò rỉ Goroutine](https://antonz.org/detecting-goroutine-leaks/)
- [Tắt máy chủ an toàn trong Go](https://victoriametrics.com/blog/go-graceful-shutdown/)

### So sánh
- [Tại sao hàng triệu Goroutine nhưng chỉ hàng ngàn Thread](https://rcoh.me/posts/why-you-can-have-a-million-go-routines-but-only-1000-java-threads/)
- [Java Virtual Thread so với Goroutine](https://medium.com/@nirajanach3/java-virtual-threads-vs-go-goroutines-037dda19e09c)
- [Goroutine so với OS Thread](https://thecodinggopher.substack.com/p/goroutines-vs-os-threads-why-go-runs)
