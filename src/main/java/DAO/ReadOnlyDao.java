package DAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Read-only DAO contract for projection/view DAOs.
 */
public interface ReadOnlyDao<T, ID> {
    List<T> getAll() throws SQLException;

    T get(ID id) throws SQLException;

    ID getID(T t) throws SQLException;

    List<ID> getAllID() throws SQLException;
}
