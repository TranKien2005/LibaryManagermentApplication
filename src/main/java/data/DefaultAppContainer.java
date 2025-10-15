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
    private final AccountApi accountApi = new HttpAccountApi("http://localhost:8080");
    private final BookApi bookApi = new HttpBookApi("http://localhost:8080");
    private final UserApi userApi = new HttpUserApi("http://localhost:8080");
    private final BorrowApi borrowApi = new HttpBorrowApi("http://localhost:8080");
    private final ReturnApi returnApi = new HttpReturnApi("http://localhost:8080");
    private final ManagerApi managerApi = new HttpManagerApi("http://localhost:8080");
    private final BorrowReturnApi borrowReturnApi = new HttpBorrowReturnApi("http://localhost:8080");
    
    // Adapters
    private final AccountRepository accountRepository = new AccountRepositoryAdapter(accountApi);
    private final BookRepository bookRepository = new BookRepositoryAdapter(bookApi);
    private final UserRepository userRepository = new UserRepositoryAdapter(userApi);
    private final BorrowRepository borrowRepository = new BorrowRepositoryAdapter(borrowApi);
    private final ReturnRepository returnRepository = new ReturnRepositoryAdapter(returnApi);
    private final ManagerRepository managerRepository = new ManagerRepositoryAdapter(managerApi);
    private final BorrowReturnRepository borrowReturnRepository = new BorrowReturnRepositoryAdapter(borrowReturnApi);
    
    private final HomeService homeService = new HomeService(bookRepository);
    private final RegisterService registerService = new RegisterService(accountRepository, userRepository, managerRepository);
    private final MemberManagementService memberManagementService = new MemberManagementService(userRepository, accountRepository);
    private final MenuService menuService = new MenuService(accountRepository, userRepository, bookRepository, borrowRepository, returnRepository, managerRepository, borrowReturnRepository);
    private final MenuUserService menuUserService = new MenuUserService(bookRepository, borrowReturnRepository, accountRepository, userRepository, managerRepository, borrowRepository, returnRepository);
    private final MyAccountService myAccountService = new MyAccountService(userRepository, accountRepository, managerRepository);
    private final AddBookService addBookService = new AddBookService(bookRepository);
    private final AuthService authService = new AuthService(accountRepository);


    private DefaultAppContainer() {

    }

    public static DefaultAppContainer getInstance() {
        if (instance == null) {
            instance = new DefaultAppContainer();
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