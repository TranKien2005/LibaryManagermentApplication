package service;

import java.sql.SQLException;

import data.AccountRepository;
import data.ManagerRepository;
import data.UserRepository;
import model.Account;
import model.Manager;
import model.User;

public class MyAccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ManagerRepository managerRepository;

    public MyAccountService(UserRepository userRepository, AccountRepository accountRepository, ManagerRepository managerRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.managerRepository = managerRepository;
    }

    public User getUser(int accountId) throws SQLException {
        return userRepository.get(accountId);
    }

    public Manager getManager(int accountId) throws SQLException {
        return managerRepository.get(accountId);
    }

    public Account getAccount(int accountId) throws SQLException {
        return accountRepository.get(accountId);
    }

    public void updateUser(int accountId, String fullname, String phone, String email, String password) throws SQLException {
        User user = userRepository.get(accountId);
        user.setFullName(fullname);
        user.setPhone(phone);
        user.setEmail(email);
        userRepository.update(user, accountId);
        accountRepository.updatePassword(accountId, password);
    }

    public void updateManager(int accountId, String fullname, String phone, String email, String password) throws SQLException {
        Manager manager = managerRepository.get(accountId);
        manager.setFullName(fullname);
        manager.setPhone(phone);
        manager.setEmail(email);
        managerRepository.update(manager, accountId);
        accountRepository.updatePassword(accountId, password);
    }
}
