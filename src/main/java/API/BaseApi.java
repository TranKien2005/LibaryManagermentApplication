package API;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generic client-side API contract (asynchronous).
 */
public interface BaseApi<T, ID> {
    CompletableFuture<List<T>> getAll();

    CompletableFuture<Void> insert(T t);

    CompletableFuture<Void> update(T t, ID id);

    CompletableFuture<Void> delete(ID id);

    CompletableFuture<T> get(ID id);

    CompletableFuture<ID> getID(T t);

    CompletableFuture<java.util.List<ID>> getAllID();
}