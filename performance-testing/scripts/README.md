# Custom Performance Test Scripts

## PowerShell Load Test

Script đơn giản để test hiệu suất mà không cần cài đặt công cụ bên ngoài.

### Sử dụng

```powershell
# Test với cấu hình mặc định (100 requests, 10 concurrent)
.\simple-load-test.ps1

# Tùy chỉnh tham số
.\simple-load-test.ps1 -BaseUrl "http://localhost:8080/api" -Requests 200 -Concurrent 20

# Test endpoint cụ thể
.\simple-load-test.ps1 -Requests 500 -Concurrent 50
```

### Ưu điểm
- Không cần cài đặt thêm
- Dễ tùy chỉnh
- Phù hợp cho quick tests

### Nhược điểm
- Không có báo cáo chi tiết như JMeter/K6
- Giới hạn về số concurrent requests
- Không phù hợp cho load testing quy mô lớn

## Gợi ý thêm

Bạn có thể tạo thêm scripts cho:
- Database performance testing
- Stress testing
- Spike testing
- Endurance testing
