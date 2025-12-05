package com.library.performance;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Setup Test Accounts - Tạo 500 tài khoản MANAGER test cho stress testing
 * Sử dụng account manager init sẵn để authenticate và tạo accounts qua /accounts/register
 */
public class SetupTestAccounts {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final int TOTAL_ACCOUNTS = 200;
    private static final int ACCOUNTS_PER_BATCH = 20; // Mỗi đợt tạo 20 accounts
    private static final int BATCH_DELAY_SECONDS = 10; // Chờ 10 giây giữa các đợt
    private static final int CONCURRENT_THREADS = 20;
    
    // Account manager init sẵn để authenticate
    private static final String ADMIN_USERNAME = "user1";
    private static final String ADMIN_PASSWORD = "pass123";
    
    // Pattern cho test accounts
    private static final String USERNAME_PREFIX = "perftest";
    private static final String PASSWORD = "Manager@123";
    
    private static String authToken = null;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   SETUP TEST ACCOUNTS - Creating 200 Manager Accounts   ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Admin Account: " + ADMIN_USERNAME + " (for authentication)");
        System.out.println("Total Accounts: " + TOTAL_ACCOUNTS);
        System.out.println("Account Type: Manager (FULL ACCESS)");
        System.out.println("Username Pattern: " + USERNAME_PREFIX + "1 to " + USERNAME_PREFIX + TOTAL_ACCOUNTS);
        System.out.println("Password (all): " + PASSWORD);
        System.out.println();
        
        // Login để lấy token
        System.out.println("🔐 Logging in as admin...");
        authToken = loginAsAdmin();
        if (authToken == null) {
            System.err.println("❌ Failed to authenticate. Exiting.");
            return;
        }
        System.out.println("✅ Authentication successful!\n");
        
        System.out.println("🚀 Starting account creation...");
        System.out.println("Strategy: " + ACCOUNTS_PER_BATCH + " accounts per batch, " + BATCH_DELAY_SECONDS + " seconds delay between batches");
        System.out.println("⚠️  Note: Using /accounts/register endpoint (requires auth)\n");

        List<AccountResult> allResults = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        int totalBatches = (int) Math.ceil((double) TOTAL_ACCOUNTS / ACCOUNTS_PER_BATCH);

        // Tạo accounts theo từng batch
        for (int batch = 0; batch < totalBatches; batch++) {
            int batchStart = batch * ACCOUNTS_PER_BATCH + 1;
            int batchEnd = Math.min((batch + 1) * ACCOUNTS_PER_BATCH, TOTAL_ACCOUNTS);
            int batchSize = batchEnd - batchStart + 1;
            
            System.out.println(String.format("\n📦 Batch %d/%d: Creating accounts %d to %d (%d accounts)...", 
                batch + 1, totalBatches, batchStart, batchEnd, batchSize));
            
            ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
            List<Future<AccountResult>> futures = new ArrayList<>();
            
            // Tạo accounts trong batch này
            for (int i = batchStart; i <= batchEnd; i++) {
                final int accountNum = i;
                Future<AccountResult> future = executor.submit(() -> createAccount(accountNum));
                futures.add(future);
            }
            
            // Thu thập kết quả của batch
            List<AccountResult> batchResults = new ArrayList<>();
            for (Future<AccountResult> future : futures) {
                try {
                    batchResults.add(future.get());
                } catch (ExecutionException e) {
                    System.err.println("Error creating account: " + e.getMessage());
                }
            }
            
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            
            allResults.addAll(batchResults);
            
            // In kết quả batch
            long successCount = batchResults.stream().filter(r -> r.success).count();
            System.out.println(String.format("   ✅ Batch %d complete: %d/%d successful", 
                batch + 1, successCount, batchSize));
            
            // Chờ giữa các batch (trừ batch cuối)
            if (batch < totalBatches - 1) {
                System.out.println(String.format("   ⏳ Waiting %d seconds before next batch...", BATCH_DELAY_SECONDS));
                Thread.sleep(BATCH_DELAY_SECONDS * 1000);
            }
        }

        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        // In kết quả
        printResults(allResults, totalTime);
        // In kết quả
        printResults(allResults, totalTime);
        
        // Lưu thông tin accounts vào file
        saveAccountsToFile(allResults);
    }
    
    private static String loginAsAdmin() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/login");
            request.setHeader("Content-Type", "application/json");
            
            String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                ADMIN_USERNAME, ADMIN_PASSWORD
            );
            request.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                    
                    Boolean success = (Boolean) responseMap.get("success");
                    if (success != null && success) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                        if (data != null && data.containsKey("accessToken")) {
                            return (String) data.get("accessToken");
                        }
                    }
                }
                System.err.println("Login failed: " + response.getCode());
            }
        } catch (Exception e) {
            System.err.println("Login exception: " + e.getMessage());
        }
        return null;
    }

    private static AccountResult createAccount(int accountNum) {
        String username = USERNAME_PREFIX + accountNum;
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Sử dụng endpoint /accounts/register (cần authentication)
            HttpPost request = new HttpPost(BASE_URL + "/accounts/register");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + authToken);
            
            // Tạo JSON body - Tạo Manager account (có tất cả quyền)
            String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"accountType\":\"Manager\"}",
                username, PASSWORD
            );
            request.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                
                // Đọc response body
                String responseBody = "";
                try {
                    responseBody = EntityUtils.toString(response.getEntity());
                } catch (Exception e) {
                    // Ignore
                }
                
                // Check cả status code VÀ success field trong response
                boolean success = false;
                String errorMessage = null;
                
                if (statusCode == 200 || statusCode == 201) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                        Boolean successField = (Boolean) responseMap.get("success");
                        
                        if (successField != null && successField) {
                            success = true;
                        } else {
                            // Lấy error message từ response
                            errorMessage = (String) responseMap.getOrDefault("message", "Unknown error");
                        }
                    } catch (Exception e) {
                        errorMessage = "JSON parse error: " + e.getMessage();
                    }
                } else {
                    errorMessage = "HTTP " + statusCode;
                }
                
                // Log MỌI lỗi (không chỉ 3 account đầu)
                if (!success) {
                    System.err.println(String.format("❌ [%s] Failed: %s - %s", 
                        username, 
                        errorMessage,
                        responseBody.length() > 100 ? responseBody.substring(0, 100) + "..." : responseBody
                    ));
                }
                
                return new AccountResult(
                    username,
                    PASSWORD,
                    success,
                    statusCode,
                    errorMessage
                );
            }
        } catch (Exception e) {
            // Log exception
            System.err.println(String.format("❌ [%s] Exception: %s", username, e.getMessage()));
            
            return new AccountResult(
                username,
                PASSWORD,
                false,
                0,
                "Exception: " + e.getMessage()
            );
        }
    }

    private static void printResults(List<AccountResult> results, double totalTime) {
        long successCount = results.stream().filter(r -> r.success).count();
        long failCount = results.size() - successCount;

        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 ACCOUNT CREATION RESULTS");
        System.out.println("=".repeat(70));
        System.out.println("Total Time: " + String.format("%.2f", totalTime) + "s");
        System.out.println("Successful: " + successCount + "/" + TOTAL_ACCOUNTS);
        System.out.println("Failed: " + failCount);
        System.out.println("Success Rate: " + String.format("%.2f", (successCount * 100.0 / results.size())) + "%");
        System.out.println("Accounts/Second: " + String.format("%.2f", results.size() / totalTime));

        if (failCount > 0) {
            System.out.println("\n❌ Failed Accounts:");
            results.stream()
                    .filter(r -> !r.success)
                    .limit(10) // Chỉ hiển thị 10 lỗi đầu
                    .forEach(r -> System.out.println("  " + r.username + " - Status: " + r.statusCode + 
                                                     (r.errorMessage != null ? " - " + r.errorMessage : "")));
            
            if (failCount > 10) {
                System.out.println("  ... and " + (failCount - 10) + " more");
            }
        }
        
        System.out.println("=".repeat(70));
    }

    private static void saveAccountsToFile(List<AccountResult> results) {
        try {
            String filename = "test-accounts.txt";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            
            writer.println("# Test Accounts for Performance Testing");
            writer.println("# Created: " + java.time.LocalDateTime.now());
            writer.println("# Total: " + results.stream().filter(r -> r.success).count() + " successful accounts");
            writer.println("#");
            writer.println("# Format: username,password");
            writer.println();
            
            for (AccountResult result : results) {
                if (result.success) {
                    writer.println(result.username + "," + result.password);
                }
            }
            
            writer.close();
            System.out.println("\n✅ Account list saved to: " + filename);
            System.out.println("📝 You can use these accounts for stress testing!\n");
            
        } catch (Exception e) {
            System.err.println("Failed to save accounts to file: " + e.getMessage());
        }
    }

    static class AccountResult {
        String username;
        String password;
        boolean success;
        int statusCode;
        String errorMessage;

        AccountResult(String username, String password, boolean success, int statusCode, String errorMessage) {
            this.username = username;
            this.password = password;
            this.success = success;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
        }
    }
}
