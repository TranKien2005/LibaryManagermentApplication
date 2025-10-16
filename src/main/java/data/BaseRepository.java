package data;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generic interface for asynchronous Repository operations.
 * All methods return CompletableFuture to enable non-blocking database operations.
 */
public interface BaseRepository<T, ID> {
    CompletableFuture<List<T>> getAll();

    CompletableFuture<Void> insert(T t);

    CompletableFuture<Void> update(T t, ID id);

    CompletableFuture<Void> delete(ID id);

    CompletableFuture<T> get(ID id);

    CompletableFuture<ID> getID(T t);

    CompletableFuture<List<ID>> getAllID();
}