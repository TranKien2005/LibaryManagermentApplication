// package DAO;

// import data.AccountRepository;
// import data.TestAppContainer;
// import model.Account;
// import org.junit.Before;
// import org.junit.Test;

// import java.util.List;
// import java.util.concurrent.ExecutionException;

// import static org.junit.Assert.assertNotNull;

// public class AccountDaoTest {
//     private AccountRepository accountRepository;

//     @Before
//     public void setUp() {
//         // Use the TestAppContainer to get the mock repository
//         TestAppContainer appContainer = new TestAppContainer();
//         accountRepository = appContainer.getAccountRepository();
//     }

//     @Test
//     public void testGetAll() throws ExecutionException, InterruptedException {
//         // Add a dummy account to the mock repository
//         Account account = new Account(1, "username7", "password", "user");
//         accountRepository.add(account).get();

//         // Test the getAll method
//         List<Account> accounts = accountRepository.getAll().get();
//         assertNotNull(accounts);
//     }

//     @Test
//     public void testInsert() throws ExecutionException, InterruptedException {
//         Account account = new Account(1, "username7", "password", "user");
//         // Test the add method
//         accountRepository.add(account).get();
//         // You could add an assertion here to verify the account was added
//     }
// }
