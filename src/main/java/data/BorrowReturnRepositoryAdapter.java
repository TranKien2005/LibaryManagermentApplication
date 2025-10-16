package data;

import API.borrowreturn.BorrowReturnApi;
import model.BorrowReturn;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BorrowReturnRepositoryAdapter implements BorrowReturnRepository {
    private final BorrowReturnApi borrowReturnApi;
    
    public BorrowReturnRepositoryAdapter(BorrowReturnApi borrowReturnApi) {
        this.borrowReturnApi = borrowReturnApi;
    }

    @Override
    public CompletableFuture<List<BorrowReturn>> getAll() {
        return borrowReturnApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(BorrowReturn t) {
        return borrowReturnApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(BorrowReturn t, Integer id) {
        return borrowReturnApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return borrowReturnApi.delete(id);
    }

    @Override
    public CompletableFuture<BorrowReturn> get(Integer id) {
        return borrowReturnApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(BorrowReturn t) {
        return borrowReturnApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return borrowReturnApi.getAllID();
    }

    @Override
    public CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId) {
        return borrowReturnApi.getByAccountId(accountId);
    }

    @Override
    public CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId) {
        return borrowReturnApi.isBorrowed(accountId, bookId);
    }
    
    @Override
    public CompletableFuture<Integer> getID(Integer accountId, Integer bookId) {
        // Since the API doesn't have this method, we'll need to implement it via the DAO directly
        // This is a limitation of the current API design
        return CompletableFuture.completedFuture(-1); // Placeholder implementation
    }
}