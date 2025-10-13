package service.auth;

import API.account.AccountApi;
import java.util.concurrent.CompletableFuture;
import model.Account;

public class AuthServiceImpl implements AuthService {
    private final AccountApi api;

    public AuthServiceImpl(AccountApi api) {
        this.api = api;
    }

    @Override
    public CompletableFuture<Account> authenticate(String username, String password) {
        return api.getByUsername(username).thenApply(account -> {
            if (account == null) return null;
            if (account.getPassword() != null && account.getPassword().equals(password)) {
                return account;
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Account> getAccountById(Integer accountId) {
        return api.get(accountId);
    }

    @Override
    public CompletableFuture<Account> authenticateByAccountId(Integer accountId) {
        // For HTTP-backed API, this is the same as fetching the account by id
        return api.get(accountId);
    }
}
