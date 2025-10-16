package API.book;

import API.BaseApi;
import model.Document;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * API interface for Book operations.
 */
public interface BookApi extends BaseApi<Document, Integer> {
    CompletableFuture<Void> setBookImageUrl(int bookId, String imageUrl);
    
    CompletableFuture<Void> setDescription(int id, String description);
    
    CompletableFuture<Void> addRating(int id, int newRating);
    
    CompletableFuture<List<Document>> getTopRatedBooks();
    
    CompletableFuture<List<Document>> getFavorite(int accountId);
    
    CompletableFuture<List<Document>> getTrendingBooks();
    
    CompletableFuture<List<Document>> getAll(int page, int pageSize);
    
    CompletableFuture<List<Document>> search(String query, int page, int pageSize);
    
    CompletableFuture<List<Document>> getNewArrivals(int page, int pageSize);
}