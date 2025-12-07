package API;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Simple API-side rate limiter wrapper. Named ApiRateLimiter to avoid conflict with Guava's RateLimiter.
 */
public class ApiRateLimiter {
    private final RateLimiter rateLimiter;
    private final ExecutorService executor;

    public ApiRateLimiter(double permitsPerSecond) {
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    public <T> CompletableFuture<T> executeAsyncRequest(Supplier<CompletableFuture<T>> asyncSupplier) {
        CompletableFuture<T> result = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                rateLimiter.acquire();
                CompletableFuture<T> cf = asyncSupplier.get();
                cf.whenComplete((r, ex) -> {
                    if (ex != null) result.completeExceptionally(ex);
                    else result.complete(r);
                });
            } catch (Throwable t) {
                result.completeExceptionally(t);
            }
        });
        return result;
    }

    /**
     * Fail-fast variant: if a permit is not immediately available, return a failed CompletableFuture
     * with ApiException("Rate limit exceeded"). Does not block.
     */
    public <T> CompletableFuture<T> executeAsyncRequestFailFast(Supplier<CompletableFuture<T>> asyncSupplier) {
        // tryAcquire without blocking
        if (!rateLimiter.tryAcquire()) {
            CompletableFuture<T> failed = new CompletableFuture<>();
            failed.completeExceptionally(new ApiException("Rate limit exceeded"));
            return failed;
        }
        // permit acquired, proceed directly on calling thread by invoking supplier
        try {
            CompletableFuture<T> cf = asyncSupplier.get();
            return cf;
        } catch (Throwable t) {
            CompletableFuture<T> failed = new CompletableFuture<>();
            failed.completeExceptionally(t);
            return failed;
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}

