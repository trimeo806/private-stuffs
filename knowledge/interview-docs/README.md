# Chuẩn bị phỏng vấn culture fit — HRS Group

**Phỏng vấn: thứ Tư 09/09** · CEO chi nhánh Việt Nam + Head of Engineering · tiếng Việt, xưng "em"

---

## Thứ tự đọc

| # | File | Là gì | Khi nào đọc | Đọc mất |
|---|---|---|---|---|
| **00** | [`00-hrs-master-guide.html`](00-hrs-master-guide.html) | Tài liệu tổng: sản phẩm HRS, 9 Leading Principles, kịch bản 65 phút, câu hỏi ngược lại họ | Đọc **cuối cùng**, ngày D-1, để tra | 40 ph |
| **01** | [`01-decode-hrs.html`](01-decode-hrs.html) | **Decode** — 8 signal areas, 9 principles, bảng dịch outsourcing → product, công cụ catalog | Nền tảng. Đọc một lần là đủ | 20 ph |
| **02** | [`02-deliver-carl.md`](02-deliver-carl.md) | **Deliver** — khung CARL, độ dài từng phần, chi tiết bao nhiêu là đủ, đọc phòng | Trước khi luyện nói | 30 ph |
| **03** | [`03-big-three.md`](03-big-three.md) | **Big Three** — lý thuyết sâu về TMAY, project, conflict, + câu hỏi ngược lại họ | Trước khi quay video TMAY | 40 ph |
| **04** | [`04-stories.md`](04-stories.md) | ⭐ **Kho story** — 10 story CARL đầy đủ, đáp án follow-up, 2 câu cấp CEO, bảng tra câu hỏi | **Đọc nhiều nhất.** Mở ra mỗi buổi luyện | 45 ph |
| **05** | [`05-practice.md`](05-practice.md) | **Practicing** — cách luyện, lịch 3 ngày thật, bản đồ 30 câu → story, prompt luyện AI | Ngay bây giờ — nó là lịch làm việc | 25 ph |
| **06** | [`06-pitfalls.md`](06-pitfalls.md) | **Pitfalls** — 7 bẫy, bẫy theo signal area, khung phòng thủ | Sau khi quay video lần đầu, để tự soi | 25 ph |
| **07** | [`07-ai-questions.md`](07-ai-questions.md) | ⭐ **Câu hỏi về AI** — 5 vùng đánh giá, kiểm kê AI của em, 5 câu trả lời soạn sẵn | **Ưu tiên cao** — HRS bán sản phẩm AI | 30 ph |
| **08** | [`08-question-bank.md`](08-question-bank.md) | ⭐ **Ngân hàng câu hỏi + mọi kịch bản nói** — Bộ ba *(mục A, C1, C5)*, "vì sao hợp HRS" và 2 câu principle *(mục B)*, ~125 câu theo dòng chảy 65 phút | **Mở ở màn hình phụ khi phỏng vấn** | tra cứu |

`interview-knowledge.html` không thuộc bộ này — đó là kiến thức kỹ thuật frontend.

---

## Nếu chỉ có 1 giờ

1. **04** — bảng tra câu hỏi → story, và câu mở đầu của #1, #4, #7
2. **06** phần III — bốn khung phòng thủ và kỹ thuật thực đơn
3. **05** phần 7 — lịch ba ngày

## Nếu chỉ có 15 phút, ngay trước giờ phỏng vấn

Chỉ mở **08**: bảng số liệu phải thuộc ở đầu file, và ba câu thần chú ở cuối. Không đọc gì khác.

---

## Lịch ba ngày

| Ngày | Việc | File |
|---|---|---|
| **CN 06/09** | Lấp 2 lỗ hổng · điền 3 ô trống · áp khối cắt · quay TMAY + #1 + #4 + #3 + #7 | 04, 05 |
| **T2 07/09 tối** | Quay #5 + #2 · **đọc 07 và điền 4 chỗ về AI** · chạy Script 3 | 05, 07 |
| **T3 08/09 tối** | Decode/Select nhanh · 2 câu cấp CEO · đọc bảng tra · ngủ sớm | 04, 00 |
| **T4 09/09** | Đọc bảng tra 10 phút. Không luyện gì mới | 04 |

---

## Việc còn dở

- [ ] **Lỗ hổng 1** — story "một lần em bị hiểu nhầm" *(chưa có)*
- [ ] **Lỗ hổng 2** — story "feedback góp ý gần đây từ quản lý" *(chưa có)*
- [ ] Story #4 — thêm vết xước vào Learnings *(xem 06, mục III.2)*
- [ ] Story #5 — điền cơ chế idempotency
- [ ] Story #7 — thời gian từ lúc tái hiện tới lúc fix · có chia sẻ lại cho team không
- [ ] Story #2 — xác nhận con số `10-20p → 10-15p?`
- [ ] Áp khối ✂️ cắt Context #2 và Theme 1 của #7
- [ ] **AI — nguồn học có tên** *(xem 07, Phần V)*
- [ ] **AI — lằn ranh dữ liệu: không đưa gì vào công cụ chạy trên API ngoài**
- [ ] AI — setup cá nhân: file quy tắc, prompt dùng lại, skill/hook tự dựng?
- [ ] AI — đã chia sẻ cách dùng AI với ai chưa?

---

## Bảy story, tra nhanh

| # | Story | Signal chính | Dùng cho |
|---|---|---|---|
| 1 | iLetter — POC thành module production | Scope, Ownership | Project tự hào nhất · tác động lớn nhất |
| 2 | Service AI dùng chung từ ADK | Ownership, Scope | Chủ động ngoài trách nhiệm |
| 3 | Bất đồng với lead rồi nhận ra mình sai | Conflict, Growth | Nhận ra mình sai · nhận feedback · mentor |
| 4 | Ẩn danh + luật dữ liệu Thuỵ Sĩ ⭐ | Ambiguity, Conflict | **Xung đột · yêu cầu mơ hồ · làm nhiều với ít** |
| 5 | Mất 25 phút cấu hình của người dùng | Growth, Ownership | Sai lầm · chất lượng · hiểu người dùng |
| 6 | Sửa code người khác hồi fresher | Growth | *Dự phòng* — chỉ khi hỏi thẳng lỗi thời mới đi làm |
| 7 | Bug chỉ xảy ra trên bản deploy | Perseverance | Trở ngại lớn · bug khó nhất · khi nào nhờ giúp |

⭐ #4 là story mạnh nhất với HRS. #1 là story scope lớn nhất.

---

## Ba điều dễ quên nhất

1. **Xưng "em" xuyên suốt**, không đổi sang "tôi" giữa chừng.
2. **Dừng ba giây sau khi nghe câu hỏi** — hỏi thầm: câu này đang đo signal gì?
3. **Nói phần phòng thủ trước khi bị hỏi** — "chưa go-live vì kế hoạch phát hành", "AI là công cụ, quyết định là của em".
