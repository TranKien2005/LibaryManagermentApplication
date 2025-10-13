package service.auth;

import java.util.concurrent.CompletableFuture;
import model.Account;

public class FallbackAuthService implements AuthService {
    private final AuthService primary;
    private final AuthService fallback;

    public FallbackAuthService(AuthService primary, AuthService fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public CompletableFuture<Account> authenticate(String username, String password) {
        return primary.authenticate(username, password).handle((res, ex) -> {
            if (ex != null) {
                return fallback.authenticate(username, password).join();
            }
            if (res == null) {
                return null;
            }
            return res;
        }).thenApply(r -> r);
    }

    @Override
    public CompletableFuture<Account> getAccountById(Integer accountId) {
        return primary.getAccountById(accountId).handle((res, ex) -> {
            if (ex != null) {
                return fallback.getAccountById(accountId).join();
            }
            return res;
        }).thenApply(r -> r);
    }

    @Override
    public CompletableFuture<Account> authenticateByAccountId(Integer accountId) {
        return primary.authenticateByAccountId(accountId).handle((res, ex) -> {
            if (ex != null) {
                return fallback.authenticateByAccountId(accountId).join();
            }
            return res;
        }).thenApply(r -> r);
    }
}
