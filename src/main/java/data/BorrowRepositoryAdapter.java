package data;

import API.borrow.BorrowApi;
import model.Borrow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BorrowRepositoryAdapter implements BorrowRepository {
    private final BorrowApi borrowApi;
    
    public BorrowRepositoryAdapter(BorrowApi borrowApi) {
        this.borrowApi = borrowApi;
    }

    @Override
    public CompletableFuture<List<Borrow>> getAll() {
        return borrowApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(Borrow t) {
        return borrowApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(Borrow t, Integer id) {
        return borrowApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return borrowApi.delete(id);
    }

    @Override
    public CompletableFuture<Borrow> get(Integer id) {
        return borrowApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(Borrow t) {
        return borrowApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return borrowApi.getAllID();
    }
}