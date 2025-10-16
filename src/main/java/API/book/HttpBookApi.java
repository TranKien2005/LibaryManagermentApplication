package API.book;

import API.BaseHttpApi;
import model.Document;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of BookApi.
 */
public class HttpBookApi extends BaseHttpApi<Document, Integer> implements BookApi {
    
    public HttpBookApi(String baseUrl) {
        super(baseUrl, "/api/books");
    }

    @Override
    protected Class<Document> getEntityClass() {
        return Document.class;
    }

    @Override
    public CompletableFuture<Integer> getID(Document document) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", document, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }

    // custom endpoints
    @Override
    public CompletableFuture<Void> setBookImageUrl(int bookId, String imageUrl) {
        Map<String, String> payload = Map.of("imageUrl", imageUrl);
        return client.putAsync(baseUrl + resourcePath + "/" + bookId + "/image", payload, Object.class).thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Void> setDescription(int id, String description) {
        Map<String, String> payload = Map.of("description", description);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/description", payload, Object.class).thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Void> addRating(int id, int newRating) {
        Map<String, Integer> payload = Map.of("rating", newRating);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/rating", payload, Object.class).thenApply(r -> null);
    }

    @Override
    public CompletableFuture<List<Document>> getTopRatedBooks() {
        return client.getAsync(baseUrl + resourcePath + "/top-rated", new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getFavorite(int accountId) {
        return client.getAsync(baseUrl + resourcePath + "/favorite?accountId=" + accountId, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getTrendingBooks() {
        return client.getAsync(baseUrl + resourcePath + "/trending", new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getAll(int page, int pageSize) {
        return client.getAsync(baseUrl + resourcePath + "?page=" + page + "&size=" + pageSize, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> search(String query, int page, int pageSize) {
        String q = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        return client.getAsync(baseUrl + resourcePath + "/search?query=" + q + "&page=" + page + "&size=" + pageSize, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getNewArrivals(int page, int pageSize) {
        return client.getAsync(baseUrl + resourcePath + "/new-arrivals?page=" + page + "&size=" + pageSize, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }
}