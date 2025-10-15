package service;

import data.UserRepository;
import model.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use UserRepository with API implementation.
 * This service handles user-related operations by delegating to the repository.
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Gets all users.
     * Calls API endpoints: GET /api/users
     * 
     * @return CompletableFuture with list of users
     */
    public CompletableFuture<List<User>> getAllUsers() {
        return userRepository.getAll();
    }
    
    /**
     * Gets a user by its ID.
     * Calls API endpoint: GET /api/users/{id}
     * 
     * @param id User ID
     * @return CompletableFuture with the user
     */
    public CompletableFuture<User> getUserById(Integer id) {
        return userRepository.get(id);
    }
    
    /**
     * Creates a new user.
     * Calls API endpoint: POST /api/users
     * 
     * @param user User to create
     * @return CompletableFuture
     */
    public CompletableFuture<Void> createUser(User user) {
        return userRepository.insert(user);
    }
    
    /**
     * Updates an existing user.
     * Calls API endpoint: PUT /api/users/{id}
     * 
     * @param user User with updated information
     * @param id ID of the user to update
     * @return CompletableFuture
     */
    public CompletableFuture<Void> updateUser(User user, Integer id) {
        return userRepository.update(user, id);
    }
    
    /**
     * Deletes a user by its ID.
     * Calls API endpoint: DELETE /api/users/{id}
     * 
     * @param id User ID to delete
     * @return CompletableFuture
     */
    public CompletableFuture<Void> deleteUser(Integer id) {
        return userRepository.delete(id);
    }
}