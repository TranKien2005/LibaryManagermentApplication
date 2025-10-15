package API.borrow;

import API.BaseHttpApi;
import model.Borrow;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of BorrowApi.
 */
public class HttpBorrowApi extends BaseHttpApi<Borrow, Integer> implements BorrowApi {
    
    public HttpBorrowApi(String baseUrl) {
        super(baseUrl, "/api/borrows");
    }

    @Override
    protected Class<Borrow> getEntityClass() {
        return Borrow.class;
    }
}