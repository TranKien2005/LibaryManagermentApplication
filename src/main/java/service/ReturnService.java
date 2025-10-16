package service;

import data.ReturnRepository;
import model.Return;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use ReturnRepository with API implementation.
 * This service handles return-related operations by delegating to the repository.
 */
public class ReturnService {
    
    private final ReturnRepository returnRepository;
    
    public ReturnService(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }
    
    /**
     * Gets all return records.
     * Calls API endpoints: GET /api/returns
     * 
     * @return CompletableFuture with list of return records
     */
    public CompletableFuture<List<Return>> getAllReturns() {
        return returnRepository.getAll();
    }
    
    /**
     * Gets a return record by its ID.
     * Calls API endpoint: GET /api/returns/{id}
     * 
     * @param id Return record ID
     * @return CompletableFuture with the return record
     */
    public CompletableFuture<Return> getReturnById(Integer id) {
        return returnRepository.get(id);
    }
    
    /**
     * Creates a new return record.
     * Calls API endpoint: POST /api/returns
     * 
     * @param ret Return record to create
     * @return CompletableFuture
     */
    public CompletableFuture<Void> createReturn(Return ret) {
        return returnRepository.insert(ret);
    }
    
    /**
     * Updates an existing return record.
     * Calls API endpoint: PUT /api/returns/{id}
     * 
     * @param ret Return record with updated information
     * @param id ID of the return record to update
     * @return CompletableFuture
     */
    public CompletableFuture<Void> updateReturn(Return ret, Integer id) {
        return returnRepository.update(ret, id);
    }
    
    /**
     * Deletes a return record by its ID.
     * Calls API endpoint: DELETE /api/returns/{id}
     * 
     * @param id Return record ID to delete
     * @return CompletableFuture
     */
    public CompletableFuture<Void> deleteReturn(Integer id) {
        return returnRepository.delete(id);
    }
}