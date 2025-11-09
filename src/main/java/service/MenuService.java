package service;

import data.*;
import model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final ReturnRepository returnRepository;
    private final ManagerRepository managerRepository;
    private final BorrowReturnRepository borrowReturnRepository;

    public MenuService(AccountRepository accountRepository, UserRepository userRepository, BookRepository bookRepository,
                       BorrowRepository borrowRepository, ReturnRepository returnRepository, ManagerRepository managerRepository,
                       BorrowReturnRepository borrowReturnRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.returnRepository = returnRepository;
        this.managerRepository = managerRepository;
        this.borrowReturnRepository = borrowReturnRepository;
    }

    public static class InitialData {
        public final List<Document> books;
        public final List<User> users;
        public final List<BorrowReturn> borrowReturns;
        public final List<Account> accounts;

        public InitialData(List<Document> books, List<User> users, List<BorrowReturn> borrowReturns, List<Account> accounts) {
            this.books = books;
            this.users = users;
            this.borrowReturns = borrowReturns;
            this.accounts = accounts;
        }
    }

    public CompletableFuture<InitialData> loadInitialData() {
        CompletableFuture<List<Document>> booksFuture = bookRepository.getAll();
        CompletableFuture<List<User>> usersFuture = userRepository.getAll();
        CompletableFuture<List<BorrowReturn>> borrowReturnsFuture = borrowReturnRepository.getAll();
        CompletableFuture<List<Account>> accountsFuture = accountRepository.getAll();

        return CompletableFuture.allOf(booksFuture, usersFuture, borrowReturnsFuture, accountsFuture)
                .thenApply(v -> new InitialData(
                        booksFuture.join(),
                        usersFuture.join(),
                        borrowReturnsFuture.join(),
                        accountsFuture.join()
                ));
    }

    public CompletableFuture<String> getAccountDisplayName(int accountId) {
        return accountRepository.get(accountId).thenCompose(account -> {
            if (account == null) {
                return CompletableFuture.completedFuture("Unknown");
            }
            if ("User".equals(account.getAccountType())) {
                return userRepository.get(accountId).thenApply(user -> user != null ? "User: " + user.getFullName() : "Unknown User");
            } else {
                return managerRepository.get(accountId).thenApply(manager -> manager != null ? "Manager: " + manager.getFullName() : "Unknown Manager");
            }
        });
    }

    public CompletableFuture<Void> borrowDocument(int documentId, int memberId, LocalDate borrowDate, LocalDate returnDate) {
        if (borrowDate.isAfter(returnDate)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Borrow date cannot be after return date."));
        }
        Borrow newBorrow = new Borrow(memberId, documentId, borrowDate, returnDate, "Borrowed");
        return borrowRepository.insert(newBorrow).thenAccept(b -> {});
    }

    public CompletableFuture<Void> returnDocument(int borrowId) {
        return borrowRepository.get(borrowId).thenCompose(borrow -> {
            if (borrow == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("Selected borrow record not found."));
            }
            return borrowReturnRepository.isBorrowed(borrow.getAccountID(), borrow.getBookID()).thenCompose(isBorrowed -> {
                if (!Boolean.TRUE.equals(isBorrowed)) {
                    return CompletableFuture.failedFuture(new IllegalStateException("This document has already been returned."));
                }
                int damagePercentage = (int) (Math.random() * 100);
                Return returnRecord = new Return(borrow.getBorrowID(), LocalDate.now(), damagePercentage);
                return returnRepository.insert(returnRecord).thenAccept(r -> {});
            });
        });
    }

    public CompletableFuture<Void> updateDocumentDescription(int bookId, String description) {
        return bookRepository.setDescription(bookId, description);
    }

    public sealed interface QRCodeResult {}
    public record UserQRResult(User user) implements QRCodeResult {}
    public record BookQRResult(Document document) implements QRCodeResult {}
    public record InvalidQRResult(String error) implements QRCodeResult {}

    public CompletableFuture<QRCodeResult> processQRCode(String qrCodeText) {
        if (qrCodeText == null) {
            return CompletableFuture.completedFuture(new InvalidQRResult("Invalid QR Code format"));
        }
        if (qrCodeText.startsWith("accountID:")) {
            try {
                int accountId = Integer.parseInt(qrCodeText.substring("accountID:".length()).trim());
                return userRepository.get(accountId).thenApply(user -> {
                    if (user == null) {
                        return new InvalidQRResult("User not found");
                    }
                    return new UserQRResult(user);
                });
            } catch (NumberFormatException e) {
                return CompletableFuture.completedFuture(new InvalidQRResult("Invalid User ID format"));
            }
        } else if (qrCodeText.startsWith("BookID:")) {
            try {
                int bookId = Integer.parseInt(qrCodeText.substring("bookID:".length()).trim());
                return bookRepository.get(bookId).thenApply(book -> {
                     if (book == null) {
                        return new InvalidQRResult("Book not found");
                    }
                    return new BookQRResult(book);
                });
            } catch (NumberFormatException e) {
                return CompletableFuture.completedFuture(new InvalidQRResult("Invalid Book ID format"));
            }
        } else {
            return CompletableFuture.completedFuture(new InvalidQRResult("Invalid QR Code format"));
        }
    }
}
