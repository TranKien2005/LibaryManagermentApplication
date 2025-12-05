# Java Performance Testing

Performance testing đơn giản cho backend sử dụng Java & Apache HttpClient.

## Công cụ

Chỉ sử dụng **Apache HttpClient** - Đơn giản, không phụ thuộc nhiều thư viện.

## Cài đặt

```powershell
cd java-tests
mvn clean install
```

## Chạy Tests

### 0️⃣ Setup Test Accounts (Chỉ chạy 1 lần đầu) 🔧

**QUAN TRỌNG:** Chạy trước khi test lần đầu tiên!

```powershell
mvn compile exec:java -Dexec.mainClass="com.library.performance.SetupTestAccounts"
```

**Tạo gì:**
- 500 test accounts (testmanager1 đến testmanager500)
- Account Type: **Manager** (có tất cả quyền truy cập)
- Password giống nhau: Manager@123
- Endpoint: `/accounts/register`
- Lưu danh sách vào file `test-accounts.txt`
- Thời gian: ~30-60 giây

**Kết quả:**
```
╔══════════════════════════════════════════════════════════╗
║   SETUP TEST ACCOUNTS - Creating 500 Manager Accounts   ║
╚══════════════════════════════════════════════════════════╝

Account Type: Manager (FULL ACCESS)

Progress: 50/500 accounts created...
Progress: 100/500 accounts created...
...
Progress: 500/500 accounts created...

📊 ACCOUNT CREATION RESULTS
Successful: 500/500
✅ Account list saved to: test-accounts.txt
```

---

### 1️⃣ Simple Load Test (Test nhanh) ⚡

Test nhanh các endpoint chính với load vừa phải.

```powershell
cd java-tests
mvn test-compile exec:java "-Dexec.mainClass=com.library.performance.SimpleLoadTest"
```

**Test gì:**
- 12 endpoints khác nhau (Books, Accounts, Borrows, Returns, Users)
- 200 requests với 20 concurrent threads
- Hiển thị breakdown theo từng endpoint

**Kết quả mẫu:**
```
╔════════════════════════════════════════════════╗
║     LIBRARY BACKEND PERFORMANCE TEST           ║
╚════════════════════════════════════════════════╝

========== RESULTS ==========
Success Rate: 98.5%
Requests/Second: 45.2
Average: 245ms

========== ENDPOINT BREAKDOWN ==========
/books                  Requests: 20 | Success: 20 (100%) | Avg: 230ms
/books/search          Requests: 20 | Success: 20 (100%) | Avg: 280ms
...
```

---

### 2️⃣ Stress Test (Tìm giới hạn hệ thống) 🔥

Tăng dần concurrent users để tìm breaking point. **Sử dụng 500 test accounts đã tạo.**

```powershell
cd java-tests
mvn compile exec:java -Dexec.mainClass="com.library.performance.StressTest"
```

**Test gì:**
- Tăng dần users: 1 → 5 → 10 → 20 → 50 → 100 → 200 → 500
- **Mỗi request dùng token khác nhau** (từ pool 100 tokens)
- Simulate real-world với nhiều users khác nhau
- Tìm configuration tối ưu
- Tự động dừng khi hệ thống quá tải
- Hiển thị throughput và success rate theo từng level

**Kết quả mẫu:**
```
╔══════════════════════════════════════════════════════════╗
║     STRESS TEST - Finding System Limits                 ║
╚══════════════════════════════════════════════════════════╝

🔥 Testing with 10 concurrent users...
  ✅ Users:  10 | Success: 100/100 (100%) | Avg: 210ms | Throughput: 125.8 req/s

🔥 Testing with 100 concurrent users...
  ⚠️  Users: 100 | Success:  85/100 (85%) | Avg: 1250ms | Throughput: 45.1 req/s

📊 STRESS TEST SUMMARY
🎯 Optimal Configuration: 20 concurrent users with 142.3 req/s throughput
```

## Tùy chỉnh

### SimpleLoadTest.java
```java
private static final String BASE_URL = "http://localhost:8080/api";
private static final int TOTAL_REQUESTS = 200;           // Số requests
private static final int CONCURRENT_THREADS = 20;        // Concurrent threads
private static final boolean TEST_ALL_ENDPOINTS = true;  // true = test tất cả, false = chỉ Books
```

### StressTest.java
```java
private static final String TEST_ENDPOINT = "/books";    // Endpoint để stress test
private static final int REQUESTS_PER_LEVEL = 100;       // Requests mỗi level
private static final int[] USER_LEVELS = {1, 5, 10, 20, 50, 100, 200};  // Các levels test
```

## Metrics được đo

Cả 2 tests đều đo:
- ✅ **Success Rate** - Tỷ lệ requests thành công
- ⏱️ **Response Time** - Thời gian phản hồi (Avg, Min, Max, P50, P95, P99)
- 🚀 **Throughput** - Requests/giây
- 📊 **Breakdown** - Stats chi tiết theo endpoint (SimpleLoadTest) hoặc theo user level (StressTest)

## Workflow khuyến nghị

```
1. Khởi động backend
2. SimpleLoadTest     → Quick health check (2-3 phút)
3. StressTest         → Tìm breaking point (5-10 phút)
4. Phân tích kết quả và tối ưu hóa
```

## Best Practices

1. **Khởi động backend trước khi test**
   ```powershell
   cd d:\My Works\Coding\LibaryManagermentApplication\backend
   mvn spring-boot:run
   ```

2. **Bắt đầu với SimpleLoadTest**
   - Test nhanh để đảm bảo hệ thống hoạt động
   - Kiểm tra tất cả endpoints

3. **Sau đó chạy StressTest**
   - Tìm giới hạn của hệ thống
   - Xác định optimal configuration

4. **Monitor server trong khi test**
   - Task Manager: CPU, Memory usage
   - Database connections (nếu có)

## Troubleshooting

### ❌ Connection refused
```
Giải pháp:
- Đảm bảo backend đang chạy trên port 8080
- Kiểm tra BASE_URL trong code
- Test bằng browser: http://localhost:8080/api/books
```

### ❌ Out of memory
```
Giải pháp:
- Giảm TOTAL_REQUESTS
- Giảm CONCURRENT_THREADS
- Tăng heap: set MAVEN_OPTS=-Xmx2g
```

### ❌ Compilation errors
```
Giải pháp:
- Chạy: mvn clean compile
- Kiểm tra Java version: java -version (cần Java 17+)
```
