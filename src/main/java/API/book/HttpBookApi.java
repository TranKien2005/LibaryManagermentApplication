package API.book;

import API.BaseHttpApi;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import model.Document;

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

    // custom endpoints kept as-is
    @Override
    public CompletableFuture<Void> setBookImage(Integer bookId, String imagePath) {
        java.util.Map<String,String> payload = java.util.Map.of("imagePath", imagePath);
        return client.putAsync(baseUrl + resourcePath + "/" + bookId + "/image", payload, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Void> setBookImageByURL(Integer id, String imageUrl) {
        java.util.Map<String,String> payload = java.util.Map.of("imageUrl", imageUrl);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/image-by-url", payload, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Void> setDescription(Integer id, String description) {
        java.util.Map<String,String> payload = java.util.Map.of("description", description);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/description", payload, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<Void> addRating(Integer id, int newRating) {
        java.util.Map<String,Integer> payload = java.util.Map.of("rating", newRating);
        return client.putAsync(baseUrl + resourcePath + "/" + id + "/rating", payload, Object.class).thenApply(r -> { r.toString(); return null; });
    }

    @Override
    public CompletableFuture<List<Document>> getTopRatedBooks() {
        return client.getAsync(baseUrl + resourcePath + "/top-rated", new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getFavorite(Integer accountId) {
        return client.getAsync(baseUrl + resourcePath + "/favorite?accountId=" + accountId, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getTrendingBooks() {
        return client.getAsync(baseUrl + resourcePath + "/trending", new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> getAll(int page, int pageSize) {
        return client.getAsync(baseUrl + resourcePath + "?page=" + page + "&pageSize=" + pageSize, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }

    @Override
    public CompletableFuture<List<Document>> searchNewArrivals(String searchText, int page, int pageSize) {
        String q = java.net.URLEncoder.encode(searchText, java.nio.charset.StandardCharsets.UTF_8);
        return client.getAsync(baseUrl + resourcePath + "/search-new?query=" + q + "&page=" + page + "&pageSize=" + pageSize, new com.google.gson.reflect.TypeToken<List<Document>>(){}.getType());
    }
}
