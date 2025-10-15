package data;

import service.HomeService;

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
}
