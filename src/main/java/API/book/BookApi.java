package API.book;

import API.BaseApi;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import model.Document;

/**
 * Async API interface for Book operations mirroring BookDao methods.
 */
public interface BookApi extends BaseApi<Document, Integer> {
    CompletableFuture<Void> setBookImage(Integer bookId, String imagePath);
    CompletableFuture<Void> setBookImageByURL(Integer id, String imageUrl);
    CompletableFuture<Void> setDescription(Integer id, String description);
    CompletableFuture<Void> addRating(Integer id, int newRating);
    CompletableFuture<List<Document>> getTopRatedBooks();
    CompletableFuture<List<Document>> getFavorite(Integer accountId);
    CompletableFuture<List<Document>> getTrendingBooks();
    CompletableFuture<List<Document>> getAll(int page, int pageSize);
    CompletableFuture<List<Document>> searchNewArrivals(String searchText, int page, int pageSize);
}
