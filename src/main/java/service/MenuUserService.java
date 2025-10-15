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

    public List<Document> getAllBooks() throws SQLException {
        return bookRepository.getAll();
    }

    public List<BorrowReturn> getBorrowReturnList(int accountId) throws SQLException {
        return borrowReturnRepository.getByAccountId(accountId);
    }

    public Account getAccount(int accountId) throws SQLException {
        return accountRepository.get(accountId);
    }

    public User getUser(int accountId) throws SQLException {
        return userRepository.get(accountId);
    }

    public Manager getManager(int accountId) throws SQLException {
        return managerRepository.get(accountId);
    }

    public void borrowDocument(int memberId, int documentId, LocalDate borrowDate, LocalDate returnDate) throws SQLException {
        Borrow newBorrow = new Borrow(memberId, documentId, borrowDate, returnDate, "Borrowed");
        borrowRepository.add(newBorrow);
    }

    public void returnDocument(int borrowId) throws SQLException {
        Borrow selectedBorrow = borrowRepository.get(borrowId);
        if (selectedBorrow == null) {
            throw new IllegalArgumentException("Borrow record not found.");
        }

        Return existingReturnRecord = returnRepository.get(selectedBorrow.getBorrowID());
        if (existingReturnRecord != null) {
            throw new IllegalStateException("Document already returned.");
        }

        int damagePercentage = (int) (Math.random() * 100);
        Return returnRecord = new Return(borrowRepository.getID(selectedBorrow), LocalDate.now(), damagePercentage);
        returnRepository.add(returnRecord);
    }

    public Document getBook(int bookId) throws SQLException {
        return bookRepository.get(bookId);
    }
}
