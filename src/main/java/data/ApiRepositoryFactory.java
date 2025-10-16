package data;

import API.account.HttpAccountApi;
import API.book.HttpBookApi;
import API.borrow.HttpBorrowApi;
import API.borrowreturn.HttpBorrowReturnApi;
import API.manager.HttpManagerApi;
import API.returnpkg.HttpReturnApi;
import API.user.HttpUserApi;

/**
 * Factory class for creating repository instances that use API implementations.
 * This factory provides repository implementations that communicate with backend services via HTTP APIs.
 */
public class ApiRepositoryFactory {
    
    private final String baseUrl;
    
    public ApiRepositoryFactory(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Creates an AccountRepository instance that uses HTTP API calls.
     * @return AccountRepository implementation
     */
    public AccountRepository createAccountRepository() {
        return new AccountRepositoryAdapter(new HttpAccountApi(baseUrl));
    }
    
    /**
     * Creates a BookRepository instance that uses HTTP API calls.
     * @return BookRepository implementation
     */
    public BookRepository createBookRepository() {
        return new BookRepositoryAdapter(new HttpBookApi(baseUrl));
    }
    
    /**
     * Creates a BorrowRepository instance that uses HTTP API calls.
     * @return BorrowRepository implementation
     */
    public BorrowRepository createBorrowRepository() {
        return new BorrowRepositoryAdapter(new HttpBorrowApi(baseUrl));
    }
    
    /**
     * Creates a BorrowReturnRepository instance that uses HTTP API calls.
     * @return BorrowReturnRepository implementation
     */
    public BorrowReturnRepository createBorrowReturnRepository() {
        return new BorrowReturnRepositoryAdapter(new HttpBorrowReturnApi(baseUrl));
    }
    
    /**
     * Creates a ManagerRepository instance that uses HTTP API calls.
     * @return ManagerRepository implementation
     */
    public ManagerRepository createManagerRepository() {
        return new ManagerRepositoryAdapter(new HttpManagerApi(baseUrl));
    }
    
    /**
     * Creates a ReturnRepository instance that uses HTTP API calls.
     * @return ReturnRepository implementation
     */
    public ReturnRepository createReturnRepository() {
        return new ReturnRepositoryAdapter(new HttpReturnApi(baseUrl));
    }
    
    /**
     * Creates a UserRepository instance that uses HTTP API calls.
     * @return UserRepository implementation
     */
    public UserRepository createUserRepository() {
        return new UserRepositoryAdapter(new HttpUserApi(baseUrl));
    }
}