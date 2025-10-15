package data;

import DAO.ReturnDao;
import model.Return;

public class ReturnRepository extends BaseRepository<Return> {
    public ReturnRepository() {
        this.dao = ReturnDao.getInstance();
    }
}
