package service.register;

import java.util.concurrent.CompletableFuture;

import API.account.AccountApi;
import API.user.UserApi;
import API.manager.ManagerApi;
// models are referenced with fully-qualified names inside methods when needed

/**
 * HTTP-backed implementation of RegisterService.
 */
public class RegisterServiceImpl implements RegisterService {
    private final AccountApi accountApi;
    private final UserApi userApi;
    private final ManagerApi managerApi;

    public RegisterServiceImpl(AccountApi accountApi, UserApi userApi, ManagerApi managerApi) {
        this.accountApi = accountApi;
        this.userApi = userApi;
        this.managerApi = managerApi;
    }

    // Helper logic is internal to registerNewAccount; do not expose lower-level helpers in the public service API

    @Override
    public CompletableFuture<model.Account> registerNewAccount(String username, String password, String confirmPassword,
            String accountType, String fullName, String email, String phone) {
        // perform validation on client-side before calling APIs
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || confirmPassword == null
                || confirmPassword.isEmpty() || email == null || email.isEmpty() || phone == null || phone.isEmpty()
                || accountType == null) {
            CompletableFuture<model.Account> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalArgumentException("All fields must be filled out."));
            return f;
        }
        if (!password.equals(confirmPassword)) {
            CompletableFuture<model.Account> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalArgumentException("Passwords do not match."));
            return f;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            CompletableFuture<model.Account> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalArgumentException("Invalid email format."));
            return f;
        }

        return accountApi.getByUsername(username).thenCompose(existing -> {
            if (existing != null) {
                CompletableFuture<model.Account> f = new CompletableFuture<>();
                f.completeExceptionally(new IllegalArgumentException("Username already exists."));
                return f;
            }

            model.Account newAccount = new model.Account(username, password, accountType);
            return accountApi.insert(newAccount).thenCompose(new java.util.function.Function<Void, CompletableFuture<Integer>>() {
                @Override
                public CompletableFuture<Integer> apply(Void v) {
                    return accountApi.getID(newAccount);
                }
            }).thenCompose(new java.util.function.Function<Integer, CompletableFuture<model.Account>>() {
                @Override
                public CompletableFuture<model.Account> apply(Integer id) {
                    if ("User".equals(accountType)) {
                        model.User u = new model.User(id, fullName, email, phone);
                        return userApi.insert(u).thenCompose(new java.util.function.Function<Void, CompletableFuture<model.Account>>() {
                            @Override
                            public CompletableFuture<model.Account> apply(Void v) {
                                return accountApi.get(id);
                            }
                        });
                    } else {
                        model.Manager m = new model.Manager(id, fullName, email, phone);
                        return managerApi.insert(m).thenCompose(new java.util.function.Function<Void, CompletableFuture<model.Account>>() {
                            @Override
                            public CompletableFuture<model.Account> apply(Void v) {
                                return accountApi.get(id);
                            }
                        });
                    }
                }
            });
        });
    }
}
