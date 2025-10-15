package API.account;

import API.BaseApi;
import model.Account;
import java.util.concurrent.CompletableFuture;

/**
 * API interface for Account operations.
 */
public interface AccountApi extends BaseApi<Account, Integer> {
    CompletableFuture<Account> findByUsername(String username);
    
    CompletableFuture<Boolean> isUsernameExists(String username);
    
    CompletableFuture<Integer> add(Account account);
    
    CompletableFuture<Void> updatePassword(int accountId, String newPassword);
    
    CompletableFuture<Account> register(String username, String password, String accountType);
    
    CompletableFuture<String> login(String username, String password);
}