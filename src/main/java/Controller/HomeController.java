package Controller;

import service.BookService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Document;
import util.ErrorDialog;
import java.io.IOException;
// removed unused InputStream import
import java.sql.SQLException;
import java.util.List;

public class HomeController {

    @FXML
    private FlowPane fpTopBooks;

    @FXML
    private FlowPane fpRecommendedBooks;

    @FXML
    private FlowPane fpTrendingBooks;

    @FXML
    private FlowPane fpNewArrivals;

    @FXML
    private TextField tfSearch;

    @FXML
    private ScrollPane scrollPaneMain;

    @FXML
    private VBox topBooksSection;

    @FXML
    private VBox recommendedBooksSection;

    @FXML
    private VBox trendingBooksSection;

    private BookService bookService = BookService.getInstance();
    private int newArrivalsPage = 0;
    private int searchPage = 0;
    private static final int PAGE_SIZE = 14;
    private static final int PAGE_SIZE_SEARCH = 21;
    private boolean isSearching = false;
    private String currentSearchText = "";

    private List<Document> topBooks;
    private List<Document> favoriteBooks;
    private List<Document> trendingBooks;
    private static HomeController instance;

    private Parent initialContent;

    public static HomeController getInstance() {
        if (instance == null) {
            instance = new HomeController();
        }
        return instance;
    }

    @FXML
    public void initialize() {
        initialContent = (Parent) scrollPaneMain.getContent();
        tfSearch.setOnAction(_ -> handleSearch());
        loadInitialContent();
    }

    private void loadInitialContent() {
        try {
            topBooks = bookService.getTopRatedBooks();
            int favAccountId = 2; // fallback account id when not logged in
            model.Account current = service.AppCache.getInstance().getCurrentAccount();
            if (current != null) {
                favAccountId = current.getAccountID();
            }
            favoriteBooks = bookService.getFavoriteBooksForAccount(favAccountId);
            trendingBooks = bookService.getTrendingBooks();

            fpTopBooks.getChildren().clear();
            for (Document book : topBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImageUrl(), book.getRating(), book);
                fpTopBooks.getChildren().add(bookItem);
            }

            fpRecommendedBooks.getChildren().clear();
            for (Document book : favoriteBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImageUrl(), book.getRating(), book);
                fpRecommendedBooks.getChildren().add(bookItem);
            }

            fpTrendingBooks.getChildren().clear();
            for (Document book : trendingBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImageUrl(), book.getRating(), book);
                fpTrendingBooks.getChildren().add(bookItem);
            }

            loadMoreNewArrivals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label lblCurrentPage;

    @FXML
    private void handleNextPage() {

        if (isSearching) {
            searchPage++;
            try {
                loadMoreSearchResults();
                scrollPaneMain.setVvalue(0);
            } catch (SQLException e) {
                e.printStackTrace();
                ErrorDialog.showError("Lỗi", "Không thể tải thêm kết quả tìm kiếm.",
                        (Stage) tfSearch.getScene().getWindow());
                searchPage--;
                return;
            }

        } else {
            newArrivalsPage++;
            try {
                loadMoreNewArrivals();
            } catch (SQLException e) {
                e.printStackTrace();
                ErrorDialog.showError("Lỗi", "Không thể tải thêm sách mới.", (Stage) tfSearch.getScene().getWindow());
                newArrivalsPage--;
                return;
            }

        }
        updateCurrentPageLabel();

    }

    @FXML
    private void handlePreviousPage() {

        if (isSearching) {
            if (searchPage == 0) {
                return;
            }
            searchPage--;
            try {
                loadMoreSearchResults();
                scrollPaneMain.setVvalue(0);

            } catch (SQLException e) {
                e.printStackTrace();
                ErrorDialog.showError("Lỗi", "Không thể tải thêm kết quả tìm kiếm.",
                        (Stage) tfSearch.getScene().getWindow());
                searchPage++;
                return;
            }
        } else {
            if (newArrivalsPage == 0) {
                return;
            }
            newArrivalsPage--;
            try {
                loadMoreNewArrivals();

            } catch (SQLException e) {
                e.printStackTrace();
                newArrivalsPage++;
                ErrorDialog.showError("Lỗi", "Không thể tải thêm sách mới.", (Stage) tfSearch.getScene().getWindow());
                return;
            }
        }
        updateCurrentPageLabel();

    }

    private void updateCurrentPageLabel() {
        int currentPage = isSearching ? searchPage : newArrivalsPage;
        lblCurrentPage.setText(String.valueOf(currentPage + 1));
    }

    private void loadMoreSearchResults() throws SQLException {
    List<Document> searchResults = bookService.searchNewArrivals(currentSearchText, searchPage, PAGE_SIZE_SEARCH);

        if (searchResults.isEmpty()) {
            throw new SQLException("No more search results to load.");
        }
        fpNewArrivals.getChildren().clear();
            for (Document book : searchResults) {

            VBox bookItem = createBookItem(book.getTitle(), book.getCoverImageUrl(), book.getRating(), book);
            fpNewArrivals.getChildren().add(bookItem);
        }
    }

    private void loadMoreNewArrivals() throws SQLException {
    List<Document> newArrivals = bookService.getNewArrivals(newArrivalsPage, PAGE_SIZE);
        if (newArrivals.isEmpty()) {
            throw new SQLException("No more new arrivals to load.");
        }
        fpNewArrivals.getChildren().clear();
        for (Document book : newArrivals) {
            VBox bookItem = createBookItem(book.getTitle(), book.getCoverImageUrl(), book.getRating(), book);
            fpNewArrivals.getChildren().add(bookItem);
        }
    }
    private VBox createBookItem(String title, String coverImageUrl, double rating, Document book) {
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("book-item");
        ImageView imageView = new ImageView();
        if (coverImageUrl != null && !coverImageUrl.isBlank()) {
            try {
                Image image = new Image(coverImageUrl, 100, 150, true, true, true);
                image.errorProperty().addListener((_obs, _old, isErr) -> {
                    if (_obs == null && _old == null) { /* no-op to avoid unused vars */ }
                    if (isErr) imageView.setImage(new Image("/images/menu/coverArtUnknown.png"));
                });
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image("/images/menu/coverArtUnknown.png"));
            }
        } else {
            Image image = new Image("/images/menu/coverArtUnknown.png");
            imageView.setImage(image);
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");

        Label label = new Label(title);
        label.getStyleClass().add("label");

        // Tạo HBox để chứa các sao
        HBox starsBox = new HBox(2);
        starsBox.getStyleClass().add("stars-box");
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView();
            if (i <= rating) {
                star.setImage(new Image("/images/menu/star_filled.png")); // Đường dẫn đến ảnh sao đen
            } else if (i - rating <= 0.5) {
                star.setImage(new Image("/images/menu/halfStar.png")); // Đường dẫn đến ảnh sao nửa
            } else {
                star.setImage(new Image("/images/menu/star_empty.png")); // Đường dẫn đến ảnh sao trống
            }
            star.setFitHeight(15);
            star.setFitWidth(15);
            star.setPreserveRatio(true);
            starsBox.getChildren().add(star);
        }
        // Thêm EventHandler cho sự kiện nháy đúp
        vBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openBookDetailTab(book);
            }
        });

        vBox.getChildren().addAll(imageView, label, starsBox);
        return vBox;
    }

    @FXML
    private void handleSearch() {
        scrollPaneMain.setContent(initialContent);
        currentSearchText = tfSearch.getText().toLowerCase().trim();
        isSearching = true;
        searchPage = 0;

        // Ẩn các VBox top, recommend, và trending
        topBooksSection.setVisible(false);
        topBooksSection.setManaged(false);
        recommendedBooksSection.setVisible(false);
        recommendedBooksSection.setManaged(false);
        trendingBooksSection.setVisible(false);
        trendingBooksSection.setManaged(false);

        // Xóa các sách hiện tại trong phần sách mới
        fpNewArrivals.getChildren().clear();
        newArrivalsPage = 0; // Reset the page number for new arrivals

        try {
            // Tìm kiếm sách mới theo tiêu đề
            loadMoreSearchResults();
            scrollPaneMain.setVvalue(0);
            updateCurrentPageLabel();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message to the user)
        }
    }

    public void handleAboutUs() {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/TranKien2005/LibaryManagerment_OOP_JavaFX_project"));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Không thể mở liên kết.", (Stage) tfSearch.getScene().getWindow());
        }
    }

    public void handleContact() {
        try {
            String recipient = "ttk08112005@gmail.com";
            String subject = "Contact Us";
            String body = "Hello, this is a message from the Library Management System.";

            String mailto = String.format("mailto:%s?subject=%s&body=%s", 
                                          java.net.URLEncoder.encode(recipient, "UTF-8"), 
                                          java.net.URLEncoder.encode(subject, "UTF-8"), 
                                          java.net.URLEncoder.encode(body, "UTF-8"));

            java.awt.Desktop.getDesktop().browse(new java.net.URI(mailto));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Không thể mở ứng dụng email.", (Stage) tfSearch.getScene().getWindow());
        }
    }

    @FXML
    public void handleReload() {
        // Clear only book-related cache and reuse the initial load routine
        service.AppCache.getInstance().clearBookCache();
        scrollPaneMain.setContent(initialContent);
        isSearching = false;
        tfSearch.clear();

        // Ensure sections are visible
        topBooksSection.setVisible(true);
        topBooksSection.setManaged(true);
        recommendedBooksSection.setVisible(true);
        recommendedBooksSection.setManaged(true);
        trendingBooksSection.setVisible(true);
        trendingBooksSection.setManaged(true);

        // Reset paging and reload content via helper
        fpNewArrivals.getChildren().clear();
        newArrivalsPage = 0;
        updateCurrentPageLabel();
        loadInitialContent();
        scrollPaneMain.setVvalue(0);
    }

    public void reload() {
        handleReload();
    }

    public void undoDetail() {
        scrollPaneMain.setContent(initialContent);
    }

    public void openBookDetailTab(Document book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/bookDetail.fxml"));
            Parent bookDetailRoot = loader.load();

            // Lấy controller của BookDetail và truyền dữ liệu sách vào
            bookDetailController controller = loader.getController();
            controller.setBook(book);

            // Thay thế nội dung của ScrollPane bằng chi tiết sách
            scrollPaneMain.setContent(bookDetailRoot);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message to the user)
        }
    }

    @FXML
    private void handleBack() {
        undoDetail();
    }
}