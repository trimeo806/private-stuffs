# Common Pitfalls — bảy cái bẫy (bản tiếng Việt có giải thích + áp dụng)

Tin tốt: gần như mọi lỗi ở đây **chỉ cần biết là tránh được**, không cần luyện thêm.
Phần I là bảy bẫy chung. Phần II là bẫy theo từng signal area. Phần III áp vào bộ story của em.

---

# PHẦN I — BẢY CÁI BẪY

## Bẫy 1 — Không nhận ra câu hỏi đang đo cái gì

**Nội dung.** Mọi câu hỏi hành vi đều đo một thứ cụ thể. Người phỏng vấn không hỏi ngẫu nhiên vì tò mò về đời em. Nếu em đọc sai thứ họ đang đo thì **cả câu trả lời trượt mục tiêu**, dù em kể hay tới đâu.

Ví dụ: *"Kể về một project thử thách."* Chữ **"thử thách"** gánh toàn bộ ý nghĩa câu hỏi. Họ muốn nghe về **Perseverance** — em đâm vào một bức tường thật và vượt qua thế nào, giữ động lực ra sao, thử những hướng nào khi hướng đầu chết.

Nhưng phần lớn ứng viên làm gì? Dành ba phút mô tả kiến trúc kỹ thuật và **ba mươi giây** cho phần thử thách thật. Họ háo hức chứng minh năng lực kỹ thuật tới mức quên mất câu hỏi không phải là "kể về một thiết kế hệ thống thú vị". Người phỏng vấn ra về mà không có tín hiệu họ cần.

> 💡 **Dừng vài giây sau khi nghe câu hỏi.** Tự hỏi: họ đang tìm tín hiệu gì? Rồi mới chọn story. **Im lặng vài giây để suy nghĩ là hoàn toàn bình thường** — nó còn cho thấy em cân nhắc chứ không phun ra bản đã học thuộc.

## Bẫy 2 — Không đủ hành động

**Nội dung.** Actions là trung tâm. Đó là nơi tín hiệu nằm, và là thứ được ghi vào sổ tay người phỏng vấn. Context, Results, Learnings đều là vật liệu hỗ trợ.

Người phỏng vấn nghe để **hình dung em sẽ làm việc thế nào nếu vào team họ**. Em có mô tả được thứ mình thật sự đã làm không? Có hình dung được từng bước không? Có hiểu được cách em ra quyết định không?

Câu trả lời hỏng điển hình:

> ❌ *"Bên em có một deadline gấp để thêm bộ lọc tìm kiếm. Em xây bộ lọc đó, test khoảng hai tuần, rồi ship đúng hạn. Khách hàng thích và bên em đạt chỉ tiêu."*

Câu này không nói lên gì cả. Em đã **làm** gì? Xây thế nào? Ra quyết định gì trên đường đi? Gặp trở ngại nào?

So với:

> ✅ *"Em cải thiện hiệu năng của service bằng cách phát hiện trang danh sách sản phẩm đang có truy vấn N+1 — mỗi sản phẩm kích hoạt một lệnh gọi database riêng để lấy review. Em refactor thành một truy vấn JOIN với eager loading, giảm p95 từ 3,2 giây xuống 400ms. Ticket support về việc tải chậm giảm 60% trong tháng sau."*

Giờ thì thấy được phán đoán kỹ thuật, cách debug, và tác động business.

**Quy tắc 30 giây:** nói 30 giây mà chưa đưa ra một hành động đẩy story tiến lên, tức là em đang sa đà vào thứ không phải trọng tâm.

**Hành động không chỉ là xây thứ gì đó.** Nhớ sáu nhóm: *thiết kế · tạo đồng thuận · giao tiếp · triển khai · lặp và đo · debug và phân tích*.

> ⚠️ **Coi chừng ngôn ngữ bị động:** *"em được giao project này"* · *"quản lý bảo em làm"* · *"ticket được chuyển tới em"*. Những câu này làm em nghe như công việc **xảy đến** với em chứ không phải em đang lái nó. Kể cả khi thật sự được giao, hãy tập trung vào **thứ em chọn làm sau khi nhận nó**.

## Bẫy 3 — Thừa bối cảnh

**Nội dung.** Bối cảnh chỉ nên ở mức tối thiểu đủ để hiểu hành động và vì sao chúng quan trọng. Phần quan trọng nhất của bối cảnh là **mức độ hệ trọng** — vì sao việc này quan trọng với tổ chức và với khách hàng.

Một tính năng ngăn khách hàng bỏ đi có trọng lượng khác một tính năng cho vui. Nhưng họ **không** cần biết sơ đồ tổ chức, quy trình sprint, lịch sử team, hay tiểu sử quản lý của em — trừ khi những thứ đó ảnh hưởng tới hành động.

Mục tiêu **10%** story cho bối cảnh. Story ba phút thì khoảng **hai mươi giây**. Nói bối cảnh quá ba mươi giây thì dừng lại tự hỏi: *người nghe có thật sự cần cái này để hiểu hành động của em không?*

**Chỉ giữ chi tiết bối cảnh nếu nó (1) thiết lập mức độ hệ trọng, hoặc (2) làm hành động của em hiểu được.** Không phục vụ một trong hai thì cắt.

**Ngoại lệ:** story có cung cảm xúc — xung đột, thất bại, tình huống nhiều rủi ro — thì bối cảnh quan trọng hơn. Ở đó những chi tiết tưởng nhỏ lại giúp hiểu động lực giữa người với người: cuộc nói chuyện quan trọng diễn ra khi nào, ở đâu? Là một cuộc tranh luận nóng giữa cuộc họp đông người, hay một buổi nói riêng cẩn thận?

## Bẫy 4 — Bệnh "chúng em"

**Nội dung.** *"Bên em xây một lớp cache mới." "Bên em quyết định dùng Redis." "Bên em ship trong ba tuần."*

"Bên em" là ai? **Em** đã làm gì? Người phỏng vấn đang đánh giá em, không đánh giá cả team. Mỗi lần nói "bên em" là một cơ hội bị bỏ lỡ.

Đây là lỗi phổ biến nhất, và **đặc biệt hay gặp ở người thật sự làm việc nhóm tốt** — họ quen nghĩ theo tập thể tới mức mô tả công việc theo tập thể.

| Thay vì | Hãy nói |
|---|---|
| "Bên em quyết định dùng Redis" | "Em đề xuất Redis vì tỉ lệ đọc/ghi là 100:1 và cần đáp ứng dưới một mili giây" |
| "Bên em xây một service mới" | "Em thiết kế kiến trúc service, viết các endpoint chính, và viết integration test" |
| "Bên em ship đúng hạn" | "Em điều phối bản phát hành qua ba team, quản lý chuỗi phụ thuộc và chạy deploy" |

Em không hề tuyên bố mình làm hết một mình — em chỉ đang **cụ thể về phần của mình**. Người phỏng vấn thừa biết em làm cùng người khác, nên không cần lo bị coi là kiêu.

## Bẫy 5 — Chọn sai story

**Nội dung.** Họ chỉ nghe được những story em kể. Thứ tự ưu tiên khi chọn:

1. **Scope trước tiên.** Chọn project có phạm vi lớn nhất — bề rộng hành động (em có lập kế hoạch, thực thi, và gỡ xung đột không?), thang thời gian, độ phức tạp kỹ thuật hoặc tổ chức, tác động business.
2. **Độ liên quan thứ hai.** Story phải khớp câu hỏi và cho đúng tín hiệu. Đây là một dải:
   - *Khớp tốt:* "kể về một lần em thất bại" → "Đề xuất của em về việc xây lại hệ thống xác thực từ đầu hoá ra là sai…"
   - *Khớp tạm:* → "Em đánh giá thấp độ phức tạp của việc tích hợp với hệ thống cũ…"
   - *Khớp kém:* → "Sprint velocity của bên em thấp hơn kế hoạch…"
3. **Tính độc nhất thứ ba.** Story đã dùng cho câu trước thì cân nhắc kể chuyện mới. Buổi phỏng vấn tốt là buổi em kể được hết những story đại diện tốt nhất cho mình.
4. **Độ mới thứ tư.** Story gần đây phản ánh năng lực hiện tại. Chuyện năm ngoái thường thắng chuyện năm năm trước.

**Ba lỗi chọn story hay gặp:**

- **Lệch scope** — đừng chọn story sửa bug cho vị trí senior, trừ khi đó là một con bug thật sự khó.
- **Quá cũ** — chuyện năm năm trước làm người nghe tự hỏi từ đó tới nay em làm gì. Bám vào khoảng **0–3 năm**.
- **Không phải project của em** — nếu đóng góp thật của em chỉ là dự họp và góp ý, họ nhìn ra ngay.

## Bẫy 6 — Kết thúc kiểu cổ tích

**Nội dung.** *"Bên em ship đúng hạn, chỉ số cải thiện toàn diện, các bên đều hài lòng, và mọi người sống hạnh phúc mãi mãi."*

Người phỏng vấn nghe câu này và nghĩ: *"Chắc bạn không đứng đủ gần để thấy vấn đề. Hoặc bạn đang giấu gì đó. Hoặc bạn không đủ nhạy để nhận ra điểm yếu."*

Project thật luôn có vấn đề thật — bug, trục trặc phối hợp. **Chọn story có thử thách thật thì cho nhiều tín hiệu hơn một story trơn tru.**

Ba cách đưa vấn đề thật vào để story đáng tin hơn:

- **Trở ngại:** *"Bên em gặp một đợt tụt hiệu năng ở staging, mất một tuần để tìm ra."*
- **Sai lầm và cách khắc phục:** *"Ban đầu em đánh giá thấp các phụ thuộc liên team, gây trễ hai tuần. Khi nhận ra, em lập sync hằng ngày để bắt vấn đề sớm hơn."*
- **Điểm chưa hoàn hảo:** *"Bên em ship đúng hạn, nhưng độ phủ test chưa được như em muốn. Em bổ sung ở sprint sau."*

**Learnings mạnh nhất là loại chỉ ra được ứng dụng cho tình huống sau này.** *"Trải nghiệm này thay đổi hẳn cách em tiếp cận X"* mạnh hơn *"em học được rằng Y quan trọng"*. Mạnh nhất là khi chỉ ra được một project sau đó em đã áp dụng bài học.

## Bẫy 7 — Không luyện

**Nội dung.** Người thuộc story trên giấy nhưng chưa nói ra miệng **luôn thể hiện kém hơn** người có kinh nghiệm yếu hơn một chút nhưng đã tập kể. Vì tải nhận thức của việc kể chuyện theo thời gian thực rất lớn.

Dấu hiệu luyện chưa đủ, đều dễ sửa: story dài 5–6 phút · mất mạch rồi phải quay lại · lan man vì chưa quyết được cái gì nên cắt · từ đệm · nhịp độ lệch.

---

# PHẦN II — BẪY THEO TỪNG SIGNAL AREA

**Scope** — Project em kể có đại diện đúng level không? · "So what" của project là gì, nói được kết quả business và tác động người dùng không? · Có truyền đạt được độ phức tạp không: bao nhiêu team, bao lâu, đụng bao nhiêu thành phần?

**Ownership** — Coi chừng ngôn ngữ bị động và bệnh "chúng em" · Có cho thấy thiên hướng hành động, cách em bắt đầu nhanh không? · Có mô tả cách em **đo** kết quả và **vì sao chọn chỉ số đó** không? · Khác biệt giữa *"em được yêu cầu cải thiện tính năng tìm kiếm"* và *"em phát hiện tìm kiếm đang làm người dùng bỏ đi nên đề xuất cách sửa"* là rất lớn.

**Perseverance** — Trong phần bối cảnh, em có **bán được** vì sao thử thách đó thật sự khó không? · Vấn đề được phát hiện thế nào — em tự tìm ra hay ai báo? · Có cho thấy nỗ lực kéo dài chứ không phải thắng nhanh không? · Có kể hành động **phòng ngừa về sau** không?

**Ambiguity** — Có định lượng hoặc mô tả đủ rõ sự mơ hồ không: phạm vi không rõ? ẩn số kỹ thuật? tiêu chí thành công không rõ? · Có cho thấy tư duy có cấu trúc không (dựng thử, phỏng vấn các bên, triển khai theo giai đoạn)? · Có cho thấy em điều chỉnh khi biết thêm không? · Có cơ hội nào cho thấy em **tạo ra sự rõ ràng cho người khác**, không chỉ cho mình?

**Communication** — Nêu rõ **cách** và **lý do** chọn kênh giao tiếp: nói hay viết, ai cần, sau đó chuyện gì xảy ra? · Cho thấy giao tiếp ở nhiều tầng: ngang với đồng nghiệp, lên với quản lý, xuống nếu có người làm dưới sự dẫn dắt của em · Cho thấy **hiểu biết hai chiều** — em có kiểm tra xem người kia hiểu đúng chưa, hoặc thu phản hồi rồi điều chỉnh không?

**Conflict Resolution** — Chọn xung đột đúng tầm: bất đồng với đồng nghiệp ngang hàng là tầm thấp hơn bất đồng với team đối tác · Story phải cho thấy em **trực tiếp tham gia giải quyết**, không phải quan sát từ ngoài · **Thể hiện sự thấu cảm** — nếu em làm người kia trông xấu đi thì họ biết em đang không công bằng · Khung kết quả theo hướng đôi bên cùng thắng · **Cho thấy quan hệ sau đó** — có xây được sự tôn trọng, hàn gắn được niềm tin không?

**Growth** — Nếu hỏi về sai lầm thì kể sai lầm thật, không phải *"em làm việc hơi nhiều quá"* · Đảm bảo không quá hiển nhiên ngay từ đầu là lẽ ra phải làm gì · Cho thấy em đã thấm bài học bằng **thay đổi hành vi cụ thể** · Chỉ ra tình huống sau đó em đã áp dụng bài học

**Leadership** — Cho thấy em ảnh hưởng người khác và **tạo điều kiện để họ làm tốt nhất** · Cho thấy em chậm lại để suy nghĩ và tạo ra sự rõ ràng giữa hỗn loạn · Có nhắc tới việc mentor hay phát triển người khác không, kể cả qua code review?

---

# PHẦN III — ÁP VÀO BỘ STORY CỦA EM

## 1. Bẫy nào em đã tránh được

| Bẫy | Trạng thái |
|---|---|
| 4 — bệnh "chúng em" | ✅ Bản viết sạch. **Nhưng đây là lỗi của lời nói, không phải của chữ viết** — dưới áp lực rất dễ bật lại. Soi kỹ khi xem video. |
| 5 — chọn sai story | ✅ Đã xếp theo scope. Tất cả đều trong 0–1 năm. #6 là chuyện fresher nên chỉ để dự phòng — đúng. |
| 7 — không luyện | 🟡 Lịch ba ngày đã có ở `05-practice.md`. Chưa làm thì bẫy này vẫn mở. |

## 2. Bẫy 6 — story #4 đang có kết thúc cổ tích 🔴

Đây là phát hiện quan trọng nhất của bài này với em.

Story #4 hiện kết thúc như thế này: em chất vấn yêu cầu → mở buổi họp → **các bên chọn phương án của em** → tiết kiệm 75% → legal duyệt. Không một vết xước nào.

Người phỏng vấn nghe một chuỗi hoàn hảo như vậy sẽ nghĩ: *bạn đang kể phiên bản đẹp, hoặc bạn không đứng đủ gần để thấy chỗ gợn.*

**Vết xước có sẵn trong chính chuyện của em, chỉ là đang bị bỏ ra:** em **đã bắt đầu đi theo hướng tách hai kho lưu trữ** — nghiên cứu, tìm cách lưu — rồi mới dừng lại đặt câu hỏi. Nghĩa là em mất một quãng đi theo hướng sai trước khi nhận ra phải hỏi.

Câu thêm vào phần Learnings:

> "Có một chỗ em làm chưa tốt: em đã bỏ mấy ngày đi theo hướng tách hai kho rồi mới dừng lại hỏi vì sao cần tính năng này. Lẽ ra câu hỏi đó phải là việc đầu tiên, trước khi em mở editor. Từ đó, việc đầu tiên em làm khi nhận một yêu cầu là hỏi nó phục vụ mục tiêu gì — chứ không phải hỏi làm nó thế nào."

Thêm câu này làm story mạnh lên chứ không yếu đi: nó biến một chuỗi hoàn hảo thành một chuyện thật, và nó nâng Learnings lên đúng chuẩn *"thay đổi hẳn cách em tiếp cận"*.

**Các story khác đã có vết xước sẵn:** #1 có 2 tuần refactor phải trả giá · #3 em sai và phải làm lại · #5 chính là story sai lầm · #7 có bước `setTimeout` bị bỏ · #2 có bài học chưa validate nhu cầu. Chỉ #4 là quá sạch.

## 3. Bẫy 2 và 3 — số liệu đã đo

Từ `05-practice.md`, tỉ lệ Context/Actions/R+L của em:

- **Actions đang 45–53%**, chuẩn là 60%. Không phải vì thiếu hành động, mà vì Context và R+L hơi dài. Cắt Context là tự động về chuẩn.
- **#2 có Context 20%** — gấp đôi. Đã có bản rút gọn hai câu.
- **#7 ngược lại**: Actions 73%, R+L 19%. Khối cắt đã ghi sẵn.

## 4. Bẫy 1 — hai câu dễ đọc sai với em

| Câu hỏi | Bẫy | Đúng ra là |
|---|---|---|
| "Kể về một project thử thách" | Lao vào kiến trúc microfrontend, qiankun, React internals — vì Head of Engineering ngồi đó | Đang đo **Perseverance**. Trọng tâm là một ngày không tái hiện được và việc bỏ `setTimeout`, không phải cơ chế |
| "Project có tác động business lớn nhất" | Kể #1 theo hướng kỹ thuật | Đang đo **Scope + kết quả**. Dẫn bằng *ai làm được* và *công ty cấp thêm dev* |

**Kỹ thuật dừng ba giây** đặc biệt quan trọng với em, vì có Head of Engineering trong phòng nên bản năng sẽ kéo em về phía kỹ thuật.

## 5. Nghĩ phòng thủ — bốn câu mời hiểu xấu

Phần này trong bài dành cho ứng viên senior, nhưng có Head of Engineering trong phòng thì áp dụng luôn. Bốn câu trong story của em mời người nghe suy diễn theo hướng xấu:

| Em nói | Họ có thể nghĩ | Khung lại thành |
|---|---|---|
| "Module làm 11 tháng, chưa go-live" | Chậm? Không đẩy được việc? | "Đã được duyệt lên production, đang chờ đủ bộ tính năng theo kế hoạch phát hành" — **nói ngay ở câu mở đầu** |
| "Em dùng AI để dựng UI" | Phần nào thật sự là của em? | "Em dùng AI như công cụ dựng giao diện; cấu trúc màn hình, cách nối API và toàn bộ phần review là của em — 2 tuần refactor sau đó cho thấy output ban đầu không dùng thẳng được" |
| "Em mất một ngày không ra rồi đi hỏi" | Tự lực kém? | "Em đánh giá là đã hết giả thuyết, và bug đang chặn release, nên tự đâm thêm một ngày là cái giá không đáng" |
| "Service AI mới có một module dùng" | Xây thứ không ai cần? | "Em thiết kế cho khả năng dùng lại nhưng chưa validate nhu cầu với module khác — đó là bài học của em" |

Cả bốn khung lại đều **đã có sẵn** trong phần follow-up của file story. Việc cần làm là **nói ra trước khi bị hỏi**, đừng đợi họ đào.

## 6. Lái buổi phỏng vấn — kỹ thuật thực đơn

Bài viết gọi đây là *menu technique*, và em có sẵn nguyên liệu hoàn hảo cho nó:

> "Em có hai ví dụ về bất đồng: một cái là em chất vấn chính yêu cầu được đưa xuống và cuối cùng phương án của em được chọn; một cái là em bất đồng với lead trực tiếp rồi nhận ra mình sai. Anh/chị muốn nghe cái nào trước?"

Ba tác dụng cùng lúc: cho thấy em có **nhiều** hơn một story · để họ chọn đúng tín hiệu họ cần · và **báo trước rằng em có cả hai loại** — một lần đúng, một lần sai. Kể cả khi họ chỉ chọn một, họ đã biết em có cái kia.

Hai kỹ thuật lái còn lại em cũng đang dùng: **cắm móc trong TMAY** (ba móc trỏ vào #1, #3, #4) và **thả vụn bánh mì** (những câu ngắn nhắc tới mảng khác của story để mời họ đào).

## 7. Checklist rà story trước khi luyện

- [ ] Story #4 đã thêm vết xước ở Learnings
- [ ] Mỗi story có ít nhất một chỗ chưa hoàn hảo
- [ ] Không câu nào bắt đầu bằng "em được giao" mà không nói tiếp em **chọn** làm gì
- [ ] Bốn khung phòng thủ nói chủ động, không đợi bị hỏi
- [ ] Chuẩn bị sẵn câu thực đơn cho câu hỏi xung đột
- [ ] Với mỗi story, trả lời được: câu này đang đo signal nào?
