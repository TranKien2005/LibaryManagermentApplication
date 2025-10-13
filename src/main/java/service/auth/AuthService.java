package service.auth;

import java.util.concurrent.CompletableFuture;
import model.Account;

public interface AuthService {
    // Authenticate credentials; returns Account on success or null on failure
    CompletableFuture<Account> authenticate(String username, String password);

    // Fetch account by id
    CompletableFuture<Account> getAccountById(Integer accountId);

    // Authenticate using account id (used by QR login). Returns Account or null.
    CompletableFuture<Account> authenticateByAccountId(Integer accountId);
}
