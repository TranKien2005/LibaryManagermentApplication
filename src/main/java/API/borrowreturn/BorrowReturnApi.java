package API.borrowreturn;

import API.BaseApi;
import model.BorrowReturn;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * API interface for BorrowReturn operations.
 */
public interface BorrowReturnApi extends BaseApi<BorrowReturn, Integer> {
    CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId);
    
    CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId);
    
    CompletableFuture<Integer> getID(Integer accountId, Integer bookId);
}