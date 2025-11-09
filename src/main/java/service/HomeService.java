package service;

import data.BookRepository;
import model.Document;
import java.sql.SQLException;
import java.util.List;

public class HomeService {
    private final BookRepository bookRepository;

    public HomeService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public static class InitialHomeContent {
        public final List<Document> topBooks;
        public final List<Document> favoriteBooks;
        public final List<Document> trendingBooks;

        public InitialHomeContent(List<Document> topBooks, List<Document> favoriteBooks, List<Document> trendingBooks) {
            this.topBooks = topBooks;
            this.favoriteBooks = favoriteBooks;
            this.trendingBooks = trendingBooks;
        }
    }

    public InitialHomeContent getInitialContent(int accountId) {
        List<Document> topBooks = bookRepository.getTopRatedBooks().join();
        List<Document> favoriteBooks = bookRepository.getFavorite(accountId).join();
        List<Document> trendingBooks = bookRepository.getTrendingBooks().join();
        return new InitialHomeContent(topBooks, favoriteBooks, trendingBooks);
    }

    public List<Document> getNewArrivals(int page, int pageSize) {
        return bookRepository.getNewArrivals(page, pageSize).join();
    }

    public List<Document> search(String searchText, int page, int pageSize) {
        return bookRepository.search(searchText, page, pageSize).join();
    }
}
