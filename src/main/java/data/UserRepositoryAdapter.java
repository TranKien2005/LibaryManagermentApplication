package data;

import API.user.UserApi;
import model.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserRepositoryAdapter implements UserRepository {
    private final UserApi userApi;
    
    public UserRepositoryAdapter(UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public CompletableFuture<List<User>> getAll() {
        return userApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(User t) {
        return userApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(User t, Integer id) {
        return userApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return userApi.delete(id);
    }

    @Override
    public CompletableFuture<User> get(Integer id) {
        return userApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(User t) {
        return userApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return userApi.getAllID();
    }
}