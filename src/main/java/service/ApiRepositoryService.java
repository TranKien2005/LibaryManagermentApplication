package service;

import API.account.AccountApi;
import API.account.HttpAccountApi;
import API.book.BookApi;
import API.book.HttpBookApi;
import API.borrow.BorrowApi;
import API.borrow.HttpBorrowApi;
import API.borrowreturn.BorrowReturnApi;
import API.borrowreturn.HttpBorrowReturnApi;
import API.manager.HttpManagerApi;
import API.manager.ManagerApi;
import API.returnpkg.HttpReturnApi;
import API.returnpkg.ReturnApi;
import API.user.HttpUserApi;
import API.user.UserApi;
import data.AccountRepository;
import data.BookRepository;
import data.BorrowRepository;
import data.BorrowReturnRepository;
import data.ManagerRepository;
import data.ReturnRepository;
import data.UserRepository;
import model.Account;
import model.Document;
import model.Borrow;
import model.BorrowReturn;
import model.Manager;
import model.Return;
import model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class that implements repository interfaces and calls API endpoints.
 * This class demonstrates how to use the API clients to implement repository operations.
 */
public class ApiRepositoryService {
    
    // API clients
    private final AccountApi accountApi;
    private final BookApi bookApi;
    private final BorrowApi borrowApi;
    private final BorrowReturnApi borrowReturnApi;
    private final ManagerApi managerApi;
    private final ReturnApi returnApi;
    private final UserApi userApi;
    
    public ApiRepositoryService(String baseUrl) {
        this.accountApi = new HttpAccountApi(baseUrl);
        this.bookApi = new HttpBookApi(baseUrl);
        this.borrowApi = new HttpBorrowApi(baseUrl);
        this.borrowReturnApi = new HttpBorrowReturnApi(baseUrl);
        this.managerApi = new HttpManagerApi(baseUrl);
        this.returnApi = new HttpReturnApi(baseUrl);
        this.userApi = new HttpUserApi(baseUrl);
    }
    
    // Book API methods
    public CompletableFuture<List<Document>> getAllBooks(int page, int size) {
        return bookApi.getAll(page, size);
    }
    
    public CompletableFuture<Document> getBookById(Integer id) {
        return bookApi.get(id);
    }
    
    public CompletableFuture<Void> createBook(Document book) {
        return bookApi.insert(book);
    }
    
    public CompletableFuture<Void> updateBook(Document book, Integer id) {
        return bookApi.update(book, id);
    }
    
    public CompletableFuture<Void> deleteBook(Integer id) {
        return bookApi.delete(id);
    }
    
    public CompletableFuture<List<Document>> getTopRatedBooks() {
        return bookApi.getTopRatedBooks();
    }
    
    public CompletableFuture<List<Document>> getTrendingBooks() {
        return bookApi.getTrendingBooks();
    }
    
    public CompletableFuture<List<Document>> searchBooks(String query, int page, int size) {
        return bookApi.search(query, page, size);
    }
    
    // Account API methods
    public CompletableFuture<Account> registerAccount(String username, String password, String accountType) {
        return accountApi.register(username, password, accountType);
    }
    
    public CompletableFuture<String> loginAccount(String username, String password) {
        return accountApi.login(username, password);
    }
    
    public CompletableFuture<Account> getAccountById(Integer id) {
        return accountApi.get(id);
    }
    
    public CompletableFuture<Void> changeAccountPassword(Integer id, String newPassword) {
        return accountApi.updatePassword(id, newPassword);
    }
    
    // Borrow API methods
    public CompletableFuture<List<Borrow>> getAllBorrows() {
        return borrowApi.getAll();
    }
    
    public CompletableFuture<Borrow> getBorrowById(Integer id) {
        return borrowApi.get(id);
    }
    
    public CompletableFuture<Void> createBorrow(Borrow borrow) {
        return borrowApi.insert(borrow);
    }
    
    public CompletableFuture<Void> updateBorrow(Borrow borrow, Integer id) {
        return borrowApi.update(borrow, id);
    }
    
    public CompletableFuture<Void> deleteBorrow(Integer id) {
        return borrowApi.delete(id);
    }
    
    // BorrowReturn API methods
    public CompletableFuture<List<BorrowReturn>> getAllBorrowReturns() {
        return borrowReturnApi.getAll();
    }
    
    public CompletableFuture<List<BorrowReturn>> getBorrowReturnsByAccount(Integer accountId) {
        return borrowReturnApi.getByAccountId(accountId);
    }
    
    public CompletableFuture<Boolean> isBookBorrowed(Integer accountId, Integer bookId) {
        return borrowReturnApi.isBorrowed(accountId, bookId);
    }
    
    // Return API methods
    public CompletableFuture<List<Return>> getAllReturns() {
        return returnApi.getAll();
    }
    
    public CompletableFuture<Return> getReturnById(Integer id) {
        return returnApi.get(id);
    }
    
    public CompletableFuture<Void> createReturn(Return ret) {
        return returnApi.insert(ret);
    }
    
    public CompletableFuture<Void> updateReturn(Return ret, Integer id) {
        return returnApi.update(ret, id);
    }
    
    public CompletableFuture<Void> deleteReturn(Integer id) {
        return returnApi.delete(id);
    }
    
    // User API methods
    public CompletableFuture<List<User>> getAllUsers() {
        return userApi.getAll();
    }
    
    public CompletableFuture<User> getUserById(Integer id) {
        return userApi.get(id);
    }
    
    public CompletableFuture<Void> createUser(User user) {
        return userApi.insert(user);
    }
    
    public CompletableFuture<Void> updateUser(User user, Integer id) {
        return userApi.update(user, id);
    }
    
    public CompletableFuture<Void> deleteUser(Integer id) {
        return userApi.delete(id);
    }
    
    // Manager API methods
    public CompletableFuture<List<Manager>> getAllManagers() {
        return managerApi.getAll();
    }
    
    public CompletableFuture<Manager> getManagerById(Integer id) {
        return managerApi.get(id);
    }
    
    public CompletableFuture<Void> createManager(Manager manager) {
        return managerApi.insert(manager);
    }
    
    public CompletableFuture<Void> updateManager(Manager manager, Integer id) {
        return managerApi.update(manager, id);
    }
    
    public CompletableFuture<Void> deleteManager(Integer id) {
        return managerApi.delete(id);
    }
}