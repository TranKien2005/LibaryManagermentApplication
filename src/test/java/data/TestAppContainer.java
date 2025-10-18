// package data;

// /**
//  * A test implementation of {@link AppContainer} that uses mock repositories for testing purposes.
//  * This class should be used in test environments to avoid dependencies on actual data sources.
//  */
// public class TestAppContainer extends DefaultAppContainer {

//     private final MockRepositoryFactory mockFactory = new MockRepositoryFactory();

    
//     @Override
//     public AccountRepository getAccountRepository() {
//         return mockFactory.createAccountRepository();
//     }

//     @Override
//     public BookRepository getBookRepository() {
//         return mockFactory.createBookRepository();
//     }

//     @Override
//     public BorrowReturnRepository getBorrowReturnRepository() {
//         return mockFactory.createBorrowReturnRepository();
//     }
// }
