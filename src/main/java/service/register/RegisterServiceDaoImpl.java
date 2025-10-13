package service.register;

import java.util.concurrent.CompletableFuture;

import DAO.AccountDao;
import DAO.UserDao;
import DAO.ManagerDao;
import model.Account;

public class RegisterServiceDaoImpl implements RegisterService {
    private final AccountDao accountDao = AccountDao.getInstance();

    // registerNewAccount implemented above; no duplicate

    @Override
    public CompletableFuture<model.Account> registerNewAccount(String username, String password, String confirmPassword,
            String accountType, String fullName, String email, String phone) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // basic validation
                if (username == null || username.isEmpty() || password == null || password.isEmpty() || confirmPassword == null
                        || confirmPassword.isEmpty() || email == null || email.isEmpty() || phone == null || phone.isEmpty()
                        || accountType == null) {
                    throw new IllegalArgumentException("All fields must be filled out.");
                }
                if (!password.equals(confirmPassword)) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new IllegalArgumentException("Invalid email format.");
                }

                Account existing = accountDao.getByUsername(username);
                if (existing != null) {
                    throw new IllegalArgumentException("Username already exists.");
                }

                Account newAccount = new Account(username, password, accountType);
                accountDao.insert(newAccount);
                Integer id = accountDao.getID(newAccount);

                if ("User".equals(accountType)) {
                    UserDao userDao = UserDao.getInstance();
                    model.User newUser = new model.User(id, fullName, email, phone);
                    userDao.update(newUser, id);
                } else {
                    ManagerDao managerDao = ManagerDao.getInstance();
                    model.Manager newManager = new model.Manager(id, fullName, email, phone);
                    managerDao.update(newManager, id);
                }

                return accountDao.get(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
