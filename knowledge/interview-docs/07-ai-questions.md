# Câu hỏi về AI (bản tiếng Việt có giải thích + áp dụng)

HRS bán sản phẩm AI. Với họ đây không phải chủ đề thời sự, **đây là roadmap** — nên phần này đào sâu hơn em nghĩ.
Mục 16 của `00-hrs-master-guide.html` đã có phần "dùng AI thế nào, kiểm soát ra sao". Tài liệu này bù ba vùng còn thiếu và soạn câu trả lời cho chúng.

---

# PHẦN I — NĂM VÙNG NGƯỜI PHỎNG VẤN ĐÁNH GIÁ

## Vì sao câu hỏi AI xuất hiện ở mọi buổi

Công ty đang chịu sức ép từ trên xuống (lãnh đạo muốn tăng tốc phát triển bằng AI) và sức ép ngang (đối thủ đang cải thiện tốc độ vòng đời phát triển). Nên phỏng vấn bắt đầu dò tín hiệu: em có bám kịp không, và em có dùng AI **hiệu quả** không.

Ba dạng câu hỏi hay gặp:

- *"Kể về một lần em dùng AI để làm được thứ mà nếu không có nó thì không làm được."*
- *"Công cụ hoặc quy trình nào em mới áp dụng trong sáu tháng qua?"*
- *"Em nghĩ thế nào về đánh đổi giữa tốc độ và kiểm chứng khi làm việc với AI?"*

## Năm vùng

| Vùng | Đo cái gì | Là phần mở rộng của |
|---|---|---|
| **1. Work** | Em thật sự **xây được gì** bằng AI | Scope |
| **2. Trust** | Em **kiểm chứng** và giữ an toàn thế nào | 🔴 **vùng mới, không chuyển giao được** |
| **3. Iteration** | Em làm AI **tốt lên theo thời gian** thế nào | Perseverance + Growth |
| **4. Growth** | Em **học và bám kịp** thế nào | Growth |
| **5. Scaling** | Em **lan ra ngoài bản thân** thế nào | Leadership |

## Vì sao Trust là vùng đặc biệt

Bốn vùng kia chỉ là signal area cũ khoác áo AI. **Trust thì không.**

Lý do: kinh nghiệm ở lĩnh vực khác **chuyển giao được**. Em chưa dùng một debugger cụ thể, chưa viết một ngôn ngữ cụ thể — nhưng kinh nghiệm với debugger khác, ngôn ngữ khác vẫn giúp em bắt nhịp.

Với AI thì không. Em phải **đã thật sự bắt được AI sai**, đã tự dựng cách kiểm chứng, đã hiểu những kiểu hỏng đặc thù của công nghệ này. Không có trải nghiệm đó thì không diễn được.

> Hệ quả: nếu em chỉ kể chuyện ship nhanh, em nghe **ngây thơ**. Người phỏng vấn đang chờ nghe em kể về lần AI làm em mất thời gian.

## Năm vùng theo cấp bậc

- **Junior** — cần mạnh ở **Work** và **Trust**.
- **Mid-level trở lên** — cần cả năm, **và đừng bỏ quên Scaling**, vì đó là chỗ phân biệt người dùng AI giỏi với người làm cả team dùng AI giỏi hơn.

---

# PHẦN II — CHI TIẾT TỪNG VÙNG

## 1. Work — em xây được gì bằng AI

**Nội dung.** Họ muốn thấy AI tạo ra khác biệt **cụ thể** trong thứ em ship: một tính năng xong nhanh hơn, một pipeline bỏ được việc thủ công, một công cụ mà trước đây không ai có thời gian làm. Mạnh hơn nữa là story mà AI **không chỉ tiết kiệm thời gian mà đổi cả thứ có thể làm được** — ví dụ một bản dựng thử giao trong một ngày, đủ để lãnh đạo tự tin đổi hướng.

**Viết code bằng AI giờ là mức sàn.** Thứ tách ứng viên mạnh là dùng AI ở những phần khó tự động hơn: điều tra bug, review thiết kế, dọn backlog, viết postmortem sự cố. Cao hơn nữa: em có nhìn quy trình phát triển của team từ đầu đến cuối và **xây lại một phần của nó** không?

**Quan trọng ngang bằng: cho thấy phán đoán về chỗ AI KHÔNG hợp.** *Không dùng AI cũng là một kỹ năng* — nó chứng minh em có mô hình đúng về năng lực của nó.

Mẫu câu mạnh:

> *"Em bắt đầu dùng AI cho X nhưng bỏ khi Y xảy ra, vì em nhận ra nó đang đoán những thứ nghe hợp lý mà không có ngữ cảnh codebase. Em làm nốt bằng tay."*

## 2. Trust — kiểm chứng và giữ an toàn

**Ba mặt:**

**Kiểm chứng tính đúng.** AI giỏi viết code vì code **kiểm chứng được**, và dở viết văn vì văn hay khó định nghĩa và khó test. Nếu em không tận dụng sự thật đó — bằng chế độ test, bằng vệ sinh CI/CD — thì codebase chỉ tệ đi khi đổ AI vào.

**Nhận ra lỗi đặc thù miền.** AI vấp ở những thứ riêng của từng codebase: pattern cũ, ràng buộc hiệu năng. Câu hỏi họ đang ngầm hỏi: *model đã sai cái gì trong ngữ cảnh cụ thể của em mà một kỹ sư senior trong team em sẽ biết để tránh?*

**Quản lý thứ em chia sẻ ra ngoài.** Công cụ AI chạy trên API bên ngoài tạo ra một loại phán đoán chưa từng tồn tại: cái gì an toàn để lộ ra. Dữ liệu khách hàng, logic nghiệp vụ nội bộ, roadmap chưa công bố, code nhạy cảm về bảo mật — đều có rủi ro. **Ứng viên mạnh đã nghĩ rõ và mô tả được một lằn ranh mình đã vạch.** Họ nghe xem em có cách tiếp cận có cấu trúc hay đang làm bừa.

## 3. Iteration — làm AI tốt lên theo thời gian

**Nội dung.** Công cụ AI cũng là hạ tầng: cần bảo trì và cần nhìn lại. Họ muốn thấy em cải thiện cách dùng hằng ngày khi thu được phản hồi.

Hai câu tự hỏi: **cách em dùng hoặc công cụ em dùng đã đổi gì trong tháng vừa rồi?** Khi nó cho kết quả tệ, em có **cập nhật hệ thống** không (rules file, skills, `CLAUDE.md`)?

Ví dụ tốt: duy trì một file rules 200 dòng đã xoá sổ cả nhóm lỗi lặp lại · dựng thư viện prompt tái dùng cho migration, sinh test, review code · dựng bảng kiểu kanban theo dõi nhiều agent chạy song song.

## 4. Growth — học và bám kịp

**Nội dung.** Ngành vốn đã đổi nhanh, nhưng mức thay đổi hiện tại là chưa từng có, nên đây thành yêu cầu bắt buộc với người mới.

**Thứ tách câu trả lời mạnh khỏi câu yếu là tính cụ thể và bằng chứng em đã HÀNH ĐỘNG sau khi học.** Phải nêu được nguồn và kỹ thuật đã thử.

> ⚠️ **Nêu tên ít nhất một nguồn cụ thể.** "Em đọc trên mạng" là câu trả lời rỗng.

## 5. Scaling — lan ra ngoài bản thân

**Nội dung.** Hai chiều: **bề rộng** (phổ biến cách làm tốt ra xung quanh) và **chiều sâu** (thay đổi tận gốc cách công việc vận hành).

Câu hỏi họ ngầm hỏi: em đã làm gì để mở rộng việc dùng AI ra ngoài bản thân? Em đề xuất thay đổi thế nào cho thuyết phục? Em xử lý ra sao với **cả hai phía** — người lo ngại và người quá tin vào output của AI?

Với mid-level trở lên, tác động lớn nhất là **thay đổi cấu trúc quy trình**: nhìn một bài toán nghiệp vụ từ đầu đến cuối và dựng lại nó cho tốt hơn, nhanh hơn, rẻ hơn.

---

# PHẦN III — KIỂM KÊ AI CỦA EM

| Vùng | Nguyên liệu đang có | Trạng thái |
|---|---|---|
| **Work** | iLetter: dùng AI dựng UI cho bản thử nghiệm → được duyệt lên production · Service AI với 3 tính năng người dùng: sinh form từ prompt, đọc PDF/docx/ảnh dựng thành form, tổng hợp câu trả lời | ✅ **mạnh** |
| **Trust** | Lần AI đi sai hướng, em revert và làm lại · 2 tuần refactor cho thấy output không dùng thẳng được · ràng buộc bằng output schema · **story #9: người khác đổ trách nhiệm cho AI, em đặt lại chuẩn** | ✅ **mạnh** |
| **Iteration** | **`ai-kit`** — bộ công cụ multi-agent tự dựng: 18 agent, 56 skill, hook có test, workflow 15 pha, đồng bộ ra 4 nền tảng (Claude Code, Antigravity, Codex, OpenCode) · CLI cài bằng `npx` · 48 commit trong ~2,5 tháng | ✅ **rất mạnh** |
| **Growth** | roadmap.sh phần AI · chương trình AI training của công ty có **test định kỳ mỗi quý** · theo dõi Andrew Ng, Matt Pocock — và **áp dụng thật**: bộ skill của Matt Pocock nằm trong setup hiện tại · workshop ADK ở Dev Day rồi mang về dùng ngay | ✅ **mạnh** |
| **Scaling** | **story #9: rules nạp tự động cho 6 feature team** · **`ai-kit` có 4 người đóng góp** *(em 28 commit, ba đồng nghiệp 20 commit)* · CLI publish để người khác cài được · service AI thiết kế cho module khác cắm vào | ✅ **mạnh** |

## Hai thứ làm em khác phần lớn ứng viên

### 1. Em xây AI **vào trong sản phẩm**, không chỉ dùng AI để viết code

Phần lớn ứng viên trả lời câu hỏi AI bằng "em dùng Copilot / Cursor để code nhanh hơn". Em có thứ khác hẳn: em **thiết kế một service AI trong sản phẩm**, có output schema, có tools, có instructions, và ba tính năng người dùng cuối thật sự dùng.

Với HRS — công ty **bán** một sản phẩm AI — đây là loại kinh nghiệm gần với việc họ đang làm hơn hẳn. Phải nói rõ sự phân biệt này, đừng để nó lẫn vào.

### 2. Em có kinh nghiệm dữ liệu nhạy cảm **và** làm việc với legal

Story #4 là về luật bảo vệ dữ liệu Thuỵ Sĩ, làm việc với bộ phận legal, và ràng buộc "lọc ở server, xoá định danh sau khi gửi".

Ghép nó với chủ đề AI thì thành một góc rất hiếm: **em không chỉ biết AI hay sai, em còn từng vận hành dưới ràng buộc pháp lý về dữ liệu.** Đó chính là vùng Trust ở mức tổ chức, không phải mức cá nhân. Với công ty châu Âu bán AI cho doanh nghiệp thì đây là thứ họ quan tâm thật.

### 3. Chỗ nối đẹp nhất — human-in-the-loop

Mục 16 tài liệu của em đã chỉ ra chỗ căng trong sản phẩm HRS Copilot: bản tin ra mắt nói quản lý phải phê duyệt mọi hành động AI đề xuất, nhưng trang sản phẩm hiện tại nhấn "act on them in one click".

**Tính năng của em cũng đúng thiết kế đó:** AI sinh ra form, **người dùng chỉnh lại rồi mới gửi** — AI không bao giờ gửi thẳng. Đó là human-in-the-loop, và em chọn vậy có lý do.

Câu nói:

> "Em thấy chỗ này thú vị vì tính năng của em cũng phải chọn đúng đánh đổi đó. AI sinh form từ một prompt, nhưng em không cho gửi thẳng — người dùng luôn chỉnh lại trên form đã sinh rồi mới gửi đi. Lý do là chi phí sai không đối xứng: gửi nhầm một khảo sát cho hàng trăm người thì không thu lại được, còn bắt người ta bấm thêm một bước thì chỉ mất vài giây. Em nghĩ ranh giới nằm ở chỗ hành động có đảo ngược được không, chứ không nằm ở chỗ AI đúng bao nhiêu phần trăm."

---

# PHẦN IV — CÂU TRẢ LỜI SOẠN SẴN

## Q1. "Kể một lần AI giúp em làm được thứ mà nếu không có nó thì không làm được" `[~60s]`

> "Module của em có một tính năng đọc bộ câu hỏi có sẵn ở dạng PDF, docx hoặc ảnh rồi dựng thành form. Trước khi có AI thì tính năng này gần như không làm được — parse layout tài liệu bằng luật cứng thì mỗi định dạng một kiểu, và bên em không có nguồn lực cho việc đó.
>
> Nhưng em không giao trọn cho AI. Em ràng buộc output bằng schema để nó chỉ trả về đúng cấu trúc form em định nghĩa, và người dùng luôn chỉnh lại trên kết quả sinh ra chứ không gửi thẳng. Cách đó biến một bài toán 'phải đúng 100%' thành bài toán 'đúng 80% rồi người sửa nốt' — và ở mức đó thì nó dùng được.
>
> Bài học của em là AI không đổi việc code nhanh hay chậm, nó đổi **loại bài toán mình dám nhận**."

**Vì sao câu này mạnh:** đúng mẫu "AI đổi thứ có thể làm được" chứ không phải "AI tiết kiệm thời gian" · có Trust nhúng sẵn (schema + human-in-the-loop) · kết bằng một nguyên tắc tái dùng được.

## Q2. "Công cụ hoặc quy trình nào em mới áp dụng gần đây?" `[~45s]`

Đây là câu **Growth**, và nó cần một nguồn có tên. Khung:

> "Gần nhất là ADK — bộ công cụ dựng agent workflow. Em biết tới nó qua workshop của hai kỹ sư AI của Google ở Dev Day công ty em, và em không dừng ở việc nghe: em mang về áp thẳng vào module của mình, dựng thành một service riêng thay vì gắn cứng AI vào một chỗ.
>
> [+ nguồn học thường xuyên — xem Phần V]
>
> Cách em học là thử trên việc thật chứ không làm ví dụ mẫu, vì với AI thì chỉ khi chạy vào codebase thật mới thấy nó hỏng ở đâu."

## Q3. "Cân bằng tốc độ và kiểm chứng thế nào?" `[~50s]`

> "Em chia theo **chi phí của một lỗi**, không chia theo loại công việc.
>
> Chỗ chi phí sai thấp và phát hiện được ngay — boilerplate, test case, refactor lặp lại, giải thích code cũ — em để AI làm nhiều, vì test và type system đã là lưới an toàn sẵn.
>
> Chỗ chi phí sai cao hoặc khó phát hiện — quyết định kiến trúc, chọn thư viện, và mọi thứ chạm tới tiền, dữ liệu người dùng, bảo mật — em vẫn tự làm, và nếu có dùng AI thì chỉ dùng như một danh sách ứng viên để em tự đánh giá.
>
> Nguyên tắc cứng của em là không đưa vào code thứ em chưa đọc hiểu. Phần nào em không giải thích được thì em viết lại, kể cả nó đang chạy đúng.
>
> Rủi ro lớn nhất của AI với em không phải nó viết code sai — cái đó review là ra. Rủi ro lớn hơn là nó khiến người ta bỏ qua bước hiểu vấn đề. Code chạy được mà không ai trong team hiểu vì sao thì sáu tháng sau thành nợ không sửa nổi."

## Q4. "Em nghĩ thế nào về việc áp dụng AI trong tổ chức?" `[~45s]`

Đây là câu **Scaling**, vùng yếu nhất của em. Trả lời thành thật, đừng phóng đại:

> "Em nghĩ chỗ AI tạo giá trị lớn nhất không phải là làm từng task nhanh hơn, mà là nhìn lại cả quy trình xem có phần nào giờ làm được theo cách khác. Đó là lý do khi em làm service AI, em không gắn cứng nó vào module của mình mà thiết kế để module khác cắm vào bằng cách khai báo endpoint, tools và output schema.
>
> Thành thật là tới giờ mới có module của em dùng — và bài học của em chính là ở đó: em thiết kế cho khả năng dùng lại trước khi đi hỏi các team khác xem họ có nhu cầu thật không. Lần sau em sẽ nói chuyện với hai ba team trước, rồi mới tổng quát hoá."

**Vì sao thừa nhận lại tốt hơn phóng đại:** với Head of Engineering, một câu "em xây nền tảng cho cả công ty" mà chỉ có một người dùng sẽ vỡ ngay ở follow-up đầu tiên. Nói thật kèm bài học thì vừa an toàn vừa ghi điểm Growth.

## Q5. "Em bắt được AI sai ở đâu trong codebase của mình?"

Đây là câu **Trust** khó nhất và họ rất hay hỏi. Em đã có nguyên liệu — lần AI đi sai hướng và em revert.

> "Lần đáng nhớ nhất là khi em giao cả một phần implementation cho AI mà không kiểm từng bước. Nó chạy được một phần, nhưng đi sai hướng so với kiến trúc đang có. Lúc phát hiện thì em có hai lựa chọn: chữa dần, hoặc bỏ đi làm lại. Em chọn bỏ, vì càng chữa càng phải sửa lan sang chỗ khác.
>
> Từ đó em đổi cách làm: em tự viết phần khung và ranh giới trước, rồi mới để AI điền vào từng phần nhỏ, và kiểm từng bước thay vì kiểm ở cuối. Cái em học được là AI hỏng không phải theo kiểu báo lỗi — nó hỏng theo kiểu **nghe rất hợp lý**, nên phải kiểm ở chỗ nó chưa kịp đi xa."

---

# PHẦN V — BỐN CHỖ ĐÃ ĐIỀN

## 1. ✅ Nguồn học AI

> "Em học AI theo ba lớp. Lớp nền là **roadmap.sh** phần AI, để biết mình đang thiếu mảng nào chứ không học vụn. Lớp thứ hai là chương trình **AI training của công ty**, có test định kỳ mỗi quý, nên nó buộc em phải cập nhật đều chứ không học theo hứng. Lớp thứ ba là theo dõi một số người làm thật — **Andrew Ng** cho phần nền tảng và hướng đi của ngành, **Matt Pocock** cho phần kỹ thuật hằng ngày.
>
> Với người thứ ba thì em không chỉ đọc. Bộ skill của Matt Pocock là phần đóng góp lớn nhất vào setup AI hiện tại của em — em lấy về, dùng, rồi sửa lại cho hợp việc của mình."

**Vì sao câu này mạnh:** có **tên cụ thể** · có **cơ chế bắt buộc cập nhật** (test hằng quý) · và có **bằng chứng đã hành động sau khi học**, không dừng ở đọc. Đó đúng ba thứ bài viết đòi ở vùng Growth.

## 2. ✅ Lằn ranh dữ liệu *(bản nháp — đọc và sửa cho đúng thực tế của em)*

> ⚠️ **Chỉ nói phần nào đúng với em.** Nếu công ty chưa có quy định thì nói thẳng là quy định của riêng em, đừng dựng thành chính sách công ty.

> "Em tách làm hai chuyện khác nhau, vì rủi ro khác nhau.
>
> **Chuyện thứ nhất là em dùng AI để làm việc.** Em không đưa dữ liệu người dùng thật vào công cụ chạy trên API bên ngoài — cụ thể với module của em là nội dung câu trả lời khảo sát và phần định danh người nhận. Khi cần AI giúp trên đoạn code có dính dữ liệu, em thay bằng dữ liệu giả có cùng hình dạng. Em cũng tránh đưa nguyên khối code chạm tới xác thực và phân quyền, vì đó là chỗ một gợi ý nghe hợp lý mà sai thì hậu quả không đối xứng.
>
> **Chuyện thứ hai là sản phẩm gọi AI.** Cái đó không phải quyết định cá nhân của em — dữ liệu đi qua nhà cung cấp nào là quyết định ở tầng công ty, và service AI của em được team AI nội bộ cùng CEO duyệt trước khi làm. Em nghĩ đúng ra nó phải như vậy: một kỹ sư không nên tự quyết dữ liệu khách hàng đi ra ngoài bằng đường nào.
>
> Cái này với em không phải lý thuyết. Em từng làm một tính năng phải qua bộ phận legal vì luật bảo vệ dữ liệu ở Thuỵ Sĩ, và họ đặt hai ràng buộc rất cụ thể — lọc ở phía server, và xoá định danh sau khi gửi. Từ đó em quen với việc hỏi trước là dữ liệu này được phép đi đâu."

**Vì sao câu này mạnh:** phân biệt **rủi ro cá nhân** với **rủi ro sản phẩm** — rất ít ứng viên tách được hai cái · và nối thẳng vào story #4, biến một câu hỏi về AI thành bằng chứng em từng vận hành dưới ràng buộc pháp lý thật. Với công ty châu Âu bán AI cho doanh nghiệp thì đây là góc đắt.

## 3. ✅ `ai-kit` — bằng chứng Iteration **và** Scaling

Đây là tài sản AI mạnh nhất của em, và em suýt không kể nó.

**Số liệu:** 18 agent · 56 skill · hook có test ở cả ba nền tảng · workflow 15 pha từ Problem tới Post-Launch · đồng bộ tự động từ một nguồn sự thật ra 4 nền tảng (Claude Code, Antigravity, Codex, OpenCode) · CLI cài bằng `npx` · **48 commit trong khoảng 2,5 tháng** · **4 người đóng góp**.

> "Em có một dự án riêng tên `ai-kit` — bộ công cụ đóng gói agent, skill và hook cho các công cụ AI coding. Nó bắt đầu từ chỗ em thấy mình lặp lại cùng một nhóm sai lầm khi làm việc với AI, và mỗi lần sửa xong thì lần sau lại quên. Nên thay vì sửa từng lần, em ghi nó thành luật.
>
> Hiện nó có 18 agent và 56 skill, kèm hook có test. Phần em thấy thú vị nhất về mặt kỹ thuật là nó có một nguồn sự thật duy nhất rồi đồng bộ ra bốn nền tảng khác nhau — Claude Code, Antigravity, Codex, OpenCode — vì em không muốn đổi công cụ là mất hết cách làm việc của mình. Em cũng đóng gói thành CLI để cài vào bất kỳ repo nào bằng một câu lệnh.
>
> Và nó không còn là của riêng em — hiện có **bốn người đóng góp**, ba đồng nghiệp của em cùng thêm skill và agent vào đó."

**Câu chốt nếu họ đào tiếp về vùng Scaling:**

> "Cái em học được là chia sẻ cách dùng AI bằng tài liệu thì gần như không ai đọc. Đóng gói thành thứ cài một câu lệnh là chạy được thì người ta mới dùng — và khi họ dùng rồi thì họ bắt đầu đóng góp ngược lại."

## 4. ✅ Chia sẻ ra ngoài bản thân

Ba đồng nghiệp đóng góp vào `ai-kit` **chính là** câu trả lời cho vùng Scaling. Cộng thêm:

- Buổi chia sẻ React cho 6 team *(story #3)* chứng minh em có thói quen lan kiến thức, không chỉ với AI
- Service AI thiết kế cho module khác cắm vào bằng config *(story #2)*

⚠️ **Vẫn giữ sự trung thực về story #2:** `ai-kit` có người dùng thật, còn service AI trong sản phẩm thì mới một module. Đừng gộp hai cái làm một.

---

## Còn một chỗ cần em xác nhận

`ai-kit` có commit gần nhất khoảng ba tháng trước. Nếu họ hỏi *"em còn duy trì nó không?"* thì trả lời thế nào? Nếu vẫn đang dùng hằng ngày dù không commit thì nói vậy. Nếu đã dừng thì nói thật kèm lý do — dừng vì công cụ đổi, hay vì đã ổn định không cần sửa nữa.

## 5. ✅ Xử lý người quá tin vào AI — story #9

Bài viết hỏi thẳng ở vùng Scaling: *"em xử lý thế nào với cả hai phía — người lo ngại về AI, và những kỹ sư quá tin vào output của AI?"*

Vế thứ hai giờ có story #9 trong `04-stories.md`. Ba ý cần nhớ:

- **Hỏi để hiểu ý định trước khi phán** — "em hiểu là chỗ này cần tuỳ biến style cho row nên mới tạo riêng, đúng không?"
- **Vấn đề không phải AI kém, mà là AI không biết bên mình đã có sẵn cái gì** — chính họ đưa lại yêu cầu kèm thông tin về component sẵn có thì AI ra đúng ngay
- **Sửa ở tầng hệ thống** — viết rules vào `claude/rules` và hook Copilot, nạp tự động khi ai đụng tới common component hoặc UI

Câu chốt:

> "Trách nhiệm không chuyển sang AI được. AI viết ra thì AI không chịu trách nhiệm — người mở PR chịu trách nhiệm. Và khi AI sai lặp đi lặp lại thì đừng sửa từng lần, hãy sửa thứ nó đọc."

Còn vế thứ nhất — **người lo ngại, không muốn dùng AI** — em chưa có story. Nếu bị hỏi thì trả lời bằng nguyên tắc: bắt đầu từ việc họ đang mất thời gian nhất, cho thấy trên chính việc của họ, và đừng ép ai dùng cho những việc mà chi phí sai cao.

# PHẦN VI — CHECKLIST

- [ ] Phân biệt rõ hai thứ khi trả lời: **dùng AI để code** và **xây AI vào sản phẩm**. Em có cả hai, và cái thứ hai hiếm hơn
- [ ] Có ít nhất một story em **bắt được AI sai** — không có thì nghe ngây thơ
- [ ] Nêu được **tên** nguồn học AI — roadmap.sh · AI training công ty (test hằng quý) · Andrew Ng · Matt Pocock
- [ ] Nói được **lằn ranh dữ liệu** — tách rủi ro cá nhân và rủi ro sản phẩm
- [ ] **Kể story #9** — người khác đổ trách nhiệm cho AI, em đặt lại chuẩn bằng rules tự nạp
- [ ] **Kể `ai-kit`** — 18 agent, 56 skill, 4 nền tảng, **4 người đóng góp**
- [ ] Có câu về **human-in-the-loop** nối vào HRS Copilot
- [ ] Nói thật về Scaling, đừng phóng đại thành "nền tảng cho cả công ty"
- [ ] Chi tiết về LLM của Anthropic trong Copilot: **đóng khung là đọc trên báo ngành**, không nói như thông tin chính thức
