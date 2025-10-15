package service;

import data.ApiRepositoryFactory;
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
 * Demonstration class showing how to use the API-based repository implementations.
 * This class shows examples of calling all the specified API endpoints.
 */
public class ApiServiceDemo {
    
    private final ApiRepositoryFactory repositoryFactory;
    
    // Services
    private final BookService bookService;
    private final AccountService accountService;
    private final BorrowService borrowService;
    private final BorrowReturnService borrowReturnService;
    private final ReturnService returnService;
    private final UserService userService;
    private final ManagerService managerService;
    
    public ApiServiceDemo(String baseUrl) {
        this.repositoryFactory = new ApiRepositoryFactory(baseUrl);
        
        // Initialize services with API-based repositories
        this.bookService = new BookService(repositoryFactory.createBookRepository());
        this.accountService = new AccountService(repositoryFactory.createAccountRepository());
        this.borrowService = new BorrowService(repositoryFactory.createBorrowRepository());
        this.borrowReturnService = new BorrowReturnService(repositoryFactory.createBorrowReturnRepository());
        this.returnService = new ReturnService(repositoryFactory.createReturnRepository());
        this.userService = new UserService(repositoryFactory.createUserRepository());
        this.managerService = new ManagerService(repositoryFactory.createManagerRepository());
    }
    
    // Book API examples
    public CompletableFuture<List<Document>> demonstrateBookPagination() {
        // GET /api/books?page=&size=
        return bookService.getBooks(0, 10);
    }
    
    public CompletableFuture<Document> demonstrateGetBookById(Integer bookId) {
        // GET /api/books/{id}
        return bookService.getBookById(bookId);
    }
    
    public CompletableFuture<Void> demonstrateCreateBook(Document book) {
        // POST /api/books
        return bookService.createBook(book);
    }
    
    public CompletableFuture<Void> demonstrateUpdateBook(Document book, Integer bookId) {
        // PUT /api/books/{id}
        return bookService.updateBook(book, bookId);
    }
    
    public CompletableFuture<Void> demonstrateDeleteBook(Integer bookId) {
        // DELETE /api/books/{id}
        return bookService.deleteBook(bookId);
    }
    
    public CompletableFuture<List<Document>> demonstrateGetTopRatedBooks() {
        // GET /api/books/top-rated
        return bookService.getTopRatedBooks();
    }
    
    public CompletableFuture<List<Document>> demonstrateGetTrendingBooks() {
        // GET /api/books/trending
        return bookService.getTrendingBooks();
    }
    
    public CompletableFuture<List<Document>> demonstrateSearchBooks(String query) {
        // GET /api/books/search?query=&page=&size=
        return bookService.searchBooks(query, 0, 10);
    }
    
    // Account API examples
    public CompletableFuture<Account> demonstrateRegisterAccount(String username, String password) {
        // POST /api/accounts/register
        return accountService.registerAccount(username, password, "User");
    }
    
    public CompletableFuture<String> demonstrateLoginAccount(String username, String password) {
        // POST /api/accounts/login
        return accountService.loginAccount(username, password);
    }
    
    public CompletableFuture<Account> demonstrateGetAccountById(Integer accountId) {
        // GET /api/accounts/{id}
        return accountService.getAccountById(accountId);
    }
    
    public CompletableFuture<Void> demonstrateChangePassword(Integer accountId, String newPassword) {
        // PATCH /api/accounts/{id}/password
        return accountService.changePassword(accountId, newPassword);
    }
    
    // Borrow API examples
    public CompletableFuture<List<Borrow>> demonstrateGetAllBorrows() {
        // GET /api/borrows
        return borrowService.getAllBorrows();
    }
    
    public CompletableFuture<Borrow> demonstrateGetBorrowById(Integer borrowId) {
        // GET /api/borrows/{id}
        return borrowService.getBorrowById(borrowId);
    }
    
    public CompletableFuture<Void> demonstrateCreateBorrow(Borrow borrow) {
        // POST /api/borrows
        return borrowService.createBorrow(borrow);
    }
    
    public CompletableFuture<Void> demonstrateUpdateBorrow(Borrow borrow, Integer borrowId) {
        // PUT /api/borrows/{id}
        return borrowService.updateBorrow(borrow, borrowId);
    }
    
    public CompletableFuture<Void> demonstrateDeleteBorrow(Integer borrowId) {
        // DELETE /api/borrows/{id}
        return borrowService.deleteBorrow(borrowId);
    }
    
    // BorrowReturn API examples
    public CompletableFuture<List<BorrowReturn>> demonstrateGetAllBorrowReturns() {
        // GET /api/borrow-returns
        return borrowReturnService.getAllBorrowReturns();
    }
    
    public CompletableFuture<List<BorrowReturn>> demonstrateGetBorrowReturnsByAccount(Integer accountId) {
        // GET /api/borrow-returns/by-account/{accountId}
        return borrowReturnService.getBorrowReturnsByAccount(accountId);
    }
    
    public CompletableFuture<Boolean> demonstrateIsBookBorrowed(Integer accountId, Integer bookId) {
        // GET /api/borrow-returns/is-borrowed?accountId=&bookId=
        return borrowReturnService.isBookBorrowed(accountId, bookId);
    }
    
    // Return API examples
    public CompletableFuture<List<Return>> demonstrateGetAllReturns() {
        // GET /api/returns
        return returnService.getAllReturns();
    }
    
    public CompletableFuture<Return> demonstrateGetReturnById(Integer returnId) {
        // GET /api/returns/{id}
        return returnService.getReturnById(returnId);
    }
    
    public CompletableFuture<Void> demonstrateCreateReturn(Return ret) {
        // POST /api/returns
        return returnService.createReturn(ret);
    }
    
    public CompletableFuture<Void> demonstrateUpdateReturn(Return ret, Integer returnId) {
        // PUT /api/returns/{id}
        return returnService.updateReturn(ret, returnId);
    }
    
    public CompletableFuture<Void> demonstrateDeleteReturn(Integer returnId) {
        // DELETE /api/returns/{id}
        return returnService.deleteReturn(returnId);
    }
    
    // User API examples
    public CompletableFuture<List<User>> demonstrateGetAllUsers() {
        // GET /api/users
        return userService.getAllUsers();
    }
    
    public CompletableFuture<User> demonstrateGetUserById(Integer userId) {
        // GET /api/users/{id}
        return userService.getUserById(userId);
    }
    
    public CompletableFuture<Void> demonstrateCreateUser(User user) {
        // POST /api/users
        return userService.createUser(user);
    }
    
    public CompletableFuture<Void> demonstrateUpdateUser(User user, Integer userId) {
        // PUT /api/users/{id}
        return userService.updateUser(user, userId);
    }
    
    public CompletableFuture<Void> demonstrateDeleteUser(Integer userId) {
        // DELETE /api/users/{id}
        return userService.deleteUser(userId);
    }
    
    // Manager API examples
    public CompletableFuture<List<Manager>> demonstrateGetAllManagers() {
        // GET /api/managers
        return managerService.getAllManagers();
    }
    
    public CompletableFuture<Manager> demonstrateGetManagerById(Integer managerId) {
        // GET /api/managers/{id}
        return managerService.getManagerById(managerId);
    }
    
    public CompletableFuture<Void> demonstrateCreateManager(Manager manager) {
        // POST /api/managers
        return managerService.createManager(manager);
    }
    
    public CompletableFuture<Void> demonstrateUpdateManager(Manager manager, Integer managerId) {
        // PUT /api/managers/{id}
        return managerService.updateManager(manager, managerId);
    }
    
    public CompletableFuture<Void> demonstrateDeleteManager(Integer managerId) {
        // DELETE /api/managers/{id}
        return managerService.deleteManager(managerId);
    }
}