public interface DaoInterface<T, ID> {
    T get(ID id);
    List<T> getAll();
    void insert(T entity);
    void update(T entity);
    void delete(ID id);
}
