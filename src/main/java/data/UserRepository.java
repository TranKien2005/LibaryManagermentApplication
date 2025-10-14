package DAO;

import model.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for User operations.
 * Provides non-blocking alternatives to all UserDao methods.
 */
public interface UserRepository extends BaseRepository<User, Integer> {
    // All methods are inherited from BaseRepository
}