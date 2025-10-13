package API.borrowreturn;

import API.BaseHttpReadOnly;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import model.BorrowReturn;

public class HttpBorrowReturnApi extends BaseHttpReadOnly<BorrowReturn, Integer> implements BorrowReturnApi {
    public HttpBorrowReturnApi(String baseUrl) {
        super(baseUrl, "/api/borrowreturns");
    }

    @Override
    protected Class<BorrowReturn> getEntityClass() {
        return BorrowReturn.class;
    }

    @Override
    public CompletableFuture<List<BorrowReturn>> getByAccountId(Integer accountId) {
        return client.getAsync(baseUrl + resourcePath + "/by-account?accountId=" + accountId, new com.google.gson.reflect.TypeToken<List<BorrowReturn>>(){}.getType());
    }

    @Override
    public CompletableFuture<Boolean> isBorrowed(Integer accountId, Integer bookId) {
        return client.getAsync(baseUrl + resourcePath + "/is-borrowed?accountId=" + accountId + "&bookId=" + bookId, Boolean.class);
    }

    @Override
    public CompletableFuture<Integer> getID(Integer accountId, Integer bookId) {
        return client.getAsync(baseUrl + resourcePath + "/get-id?accountId=" + accountId + "&bookId=" + bookId, Integer.class);
    }
}
