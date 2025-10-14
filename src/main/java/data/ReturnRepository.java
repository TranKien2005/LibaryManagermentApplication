package DAO;

import model.Return;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for Return operations.
 * Provides non-blocking alternatives to all ReturnDao methods.
 */
public interface ReturnRepository extends BaseRepository<Return, Integer> {
    // All methods are inherited from BaseRepository
}