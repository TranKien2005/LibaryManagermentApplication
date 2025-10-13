package API.account;

import API.BaseApi;
import java.util.concurrent.CompletableFuture;
import model.Account;

public interface AccountApi extends BaseApi<Account, Integer> {
    CompletableFuture<Account> getByUsername(String username);
    CompletableFuture<Void> updatePassword(Integer id, String newPassword);
}
