package data;

import java.util.concurrent.CompletableFuture;
import model.Account;

public interface AccountRepository extends BaseRepository<Account, Integer> {
    CompletableFuture<Account> findByUsername(String username);
    
    CompletableFuture<Boolean> isUsernameExists(String username);
    
    CompletableFuture<Integer> add(Account account);
    
    CompletableFuture<Void> updatePassword(int accountId, String newPassword);
}
