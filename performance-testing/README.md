# Performance Testing

Thư mục này chứa công cụ test hiệu suất backend API - **ĐƠN GIẢN & HIỆU QUẢ**.

## 🎯 Chỉ 2 công cụ chính:

### 1️⃣ **SimpleLoadTest** ⚡ (Test nhanh)
- Test tất cả endpoints với load vừa phải
- 200 requests, 20 concurrent threads
- Hiển thị breakdown theo endpoint
- **Thời gian:** 2-3 phút

### 2️⃣ **StressTest** 🔥 (Tìm giới hạn)
- Tăng dần concurrent users
- Tìm breaking point của hệ thống
- Tự động tìm optimal configuration
- **Thời gian:** 5-10 phút

### 3️⃣ **PowerShell Script** (Backup option)
- Không cần Maven/Java
- Quick test đơn giản
- Windows-friendly

## 📁 Cấu trúc

```
performance-testing/
├── java-tests/          ⭐ CHÍNH - 2 Java tests
│   ├── SimpleLoadTest.java
│   ├── StressTest.java
│   └── README.md
├── scripts/             PowerShell backup
└── results/             Lưu kết quả
```

## 🚀 Quick Start

```powershell
# 1. Khởi động backend
cd backend
mvn spring-boot:run

# 2. Tạo 500 test accounts (CHỈ CHẠY 1 LẦN ĐẦU TIÊN)
cd performance-testing/java-tests
mvn test-compile exec:java "-Dexec.mainClass=com.library.performance.SetupTestAccounts"

# 3. Chạy tests
# Test nhanh
mvn test-compile exec:java "-Dexec.mainClass=com.library.performance.SimpleLoadTest"

# Stress test (dùng 500 accounts)
mvn test-compile exec:java "-Dexec.mainClass=com.library.performance.StressTest"
```


## Hướng dẫn sử dụng

Xem hướng dẫn chi tiết trong từng thư mục con.
