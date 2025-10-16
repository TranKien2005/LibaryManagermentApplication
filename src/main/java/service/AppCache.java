package service;

import model.Account;

/**
 * Simple application-level cache for small state like the currently authenticated account.
 */
public class AppCache {
    private static final AppCache INSTANCE = new AppCache();

    private final InMemoryCache<String, Object> cache = new InMemoryCache<>();

    private AppCache() {}

    public static AppCache getInstance() {
        return INSTANCE;
    }

    public void setCurrentAccount(Account account) {
        cache.put("currentAccount", account);
    }

    public Account getCurrentAccount() {
        Object v = cache.get("currentAccount");
        return v instanceof Account ? (Account) v : null;
    }

    public void clear() {
        cache.clear();
    }

    // --- Book-related cache helpers (top, trending, favorites) ---
    public void setTopRatedBooks(java.util.List<model.Document> list) {
        cache.put("book:topRated", list);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<model.Document> getTopRatedBooks() {
        Object v = cache.get("book:topRated");
        return v instanceof java.util.List ? (java.util.List<model.Document>) v : null;
    }

    public void setTrendingBooks(java.util.List<model.Document> list) {
        cache.put("book:trending", list);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<model.Document> getTrendingBooks() {
        Object v = cache.get("book:trending");
        return v instanceof java.util.List ? (java.util.List<model.Document>) v : null;
    }

    public void setFavoriteBooksForAccount(int accountId, java.util.List<model.Document> list) {
        cache.put("book:favorite:" + accountId, list);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<model.Document> getFavoriteBooksForAccount(int accountId) {
        Object v = cache.get("book:favorite:" + accountId);
        return v instanceof java.util.List ? (java.util.List<model.Document>) v : null;
    }

    /** Clear only book-related cache entries (top/trending/favorites) */
    public void clearBookCache() {
        // Remove known book keys and any favorite keys by prefix without wiping other cache entries
        Account current = getCurrentAccount();
        cache.remove("book:topRated");
        cache.remove("book:trending");
        cache.removeByPrefix("book:favorite:");
        // preserve currentAccount if present (remove may have cleared it unintentionally)
        if (current != null) setCurrentAccount(current);
    }
}
