package com.library.performance;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.*;

/**
 * Stress Test - Kiểm tra giới hạn của hệ thống
 * Tăng dần số lượng concurrent users để tìm breaking point
 */
public class StressTest {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String TEST_ENDPOINT = "/books"; // Endpoint để stress test
    private static final int REQUESTS_PER_USER = 10; // Mỗi user gửi 10 requests
    private static final int[] USER_LEVELS = {1, 5, 10, 20, 50, 100, 200};
    
    // Test accounts (đã tạo sẵn bằng SetupTestAccounts)
    private static final String USERNAME_PREFIX = "perftest";
    private static final String PASSWORD = "Manager@123";
    private static final int TOTAL_TEST_ACCOUNTS = 200;
    
    // Pool of tokens cho nhiều users
    private static final List<String> TOKEN_POOL = new ArrayList<>();
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     STRESS TEST - Finding System Limits                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        System.out.println("Test Endpoint: " + TEST_ENDPOINT);
        System.out.println("Requests per User: " + REQUESTS_PER_USER);
        System.out.println("Total Available Accounts: " + TOTAL_TEST_ACCOUNTS + " users");
        System.out.println("Max Concurrent Users: 500");
        System.out.println("Strategy: Each user sends " + REQUESTS_PER_USER + " requests concurrently\n");
        
        System.out.println("⚡ Starting stress test...\n");

        List<StressTestResult> results = new ArrayList<>();

        for (int userLevel : USER_LEVELS) {
            // Clear token pool và login đúng số users cần thiết
            TOKEN_POOL.clear();
            
            System.out.println(String.format("🔥 Testing with %d concurrent users...", userLevel));
            System.out.println(String.format("   🔐 Logging in %d test accounts...", userLevel));
            
            int successTokens = loginMultipleAccounts(userLevel);
            
            if (successTokens < userLevel * 0.8) { // Nếu < 80% thành công
                System.err.println(String.format("   ❌ Only %d/%d accounts logged in successfully. Skipping this level.", successTokens, userLevel));
                continue;
            }
            
            System.out.println(String.format("   ✅ %d accounts ready. Starting test...", successTokens));
            StressTestResult result = runStressTest(userLevel);
            results.add(result);
            
            printLevelResult(result);
            
            // Check if system is degrading significantly
            if (result.successRate < 50.0) {
                System.out.println("\n⛔ System failure rate too high. Stopping test.");
                break;
            }
            
            if (result.avgResponseTime > 5000) {
                System.out.println("\n⚠️  Response time exceeds threshold. Stopping test.");
                break;
            }
            
            // Cool down between tests (chờ 10 giây trước khi lên level tiếp theo)
            if (userLevel != USER_LEVELS[USER_LEVELS.length - 1]) { // Không chờ sau level cuối
                System.out.println("   ⏳ Waiting 10 seconds before next level...\n");
                Thread.sleep(10000);
            }
        }

        System.out.println("\n" + "=".repeat(70));
        printSummary(results);
    }

    private static StressTestResult runStressTest(int concurrentUsers) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        List<Future<RequestMetric>> futures = new ArrayList<>();
        
        long testStart = System.currentTimeMillis();
        String url = BASE_URL + TEST_ENDPOINT;
        int totalRequests = concurrentUsers * REQUESTS_PER_USER;

        for (int i = 0; i < totalRequests; i++) {
            Future<RequestMetric> future = executor.submit(() -> {
                long start = System.nanoTime();
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);
                    
                    // Thêm Authorization header với random token từ pool
                    if (!TOKEN_POOL.isEmpty()) {
                        String token = TOKEN_POOL.get(random.nextInt(TOKEN_POOL.size()));
                        request.setHeader("Authorization", "Bearer " + token);
                    }
                    
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        long duration = (System.nanoTime() - start) / 1_000_000;
                        int statusCode = response.getCode();
                        
                        // Đọc response body để kiểm tra success field
                        String responseBody = EntityUtils.toString(response.getEntity());
                        boolean hasSuccess = responseBody.contains("\"success\":true") || responseBody.contains("\"success\": true");
                        
                        // Chỉ coi là success nếu status 200 VÀ có success=true trong body
                        boolean isSuccess = (statusCode == 200) && hasSuccess;
                        
                        return new RequestMetric(isSuccess, statusCode, duration);
                    }
                } catch (Exception e) {
                    long duration = (System.nanoTime() - start) / 1_000_000;
                    return new RequestMetric(false, 0, duration);
                }
            });
            futures.add(future);
        }

        List<RequestMetric> metrics = new ArrayList<>();
        for (Future<RequestMetric> future : futures) {
            try {
                metrics.add(future.get());
            } catch (ExecutionException e) {
                metrics.add(new RequestMetric(false, 0, 0));
            }
        }

        long testEnd = System.currentTimeMillis();
        double totalTime = (testEnd - testStart) / 1000.0;

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);

        return calculateResult(concurrentUsers, metrics, totalTime);
    }

    private static StressTestResult calculateResult(int users, List<RequestMetric> metrics, double totalTime) {
        long successCount = metrics.stream().filter(m -> m.success).count();
        double successRate = (successCount * 100.0 / metrics.size());
        
        List<Long> successTimes = metrics.stream()
                .filter(m -> m.success)
                .map(m -> m.duration)
                .sorted()
                .toList();

        double avg = 0, p50 = 0, p95 = 0, p99 = 0;
        long min = 0, max = 0;
        
        if (!successTimes.isEmpty()) {
            avg = successTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            min = successTimes.get(0);
            max = successTimes.get(successTimes.size() - 1);
            p50 = successTimes.get(successTimes.size() / 2);
            p95 = successTimes.get((int) (successTimes.size() * 0.95));
            p99 = successTimes.size() > 1 ? successTimes.get((int) (successTimes.size() * 0.99)) : max;
        }

        double throughput = metrics.size() / totalTime;

        return new StressTestResult(
            users, metrics.size(), successCount, successRate,
            avg, min, max, p50, p95, p99, throughput, totalTime
        );
    }

    private static void printLevelResult(StressTestResult result) {
        String statusIcon = result.successRate >= 95 ? "✅" : result.successRate >= 80 ? "⚠️" : "❌";
        
        System.out.println(String.format(
            "  %s Users: %3d | Success: %3d/%d (%.1f%%) | Avg: %5.0fms | P95: %5.0fms | Throughput: %.1f req/s",
            statusIcon, result.users, result.successCount, result.totalRequests,
            result.successRate, result.avgResponseTime, result.p95, result.throughput
        ));
    }

    private static void printSummary(List<StressTestResult> results) {
        System.out.println("\n📊 STRESS TEST SUMMARY");
        System.out.println("─".repeat(70));
        
        System.out.println(String.format("%-10s %-15s %-15s %-15s %-15s",
            "Users", "Success Rate", "Avg Time", "P95 Time", "Throughput"));
        System.out.println("─".repeat(70));
        
        for (StressTestResult result : results) {
            System.out.println(String.format("%-10d %-15s %-15s %-15s %-15s",
                result.users,
                String.format("%.1f%%", result.successRate),
                String.format("%.0fms", result.avgResponseTime),
                String.format("%.0fms", result.p95),
                String.format("%.1f req/s", result.throughput)
            ));
        }
        
        // Find optimal point
        StressTestResult optimal = results.stream()
                .filter(r -> r.successRate >= 95)
                .max(Comparator.comparingDouble(r -> r.throughput))
                .orElse(results.get(0));
        
        System.out.println("\n" + "─".repeat(70));
        System.out.println(String.format("🎯 Optimal Configuration: %d concurrent users with %.1f req/s throughput",
            optimal.users, optimal.throughput));
        System.out.println("─".repeat(70));
    }

    /**
     * Login nhiều accounts - Theo batch để tránh quá tải
     * Mỗi batch tối đa 50 accounts, chờ 10 giây giữa các batch
     */
    private static int loginMultipleAccounts(int count) {
        int successCount = 0;
        int batchSize = 50; // Mỗi đợt login tối đa 50 accounts
        int totalBatches = (int) Math.ceil((double) count / batchSize);
        
        System.out.println(String.format("   Login strategy: %d accounts in %d batches (max %d per batch)", 
            count, totalBatches, batchSize));
        
        for (int batch = 0; batch < totalBatches; batch++) {
            int batchStart = batch * batchSize + 1;
            int batchEnd = Math.min((batch + 1) * batchSize, count);
            int currentBatchSize = batchEnd - batchStart + 1;
            
            if (totalBatches > 1) {
                System.out.println(String.format("   📦 Login batch %d/%d: accounts %d to %d (%d accounts)...", 
                    batch + 1, totalBatches, batchStart, batchEnd, currentBatchSize));
            }
            
            ExecutorService executor = Executors.newFixedThreadPool(20);
            List<Future<String>> futures = new ArrayList<>();
            
            // Login concurrent trong batch này
            for (int i = batchStart; i <= batchEnd; i++) {
                final int accountNum = i;
                Future<String> future = executor.submit(() -> loginSingleAccount(accountNum));
                futures.add(future);
            }
            
            // Thu thập tokens của batch
            int batchSuccess = 0;
            for (Future<String> future : futures) {
                try {
                    String token = future.get();
                    if (token != null) {
                        TOKEN_POOL.add(token);
                        batchSuccess++;
                    }
                } catch (Exception e) {
                    // Ignore failed logins
                }
            }
            
            executor.shutdown();
            successCount += batchSuccess;
            
            if (totalBatches > 1) {
                System.out.println(String.format("      ✅ Batch %d complete: %d/%d successful", 
                    batch + 1, batchSuccess, currentBatchSize));
                
                // Chờ 10 giây trước khi login batch tiếp theo (trừ batch cuối)
                if (batch < totalBatches - 1) {
                    System.out.println("      ⏳ Waiting 10 seconds before next login batch...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // Hiển thị tổng kết login
        int failCount = count - successCount;
        if (failCount > 0) {
            System.err.println(String.format("   ⚠️  Login summary: %d succeeded, %d failed (%.1f%% success rate)",
                successCount, failCount, (successCount * 100.0 / count)));
        }
        
        return successCount;
    }
    
    private static String loginSingleAccount(int accountNum) {
        String username = USERNAME_PREFIX + accountNum;
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/login");
            request.setHeader("Content-Type", "application/json");
            
            String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, PASSWORD
            );
            request.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (statusCode == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                    
                    // Kiểm tra success flag
                    Boolean success = (Boolean) responseMap.get("success");
                    if (success != null && success) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                        if (data != null && data.containsKey("accessToken")) {
                            return (String) data.get("accessToken");
                        }
                    }
                    
                    // Login failed, log chi tiết
                    if (accountNum <= 10) {
                        String message = (String) responseMap.getOrDefault("message", "Unknown error");
                        System.err.println(String.format("   ❌ Login failed [%s]: %s", username, message));
                    }
                } else {
                    // Log lỗi đầu tiên để debug
                    if (accountNum <= 10) {
                        System.err.println(String.format("   ❌ Login failed [%s]: Status %d - %s", 
                            username, statusCode, responseBody.length() > 150 ? responseBody.substring(0, 150) : responseBody));
                    }
                }
            }
        } catch (Exception e) {
            // Log exception cho 10 account đầu
            if (accountNum <= 10) {
                System.err.println(String.format("   ❌ Login exception [%s]: %s", username, e.getMessage()));
            }
        }
        return null;
    }

    static class RequestMetric {
        boolean success;
        int statusCode;
        long duration;

        RequestMetric(boolean success, int statusCode, long duration) {
            this.success = success;
            this.statusCode = statusCode;
            this.duration = duration;
        }
    }

    static class StressTestResult {
        int users;
        int totalRequests;
        long successCount;
        double successRate;
        double avgResponseTime;
        long minResponseTime;
        long maxResponseTime;
        double p50;
        double p95;
        double p99;
        double throughput;
        double totalTime;

        StressTestResult(int users, int totalRequests, long successCount, double successRate,
                        double avgResponseTime, long minResponseTime, long maxResponseTime,
                        double p50, double p95, double p99, double throughput, double totalTime) {
            this.users = users;
            this.totalRequests = totalRequests;
            this.successCount = successCount;
            this.successRate = successRate;
            this.avgResponseTime = avgResponseTime;
            this.minResponseTime = minResponseTime;
            this.maxResponseTime = maxResponseTime;
            this.p50 = p50;
            this.p95 = p95;
            this.p99 = p99;
            this.throughput = throughput;
            this.totalTime = totalTime;
        }
    }
}
