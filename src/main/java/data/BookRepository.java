package DAO;

import model.Document;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async repository interface for Book operations.
 * Provides non-blocking alternatives to all BookDao methods.
 */
public interface BookRepository extends BaseRepository<Document, Integer> {
    CompletableFuture<Void> setBookImageUrl(int bookId, String imageUrl);
    
    CompletableFuture<Void> setDescription(int id, String description);
    
    CompletableFuture<Void> addRating(int id, int newRating);
    
    CompletableFuture<List<Document>> getTopRatedBooks();
    
    CompletableFuture<List<Document>> getFavorite(int accountId);
    
    CompletableFuture<List<Document>> getTrendingBooks();
    
    CompletableFuture<List<Document>> getAll(int page, int pageSize);
    
    CompletableFuture<List<Document>> searchNewArrivals(String searchText, int page, int pageSize);
}