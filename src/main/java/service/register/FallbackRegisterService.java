package service.register;

import java.util.concurrent.CompletableFuture;

/**
 * Composite that tries the primary register service and falls back to a secondary on error.
 * Only exposes the high-level registration API to keep the service surface minimal.
 */
public class FallbackRegisterService implements RegisterService {
    private final RegisterService primary;
    private final RegisterService fallback;

    public FallbackRegisterService(RegisterService primary, RegisterService fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public CompletableFuture<model.Account> registerNewAccount(String username, String password, String confirmPassword,
            String accountType, String fullName, String email, String phone) {
        return primary.registerNewAccount(username, password, confirmPassword, accountType, fullName, email, phone)
                .handle((res, ex) -> {
                    if (ex == null) return CompletableFuture.completedFuture(res);
                    return fallback.registerNewAccount(username, password, confirmPassword, accountType, fullName, email, phone);
                }).thenCompose(f -> f);
    }
}
