package API.account;

import API.BaseHttpApi;
import model.Account;
import model.AuthResponse;
import service.auth.AuthContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of AccountApi.
 */
public class HttpAccountApi extends BaseHttpApi<Account, Integer> implements AccountApi {
    
    public HttpAccountApi(String baseUrl) {
        super(baseUrl, "/api/accounts");
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public CompletableFuture<Account> findByUsername(String username) {
        return client.getAsync(baseUrl + resourcePath + "/by-username/" + username, Account.class);
    }

    @Override
    public CompletableFuture<Boolean> isUsernameExists(String username) {
        return client.getAsync(baseUrl + resourcePath + "/exists/" + username, Boolean.class);
    }

    @Override
    public CompletableFuture<Integer> add(Account account) {
        return client.postAsync(baseUrl + resourcePath, account, Integer.class);
    }

    @Override
    public CompletableFuture<Void> updatePassword(int accountId, String newPassword) {
        Map<String, String> payload = Map.of("newPassword", newPassword);
        return client.putAsync(baseUrl + resourcePath + "/" + accountId + "/password", payload, Object.class)
                .thenApply(r -> { 
                    if (r != null) r.toString(); 
                    return null; 
                });
    }

    @Override
    public CompletableFuture<Account> register(String username, String password, String accountType) {
        Map<String, String> payload = Map.of(
            "username", username,
            "password", password,
            "accountType", accountType
        );
        return client.postAsync(baseUrl + resourcePath + "/register", payload, Account.class);
    }

    @Override
    public CompletableFuture<String> login(String username, String password) {
        Map<String, String> payload = Map.of(
            "username", username,
            "password", password
        );
        // Backend exposes token-based login at /api/auth/login which returns an AuthResponse
        // Call that endpoint, save access/refresh tokens into AuthContext and return accessToken
        return client.postAsync(baseUrl + "/api/auth/login", payload, AuthResponse.class)
                .thenApply(authResp -> {
                    if (authResp != null) {
                        AuthContext.getInstance().setTokens(authResp.getAccessToken(), authResp.getRefreshToken(), authResp.getAccountType());
                        return authResp.getAccessToken();
                    }
                    return null;
                });
    }

    @Override
    public CompletableFuture<Integer> getID(Account account) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", account, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }
}