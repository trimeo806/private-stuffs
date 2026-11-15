# 10 Story — bản CARL hoàn chỉnh (tiếng Việt, mid-level, HRS)

Cách dùng: đọc to, bấm giờ. Mục tiêu **2–4 phút**. Quá 4 phút là dài.
Người phỏng vấn: **CEO chi nhánh Việt Nam + Head of Engineering** — cả hai có nền kỹ thuật.
Xưng hô: **"em"** xuyên suốt.
Nguyên tắc kể: mở bằng vế business → đào sâu kỹ thuật ở giữa → kết bằng vế business.
Mỗi story có: câu mở đầu → C → A (chia theme) → R → L → đáp án follow-up → dùng cho câu hỏi nào.

Ký hiệu `[~30s]` là mốc thời gian mục tiêu cho đoạn đó.

**Leading Principle luôn gọi bằng TÊN, không bao giờ bằng số** — tài liệu master của em đánh số khác trang tuyển dụng HRS, gọi số là gọi nhầm.

📄 **File này sở hữu:** 10 story CARL đầy đủ · đáp án follow-up · hai câu cấp CEO.
📄 **`08-question-bank.md` sở hữu:** mọi khung trả lời và mọi kịch bản đọc thành lời — Bộ ba *(mục A, C1, C5)*, "vì sao phù hợp HRS" và hai câu principle *(mục B)*, bảng câu hỏi 65 phút.
⚠️ Chỗ nào hai file nói khác nhau thì **`08` đúng**.

---

# BỘ BA — đã chuyển sang `08-question-bank.md`

Khung trả lời và kịch bản đọc thành lời cho ba câu chắc chắn có giờ nằm hết ở question bank, đúng vị trí trong dòng thời gian 65 phút:

| Câu | Story | Ở đâu |
|---|---|---|
| "Kể một chút về em" | *(cắm móc #3 · #4 · #2)* | `08` mục **A** — phút 0–5 |
| "Project em tâm đắc nhất" | **#1** | `08` mục **C1** — phút 12–35 |
| "Kể về một lần xung đột" | **#4** → nối **#3** | `08` mục **C5** — phút 12–35 |

Bảng chỉ mục Bộ ba nằm ngay trên mục A của `08`, kèm bảng rủi ro không đối xứng và sơ đồ ba móc trỏ đi đâu.

**File này giữ vai trò kho:** 10 story CARL đầy đủ, đáp án follow-up, và hai câu cấp CEO ở cuối file. Khi `08` nói *"rút story #4"* thì chi tiết nằm ở đây.

---

# STORY 1 — iLetter: từ bản thử nghiệm thành module production

📄 **Bản nói thành lời cho câu "project tâm đắc nhất" nằm ở `08-question-bank.md` mục C1.** Phần dưới đây là kho chi tiết và kho follow-up.

**Story dài (11 tháng) → bắt buộc đọc mục lục sau Context.**

### Câu mở đầu — front-load `[~25s]`

> "Module khảo sát trong sản phẩm chính của công ty không có giao diện — muốn tạo một form thì phải tự viết JSON đúng cấu trúc rồi gửi qua API, nên trên thực tế chỉ dev dùng được. Em nhận làm bản thử nghiệm giao diện, bản đó được duyệt triển khai production, và em làm chính module này hơn 11 tháng."

### Mục lục `[~15s]`

> "Em xin đi qua ba phần: thứ nhất là dựng bản thử nghiệm và đưa nó qua được cửa duyệt; thứ hai là refactor lại khi nó trở thành module thật và có người thứ hai vào; thứ ba là tự học backend để tự làm phần dữ liệu và mở rộng tính năng cùng PO và designer."

### C — Context `[~30s]`

Sản phẩm có sẵn module khảo sát nhưng không có UI. CEO đưa ra chủ đề dùng AI để dựng giao diện. Team lúc đó gồm em, một designer và PO. Bên em không có BA hay tech lead nào dịch yêu cầu thành spec — CEO nêu mục tiêu, còn phần biến nó thành thứ làm được là em cùng PO và designer tự làm. Công ty là công ty Thuỵ Sĩ nên phía stakeholder ở bên đó.

### A — Actions

**Theme 1 — Dựng bản thử nghiệm và đưa nó qua cửa duyệt** `[~40s]`

Em nhận làm phần frontend. Em dùng AI như công cụ dựng UI, còn phần em quyết là cấu trúc màn hình, cách nó nối vào các API sẵn có, và review lại toàn bộ output trước khi đưa vào. Em làm tới mức chạy được đầu-cuối chứ không dừng ở mockup, vì em biết muốn được duyệt thì phải cho thấy nó thật sự gửi được form đi. Kết quả là bản thử nghiệm được duyệt để triển khai production, và em làm chính module đó khoảng 4 tháng trước khi công ty cấp thêm một dev.

**Theme 2 — Refactor khi nó thành module thật** `[~40s]`

Khi có người thứ hai vào, em thấy code không còn chịu được nữa nên em chủ động refactor, không chờ ai yêu cầu. Em tách các lớp riêng — lớp xử lý tích hợp dữ liệu, lớp state phía client, model, controller — để UI không dính thẳng vào luồng dữ liệu, người mới vào đọc theo lớp là hiểu. Em rà lại logic bỏ trùng lặp, dọn hard-code và magic number. Việc này tốn 2 tuần, cộng thêm 2–3 tuần theo dõi và xử lý lỗi phát sinh sau đó.

**Theme 3 — Tự học backend và mở rộng tính năng** `[~30s]`

Vì em phụ trách cả UI lẫn dữ liệu, em tự học backend để tự làm phần đó thay vì đẩy sang người khác. Sau đó em làm việc với PO và designer để mở rộng: đóng form khảo sát, đẩy dữ liệu sang Google Sheet và một số nền tảng bên thứ ba.

### R — Results `[~40s]`

Đo được: với form phức tạp có logic điều hướng và webhook, thời gian tạo giảm từ khoảng 20 phút xuống khoảng 10 phút.

Nhưng thay đổi lớn hơn nằm ở chỗ **ai làm được**. Trước đây chỉ người viết được JSON mới tạo được form; giờ người không biết kỹ thuật cũng tự làm. Đó là mở ra khả năng mới, không chỉ nhanh hơn. Và sau khi thấy bản thử nghiệm, công ty cấp thêm một dev cho module.

*(Trạng thái go-live để dành cho follow-up "sau đó thì sao", đừng nói ở đây.)*

### L — Learnings `[~25s]`

Bài học lớn nhất là em đã nhảy vào code quá sớm. Nếu làm lại, em sẽ làm rõ yêu cầu trước, chốt các yêu cầu phi chức năng, xác định MVP, dựng data model và luồng dữ liệu, thiết kế kiến trúc rồi mới bắt đầu. Cái giá của việc bỏ qua bước đó là 2 tuần refactor cộng 2–3 tuần xử lý hậu quả — tức là gần một tháng, và nó hoàn toàn tránh được.

### Follow-up đã chuẩn bị

**Phần khó nhất?** — Không phải phần code. Khó nhất là refactor khi module đã chạy và có người thứ hai đang làm song song: vừa phải tách lớp vừa không được làm hỏng tính năng đang có, nên 2–3 tuần sau đó em vừa làm tính năng mới vừa xử lý lỗi phát sinh.

**Đo thành công thế nào?** — Thành thật là ban đầu em không đặt baseline, đó là thiếu sót. Cái em đo được sau này là thời gian tạo form và việc người không biết JSON dùng được. Trước khi go-live em sẽ chốt chỉ số cụ thể để đo. *(Nếu bạn chốt được 2–3 chỉ số với PO thì nói luôn ở đây.)*

**Sau đó thì sao?** — Module đang chờ go-live trong 3 tháng, hiện dùng nội bộ. Em vẫn đang làm chính.

**AI làm bao nhiêu phần?** — AI dựng phần giao diện theo mô tả của em. Cấu trúc màn hình, cách nối API, và toàn bộ phần review, refactor, kiến trúc là của em. Thực tế là 2 tuần refactor sau đó cho thấy output ban đầu không dùng thẳng được.

**Vì sao 11 tháng vẫn chưa go-live?** — Nó không nằm ở tiến độ của em mà ở kế hoạch phát hành: đang clear backlog và chờ đủ bộ tính năng. Trong lúc chờ, em dùng thời gian đó rà lại error handling và các trường hợp lỗi — đó chính là câu chuyện em có thể kể riêng.

**✂️ Rút ngắn khi cần (4,5 → 3,7 phút):** bỏ Theme 3, thay bằng một câu — *"em cũng tự học backend để tự làm phần dữ liệu, và mở rộng thêm vài tính năng cùng PO và designer"* — rồi để dành cho follow-up.

### Dùng cho câu hỏi

Project tự hào nhất · Project thử thách nhất · Ownership · Tự học cái mới · Làm việc với stakeholder cấp cao

---

# STORY 2 — Dịch vụ AI dùng chung từ workshop ADK

**Story ngắn. Mục tiêu 2 phút. Không phóng đại — mới có 1 module dùng.**

### Câu mở đầu `[~20s]`

> "Sau một workshop nội bộ, em đề xuất và xây một service AI dùng chung cho hệ thống, thay vì gắn cứng AI vào riêng module của em. Không ai giao việc này, em mang ý tưởng đi thuyết phục và được team AI của công ty cùng CEO duyệt. Hiện service đang ở giai đoạn dev và mới có module của em tích hợp."

### C — Context `[~25s]`

Công ty mỗi năm có một Dev Day, tất cả developer ngồi lại chia sẻ. Năm đó có hai kỹ sư AI của Google sang làm workshop, giới thiệu ADK — bộ công cụ cho phép tự dựng agent workflow, tự định nghĩa tools và skills, thay vì phụ thuộc hoàn toàn vào sản phẩm bên thứ ba. Module của em lúc đó đang có một điểm đau rõ: người dùng phải tự ghép từng câu hỏi trong form, một form thường 10–15 câu.

### A — Actions `[~60s]`

Em thấy chỗ áp dụng được ngay. Trước đó em đã tìm hiểu và biết các nhà cung cấp LLM đều cho tích hợp qua endpoint, nên em đề xuất xây một service AI riêng, làm cho module của em trước.

Quyết định thiết kế quan trọng nhất của em là **không đóng cứng nó vào module mình**. Module khác muốn dùng chỉ cần vào repository khai báo endpoint, tools, instructions và output schema của họ, rồi gọi service để lấy kết quả. Em chọn hướng này vì nếu mỗi module tự tích hợp AI riêng thì sau này sẽ có năm sáu chỗ gọi LLM khác nhau, không quản được chi phí lẫn chất lượng.

Em đưa đề xuất lên team AI của công ty và CEO. Cả hai duyệt.

Về phía người dùng, em làm ba thứ: tạo form từ một prompt thay vì tự ghép 10–15 câu hỏi; đọc bộ câu hỏi có sẵn ở dạng PDF, docx hoặc ảnh rồi dựng thành form để người gửi không phải gõ lại; và tổng hợp các câu trả lời thu về để người gửi ra quyết định nhanh hơn.

### R — Results `[~20s]`

Service đang ở giai đoạn dev, mới có module của em tích hợp. Kết quả cụ thể nhất hiện có: người dùng không còn phải tự ghép 10–15 câu hỏi mà mô tả bằng một prompt rồi chỉnh lại. *(Bổ sung con số thời gian khi bạn xác nhận.)* Về mặt tổ chức, việc nó được team AI và CEO duyệt cho làm là tín hiệu rõ nhất ở thời điểm này.

### L — Learnings `[~25s]`

Em thiết kế cho khả năng dùng lại nhưng chưa đi hỏi các module khác xem họ có nhu cầu thật không — nên đến giờ vẫn chỉ có một bên dùng, là chính em. Lần sau em sẽ nói chuyện với hai ba team trước khi tổng quát hoá thiết kế, hoặc làm hẹp trước và chỉ mở rộng khi có người thứ hai thật sự cần. Em đã tối ưu cho một tương lai mà em chưa kiểm chứng.

### Follow-up

**Vì sao tự dựng thay vì gọi thẳng provider?** — Gọi thẳng thì mỗi module sẽ tự quản prompt, schema và chi phí riêng, không kiểm soát được. Service ở giữa cho phép chuẩn hoá output schema và đổi provider mà module không phải sửa.

**Chi phí LLM tính thế nào?** — *(cần bạn bổ sung nếu có)*

**Làm sao đảm bảo AI không sinh form sai?** — Ràng buộc bằng output schema, và người dùng luôn chỉnh lại trên form đã sinh chứ không gửi thẳng.

**Phần khó nhất?** — *(cần bạn bổ sung: prompt hay schema? xem mục Perseverance cuối file)*

### Dùng cho câu hỏi

Chủ động làm việc ngoài trách nhiệm · Đề xuất cái mới · Học công nghệ mới · Nghĩ dài hạn

---

# STORY 3 — Em bảo vệ quan điểm, rồi nhận ra lead đúng và làm lại theo hướng của chị

**Hai câu mở đầu, chọn theo câu được hỏi.**

### Câu mở đầu — bản A, cho câu "bất đồng nhưng quyết định đi ngược ý bạn" `[~33s]`

> "Em nhận làm buổi chia sẻ về React performance cho 6 feature team. Trong lúc chuẩn bị em bất đồng với lead trực tiếp về cách trình bày, và em bảo vệ quan điểm của mình đủ lâu để buổi trao đổi trở nên căng. Sau khi ngồi lại nghe kỹ, em thấy chị đúng và em sai. Em bỏ hướng của mình, làm lại theo hướng của chị — và kết quả sau đó cho thấy chị đúng."

### Câu mở đầu — bản B, cho câu "bất đồng với đồng nghiệp / ảnh hưởng người khác" `[~23s]`

> "Em review PR React cho các feature team và thấy cùng một nhóm lỗi lặp ở 6 team. Em nhận làm buổi chia sẻ về React performance cho toàn công ty, 30 dev tham dự. Khoảng một tháng sau, lượng comment của em trong PR về đúng nhóm lỗi đó giảm chừng 30%."

### C — Context `[~35s]`

Team em review PR phần UI cho 6 feature team, mà các team đó phần lớn là dev Java mới chuyển sang React và Next.js. Cùng một nhóm lỗi lặp lại: state đặt cao hơn mức cần, useMemo và useCallback rải khắp nơi, useEffect bị dùng để ép render thay vì để sự kiện kích hoạt. Comment từng PR không giải quyết được gốc, nên em nhận làm một topic cho Dev Day, với mục tiêu rõ: nghe xong thì các team kiểm soát được re-render không cần thiết. Chuẩn bị 2 tháng, 3 buổi rehearsal.

### A — Actions

**Theme 1 — Quan điểm ban đầu của em** `[~28s]`

Quan điểm của em: muốn tối ưu thì phải hiểu cơ chế render, và cơ chế đó nên diễn đạt bằng sơ đồ. Em đã dựng khá nhiều sơ đồ, phần lý thuyết đó nặng và đi sâu. Lead muốn ngược lại: giảm sơ đồ, nói lý thuyết bằng lời và làm nhẹ đi, vì lo người nghe mất mạch.

**Theme 2 — Lúc căng và cách em xử lý** `[~21s]`

Trao đổi một lúc thì căng, vì em liên tục đưa lý do để giữ sơ đồ. Em nhận ra mình đang tranh luận để thắng chứ không phải để ra được bài tốt hơn, nên em đề nghị tạm nghỉ thay vì cố nói tiếp.

**Theme 3 — Em đổi ý, và em làm lại thế nào** `[~55s]`

Quay lại, em hỏi lại từ đầu để hiểu chị lo điều gì. Điểm em bỏ sót là mục tiêu của chính buổi chia sẻ: nó không phải để giảng cơ chế React, mà để các team kiểm soát được re-render trong code của họ. Thời lượng có hạn, nên mỗi phút em dành cho lý thuyết là một phút lấy khỏi phần phương pháp.

Thấy vậy thì em đồng ý, và em không làm nửa vời. Em viết lại phần lý thuyết thành lời dẫn ngắn, chỉ giữ đủ để hiểu vì sao các phương pháp hoạt động, rồi dồn thời lượng vào phần phương pháp: đặt state đúng chỗ, dùng useMemo, useCallback, React.memo đúng lúc, không dùng useEffect để ép render. Em tự cắt thêm cả những sơ đồ chị không yêu cầu cắt, và tách bản chi tiết ra ngoài bài để ai muốn xem sâu thì em gửi.

### R — Results `[~33s]`

Buổi đó 30 dev tham dự và 6 câu hỏi — cả 6 đều xoay quanh viết code React tối ưu, tức là đúng phần phương pháp, không câu nào hỏi phần lý thuyết em từng muốn giữ. Sau đó hai đồng nghiệp tự debug được lỗi thật nhờ buổi hôm ấy. Khoảng một tháng sau, comment của em trong PR về nhóm lỗi này giảm chừng 30% — đây là quan sát của em, chưa đo bằng số liệu hệ thống.

### L — Learnings `[~35s]`

Em tranh luận về **cách trình bày** một phần nội dung, mà lẽ ra phải hỏi trước là phần đó **có đáng chiếm nhiều chỗ như vậy không**. Em bám vào thứ em thấy hay thay vì bám vào kết quả em muốn người nghe đạt được. Từ đó, trước khi viết bất cứ tài liệu hay bài chia sẻ nào, việc đầu tiên em làm là viết ra một câu — nghe xong thì người nghe làm được gì — rồi cắt mọi thứ không phục vụ câu đó.

### Follow-up đã chuẩn bị

**Hai lỗi mà đồng nghiệp debug được là gì?** — Một chị senior tìm ra lỗi ô input mất focus nhấp nháy sau mỗi lần gõ, nguyên nhân là component bị re-render. Một chị khác tìm ra lỗi dùng object và array làm dependency của useEffect ở component con, khiến effect chạy lại mỗi lần component cha render dù giá trị không đổi; chị áp dụng useMemo, useCallback và React.memo để xử lý.

**Bạn thật sự đồng ý hay chỉ làm cho xong?** — Thật sự đồng ý, và kiểm chứng được bằng hành vi: em viết lại toàn bộ phần lý thuyết chứ không chỉ xoá vài slide, và em tự cắt thêm những sơ đồ chị không yêu cầu cắt. Nếu chỉ nhượng bộ cho xong thì em đã cắt đúng số tối thiểu.

**Nếu sau đó bạn thấy chị sai thì sao?** — Thì em vẫn làm hết mình cho lần này rồi mang dữ liệu quay lại. Ở buổi này cách kiểm chứng có sẵn là phản hồi và câu hỏi của người nghe, và thực tế cho thấy hướng của chị đúng.

**Bài học thứ hai của bạn là gì?** — Em mất khá nhiều thời gian bảo vệ quan điểm trước khi thật sự hiểu lo ngại của lead. Hỏi để hiểu trước, bảo vệ sau, thì em đã tới cùng kết luận mà không cần đoạn căng thẳng ở giữa. Và đổi ý khi thấy mình sai không làm mình yếu đi — cái làm mình yếu đi là đổi ý cho xong rồi làm nửa vời.

**Phần khó nhất?** — Không phải nội dung kỹ thuật. Khó nhất là lúc nhận ra mình đang tranh luận để thắng, và dừng lại.

**Quan hệ với lead sau đó?** — Tốt hơn trước. Ba buổi rehearsal sau đó chị góp ý rất nhiều và em nhận hết. Và sau buổi thuyết trình, chị chủ động nói với em rằng chị xin lỗi vì lúc đầu đã không chịu hiểu ý em, và chị tự hào vì buổi hôm đó chạy tốt. Em nghĩ chuyện đó xảy ra được là vì cả hai đều đã lùi lại một bước ở đúng lúc căng nhất.

**Vì sao ban đầu bạn để phần lý thuyết nặng như vậy?** — Vì em xuất phát từ nội dung chứ không từ mục tiêu. Em hiểu cơ chế render nên muốn truyền đạt cho trọn vẹn, và đi thẳng vào việc trình bày phần đó sao cho hay, không dừng lại hỏi phần đó nên chiếm bao nhiêu phần trăm thời lượng.

**Bỏ bớt lý thuyết thì người nghe có hiểu được phương pháp không?** — Em vẫn giữ phần lý thuyết tối thiểu để họ hiểu vì sao các phương pháp hoạt động, chỉ là nói bằng lời và ngắn hơn nhiều. Sáu câu hỏi hôm đó cho thấy họ theo được.

**Làm sao biết comment giảm 30%?** — Quan sát của em khi review, không phải số đo hệ thống. Em để ý là mình không còn phải viết lại cùng một loại comment nữa.

**✂️ Rút ngắn khi cần (4,8 → 4,0 phút):** gộp Theme 1 vào Theme 2 — *"em muốn dùng nhiều sơ đồ cho phần cơ chế render, lead muốn làm nhẹ phần đó và nói bằng lời"* — bỏ đoạn mô tả riêng quan điểm hai bên.

### Dùng cho câu hỏi

Bất đồng nhưng quyết định không theo ý bạn *(mở đầu A)* · Bất đồng với đồng nghiệp/quản lý *(mở đầu B)* · Nhận ra mình sai · Nhận feedback · Ảnh hưởng người khác không có quyền hạn · Mentor · Giải thích thứ kỹ thuật

⚠️ **Giới hạn:** đây là trường hợp bạn **được thuyết phục** rồi mới thực thi, không phải bị áp một quyết định mà vẫn không đồng ý. Nếu bị hỏi đúng vế đó thì cần story khác.

---

# STORY 4 — Ẩn danh người trả lời và luật bảo vệ dữ liệu Thuỵ Sĩ

📄 **Bản nói thành lời cho câu xung đột nằm ở `08-question-bank.md` mục C5.** Phần dưới đây là kho chi tiết và kho follow-up.

**Story mạnh nhất cho HRS. Ưu tiên dùng khi có cơ hội.**

### Câu mở đầu `[~25s]`

> "Module của em cần tính năng trả lời khảo sát ẩn danh. Yêu cầu ban đầu đưa xuống khá nặng: phải tách hai chỗ lưu, mã hoá phần định danh, nhưng vẫn de-anonymize được khi audit. Em ước khoảng 4 ngày. Thay vì làm luôn, em đi hỏi lại vì sao tính năng này tồn tại — và sau một buổi trao đổi 6 người có cả legal, họ chốt đi theo phương án em mang tới, chỉ mất 1 ngày mà vẫn đạt đúng mục tiêu."

### C — Context `[~30s]`

Có những khảo sát lấy phản hồi mà người trả lời cần được bảo vệ danh tính. Công ty em là công ty Thuỵ Sĩ nên tính năng dạng này bắt buộc phải qua bộ phận legal, luật bảo vệ dữ liệu ở đó rất chặt. Yêu cầu đưa xuống là: ẩn danh, không lưu chung phần định danh với câu trả lời, nhưng vẫn phải de-anonymize được khi có yêu cầu phục vụ audit hoặc điều tra.

### A — Actions `[~70s]`

Em bắt đầu nghiên cứu hướng đó, tìm cách lưu tách hai nơi. Nhưng càng làm em càng thấy có một thứ chưa rõ: **mục tiêu thật sự của tính năng là gì** — ai cần được bảo vệ khỏi ai.

Nên thay vì implement nguyên văn yêu cầu, em chuẩn bị một buổi trao đổi. Em đi vào với ba câu hỏi cụ thể: vì sao tính năng này tồn tại; nếu lưu tách hai nơi thì chi phí duy trì về sau ra sao; và nếu không tách thì rủi ro cụ thể là gì.

Em không đi tay không — em mang theo một phương án thay thế đã nghĩ sẵn: vẫn lưu chung, nhưng chặn ở tầng server, không bao giờ trả danh tính người trả lời về phía client.

Buổi đó có 6 người, gồm cả legal. Khi bóc ra thì nhu cầu thật là **người gửi khảo sát không được biết ai đã trả lời** — chứ không phải nhu cầu tách hạ tầng lưu trữ. Việc tách hai chỗ là không cần thiết cho mục tiêu đó, nên các bên chốt đi theo phương án của em.

Legal đặt lại hai ràng buộc: việc lọc phải diễn ra ở phía server, và sau khi gửi xong thì xoá phần định danh của người nhận. Em xác nhận lại cả hai với họ và làm đúng như vậy — em không tự diễn giải phần luật.

### R — Results `[~25s]`

Toàn bộ quá trình từ lúc nhận yêu cầu tới lúc chốt mất 1 tuần.

Phương án ban đầu em ước khoảng 4 ngày: 2 ngày cho phần tách chỗ lưu, 1 ngày cho lưu và mã hoá định danh, 1 ngày cho phần câu trả lời. Phương án chốt mất 1 ngày, đã gồm test — giảm khoảng 75% khối lượng cho cùng một mục tiêu nghiệp vụ. Và về lâu dài bên em không phải duy trì hai kho dữ liệu cùng một cơ chế de-anonymization.

### L — Learnings `[~25s]`

Bài học là hỏi "vì sao cần tính năng này" ngay từ đầu, thay vì nhận yêu cầu rồi đi tìm cách làm. Ở đây phần phức tạp nhất của yêu cầu — tách kho và de-anonymize — hoá ra không phục vụ mục tiêu thật, mà nếu em im lặng làm thì bên em đã phải nuôi nó nhiều năm. Điều thứ hai em rút ra là khi chất vấn một yêu cầu, phải mang sẵn phương án thay thế; nếu chỉ đến để hỏi thì buổi họp sẽ dừng ở chỗ hoài nghi chứ không ra được quyết định.

### Follow-up

**Vì sao vẫn an toàn khi lưu chung?** — Vì việc lọc nằm ở tầng server, client không bao giờ nhận được phần định danh, và định danh người nhận bị xoá sau khi gửi. Đây là hai ràng buộc legal đặt ra và em xác nhận lại với họ.

**Nếu sau này thật sự cần audit thì sao?** — Đó chính là câu em đặt ra trong buổi họp. Kết luận của các bên là nhu cầu đó không tồn tại với loại khảo sát này, nên không cần xây trước.

**Bạn có sợ mình đang cắt góc về privacy không?** — Chính vì sợ nên em mới đưa lên legal thay vì tự quyết. Em giảm khối lượng công việc, không giảm mức bảo vệ.

**Đo thành công thế nào?** — Bằng effort tránh được (4 ngày xuống 1 ngày) và bằng việc legal ký duyệt phương án.

**Phần khó nhất?** — Đặt câu hỏi ngược lại một yêu cầu đã được đưa xuống, mà không làm người đưa yêu cầu thấy mình bị bắt bẻ. Em xử lý bằng cách hỏi về mục tiêu chứ không phản bác giải pháp của họ.

**✂️ Rút ngắn khi cần (4,7 → 3,9 phút):** trong Actions, gộp ba câu hỏi chuẩn bị thành một câu — *"em đi vào với ba câu hỏi: vì sao có tính năng này, chi phí duy trì về sau, và rủi ro nếu không tách"*. Trong Results, bỏ phần bóc tách 2+1+1 ngày, chỉ nói 4 ngày xuống 1 ngày.

### Dùng cho câu hỏi

Yêu cầu không rõ ràng · Làm nhiều với ít nguồn lực · Thuyết phục stakeholder · Đơn giản hoá · Làm việc với bộ phận ngoài kỹ thuật · Vì sao muốn làm ở môi trường châu Âu

---

# STORY 5 — Mất 25 phút cấu hình của người dùng vì em thiếu xử lý lỗi

### Câu mở đầu `[~25s]`

> "Đây là lỗi của em. Em làm một luồng tạo form mà người dùng phải bỏ ra khoảng 25 phút cấu hình, và em không xử lý trường hợp gọi backend thất bại ở bước cuối. Lỗi xảy ra là mất sạch, phải làm lại từ đầu. Em tự phát hiện ra, và em không dừng ở việc vá chỗ đó — em rà lại toàn bộ 5 tính năng có gọi backend trong module."

### C — Context `[~25s]`

Để gửi được một form, người dùng phải thêm hàng loạt thành phần, cấu hình logic điều hướng giữa các câu hỏi, khai báo webhook payload và response để gọi sang hệ thống bên thứ ba. Tính ra khoảng 25 phút cho một form đầy đủ. Có lần service bị down và em phát hiện bước gửi form bị lỗi.

### A — Actions `[~55s]`

Vấn đề không nằm ở chỗ nó lỗi — service nào cũng có lúc lỗi. Vấn đề là em đã không xử lý thử lại cho bước đó, nên người dùng không có cách nào ngoài làm lại từ đầu.

Em tự đặt mình vào vị trí người dùng: mất 25 phút cấu hình vì một lần gọi mạng hỏng là không chấp nhận được. Nên em không vá đúng chỗ đó rồi thôi.

Em rà lại toàn bộ 5 tính năng có gọi backend trong module — đóng khảo sát, lưu trữ, xoá, tải form lên, và cập nhật form khi đang tạo dở — với giả định là service có thể chết bất cứ lúc nào.

Cách xử lý em chọn: người dùng thử lại được ngay, hệ thống tự thử lại tối đa 5 lần; nếu vẫn không được thì dữ liệu form đã được lưu ở phía backend nên họ quay lại làm tiếp chứ không mất gì. Em chọn kết hợp cả hai thay vì chỉ retry, vì retry giải quyết lỗi thoáng qua còn persist mới cứu được trường hợp service chết lâu.

Cuối cùng em đưa việc này thành checklist chung cho team, để không phụ thuộc vào việc từng người có nhớ hay không.

### R — Results `[~20s]`

Sau khi sửa, trường hợp này không còn làm mất dữ liệu của người dùng: hoặc tự thử lại thành công, hoặc họ quay lại làm tiếp từ chỗ đang dở. 5 tính năng còn lại được rà cùng một chuẩn. Và checklist thì áp dụng cho cả những tính năng làm sau này, không chỉ chỗ em đã sửa.

### L — Learnings `[~25s]`

Trước đó em làm theo happy path: tính năng chạy đúng là coi như xong. Từ lần này em hiểu ra khái niệm yêu cầu phi chức năng một cách cụ thể chứ không phải trên lý thuyết. Giờ trước khi bắt tay vào bất cứ tính năng nào em đều hỏi trước: lỗi thì xảy ra chuyện gì, có edge case nào, người dùng mất gì nếu hỏng giữa chừng. Và cái em nhớ nhất không phải là bug — mà là 25 phút của người dùng, vì đó mới là thứ quyết định họ có quay lại dùng nữa hay không.

### Follow-up

**Vì sao chọn 5 lần?** — Đủ để vượt qua lỗi mạng thoáng qua mà không làm người dùng chờ quá lâu. Sau 5 lần thì gần như chắc chắn là lỗi phía service chứ không phải nhiễu, lúc đó persist mới là cách đúng.

**Có trường hợp nào retry không an toàn không?** — Có, và em có xử lý idempotency nên client thử lại 5 lần thì backend vẫn chỉ ghi nhận một lần. *(Điền cơ chế đúng của bạn: gửi kèm request id · khoá theo trạng thái form ở backend · backend tự chống trùng. Head of Engineering gần như chắc chắn hỏi tiếp cơ chế.)*

**Checklist gồm những gì?** — *(bổ sung 3–4 mục cụ thể)*

**Đo thành công thế nào?** — *(nếu từ đó tới nay không còn case mất dữ liệu, nói rõ khoảng thời gian)*

**Sao không phải QA bắt được?** — Vì nó chỉ xảy ra khi backend thật sự down, không phải luồng test thông thường. Đó cũng là lý do em chuyển sang giả định service có thể chết bất cứ lúc nào thay vì chờ test bắt.

### Dùng cho câu hỏi

Sai lầm của bạn · Bài học lớn nhất · Bạn hiểu người dùng thế nào · Chất lượng · Nhận feedback/tự phát hiện vấn đề

---

# STORY 6 — DỰ PHÒNG: sửa code người khác hồi mới đi làm

**Chỉ dùng khi họ hỏi thẳng về lỗi thời mới vào nghề. Mục tiêu 60 giây. Mở đầu bắt buộc: "Lúc đó em mới đi làm."**

**C:** Em thấy một anh senior viết `number > 0 ? value : null` để hiển thị UI.

**A:** Em nghĩ viết `number && value` gọn hơn nên sửa, mà không hỏi anh ấy.

**R:** Trong JavaScript `&&` trả về giá trị falsy đầu tiên, mà `number` khi đó bằng 0 nên UI hiện số 0 thay vì để trống. Em bị gọi vào hỏi vì sao sửa code người khác mà không hỏi.

**L:** Không sửa code người khác khi chưa hiểu nó ảnh hưởng tới những trường hợp nào. Nếu cần sửa thì hiểu kỹ trước rồi xác nhận với người viết; nếu họ không còn ở công ty thì tìm tài liệu của tính năng; nếu cả hai đều không có thì phải test lại tất cả chỗ dùng đoạn code đó. Điều em nhớ nhất là một dòng code trông thừa thường là đang gánh một trường hợp mà mình chưa thấy.

---

# STORY 7 — Bug chỉ xảy ra trên bản deploy: race giữa React và qiankun

**Story Perseverance chính. Cũng dùng cho: bug khó nhất · biết khi nào nhờ giúp · chất lượng.**

### Câu mở đầu `[~35s]`

> "Bên em có một bug trên UAT: chuyển giữa các sub-app trong hệ microfrontend thì giá trị context không được lưu. Nó chặn việc release lên pilot và chặn luôn tester. Nhưng ở local thì chạy hoàn toàn bình thường — mà không tái hiện được thì không sửa được. Em mất trọn một ngày không ra. Điều em nhớ nhất trong chuyện này không phải một ngày đó, mà là lúc em đã có một cách sửa chạy được rồi mà vẫn quyết định không dùng nó."

### Mục lục `[~16s]`

> "Em xin đi qua bốn phần: khoanh vùng được nhưng không tái hiện được; quyết định đi hỏi và cách em hỏi; nguyên nhân thật; và vì sao em bỏ cách sửa đầu tiên dù nó chạy được."

### C — Context `[~15s]`

Hệ thống dùng kiến trúc microfrontend với qiankun, các sub-app chia sẻ state qua context. Bug nằm trên UAT nên gấp: nó chặn release lên pilot và tester không test tiếp được.

### A — Actions

**Theme 1 — Khoanh vùng được nhưng không tái hiện được** `[~37s]`

Em đặt log ở chỗ redirect giữa các source và kiểm tra giá trị context. Ở local mọi thứ đúng như mong đợi: setState được gọi, context nhận giá trị. Em đẩy log lên bản deploy thì mới thấy lỗi — context không được lưu khi chuyển source. Vậy là em biết chắc hiện tượng và biết nó nằm ở đâu, nhưng vẫn kẹt ở chỗ quan trọng nhất: không tái hiện được ở local thì mỗi giả thuyết đều phải deploy lại mới thử được. Em mất trọn một ngày.

**Theme 2 — Quyết định đi hỏi, và cách em hỏi** `[~39s]`

Đến cuối ngày em đánh giá là mình đã hết giả thuyết, mà bug đang chặn release, nên tự đâm thêm một ngày nữa thì cái giá không đáng. Em đi hỏi một anh senior — nhưng không hỏi kiểu "em bí quá". Em soạn sẵn danh sách những gì đã làm và kết quả từng cái: log đặt ở đâu, ở local ra sao, trên deploy ra sao, và điểm kẹt là không tái hiện được ở local. Anh ấy nhìn danh sách đó và gợi ý đúng một câu: thử giả lập mạng chậm. Em tái hiện được ngay.

**Theme 3 — Nguyên nhân thật** `[~36s]`

Tái hiện được thì em lần ra cơ chế. `setState` nằm ngay cạnh lệnh redirect và nó có được gọi thật, nhưng qiankun unmount sub-app hiện tại trước khi React kịp commit giá trị đó vào context, nên giá trị không bao giờ được ghi. Ở local không bao giờ lộ vì bundle của app đích được webpack-dev-server phục vụ qua loopback, gần như tức thì; trên bản deploy thì phải đi qua mạng thật, và chênh lệch đó đủ để cuộc đua đảo chiều.

**Theme 4 — Có fix chạy được, và em từ chối dùng nó** `[~39s]`

Cách sửa đầu tiên em thử là `setTimeout` 200ms trước khi redirect. Nó chạy, bug biến mất. Nhưng em không dùng. 200 mili giây là một con số không ai giải thích được — nó chỉ đang thắng cuộc đua chứ không loại bỏ cuộc đua. Máy chậm hơn, mạng tệ hơn, hoặc bundle nặng lên sau vài tháng là vỡ lại, và lần sau sẽ không ai nhớ vì sao có con số đó. Em tìm tiếp và ra `flushSync`: buộc React commit update ngay trước khi redirect, thay vì hy vọng nó kịp.

### R — Results `[~22s]`

Bug được sửa đúng nguyên nhân chứ không phải bằng một khoảng chờ, nên không còn phụ thuộc tốc độ mạng hay tốc độ máy. Release lên pilot đi tiếp được và tester quay lại test bình thường. *(Nếu từ đó tới nay không có case tương tự, nói rõ khoảng thời gian.)*

### L — Learnings `[~30s]`

Bài học em giữ lại lâu nhất: **một cách sửa chạy được không có nghĩa là một cách sửa đúng.** `setTimeout` 200ms hôm đó là một con số may mắn. Giờ khi nào em phải chọn một con số mà không giải thích được vì sao lại là con số đó, em coi đó là dấu hiệu mình chưa hiểu vấn đề.

### Follow-up đã chuẩn bị

**Vì sao ở local lại không bao giờ lộ?** — Ngoài chuyện loopback gần như tức thì, bản deploy còn qua CDN và là bản production, chi phí thực thi khác với bản dev. Chênh lệch đó làm lệch thời điểm main thread rảnh để React flush update — nó không trực tiếp làm qiankun unmount nhanh hơn, mà làm React commit muộn hơn.

**Vì sao không giữ setTimeout cho nhanh?** — Vì nó không sửa cuộc đua, nó chỉ đang thắng cuộc đua. Đổi thiết bị, đổi mạng, hoặc bundle nặng lên là vỡ lại, mà lúc đó không ai biết 200ms từ đâu ra. Chi phí tìm cách đúng lúc ấy nhỏ hơn nhiều so với chi phí gỡ lại sau này.

**flushSync có tác dụng phụ gì?** — Có. Nó buộc React commit đồng bộ nên mất lợi ích gộp update, và tốn hơn nếu lạm dụng. Em chỉ dùng đúng ở điểm trước khi redirect, nơi bắt buộc phải chắc chắn giá trị đã được ghi trước khi component bị unmount.

**Sao không nghĩ tới mạng sớm hơn?** — Vì em mặc định môi trường deploy chỉ khác về cấu hình, nên em đi tìm khác biệt trong code. Đó là giả định sai, và chính nó là bài học thứ hai của em: bug chỉ xảy ra trên deploy thì gần như luôn là khác biệt môi trường, không phải khác biệt code. Từ đó việc đầu tiên em làm là liệt kê những gì khác nhau giữa hai môi trường rồi tái hiện từng cái ở local.

**Nếu anh senior không gợi ý thì sao?** — Nhiều khả năng em vẫn ra, nhưng chậm hơn. Điều em rút ra không phải là phải hỏi sớm hơn, mà là phải hỏi kèm danh sách đã loại trừ — đó là thứ giúp người khác giúp được mình.

**Từ lúc tái hiện được tới lúc sửa xong mất bao lâu?** — *(bạn bổ sung)*

**Có ghi lại hoặc chia sẻ cho team không?** — *(bạn bổ sung — nếu có thì nói, đây là điểm cộng đáng kể)*

**✂️ Rút ngắn khi cần (5,0 → 4,0 phút):** rút Theme 1 còn hai câu — *"em đặt log ở chỗ redirect, ở local mọi thứ đúng, đẩy log lên bản deploy thì mới thấy context không được lưu. Biết hiện tượng rồi nhưng không tái hiện được ở local nên mỗi giả thuyết đều phải deploy lại"*.

### Dùng cho câu hỏi

Dự án gặp trở ngại lớn · Bug khó nhất từng gặp · Khi nào bạn nhờ giúp đỡ · Chất lượng và nợ kỹ thuật · Làm việc dưới áp lực thời gian · Bạn học từ đồng nghiệp thế nào

**Signal:** Perseverance *(chính)*, Growth, Communication, Ownership · **HRS:** #5 Pioneer for growth, #6 Dive deep and act upon it

⚠️ **Hai bẫy:** đừng để anh senior thành nhân vật chính — ghi nhận đúng một câu rồi quay về phần bạn làm. Đừng nói "em thử mọi cách" — nói ba việc cụ thể và kết quả từng cái.

---

# STORY 8 — Feedback: "PR của em quá dài, khó review"

**⚠️ DỰ PHÒNG — story chính cho câu feedback giờ là #10.** Dùng #8 khi họ hỏi riêng về góp ý trong công việc hằng ngày, hoặc khi đã dùng #10 cho câu khác. *(Phần Actions và Results vẫn còn ô trống chưa điền.)*

### Câu mở đầu `[~20s]`

> "Feedback trực tiếp nhất em nhận gần đây là PR của em quá dài và khó review. Nghe thì nhẹ, nhưng nó chạm đúng một chỗ em không nhận ra: em đang tối ưu cho việc mình viết cho nhanh, và đẩy chi phí sang người review."

### C — Context `[~15s]`

Em làm chính một module và thường gộp nhiều thay đổi vào một PR — đặc biệt trong giai đoạn refactor, khi một thay đổi kéo theo nhiều chỗ khác.

### A — Actions `[~40s]`

Phản ứng đầu tiên của em thật ra là thấy hơi oan, vì các thay đổi đó liên quan tới nhau nên em nghĩ tách ra sẽ khó hiểu hơn. Nhưng khi em thử đặt mình vào vị trí người review — mở một PR mấy chục file mà không biết bắt đầu từ đâu — thì em thấy góp ý đó đúng.

*(👉 điền: em đã làm gì cụ thể sau đó — chọn những cái đúng với em)*
- Tách PR theo lớp hoặc theo bước, mỗi PR một mục đích
- Viết mô tả PR có bối cảnh và thứ tự nên đọc
- Tự review một lượt trước khi gửi
- Mở PR nháp sớm để lấy góp ý về hướng trước khi viết xong

### R — Results `[~10s]`

*(👉 điền: PR ngắn hơn bao nhiêu · thời gian chờ review có giảm không · có ai nhận xét gì sau đó không)*

### L — Learnings `[~20s]`

Bài học của em là **code không chỉ cần đúng, nó còn cần đọc được bởi người không viết nó**. Và cụ thể hơn: khi em thấy một góp ý hơi oan, đó thường là dấu hiệu em đang nhìn từ phía người viết chứ chưa nhìn từ phía người đọc. Giờ trước khi gửi PR em đều tự hỏi một câu — người mở cái này ra mà không có ngữ cảnh trong đầu thì họ bắt đầu từ đâu.

### Follow-up

**Em có thấy khó chịu khi nhận góp ý đó không?** — Ban đầu có, em thấy hơi oan. Nhưng em thử nhìn từ phía người review thì thấy góp ý đúng. Em nghĩ phản ứng đầu tiên hơi tự ái là bình thường; quan trọng là nó kéo dài bao lâu.

**Còn feedback nào khác gần đây không?** — Có, và cái này khó nghe hơn: em được nhận xét là còn thiếu kinh nghiệm. *(xem phần xử lý bên dưới)*

### Dùng cho câu hỏi

Feedback gần đây từ quản lý · Em nhận góp ý thế nào · Em cải thiện gì gần đây · Một lần em không đồng ý với góp ý

---

## Xử lý feedback "em còn thiếu kinh nghiệm" ⚠️

Đây là loại feedback khó vì nó **không hành động được trực tiếp** — không ai tăng số năm kinh nghiệm bằng nỗ lực. Nếu bị hỏi, đừng phòng thủ và cũng đừng gạt đi.

> "Em cũng có nhận một feedback khó hơn: rằng em còn thiếu kinh nghiệm. Thành thật là lúc đầu em không biết làm gì với nó, vì đó không phải thứ em sửa được trong một sprint.
>
> Cái em rút ra là điều họ thật sự nói tới không phải số năm, mà là số **loại tình huống** em từng gặp. Nên em xử lý theo hai hướng. Một là rút ngắn khoảng cách bằng cách chủ động nhận những việc em chưa từng làm — năm vừa rồi em tự học backend để tự làm phần dữ liệu thay vì đẩy sang người khác. Hai là bù bằng quy trình: chỗ nào em biết mình chưa đủ trải nghiệm thì em đi hỏi sớm và hỏi kèm những gì đã loại trừ, thay vì tự đâm cho tới lúc muộn.
>
> Em không nghĩ mình đã hết thiếu kinh nghiệm. Nhưng em nghĩ mình đã biết cách làm việc khi biết mình thiếu."

⚠️ **Chỉ mang câu này ra nếu họ hỏi thẳng.** Đừng tự nêu.

---

# STORY 9 — "AI generate ra vậy": trách nhiệm với code AI sinh ra

**Story AI mạnh thứ hai sau `ai-kit`. Dùng cho: kỹ sư quá tin AI · đặt chuẩn cho team · ảnh hưởng không quyền hạn · chất lượng.**

### Câu mở đầu `[~30s]`

> "Em review một PR của team khác và thấy họ tự viết một component bảng dữ liệu mới, trong khi team em đã có sẵn một cái dùng chung — và họ hardcode màu thay vì dùng design token. Khi em hỏi, câu trả lời là 'AI generate ra vậy'.
>
> Thứ em thấy đáng nói không phải đoạn code đó. Là ở thời điểm ấy, chưa ai đặt ra chuẩn về việc code do AI sinh ra thì ai chịu trách nhiệm. Nên em sửa cái chuẩn đó, chứ không chỉ sửa cái PR."

### C — Context `[~25s]`

Team em giữ bộ component dùng chung và design token cho 6 feature team. Các team đó phần lớn là dev Java mới chuyển sang React, đang chịu tiến độ, và AI thì cho ra code trông rất hợp lý. Ở thời điểm đó chưa có quy ước nào về chuyện dùng lại component hay tự tạo mới.

### A — Actions

**Theme 1 — Hỏi để hiểu ý định trước, không phán trước** `[~40s]`

Em đọc code và thấy thứ họ thật sự cần là tuỳ biến style cho từng dòng của bảng — mà component dùng chung của em đã hỗ trợ đúng việc đó qua props. Nhưng em không mở đầu bằng "chỗ này sai".

Em hỏi xác nhận trước: *"em hiểu là chỗ này đang cần tuỳ biến style cho row nên mới tạo component riêng, đúng không?"* Họ xác nhận đúng như vậy. Lúc đó em mới nói là component sẵn có làm được việc đó, và gợi ý cách sửa.

Em làm theo thứ tự đó vì hai lý do. Một là em có thể đã hiểu sai nhu cầu của họ. Hai là nếu em mở đầu bằng phán xét thì phần còn lại của cuộc trao đổi sẽ về việc ai đúng, chứ không về việc code nên thế nào.

**Theme 2 — Để chính AI sửa lại** `[~25s]`

Họ đưa lại yêu cầu cho AI, lần này kèm thông tin về component đã có. Và AI ra đúng pattern, đúng component.

Chi tiết đó làm em đổi cách nghĩ: vấn đề không phải AI kém. Vấn đề là AI không biết bên em đã có sẵn cái gì, và **không ai nói cho nó biết**. Nó đọc được thứ mình viết ra cho nó đọc, chứ nó không đọc được ý định của team.

**Theme 3 — Sửa ở tầng hệ thống, không sửa từng PR** `[~45s]`

Em nhận ra comment từng PR không giải quyết được gốc. Có 6 feature team; nếu team nào cũng tự tạo bản riêng thì vài tháng sau bộ giao diện không còn maintain nổi, mà em thì không thể review hết mọi PR.

Nên em viết rules vào `claude/rules` và hook của Copilot, để khi ai đó đụng tới common component hoặc phần UI thì công cụ **tự động nạp quy ước** trước: ưu tiên dùng component sẵn có và tuỳ biến qua props; nếu thật sự cần tạo mới thì trao đổi với team em trước.

Em cố ý viết là "trao đổi" chứ không phải "cấm", vì có những trường hợp tạo mới là đúng — em chỉ muốn quyết định đó được nhìn thấy, thay vì xảy ra âm thầm trong một PR.

### R — Results `[~25s]`

PR đó được sửa để dùng lại component chung. Quan trọng hơn: quy ước giờ được nạp tự động ở cả hai công cụ AI mà các team đang dùng, nên nó không phụ thuộc vào việc em có kịp review PR đó hay không.

*(👉 nếu có: từ đó tới nay còn trường hợp tự tạo trùng nào không, và có ai còn dùng lý do "AI làm" nữa không)*

### L — Learnings `[~30s]`

Hai điều.

Thứ nhất, **trách nhiệm không chuyển sang AI được.** AI viết ra thì AI không chịu trách nhiệm — người mở PR chịu trách nhiệm. Em có nói với bạn đó là nên hỏi ngược lại AI tại sao nó làm như vậy, vì mọi dòng code được viết ra đều phải có lý do, và nếu mình không giải thích được thì mình chưa sở hữu đoạn code đó.

Thứ hai, khi AI sai lặp đi lặp lại thì đừng sửa từng lần — **sửa thứ nó đọc**. Đó cũng là cách em làm với công cụ của riêng mình: mỗi lần AI lặp lại một sai lầm, em ghi nó thành luật thay vì sửa tay lần nữa.

### Follow-up đã chuẩn bị

**Sao không tự sửa hộ cho nhanh?** — Sửa hộ thì lần sau lặp lại, và nó cũng lấy mất cơ hội để bạn đó hiểu vì sao. Em muốn kết quả là họ biết chọn giữa dùng lại và tạo mới, chứ không phải là một PR đúng.

**Rules gồm những gì?** — Ưu tiên dùng component sẵn có và tuỳ biến qua props · dùng design token thay vì hardcode giá trị · muốn tạo component mới thì trao đổi với team giữ bộ chung trước. Nạp tự động khi chạm tới common component hoặc UI.

**Có ai phản đối quy ước đó không?** — *(👉 bạn bổ sung)*

**Nếu họ thật sự cần một component riêng thì sao?** — Thì vẫn tạo. Quy ước là "trao đổi trước", không phải "cấm" — em chỉ muốn quyết định đó được nhìn thấy thay vì xảy ra âm thầm.

**Em có nghĩ AI làm giảm chất lượng code không?** — Em nghĩ AI khuếch đại cái sẵn có. Nơi nào đã có quy ước rõ và người review hiểu việc thì AI làm nhanh hơn. Nơi nào quy ước nằm trong đầu vài người thì AI làm chỗ lệch lan nhanh hơn — vì nó sinh ra code trông rất giống code đúng.

**Làm sao biết quy ước có tác dụng?** — *(👉 bạn bổ sung: còn gặp trường hợp trùng nữa không)*

### Dùng cho câu hỏi

Kỹ sư quá tin vào output của AI · Em xử lý thế nào khi AI làm giảm chất lượng · Đặt chuẩn cho team · Ảnh hưởng người khác không có quyền hạn · Bất đồng nhẹ khi review code · Em làm gì khi AI lặp lại cùng một lỗi

**Signal:** Trust, Iteration, Scaling, Communication, Leadership · **HRS:** #6 Dive deep · #9 Foster healthy relationships · #2 Think and act big

⚠️ **Bẫy khi kể:** đừng để bạn đồng nghiệp trông tệ. Khung đúng là **chuẩn chưa tồn tại**, không phải người cẩu thả — họ là dev Java mới sang React, đang chịu tiến độ, và AI cho ra code trông hợp lý. Em là người đặt chuẩn; đó mới là điểm của story.

---

# STORY 10 — Feedback "trau dồi thêm kinh nghiệm" và 700 dòng viết lại

**Story chính cho câu về feedback. Có hai chế độ kể — chọn theo câu hỏi.**

### Mở đầu A — cho câu "feedback gần đây từ quản lý" `[~30s]`

> "Trong buổi review theo quý gần nhất, feedback em nhận là nên trau dồi thêm kinh nghiệm làm việc.
>
> Nó không làm em bất ngờ — trước đó em đã tự thấy một lỗi của mình và đang sửa: em hay đâm thẳng vào giải pháp mà chưa khảo sát đủ. Nhưng chính vì không bất ngờ nên nó nói với em một điều khác: **nếu người khác cũng nhìn thấy thì khoảng cách đó lớn hơn mức em tự đánh giá.** Nên sau buổi đó em không chỉ tiếp tục, em tăng mức độ."

### Mở đầu B — cho câu kỹ thuật *(over-engineering · khi nào tự viết khi nào dùng thư viện · quyết định em phải làm lại)* `[~30s]`

> "Em từng tự viết toàn bộ phần xử lý ảnh cho một component dùng chung — kéo ảnh, phóng to thu nhỏ, co giãn theo cửa sổ — khoảng 700 dòng trong một hook riêng. Sau đó em phải viết lại bằng thư viện, còn hơn 200 dòng.
>
> Điều đáng nói không phải con số, mà là em đã đi khá sâu rồi mới dừng lại hỏi câu lẽ ra phải hỏi từ đầu."

### C — Context `[~25s]`

Team em giữ bộ component dùng chung cho các feature team. Một trong số đó là component tải ảnh lên có kèm cắt ảnh. Yêu cầu nghe đơn giản, nên em bắt tay vào viết ngay.

### A — Actions

**Theme 1 — Quyết định ban đầu và cái giá của nó** `[~40s]`

Em tự implement toàn bộ: tự tính toán khi người dùng kéo ảnh, khi phóng to thu nhỏ, khi cửa sổ đổi kích thước. Mỗi thao tác đó kéo theo một loạt trường hợp biên phải xử lý — ảnh vượt khung, tỉ lệ lệch, toạ độ sai sau khi co giãn.

Kết quả là một hook khoảng 700 dòng. Nó chạy đúng, nhưng phần phức tạp nhất — toán hình học — nằm phơi ra trong code của team em. Người mới vào đọc rất khó, và mỗi lần sửa một lỗi nhỏ đều phải hiểu lại toàn bộ phần tính toán.

**Theme 2 — Dừng lại và viết lại** `[~40s]`

Chỗ làm em dừng không phải là nó chạy sai, mà là lúc em nhận ra người mới gần như không maintain nổi phần này.

Em khảo sát và chọn Cropper.js, viết lại phần đó còn hơn 200 dòng. Nhưng em **không** giao hết cho thư viện: phần lắng nghe sự kiện co giãn cửa sổ và điều chỉnh lại ảnh theo đó thì thư viện không hỗ trợ, nên em vẫn tự viết. Em muốn ranh giới rõ — cái gì thư viện làm tốt hơn thì để nó làm, cái gì nó không phủ thì mình giữ, chứ không phải chọn giữa tự viết tất cả hay giao tất cả.

Đánh đổi là em thêm một dependency và bundle tăng lên. Em chấp nhận vì đổi lại là 500 dòng logic hình học không còn là thứ team em phải nuôi.

**Theme 3 — Biến nó thành cách làm** `[~30s]`

Từ đó, khi nhận một yêu cầu mới em không viết ngay. Em khảo sát xem đã có ai giải quyết bài toán này chưa, và đánh giá theo bốn tiêu chí: **kích thước bundle · phạm vi tính năng có phủ nhu cầu không · khả năng maintain · độ tin cậy** — nhìn số sao, tần suất commit, còn ai đang duy trì không. Rồi em đưa lựa chọn đó ra bảo vệ trước team trước khi bắt tay viết.

Việc này em bắt đầu làm **trước** buổi review theo quý, không phải sau.

**Theme 4 — Buổi review đến sau, và nó thêm gì** `[~35s]` *(chỉ kể ở chế độ A)*

Khi nhận feedback "nên trau dồi thêm kinh nghiệm làm việc", em không bất ngờ, vì em đã tự thấy và đã bắt đầu sửa. Nhưng đúng chỗ đó lại là chỗ đáng suy nghĩ nhất: **nếu người khác cũng nhìn ra thì nghĩa là khoảng cách lớn hơn mức em tự đánh giá.** Tự mình thấy một lỗi thì dễ nghĩ là mình đang xử lý ổn rồi.

Nên em đổi mức độ, không chỉ tiếp tục. Trước đó em sửa theo hướng **cẩn thận hơn** — khảo sát kỹ trước khi viết. Sau buổi review em thêm một hướng nữa là **tăng số loại tình huống mình gặp**: chủ động nhận những việc chưa từng làm, thay vì chọn việc mình chắc tay. Vì em nghĩ thứ họ gọi là kinh nghiệm không phải số năm, mà là số kiểu vấn đề mình từng phải tự gỡ.

### R — Results `[~20s]`

Phần xử lý ảnh giảm từ khoảng 700 dòng xuống hơn 200 — khoảng 70%. Phần khó nhất là tính toán hình học không còn nằm trong codebase của team. Người mới giờ đọc được phần còn lại, vì nó chủ yếu là ghép nối chứ không phải toán.

*(👉 nếu biết: bundle tăng khoảng bao nhiêu KB)*

### L — Learnings `[~35s]`

Ba điều, và chúng nối vào nhau.

Thứ nhất là về chính việc nhận feedback. Em đã tự thấy vấn đề trước khi ai nói, và lúc đầu em thấy hơi yên tâm vì mình đang sửa rồi. Nhưng nghĩ kỹ thì việc người khác cũng nhìn ra mới là dữ kiện quan trọng — nó nói rằng tốc độ em sửa chưa đủ nhanh so với mức người khác kỳ vọng. Từ đó em không dùng "mình biết rồi" làm lý do để giữ nguyên nhịp.

Thứ hai là em tìm được tên cho lỗi của mình khi đọc *A Philosophy of Software Design*: em đã viết một **module nông** — nó không giấu được độ phức tạp, nó đẩy hết lên người dùng nó. Cái em nên làm là kéo phức tạp xuống càng sâu càng tốt sau một ranh giới rõ ràng.

Thứ ba, cụ thể hơn cả hai cái trên: trước khi viết bất cứ thứ gì, em hỏi hai câu — *phần phức tạp này có thể đẩy xuống sâu hơn không*, và *đã có ai giải quyết nó rồi chưa*.

### Follow-up đã chuẩn bị

**Vì sao ban đầu em không tìm thư viện?** — Vì em đánh giá nhu cầu là đơn giản, và em muốn kiểm soát hoàn toàn phần hiển thị. Cả hai đều là lý do thật, nhưng em đánh giá sai độ phức tạp — nó không nằm ở giao diện mà nằm ở phần tính toán phía sau.

**Sao vẫn giữ phần co giãn cửa sổ tự viết?** — Thư viện không phủ phần đó. Và nó cũng là phần ít thay đổi nhất, nên giữ lại không tốn chi phí về sau. Em không muốn chọn kiểu tất-cả-hoặc-không-gì.

**Nếu thư viện ngừng được duy trì thì sao?** — Đó chính là lý do em có tiêu chí độ tin cậy trong bốn tiêu chí. Và vì em bọc nó sau một ranh giới rõ, thay thế thì chỉ sửa trong phạm vi đó chứ không lan ra ngoài.

**Sau buổi review em đã làm gì?** — Hai việc. Chủ động nhận task chưa từng làm để tăng số loại tình huống mình gặp. Và đọc có chọn lọc theo thứ đang cần — lúc chuẩn bị buổi chia sẻ React thì đọc sâu về performance, còn về thiết kế thì *A Philosophy of Software Design* là cuốn đổi cách nghĩ của em nhiều nhất.

**Feedback đó có làm em bất ngờ không?** — Không, và em nghĩ đó mới là chỗ đáng nói. Em đã tự thấy và đã bắt đầu sửa từ trước. Nhưng việc người khác cũng nhìn ra nghĩa là em đang sửa chậm hơn mức cần thiết — nên em tăng mức độ chứ không giữ nguyên nhịp.

**Có feedback nào làm em thấy khó tiếp thu không?** — Có, nhưng là chuyện khác: *(chuyển sang #8 — góp ý PR quá dài, lúc đầu em thấy hơi oan)*

**Em còn đọc gì khác?** — *The Pragmatic Programmer*, và *AI Beyond Vibe Coding* cho phần làm việc với AI.

**Em có đồng ý với feedback đó không?** — Có. Em nghĩ nó đúng, chỉ là nó được nói ở mức khái quát nên em phải tự dịch ra thành thứ làm được.

### Dùng cho câu hỏi

Feedback gần đây từ quản lý *(mở đầu A)* · Em nhận góp ý thế nào · Một quyết định kỹ thuật em phải làm lại *(mở đầu B)* · Over-engineering · Khi nào tự viết khi nào dùng thư viện · Em đánh giá thư viện thế nào · Em học từ sách thế nào · Em cải thiện gì gần đây

**Signal:** Growth *(chính)*, Ownership, Scope · **HRS:** #3 candid feedback · #5 pioneer for growth · #7 achieve more with less

⚠️ **Giữ đúng giọng khi nói "em đã tự biết trước".** Nói kiểu *"em biết rồi nên feedback đó không mới"* là gạt góp ý và mất sạch signal tiếp thu. Giọng đúng là: em đã tự thấy, nhưng việc người khác cũng thấy nói rằng em đang sửa chưa đủ nhanh.

⚠️ **Đừng kể feedback thứ hai** *("tiếp tục phát triển technical và AI knowledge như đang làm")* — đó là lời khen, không phải góp ý. Kể vào nghe như tự khen và làm loãng phần chính.

---

# BẢNG TRA: câu hỏi → story nào, nhấn phần nào

| Câu hỏi | Story | Nhấn ngay đầu |
|---|---|---|
| Project tự hào nhất | #1 | POC → được duyệt production; mở rộng người dùng làm được |
| Chủ động ngoài trách nhiệm | #2 | "Không ai giao việc này" |
| Bất đồng với đồng nghiệp | #3 (mở đầu B) | Bất đồng với lead, em chủ động dừng để nghe lại |
| Bất đồng, quyết định không theo ý bạn | #3 (mở đầu A) | Em thấy chị đúng, bỏ hướng của mình, làm lại toàn bộ |
| Nhận ra mình sai / nhận feedback | #3 (mở đầu A) | Em tranh luận về cách trình bày, lẽ ra phải hỏi phần đó có đáng chiếm nhiều chỗ không |
| Yêu cầu mơ hồ | #4 | Em hỏi ngược "vì sao có tính năng này" |
| Làm nhiều với ít nguồn lực | #4 | 4 ngày → 1 ngày |
| Sai lầm lớn nhất | #5 | 25 phút của người dùng bị mất, do em |
| Feedback gần đây từ quản lý | **#10** | "trau dồi thêm kinh nghiệm" → em tự dịch thành: kinh nghiệm là **số loại tình huống**, không phải số năm |
| Quyết định kỹ thuật phải làm lại | **#10** (mở đầu B) | 700 dòng → hơn 200 · module nông vs module sâu |
| Khi nào tự viết, khi nào dùng thư viện | **#10** | 4 tiêu chí: bundle · tính năng · maintain · độ tin cậy |
| Feedback về công việc hằng ngày | **#8** *(dự phòng)* | PR quá dài, khó review |
| Kỹ sư quá tin vào AI | **#9** | "AI generate ra vậy" → em viết rules nạp tự động cho 6 team |
| Em làm gì khi AI lặp lại một lỗi | **#9** | sửa thứ AI đọc, đừng sửa từng PR |
| Ảnh hưởng người khác | #3 | 6 team, 30 dev, 2 người debug được lỗi thật |
| Học công nghệ mới | #1 hoặc #2 | Tự học backend / ADK |
| Bạn hiểu customer thế nào | #5 rồi #4 | Đứng ở vị trí người dùng mất 25 phút |
| Dự án gặp trở ngại lớn / kiên trì | #7 | Một ngày không tái hiện được, và fix chạy được mà em vẫn bỏ |
| Bug khó nhất | #7 | Chỉ xảy ra trên deploy, race giữa React flush và qiankun unmount |
| Khi nào bạn nhờ giúp đỡ | #7 | Hết giả thuyết thì hỏi, kèm danh sách đã loại trừ |
| ⭐ Kể một chút về em | *(cắm móc)* | 📄 kịch bản ở `08` mục A — trục "phạm vi trách nhiệm lớn dần", ba móc #3 · #4 · #2 |
| ⭐ Vì sao em phù hợp với HRS | — | 📄 kịch bản ở `08` mục B — ba nhóm: AI · cách làm việc · nền kỹ thuật, mỗi ý một số |
| ⭐ Principle nào hợp nhất với em | #5 rồi #2 | 📄 `08` mục B — *take the customer view first*; #5 là lần em sai rồi sửa, #2 là lần chủ động |
| ⭐ Principle nào khó nhất với em | #10 + #1 | 📄 `08` mục B — *pioneer for growth*; khó ở **ước lượng cái giá phải trả khi thử sai** |

---

# CÒN THIẾU

## Vế khó của *seek truth and commit* — đã có bản dùng được, nên có thêm bản dự phòng

**#3 (mở đầu A) giờ phục vụ được ô này**, vì nó có đủ hai vế: bỏ quan điểm khi thấy mình sai (*seek truth*) và thực thi hết mình theo hướng không phải của mình (*commit*).

Giới hạn: đây là trường hợp em **được thuyết phục**. Vế khó hơn — *vẫn không đồng ý nhưng vẫn dốc sức làm* — chưa có story. Nếu đào được một cái thì bộ story đóng lại hoàn chỉnh. Câu hỏi để đào:
1. Lần nào bạn đề xuất một cách làm mà lead/CEO chọn cách khác, và bạn vẫn làm theo cách của họ?
2. Lần nào bạn muốn refactor / đổi thư viện / đổi cách tiếp cận mà bị gạt vì ưu tiên khác?
3. Lần nào bạn phản đối một yêu cầu sản phẩm và cuối cùng vẫn phải build?
4. Ràng buộc nào từ legal/security/team khác bạn thấy thừa nhưng vẫn tuân thủ đầy đủ?
5. Estimate nào bạn nói không đủ thời gian nhưng vẫn bị chốt — bạn xử lý thế nào?

## Số cần xác nhận
- #7: từ lúc tái hiện được tới lúc sửa xong mất bao lâu? có ghi lại/chia sẻ cho team không?
- #5: có xử lý idempotency khi retry không?
- #5: từ khi sửa tới nay còn case mất dữ liệu nào không?
- #3: sau buổi talk có hoạt động tiếp nối nào không?

---

# HAI CÂU CẤP CEO

Không phải câu story. Ngắn, chốt nhanh, rồi dừng. Cả hai dùng xưng "em" cho khớp với tài liệu chuẩn bị còn lại.

---

## A. "Vì sao em muốn rời công ty hiện tại?"

### Họ đang đo bốn thứ

1. **Em chạy tới cái gì, hay chạy khỏi cái gì.** Người chạy khỏi thì sẽ lại chạy.
2. **Em có nói xấu chỗ cũ không.** Nói xấu chỗ cũ hôm nay = dự báo nói xấu HRS ngày mai. Đây là lỗi giết câu trả lời nhanh nhất.
3. **Lý do có bền không.** Nếu lý do em nêu cũng tồn tại ở HRS thì em sẽ lại đi sau một năm — CEO chi nhánh nghĩ tới chi phí tuyển và giữ người.
4. **Em có bỏ dở việc không.** Em đang làm chính một module sắp go-live.

**Nguyên tắc:** nói theo hướng **thứ mình muốn tới**, không phải thứ mình muốn thoát.

### Bản chuẩn `[~50s]`

> "Em không có vấn đề gì với chỗ hiện tại. Em học được nhiều ở đó, đặc biệt là cách làm việc bài bản — làm rõ yêu cầu, chốt phạm vi rồi mới code, thay vì nhận task là làm luôn như hồi đầu đi làm của em.
>
> Lý do em tìm cơ hội mới là về **loại sản phẩm và quy mô**. Module em đang làm phục vụ đối tác, team gồm hai dev và một designer. Em muốn vào một sản phẩm mà chính công ty sở hữu và vận hành lâu dài, có người dùng thật ở quy mô lớn và có số liệu để đo xem quyết định của mình đúng hay sai — chứ không dừng ở chỗ bàn giao xong là hết.
>
> Về kỹ thuật, năm vừa rồi em tự học backend để tự làm phần dữ liệu cho module của mình, nhưng em học chủ yếu bằng cách tự mò. Giai đoạn tiếp theo em muốn học nhanh hơn bằng cách làm cạnh những người giỏi hơn mình trong một tổ chức lớn hơn."

Nói xong thì **dừng**. Đừng giải thích thêm.

### Ba khung để chọn — lấy khung nào ĐÚNG với em

| Khung | Nội dung | Dùng khi |
|---|---|---|
| **A. Sản phẩm của chính công ty** | Từ làm sản phẩm cho đối tác → làm sản phẩm công ty sở hữu, thấy được hệ quả quyết định của mình qua nhiều năm | Nếu mô hình hiện tại đúng là làm cho đối tác |
| **B. Quy mô kỹ thuật và đồng nghiệp** | Muốn hệ thống lớn hơn, lưu lượng thật, nhiều kỹ sư hơn để học | An toàn nhất, gần như luôn đúng |
| **C. Miền nghiệp vụ** | Muốn làm sản phẩm có người dùng cuối rõ ràng và dữ liệu để đo | Nối rất mượt vào principle *take the customer view first* |

Bản chuẩn ở trên đang gộp A + B. Nếu A không đúng với tình huống của em thì bỏ A, giữ B và C.

### Câu vặn chắc chắn có: "Module sắp go-live mà em đi lúc này?"

Đây là câu của CEO, và nó đo **trách nhiệm**, không đo động cơ.

> "Em không bỏ ngang. Người thứ hai đã vào module từ nhiều tháng trước và cùng nắm codebase — chính đợt refactor tách lớp em làm là để người mới đọc theo lớp là hiểu, nên việc bàn giao không phụ thuộc vào mình em. Quy trình chuyển việc cũng có thời gian báo trước nên phần của em sẽ được bàn giao đủ.
>
> Còn về thời điểm thì em nghĩ tìm hiểu cơ hội không có lúc nào là 'đẹp' cả — nếu chờ hết việc thì sẽ không bao giờ hết."

Chú ý: câu trả lời này lấy chính đợt refactor ở story #1 làm bằng chứng. Hai phần nối được vào nhau.

### Năm câu làm hỏng

| Đừng nói | Họ nghe ra |
|---|---|
| "Lương chưa tương xứng" | Sẽ đi tiếp khi có nơi trả cao hơn |
| "Sếp / team không hợp" | Vấn đề nằm ở em, và em sẽ nói về HRS như vậy |
| "Không có cơ hội phát triển" | Em có từng đề nghị chưa? Nghe như đòi hỏi |
| "Chỗ cũ làm việc thiếu chuyên nghiệp" | Chê chỗ cũ — cờ đỏ |
| "Công ty nhỏ quá" | Chi nhánh Việt Nam của HRS cũng không lớn — tự bắn vào chân |

---

## B. "Ba tới năm năm nữa em muốn gì?"

### Họ đang đo bốn thứ

1. **Gắn bó** — tuyển để giữ, không phải để một năm.
2. **Có kế hoạch hay trôi theo dòng.**
3. **Kế hoạch đó thực hiện được ở HRS không** — nói muốn mở công ty riêng là trượt.
4. **Tự nhận thức về khoảng cách hiện tại** — biết mình còn thiếu gì.

**Cấu trúc:** đang ở đâu → 1–2 năm → 3 năm → 5 năm → vì sao HRS khớp với lộ trình đó.

### Bản chuẩn `[~55s]`

> "Một tới hai năm tới, em muốn thành người nắm được một mảng nghiệp vụ đầu-cuối chứ không chỉ phần frontend. Em đi được nửa đường rồi — năm vừa rồi em tự học backend để tự làm phần dữ liệu cho module của mình. Phần còn thiếu là em chưa làm với hệ thống lớn và lưu lượng thật.
>
> Ba năm, em muốn tới mức khi có vấn đề trong mảng đó thì người khác tìm tới em — không phải vì chức danh mà vì em hiểu nó sâu nhất. Và em muốn là người viết lại và chia sẻ cho team, giống buổi chia sẻ React em từng làm cho sáu team ở công ty hiện tại.
>
> Năm năm thì em nghĩ hướng của em là kỹ thuật chứ không phải quản lý — kiểu người thiết kế được giải pháp cho những vấn đề chưa rõ ràng. Nhưng em không muốn nói quá chắc về mốc năm năm, vì những gì em học trong hai năm tới hoàn toàn có thể đổi hướng đó.
>
> Em có tìm hiểu thì thấy HRS có chương trình L&D toàn cầu và có cơ hội onsite ở châu Âu. Đó là hai thứ khớp trực tiếp với lộ trình này, và cũng là một lý do em ứng tuyển."

### Vì sao bản này hiệu quả

- **Neo vào bằng chứng đã có**, không phải mong muốn suông: tự học backend (đang đi), buổi chia sẻ 6 team (đã làm). Kế hoạch có gốc rễ thì đáng tin.
- **Tự nêu khoảng cách của mình** — "chưa làm với hệ thống lớn và lưu lượng thật". Tự nhận thức, và khớp principle *hire the most likely to win and develop them*.
- **Chừa chỗ cho thay đổi** ở mốc 5 năm. Nói quá chắc nghe cứng nhắc và giả.
- **Kết bằng lý do chọn HRS** — biến câu về bản thân thành câu về sự phù hợp.
- **"Không phải vì chức danh"** chạm thẳng principle *make merits rule not entitlements*.

### Biến thể nếu em muốn hướng quản lý

Chỉ đổi đoạn năm năm, và chỉ khi đúng ý em:

> "Năm năm thì em để mở giữa hai hướng. Em thích phần kỹ thuật sâu, nhưng em cũng thấy mình có hứng thú với việc giúp người khác khá lên — buổi chia sẻ React là lần đầu em thấy rõ điều đó. Em nghĩ hai ba năm tới sẽ cho em câu trả lời."

Đừng nói "em muốn làm manager" nếu chưa chắc. Với vị trí mid-level, câu đó thường bị đọc là muốn thoát khỏi việc kỹ thuật.

### Bốn câu làm hỏng

| Đừng nói | Họ nghe ra |
|---|---|
| "Em muốn mở công ty riêng" | Không giữ được — cờ đỏ về giữ người |
| "Em chưa nghĩ tới" | Thiếu chủ động |
| "Em muốn lên senior trong hai năm" | Nói về chức danh, trái principle *make merits rule not entitlements* |
| "Em muốn học thật nhiều thứ" | Mơ hồ, ai cũng nói được |

---

⚠️ **Xưng hô:** toàn bộ tài liệu dùng "em" — nhất quán với phần chuẩn bị còn lại. Giữ nguyên cả buổi, đừng đổi qua lại giữa chừng.
