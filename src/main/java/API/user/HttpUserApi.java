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

    @Override
    public CompletableFuture<Integer> getID(User user) {
        return client.postAsync(baseUrl + resourcePath + "/get-id", user, Integer.class);
    }

    @Override
    public CompletableFuture<java.util.List<Integer>> getAllID() {
        return client.getAsync(baseUrl + resourcePath + "/ids", new com.google.gson.reflect.TypeToken<java.util.List<Integer>>(){}.getType());
    }
}