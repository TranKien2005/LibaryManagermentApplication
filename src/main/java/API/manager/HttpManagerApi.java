package API.manager;

import API.BaseHttpApi;
import model.Manager;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of ManagerApi.
 */
public class HttpManagerApi extends BaseHttpApi<Manager, Integer> implements ManagerApi {
    
    public HttpManagerApi(String baseUrl) {
        super(baseUrl, "/api/managers");
    }

    @Override
        protected Class<Manager> getEntityClass() {
        return Manager.class;
    }
}