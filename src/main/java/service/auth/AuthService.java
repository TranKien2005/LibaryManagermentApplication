package service.auth;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import data.AccountRepository;
import model.Account;

public class AuthService {

    private final AccountRepository accountRepository;

    public AuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public CompletableFuture<Account> authenticate(String username, String password) {
        // New flow: perform API login which will set tokens in AuthContext via HttpAccountApi,
        // then fetch account details using protected endpoint (token will be attached automatically).
        return accountRepository.login(username, password)
                .thenCompose(token -> accountRepository.findByUsername(username))
                .exceptionally(ex -> {
                    // unwrap ApiException or other causes
                    throw new RuntimeException(ex.getCause() != null ? ex.getCause() : ex);
                });
    }

    public CompletableFuture<Account> authenticateByAccountId(int accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return accountRepository.get(accountId).join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
