package API.account;

import API.BaseHttpApi;
import java.util.concurrent.CompletableFuture;
import model.Account;

public class HttpAccountApi extends BaseHttpApi<Account, Integer> implements AccountApi {
    public HttpAccountApi(String baseUrl) {
        super(baseUrl, "/api/accounts");
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public CompletableFuture<Account> getByUsername(String username) {
        return client.getAsync(baseUrl + resourcePath + "/by-username?username=" + username, Account.class);
    }

    @Override
    public CompletableFuture<Void> updatePassword(Integer id, String newPassword) {
        java.util.Map<String,String> payload = new java.util.HashMap<>();
        payload.put("password", newPassword);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/password", payload, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Integer> getID(Account account) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", account, Integer.class);
    }
}
