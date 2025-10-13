package service.auth;

import java.util.concurrent.CompletableFuture;
import model.Account;
import DAO.AccountDao;

public class AuthServiceDaoImpl implements AuthService {

    private final AccountDao accountDao = AccountDao.getInstance();

    @Override
    public CompletableFuture<Account> authenticate(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Account acc = accountDao.getByUsername(username);
                if (acc == null) return null;
                if (acc.getPassword() != null && acc.getPassword().equals(password)) return acc;
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Account> getAccountById(Integer accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return accountDao.get(accountId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Account> authenticateByAccountId(Integer accountId) {
        // DAO-backed: same as fetching by id
        return getAccountById(accountId);
    }
}
