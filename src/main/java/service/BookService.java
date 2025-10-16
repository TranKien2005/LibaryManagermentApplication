package service;

import data.BookRepository;
import model.Document;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use BookRepository with API implementation.
 * This service handles book-related operations by delegating to the repository.
 */
public class BookService {
    
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    /**
     * Gets a paginated list of books.
     * Calls API endpoint: GET /api/books?page=&size=
     * 
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @return CompletableFuture with list of books
     */
    public CompletableFuture<List<Document>> getBooks(int page, int size) {
        // This will call the API implementation of BookRepository
        return bookRepository.getAll(page, size);
    }
    
    /**
     * Gets a book by its ID.
     * Calls API endpoint: GET /api/books/{id}
     * 
     * @param id Book ID
     * @return CompletableFuture with the book
     */
    public CompletableFuture<Document> getBookById(Integer id) {
        return bookRepository.get(id);
    }
    
    /**
     * Creates a new book.
     * Calls API endpoint: POST /api/books
     * 
     * @param book Book to create
     * @return CompletableFuture
     */
    public CompletableFuture<Void> createBook(Document book) {
        return bookRepository.insert(book);
    }
    
    /**
     * Updates an existing book.
     * Calls API endpoint: PUT /api/books/{id}
     * 
     * @param book Book with updated information
     * @param id ID of the book to update
     * @return CompletableFuture
     */
    public CompletableFuture<Void> updateBook(Document book, Integer id) {
        return bookRepository.update(book, id);
    }
    
    /**
     * Deletes a book by its ID.
     * Calls API endpoint: DELETE /api/books/{id}
     * 
     * @param id Book ID to delete
     * @return CompletableFuture
     */
    public CompletableFuture<Void> deleteBook(Integer id) {
        return bookRepository.delete(id);
    }
    
    /**
     * Gets top-rated books.
     * Calls API endpoint: GET /api/books/top-rated
     * 
     * @return CompletableFuture with list of top-rated books
     */
    public CompletableFuture<List<Document>> getTopRatedBooks() {
        return ((data.BookRepository) bookRepository).getTopRatedBooks();
    }
    
    /**
     * Gets trending books.
     * Calls API endpoint: GET /api/books/trending
     * 
     * @return CompletableFuture with list of trending books
     */
    public CompletableFuture<List<Document>> getTrendingBooks() {
        return ((data.BookRepository) bookRepository).getTrendingBooks();
    }
    
    /**
     * Searches for books.
     * Calls API endpoint: GET /api/books/search?query=&page=&size=
     * 
     * @param query Search query
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @return CompletableFuture with list of matching books
     */
    public CompletableFuture<List<Document>> searchBooks(String query, int page, int size) {
        return ((data.BookRepository) bookRepository).search(query, page, size);
    }
}