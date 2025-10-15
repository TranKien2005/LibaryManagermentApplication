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
        return CompletableFuture.supplyAsync(() -> {
            try {
                Account account = accountRepository.findByUsername(username);
                if (account != null && account.getPassword().equals(password)) {
                    return account;
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Account> authenticateByAccountId(int accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return accountRepository.get(accountId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}