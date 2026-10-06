# Quy trình giải một bài LeetCode

Có 7 bước: **Hiểu đề → Brute force → Invariant → Tối ưu → Code → Dry run → Luyện tập**.

---

## Bước 0: Hiểu đề (2–5 phút)

- Viết lại đề bằng lời của mình: input là gì, output là gì.
- Từ constraints, suy ra **độ phức tạp cần đạt**:

| n | Độ phức tạp đích |
|---|---|
| ≤ 20 | O(2ⁿ), backtracking |
| ≤ 500 | O(n³) |
| ≤ 5·10³ | O(n²) |
| ≤ 10⁵–10⁶ | O(n log n) hoặc O(n) |
| > 10⁸ | O(log n) hoặc O(1) |

- Tự tạo 3 loại ví dụ: **bình thường**, **biên** (rỗng, 1 phần tử, tất cả giống nhau) và **bẫy** (ví dụ `"abba"`).

---

## Bước 1: Brute force

Mục tiêu là có đáp án đúng, chưa cần nhanh. Brute force có 2 vai trò:
1. Làm **oracle** để so kết quả với bản tối ưu sau này.
2. **Lộ ra phần việc bị lặp lại**, là manh mối để tối ưu.

Viết xong, ghi **chi phí của từng tầng vòng lặp**. Ví dụ `for i (n) → for j (n) → sum(i..j) (n)` = O(n³). Tầng trong cùng là chỗ cần tối ưu đầu tiên.

---

## Bước 2: Invariant để hiểu vì sao thuật toán đúng

### 2.1 Invariant là gì, và vì sao hiểu đề chưa đủ

> **Invariant là mệnh đề nối trạng thái các biến với phần dữ liệu đã xử lý, và mệnh đề này đúng tại một thời điểm cố định trong mỗi vòng lặp.**

Đề bài chỉ cho biết ý nghĩa của **đáp án cuối cùng**. Invariant mô tả **trạng thái ở giữa quá trình chạy**, và có 3 điểm khác:
1. **Đề nói về toàn bộ input, invariant nói về phần đã xử lý.** Đề: `best` = max của cả mảng. Invariant: `best` = max của `a[0..i]`.
2. **Thuật toán cần biến phụ mà đề không hề nhắc tới.** Ví dụ `cur` trong Kadane, `left` và `last` trong sliding window. Ý nghĩa của các biến này phải tự định nghĩa, không đọc được từ đề.
3. **Invariant chứa điều kiện phụ cần cho tính đúng.** Ví dụ "`left` là vị trí **nhỏ nhất** thỏa điều kiện" không có trong đề nhưng cần để chứng minh thuật toán không bỏ sót.

### 2.2 Tìm biến và invariant khi nhìn đề lần đầu

Không cần biết trước sẽ có những biến nào. Ban đầu chỉ có **1 biến đáp án**. Các biến khác tự xuất hiện qua quy trình 4 bước:

1. **Bắt đầu chỉ với biến đáp án** mà đề hỏi: `best`, `count`, `ans`.
2. **Cho dữ liệu đến từng phần tử, không được nhìn lại.** Tự hỏi: *"Khi `x` đến, đáp án thay đổi thế nào? Để tính được thay đổi đó, mình cần nhớ gì về quá khứ?"*
3. **Viết công thức cập nhật. Thiếu thứ gì thì thứ đó thành biến mới**, và định nghĩa chính xác biến đó ngay lúc tạo: biến lưu gì, về phần dữ liệu nào, có chỉ số rõ ràng.
4. **Kiểm tra đóng.** Mỗi biến phải tính được từ giá trị cũ của nó cộng với `x`, với chi phí rẻ.
   - Tính được: dừng lại, hệ biến đã đủ.
   - Lại thiếu thông tin: quay về bước 3.
   - Phải duyệt lại cả mảng: định nghĩa lại cho chặt hơn, hoặc dùng cấu trúc dữ liệu.

Mẹo then chốt: với bài về subarray hoặc substring, **chia mọi ứng viên theo điểm kết thúc của nó**. Ta có *đáp án = tốt nhất trong các "đáp án kết thúc tại j"*.

Định nghĩa chính xác của các biến thu được ở cuối quy trình **chính là invariant**.

**Ví dụ 1: LC 121 Best Time to Buy and Sell Stock**
- Đáp án là lợi nhuận lớn nhất, đặt biến `best`.
- Nếu bán hôm nay với giá `p`, lợi nhuận bằng `p - (giá thấp nhất trước hôm nay)`. Mình chưa có giá trị này, nên thêm biến **`min_price`**.
- Kiểm tra đóng: `min_price = min(min_price, p)`. Hệ biến đã đủ.

**Ví dụ 2: LC 53 Kadane**
- Đáp án là max sum subarray, đặt biến `best`.
- Subarray tốt nhất **mới** phải kết thúc tại `x`. Mình chưa có giá trị này, nên thêm biến **`cur`** = max sum subarray **kết thúc tại i**.
- Kiểm tra đóng: `cur = max(x, cur + x)`. Hệ biến đã đủ.

**Ví dụ 3: LC 3 Longest Substring Without Repeating Characters**
- Đáp án là độ dài lớn nhất, đặt biến `best`.
- Chuỗi hợp lệ dài nhất kết thúc tại `right` bắt đầu từ đâu? Thêm biến **`left`**.
- Kiểm tra đóng cho `left`: muốn biết có cần dịch `left` không, phải biết `c` xuất hiện gần nhất ở đâu. Thêm biến **`last`** (map).
- Kiểm tra kỹ: `last[c]` có thể nằm **trước `left`**, tức ngoài cửa sổ. Từ đó ra điều kiện `last[c] >= left`.

**Các loại thông tin hay cần nhớ về quá khứ:**

| Cần nhớ | Biến hay dùng |
|---|---|
| Min hoặc max của phần đã qua | 1 số |
| Tốt nhất **kết thúc tại** vị trí trước | 1 số (`cur`, `dp[i-1]`) |
| Điểm bắt đầu của đoạn hợp lệ | con trỏ `left` |
| Đã thấy giá trị chưa, ở đâu, bao nhiêu lần | HashMap |
| Tổng tích lũy | `prefix` |
| Các phần tử đang chờ đáp án | Stack |
| k phần tử lớn hoặc nhỏ nhất | Heap |

Với bài không duyệt tuyến tính (DP 2 chiều, đồ thị), câu hỏi vẫn giữ nguyên: *"Để giải bước này, cần biết gì từ các bước trước?"* Câu trả lời trở thành trạng thái `dp[...]` hoặc thông tin gắn với mỗi node.

### 2.3 Lối tắt: template invariant theo pattern

Khi nhận ra pattern quen, có thể lấy luôn bộ biến và invariant có sẵn. Vẫn nên kiểm tra lại bằng bước kiểm tra đóng và bằng ví dụ bẫy.

| Pattern | Invariant |
|---|---|
| Duyệt prefix | biến lưu đáp án (hoặc đủ thông tin để tính) cho `a[0..i]` |
| Kadane, DP 1D | `x` = tốt nhất **kết thúc đúng tại** i |
| Sliding window | `[l, r]` hợp lệ và `l` nhỏ nhất có thể |
| Two pointers thu hẹp | mọi cặp ngoài `[l, r]` đã bị loại, không tốt hơn `best` |
| Binary search | `pred(lo) = false`, `pred(hi) = true`, đáp án nằm trong `(lo, hi]` |
| Monotonic stack | stack đơn điệu, phần tử đã pop đã có đáp án |
| DP | `dp[state]` = đáp án subproblem, định nghĩa bằng lời |
| Greedy | tồn tại lời giải tối ưu nhất quán với các lựa chọn đến giờ |
| BFS | queue theo `dist` không giảm |
| Union-Find | `find(x)==find(y)` ⇔ x, y liên thông qua các cạnh đã xử lý |

### 2.4 Khôi phục invariant khi đọc lời giải hoặc debug

Khi có sẵn code mà không tự suy ra, hãy làm ngược lại: với từng biến, viết câu định nghĩa chính xác. Sau đó kiểm tra từng dòng code có giữ đúng định nghĩa đó không.
- Nếu câu định nghĩa mơ hồ ("tổng hiện tại", "vị trí cuối"), chưa hiểu biến đó.
- Nếu một dòng code phá định nghĩa, đó là chỗ có bug.

### 2.5 Chứng minh bằng 4 câu hỏi

1. **Khởi tạo:** invariant có đúng trước vòng lặp đầu tiên không?
2. **Duy trì:** giả sử đúng ở bước k, **xét từng nhánh `if`** và chứng minh vẫn đúng ở bước k+1.
3. **Kết thúc:** invariant cộng điều kiện dừng có suy ra đáp án không?
4. **Dừng được:** đại lượng nào giảm chặt sau mỗi bước, ví dụ `hi - lo` hay `r - l`?

Kỹ thuật chứng minh bước duy trì:
- **Loại trừ** (two pointers): ứng viên bị bỏ qua không tốt hơn ứng viên đã xét.
- **Exchange argument** (greedy): hoán đổi lời giải tối ưu sang lựa chọn của mình thì không tệ hơn.
- **Quy nạp** (DP): recurrence phủ mọi trường hợp của lựa chọn cuối cùng.

---

## Bước 3: Tối ưu theo thứ tự cố định

1. Xác định **độ phức tạp đích** từ Bước 0.
2. Nhìn **tầng trong cùng** của brute force.
3. Đi qua các câu hỏi theo thứ tự:

| # | Câu hỏi | Nếu có thì dùng |
|---|---|---|
| a | Có **tính lại** thứ đã tính không? | Prefix sum, cache, DP |
| b | **Cố định j, cần biết gì về các i < j?** | Duy trì thông tin đó khi duyệt |
| c | Tầng trong là "**tìm x thỏa điều kiện**"? | HashMap (bằng), sort + binary search (≤, ≥), heap (min/max) |
| d | Có **tính đơn điệu** để loại ứng viên? | Two pointers, sliding window, monotonic stack, binary search on answer |
| e | **Sort được không?** | Sort rồi greedy hoặc two pointers |
| f | Vẫn kẹt? **Đổi góc nhìn** | Đếm theo đóng góp từng phần tử, xử lý ngược, đếm phần bù |

**Câu b quan trọng nhất**, và nó chính là câu hỏi "cần nhớ gì về quá khứ" ở mục 2.2. Tối ưu và tìm invariant là cùng một tư duy nhìn từ hai phía.

**Ví dụ: LC 560 Subarray Sum Equals K**
- Brute force O(n³).
- Câu a: dùng prefix sum, `sum(i..j) = P[j+1] - P[i]` → O(n²).
- Câu b: cố định j, cần **đếm số i có `P[i] = P[j+1] - k`**.
- Câu c: đây là tìm theo giá trị bằng, dùng HashMap → O(n).
- Câu d không áp dụng được: mảng có số âm nên mất tính đơn điệu, không dùng sliding window.

**Sau khi tối ưu, viết lại invariant.** Phần lớn bug sinh ra lúc tối ưu làm vỡ invariant.

---

## Bước 4: Chuyển idea thành code

1. Viết **pseudo-code** 5–10 dòng.
2. **Đặt tên biến theo invariant**: `left`, `best`, `last_seen`, không dùng `a` hay `tmp`.
3. **Chốt quy ước biên**: khoảng đóng `[l, r]` hay nửa mở `[l, r)`, rồi giữ nhất quán.
4. Code theo khung: **khởi tạo → vòng lặp (cập nhật trạng thái, giữ invariant, cập nhật đáp án) → return**.
5. Xử lý edge case ở đầu hàm, hoặc chứng minh khung chung đã tự xử lý.

```python
def lengthOfLongestSubstring(s: str) -> int:
    last = {}
    left = best = 0
    for right, ch in enumerate(s):
        if ch in last and last[ch] >= left:
            left = last[ch] + 1
        last[ch] = right
        best = max(best, right - left + 1)
    return best

def subarraySum(nums, k):
    count = {0: 1}
    prefix = ans = 0
    for x in nums:
        prefix += x
        ans += count.get(prefix - k, 0)
        count[prefix] = count.get(prefix, 0) + 1
    return ans
```

---

## Bước 5: Dry run

Lập **bảng trạng thái**: mỗi dòng là 1 vòng lặp, mỗi cột là 1 biến. Dùng **ví dụ bẫy**.

Chạy LC 3 với `s = "abba"`:

| right | ch | xét `last` | left | best |
|---|---|---|---|---|
| 0 | a | chưa có | 0 | 1 |
| 1 | b | chưa có | 0 | 2 |
| 2 | b | b=1 ≥ 0 → nhảy | 2 | 2 |
| 3 | a | a=0 < 2 → **không nhảy** | 2 | 2 ✅ |

Nếu thiếu điều kiện `last[ch] >= left`, `left` sẽ lùi về 1 và đáp án sai thành 3.

Checklist khi dry run: input rỗng hoặc 1 phần tử · tất cả giống nhau · đáp án ở đầu hoặc cuối mảng · off-by-one (`<` hay `<=`, `+1` hay không) · số âm, trùng lặp, overflow.

Cách mạnh hơn dry run tay: viết invariant thành `assert` và so kết quả với brute force trên input ngẫu nhiên.

---

## Bước 6: Luyện để thành phản xạ

1. **Timebox 20–30 phút.** Nếu kẹt, xác định mình kẹt ở câu hỏi a–f nào, chỉ xem hint hoặc tên pattern.
2. **Đọc lời giải xong thì gập lại và tự code lại.** Sau đó khôi phục invariant của lời giải theo mục 2.4.
3. **Ghi sổ mỗi bài**, 5 dòng: brute force · bottleneck · câu hỏi a–f nào đã giúp · invariant · bug gặp khi dry run.
4. **Bậc thang độ phức tạp:** viết ra mọi bậc, ví dụ O(n³) → O(n²) → O(n), và ghi câu hỏi nào giúp bước xuống mỗi bậc.
5. **Học theo pattern:** làm 5–8 bài cùng pattern liên tiếp, sau đó trộn ngẫu nhiên. Drill câu b bằng LC 1, 3, 121, 560, 523, 974.
6. **Spaced repetition:** làm lại bài sau 1 ngày, 1 tuần, 1 tháng. Nếu không nói ra được invariant thì coi như chưa thuộc.
7. **Nói to** từng bước như đang phỏng vấn.

---

## Tóm tắt

- **Hiểu đề:** từ constraints suy ra độ phức tạp đích, tự tạo ví dụ bẫy.
- **Brute force:** có oracle và thấy được phần việc bị lặp lại.
- **Invariant:** bắt đầu từ 1 biến đáp án → cho dữ liệu đến từng phần tử → thiếu gì thì thêm biến đó, định nghĩa chính xác → kiểm tra đóng. Định nghĩa các biến chính là invariant. Chứng minh bằng 4 câu: khởi tạo, duy trì, kết thúc, dừng được.
- **Tối ưu:** độ phức tạp đích → tầng trong cùng → câu hỏi a→f, trọng tâm là *"cố định j, cần gì về quá khứ?"*.
- **Code:** pseudo-code trước, đặt tên biến theo invariant, chốt quy ước biên.
- **Dry run:** bảng trạng thái với ví dụ bẫy, sau đó assert invariant và so với brute force.
