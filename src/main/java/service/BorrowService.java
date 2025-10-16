package service;

import data.BorrowRepository;
import model.Borrow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use BorrowRepository with API implementation.
 * This service handles borrow-related operations by delegating to the repository.
 */
public class BorrowService {
    
    private final BorrowRepository borrowRepository;
    
    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }
    
    /**
     * Gets all borrow records.
     * Calls API endpoint: GET /api/borrows
     * 
     * @return CompletableFuture with list of borrow records
     */
    public CompletableFuture<List<Borrow>> getAllBorrows() {
        return borrowRepository.getAll();
    }
    
    /**
     * Gets a borrow record by its ID.
     * Calls API endpoint: GET /api/borrows/{id}
     * 
     * @param id Borrow record ID
     * @return CompletableFuture with the borrow record
     */
    public CompletableFuture<Borrow> getBorrowById(Integer id) {
        return borrowRepository.get(id);
    }
    
    /**
     * Creates a new borrow record.
     * Calls API endpoint: POST /api/borrows
     * 
     * @param borrow Borrow record to create
     * @return CompletableFuture
     */
    public CompletableFuture<Void> createBorrow(Borrow borrow) {
        return borrowRepository.insert(borrow);
    }
    
    /**
     * Updates an existing borrow record.
     * Calls API endpoint: PUT /api/borrows/{id}
     * 
     * @param borrow Borrow record with updated information
     * @param id ID of the borrow record to update
     * @return CompletableFuture
     */
    public CompletableFuture<Void> updateBorrow(Borrow borrow, Integer id) {
        return borrowRepository.update(borrow, id);
    }
    
    /**
     * Deletes a borrow record by its ID.
     * Calls API endpoint: DELETE /api/borrows/{id}
     * 
     * @param id Borrow record ID to delete
     * @return CompletableFuture
     */
    public CompletableFuture<Void> deleteBorrow(Integer id) {
        return borrowRepository.delete(id);
    }
}