package DAO;

import model.Manager;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for Manager operations.
 * Provides non-blocking alternatives to all ManagerDao methods.
 */
public interface ManagerRepository extends BaseRepository<Manager, Integer> {
    // All methods are inherited from BaseRepository
}