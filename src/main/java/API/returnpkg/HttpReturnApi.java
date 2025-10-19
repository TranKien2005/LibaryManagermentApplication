package API.returnpkg;

import API.BaseHttpApi;
import model.Return;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of ReturnApi.
 */
public class HttpReturnApi extends BaseHttpApi<Return, Integer> implements ReturnApi {
    
    public HttpReturnApi(String baseUrl) {
        super(baseUrl, "/api/returns");
    }

    @Override
    protected Class<Return> getEntityClass() {
        return Return.class;
    }

    @Override
    public CompletableFuture<Integer> getID(Return returnObj) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", returnObj, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }
}