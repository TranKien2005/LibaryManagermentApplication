package API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import model.AuthResponse;
import service.auth.AuthContext;

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
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    // Adapter cho LocalDateTime
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    public <T> CompletableFuture<T> getAsync(String url, Type typeOfT) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET()
                .header("Accept", "application/json");
        addAuthHeader(builder);
        HttpRequest req = builder.build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenCompose(resp -> handleResponseWithPossibleRefresh(resp, url, null, typeOfT, () -> getAsync(url, typeOfT)));
    }

    public <T> CompletableFuture<T> postAsync(String url, Object body, Class<T> respClass) {
        String json = gson.toJson(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .POST(BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        addAuthHeader(builder);
        HttpRequest req = builder.build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenCompose(resp -> handleResponseWithPossibleRefresh(resp, url, json, respClass, () -> postAsync(url, body, respClass)));
    }

    public <T> CompletableFuture<T> putAsync(String url, Object body, Class<T> respClass) {
        String json = gson.toJson(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .PUT(BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        addAuthHeader(builder);
        HttpRequest req = builder.build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenCompose(resp -> handleResponseWithPossibleRefresh(resp, url, json, respClass, () -> putAsync(url, body, respClass)));
    }

    public CompletableFuture<Void> deleteAsync(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .DELETE();
        addAuthHeader(builder);
        HttpRequest req = builder.build();
        return http.sendAsync(req, BodyHandlers.ofString()).thenCompose(resp ->
                handleResponseWithPossibleRefresh(resp, url, null, Void.class, () -> deleteAsync(url))
        ).thenApply(ApiClient::returnNull);
    }

    // attach Authorization header when access token exists
    private void addAuthHeader(HttpRequest.Builder builder) {
        String token = AuthContext.getInstance().getAccessToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    // Centralized response handler which attempts a refresh once on 401 then retries the provided supplier.
    private <T> CompletableFuture<T> handleResponseWithPossibleRefresh(HttpResponse<String> resp, String url, String requestBody, Type respType, java.util.function.Supplier<CompletableFuture<T>> retrySupplier) {
        int status = resp.statusCode();
        if (status == 401) {
            // try refresh
            String refreshToken = AuthContext.getInstance().getRefreshToken();
            if (refreshToken == null) {
                AuthContext.getInstance().clear();
                return CompletableFuture.failedFuture(new ApiException("Missing refresh token, please login again"));
            }
            synchronized (ApiClient.class) {
                // perform refresh
                try {
                    URI uri = URI.create(url);
                    String origin = uri.getScheme() + "://" + uri.getAuthority();
                    // backend is served under /api context, refresh endpoint is /api/auth/refresh
                    String refreshUrl = origin + "/api/auth/refresh";
                    String json = gson.toJson(Map.of("refreshToken", refreshToken));
                    HttpRequest refreshReq = HttpRequest.newBuilder()
                            .uri(URI.create(refreshUrl))
                            .timeout(timeout)
                            .POST(BodyPublishers.ofString(json))
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .build();
                    HttpResponse<String> refreshResp = http.send(refreshReq, BodyHandlers.ofString());
                    String raw = refreshResp.body();
                    ApiEnvelope<AuthResponse> env = gson.fromJson(raw, com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, AuthResponse.class).getType());
                    if (env == null) throw new ApiException("Empty response from refresh endpoint");
                    if (!env.success) {
                        AuthContext.getInstance().clear();
                        throw new ApiException(env.message != null ? env.message : "Refresh failed");
                    }
                    AuthResponse auth = env.data;
                    if (auth == null || auth.getAccessToken() == null) {
                        AuthContext.getInstance().clear();
                        throw new ApiException("Invalid refresh response");
                    }
                    // update context
                    AuthContext.getInstance().setTokens(auth.getAccessToken(), auth.getRefreshToken(), auth.getAccountType());
                } catch (Exception e) {
                    AuthContext.getInstance().clear();
                    return CompletableFuture.failedFuture(new ApiException("Unable to refresh token: " + e.getMessage()));
                }
            }
            // retry original request once
            return retrySupplier.get();
        }

        // not a 401, parse normally (with fallback for non-wrapped primitive responses)
        return CompletableFuture.supplyAsync(() -> {
            String raw = resp.body();
            Type envType = com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, respType).getType();
            try {
                ApiEnvelope<T> env = gson.fromJson(raw, envType);
                if (env == null) throw new ApiException("Empty response from server");
                if (!env.success) throw new ApiException(env.message != null ? env.message : "API error");
                return env.data;
            } catch (com.google.gson.JsonSyntaxException ex) {
                // fallback: server returned a raw primitive (e.g. a plain string/token) instead of ApiEnvelope
                try {
                    Object direct = gson.fromJson(raw, respType);
                    return (T) direct;
                } catch (Exception ex2) {
                    // if expected type is String, return raw body as string
                    if (respType instanceof Class && respType == String.class) {
                        return (T) raw;
                    }
                    throw new ApiException("Invalid response format: " + raw);
                }
            }
        });
    }

    // helper to map any object to null for CompletableFuture<Void> results
    private static Void returnNull(Object o) {
        return null;
    }
}
