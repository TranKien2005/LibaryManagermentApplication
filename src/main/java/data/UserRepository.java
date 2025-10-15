package data;

import DAO.UserDao;
import model.User;

public class UserRepository extends BaseRepository<User> {
    public UserRepository() {
        this.dao = UserDao.getInstance();
    }
}
