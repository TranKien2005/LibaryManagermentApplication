package com.library.performance;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Simple load test sử dụng Apache HttpClient
 * Không cần Gatling, có thể chạy trực tiếp
 */
public class SimpleLoadTest {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final int TOTAL_REQUESTS = 200;
    private static final int CONCURRENT_THREADS = 20;
    
    // Toggle để test các endpoint khác nhau
    private static final boolean TEST_ALL_ENDPOINTS = true;
    
    // JWT Token (sẽ được lấy từ login)
    private static String ACCESS_TOKEN = null;
    
    // Credentials để login
    private static final String USERNAME = "user1";
    private static final String PASSWORD = "pass123";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     LIBRARY BACKEND PERFORMANCE TEST           ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Total Requests: " + TOTAL_REQUESTS);
        System.out.println("Concurrent Threads: " + CONCURRENT_THREADS);
        System.out.println("Test Mode: " + (TEST_ALL_ENDPOINTS ? "All Endpoints" : "Read-Only Endpoints"));
        System.out.println();
        
        // Login để lấy token
        System.out.println("🔐 Logging in to get access token...");
        if (!login()) {
            System.err.println("❌ Login failed! Cannot proceed with tests.");
            System.err.println("⚠️  Please check:");
            System.err.println("   - Backend is running on port 8080");
            System.err.println("   - Username/Password: " + USERNAME + "/" + PASSWORD);
            return;
        }
        System.out.println("✅ Login successful! Token obtained.\n");

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        List<Future<RequestResult>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Gửi requests
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int requestNum = i;
            Future<RequestResult> future = executor.submit(() -> executeRequest(requestNum));
            futures.add(future);
        }

        // Thu thập kết quả
        List<RequestResult> results = new ArrayList<>();
        for (Future<RequestResult> future : futures) {
            try {
                results.add(future.get());
            } catch (ExecutionException e) {
                System.err.println("Error executing request: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        executor.shutdown();

        // In kết quả
        printResults(results, totalTime);
        printEndpointBreakdown(results);
    }

    private static RequestResult executeRequest(int requestNum) {
        // Các endpoint READ-ONLY (an toàn để test)
        String[] readEndpoints = {
            // Books endpoints
            "/books",
            "/books/search?query=Java",
            "/books/top-rated?limit=10",
            "/books/trending?days=7&limit=10", 
            "/books/favorite?limit=10",
            "/books/new-arrivals?days=30&limit=10",
            
            // Accounts endpoints
            "/accounts",
            
            // Borrow/Return endpoints
            "/borrow-returns",
            
            // Users endpoints
            "/users",
            
            // Borrows endpoints
            "/borrows",
            
            // Returns endpoints
            "/returns"
        };
        
        String endpoint;
        if (TEST_ALL_ENDPOINTS) {
            endpoint = readEndpoints[requestNum % readEndpoints.length];
        } else {
            // Chỉ test books endpoints
            String[] bookEndpoints = {
                "/books",
                "/books/search?query=Programming",
                "/books/top-rated?limit=10",
                "/books/trending?days=7&limit=10"
            };
            endpoint = bookEndpoints[requestNum % bookEndpoints.length];
        }
        
        String url = BASE_URL + endpoint;

        long start = System.nanoTime();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            
            // Thêm Authorization header
            if (ACCESS_TOKEN != null) {
                request.setHeader("Authorization", "Bearer " + ACCESS_TOKEN);
            }
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                long duration = (System.nanoTime() - start) / 1_000_000; // Convert to ms
                int statusCode = response.getCode();
                
                return new RequestResult(
                    true,
                    statusCode,
                    duration,
                    endpoint
                );
            }
        } catch (Exception e) {
            long duration = (System.nanoTime() - start) / 1_000_000;
            return new RequestResult(
                false,
                0,
                duration,
                endpoint,
                e.getMessage()
            );
        }
    }

    private static void printResults(List<RequestResult> results, double totalTime) {
        long successCount = results.stream().filter(r -> r.success).count();
        long failCount = results.size() - successCount;

        List<Long> successDurations = results.stream()
                .filter(r -> r.success)
                .map(r -> r.duration)
                .sorted()
                .toList();

        System.out.println("\n========== RESULTS ==========");
        System.out.println("Total Time: " + String.format("%.2f", totalTime) + "s");
        System.out.println("Successful Requests: " + successCount);
        System.out.println("Failed Requests: " + failCount);
        System.out.println("Success Rate: " + String.format("%.2f", (successCount * 100.0 / results.size())) + "%");
        System.out.println("Requests/Second: " + String.format("%.2f", results.size() / totalTime));

        if (!successDurations.isEmpty()) {
            double avg = successDurations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = successDurations.get(0);
            long max = successDurations.get(successDurations.size() - 1);
            long p50 = successDurations.get(successDurations.size() / 2);
            long p95 = successDurations.get((int) (successDurations.size() * 0.95));
            long p99 = successDurations.get((int) (successDurations.size() * 0.99));

            System.out.println("\nResponse Times (ms):");
            System.out.println("  Average: " + String.format("%.2f", avg));
            System.out.println("  Min: " + min);
            System.out.println("  Max: " + max);
            System.out.println("  P50 (Median): " + p50);
            System.out.println("  P95: " + p95);
            System.out.println("  P99: " + p99);
        }

        // In requests thất bại
        if (failCount > 0) {
            System.out.println("\nFailed Requests:");
            results.stream()
                    .filter(r -> !r.success)
                    .forEach(r -> System.out.println("  " + r.endpoint + " - " + r.errorMessage));
        }

        System.out.println("============================\n");
    }

    private static void printEndpointBreakdown(List<RequestResult> results) {
        System.out.println("\n========== ENDPOINT BREAKDOWN ==========");
        
        // Group by endpoint
        Map<String, List<RequestResult>> byEndpoint = new HashMap<>();
        for (RequestResult result : results) {
            byEndpoint.computeIfAbsent(result.endpoint, k -> new ArrayList<>()).add(result);
        }
        
        // Print stats for each endpoint
        byEndpoint.forEach((endpoint, endpointResults) -> {
            long successCount = endpointResults.stream().filter(r -> r.success).count();
            double successRate = (successCount * 100.0 / endpointResults.size());
            
            List<Long> successDurations = endpointResults.stream()
                    .filter(r -> r.success)
                    .map(r -> r.duration)
                    .sorted()
                    .toList();
            
            if (!successDurations.isEmpty()) {
                double avg = successDurations.stream().mapToLong(Long::longValue).average().orElse(0);
                long min = successDurations.get(0);
                long max = successDurations.get(successDurations.size() - 1);
                
                System.out.println(String.format("\n%-50s", endpoint));
                System.out.println(String.format("  Requests: %d | Success: %d (%.1f%%) | Avg: %.0fms | Min: %dms | Max: %dms",
                    endpointResults.size(), successCount, successRate, avg, min, max));
            } else {
                System.out.println(String.format("\n%-50s", endpoint));
                System.out.println(String.format("  Requests: %d | All Failed ❌", endpointResults.size()));
            }
        });
        
        System.out.println("\n========================================\n");
    }

    private static boolean login() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/login");
            request.setHeader("Content-Type", "application/json");
            
            // Tạo JSON body
            String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                USERNAME, PASSWORD
            );
            request.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    
                    // Parse JSON để lấy accessToken
                    ObjectMapper mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                    ACCESS_TOKEN = (String) data.get("accessToken");
                    
                    return ACCESS_TOKEN != null;
                }
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return false;
    }

    static class RequestResult {
        boolean success;
        int statusCode;
        long duration;
        String endpoint;
        String errorMessage;

        RequestResult(boolean success, int statusCode, long duration, String endpoint) {
            this(success, statusCode, duration, endpoint, null);
        }

        RequestResult(boolean success, int statusCode, long duration, String endpoint, String errorMessage) {
            this.success = success;
            this.statusCode = statusCode;
            this.duration = duration;
            this.endpoint = endpoint;
            this.errorMessage = errorMessage;
        }
    }
}
