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
}