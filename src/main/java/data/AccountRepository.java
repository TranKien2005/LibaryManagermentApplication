package data;

import java.sql.SQLException;

import DAO.AccountDao;
import model.Account;

public class AccountRepository extends BaseRepository<Account> {

    public AccountRepository() {
        this.dao = AccountDao.getInstance();
    }

    public Account findByUsername(String username) throws SQLException {
        return ((AccountDao) dao).findByUsername(username);
    }

    public boolean isUsernameExists(String username) throws SQLException {
        return ((AccountDao) dao).isUsernameExists(username);
    }

    public int add(Account account) throws SQLException {
        return ((AccountDao) dao).add(account);
    }

    public void updatePassword(int accountId, String newPassword) throws SQLException {
        ((AccountDao) dao).updatePassword(accountId, newPassword);
    }
}
