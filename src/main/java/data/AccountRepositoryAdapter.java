package data;

import API.account.AccountApi;
import model.Account;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AccountRepositoryAdapter implements AccountRepository {
    private final AccountApi accountApi;
    
    public AccountRepositoryAdapter(AccountApi accountApi) {
        this.accountApi = accountApi;
    }

    @Override
    public CompletableFuture<List<Account>> getAll() {
        return accountApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(Account t) {
        return accountApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(Account t, Integer id) {
        return accountApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return accountApi.delete(id);
    }

    @Override
    public CompletableFuture<Account> get(Integer id) {
        return accountApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(Account t) {
        return accountApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return accountApi.getAllID();
    }

    @Override
    public CompletableFuture<Account> findByUsername(String username) {
        return accountApi.findByUsername(username);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return accountApi.isUsernameExists(username).join();
    }

    @Override
    public CompletableFuture<Integer> add(Account account) {
        return accountApi.add(account);
    }

    @Override
    public CompletableFuture<Void> updatePassword(int accountId, String newPassword) {
        return accountApi.updatePassword(accountId, newPassword);
    }
}
