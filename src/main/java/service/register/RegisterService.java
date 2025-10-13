package service.register;

import java.util.concurrent.CompletableFuture;

/**
 * Service contract for registration-related operations. Mirrors DAO method signatures so
 * controllers can switch between HTTP-backed and DAO-backed implementations.
 */
public interface RegisterService {
    // High-level registration flow exposed to UI: validate input, create account and profile, and return created Account
    CompletableFuture<model.Account> registerNewAccount(String username, String password, String confirmPassword, String accountType,
            String fullName, String email, String phone);
}
