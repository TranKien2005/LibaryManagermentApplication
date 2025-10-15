package data;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Factory for creating mock repository instances for testing purposes.
 * This factory provides in-memory mock implementations of the repositories,
 * allowing for testing without actual database or network interactions.
 */
public class MockRepositoryFactory {

    /**
     * Creates a mock AccountRepository.
     * @return A mock implementation of AccountRepository.
     */
    public AccountRepository createAccountRepository() {
        return new AccountRepository() {
            private final Map<Integer, Account> accounts = new HashMap<>();
            private int nextId = 1;

            @Override
            public CompletableFuture<List<Account>> getAll() {
                return CompletableFuture.completedFuture(new ArrayList<>(accounts.values()));
            }

            @Override
            public CompletableFuture<Void> insert(Account account) {
                // Assumes account has an ID
                accounts.put(account.getAccountID(), account);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> update(Account account, Integer id) {
                accounts.put(id, account);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> delete(Integer id) {
                accounts.remove(id);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Account> get(Integer id) {
                return CompletableFuture.completedFuture(accounts.get(id));
            }

            @Override
            public CompletableFuture<Integer> getID(Account account) {
                return CompletableFuture.completedFuture(account.getAccountID());
            }

            @Override
            public CompletableFuture<List<Integer>> getAllID() {
                return CompletableFuture.completedFuture(new ArrayList<>(accounts.keySet()));
            }

            @Override
            public CompletableFuture<Account> findByUsername(String username) {
                return CompletableFuture.completedFuture(accounts.values().stream()
                        .filter(a -> a.getUsername().equals(username))
                        .findFirst()
                        .orElse(null));
            }

            @Override
            public boolean isUsernameExists(String username) {
                return accounts.values().stream().anyMatch(a -> a.getUsername().equals(username));
            }

            @Override
            public CompletableFuture<Integer> add(Account account) {
                int id = nextId++;
                // The Account model has a setter for the ID, so we can use it.
                account.setAccountID(id);
                accounts.put(id, account);
                return CompletableFuture.completedFuture(id);
            }

            @Override
            public CompletableFuture<Void> updatePassword(int accountId, String newPassword) {
                Account account = accounts.get(accountId);
                if (account != null) {
                    account.setPassword(newPassword);
                }
                return CompletableFuture.completedFuture(null);
            }
        };
    }

    /**
     * Creates a mock BookRepository.
     * @return A mock implementation of BookRepository.
     */
    public BookRepository createBookRepository() {
        return new BookRepository() {
            private final Map<Integer, Document> documents = new HashMap<>();

            @Override
            public CompletableFuture<List<Document>> getAll() {
                return CompletableFuture.completedFuture(new ArrayList<>(documents.values()));
            }

            @Override
            public CompletableFuture<Void> insert(Document document) {
                // Assumes document has an ID, as there is no public setter for it.
                documents.put(document.getBookID(), document);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> update(Document document, Integer id) {
                documents.put(id, document);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> delete(Integer id) {
                documents.remove(id);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Document> get(Integer id) {
                return CompletableFuture.completedFuture(documents.get(id));
            }

            @Override
            public CompletableFuture<Integer> getID(Document document) {
                return CompletableFuture.completedFuture(document.getBookID());
            }

            @Override
            public CompletableFuture<List<Integer>> getAllID() {
                return CompletableFuture.completedFuture(new ArrayList<>(documents.keySet()));
            }

            @Override
            public CompletableFuture<Void> setBookImageUrl(int bookId, String imageUrl) {
                Document document = documents.get(bookId);
                if (document != null) {
                    document.setCoverImageUrl(imageUrl);
                }
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> setDescription(int id, String description) {
                Document document = documents.get(id);
                if (document != null) {
                    document.setDescription(description);
                }
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> addRating(int id, int newRating) {
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<List<Document>> getTopRatedBooks() {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }

            @Override
            public CompletableFuture<List<Document>> getFavorite(int accountId) {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }

            @Override
            public CompletableFuture<List<Document>> getTrendingBooks() {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }

            @Override
            public CompletableFuture<List<Document>> getAll(int page, int pageSize) {
                return CompletableFuture.completedFuture(documents.values().stream()
                        .skip((long) (page - 1) * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList()));
            }

            @Override
            public CompletableFuture<List<Document>> search(String query, int page, int pageSize) {
                return CompletableFuture.completedFuture(documents.values().stream()
                        .filter(d -> d.getTitle().toLowerCase().contains(query.toLowerCase()))
                        .skip((long) (page - 1) * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList()));
            }

            @Override
            public CompletableFuture<List<Document>> getNewArrivals(int page, int pageSize) {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }
        };
    }

    public BorrowReturnRepository createBorrowReturnRepository() {
        return new BorrowReturnRepository() {
            private final Map<Integer, BorrowReturn> borrowReturns = new HashMap<>();

            @Override
            public CompletableFuture<List<BorrowReturn>> getAll() {
                return CompletableFuture.completedFuture(new ArrayList<>(borrowReturns.values()));
            }

            @Override
            public CompletableFuture<Void> insert(BorrowReturn borrowReturn) {
                // Assumes borrowReturn has an ID, as it is final.
                borrowReturns.put(borrowReturn.getBorrowID(), borrowReturn);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> update(BorrowReturn borrowReturn, Integer id) {
                borrowReturns.put(id, borrowReturn);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> delete(Integer id) {
                borrowReturns.remove(id);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<BorrowReturn> get(Integer id) {
                return CompletableFuture.completedFuture(borrowReturns.get(id));
            }

            @Override
            public CompletableFuture<Integer> getID(BorrowReturn borrowReturn) {
                return CompletableFuture.completedFuture(borrowReturn.getBorrowID());
            }

            @Override
            public CompletableFuture<List<Integer>> getAllID() {
                return CompletableFuture.completedFuture(new ArrayList<>(borrowReturns.keySet()));
            }

            @Override
            public CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId) {
                return CompletableFuture.completedFuture(borrowReturns.values().stream()
                        // Assumes getMember() returns a string representation of the member ID
                        .filter(br -> Objects.equals(Integer.parseInt(br.getMember()), accountId))
                        .collect(Collectors.toList()));
            }

            @Override
            public CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId) {
                return CompletableFuture.completedFuture(borrowReturns.values().stream()
                        .anyMatch(br -> Objects.equals(Integer.parseInt(br.getMember()), accountId)
                                // Assumes getBook() returns a string representation of the book ID
                                && Objects.equals(Integer.parseInt(br.getBook()), bookId)
                                && br.getReturnDate() == null));
            }

            @Override
            public CompletableFuture<Integer> getID(Integer accountId, Integer bookId) {
                return CompletableFuture.completedFuture(borrowReturns.values().stream()
                        .filter(br -> Objects.equals(Integer.parseInt(br.getMember()), accountId)
                                && Objects.equals(Integer.parseInt(br.getBook()), bookId))
                        .map(BorrowReturn::getBorrowID)
                        .findFirst()
                        .orElse(null));
            }
        };
    }
}
