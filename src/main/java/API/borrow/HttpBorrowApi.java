package API.borrow;

import API.BaseHttpApi;
import java.util.concurrent.CompletableFuture;
import model.Borrow;

public class HttpBorrowApi extends BaseHttpApi<Borrow, Integer> implements BorrowApi {
    public HttpBorrowApi(String baseUrl) {
        super(baseUrl, "/api/borrows");
    }

    @Override
    protected Class<Borrow> getEntityClass() {
        return Borrow.class;
    }

    @Override
    public CompletableFuture<Integer> getID(Borrow borrow) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", borrow, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }
}
