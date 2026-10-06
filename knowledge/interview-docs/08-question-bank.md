# Ngân hàng câu hỏi — HRS, thứ Tư 09/09

**Người phỏng vấn:** CEO chi nhánh Việt Nam · Head of Engineering — cả hai có nền kỹ thuật
**Cách dùng khi phỏng vấn online:** mở file này ở màn hình phụ, `Cmd+F` gõ từ khoá trong câu hỏi vừa nghe. Cột **Ghi nhớ** là mồi, không phải bài đọc — liếc rồi ngẩng lên nói.

**Ký hiệu:** 🔴 chưa có story · ⚠️ câu bẫy · ⭐ câu gần như chắc chắn có
**Quy ước:** `#1`–`#10` là **story**. **Leading Principle luôn gọi bằng TÊN, không bao giờ bằng số** — tài liệu master đánh số khác trang tuyển dụng HRS.
📄 **File này sở hữu** kịch bản TMAY *(mục A)*, "vì sao phù hợp HRS" + hai câu principle *(mục B)*, bảng câu hỏi 65 phút. **`04-stories.md` sở hữu** 10 story CARL, khung + kịch bản cho **câu project** và **câu xung đột**, hai câu cấp CEO.

---

## SỐ LIỆU PHẢI THUỘC

| Story | Con số |
|---|---|
| **#1 iLetter** | 11 tháng · 2 dev + 1 designer + PO · CEO nêu mục tiêu, **không có tầng BA/tech lead ở giữa** · form phức tạp 20 → 10 phút · refactor 2 tuần + 2–3 tuần sửa lỗi · công ty cấp thêm 1 dev · go-live sau 3 tháng |
| **#2 AI service** | 10–15 câu hỏi → 1 prompt · đang ở dev · 1 module dùng · team AI + CEO duyệt |
| **#3 React talk** | 6 feature team · chuẩn bị 2 tháng, 3 rehearsal · 30 dev, 6 câu hỏi · 2 người debug được lỗi thật · **sau buổi đó chị lead xin lỗi vì lúc đầu chưa chịu hiểu ý em, và nói tự hào về buổi hôm ấy** · comment PR giảm ~30% sau 1 tháng |
| **#4 Privacy** | 1 tuần · 6 người, có legal · 4 ngày → 1 ngày (−75%) |
| **#5 Error handling** | 25 phút cấu hình của người dùng · rà 5 tính năng · retry tối đa 5 lần + persist backend · thành checklist team |
| **#9 "AI generate ra vậy"** | tự tạo DataTable trùng + hardcode màu thay vì design token · em hỏi xác nhận ý định trước · họ đưa lại cho AI kèm ngữ cảnh → ra đúng · em viết rules vào `claude/rules` + hook Copilot, nạp tự động cho 6 team |
| **#10 Feedback + ảnh** | review theo quý: "trau dồi thêm kinh nghiệm" · hook xử lý ảnh **700 dòng → hơn 200** nhờ Cropper.js · vẫn tự viết phần co giãn cửa sổ vì thư viện không phủ · 4 tiêu chí chọn thư viện |
| **#8 Feedback PR** *(dự phòng)* | "PR quá dài, khó review" · lúc đầu em thấy hơi oan rồi mới đổi ý |
| **ai-kit** | 18 agent · 56 skill · 4 nền tảng · CLI cài bằng npx · 48 commit / ~2,5 tháng · **4 người đóng góp** |
| **#7 Bug deploy** | kẹt 1 ngày · slow 3G để tái hiện · qiankun unmount trước khi React commit · `setTimeout` 200ms → `flushSync` · chặn release pilot + chặn tester |

---

## BỘ BA — ba câu gần như chắc chắn có

Ba câu này khác mọi câu khác ở một điểm: **em biết trước 100%**. Chúng rơi vào đầu buổi và định khung cho mọi thứ phía sau.

| | Câu hỏi | Story | Signal | Kịch bản ở |
|---|---|---|---|---|
| 1 | "Kể một chút về em" | *(cắm móc)* | Communication | **mục A** ngay dưới |
| 2 | "Project em tâm đắc nhất" | **#1 iLetter** | Scope, Ownership | **mục C1** |
| 3 | "Kể về một lần xung đột" | **#4** → nối **#3** | Conflict, Ambiguity | **mục C5** |

**Rủi ro không đối xứng** — dồn thời gian luyện theo đúng thứ tự này:

| Câu | Trả lời tốt | Trả lời tệ |
|---|---|---|
| TMAY | +1 | −1 |
| **Project** | **+3** | −2 |
| **Xung đột** | +1 | 🔴 **loại thẳng** |

Câu 2 là câu ăn điểm. Câu 3 là câu **phòng thủ** — mục tiêu là không có cờ đỏ, không phải toả sáng.

**Ba móc cắm ở câu 1 trỏ đi đâu:** móc #3 và #4 trỏ thẳng vào hai story dùng ở câu 3. Câu 2 không cần móc riêng vì cả nhịp 2 đã là iLetter. Móc #2 không phục vụ Bộ ba — nó mở sang chủ đề AI.

Còn lại làm dự phòng: **#7** câu thử thách/bug khó · **#5** câu sai lầm · **#2** câu chủ động · **#6** câu lỗi hồi mới đi làm.

---

## A · MỞ MÀN — phút 0–5

### ⭐ "Kể một chút về em" — khung + kịch bản đầy đủ

**Khung ba phần:**

| Phần | Độ dài | Nhiệm vụ | Là nhịp |
|---|---|---|---|
| **Em là ai** | 2–3 câu | Vai trò, số năm, một nét riêng | 1 |
| **Thành tựu** | 2–3 ý | Signal mạnh nhất + **cắm móc** | 2 |
| **Hướng về phía trước** | 1–2 câu | Vì sao vị trí này, vì sao lúc này | 3 |

📌 **Kỹ thuật quan trọng nhất nằm ở phần 2:** cố ý nhắc lửng một việc để họ hỏi tiếp. Em kiểm soát luôn cả câu hỏi thứ hai, không chỉ câu đầu.
📌 **Liều đúng cho mid-level ở phần 3:** *"em tìm X, và ở đây có X"* — nêu tiêu chí của mình, nhưng là tiêu chí về công việc. Không "cho em cơ hội học hỏi" *(quá thấp)*, không "em sẽ tái cấu trúc team anh/chị" *(quá cao)*.

**Mục tiêu 85–90 giây.** Trục là **phạm vi trách nhiệm lớn dần**, không phải dòng thời gian công nghệ.
⚠️ Đừng chia sự nghiệp thành "trước AI / sau AI" — nghe như AI là nhân vật chính còn em đi theo.

**Nhịp 1 — em là ai** `[~15s]`

> "Em là fullstack developer, phần lớn thời gian làm frontend, bốn năm kinh nghiệm. Việc chính của em hiện tại là sở hữu trọn một module trong sản phẩm của công ty — từ làm rõ yêu cầu tới lúc ship."

**Nhịp 2 — ba nấc, cắm ba móc** `[~45s]`

> "Nhìn lại thì em đi qua ba nấc.
>
> Đầu tiên ở DXC, em làm phần tích hợp cho một dự án ngân hàng — ứng dụng thời gian thực giữa khách hàng và giao dịch viên.
>
> Nấc thứ hai là khi em làm bộ component dùng chung và design token cho các feature team. Điều em học được nhiều nhất ở giai đoạn đó là **người dùng của em chính là các team khác** — họ chỉ cần lo layout và dữ liệu, còn phần giao diện thì bên em lo, nên họ không phải tự dựng lại component cho giống Figma nữa. Đổi lại, một quyết định sai của em thì nhiều chỗ phải sửa theo. Giai đoạn đó em cũng đứng ra làm một buổi chia sẻ về React performance cho **sáu team** trong công ty. 〔móc #3〕
>
> Nấc thứ ba là hiện tại. Em nhận làm bản thử nghiệm giao diện cho một module tạo form — trước đó nó không có UI, muốn tạo form phải tự viết JSON, nên thực tế chỉ dev dùng được. Bản thử nghiệm đó được duyệt lên production và em làm chính module này hơn mười một tháng. Từ chỗ ban đầu chỉ tạo được form, giờ nó có thêm thu thập câu trả lời ẩn danh 〔móc #4 — tuỳ chọn thêm: *"tính năng này phải qua bộ phận legal"*〕, tích hợp webhook sang hệ thống bên thứ ba, và **gần đây nhất là một service AI em đề xuất và xây** — người dùng mô tả bằng một câu là ra form, thay vì tự ghép từng thành phần. 〔móc #2〕"

⚠️ **Ba tính năng đầu phải nói thành một mệnh đề "module lớn dần", không phải một danh sách.** Liệt kê bốn món ngang hàng thì không món nào được nhớ, và nó mô tả *sản phẩm làm gì* chứ không phải *em làm gì*. AI đặt cuối, có kết quả kèm theo — đó là móc duy nhất em muốn họ cắn ở nhịp này.

📌 **Nhắc tên tính năng không làm cháy story.** Story #4 không phải "bên em làm tính năng ẩn danh" mà là "em chất vấn yêu cầu và cắt 75% khối lượng". Tên tính năng là bối cảnh; **quyết định** mới là story — vẫn kể đầy đủ sau được.

**Nhịp 3 — vì sao ngồi đây** `[~30s]`

⚠️ Đây chỉ là **mồi**, không phải câu trả lời đầy đủ cho "vì sao HRS" — câu đó là câu riêng ở phút 5–12. Nói xong thì **dừng**, để họ hỏi tiếp.

> "Có hai thứ làm em thấy mình hợp với vị trí này.
>
> Thứ nhất là **AI nằm trong chính sản phẩm**, không phải một tính năng gắn thêm. Việc gần đây nhất của em cũng vậy — em đưa AI vào một module thật, để người dùng tạo form bằng một câu mô tả thay vì tự ghép từng thành phần. Và phần khó nhất không phải gọi model, mà là quyết định chỗ nào bắt buộc phải có người xác nhận trước khi hệ thống hành động. Em thấy Copilot của HRS cũng đang đứng trước đúng câu hỏi đó.
>
> Thứ hai là em đọc thấy HRS tìm kỹ sư biết dùng AI để phát triển nhanh hơn. Cái đó em làm có hệ thống chứ không chỉ dùng cho tiện — em tự dựng một bộ công cụ đóng gói agent và quy tắc cho các công cụ AI coding, hiện có bốn người cùng đóng góp."

**Bản ngắn** `[~18s]` — dùng khi nhịp 2 đã dài, hoặc họ có vẻ muốn đi tiếp:

> "Hai lý do. Một là AI nằm trong chính sản phẩm — việc gần đây nhất của em cũng là đưa AI vào một module thật, và phần khó nhất là quyết định chỗ nào bắt buộc có người xác nhận trước khi hệ thống hành động. Hai là em đọc thấy HRS tìm người dùng AI để phát triển nhanh hơn; đó là thứ em làm có hệ thống, không chỉ dùng cho tiện."

**Câu thay thế** — nếu họ **đã** hỏi "em biết gì về HRS" trước đó, dùng câu này để không lặp:

> "…Và về sản phẩm thì điều em thấy thú vị nhất là cả chuỗi năm mắt xích chỉ chạy được nếu nhân viên chịu đặt phòng trong hệ thống. Adoption là chỗ quyết định, và đó đúng là chỗ frontend can thiệp được."

### Vì sao nhịp 3 này hiệu quả

- **Tách hai chiều AI** — AI *trong sản phẩm* và AI *trong cách làm việc*. Phần lớn ứng viên chỉ có chiều thứ hai. HRS bán sản phẩm AI nên chiều thứ nhất mới là chiều hiếm.
- **Không khoe, mà nêu đúng câu hỏi chung** — "chỗ nào bắt buộc có người xác nhận" chạm đúng vùng căng của Copilot *(bản tin ra mắt nói quản lý phải phê duyệt, trang sản phẩm hiện tại nhấn one-click)*. Nói vậy là báo hiệu em đã nghĩ tới cùng bài toán họ đang giải.
- **"Bốn người cùng đóng góp"** biến "em dùng AI" thành "người khác dùng theo em" — Scaling, không phải sở thích cá nhân.
- **Cả hai vế đều mở đường follow-up** — họ hỏi tiếp thì có story #2 và `ai-kit` chờ sẵn.

### Ba móc trỏ đi đâu

| Câu trong TMAY | Mời họ hỏi |
|---|---|
| "buổi chia sẻ React cho sáu team" | **#3** — leadership, ảnh hưởng ngoài team |
| "thu thập câu trả lời ẩn danh" *(+ tuỳ chọn: "phải qua legal")* | **#4** ⭐ story mạnh nhất với HRS — nếu không cắm móc thì nó vẫn tới qua **câu hỏi về xung đột**, vốn gần như chắc chắn có |
| "một service AI em đề xuất và xây" | **#2** — và mở đường sang chủ đề AI |

⚠️ **Đừng cắm móc cho story chưa dựng CARL.** Đừng nhắc DataTable, image upload, color picker ở đây — để dành khi được hỏi.

### Bẫy ở phần mở màn

| Bẫy | Cách tránh |
|---|---|
| Kể hết story #1 ngay trong TMAY | Chỉ **nói tên và kết quả**. Đóng form, webhook, đẩy dữ liệu sang bên thứ ba — **không nhắc**, để dành follow-up |
| Vượt 90 giây | Quá 2 phút là mất điểm ngay câu đầu tiên. Bấm giờ khi luyện |
| Bắt đầu từ hồi đi học | Bắt đầu từ hiện tại là ai |
| Lắp bắp 20 giây đầu | 20 giây đầu định tông cả buổi — luyện riêng đoạn này tới khi trôi |
| 🔴 **Nói xấu công ty cũ** | Bẫy nguy nhất với em — động cơ rời ODC luôn có vế tiêu cực. **Giữ nguyên động cơ, đổi hướng của câu:** ~~"em chán dự án ngắn hạn"~~ → *"em muốn theo một sản phẩm đủ lâu để thấy quyết định của mình đúng hay sai"* |
| "team em", "bên em được giao" | Phản xạ ODC. Công ty sản phẩm nghe ra là em không phải người quyết |
| Dùng "11 tháng" như thành tích | Thời gian không phải impact. Nó chỉ đứng đó để chứng minh scope |

📌 Đây là đoạn **duy nhất** cả buổi được phép thuộc gần như nguyên văn. Bấm giờ, nói to, 5 lần.

### Câu khác ở phần mở màn

| Câu hỏi | Đo | Dùng | Ghi nhớ |
|---|---|---|---|
| Em đang làm gì ở công ty hiện tại? | Scope | TMAY nhịp 1 | sở hữu trọn module iLetter · làm hằng ngày với PO và designer · không có tầng trung gian dịch yêu cầu |
| Một ngày làm việc của em thế nào? | — | — | tự nhận việc, không chờ giao · làm hằng ngày với PO và designer · CEO nêu mục tiêu · không có BA hay tech lead dịch yêu cầu thành spec |
| Em có thể nói ngắn hơn không / kể kỹ hơn phần nào | Communication | — | ngắn: bỏ nấc 1 DXC · kỹ: hỏi lại "anh/chị muốn em đi sâu phần nào?" |

## B · VỀ HRS — phút 5–12

### ⭐ "Vì sao em thấy mình phù hợp với HRS?" — kịch bản

**Mục tiêu 70–80 giây.** Nói thẳng ba nhóm, mỗi nhóm hai ý, mỗi ý một dẫn chứng có số.

🚫 **Không gọi tên principle.** Kể việc, để họ tự dán nhãn. Chỉ gọi tên khi họ hỏi thẳng.

**Câu dẫn** `[~8s]`

> "Em thấy mình hợp ở ba chỗ: phần AI, cách làm việc, và nền kỹ thuật. Em nói nhanh từng cái ạ."

**1 · AI** `[~25s]`

> "Em không chỉ dùng AI để viết code, em đã **đưa AI vào sản phẩm**. Em đề xuất và xây một service AI cho module của em — trước đây người dùng phải tự ghép 10 đến 15 câu hỏi cho một form, giờ mô tả bằng một câu là ra. Nó cũng đọc được bộ câu hỏi có sẵn ở dạng PDF hay ảnh rồi dựng thành form.
>
> Và em dùng AI trong công việc **có hệ thống**, không phải dùng cho tiện. Em tự dựng một bộ công cụ đóng gói agent và quy tắc cho các công cụ AI coding — hiện có 18 agent, 56 skill, và **bốn người cùng đóng góp**."

**2 · Cách làm việc** `[~25s]`

> "Em quen **tự chạy đầu-cuối**. Ở module hiện tại em đi từ làm rõ yêu cầu, dựng data model, tới lúc ship. Bên em không có tầng BA hay tech lead nào dịch yêu cầu thành spec — CEO nêu mục tiêu muốn có gì, còn phần biến nó thành thứ làm được là em cùng PO và designer tự làm.
>
> Và em **hỏi lại trước khi làm**. Có một tính năng yêu cầu ban đầu em ước bốn ngày; em thấy phần phức tạp nhất có thể không cần thiết nên đề nghị họp lại với các bên, có cả bộ phận legal vì nó chạm luật bảo vệ dữ liệu. Cuối cùng chốt phương án **một ngày** mà vẫn đạt đúng mục tiêu."

**3 · Nền kỹ thuật** `[~25s]`

> "Frontend là phần em sâu nhất — em từng đứng ra chia sẻ về React performance cho **sáu team** trong công ty, và sau đó lượng comment của em trong PR về nhóm lỗi đó giảm khoảng 30%.
>
> Nhưng em không dừng ở frontend. Em tự học backend để tự làm phần dữ liệu cho module của mình. Và em có kinh nghiệm với hệ thống dùng chung — em làm bộ component và design token cho các feature team, nên em quen với việc **người dùng của mình là các team khác**, một quyết định sai thì nhiều chỗ phải sửa theo."

**Câu chốt** `[~5s]` *(tuỳ chọn, nếu còn thời gian)*

> "Ba thứ đó em thấy khớp với chỗ HRS đang đứng — sản phẩm có AI thật, và team cần người tự chạy được."

### Kho dẫn chứng — nếu họ đào sâu nhóm nào

| Nhóm | Dẫn chứng có sẵn |
|---|---|
| **AI trong sản phẩm** | service AI 3 tính năng · ràng buộc bằng output schema · người dùng chỉnh rồi mới gửi *(human-in-the-loop)* · duyệt bởi team AI nội bộ + CEO |
| **AI trong công việc** | `ai-kit`: 18 agent, 56 skill, 4 nền tảng, CLI cài bằng npx, 48 commit, **4 người đóng góp** · rules chặn AI tạo component trùng, nạp tự động cho 6 team *(#9)* |
| **Giới hạn của AI** | lần AI đi sai hướng → **vứt làm lại** · 2 tuần refactor vì output không dùng thẳng được · "trách nhiệm không chuyển sang AI được" |
| **Học AI** | roadmap.sh · AI training công ty, **test mỗi quý** · Andrew Ng · Matt Pocock — và skill của Matt Pocock nằm trong setup thật |
| **Tự chạy** | iLetter 11 tháng, POC → production · không có BA/tech lead dịch yêu cầu · tự học backend · refactor không ai yêu cầu |
| **Hỏi lại trước khi làm** | #4: 4 ngày → 1 ngày, giảm 75% · mang sẵn phương án thay thế vào họp |
| **Làm việc với châu Âu** | stakeholder Thuỵ Sĩ · legal đặt 2 ràng buộc, em xác nhận lại và làm đúng |
| **Không cày OT** | cắt scope, ưu tiên lại, báo rủi ro sớm |
| **Frontend sâu** | React performance cho 6 team · 30 dev dự · 2 người debug được lỗi thật · comment PR giảm ~30% |
| **Chẩn đoán khó** | bug chỉ xảy ra trên deploy · race giữa React và qiankun · bỏ `setTimeout` để dùng `flushSync` |
| **Hệ thống dùng chung** | common component + design token · người dùng là các team khác |
| **Chất lượng** | retry 5 lần + persist · rà 5 tính năng · thành checklist team |

### Ba nhóm này phủ principle nào *(chỉ để biết, không nói ra)*

| Nhóm | Principle chạm tới |
|---|---|
| AI | *pioneer for growth* · *think and act big* |
| Cách làm việc | *dive deep and act upon it* · *achieve more with less* · *take the customer view first* |
| Nền kỹ thuật | *foster healthy relationships* · *hire the most likely to win and develop them* |

Sáu trên chín principle, không gài chữ nào. Ba cái còn lại để dành cho câu hành vi.

### Khung chung cho mọi câu về giá trị

Tài liệu master của em đã có khung đúng: **Quan điểm → Vì sao → Bằng chứng ngắn**.
Kèm bài kiểm tra: *nếu câu trả lời áp được cho bất kỳ ai trong ngành thì nó chưa phải quan điểm.*

Hai câu dưới đây là biến thể của khung đó.

---

### ⭐ "Principle nào em thấy hợp nhất với mình?" — 4 nhịp

| Nhịp | Làm gì |
|---|---|
| 1 | Chọn, và nói **một câu quan điểm riêng** — đừng đọc lại định nghĩa trên website |
| 2 | **Vì sao** — phải qua được bài kiểm tra ở trên |
| 3 | **Hai bằng chứng khác loại nhau** — một lần em sai rồi sửa, một lần em chủ động đề xuất |
| 4 | **Điều nó đổi** trong cách em làm việc — thành một câu hỏi em tự đặt |

**Chọn: "Take the customer view first"** `[~55s]`

> "Em chọn *take the customer view first*.
>
> Điều em thấy đáng chú ý ở principle này là nguyên văn HRS viết rằng thành công của khách hàng **đứng trên** thành công thương mại của chính họ. Nghĩa là nó không phải lời khuyên chung chung — nó nói cho mình biết **chọn cái nào khi hai thứ xung khắc**. Ở tầng kỹ sư, xung khắc đó thường xuất hiện dưới dạng rất nhỏ: cái gì tiện cho mình viết, với cái gì đỡ khổ cho người dùng.
>
> Em gặp nó hai lần ở module hiện tại.
>
> **Lần thứ nhất là một lỗi của em.** Người dùng phải bỏ khoảng 25 phút để cấu hình một form — thêm thành phần, cấu hình logic điều hướng, khai báo webhook — và em không xử lý trường hợp gọi backend thất bại ở bước cuối. Lỗi là mất sạch. Cách tiện nhất là vá đúng chỗ đó. Nhưng nếu đứng ở chỗ người dùng thì 25 phút bị xoá vì một lần gọi mạng hỏng là không chấp nhận được, nên em rà lại toàn bộ 5 tính năng có gọi backend với giả định service có thể chết bất cứ lúc nào, rồi biến nó thành checklist chung cho team.
>
> **Lần thứ hai là chủ động.** Không ai yêu cầu, nhưng em thấy để tạo một form người dùng phải tự ghép 10 đến 15 câu hỏi bằng tay. Em đề xuất và xây một service AI để họ mô tả bằng một câu là ra form, rồi chỉnh lại.
>
> Điều nó đổi trong cách em làm việc là em có thêm một câu hỏi cố định trước khi bắt tay: **nếu chỗ này hỏng thì ai chịu hậu quả, và họ mất gì.** Trả lời được câu đó thì thường biết luôn nên đầu tư công sức vào đâu."

⚠️ Nếu đã kể #5 ở câu trước rồi thì thay bằng **#1** — trước đây chỉ người viết được JSON mới tạo được form, giờ người không biết kỹ thuật cũng làm được.

---

### ⭐ "Principle nào khó nhất với em?" — 5 nhịp

| Nhịp | Làm gì | Vì sao |
|---|---|---|
| 1 | Chọn, và nói ngay **khó ở CHỖ NÀO** | "khó chung chung" nghe như né |
| 2 | **Chặn hiểu lầm** — không phải em không làm, mà là làm chưa đủ tốt ở khía cạnh nào | Tránh bị đọc thành "em ngại thử" |
| 3 | **Bằng chứng em vẫn đang làm** | Chứng minh không né |
| 4 | **Bằng chứng cái giá đã trả** | Chứng minh cái khó là thật |
| 5 | **Cơ chế đang dùng để cải thiện** + thừa nhận phần còn lại | Thừa nhận suông là tự bắn vào chân |

**Chọn: "Pioneer for growth"** `[~60s]`

> "Em chọn *pioneer for growth*.
>
> **Cái khó với em không phải là dám thử cái mới.** Là **ước lượng cái giá phải trả khi thử sai**. Principle này nói không có trạng thái hoàn hảo, chỉ có cải tiến liên tục — mà cải tiến liên tục nghĩa là chấp nhận sai và sửa. Chỗ em còn yếu là biết trước cái sai đó tốn bao nhiêu.
>
> Em vẫn thử khá nhiều. Sau một workshop nội bộ em mang ADK về dựng thành một service AI cho module của mình. Em tự học backend để tự làm phần dữ liệu thay vì đẩy sang người khác. Và em tự dựng một bộ công cụ đóng gói agent và quy tắc cho các công cụ AI coding.
>
> Nhưng em cũng trả giá vài lần vì không lường được chi phí. Có một component xử lý ảnh em tự viết từ đầu — kéo ảnh, phóng to thu nhỏ, co giãn theo cửa sổ — khoảng **700 dòng**. Sau đó em phải viết lại bằng thư viện, còn **hơn 200 dòng**. Em không đánh giá sai độ khó của tính năng; em đánh giá sai chi phí nuôi nó về sau. Lần khác là một đợt refactor tốn hai tuần, rồi thêm hai đến ba tuần xử lý lỗi phát sinh — gần một tháng cho một việc em nghĩ là gọn.
>
> Cách em đang xử lý có hai phần. Trước khi tự viết thứ gì, em khảo sát xem đã có ai giải quyết bài toán đó chưa và đánh giá theo bốn tiêu chí: kích thước bundle, phạm vi tính năng, khả năng maintain, và độ tin cậy. Và em học cách **làm hẹp trước rồi mới mở rộng** — bài học từ chính service AI của em: em thiết kế cho khả năng dùng lại trước khi đi hỏi các team khác xem họ có nhu cầu thật không, nên tới giờ vẫn chỉ có một bên dùng.
>
> Em không nghĩ mình đã giải xong. Với những thứ em chưa từng làm bao giờ thì em vẫn chưa ước lượng tốt — đó là lý do gần đây em chủ động nhận nhiều việc lạ hơn, để có thêm dữ liệu về chính mình."

**Vì sao lựa chọn này an toàn:** nhịp 2 và 3 chặn được cách đọc *"em ngại rời vùng an toàn"* — điều rất tệ với một công ty lấy *think and act big* làm giá trị. Cái khó em nêu là **ước lượng rủi ro**, một điểm yếu chín chắn chứ không phải một điểm yếu về thái độ.

⚠️ **Đừng chọn** *take the customer view first* hay *dive deep* làm cái khó nhất — đó là cốt lõi của công việc, nói khó là tự loại.

---

### Biến thể khác của câu principle

| Câu hỏi | Cách trả lời |
|---|---|
| "Principle nào em thấy **khó đồng ý** nhất?" | ⚠️ Đừng bất đồng với một giá trị của họ ngay buổi phỏng vấn. Chuyển hướng: *"Em không thấy cái nào khó đồng ý, nhưng có một cái em phải cố ý hơn người khác — merits, not entitlements. Nó đòi mình nói thẳng bất kể chức danh, mà thói quen làm việc của em trước đây là nể vai vế. Em đang tập bằng cách khi không đồng ý thì mang dữ liệu vào thay vì im."* |
| "Em nghĩ gì về *merits, not entitlements*?" | Không nói về chức danh · #4: phương án được chọn vì nó đúng, không vì ai đề xuất |
| "Em nghĩ gì về *seek truth and commit*?" | #3: em bảo vệ quan điểm, rồi nhận ra lead đúng, bỏ hướng của mình và **tự cắt thêm cả những sơ đồ chị không yêu cầu cắt** |
| "Em nghĩ gì về *foster healthy relationships*?" | Đây **không** phải "hoà đồng" — nó là đối trọng của việc nói thẳng. #3: phần khó không nằm ở lúc tranh luận mà ở sau đó |
| "Principle nào em nghĩ HRS làm tốt/chưa tốt?" | ⚠️ Không đánh giá công ty khi chưa vào. Chuyển thành câu hỏi ngược: *"Em tò mò principle nào ở đây được nhắc tới nhiều nhất trong công việc hằng ngày?"* |

### Các câu khác về HRS

| Câu hỏi | Đo | Dùng | Ghi nhớ |
|---|---|---|---|
| ⭐ Em biết gì về HRS? | Chuẩn bị | `00-hrs-master-guide` mục 03 | nền tảng corporate lodging · Copilot = bộ não, Connect = bàn tay · 35% Fortune 500 · nói 30s rồi dừng |
| Em biết gì về team Search Book? | Chuẩn bị | mục 04 | mắt xích **Booking** trong chuỗi 5 bước · engine Smart Booking · sản phẩm Connect |
| Em thấy sản phẩm bên anh thế nào? | Judgment | mục 16 | Copilot: kiểm soát hallucination + tách privacy theo tài khoản |
| Em có biết Copilot không? | Chuẩn bị | mục 16 | ⚠️ chi tiết LLM Anthropic → **đóng khung "em đọc trên báo ngành"** |
| Vì sao rời công ty hiện tại? | Động cơ | `04-stories.md` mục cấp CEO | thứ muốn **tới**, không phải thứ muốn thoát · ⚠️ không chê chỗ cũ · ⚠️ không nói "công ty nhỏ quá" |
| ⚠️ Module sắp go-live mà em đi lúc này? | Trách nhiệm | ↑ | không bỏ ngang · người thứ hai đã nắm codebase · **refactor tách lớp chính là để bàn giao được** |

## C · CÂU HỎI HÀNH VI — phút 12–35 *(phần dài nhất)*

### C1 · Scope

#### ⭐ BỘ BA 2 · "Project em tâm đắc nhất" — khung + kịch bản

**Đây là câu nặng ký nhất cả buổi.** Nếu chỉ có một tiếng để luyện, dồn hết vào đây. Họ đang đo **em từng vận hành ở scope nào** và **cách em làm xong một việc từ đầu tới cuối**.

⚠️ **Hỏi "tâm đắc" hay "impactful" hay "scope lớn nhất" — trả lời bằng CÙNG một story.** Họ không quan tâm em *thích* dự án nào. Đừng nghe chữ "favorite" rồi chọn dự án vui nhất hoặc ngầu nhất về kỹ thuật.

**Ba trục chọn story:** Impact · Scope · **Personal Contribution**. Khi phải đánh đổi thì **ưu tiên Personal Contribution** — hai trục kia kể lại được, trục này bị kiểm tra chéo bằng follow-up. Một dự án nhỏ mà em lái toàn bộ tốt hơn một sáng kiến lớn mà vai trò em ở rìa.

**Chọn #1.** #4 chỉ 1 tuần *(quyết định xuất sắc, không phải project)* · #7 là sự cố · #3 là sáng kiến.

**Khung bảy nhịp** *(dự án lớn — nhóm Actions thành theme, đừng kể như một CARL đơn lẻ)*:

| Nhịp | Làm gì | Vì sao |
|---|---|---|
| 1 | **Front-load** — kết quả + trạng thái duyệt ngay câu đầu | Chưa go-live là điểm yếu duy nhất; đừng để họ tự phát hiện sau 4 phút |
| 2 | **Mục lục** — báo trước ba theme | Người nghe biết đang ở đâu |
| 3 | Context ngắn | 10% thời lượng, không hơn |
| 4 | Ba theme, mỗi theme một **quyết định của em** | 60% thời lượng — chỗ chấm điểm |
| 5 | Results: **"ai làm được" TRƯỚC, con số SAU** | "20 xuống 10 phút" quá yếu cho story 11 tháng |
| 6 | Learnings — thú nhận **rồi kết bằng em đã đổi thế nào** | Dừng ở lời thú nhận là tự bắn vào chân |
| 7 | **Menu** — mời họ khoan xuống hai chỗ em chọn sẵn | Em đưa thực đơn, cả hai món đều là món em nấu ngon nhất |

---

**① Front-load** `[~25s]`

> "Em kể module em đang làm chính. Sản phẩm công ty có sẵn một module khảo sát nhưng không có giao diện — muốn tạo form thì phải tự viết JSON, nên thực tế chỉ dev dùng được. Em nhận dựng bản thử nghiệm giao diện cho nó, bản đó được duyệt lên production, và em làm chính module này hơn mười một tháng tới giờ."

**② Mục lục** `[~15s]`

> "Có ba phần em muốn đi qua: dựng bản thử nghiệm và đưa nó qua cửa duyệt, refactor khi nó thành module thật, và mở rộng tính năng — trong đó em tự học backend."

**③ Context** `[~25s]`

> "Team lúc đó gồm em, một designer và PO. Bên em **không có BA hay tech lead nào dịch yêu cầu thành spec** — CEO nêu mục tiêu muốn có gì, còn phần biến nó thành thứ làm được là em cùng PO và designer tự làm. Công ty là công ty Thuỵ Sĩ nên stakeholder ở bên đó."

**④ Theme 1 — bản thử nghiệm và cửa duyệt** `[~40s]`

> "Em nhận phần frontend. Em dùng AI như công cụ dựng UI, còn phần em quyết là cấu trúc màn hình, cách nó nối vào các API sẵn có, và review lại toàn bộ output trước khi đưa vào. Em làm tới mức chạy được đầu-cuối chứ không dừng ở mockup — vì em biết muốn được duyệt thì phải cho thấy nó thật sự gửi được form đi. Bản đó được duyệt triển khai production, và em làm chính module khoảng bốn tháng một mình trước khi công ty cấp thêm một dev."

**⑤ Theme 2 — refactor** `[~40s]`

> "Khi có người thứ hai vào, em thấy code không còn chịu được nữa nên em chủ động refactor, không chờ ai yêu cầu. Em tách các lớp riêng — lớp tích hợp dữ liệu, state phía client, model, controller — để UI không dính thẳng vào luồng dữ liệu, người mới đọc theo lớp là hiểu. Em rà lại logic bỏ trùng lặp, dọn hard-code và magic number. Việc này tốn hai tuần, cộng hai tới ba tuần theo dõi và xử lý lỗi phát sinh sau đó."

**⑥ Theme 3 — backend và mở rộng** `[~25s]`

> "Vì em phụ trách cả UI lẫn dữ liệu, em tự học backend để tự làm phần đó thay vì đẩy sang người khác. Sau đó em cùng PO và designer mở rộng: đóng form khảo sát, thu thập câu trả lời ẩn danh, đẩy dữ liệu sang Google Sheet và một số nền tảng bên thứ ba."

**⑦ Results — thứ tự quan trọng** `[~35s]`

> "Thay đổi lớn nhất nằm ở chỗ **ai làm được**. Trước đây chỉ người viết được JSON mới tạo được form; giờ người không biết kỹ thuật cũng tự làm. Đó là mở ra khả năng mới, không chỉ nhanh hơn. Đo được thì với form phức tạp có logic điều hướng và webhook, thời gian tạo giảm từ khoảng hai mươi phút xuống khoảng mười phút. Và sau khi thấy bản thử nghiệm, công ty cấp thêm một dev cho module."

⚠️ **Đừng nhắc trạng thái go-live ở đây.** Để dành cho follow-up *"sau đó thì sao"*.

**⑧ Learnings** `[~25s]`

> "Bài học lớn nhất là em đã nhảy vào code quá sớm. Nếu làm lại, em sẽ làm rõ yêu cầu, chốt các yêu cầu phi chức năng, xác định MVP, dựng data model và luồng dữ liệu, thiết kế kiến trúc rồi mới bắt đầu. Cái giá của việc bỏ bước đó là gần một tháng — và nó hoàn toàn tránh được. Giờ với mọi tính năng mới trong module em đều đi qua đúng thứ tự đó trước khi mở editor."

**⑨ Menu — câu mời khoan** `[~15s]`

> "Trong ba phần này có hai chỗ em nghĩ đáng nói sâu hơn nếu anh/chị quan tâm: một quyết định về bảo vệ dữ liệu em phải làm việc với bộ phận legal, và một lỗi của em làm người dùng mất dữ liệu. Anh/chị muốn em đi qua phần nào trước?"

**Bản đồ follow-up — mỗi nhánh đều có CARL đầy đủ trong `04-stories.md`**

| Họ hỏi | Rút story |
|---|---|
| "Phần khó nhất?" | Refactor khi module đã chạy và có người thứ hai làm song song *(có sẵn trong #1)* |
| "Có bất đồng nào không?" | **#4** — đây là câu conflict bị hỏi lồng, em được lợi kép |
| "Sai lầm lớn nhất?" | **#5** — 25 phút cấu hình của người dùng |
| "Có đề xuất gì ngoài phạm vi không?" | **#2** — service AI |
| "Nếu làm lại?" | Đã nằm trong nhịp ⑧ |
| "Đo thành công thế nào?" | #1 — kèm phần thừa nhận không đặt baseline |
| "Sau đó thì sao?" | Chờ go-live trong 3 tháng, hiện dùng nội bộ, em vẫn làm chính. Gọn, rồi dừng |

Cấu trúc này là **một dự án lõi, bốn story vệ tinh**. Họ khoan chỗ nào cũng chạm CARL hoàn chỉnh.

**✂️ Rút xuống ~3 phút:** bỏ nhịp ⑥, thay bằng một câu — *"em cũng tự học backend để tự làm phần dữ liệu, và mở rộng thêm vài tính năng cùng PO và designer"*.

#### Các câu Scope khác

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| Project có tác động business lớn nhất | **#1** | nhấn kết quả: **ai làm được** (trước cần biết JSON, giờ không) + công ty cấp thêm dev |
| Project scope lớn nhất từng làm | **#1** | 11 tháng · mục lục 3 phần |
| Project phức tạp nhất về kỹ thuật | **#7** hoặc **#2** | #7 nếu là chẩn đoán · #2 nếu là thiết kế |
| Em từng làm việc với bao nhiêu team? | **#3** | 6 feature team |

### C2 · Ownership

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| ⭐ Việc có tác động mà không ai giao em | **#2** | "không ai giao việc này" · workshop ADK → tự đề xuất → team AI + CEO duyệt |
| Em từng giải quyết vấn đề ngoài phạm vi của mình chưa | **#5** hoặc **#2** | #5: tự phát hiện rồi rà cả 5 tính năng |
| Làm vượt mức yêu cầu | **#1** | refactor 2 tuần không ai yêu cầu |
| Em đo thành công bằng gì? | Values | #4: effort tránh được + legal duyệt · #1: thừa nhận ban đầu không đặt baseline |
| Thấy cơ hội người khác bỏ lỡ | **#2** | ADK — người khác nghe workshop xong thôi, em mang về áp vào việc thật |
| ⚠️ Ai giao project đó cho em? | Ownership | **đừng nói "em được giao"** — nói em **chọn** làm gì sau khi nhận |

### C3 · Ambiguity

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| ⭐ Dự án rất mơ hồ / yêu cầu không rõ | **#4** | chưa rõ **mục tiêu thật** của tính năng → em hỏi ngược |
| Yêu cầu thay đổi liên tục | **#4** | mang sẵn phương án thay thế, không đi tay không |
| Vấn đề có nhiều cách giải, em chọn thế nào | **#7** | `setTimeout` vs `flushSync` — chọn cái đúng, không chọn cái chạy được |
| Em xử lý thế nào khi thiếu thông tin | Values + **#4** | 3 câu hỏi mang vào họp: vì sao có tính năng · chi phí duy trì · rủi ro nếu không tách |

### C4 · Perseverance

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| ⭐ Project gặp trở ngại lớn | **#7** | mục lục 4 phần · ⚠️ đang đo **Perseverance**, đừng lao vào kiến trúc |
| Bug khó nhất từng gặp | **#7** | chỉ xảy ra trên deploy · slow 3G · race React vs qiankun |
| Dự án bị trễ, em phản ứng thế nào | 🟡 **#7** | gần nhất — bug chặn release pilot |
| Em làm gì khi thiếu người / thiếu thời gian | Values | ⚠️ **tuyệt đối không nói OT** · cắt scope, ưu tiên lại, báo rủi ro sớm *(achieve more with less)* |
| Khi nào em quyết định dừng một hướng | **#7** hoặc **#4** | #7: vứt `setTimeout` đang chạy · #4: bỏ hướng tách 2 kho |

### C5 · Conflict

#### ⭐ BỘ BA 3 · "Kể về một lần xung đột" — khung + kịch bản

⚠️ **Câu phòng thủ.** Trả lời tốt được +1, trả lời tệ là **loại thẳng**. Mục tiêu không phải toả sáng, mà là **không có cờ đỏ**. Đừng cố kể chuyện kịch tính.

🚫 **Câu mở đầu tệ nhất trong tiếng Việt:** ~~"Dạ em không phải kiểu người tạo xung đột."~~
Nhiều người Việt nói câu này vì nghĩ là điểm cộng. Ở công ty tech đó là **né tránh** — họ đọc ra là em sẽ im lặng khi thấy vấn đề. Với HRS còn tệ hơn: principle *seek truth and commit* viết thẳng *"ok để bất đồng"*.

**Năm hành vi họ tìm:** chủ động lên tiếng bỏ qua thứ bậc · đi thẳng tới nguồn, không núp sau quản lý · giữ được cảm xúc · ưu tiên kết quả chung chứ không phải thắng thua · **dữ liệu và logic đủ để em đổi ý**.

📌 Gạch cuối là chỗ **#3** tồn tại. Một story conflict tốt phải cho thấy em **có thể bị thuyết phục**, không chỉ thuyết phục được người khác.

**Vì sao #4 làm story chính, không phải #3:** #3 kết thúc bằng *em sai và em làm theo lead*. Rất chín, nhưng nếu đó là lần bất đồng duy nhất họ nghe thì họ chỉ thấy em **nhượng bộ**. #4 thì em bất đồng với **chính yêu cầu được đưa xuống**, xuyên phòng ban có cả legal, và **phương án của em được chọn**.

**Khung sáu nhịp:**

| Nhịp | Làm gì | Vì sao |
|---|---|---|
| 1 | **Đặt tên cho sự bất đồng** ngay câu đầu | Không có chữ "em không đồng ý" thì họ tính đây là story làm rõ yêu cầu, không tính là conflict |
| 2 | **Nói ra em chọn kênh nào và vì sao** | Comment trên ticket hiếm khi giải quyết được xung đột |
| 3 | **Tìm hiểu trước khi đòi được hiểu** | Hỏi về mục tiêu, không phản bác giải pháp |
| 4 | **Mang sẵn phương án thay thế** | Đến chỉ để hỏi thì buổi họp dừng ở hoài nghi, không ra quyết định |
| 5 | **Kết luận rõ ràng** + con số | Ai chốt gì, đường đi tiếp là gì |
| 6 | 🔴 **Trạng thái quan hệ sau đó** | Nhịp bị bỏ sót nhiều nhất, rẻ nhất để thêm — đúng một câu |

⚠️ **Đừng gột sạch cảm xúc khỏi story.** Chỗ căng thì nói là căng, bằng giọng điềm tĩnh kiểu nhà báo. Sự tương phản giữa nội dung căng và giọng kể bình tĩnh **chính là bằng chứng**. Story nhạt hoàn toàn thì họ không có gì để chấm.

---

**① Đặt tên bất đồng** `[~30s]`

> "Có một lần em không đồng ý với chính yêu cầu được đưa xuống. Module của em cần tính năng trả lời khảo sát ẩn danh, và yêu cầu ghi rõ: tách hai chỗ lưu, mã hoá phần định danh, nhưng vẫn de-anonymize được khi audit. Em ước phương án đó mất khoảng bốn ngày, và em cho rằng phần lớn công sức đó không phục vụ mục tiêu thật. **Thay vì im lặng làm, em đưa nó ra một buổi trao đổi.**"

⚠️ Hai cụm bắt buộc: **"em không đồng ý"** và **"thay vì im lặng làm"**.

**② Chọn kênh** `[~15s]`

> "Em không phản hồi bằng comment trên ticket, vì đây là câu hỏi về **mục tiêu** chứ không phải về cách làm — loại câu hỏi đó không giải quyết được bằng bình luận qua lại. Em xin một buổi trao đổi trực tiếp và đề nghị có mặt cả bộ phận legal."

**③ Tìm hiểu trước, và giữ thái độ** `[~40s]`

> "Em bắt đầu nghiên cứu hướng được yêu cầu, tìm cách lưu tách hai nơi. Nhưng càng làm em càng thấy một thứ chưa rõ: **mục tiêu thật sự của tính năng là gì — ai cần được bảo vệ khỏi ai.**
>
> Phần khó nhất không phải kỹ thuật. Là chất vấn một yêu cầu đã được đưa xuống mà không làm người đưa yêu cầu thấy mình bị bắt bẻ. Em xử lý bằng cách **hỏi về mục tiêu, không phản bác giải pháp của họ.** Em đi vào với ba câu hỏi: vì sao tính năng này tồn tại; nếu tách hai nơi thì chi phí duy trì về sau ra sao; và nếu không tách thì rủi ro cụ thể là gì."

**④ Mang phương án thay thế** `[~20s]`

> "Em không đi tay không. Em mang theo một phương án đã nghĩ sẵn: vẫn lưu chung, nhưng chặn ở tầng server, không bao giờ trả danh tính người trả lời về phía client."

**⑤ Kết luận** `[~40s]`

> "Buổi đó có sáu người, gồm cả legal. Khi bóc ra thì nhu cầu thật là **người gửi khảo sát không được biết ai đã trả lời** — chứ không phải nhu cầu tách hạ tầng lưu trữ. Việc tách hai chỗ không cần thiết cho mục tiêu đó, nên các bên chốt đi theo phương án của em.
>
> Phương án ban đầu em ước bốn ngày. Phương án chốt mất một ngày, đã gồm test — giảm khoảng bảy mươi lăm phần trăm khối lượng cho cùng một mục tiêu nghiệp vụ. Và về lâu dài bên em không phải nuôi hai kho dữ liệu cùng một cơ chế de-anonymization."

**⑥ Trạng thái quan hệ** `[~20s]` 🔴 *nhịp đừng bỏ*

> "Legal đặt lại hai ràng buộc: việc lọc phải diễn ra ở phía server, và sau khi gửi xong thì xoá phần định danh của người nhận. Em xác nhận lại cả hai với họ và làm đúng như vậy — em không tự diễn giải phần luật. Em nghĩ đó là lý do buổi đó kết thúc ở chỗ cùng chốt một phương án, chứ không phải ở chỗ ai thắng ai."

*(👉 Nếu sau đó bên em còn làm việc tiếp với legal và nó trơn hơn, thêm đúng một câu ở đây. Đừng bịa nếu không có.)*

**⑦ Learnings** `[~25s]`

> "Bài học là hỏi *vì sao cần tính năng này* ngay từ đầu, thay vì nhận yêu cầu rồi đi tìm cách làm. Ở đây phần phức tạp nhất của yêu cầu hoá ra không phục vụ mục tiêu thật, mà nếu em im lặng làm thì bên em đã phải nuôi nó nhiều năm. Điều thứ hai là khi chất vấn một yêu cầu thì phải mang sẵn phương án thay thế — nếu chỉ đến để hỏi thì buổi họp dừng ở chỗ hoài nghi chứ không ra được quyết định."

---

#### Cặp đôi — câu tự nối, đây là chỗ em nổi bật

Nói ngay sau nhịp ⑦, đừng chờ được hỏi:

> "Đó là lần em bất đồng và em đúng. Em cũng có một lần bất đồng mà cuối cùng em sai — nếu anh/chị muốn nghe thì em kể."

**Bản nén #3** `[~75s]` — dùng khi họ gật:

> "Em đứng ra làm một buổi chia sẻ về React performance cho sáu team. Em muốn dành một phần cho cơ chế re-render, vẽ bằng sơ đồ, vì em nghĩ không hiểu cơ chế thì tối ưu chỉ là học vẹt. Chị lead thì muốn cắt phần đó.
>
> Trao đổi một lúc thì căng, vì em liên tục đưa lý do để giữ sơ đồ. Em nhận ra mình đang tranh luận **để thắng** chứ không phải để ra bài tốt hơn, nên em đề nghị tạm nghỉ. Lúc quay lại em hỏi lo ngại thật của chị là gì, và câu trả lời là: buổi này tên là *các phương pháp giảm re-render*, người nghe tới để lấy thứ dùng được ngay, mà phần lý thuyết của em đang chiếm quá nhiều thời lượng. Nghe tới đó em thấy chị đúng — em đã xuất phát từ nội dung chứ không từ mục tiêu của buổi.
>
> Em bỏ hướng của mình và làm lại, **và em tự cắt thêm cả những sơ đồ chị không yêu cầu cắt.** Kết quả là ba mươi dev tới dự, sáu câu hỏi, và hai người sau đó debug được lỗi re-render thật trong code của họ.
>
> Quan hệ với chị sau đó tốt hơn trước. Ba buổi rehearsal chị góp ý rất nhiều và em nhận hết. Sau buổi thuyết trình chị chủ động nói với em rằng chị xin lỗi vì lúc đầu đã không chịu hiểu ý em, và chị tự hào vì buổi hôm đó chạy tốt."

Một lần đúng và một lần sai, kể liền nhau, mạnh hơn bất kỳ story đơn lẻ nào: nó chứng minh em bất đồng vì **muốn ra kết quả đúng**, không phải vì muốn thắng. Rất ít ứng viên chuẩn bị cặp này.

⚠️ **Giới hạn của #3:** đây là trường hợp em **được thuyết phục** rồi mới thực thi. Nếu họ hỏi đúng vế *"bị áp một quyết định mà vẫn không đồng ý"* thì bộ story chưa có — xem mục **J**.

#### Kiểm tra trước khi kể — 12 hành động

Với kịch bản trên, #4 đang có: **1** chủ động nêu · **2** xung đột mang tính xây dựng · **3** tìm hiểu trước · **4** chọn đúng kênh · **5** đồng cảm · **6** giữ được cảm xúc · **7** dùng dữ liệu · **8** kéo đúng người đúng lúc · **10** kết luận rõ ràng · **11** giữ quan hệ.

Mười trên mười hai. Nhịp ② và ③ tồn tại chính vì trước đó thiếu **4** và **6**.

#### Các câu Conflict khác

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| Bất đồng với quản lý hoặc lãnh đạo | **#3** (mở đầu A) | em thấy chị đúng → bỏ hướng của mình → **tự cắt thêm sơ đồ chị không yêu cầu cắt** |
| Bất đồng với đồng nghiệp trong team | **#3** (mở đầu B) | |
| Lần nào em phải làm theo quyết định không phải của mình | **#3** (mở đầu A) | ⚠️ đây là *được thuyết phục*, không phải *bị áp* — nếu họ đào đúng vế đó thì thừa nhận |
| Lấy được thứ từ người không muốn cho | **#4** | chất vấn yêu cầu đã được quyết |
| Em xử lý thế nào khi team không đồng ý với em | Values | hỏi để hiểu lo ngại **trước**, bảo vệ sau |
| ⚠️ Em có phải người hay tranh luận không? | Bẫy | **không được nói "em không tạo xung đột"** — nói: bất đồng để ra kết quả đúng, không để thắng |

### C6 · Communication

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| Giải thích thứ kỹ thuật cho người không kỹ thuật | 🟡 **#4** | giải thích ràng buộc kỹ thuật cho legal + CEO |
| 🔴 Một lần em bị hiểu nhầm | **chưa có** | *xem mục J — dùng #4 ở góc khác* |
| Em báo cáo tiến độ thế nào | Values | chủ động báo sớm khi thấy rủi ro, kèm phương án |
| Làm việc với người ở múi giờ khác | **#1**, **#4** | stakeholder Thuỵ Sĩ · viết rõ để không block bên kia |
| Em thuyết phục người khác bằng cách nào | **#4** | mang dữ liệu + phương án thay thế, hỏi về mục tiêu chứ không phản bác giải pháp |

### C7 · Growth

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| ⭐ Kể về một lần em thất bại / sai lầm lớn nhất | **#5** | "đây là lỗi của em" · 25 phút của người dùng · rà 5 tính năng · thành checklist |
| ⭐ Feedback góp ý gần đây từ quản lý | **#10** (mở đầu A) | review theo quý · **tự dịch** feedback mơ hồ thành thứ làm được · ⚠️ đừng kể feedback thứ hai (lời khen) |
| ⚠️ Có ai nói em thiếu kinh nghiệm không | **#10** | kinh nghiệm = **số loại tình huống**, tăng tốc được · chủ động nhận việc chưa từng làm |
| Em nhận góp ý thế nào | **#10** hoặc **#8** | #8 dự phòng: PR quá dài, khó review |
| Em đồng ý với feedback đó không? | **#10** | có — đúng, chỉ là nói ở mức khái quát nên em phải tự dịch |
| Feedback đó có làm em bất ngờ không? | **#10** Theme 4 | không — em đã tự thấy trước · **nhưng người khác cũng thấy nghĩa là em sửa chưa đủ nhanh** · ⚠️ đừng nói giọng "em biết rồi" |
| Feedback nào khó tiếp thu nhất | **#8** | PR quá dài — lúc đầu thấy hơi oan, rồi thử nhìn từ phía người review |
| Lần em nhận ra mình sai | **#3** | |
| Lỗi hồi mới đi làm | **#6** | mở đầu bắt buộc: "lúc đó em mới đi làm" |
| Em học một thứ mới thế nào | **#1** tự học backend · **#2** ADK | học trên việc thật, không làm ví dụ mẫu |
| Em đọc sách gì gần đây | **#10** | **A Philosophy of Software Design** — deep module vs shallow module · Pragmatic Programmer · AI Beyond Vibe Coding |
| Quyết định kỹ thuật em phải làm lại | **#10** (mở đầu B) | 700 → hơn 200 dòng · em viết một **module nông** |
| Khi nào tự viết, khi nào dùng thư viện | **#10** | bundle · tính năng · maintain · độ tin cậy → rồi **bảo vệ lựa chọn với team trước khi viết** |
| Em từng over-engineer chưa | **#10** | đánh giá sai độ phức tạp: nó không nằm ở giao diện mà ở phần tính toán |
| Điểm yếu lớn nhất của em | Values | ⚠️ **không humble-brag** · dùng bài học #1: nhảy vào code quá sớm, bỏ bước làm rõ yêu cầu |
| Em cải thiện gì trong 6 tháng qua | **#5** | từ happy path → luôn hỏi trước về lỗi và edge case |

### C8 · Leadership

| Câu hỏi | Dùng | Ghi nhớ |
|---|---|---|
| ⭐ Em từng ảnh hưởng người khác mà không có quyền hạn | **#3** | 6 team · 2 người debug được lỗi thật · comment PR giảm ~30% |
| Em từng mentor ai chưa | **#3** | |
| Em thể hiện leadership thế nào | **#3** + **#2** | #3 lan kiến thức · #2 thiết kế cho người khác dùng lại |
| Em giúp người mới vào team thế nào | **#1** | refactor tách lớp để người mới đọc theo lớp là hiểu |

---

## D · CÁCH EM LÀM VIỆC — phút 35–45

| Câu hỏi | Ghi nhớ |
|---|---|
| ⭐ Em bắt đầu một tính năng mới thế nào | làm rõ yêu cầu → NFR → MVP → data model → luồng dữ liệu → HLD/LLD → mới code. **Bài học từ #1: bỏ bước này trả giá gần 1 tháng** |
| Em chia task thế nào | chia theo luồng dữ liệu và ranh giới lớp, không chia theo màn hình |
| Em ước lượng thế nào | #4: bóc 4 ngày thành 2+1+1 · nói rõ giả định |
| Em xử lý deadline gấp thế nào | ⚠️ **không OT** · cắt scope, ưu tiên lại, báo sớm |
| Em review code thế nào | #3: thấy cùng nhóm lỗi lặp lại → giải quyết ở gốc bằng buổi chia sẻ, không comment mãi |
| Em đảm bảo chất lượng thế nào | #5: giả định service có thể chết bất cứ lúc nào · checklist team |
| Em làm việc với designer thế nào | #1: em, PO và designer cùng chốt phạm vi rồi mở rộng tính năng · em vào sớm từ lúc bàn giải pháp, không đợi Figma xong mới nhận |
| Em xử lý bug production thế nào | #7: khoanh vùng → tái hiện → tìm nguyên nhân gốc, không vá triệu chứng |
| Khi nào em nhờ giúp đỡ | #7: khi hết giả thuyết **và** đang chặn người khác · hỏi kèm danh sách đã loại trừ |

---

## E · AI — phút 45–52 ⭐ *(HRS bán sản phẩm AI, phần này đào sâu)*

| Câu hỏi | Vùng | Dùng | Ghi nhớ |
|---|---|---|---|
| ⭐ Em dùng AI thế nào? Kiểm soát ra sao? | Work + Trust | `07` Q3 + mục 16 | chia theo **chi phí của một lỗi**, không theo loại việc |
| ⭐ Lần AI giúp em làm được thứ không làm được | Work | `07` Q1 | đọc PDF/docx/ảnh → form · **AI đổi loại bài toán dám nhận** |
| Em bắt được AI sai ở đâu? | Trust | `07` Q5 | giao cả phần implementation → đi sai hướng → **vứt làm lại** |
| ⭐ Em không đưa gì vào công cụ AI? | Trust | `07` Phần V.2 | tách **rủi ro cá nhân** và **rủi ro sản phẩm** · dữ liệu giả cùng hình dạng · service AI do team AI + CEO duyệt · nối #4 legal Thuỵ Sĩ |
| Tool/workflow mới adopt gần đây | Growth | `07` Q2 | ADK từ workshop Google → mang về dùng ngay |
| ⭐ Em bám kịp AI bằng cách nào | Growth | `07` Phần V.1 | 3 lớp: **roadmap.sh** · **AI training công ty, test mỗi quý** · **Andrew Ng + Matt Pocock** — và skill của Matt Pocock nằm trong setup thật của em |
| Em nghĩ gì về việc áp dụng AI trong tổ chức | Scaling | `07` Q4 | thừa nhận thẳng: mới 1 module dùng · bài học: chưa validate nhu cầu trước khi tổng quát hoá |
| ⭐ Em có chia sẻ cách dùng AI với team không | Scaling | `07` Phần V.3–4 | **`ai-kit`: 18 agent, 56 skill, 4 nền tảng, CLI, 4 người đóng góp** |
| ⭐ Em xử lý thế nào với người quá tin vào AI | Scaling + Trust | **#9** | "AI generate ra vậy" → hỏi ý định trước → sửa ở tầng rules, không sửa từng PR |
| Ai chịu trách nhiệm cho code AI sinh ra? | Trust | **#9** | "AI viết ra thì AI không chịu trách nhiệm — người mở PR chịu trách nhiệm" |
| AI có làm giảm chất lượng code không? | Values | **#9** | AI **khuếch đại cái sẵn có**: quy ước rõ thì nhanh hơn, quy ước trong đầu vài người thì chỗ lệch lan nhanh hơn |
| Em xử lý thế nào với người không muốn dùng AI | Scaling | 🟡 nguyên tắc | bắt đầu từ việc họ mất thời gian nhất · cho thấy trên chính việc của họ · không ép ở chỗ chi phí sai cao |
| Em tự dựng công cụ AI nào chưa | Iteration | `07` Phần V.3 | một nguồn sự thật → đồng bộ 4 nền tảng · 48 commit / 2,5 tháng |
| Em làm gì khi AI lặp lại cùng một lỗi | Iteration | `07` Phần V.3 | "thay vì sửa từng lần, em ghi nó thành luật" |
| Cân bằng tốc độ và kiểm chứng | Values | `07` Q3 | không đưa vào code thứ chưa đọc hiểu · không giải thích được thì viết lại |
| AI có thay thế được developer không | Values | mục 16 | rủi ro không phải AI viết sai — mà là **bỏ qua bước hiểu vấn đề** |
| Em nghĩ gì về human-in-the-loop trong Copilot | Judgment | `07` Phần III.3 | 🎯 tính năng của em cũng vậy: AI sinh form, người dùng chỉnh rồi mới gửi · **ranh giới là hành động có đảo ngược được không** |

---

## F · GIÁ TRỊ & 9 LEADING PRINCIPLES — phút 52–58

*Có thể xen vào bất cứ lúc nào, không chỉ cuối buổi.*

| Câu hỏi | Ghi nhớ |
|---|---|
| ⭐ Em thấy principle nào khó nhất với mình? | → **kịch bản đầy đủ ở phần B** — chọn *hire the most likely to win*, vế **đưa** feedback |
| Principle nào em thấy đúng với mình nhất? | → **kịch bản đầy đủ ở phần B** |
| Ownership với em nghĩa là gì? | ngắn + neo ngay ví dụ #5: không dừng ở vá một chỗ, rà cả 5 tính năng |
| Customer view nghĩa là gì? | #5: đứng ở chỗ người dùng mất 25 phút · #1: người không biết JSON giờ dùng được |
| Em nghĩ gì về "merits, not entitlements"? | không nói về chức danh · #4: phương án được chọn vì đúng, không vì ai đề xuất |
| Em nghĩ gì về "seek truth and commit"? | #3: bỏ hướng của mình rồi làm hết mình theo hướng của lead |
| Em định nghĩa thành công thế nào | kết quả người dùng, không phải task done |

---

## G · CÂU HỎI CẤP CEO

| Câu hỏi | Ghi nhớ |
|---|---|
| ⭐ Vì sao rời công ty hiện tại | `04-stories` · thứ muốn tới, không phải thứ muốn thoát |
| ⭐ Ba tới năm năm nữa em muốn gì | 1–2 năm nắm một mảng đầu-cuối → 3 năm người khác tìm tới → 5 năm hướng kỹ thuật, để mở · nối vào L&D toàn cầu + onsite châu Âu |
| Em mong đợi gì ở vị trí này | hệ thống lớn, dữ liệu thật, đồng nghiệp giỏi hơn để học |
| Em thích môi trường làm việc thế nào | tự chủ, nói thẳng, có người review mình · khớp "flat, appreciate your idea" |
| Điều gì làm em có động lực | thấy thứ mình làm có người dùng thật |
| Em xử lý thế nào khi không đồng ý với hướng công ty | story #3 + "seek truth and commit": nói thẳng bằng dữ liệu, quyết rồi thì làm hết mình |
| Kỳ vọng mức lương | *(chuẩn bị riêng — không nằm trong tài liệu này)* |

---

## H · FOLLOW-UP CHẮC CHẮN BỊ ĐÀO

Head of Engineering sẽ đào 2 tầng. Bốn câu nguy hiểm nhất:

| Câu | Trả lời ngắn |
|---|---|
| ⚠️ **AI làm bao nhiêu phần ở #1?** | AI dựng giao diện theo mô tả của em; kiến trúc, nối API, review là của em — **2 tuần refactor sau đó cho thấy output không dùng thẳng được** |
| ⚠️ **Retry 5 lần thì idempotency thế nào?** | có xử lý → *(điền cơ chế: request id / khoá theo trạng thái form / backend chống trùng)* |
| ⚠️ **`flushSync` có tác dụng phụ gì?** | buộc commit đồng bộ, mất gộp update, tốn hơn nếu lạm dụng — chỉ dùng đúng điểm trước redirect |
| ⚠️ **11 tháng chưa go-live?** | kế hoạch phát hành, đang clear backlog — **nói chủ động ngay câu mở đầu, đừng đợi bị hỏi** |

Các câu chuẩn cho mọi story: *làm lại sẽ khác gì · phần khó nhất · đo thành công thế nào · sau đó thì sao · còn ai tham gia và họ làm gì*.

---

## I · CÂU HỎI EM HỎI NGƯỢC LẠI — phút 58–65

*Không phải phần phụ. Nhiều người phỏng vấn chốt đánh giá ở đây. Hỏi 3–4 câu.*
Bộ câu hỏi chi tiết ở `00-hrs-master-guide` mục 17. Gợi ý phân bổ:

| Hỏi ai | Câu |
|---|---|
| **Head of Eng** | Quy trình từ lúc PM đưa mục tiêu tới lúc ship ở đây trông thế nào? · Team frontend hiện đang đau nhất ở đâu? · Code review và chất lượng được vận hành ra sao? |
| **CEO chi nhánh** | Chi nhánh Việt Nam đóng vai trò gì trong roadmap của tập đoàn? · Trong 12 tháng tới điều gì quyết định chi nhánh thành công? · Người làm tốt ở đây trong 6 tháng đầu thường làm được gì? |
| **Cả hai** | Nếu em vào, ba tháng đầu thành công trông như thế nào? |

⚠️ Đừng hỏi những thứ tra được trên web. Đừng hỏi lương ở câu đầu tiên.

---

## J · CÂU CHƯA CÓ STORY — xử lý thế nào nếu bị hỏi

| Câu | Cách xử lý |
|---|---|
| 🔴 Một lần em bị hiểu nhầm | Nếu chưa kịp chuẩn bị: dùng #4 ở góc khác — em chất vấn yêu cầu và ban đầu có thể bị hiểu là muốn né việc khó, nên em phải nói rõ mình đang hỏi về mục tiêu chứ không phản bác giải pháp |
| ~~Feedback gần đây từ quản lý~~ | ✅ đã có story **#10** *(và #8 làm dự phòng)* |
| Em từng quản lý ai chưa | Chưa, nhưng #3 là lần em dẫn dắt về mặt kỹ thuật cho 6 team |
| Kinh nghiệm với .NET / hệ thống lớn | Thành thật: chưa · nêu tốc độ học qua #1 tự học backend và #2 ADK |

> Nguyên tắc chung khi bí: **thành thật là chưa có, rồi đề nghị một story gần nhất.** *"Em chưa gặp đúng tình huống đó. Em có một chuyện gần với nó — anh/chị có muốn nghe không?"* Bịa là hỏng ở follow-up đầu tiên.

---

## BA CÂU THẦN CHÚ TRƯỚC KHI VÀO PHÒNG

1. **Dừng ba giây** sau mỗi câu hỏi — tự hỏi: câu này đang đo signal gì?
2. **"Em"**, không phải "chúng em". Nói thứ **em** làm.
3. **Nói phần phòng thủ trước khi bị hỏi** — chưa go-live vì kế hoạch phát hành · AI là công cụ, quyết định là của em.
