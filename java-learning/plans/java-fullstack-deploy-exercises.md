# BÀI TẬP THÁNG 6: FULL-STACK INTEGRATION & DEPLOY

**Tương ứng**: Tháng 6 trong lộ trình (Tuần 23-26)
**Mục tiêu**: Kết nối React/Next.js với Spring Boot, Docker, deploy lên cloud
**Quy ước**: ⭐ Dễ | ⭐⭐ Trung bình | ⭐⭐⭐ Khó

---

## Phần A: Full-Stack Integration (8 bài)

### A1. Gọi Spring Boot API từ Next.js ⭐

Tạo Next.js project, fetch data từ Spring Boot:

```typescript
// app/products/page.tsx
async function getProducts() {
    const res = await fetch('http://localhost:8080/api/products');
    if (!res.ok) throw new Error('Failed to fetch');
    return res.json();
}

export default async function ProductsPage() {
    const products = await getProducts();
    return (
        <ul>
            {products.map((p: any) => (
                <li key={p.id}>{p.name} - {p.price.toLocaleString()} VNĐ</li>
            ))}
        </ul>
    );
}
```

Bài tập:
1. Server Component fetch danh sách products
2. Hiển thị loading state
3. Xử lý error state
4. Kiểm tra CORS hoạt động

---

### A2. Auth Flow — Login từ React ⭐⭐

Implement login flow đầy đủ:

```typescript
// lib/auth.ts
export async function login(email: string, password: string) {
    const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
    });

    if (!res.ok) throw new Error('Login failed');

    const data = await res.json();
    localStorage.setItem('token', data.token);
    return data;
}

export function getAuthHeader() {
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function fetchWithAuth(url: string, options: RequestInit = {}) {
    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            ...getAuthHeader(),
            'Content-Type': 'application/json',
        },
    });
}
```

Bài tập:
1. Login form component
2. Lưu JWT token
3. Gọi protected API với token
4. Redirect khi token hết hạn
5. Logout (xóa token)

---

### A3. CRUD UI hoàn chỉnh ⭐⭐

Tạo các pages cho Task Management:

```
app/
├── page.tsx                 # Landing page
├── login/page.tsx           # Login form
├── register/page.tsx        # Register form
├── tasks/
│   ├── page.tsx             # Task list (GET /api/tasks)
│   ├── new/page.tsx         # Create task form (POST)
│   └── [id]/
│       ├── page.tsx         # Task detail (GET /api/tasks/:id)
│       └── edit/page.tsx    # Edit task form (PUT)
```

Mỗi page cần:
- Loading state (skeleton/spinner)
- Error state (error message + retry)
- Empty state ("Chưa có task nào")
- Form validation (client-side)

---

### A4. React Query / SWR ⭐⭐

Dùng TanStack Query (React Query) hoặc SWR cho data fetching:

```typescript
// hooks/useTasks.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export function useTasks() {
    return useQuery({
        queryKey: ['tasks'],
        queryFn: () => fetchWithAuth('/api/tasks').then(r => r.json()),
    });
}

export function useCreateTask() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (task: TaskRequest) =>
            fetchWithAuth('/api/tasks', {
                method: 'POST',
                body: JSON.stringify(task),
            }),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ['tasks'] }),
    });
}
```

Bài tập: Implement hooks cho tất cả CRUD operations. Optimistic updates cho delete.

---

### A5. Environment Variables ⭐

```bash
# .env.local (Next.js)
NEXT_PUBLIC_API_URL=http://localhost:8080

# .env.production
NEXT_PUBLIC_API_URL=https://api.myapp.com
```

```typescript
// lib/config.ts
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export function apiUrl(path: string) {
    return `${API_URL}${path}`;
}
```

Bài tập: Tách tất cả hardcoded URLs thành environment variables. Chạy local và production config.

---

### A6-A8: 3 bài nâng cao

**A6** ⭐⭐⭐ — **Protected Routes**: Tạo middleware Next.js kiểm tra JWT trước khi cho vào `/tasks/*`. Redirect sang `/login` nếu chưa auth.

**A7** ⭐⭐⭐ — **Real-time Updates**: Spring Boot gửi SSE (Server-Sent Events) khi task thay đổi. Next.js listen và auto-refresh.

**A8** ⭐⭐⭐ — **Error Boundary**: Tạo error boundary component bắt mọi lỗi, hiển thị UI fallback thay vì crash.

---

## Phần B: Docker (7 bài)

### B1. Dockerfile cho Spring Boot ⭐⭐

```dockerfile
# Multi-stage build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline    # Cache dependencies
COPY src ./src
RUN mvn package -DskipTests     # Build JAR

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Bài tập:
1. Build image: `docker build -t task-api .`
2. Run container: `docker run -p 8080:8080 task-api`
3. Kiểm tra size image, tối ưu bằng multi-stage build

---

### B2. Dockerfile cho Next.js ⭐⭐

```dockerfile
FROM node:20-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci

FROM node:20-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static
COPY --from=build /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
```

---

### B3. docker-compose — Chạy cả stack ⭐⭐

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: taskmanager
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports: ["5432:5432"]
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d taskmanager"]
      interval: 5s
      retries: 5

  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/taskmanager
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_THREADS_VIRTUAL_ENABLED: true
      JWT_SECRET: my-super-secret-key-change-in-prod

  frontend:
    build: ./frontend
    ports: ["3000:3000"]
    depends_on: [backend]
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080

volumes:
  pgdata:
```

Bài tập:
1. `docker compose up` — chạy cả 3 services
2. Mở browser → test full flow
3. `docker compose down -v` — cleanup

---

### B4. Docker Networking ⭐⭐

Hiểu cách services giao tiếp:

```
frontend (3000) ──► backend (8080) ──► db (5432)
    │                    │
    │   Từ browser:      │   Container-to-container:
    │   localhost:8080    │   db:5432 (tên service = hostname)
```

Bài tập: Tạo custom network, kiểm tra containers ping được nhau bằng tên service.

---

### B5-B7: 3 bài Docker nâng cao

**B5** ⭐⭐ — **Volumes & Persistence**: Mount source code vào container cho hot-reload khi dev. Tạo named volume cho database.

**B6** ⭐⭐⭐ — **Docker Compose Profiles**: Tạo profile `dev` (với hot-reload, debug port) và `prod` (optimized images).

**B7** ⭐⭐⭐ — **.dockerignore**: Tạo `.dockerignore` cho cả backend và frontend. Đo size image trước/sau.

---

## Phần C: CI/CD & Deployment (5 bài)

### C1. GitHub Actions — Build & Test ⭐⭐

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  backend:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports: ["5432:5432"]
        options: --health-cmd pg_isready --health-interval 10s

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build & Test
        working-directory: ./backend
        run: mvn verify
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'

      - name: Install & Build
        working-directory: ./frontend
        run: |
          npm ci
          npm run build
          npm run lint
```

Bài tập: Push code → xem CI chạy trên GitHub Actions → fix nếu lỗi.

---

### C2. Deploy Backend — Railway/Render ⭐⭐

Chọn 1 platform và deploy:

**Railway:**
1. Kết nối GitHub repo
2. Thêm PostgreSQL addon
3. Set environment variables
4. Deploy → nhận URL

**Render:**
1. Tạo Web Service từ GitHub repo
2. Set build command: `mvn package -DskipTests`
3. Set start command: `java -jar target/*.jar`
4. Thêm PostgreSQL database
5. Deploy

Bài tập: Deploy thành công, test API bằng curl từ local.

---

### C3. Deploy Frontend — Vercel ⭐

1. Kết nối GitHub repo
2. Set `NEXT_PUBLIC_API_URL` = URL backend đã deploy
3. Deploy → nhận URL

Test: Mở URL frontend → login → CRUD tasks → tất cả hoạt động.

---

### C4. Health Check, Monitoring & Observability ⭐⭐

Thêm Spring Boot Actuator + Micrometer:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized

# Bật Virtual Threads (Spring Boot 3.2+) — tăng throughput
spring.threads.virtual.enabled=true

# Structured logging (Spring Boot 3.4+)
logging.structured.format.console=ecs
```

Bài tập:
1. `GET /actuator/health` → `{"status": "UP"}`
2. `GET /actuator/info` → app info
3. `GET /actuator/metrics/http.server.requests` → xem request metrics
4. Bật `spring.threads.virtual.enabled=true` → so sánh throughput trước/sau
5. Configure uptime monitor (UptimeRobot free tier)

---

### C5. Capstone — Full Deployment ⭐⭐⭐

Deploy toàn bộ stack:
1. Backend trên Railway/Render (Spring Boot + PostgreSQL)
2. Frontend trên Vercel (Next.js)
3. CI/CD tự chạy khi push code
4. Health check endpoint
5. Custom domain (optional)

Checklist deploy:
- [ ] Backend API accessible
- [ ] Frontend loads
- [ ] Login/Register works
- [ ] CRUD operations work
- [ ] JWT authentication works
- [ ] CORS configured for production domain
- [ ] Environment variables set (NOT hardcoded)
- [ ] Virtual threads enabled (`spring.threads.virtual.enabled=true`)
- [ ] OpenAPI docs accessible (`/swagger-ui.html`)
- [ ] Health check endpoint working (`/actuator/health`)
- [ ] CI pipeline passes

---

## Checklist theo dõi

| Phần | Số bài | Hoàn thành |
|------|--------|-----------|
| A — Full-Stack Integration | 8 bài | ☐ |
| B — Docker | 7 bài | ☐ |
| C — CI/CD & Deployment | 5 bài | ☐ |
| **Tổng** | **20 bài** | |

---

## Tổng kết toàn bộ 6 tháng bài tập

| Tháng | File | Số bài |
|-------|------|--------|
| 1 — Java Cơ Bản | `java-basic-exercises.md` | 60 |
| 2 — OOP & Collections & Concurrency | `java-oop-collections-exercises.md` | 50 |
| 3 — SQL & Database | `java-sql-database-exercises.md` | 30 |
| 4 — Spring Boot & REST API | `java-springboot-rest-exercises.md` | 24 |
| 5 — Testing & Security | `java-testing-security-exercises.md` | 20 |
| 6 — Full-Stack & Deploy | `java-fullstack-deploy-exercises.md` | 20 |
| **TỔNG** | | **204 bài** |

Hoàn thành tất cả 204 bài → bạn đã sẵn sàng nhận role **Fullstack Developer** (React + Spring Boot).
