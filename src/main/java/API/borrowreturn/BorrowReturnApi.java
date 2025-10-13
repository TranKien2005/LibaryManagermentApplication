package API.borrowreturn;

import API.ReadOnlyApi;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import model.BorrowReturn;

/**
 * Async API interface matching methods in BorrowReturnDAO (read-only projection from view)
 */
public interface BorrowReturnApi extends ReadOnlyApi<BorrowReturn, Integer> {
    CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId);
    CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId);
    // helper that looks up BorrowID for an account/book combination
    CompletableFuture<Integer> getID(Integer accountId, Integer bookId);
}
