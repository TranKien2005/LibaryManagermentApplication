package data;

import java.sql.SQLException;

import DAO.BorrowDao;
import model.Borrow;

public class BorrowRepository extends BaseRepository<Borrow> {
    public BorrowRepository() {
        this.dao = BorrowDao.getInstance();
    }

    public int getID(Borrow borrow) throws SQLException {
        return ((BorrowDao) dao).getID(borrow);
    }
}
