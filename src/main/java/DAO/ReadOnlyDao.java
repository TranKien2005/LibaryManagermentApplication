package DAO;

import java.util.List;

/**
 * Read-only DAO contract for projection/view DAOs.
 */
public interface ReadOnlyDao<T, ID> {
    List<T> getAll() throws Exception;

    T get(ID id) throws Exception;

    ID getID(T t) throws Exception;

    List<ID> getAllID() throws Exception;
}
