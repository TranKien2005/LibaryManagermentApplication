package API.user;

import API.BaseHttpApi;
import model.User;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP implementation of UserApi.
 */
public class HttpUserApi extends BaseHttpApi<User, Integer> implements UserApi {
    
    public HttpUserApi(String baseUrl) {
        super(baseUrl, "/api/users");
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
}