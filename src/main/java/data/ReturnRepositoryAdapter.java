package data;

import API.returnpkg.ReturnApi;
import model.Return;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReturnRepositoryAdapter implements ReturnRepository {
    private final ReturnApi returnApi;
    
    public ReturnRepositoryAdapter(ReturnApi returnApi) {
        this.returnApi = returnApi;
    }

    @Override
    public CompletableFuture<List<Return>> getAll() {
        return returnApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(Return t) {
        return returnApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(Return t, Integer id) {
        return returnApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return returnApi.delete(id);
    }

    @Override
    public CompletableFuture<Return> get(Integer id) {
        return returnApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(Return t) {
        return returnApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return returnApi.getAllID();
    }
}