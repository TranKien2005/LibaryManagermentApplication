package data;

import API.manager.ManagerApi;
import model.Manager;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ManagerRepositoryAdapter implements ManagerRepository {
    private final ManagerApi managerApi;
    
    public ManagerRepositoryAdapter(ManagerApi managerApi) {
        this.managerApi = managerApi;
    }

    @Override
    public CompletableFuture<List<Manager>> getAll() {
        return managerApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(Manager t) {
        return managerApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(Manager t, Integer id) {
        return managerApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return managerApi.delete(id);
    }

    @Override
    public CompletableFuture<Manager> get(Integer id) {
        return managerApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(Manager t) {
        return managerApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return managerApi.getAllID();
    }
}