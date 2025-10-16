package service;

import data.ManagerRepository;
import model.Manager;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use ManagerRepository with API implementation.
 * This service handles manager-related operations by delegating to the repository.
 */
public class ManagerService {
    
    private final ManagerRepository managerRepository;
    
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
    
    /**
     * Gets all managers.
     * Calls API endpoints: GET /api/managers
     * 
     * @return CompletableFuture with list of managers
     */
    public CompletableFuture<List<Manager>> getAllManagers() {
        return managerRepository.getAll();
    }
    
    /**
     * Gets a manager by its ID.
     * Calls API endpoint: GET /api/managers/{id}
     * 
     * @param id Manager ID
     * @return CompletableFuture with the manager
     */
    public CompletableFuture<Manager> getManagerById(Integer id) {
        return managerRepository.get(id);
    }
    
    /**
     * Creates a new manager.
     * Calls API endpoint: POST /api/managers
     * 
     * @param manager Manager to create
     * @return CompletableFuture
     */
    public CompletableFuture<Void> createManager(Manager manager) {
        return managerRepository.insert(manager);
    }
    
    /**
     * Updates an existing manager.
     * Calls API endpoint: PUT /api/managers/{id}
     * 
     * @param manager Manager with updated information
     * @param id ID of the manager to update
     * @return CompletableFuture
     */
    public CompletableFuture<Void> updateManager(Manager manager, Integer id) {
        return managerRepository.update(manager, id);
    }
    
    /**
     * Deletes a manager by its ID.
     * Calls API endpoint: DELETE /api/managers/{id}
     * 
     * @param id Manager ID to delete
     * @return CompletableFuture
     */
    public CompletableFuture<Void> deleteManager(Integer id) {
        return managerRepository.delete(id);
    }
}