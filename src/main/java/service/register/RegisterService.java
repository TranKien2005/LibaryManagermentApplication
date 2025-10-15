package service.register;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import data.AccountRepository;
import data.ManagerRepository;
import data.UserRepository;
import model.Account;
import model.Manager;
import model.User;

public class RegisterService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    public RegisterService(AccountRepository accountRepository, UserRepository userRepository, ManagerRepository managerRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
    }

    public CompletableFuture<Account> registerNewAccount(String username, String password, String confirmPassword,
            String accountType, String fullName, String email, String phone) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (accountRepository.isUsernameExists(username)) {
                    throw new IllegalArgumentException("Username already exists.");
                }

                Account newAccount = new Account(username, password, accountType);
                int accountId = accountRepository.add(newAccount);
                newAccount.setAccountID(accountId);

                if ("user".equalsIgnoreCase(accountType)) {
                    User newUser = new User(fullName, email, phone, accountId);
                    userRepository.add(newUser);
                } else if ("manager".equalsIgnoreCase(accountType)) {
                    Manager newManager = new Manager(fullName, email, phone, accountId);
                    managerRepository.add(newManager);
                } else {
                    throw new IllegalArgumentException("Invalid account type specified.");
                }

                return newAccount;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}