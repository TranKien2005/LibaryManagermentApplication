package service.add;

import data.BookRepository;
import model.Document;
import googleAPI.GoogleApiBookController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AddBookService {

    private final BookRepository bookRepository;

    public AddBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public CompletableFuture<Document> addDocument(String title, String author, String category, String publisher, int year, int quantity) {
        Document newDocument = new Document(title, author, category, publisher, year, quantity);
        return bookRepository.insert(newDocument);
    }

    public CompletableFuture<Document> addBookByIsbn(String isbn) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document document = GoogleApiBookController.getBookInfoByISBN(isbn);
                if (document == null) {
                    throw new RuntimeException("Book not found for ISBN: " + isbn);
                }
                // Using join() here because we are already in a background thread supplied by supplyAsync
                return bookRepository.insert(document).join();
            } catch (Exception e) {
                throw new RuntimeException("Failed to add book by ISBN: " + e.getMessage(), e);
            }
        });
    }

    public static class BatchAddResult {
        public final int successCount;
        public final int failureCount;

        public BatchAddResult(int successCount, int failureCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
        }
    }

    public CompletableFuture<BatchAddResult> addBooksFromFile(File file, Consumer<Double> progressUpdater) {
        return CompletableFuture.supplyAsync(() -> {
            int successCount = 0;
            int failureCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                long totalLines = reader.lines().count();
                if (totalLines == 0) {
                    return new BatchAddResult(0, 0);
                }
                // Re-open the reader
                try (BufferedReader reader2 = new BufferedReader(new FileReader(file))) {
                    String isbn;
                    int currentLine = 0;
                    while ((isbn = reader2.readLine()) != null) {
                        if (!isbn.trim().isEmpty()) {
                            try {
                                Document document = GoogleApiBookController.getBookInfoByISBN(isbn.trim());
                                if (document != null) {
                                    bookRepository.insert(document).join();
                                    successCount++;
                                } else {
                                    failureCount++;
                                }
                            } catch (Exception e) {
                                failureCount++;
                                e.printStackTrace();
                            }
                        }
                        currentLine++;
                        final double progress = (double) currentLine / totalLines;
                        if (progressUpdater != null) {
                            progressUpdater.accept(progress);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
            }
            return new BatchAddResult(successCount, failureCount);
        });
    }
}
