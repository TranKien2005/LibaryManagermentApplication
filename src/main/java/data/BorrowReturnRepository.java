package DAO;

import model.BorrowReturn;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for BorrowReturn operations.
 * Provides non-blocking alternatives to all BorrowReturnDAO methods.
 */
public interface BorrowReturnRepository {
    CompletableFuture<List<BorrowReturn>> getAll();
    
    CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId);
    
    CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId);
    
    CompletableFuture<Integer> getID(Integer accountId, Integer bookId);
    
    CompletableFuture<BorrowReturn> get(Integer id);
    
    CompletableFuture<Integer> getID(BorrowReturn t);
    
    CompletableFuture<List<Integer>> getAllID();
}