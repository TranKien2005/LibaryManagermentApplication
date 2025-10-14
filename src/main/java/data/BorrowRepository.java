package DAO;

import model.Borrow;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for Borrow operations.
 * Provides non-blocking alternatives to all BorrowDao methods.
 */
public interface BorrowRepository extends BaseRepository<Borrow, Integer> {
    // All methods are inherited from BaseRepository
}