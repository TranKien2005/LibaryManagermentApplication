package API;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Partial implementation of BaseApi using ApiClient. Subclasses should provide
 * the resource path and concrete types via protected helpers.
 */
public abstract class BaseHttpApi<T, ID> implements BaseApi<T, ID> {
    protected final ApiClient client;
    protected final String baseUrl;
    protected final String resourcePath; // e.g. "/api/users"

    protected BaseHttpApi(String baseUrl, String resourcePath) {
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
    public CompletableFuture<Void> insert(T t) {
        return client.postAsync(baseUrl + resourcePath, t, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Void> update(T t, ID id) {
        return client.putAsync(baseUrl + resourcePath + "/" + id, t, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Void> delete(ID id) {
        return client.deleteAsync(baseUrl + resourcePath + "/" + id);
    }

    @Override
    public CompletableFuture<T> get(ID id) {
        return client.getAsync(baseUrl + resourcePath + "/" + id, getEntityClass());
    }

    @Override
    public CompletableFuture<ID> getID(T t) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("getID not implemented in BaseHttpApi - override in subclass if required"));
    }

    @Override
    public CompletableFuture<java.util.List<ID>> getAllID() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("getAllID not implemented in BaseHttpApi - override in subclass if required"));
    }
}
