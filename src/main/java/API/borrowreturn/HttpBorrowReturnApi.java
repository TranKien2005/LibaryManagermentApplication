package API.borrowreturn;

import API.BaseHttpApi;
import model.BorrowReturn;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of BorrowReturnApi.
 */
public class HttpBorrowReturnApi extends BaseHttpApi<BorrowReturn, Integer> implements BorrowReturnApi {
    
    public HttpBorrowReturnApi(String baseUrl) {
        super(baseUrl, "/api/borrow-returns");
    }

    @Override
    protected Class<BorrowReturn> getEntityClass() {
        return BorrowReturn.class;
    }

    @Override
    public CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId) {
        return client.getAsync(baseUrl + resourcePath + "/by-account/" + accountId, new com.google.gson.reflect.TypeToken<List<BorrowReturn>>(){}.getType());
    }

    @Override
    public CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId) {
        return client.getAsync(baseUrl + resourcePath + "/is-borrowed?accountId=" + accountId + "&bookId=" + bookId, Boolean.class);
    }
    
    @Override
    public CompletableFuture<Integer> getID(Integer accountId, Integer bookId) {
        // This would typically be implemented as an API call to get the ID
        // For now, returning a failed future as this would need backend API support
        return CompletableFuture.failedFuture(new UnsupportedOperationException("getID with accountId and bookId not implemented in HttpBorrowReturnApi"));
    }
}