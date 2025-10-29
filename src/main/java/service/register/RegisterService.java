package service.register;

import data.AccountRepository;
import data.ManagerRepository;
import data.UserRepository;
import model.Account;
import model.Manager;
import model.User;

import java.util.concurrent.CompletableFuture;

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
                if (!password.equals(confirmPassword)) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }

//                if (accountRepository.isUsernameExists(username)) {
//                    throw new IllegalArgumentException("Username already exists.");
//                }

                Account newAccount = new Account(username, password, accountType);
                accountRepository.insert(newAccount).join();
                System.out.println("1");
                System.out.println(newAccount.getAccountType());
                int accountId = accountRepository.getID(newAccount).join();
                System.out.println("2");

                if ("user".equalsIgnoreCase(accountType)) {
                    User newUser = new User(fullName, email, phone, accountId);
                    userRepository.insert(newUser).join();
                    System.out.println("3");
                } else if ("manager".equalsIgnoreCase(accountType)) {
                    Manager newManager = new Manager(fullName, email, phone, accountId);
                    managerRepository.insert(newManager).join();
                } else {
                    throw new IllegalArgumentException("Invalid account type specified.");
                }

                return newAccount;
            } catch (Exception e) {
                throw new RuntimeException("Failed to register new account: " + e.getMessage(), e);
            }
        });
    }
}
