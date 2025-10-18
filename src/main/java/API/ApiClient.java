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
// import java.net.http.HttpResponse; // not needed
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET()
                .header("Accept", "application/json")
                .build();
        return http.sendAsync(req, BodyHandlers.ofString())
                .thenApply(resp -> {
                    String raw = resp.body();
                    // parse envelope first
                    Type envType = com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, typeOfT).getType();
                    ApiEnvelope<T> env = gson.fromJson(raw, envType);
                    if (env == null) throw new ApiException("Empty response from server");
                    if (!env.success) throw new ApiException(env.message != null ? env.message : "API error");
                    return env.data;
                })
                .whenComplete((ignored, ex) -> {
                    // reference ignored to satisfy linter (no-op)
                    if (ignored == null) {
                        // no-op
                    }
                    if (ex == null) return;
                    Throwable cause = ex instanceof java.util.concurrent.CompletionException ? ex.getCause() : ex;
                    // convert only network errors; preserve ApiException thrown by thenApply
                    if (cause instanceof java.net.http.HttpTimeoutException) {
                        throw new ApiException("Yêu cầu đã hết thời gian chờ (timeout)");
                    } else if (cause instanceof java.net.ConnectException || cause instanceof java.net.UnknownHostException) {
                        throw new ApiException("Không thể kết nối tới server");
                    } else if (cause instanceof ApiException) {
                        // rethrow original ApiException so caller sees server message
                        throw (ApiException) cause;
                    }
                    // not a network error nor ApiException -> rethrow cause
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException(cause);
                });
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
                .thenApply(resp -> {
                    String raw = resp.body();
                    ApiEnvelope<T> env = gson.fromJson(raw, com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, respClass).getType());
                    if (env == null) throw new ApiException("Empty response from server");
                    if (!env.success) throw new ApiException(env.message != null ? env.message : "API error");
                    return env.data;
                })
                .whenComplete((ignored, ex) -> {
                    if (ignored == null) {
                        // no-op
                    }
                    if (ex == null) return;
                    Throwable cause = ex instanceof java.util.concurrent.CompletionException ? ex.getCause() : ex;
                    if (cause instanceof java.net.http.HttpTimeoutException) {
                        throw new ApiException("Yêu cầu đã hết thời gian chờ (timeout)");
                    } else if (cause instanceof java.net.ConnectException || cause instanceof java.net.UnknownHostException) {
                        throw new ApiException("Không thể kết nối tới server");
                    } else if (cause instanceof ApiException) {
                        throw (ApiException) cause;
                    }
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException(cause);
                });
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
                .thenApply(resp -> {
                    String raw = resp.body();
                    ApiEnvelope<T> env = gson.fromJson(raw, com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, respClass).getType());
                    if (env == null) throw new ApiException("Empty response from server");
                    if (!env.success) throw new ApiException(env.message != null ? env.message : "API error");
                    return env.data;
                })
                .whenComplete((ignored, ex) -> {
                    if (ignored == null) {
                        // no-op
                    }
                    if (ex == null) return;
                    Throwable cause = ex instanceof java.util.concurrent.CompletionException ? ex.getCause() : ex;
                    if (cause instanceof java.net.http.HttpTimeoutException) {
                        throw new ApiException("Yêu cầu đã hết thời gian chờ (timeout)");
                    } else if (cause instanceof java.net.ConnectException || cause instanceof java.net.UnknownHostException) {
                        throw new ApiException("Không thể kết nối tới server");
                    } else if (cause instanceof ApiException) {
                        throw (ApiException) cause;
                    }
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException(cause);
                });
    }

    public CompletableFuture<Void> deleteAsync(String url) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .DELETE()
                .build();
                return http.sendAsync(req, BodyHandlers.ofString()).thenApply(resp -> {
                        String raw = resp.body();
                        ApiEnvelope<Void> env = gson.fromJson(raw, com.google.gson.reflect.TypeToken.getParameterized(ApiEnvelope.class, Void.class).getType());
                        if (env == null) throw new ApiException("Empty response from server");
                        if (!env.success) throw new ApiException(env.message != null ? env.message : "API error");
                        return null;
                }).whenComplete((ignored, ex) -> {
                    if (ignored == null) {
                        // no-op
                    }
                    if (ex == null) return;
                    Throwable cause = ex instanceof java.util.concurrent.CompletionException ? ex.getCause() : ex;
                    if (cause instanceof java.net.http.HttpTimeoutException) {
                        throw new ApiException("Yêu cầu đã hết thời gian chờ (timeout)");
                    } else if (cause instanceof java.net.ConnectException || cause instanceof java.net.UnknownHostException) {
                        throw new ApiException("Không thể kết nối tới server");
                    } else if (cause instanceof ApiException) {
                        throw (ApiException) cause;
                    }
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException(cause);
                }).thenApply(ApiClient::returnNull);
    }

    // helper to map any object to null for CompletableFuture<Void> results
    private static Void returnNull(Object o) {
        return null;
    }
}
