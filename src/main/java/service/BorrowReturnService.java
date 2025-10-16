package service;

import data.BorrowReturnRepository;
import model.BorrowReturn;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use BorrowReturnRepository with API implementation.
 * This service handles borrow-return related operations by delegating to the repository.
 */
public class BorrowReturnService {
    
    private final BorrowReturnRepository borrowReturnRepository;
    
    public BorrowReturnService(BorrowReturnRepository borrowReturnRepository) {
        this.borrowReturnRepository = borrowReturnRepository;
    }
    
    /**
     * Gets all borrow-return records.
     * Calls API endpoint: GET /api/borrow-returns
     * 
     * @return CompletableFuture with list of borrow-return records
     */
    public CompletableFuture<List<BorrowReturn>> getAllBorrowReturns() {
        return borrowReturnRepository.getAll();
    }
    
    /**
     * Gets borrow-return records by account ID.
     * Calls API endpoint: GET /api/borrow-returns/by-account/{accountId}
     * 
     * @param accountId Account ID
     * @return CompletableFuture with list of borrow-return records for the account
     */
    public CompletableFuture<List<BorrowReturn>> getBorrowReturnsByAccount(Integer accountId) {
        return borrowReturnRepository.getByAccountId(accountId);
    }
    
    /**
     * Checks if a book is currently borrowed by an account.
     * Calls API endpoint: GET /api/borrow-returns/is-borrowed?accountId=&bookId=
     * 
     * @param accountId Account ID
     * @param bookId Book ID
     * @return CompletableFuture with boolean indicating if the book is borrowed
     */
    public CompletableFuture<Boolean> isBookBorrowed(Integer accountId, Integer bookId) {
        return borrowReturnRepository.isBorrowed(accountId, bookId);
    }
}