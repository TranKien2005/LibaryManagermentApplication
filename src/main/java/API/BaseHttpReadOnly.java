package API;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Partial implementation for read-only APIs backed by HTTP.
 */
public abstract class BaseHttpReadOnly<T, ID> implements ReadOnlyApi<T, ID> {
    protected final ApiClient client;
    protected final String baseUrl;
    protected final String resourcePath;

    protected BaseHttpReadOnly(String baseUrl, String resourcePath) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.resourcePath = resourcePath.startsWith("/") ? resourcePath : ("/" + resourcePath);
        this.client = new ApiClient();
    }

    protected abstract Class<T> getEntityClass();

    @Override
    public CompletableFuture<List<T>> getAll() {
        Type listType = TypeToken.getParameterized(List.class, getEntityClass()).getType();
        return client.getAsync(baseUrl + resourcePath, listType);
    }

    @Override
    public CompletableFuture<T> get(ID id) {
        return client.getAsync(baseUrl + resourcePath + "/" + id, getEntityClass());
    }

    @Override
    public CompletableFuture<ID> getID(T t) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("getID not implemented in BaseHttpReadOnly - override if needed"));
    }

    @Override
    public CompletableFuture<java.util.List<ID>> getAllID() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("getAllID not implemented in BaseHttpReadOnly - override if needed"));
    }
}
