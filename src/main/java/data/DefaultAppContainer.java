package data;

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
import service.HomeService;
import service.MemberManagementService;
import service.MenuService;
import service.MenuUserService;
import service.MyAccountService;
import service.add.AddBookService;
import service.auth.AuthService;
import service.register.RegisterService;

public class DefaultAppContainer implements AppContainer {

    private static DefaultAppContainer instance = null;

    // API clients
    private final AccountApi accountApi;
    private final BookApi bookApi;
    private final UserApi userApi;
    private final BorrowApi borrowApi;
    private final ReturnApi returnApi;
    private final ManagerApi managerApi;
    private final BorrowReturnApi borrowReturnApi;

    // Adapters
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final ReturnRepository returnRepository;
    private final ManagerRepository managerRepository;
    private final BorrowReturnRepository borrowReturnRepository;

    private final HomeService homeService;
    private final RegisterService registerService;
    private final MemberManagementService memberManagementService;
    private final MenuService menuService;
    private final MenuUserService menuUserService;
    private final MyAccountService myAccountService;
    private final AddBookService addBookService;
    private final AuthService authService;

    private DefaultAppContainer(boolean isTestEnvironment) {
        if (isTestEnvironment) {
            // Use mock repositories for testing
            MockRepositoryFactory mockFactory = new MockRepositoryFactory();
            this.accountRepository = mockFactory.createAccountRepository();
            this.bookRepository = mockFactory.createBookRepository();
            this.userRepository = null; // Update with mock if needed
            this.borrowRepository = null; // Update with mock if needed
            this.returnRepository = null; // Update with mock if needed
            this.managerRepository = null; // Update with mock if needed
            this.borrowReturnRepository = null; // Update with mock if needed

            this.accountApi = null;
            this.bookApi = null;
            this.userApi = null;
            this.borrowApi = null;
            this.returnApi = null;
            this.managerApi = null;
            this.borrowReturnApi = null;
        } else {
            // Use real API clients for production
            this.accountApi = new HttpAccountApi("http://localhost:8080");
            this.bookApi = new HttpBookApi("http://localhost:8080");
            this.userApi = new HttpUserApi("http://localhost:8080");
            this.borrowApi = new HttpBorrowApi("http://localhost:8080");
            this.returnApi = new HttpReturnApi("http://localhost:8080");
            this.managerApi = new HttpManagerApi("http://localhost:8080");
            this.borrowReturnApi = new HttpBorrowReturnApi("http://localhost:8080");

            this.accountRepository = new AccountRepositoryAdapter(accountApi);
            this.bookRepository = new BookRepositoryAdapter(bookApi);
            this.userRepository = new UserRepositoryAdapter(userApi);
            this.borrowRepository = new BorrowRepositoryAdapter(borrowApi);
            this.returnRepository = new ReturnRepositoryAdapter(returnApi);
            this.managerRepository = new ManagerRepositoryAdapter(managerApi);
            this.borrowReturnRepository = new BorrowReturnRepositoryAdapter(borrowReturnApi);
        }

        this.homeService = new HomeService(bookRepository);
        this.registerService = new RegisterService(accountRepository, userRepository, managerRepository);
        this.memberManagementService = new MemberManagementService(userRepository, accountRepository);
        this.menuService = new MenuService(accountRepository, userRepository, bookRepository, borrowRepository, returnRepository, managerRepository, borrowReturnRepository);
        this.menuUserService = new MenuUserService(bookRepository, borrowReturnRepository, accountRepository, userRepository, managerRepository, borrowRepository, returnRepository);
        this.myAccountService = new MyAccountService(userRepository, accountRepository, managerRepository);
        this.addBookService = new AddBookService(bookRepository);
        this.authService = new AuthService(accountRepository);
    }

    public static DefaultAppContainer getInstance() {
        if (instance == null) {
            boolean isTest = "test".equals(System.getProperty("env"));
            instance = new DefaultAppContainer(isTest);
        }
        return instance;
    }

    @Override
    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    @Override
    public BookRepository getBookRepository() {
        return bookRepository;
    }

    @Override
    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public BorrowRepository getBorrowRepository() {
        return borrowRepository;
    }

    @Override
    public ReturnRepository getReturnRepository() {
        return returnRepository;
    }

    @Override
    public ManagerRepository getManagerRepository() {
        return managerRepository;
    }

    @Override
    public BorrowReturnRepository getBorrowReturnRepository() {
        return borrowReturnRepository;
    }

    @Override
    public HomeService getHomeService() {
        return homeService;
    }

    @Override
    public RegisterService getRegisterService() {
        return registerService;
    }

    @Override
    public MemberManagementService getMemberManagementService() {
        return memberManagementService;
    }

    @Override
    public MenuService getMenuService() {
        return menuService;
    }

    @Override
    public MenuUserService getMenuUserService() {
        return menuUserService;
    }

    @Override
    public MyAccountService getMyAccountService() {
        return myAccountService;
    }

    @Override
    public AddBookService getAddBookService() {
        return addBookService;
    }

    @Override
public AuthService getAuthService() {
    return authService;
}
}
