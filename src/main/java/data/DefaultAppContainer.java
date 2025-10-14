package data;

public class DefaultAppContainer implements AppContainer {

    private static DefaultAppContainer instance = null;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAccountRepository'");
    }

    @Override
    public BookRepository getBookRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookRepository'");
    }

    @Override
    public UserRepository getUserRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserRepository'");
    }

    @Override
    public BorrowRepository getBorrowRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBorrowRepository'");
    }

    @Override
    public ReturnRepository getReturnRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReturnRepository'");
    }

    @Override
    public ManagerRepository getManagerRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getManagerRepository'");
    }

    @Override
    public BorrowReturnRepository getBorrowReturnRepository() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBorrowReturnRepository'");
    } 

}
