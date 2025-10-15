package data;

import service.HomeService;
import service.MemberManagementService;
import service.MenuService;
import service.MyAccountService;
import service.add.AddBookService;
import service.auth.AuthService;
import service.register.RegisterService;

public class DefaultAppContainer implements AppContainer {

    private static DefaultAppContainer instance = null;

    private final AccountRepository accountRepository = new AccountRepository();
    private final BookRepository bookRepository = new BookRepository();
    private final UserRepository userRepository = new UserRepository();
    private final BorrowRepository borrowRepository = new BorrowRepository();
    private final ReturnRepository returnRepository = new ReturnRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();
    private final BorrowReturnRepository borrowReturnRepository = new BorrowReturnRepository();
    private final HomeService homeService = new HomeService(bookRepository);
    private final RegisterService registerService = new RegisterService(accountRepository, userRepository, managerRepository);
    private final MemberManagementService memberManagementService = new MemberManagementService(userRepository, accountRepository);
    private final MenuService menuService = new MenuService(accountRepository, userRepository, bookRepository, borrowRepository, returnRepository, managerRepository, borrowReturnRepository);
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