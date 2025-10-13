package service;

import DAO.BookDao;
import model.Document;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for book-related operations. Encapsulates BookDao and
 * provides a small in-memory cache for read-mostly lists (top/trending/favorites).
 */
public class BookService {
    private static final BookService INSTANCE = new BookService();

    private final BookDao bookDao = BookDao.getInstance();
    private final AppCache appCache = AppCache.getInstance();

    private BookService() {}

    public static BookService getInstance() {
        return INSTANCE;
    }

    public List<Document> getTopRatedBooks() throws SQLException {
    List<Document> cached = appCache.getTopRatedBooks();
    if (cached != null) return cached;
    List<Document> fresh = bookDao.getTopRatedBooks();
    if (fresh != null) appCache.setTopRatedBooks(fresh);
    return fresh;
    }

    public List<Document> getTrendingBooks() throws SQLException {
    List<Document> cached = appCache.getTrendingBooks();
    if (cached != null) return cached;
    List<Document> fresh = bookDao.getTrendingBooks();
    if (fresh != null) appCache.setTrendingBooks(fresh);
    return fresh;
    }

    public List<Document> getFavoriteBooksForAccount(int accountId) throws SQLException {
    List<Document> cached = appCache.getFavoriteBooksForAccount(accountId);
    if (cached != null) return cached;
    List<Document> fresh = bookDao.getFavorite(accountId);
    if (fresh != null) appCache.setFavoriteBooksForAccount(accountId, fresh);
    return fresh;
    }

    public List<Document> getNewArrivals(int page, int pageSize) throws SQLException {
        // new arrivals are paged; don't cache pages by default
        return bookDao.getAll(page, pageSize);
    }

    public List<Document> searchNewArrivals(String text, int page, int pageSize) throws SQLException {
        // search results are dynamic; don't cache
        return bookDao.searchNewArrivals(text, page, pageSize);
    }

    // Cache is managed by AppCache; clearing is done via AppCache.clearBookCache()
}
