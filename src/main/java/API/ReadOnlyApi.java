package API;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Read-only API contract for resources that only support queries.
 */
public interface ReadOnlyApi<T, ID> {
    CompletableFuture<List<T>> getAll();

    CompletableFuture<T> get(ID id);

    CompletableFuture<ID> getID(T t);

    CompletableFuture<java.util.List<ID>> getAllID();
}
