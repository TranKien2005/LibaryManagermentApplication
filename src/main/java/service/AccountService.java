package service;

import data.AccountRepository;
import model.Account;
import java.util.concurrent.CompletableFuture;

/**
 * Service class demonstrating how to use AccountRepository with API implementation.
 * This service handles account-related operations by delegating to the repository.
 */
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * Registers a new account.
     * Calls API endpoint: POST /api/accounts/register
     * 
     * @param username Username for the new account
     * @param password Password for the new account (will be hashed)
     * @param accountType Type of account (User or Manager)
     * @return CompletableFuture with the created account
     */
    public CompletableFuture<Account> registerAccount(String username, String password, String accountType) {
        // In a real implementation, the API would handle password hashing
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password); // API will hash this
        account.setAccountType(accountType);
        
        return accountRepository.add(account).thenCompose(id -> {
            account.setAccountID(id);
            return CompletableFuture.completedFuture(account);
        });
    }
    
    /**
     * Authenticates a user.
     * Calls API endpoint: POST /api/accounts/login
     * 
     * @param username Username
     * @param password Password
     * @return CompletableFuture with authentication token (JWT or session ID)
     */
    public CompletableFuture<String> loginAccount(String username, String password) {
        // This would call the login method on the API
        // For now, we'll simulate by calling a method that would be added to the API
        return CompletableFuture.completedFuture("fake-jwt-token");
    }
    
    /**
     * Gets an account by its ID.
     * Calls API endpoint: GET /api/accounts/{id}
     * 
     * @param id Account ID
     * @return CompletableFuture with the account (without password)
     */
    public CompletableFuture<Account> getAccountById(Integer id) {
        return accountRepository.get(id);
    }
    
    /**
     * Changes an account's password.
     * Calls API endpoint: PATCH /api/accounts/{id}/password
     * 
     * @param accountId Account ID
     * @param newPassword New password
     * @return CompletableFuture
     */
    public CompletableFuture<Void> changePassword(Integer accountId, String newPassword) {
        return accountRepository.updatePassword(accountId, newPassword);
    }
}