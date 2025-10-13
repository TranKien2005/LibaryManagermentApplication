package API;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
// import java.net.http.HttpResponse; // not needed
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Simple async HTTP + JSON helper using java.net.http and Gson.
 * Place in package `API` as requested.
 */
public class ApiClient {
    private final HttpClient http;
    private final Gson gson;
    private final Duration timeout = Duration.ofSeconds(10);

    public ApiClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
        this.gson = new Gson();
    }

    public <T> CompletableFuture<T> getAsync(String url, Type typeOfT) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET()
                .header("Accept", "application/json")
                .build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenApply(resp -> gson.fromJson(resp.body(), typeOfT));
    }

    public <T> CompletableFuture<T> postAsync(String url, Object body, Class<T> respClass) {
        String json = gson.toJson(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .POST(BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenApply(resp -> gson.fromJson(resp.body(), respClass));
    }

    public <T> CompletableFuture<T> putAsync(String url, Object body, Class<T> respClass) {
        String json = gson.toJson(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .PUT(BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenApply(resp -> gson.fromJson(resp.body(), respClass));
    }

    public CompletableFuture<Void> deleteAsync(String url) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .DELETE()
                .build();
        return http.sendAsync(req, BodyHandlers.discarding()).thenApply(resp -> { resp.toString(); return null; });
    }
}
