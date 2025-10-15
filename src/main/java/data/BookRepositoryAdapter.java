package data;

import API.book.BookApi;
import model.Document;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BookRepositoryAdapter implements BookRepository {
    private final BookApi bookApi;
    
    public BookRepositoryAdapter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public CompletableFuture<List<Document>> getAll() {
        return bookApi.getAll();
    }

    @Override
    public CompletableFuture<Void> insert(Document t) {
        return bookApi.insert(t);
    }

    @Override
    public CompletableFuture<Void> update(Document t, Integer id) {
        return bookApi.update(t, id);
    }

    @Override
    public CompletableFuture<Void> delete(Integer id) {
        return bookApi.delete(id);
    }

    @Override
    public CompletableFuture<Document> get(Integer id) {
        return bookApi.get(id);
    }

    @Override
    public CompletableFuture<Integer> getID(Document t) {
        return bookApi.getID(t);
    }

    @Override
    public CompletableFuture<List<Integer>> getAllID() {
        return bookApi.getAllID();
    }

    @Override
    public CompletableFuture<Void> setBookImageUrl(int bookId, String imageUrl) {
        return bookApi.setBookImageUrl(bookId, imageUrl);
    }

    @Override
    public CompletableFuture<Void> setDescription(int id, String description) {
        return bookApi.setDescription(id, description);
    }

    @Override
    public CompletableFuture<Void> addRating(int id, int newRating) {
        return bookApi.addRating(id, newRating);
    }

    @Override
    public CompletableFuture<List<Document>> getTopRatedBooks() {
        return bookApi.getTopRatedBooks();
    }

    @Override
    public CompletableFuture<List<Document>> getFavorite(int accountId) {
        return bookApi.getFavorite(accountId);
    }

    @Override
    public CompletableFuture<List<Document>> getTrendingBooks() {
        return bookApi.getTrendingBooks();
    }

    @Override
    public CompletableFuture<List<Document>> getAll(int page, int pageSize) {
        return bookApi.getAll(page, pageSize);
    }

    @Override
    public CompletableFuture<List<Document>> search(String query, int page, int pageSize) {
        return bookApi.search(query, page, pageSize);
    }

    @Override
    public CompletableFuture<List<Document>> getNewArrivals(int page, int pageSize) {
        return bookApi.getNewArrivals(page, pageSize);
    }
}