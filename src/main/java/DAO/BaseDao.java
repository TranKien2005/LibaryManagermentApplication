package DAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic interface for DAO (server-side) CRUD contract.
 * Implementations remain free to throw SQLExceptions and operate synchronously.
 */
public interface BaseDao<T, ID> {
    List<T> getAll() throws SQLException;

    void insert(T t) throws SQLException;

    void update(T t, ID id) throws SQLException;

    void delete(ID id) throws SQLException;

    T get(ID id) throws SQLException;

    ID getID(T t) throws SQLException;

    List<ID> getAllID() throws SQLException;
}
