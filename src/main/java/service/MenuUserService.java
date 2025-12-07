package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import data.AccountRepository;
import data.BookRepository;
import data.BorrowRepository;
import data.BorrowReturnRepository;
import data.ManagerRepository;
import data.ReturnRepository;
import data.UserRepository;
import model.Account;
import model.Borrow;
import model.BorrowReturn;
import model.Document;
import model.Manager;
import model.Return;
import model.User;

public class MenuUserService {

    private final BookRepository bookRepository;
    private final BorrowReturnRepository borrowReturnRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final BorrowRepository borrowRepository;
    private final ReturnRepository returnRepository;

    public MenuUserService(BookRepository bookRepository, BorrowReturnRepository borrowReturnRepository,
            AccountRepository accountRepository, UserRepository userRepository, ManagerRepository managerRepository,
            BorrowRepository borrowRepository, ReturnRepository returnRepository) {
        this.bookRepository = bookRepository;
        this.borrowReturnRepository = borrowReturnRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
        this.borrowRepository = borrowRepository;
        this.returnRepository = returnRepository;
    }

    public List<Document> getAllBooks() {
        return bookRepository.getAll().join();
    }

    public List<BorrowReturn> getBorrowReturnList(int accountId) {
        return borrowReturnRepository.getByAccountId(accountId).join();
    }

    public Account getAccount(int accountId) {
        return accountRepository.get(accountId).join();
    }

    public User getUser(int accountId) {
        return userRepository.get(accountId).join();
    }

    public Manager getManager(int accountId) {
        return managerRepository.get(accountId).join();
    }

    public void borrowDocument(int memberId, int documentId, LocalDate borrowDate, LocalDate returnDate) {
        Borrow newBorrow = new Borrow(memberId, documentId, borrowDate, returnDate, "Borrowed");
        borrowRepository.insert(newBorrow).join();
    }

    public void returnDocument(int borrowId) {
        Borrow selectedBorrow = borrowRepository.get(borrowId).join();
        if (selectedBorrow == null) {
            throw new IllegalArgumentException("Borrow record not found.");
        }

        
        Boolean isBorrowed = borrowReturnRepository.isBorrowed(selectedBorrow.getAccountID(), selectedBorrow.getBookID()).join();
        if (!isBorrowed) {
            throw new IllegalStateException("This document has already been returned.");
        }
        
        int damagePercentage = (int) (Math.random() * 100);
        // We already have the borrowId from the parameter; avoid calling borrowRepository.getID(selectedBorrow)
        // which falls back to BaseHttpApi.getID() and throws UnsupportedOperationException.
        Return returnRecord = new Return(borrowId, LocalDate.now(), damagePercentage);
        returnRepository.insert(returnRecord).join();
    }

    public Document getBook(int bookId) {
        return bookRepository.get(bookId).join();
    }
}
