# Deliver — Kể một story tốt (bản tiếng Việt có giải thích)

Tài liệu lý thuyết. Story đã dựng nằm ở `04-stories.md`.
Mỗi mục có hai phần: **Nội dung** (dịch ý) và **Giải thích** (vì sao quan trọng, hỏng ở đâu, áp vào bạn thế nào).

---

## 0. Vì sao phải kể bằng story

**Nội dung.** Não người được nối dây để nghĩ và nhớ bằng câu chuyện. Khi nghe một story, các vùng não chuyên biệt hoạt động: dựng lại dòng thời gian, kích hoạt đồng cảm, mô phỏng lại sự kiện. Khác với một danh sách thành tích hay một mô tả trừu tượng về kỹ năng, story kéo người nghe vào trải nghiệm của bạn, lọt qua hàng rào phản biện, và ở lại trong đầu họ rất lâu sau buổi phỏng vấn.

**Giải thích.** Đây không phải chuyện văn vẻ. Sau một ngày phỏng vấn 5 ứng viên, người phỏng vấn không nhớ được danh sách kỹ năng của ai. Họ nhớ "anh chàng có bug chỉ xảy ra trên bản deploy". Story là **định dạng lưu trữ** — bạn đang tối ưu cho việc họ nhớ được bạn lúc ngồi họp hội đồng, chứ không chỉ lúc nghe.

Hệ quả thực tế: mỗi story của bạn nên có **một thứ dễ nhớ** — một con số, một hình ảnh, một câu chốt. Của bạn đang có sẵn: *25 phút cấu hình của người dùng bị xoá sạch* · *200 mili giây không ai giải thích được* · *4 ngày xuống 1 ngày*.

---

## 1. STAR và vì sao chuyển sang CARL

**Nội dung.** STAR gồm Situation (bối cảnh), Task (nhiệm vụ của bạn), Actions (hành động), Results (kết quả). Nó không tệ — nếu mai bạn phỏng vấn và đã chuẩn bị theo STAR thì cứ dùng. Nhưng nó có hai nhược điểm, rõ nhất với ứng viên cấp cao:

- **STAR bỏ quên phần chiêm nghiệm.** Không có chỗ nào cho "tôi học được gì". Với ứng viên senior, đây chính là cách người phỏng vấn đo scope: người càng cao cấp càng học được từ sai lầm và rút ra thứ tái dùng được cho dự án sau.
- **Situation và Task hay dính vào nhau.** Ứng viên tốn thời gian chuẩn bị chỉ để tách hai cái, mà với dự án lớn thì ranh giới giữa "chuyện gì đang xảy ra" và "tôi phải làm gì" vốn đã là ranh giới nhân tạo.

CARL giải quyết cả hai:

| | |
|---|---|
| **C — Context** | Toàn cảnh chuyện gì đang diễn ra, gồm cả việc bạn dính vào thế nào. Chính là Situation + Task gộp lại. |
| **A — Actions** | Các bước cụ thể bạn đã làm: làm gì, làm thế nào, và **vì sao chọn cách đó**. |
| **R — Results** | Kết quả từ hành động của bạn: cái gì đổi, tác động lên business ra sao. |
| **L — Learnings** | Bạn học được gì, bạn nghĩ lại thế nào về những lựa chọn đã làm. |

Trong CARL, Actions vẫn là trung tâm. Context co giãn được — nguyên tắc là **đưa lượng bối cảnh tối thiểu đủ để người nghe hiểu được Actions và động cơ đằng sau**. Learnings là điểm kết tự nhiên, giống truyện ngụ ngôn kết bằng một bài học: đây là sân khấu để bạn thể hiện chiều sâu và sự tự nhận thức.

**Giải thích.** Điểm mấu chốt là chữ **L**. Ứng viên trung bình kể xong Results là dừng. Nhưng "tôi làm được X" chỉ chứng minh quá khứ. "Tôi hiểu vì sao X hiệu quả và lần sau tôi sẽ làm khác chỗ nào" mới dự báo được tương lai — mà người phỏng vấn là người dự báo.

Áp vào bạn: Learnings của story #7 (`200ms là con số may mắn`) có giá trị hơn cả phần fix, vì nó là nguyên tắc dùng được ở mọi bug sau này.

---

## 2. Context — 30 đến 45 giây

**Nội dung.** Nhiệm vụ của Context là định hướng người nghe: bạn làm việc với ai, tình huống là gì, vì sao nó quan trọng. Nên gồm:

- Bối cảnh công ty
- Team hoặc vai trò — **chỉ khi nó liên quan tới story**
- Vấn đề hoặc cơ hội đã khởi động mọi thứ
- **Mức độ hệ trọng (stakes)**

Ba bậc stakes:

- Yếu: "Tôi làm một project về performance…"
- Khá hơn: "Chúng tôi cần cải thiện performance cho người dùng"
- Tốt nhất: "Luồng checkout có tỉ lệ bỏ giỏ 40%, ước tính mất 2 triệu đô mỗi quý"

Giữ ngắn: **30–45 giây**. Rất nhiều ứng viên nói 1–3 phút bối cảnh trước khi kể tới việc mình làm. Tới lúc đó người nghe đã trôi mất rồi.

**Ba lỗi phải tránh:**

1. **Bài giảng lịch sử dự án** — "Hệ thống này build từ 2018, năm 2019 định refactor nhưng bị hoãn, 2020 thì reorg…". Người nghe không cần dòng thời gian đầy đủ. Họ cần biết bạn bắt đầu từ đâu và bạn đã làm gì.
2. **Chi tiết sơ đồ tổ chức thừa** — "Tôi báo cáo cho Sarah, Sarah báo cáo cho VP Platform, còn PM thì thuộc team khác…". Trừ khi cấu trúc đó trực tiếp liên quan tới story (kiểu bạn có hành động phát sinh vì nó), bỏ qua.
3. **Giải thích công nghệ mà họ đã biết** — "Kubernetes là nền tảng điều phối container…". Phỏng vấn ở công ty công nghệ thì mặc định họ có nền tảng kỹ thuật.

**Giải thích.** Context là phần **duy nhất** bạn nên cắt không thương tiếc. Lý do: nó không mang signal. Không ai được tuyển vì mô tả bối cảnh hay. Nhưng nhiều người bị loại vì tiêu hết thời gian ở đó.

Cách kiểm tra nhanh: đọc phần Context của bạn, xoá từng câu, hỏi *"thiếu câu này thì Actions phía sau còn hiểu được không?"* — hiểu được thì xoá luôn.

Về stakes: bậc thứ ba mạnh không phải vì có số, mà vì số đó **buộc người nghe quan tâm**. Không có số thì thay bằng hậu quả cụ thể. Của bạn: *"người dùng bỏ 25 phút cấu hình rồi mất sạch"* — không có tiền, nhưng có sức nặng.

---

## 3. Actions — nơi signal thật sự nằm

**Nội dung.** Đây là phần cốt lõi bị đánh giá. Bốn nguyên tắc:

**Dùng "tôi" một cách nhất quán.** Không phải "chúng tôi quyết định" mà "tôi đề xuất và team đồng ý", hoặc "tôi thuyết phục được tech lead…". Mỗi câu phải làm rõ đóng góp riêng của bạn. Bạn là người biết làm việc nhóm, nhưng nhóm được tạo bởi từng cá nhân hành động. Bạn hoàn toàn ghi nhận được đóng góp của người khác mà không xoá đi vai trò của mình.

**Gồm cả hành động phi kỹ thuật.** Đúng, bạn viết code. Nhưng bạn có chốt phạm vi dự án không? Có trao đổi với stakeholder không? Có mentor ai không? Có gỡ xung đột không? Những hành động phi kỹ thuật đó thường chính là thứ phân biệt một story cấp Senior với một story cấp Staff.

**Cụ thể.** Sự cụ thể cho người nghe một ví dụ về cách bạn sẽ làm việc đó sau khi được tuyển.

- Không phải "tôi trao đổi với stakeholder" mà "tôi lập sync hàng tuần với PM và viết một trang tóm tắt gửi cho director".
- Không phải "tôi debug lỗi" mà "tôi thêm distributed tracing và phát hiện Redis connection pool bị cạn lúc cao điểm".

**Cho thấy hành vi lặp lại được.** Người phỏng vấn không chỉ muốn biết bạn đã thành công, mà muốn biết bạn **sẽ thành công lần nữa** trong hoàn cảnh tương tự. Kể cách làm, không chỉ kể việc đã làm.

**Giải thích.** "Chúng tôi" là lỗi phổ biến nhất của ứng viên Việt Nam, vì văn hoá ta coi nhận công là bất lịch sự. Nhưng người phỏng vấn không đang chấm đạo đức — họ đang cố xác định **bạn** làm được gì. Nói "team em đã…" là để họ tự đoán, mà họ sẽ đoán về phía thấp.

Cách dung hoà: ghi nhận người khác **một câu**, rồi quay ngay về mình. Ví dụ trong story #7 của bạn: *"một anh senior gợi ý hướng mạng"* — đủ, rồi kể tiếp phần bạn làm.

Về "hành vi lặp lại được": đây là tiêu chí ngầm quan trọng nhất. Người phỏng vấn nghe story không phải để biết quá khứ mà để **dự đoán tương lai**. Nên câu có giá nhất trong Actions thường là câu bắt đầu bằng *"tôi làm vậy vì…"* — nó cho thấy nguyên tắc, mà nguyên tắc thì lặp lại được, còn sự việc thì không.

---

## 4. Chi tiết bao nhiêu là đủ

**Nội dung.** "Thiếu chi tiết" và "thừa chi tiết" là hai phản hồi phổ biến nhất mà huấn luyện viên đưa ra, và cũng là hai phản hồi làm ứng viên bực nhất. Bao nhiêu là đủ?

Chi tiết phục vụ đúng **hai mục đích**: (1) làm story hiểu được, (2) tạo dựng uy tín của bạn. Người nghe hiểu được hành động của bạn khi họ có đủ bối cảnh; chi tiết cũng chứng minh bạn thật sự đã làm và hiểu sâu. **Khi đã tạo được uy tín rồi thì mọi chi tiết thêm là đang lãng phí thời gian.** Chọn một chỗ để đào sâu, đào ở đó, rồi giữ mạch truyện đi tiếp.

Ví dụ tạo uy tín mà không tốn nhiều thời gian:

> "Tôi cải thiện performance của service bằng cách phát hiện chúng tôi đang có truy vấn N+1 ở trang danh sách sản phẩm — mỗi sản phẩm kích hoạt một lệnh gọi database riêng để lấy review. Tôi refactor lại thành một truy vấn JOIN duy nhất với eager loading, giảm p95 từ 3,2 giây xuống 400ms. Ticket support về việc tải chậm giảm 60% trong tháng sau đó."

**Đặt chi tiết chủ yếu ở phần Actions**, vì đó là nơi bạn thể hiện tư duy, chiều sâu kỹ thuật, khả năng lãnh đạo và cách ra quyết định. Trong Actions nên có:

- **Các phương án đã cân nhắc**: "Tôi đánh giá ba hướng: microservices, modular monolith, hoặc refactor monolith hiện có…"
- **Lý do của bạn**: "Tôi chọn modular monolith vì team 8 người không kham nổi 15 microservices…"
- **Quyết định kỹ thuật cụ thể**: "Tôi triển khai kiến trúc hexagonal với ranh giới rõ ràng…"
- **Cách bạn vượt trở ngại**: "Khi VP chất vấn tiến độ, tôi dựng một kế hoạch triển khai theo giai đoạn…"
- **Phối hợp và ảnh hưởng**: "Tôi pair với Principal Engineer để kiểm chứng hướng đi…"

Ngược lại, chi tiết trong Context phải liên quan. Đừng mô tả cấu trúc tổ chức hay đưa một con số latency trừ khi story của bạn có đoạn giao tiếp qua cấu trúc đó hoặc ra quyết định kỹ thuật dựa trên con số đó.

**Quy tắc 30 giây:** nếu sau khoảng 30 giây nói mà bạn chưa chuyển sang hành động tiếp theo, nhiều khả năng bạn đang sa đà vào một ý. Cứ thả một câu "chỗ này tôi có thể nói sâu hơn nếu anh/chị muốn" rồi đi tiếp.

**Giải thích.** Đây là mục khó nhất của cả bài, và cũng là mục có giá trị nhất.

Hình dung "uy tín" như một cái công tắc, không phải cái thước. Một chi tiết đúng chỗ là bật được công tắc — sau đó thêm mười chi tiết nữa cũng không bật thêm được gì, chỉ tốn thời gian và làm người nghe mệt.

Trong story #7 của bạn, công tắc bật ở đúng câu: *"qiankun unmount sub-app trước khi React kịp commit giá trị vào context"*. Nghe câu đó là người phỏng vấn biết bạn thật sự đã đi tới đáy. Sau câu đó, mọi giải thích thêm về webpack-dev-server, CDN, minify đều là phần **để dành cho follow-up**, không phải để nói luôn.

Câu "tôi có thể nói sâu hơn nếu anh/chị muốn" là công cụ mạnh và ít người dùng. Nó vừa cho thấy bạn còn chiều sâu, vừa trao quyền chọn cho người nghe — mà người nghe được chọn thì họ nghe kỹ hơn.

---

## 5. Tám nhóm hành động để nhặt

**Nội dung.**

| Nhóm | Gồm |
|---|---|
| **Thiết kế** | Quyết định sản phẩm/kiến trúc, các phương án đã cân nhắc |
| **Tạo đồng thuận** | Xây sự đồng ý, quản lý stakeholder, thương lượng |
| **Giao tiếp** | Tài liệu, thuyết trình, những cuộc nói chuyện khó |
| **Triển khai** | Thực thi kỹ thuật, phân bổ nguồn lực, giảm thiểu rủi ro |
| **Lặp** | Vòng phản hồi, đo lường, điều chỉnh hướng |
| **Test và debug** | Quy trình QA, chẩn đoán vấn đề, tối ưu |
| **Phát hành** | Chiến lược deploy, giám sát, hỗ trợ sau khi lên |
| **Tư duy và quyết định** | Phân tích, lựa chọn chiến lược |

**Giải thích.** Dùng bảng này như một **danh sách kiểm** sau khi viết xong story: story của bạn đang chạm mấy nhóm? Chỉ chạm "Triển khai" và "Test/debug" là story của một người code giỏi. Chạm thêm "Tạo đồng thuận", "Giao tiếp", "Tư duy và quyết định" là story của một kỹ sư mà công ty muốn giữ.

Kiểm nhanh bộ story của bạn: #4 chạm mạnh nhóm *Tạo đồng thuận* và *Tư duy và quyết định*; #7 chạm *Test/debug* và *Giao tiếp* (cách bạn soạn câu hỏi đi hỏi); #1 chạm *Thiết kế*, *Triển khai*, *Tạo đồng thuận*. Bộ của bạn cân — chỉ mỏng ở nhóm *Phát hành*, vì chưa go-live.

---

## 6. Results — chứng minh hành động của bạn có nghĩa

**Nội dung.** Nghĩ tác động theo nhiều chiều:

- **Business**: doanh thu, tiết kiệm chi phí, hiệu suất
- **Người dùng**: mức hài lòng, giảm ma sát, khả năng mới
- **Team**: tốc độ, giảm gánh on-call, lan toả kiến thức

Lượng hoá khi có thể. "Cải thiện performance" là mơ hồ. "Giảm p99 85%, từ 800ms xuống 120ms" là cụ thể.

**Khi bạn không có số:** không phải dự án nào cũng có kết quả đo được — công cụ nội bộ, cải tiến quy trình, giai đoạn đầu, hoặc startup không kịp đo. Vẫn nói được tác động bằng bốn cách:

1. **So sánh trạng thái trước và sau** — "Trước dự án này, deploy cần ba người phối hợp trong bốn tiếng. Sau đó, một kỹ sư bất kỳ deploy trong mười lăm phút."
2. **Phản hồi định tính** — "PM nói với tôi đây là lần đầu chị ấy thấy bên engineering thật sự hiểu ưu tiên của mình."
3. **Quy về thời gian hoặc công sức** — "Với API cũ, làm một tính năng mất vài tuần; sau refactor thì vài ngày."
4. **Mô tả điều gì trở nên khả thi** — "Sau khi ship, chúng tôi test được tính năng mới trên app mobile mà không cần submit build mới, mở ra khả năng thử nghiệm nhanh."

Số liệu là tốt nhất, nhưng tác động được diễn đạt rõ ràng thì vẫn hiệu quả.

**Giải thích.** Mục này quan trọng đặc biệt với bạn, vì iLetter chưa go-live.

Cách số 4 là cách mạnh nhất trong hoàn cảnh của bạn, và bạn đã có sẵn: **trước đây chỉ người viết được JSON mới tạo được form; sau khi có UI thì người không biết kỹ thuật cũng làm được.** Đó không phải "nhanh hơn" — đó là *một nhóm người trước kia không dùng được, giờ dùng được*. Loại kết quả này nhiều khi ấn tượng hơn cả một con số phần trăm.

Một lưu ý về sự trung thực: khi dùng cách 2 hoặc 3, nói rõ đó là quan sát hay ước lượng. Câu *"đây là quan sát của tôi, tôi chưa đo bằng số liệu hệ thống"* không làm bạn yếu đi — nó làm mọi con số khác bạn đưa ra đáng tin hơn.

---

## 7. Learnings — chứng minh bạn khá lên theo thời gian

**Nội dung.** Learnings cho thấy bạn là người tiến bộ. Bạn sẽ làm khác gì? Bạn học được gì về chính mình?

**Vượt qua mức hiển nhiên.** "Tôi học được rằng giao tiếp rất quan trọng" là sáo rỗng. "Tôi học được rằng khi làm với team ở xa, tôi phải ghi lại quyết định nhiều hơn bình thường vì không có những cuộc trao đổi tình cờ ở hành lang" mới là cụ thể và cho thấy hiểu biết thật.

**Thành thật về sai lầm.** "Lẽ ra tôi phải phản đối phạm vi công việc sớm hơn. Tôi biết chúng tôi không thể kịp hạn với chừng đó tính năng, nhưng tôi không muốn làm người nói không. Từ đó tôi học được là phải nêu lo ngại về tiến độ ngay khi nhìn thấy."

Learnings cũng cho thấy bạn tiếp nhận được phản hồi. Nếu bạn mô tả mọi thứ đều hoàn hảo, không có gì để cải thiện — đó là cờ đỏ.

**Giải thích.** Cách kiểm tra một Learning có giá trị hay không: **nó có dùng được cho một tình huống khác không?**

- "Tôi học được là phải test kỹ hơn" → dùng được ở đâu cũng được, tức là chẳng nói lên gì.
- "Khi tôi phải chọn một con số mà không giải thích được vì sao là con số đó, đó là dấu hiệu tôi chưa hiểu vấn đề" → là một nguyên tắc thật, áp dụng được ở tình huống khác, và cho thấy cách bạn suy nghĩ.

Cái thứ hai là Learning của story #7 của bạn. Đó là chuẩn để so những Learning khác.

Về sự thành thật: người phỏng vấn đã nghe hàng trăm ứng viên nói "điểm yếu của em là quá cầu toàn". Một lời thú nhận thật, có hậu quả cụ thể, tạo tin cậy cho **toàn bộ** phần còn lại của buổi phỏng vấn. Đây là lý do story #5 của bạn (mất 25 phút của người dùng vì tôi quên xử lý lỗi) đáng giá hơn nó có vẻ.

---

## 8. Thích ứng theo câu hỏi — nguyên tắc trailer

**Nội dung.** Không phải story nào cũng trả lời được mọi câu hỏi, nhưng phần lớn story chứa nhiều góc đáng khai thác. Hollywood cắt nhiều trailer khác nhau từ cùng một bộ phim: chỉ lấy cảnh của Arwen, Aragorn, Éowyn thì tưởng phim tình cảm; chỉ lấy cảnh Merry, Pippin, Gimli, Legolas thì tưởng phim hài. Cùng nguồn, khác nhấn nhá.

Story của bạn cũng vậy. Cùng một story chatbot có thể trả lời:

| Câu hỏi | Nhấn phần nào |
|---|---|
| "Kể về lần bạn kiên trì" | Học ML từ đầu, dịch ngược hệ thống cũ, lặp qua nhiều vòng phản hồi |
| "Kể về lần bạn thể hiện ownership" | "Không ai giao việc này. Tôi mang ý tưởng tới quản lý. Tôi phát hiện vấn đề và đề xuất giải pháp." |
| "Kể về kỹ năng giao tiếp của bạn" | Buổi gặp Support Director, dùng dữ liệu để thuyết phục, demo tiến độ để giữ các bên đồng bộ |
| "Kể về lần bạn ảnh hưởng mà không có quyền hạn" | Chủ động hẹn gặp Director, đưa dữ liệu để gỡ lo ngại, thương lượng một bản thử nghiệm |

**Nhấn đúng signal được hỏi ngay ở đầu câu trả lời.** Với các phần khác của story, đừng bỏ hẳn — nhắc lướt qua như một chú thích, phòng khi người nghe muốn đào tiếp.

**Giải thích.** Đây là kỹ thuật giúp bạn không cần 20 story. Bảy story của bạn phủ được hầu hết câu hỏi, miễn là bạn đổi được câu mở đầu.

Chỗ hay bị bỏ sót là vế thứ hai: **thả chú thích**. Ví dụ khi kể story #7 cho câu về kiên trì, thả một câu ngắn *"lúc đi hỏi tôi có soạn sẵn danh sách những gì đã loại trừ"* rồi đi tiếp. Nếu người nghe quan tâm giao tiếp, họ sẽ kéo bạn về chỗ đó — và lúc đó bạn có sẵn cả một mảng để nói. Đây là cách bạn **điều khiển được câu hỏi tiếp theo** thay vì bị động chờ.

---

## 9. Thích ứng theo người nghe — đọc phòng

**Nội dung.** Dấu hiệu bạn đang mất người nghe:

- Mắt lơ đãng
- Không hỏi tiếp về các đoạn kỹ thuật
- Có vẻ đang chờ bạn nói xong
- Tự bật mic hoặc mở miệng định chen vào
- Trông như đang nghĩ để đặt câu hỏi
- Ngừng ghi chép
- Nói "yeah", "hmm" liên tục — trong hội thoại thường đây là lắng nghe tích cực, nhưng trong phỏng vấn nhiều khả năng là dấu hiệu họ muốn chuyển chủ đề

Thấy những dấu hiệu này là bạn đã đi quá sâu. Điều chỉnh.

Chỉ đưa đủ chi tiết để tạo uy tín. Ví dụ thứ ba, thứ tư về một con bug khó không giúp được gì thêm sau ví dụ thứ nhất và thứ hai.

Nghĩ tới người đang ngồi trước mặt. Nói với một kỹ sư backend về dự án backend thì đi sâu vào kiến trúc được. Nói với hiring manager đã nhiều năm không viết code thì tập trung vào quyết định và đánh đổi, không phải chi tiết triển khai.

**Giải thích.** Với buổi culture fit ở HRS, khả năng cao người phỏng vấn là HR hoặc quản lý, **không phải kỹ sư**. Điều đó đổi cách kể story #7 khá nhiều: `flushSync`, `qiankun`, `webpack-dev-server` là những từ có thể làm họ trôi.

Bản cho người không chuyên sâu kỹ thuật:

> "Bug chỉ xảy ra trên bản đã deploy, không bao giờ xảy ra ở máy tôi. Nguyên nhân là một cuộc đua về thời điểm: trên môi trường thật, mọi thứ tải chậm hơn một chút, và cái đó làm lộ ra một chỗ trong code tôi đã ngầm giả định là sẽ luôn kịp."

Giữ nguyên toàn bộ ý nghĩa, bỏ hết thuật ngữ. Chuẩn bị sẵn **hai phiên bản** cho mỗi story kỹ thuật, chọn theo người ngồi đối diện.

Dấu hiệu "yeah, hmm liên tục" đáng chú ý nhất, vì người Việt hay hiểu ngược. Nghe thấy nó nhiều lên là **nhảy sang Results ngay**, đừng kể nốt cho đủ.

---

## 10. Story dài — dựng mục lục

**Nội dung.** Có story dài thật, thường ở người cấp cao với dự án hai năm, hoặc khi vòng phỏng vấn là đào sâu một dự án duy nhất.

Story dài khó kể và còn khó nghe hơn, nên cần một hệ thống tổ chức. Phần Actions thường là phần làm story dài ra, nên hãy **chia Actions thành các theme và đọc danh sách theme ngay sau Context** — gọi là "mục lục". Người nghe biết sắp tới cái gì, còn bạn thì có cột mốc để bám.

Ví dụ:

> "Dự án này diễn ra theo ba giai đoạn: thứ nhất là tạo đồng thuận về hướng đi; thứ hai là phần triển khai kỹ thuật; thứ ba là rollout và đo lường. Tôi xin đi qua từng phần."

Hoặc:

> "Tôi đóng góp theo bốn hướng: dựng kiến trúc kỹ thuật, mentor hai kỹ sư junior trong quá trình triển khai, quản lý kỳ vọng của stakeholder ở ba team, và chịu trách nhiệm phân tích số liệu sau khi ra mắt."

Việc này làm được ba thứ: giúp người nghe bám mạch (họ không phải tự hỏi "chỗ này rồi sẽ nối vào đâu?"), cho thấy bạn tư duy có tổ chức (vị trí cấp cao đòi hỏi khả năng cấu trúc thông tin phức tạp — thể hiện được ngay trong cách kể là bằng chứng), và giữ bạn không lạc đề.

**Đặt tên theme kèm kết luận, không chỉ chủ đề.** So sánh "Thiết kế kỹ thuật" với "Làm việc với tech lead để thiết kế quanh các ràng buộc phức tạp" — cái sau nói được nhiều hơn hẳn.

**Cân nhắc đưa Results lên đầu với story dài.** Nếu story đủ dài, rất có thể bạn không bao giờ kể tới Results vì người nghe đã kéo bạn vào các nhánh follow-up. Cứ nói Results, thậm chí bản rút gọn của Learnings, ngay từ đầu.

**Giải thích.** Mục lục là kỹ thuật cho hiệu quả cao nhất so với công sức bỏ ra trong cả bài này. Ba mươi giây nói mục lục đổi lại được sự chú ý của người nghe cho suốt ba phút sau đó.

Lý do sâu hơn: người nghe không thể vừa xử lý nội dung vừa đoán cấu trúc. Bạn đưa sẵn cấu trúc là giải phóng toàn bộ năng lực của họ cho nội dung.

Trong bộ story của bạn, chỉ #1 (11 tháng) và #7 (bốn theme) đủ dài để cần mục lục. Cả hai đã có sẵn trong file story. Các story còn lại ngắn, đọc mục lục sẽ thành máy móc.

---

## 11. Chuẩn bị cho câu hỏi đào sâu

**Nội dung.** Chắc chắn sẽ có follow-up. Bốn câu lặp đi lặp lại:

- **"Bạn sẽ làm khác gì?"** — họ đang tìm chiều sâu hiểu biết và khả năng nhận ra sai lầm. Nếu bạn đã có Learnings trong CARL thì đã sẵn sàng.
- **"Phần khó nhất là gì?"** — thường để đo chiều sâu kỹ thuật. Phải nắm rõ những trở ngại mình đã vượt.
- **"Bạn đo thành công thế nào?"** — kể cả khi đã nói kết quả, vẫn phải sẵn sàng nói về việc bạn có đặt mục tiêu đo được không và định nghĩa trạng thái hoàn thành ra sao.
- **"Sau đó thì sao?"** — dự án có tác động lâu dài không? Còn chạy không? Được mở rộng hay bị bỏ? Bạn có tham gia phần tiếp theo không?

Các nhóm follow-up khác nên chuẩn bị:

| Nhóm | Câu hỏi |
|---|---|
| **Về thử thách** | Phần khó nhất? Bạn làm việc dưới những ràng buộc nào? |
| **Về vai trò của bạn** | Đóng góp cụ thể của bạn? Bao nhiêu phần là ý bạn so với việc được giao? Còn ai tham gia và họ làm gì? |
| **Về quyết định** | Bạn ra quyết định đó thế nào? Đã cân nhắc phương án nào? Ai cùng tham gia quyết định? |
| **Về kết quả** | Đo thành công thế nào? Tác động dài hạn? Làm lại sẽ khác gì? |
| **Về quan hệ** | Người khác phản ứng ra sao với cách làm của bạn? Bạn nhận được phản hồi gì? Chuyện này ảnh hưởng thế nào tới quan hệ làm việc? |

**Giải thích.** Follow-up là nơi story thật lộ ra khỏi story thuộc lòng. Ai cũng kể trôi được đoạn đã chuẩn bị; chỉ người thật sự làm mới trả lời trôi được câu hỏi chệch khỏi kịch bản.

Nhóm nguy hiểm nhất với bạn là **"Về vai trò của bạn"**, vì hai lý do: story #1 dùng AI nhiều (họ sẽ hỏi *AI làm bao nhiêu phần*), và story #7 có anh senior đưa ra gợi ý then chốt (họ sẽ hỏi *nếu không có anh ấy thì sao*). Cả hai câu đã có đáp án chuẩn bị sẵn trong file story — đọc lại cho thuộc ý, đừng thuộc chữ.

Nhóm hay bị bỏ qua nhất là **"Về quan hệ"**. Ứng viên chuẩn bị rất kỹ phần kỹ thuật rồi đứng hình khi bị hỏi "sau chuyện đó quan hệ giữa hai người thế nào". Với story #3 của bạn, câu trả lời phải có sẵn và phải tích cực.

---

## 12. Ba ví dụ theo cấp bậc — dùng để hiệu chỉnh

**Nội dung tóm tắt.**

**Junior — vấn đề kỹ thuật khó.** API thỉnh thoảng trả lỗi 500 trên production, 2–3 lần mỗi ngày, không theo quy luật; người trực đã tìm cả tuần không ra. Ứng viên xung phong nhìn lại từ đầu: đối chiếu thời điểm lỗi với log deploy, thêm log chi tiết, phát hiện lỗi chỉ xảy ra ở một server cụ thể trong load balancer. Server đó đang chạy config cũ vì một bug trong script deploy bỏ sót nó khoảng 30% số lần. Sửa script để xác minh mọi server đã cập nhật trước khi báo deploy xong, thêm health check bắt lệch phiên bản. Lỗi 500 biến mất hoàn toàn, từ 2–3 lần mỗi ngày xuống 0 trong tháng sau. Viết lại quy trình debug vào wiki của team. Bài học: đừng mặc định lời giải thích hiển nhiên — mọi người đều nhìn vào code, nhưng bug nằm ở quy trình deploy.

**Senior — dự án xuyên team.** Thời gian khởi động nguội của app mobile tăng dần lên 8 giây sau hai năm thêm tính năng, và có tương quan với tỉ lệ gỡ app. Đường khởi động đụng code của năm team, không team nào coi performance là ưu tiên. Ứng viên đề xuất một sáng kiến xuyên team, viết một trang tóm tắt tác động tới người dùng, xin được một quý để dẫn dắt. Tự profile và chia nhỏ: 40% ở codebase chính, 30% ở một SDK bên thứ ba, 30% rải ở code khởi tạo của các team khác. Gặp 1:1 từng tech lead. Hai team đồng ý ưu tiên sửa; hai team nói không có người — ứng viên đề nghị tự viết PR nếu họ chịu review, và họ đồng ý. Với SDK thì liên hệ nhà cung cấp, đưa số liệu profiling và thuyết phục họ tối ưu trong bản kế tiếp. Kết quả: 8 giây xuống 3,2 giây (giảm 60%), tỉ lệ gỡ app trong phiên đầu giảm 15%, cách làm trở thành mẫu cho các dự án performance xuyên team sau này. Bài học: gặp người khác ở chỗ họ đang đứng — ban đầu tưởng ai cũng sẽ ưu tiên việc hiển nhiên quan trọng, nhưng các team có ràng buộc thật; đề nghị tự làm đã biến người cản thành người cộng tác.

**Staff — chiến lược kỹ thuật.** Sáu team sản phẩm mỗi team tự xây một pipeline dữ liệu riêng, khác công nghệ, khác schema, khác mức tin cậy. Ứng viên dành tháng đầu phỏng vấn từng team và lập bản đồ hiện trạng, và thấy lý do của mỗi team đều hợp lý ở thời điểm của họ. Viết RFC đề xuất hợp nhất về một nền tảng streaming, với ý tưởng then chốt là **không cần di chuyển tất cả cùng lúc**: xây một lớp adapter để các team tiếp tục dùng API cũ trong khi dữ liệu được định tuyến qua hệ thống mới — giảm mạnh rủi ro di chuyển. Việc thuyết phục cần những cuộc nói chuyện khác nhau cho từng nhóm: với lãnh đạo kỹ thuật thì nói về chi phí bảo trì và độ tin cậy; với lãnh đạo sản phẩm thì nói về khả năng phân tích xuyên sản phẩm; với từng team thì nhấn rằng họ chưa phải đổi code ngay. Kết quả sau sáu tháng: cả sáu team chuyển xong, chi phí bảo trì giảm ước tính 40% thời gian kỹ sư, và dashboard phân tích xuyên sản phẩm đầu tiên ra mắt trong ba tháng thay vì hơn sáu tháng như trước. Bài học: sức mạnh của việc **làm cho sự thay đổi có cảm giác an toàn** — lớp adapter tốn thêm công, nhưng chính nó khiến đề xuất khả thi về mặt chính trị; nếu đề xuất cắt chuyển thẳng thì đã vấp phải kháng cự lớn hơn nhiều.

**Giải thích.** Đọc ba ví dụ này để hiệu chỉnh scope. Điểm khác biệt giữa ba cấp **không phải độ khó kỹ thuật** — mà là:

| | Junior | Senior | Staff |
|---|---|---|---|
| Phạm vi | Một hệ thống | Năm team + nhà cung cấp ngoài | Sáu team + chiến lược tổ chức |
| Trở ngại chính | Kỹ thuật | **Con người** — team không có bandwidth | **Chính trị** — làm sao đổi mà không bị kháng cự |
| Bài học | Về cách debug | Về cách làm việc với người | Về cách khiến tổ chức chấp nhận thay đổi |

Chú ý: càng lên cao, bài học càng **ít về kỹ thuật**. Ở cấp Staff, phần kỹ thuật (lớp adapter) chỉ là phương tiện cho một mục tiêu con người.

Vị trí của bạn (mid-level) nằm giữa Junior và Senior: story #7 có hình dáng rất giống ví dụ Junior (chẩn đoán một bug khó, rút thành quy trình), còn story #4 đã chạm tới hình dáng Senior (trở ngại là con người và tổ chức, không phải kỹ thuật). Đó là lý do tôi xếp #4 là story mạnh nhất của bạn.

---

## 13. Bài tập

**Nội dung.**

1. Lấy một story cốt lõi từ catalog.
2. Viết đầy đủ cấu trúc CARL.
3. **Bấm giờ kể to.** Mục tiêu 2–4 phút. Quá giờ thì gần như chắc chắn là thừa Context hoặc thừa chi tiết ở Actions.
4. Viết sẵn câu trả lời cho bốn follow-up: làm khác gì · phần khó nhất · đo thành công thế nào · sau đó thì sao.
5. Lặp lại cho các story còn lại.

**Giải thích.** Bước 3 là bước duy nhất bạn chưa làm, và là bước quan trọng nhất còn lại.

Đọc thầm với nói ra miệng là hai việc khác nhau. Đọc thầm nhanh gấp rưỡi, nên story bạn tưởng dài 3 phút thực tế có thể 5 phút. Ngoài ra chỉ khi nói to bạn mới phát hiện những câu viết ra thì mượt mà nói thì vấp.

Cách làm hiệu quả: bật ghi âm điện thoại, kể một lượt không nhìn giấy, nghe lại. Ba thứ cần chú ý khi nghe lại — có nói "chúng tôi" chỗ nào không · Context có dài quá 45 giây không · có chỗ nào nói quá 30 giây mà chưa sang ý mới không.

**Đừng học thuộc lòng.** Thuộc chữ thì nghe như đọc bài và vỡ ngay khi bị cắt ngang. Thuộc **cấu trúc**: câu mở đầu, các theme, ba con số, câu Learning. Phần còn lại kể tự nhiên.

---

## Tóm tắt một trang

| Phần | Thời lượng | Quy tắc sống còn |
|---|---|---|
| Câu mở đầu | 20–30s | Trả lời thẳng câu hỏi + stakes có số |
| Mục lục *(chỉ story dài)* | 15s | Tên theme kèm kết luận, không chỉ chủ đề |
| **C** ontext | 30–45s | Tối thiểu đủ hiểu Actions. Cắt không thương tiếc. |
| **A** ctions | 60–120s | "Tôi", không "chúng tôi". Có lý do chọn. 30 giây một ý. |
| **R** esults | 20–35s | Ba chiều. Không có số thì dùng "điều gì trở nên khả thi". |
| **L** earnings | 20–35s | Phải dùng lại được ở tình huống khác. |
| Tổng | **2–4 phút** | Quá 4 phút là thừa Context hoặc thừa chi tiết |
