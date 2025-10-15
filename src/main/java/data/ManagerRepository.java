package data;

import DAO.ManagerDao;
import model.Manager;

public class ManagerRepository extends BaseRepository<Manager> {
    public ManagerRepository() {
        this.dao = ManagerDao.getInstance();
    }
}
