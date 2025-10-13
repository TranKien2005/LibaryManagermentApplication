package API.manager;

import API.BaseHttpApi;
import java.util.concurrent.CompletableFuture;
import model.Manager;

public class HttpManagerApi extends BaseHttpApi<Manager, Integer> implements ManagerApi {
    public HttpManagerApi(String baseUrl) {
        super(baseUrl, "/api/managers");
    }

    @Override
    protected Class<Manager> getEntityClass() {
        return Manager.class;
    }

    @Override
    public CompletableFuture<Integer> getID(Manager manager) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", manager, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }
}
