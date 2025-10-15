package service;

import java.sql.SQLException;
import java.util.List;

import data.AccountRepository;
import data.UserRepository;
import model.Account;
import model.User;

public class MemberManagementService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public MemberManagementService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public List<User> getUsers() throws SQLException {
        return userRepository.getAll();
    }

    public Account getAccount(int accountId) throws SQLException {
        return accountRepository.get(accountId);
    }

    public void updateUser(User user, String newPassword) throws SQLException {
        userRepository.update(user, user.getAccountID());
        accountRepository.updatePassword(user.getAccountID(), newPassword);
    }

    public void deleteUser(User user) throws SQLException {
        userRepository.delete(user.getAccountID());
    }
}
