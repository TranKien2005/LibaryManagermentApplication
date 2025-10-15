package data;

import service.HomeService;
import service.MemberManagementService;
import service.MenuService;
import service.MenuUserService;
import service.MyAccountService;
import service.register.RegisterService;

public interface AppContainer {
    AccountRepository getAccountRepository();
    BookRepository getBookRepository();
    UserRepository getUserRepository();
    BorrowRepository getBorrowRepository();
    ReturnRepository getReturnRepository();
    ManagerRepository getManagerRepository();
    BorrowReturnRepository getBorrowReturnRepository();
    HomeService getHomeService();
    RegisterService getRegisterService();
    MemberManagementService getMemberManagementService();
    MenuService getMenuService();
    MenuUserService getMenuUserService();
    MyAccountService getMyAccountService();
    service.add.AddBookService getAddBookService();
    service.auth.AuthService getAuthService();
}