package DAO;

import model.Account;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for Account operations.
 * Provides non-blocking alternatives to all AccountDao methods.
 */
public interface AccountRepository extends BaseRepository<Account, Integer> {
    CompletableFuture<Account> getByUsername(String username);
    
    CompletableFuture<Void> updatePassword(int id, String newPassword);
}