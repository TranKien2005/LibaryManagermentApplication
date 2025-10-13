package API.borrow;

import API.BaseApi;
import model.Borrow;

/**
 * Async API interface matching BorrowDao methods.
 */
public interface BorrowApi extends BaseApi<Borrow, Integer> {
    // BaseApi already declares CRUD/getAllID/getID contracts; add any borrow-specific methods here
}
