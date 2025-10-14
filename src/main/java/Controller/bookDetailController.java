package Controller;

import java.io.File;
// java.io.IOException intentionally unused after refactor but kept for clarity
// (no direct usage required)
import java.io.InputStream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import DAO.BookDao;
import DAO.BorrowDao;
import DAO.BorrowReturnDAO;
import DAO.ReturnDao;
import QR.CreateQRCode;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import model.Borrow;
import model.Document;
import model.Return;
import util.ErrorDialog;
import data.DefaultAppContainer;
import data.BorrowReturnRepository;
import data.ReturnRepository;
import data.BorrowRepository;

public class bookDetailController {

    @FXML
    private ImageView coverImageView; // Hiển thị ảnh bìa sách

    @FXML
    private Label titleLabel; // Tiêu đề sách

    @FXML
    private Label authorLabel; // Tác giả

    @FXML
    private Label publisherLabel; // Nhà xuất bản

    @FXML
    private Label yearPublishedLabel; // Năm phát hành

    @FXML
    private Label categoryLabel; // Thể loại

    @FXML
    private Label availableCopiesLabel; // Số bản có sẵn

    @FXML
    private Label descriptionLabel; // Mô tả sách (có hỗ trợ wrap text)

    @FXML
    private HBox ratingBox; // Khung hiển thị đánh giá (dạng sao)

    @FXML
    private Label numberOfRatingsLabel; // Số lượt đánh giá

    private Document book; // Đối tượng chứa thông tin sách

    @FXML
    private Button star1;

    @FXML
    private Button star2;

    @FXML
    private Button star3;

    @FXML
    private Button star4;

    @FXML
    private Button star5;

    @FXML
    private ImageView qrCodeImageView;

    private static ReturnRepository returnRepository = DefaultAppContainer.getInstance().getReturnRepository();
    private static BorrowReturnRepository borrowReturnRepository = DefaultAppContainer.getInstance().getBorrowReturnRepository();
    private static BorrowRepository borrowRepository = DefaultAppContainer.getInstance().getBorrowRepository();
    private static ReturnRepository returnDao = DefaultAppContainer.getInstance().getReturnRepository();
    private int rating = 0;

    int accountID = menuUserController.getAccountID();

    /**
     * Thiết lập sách cần hiển thị.
     * 
     * @param book Đối tượng Document đại diện cho thông tin sách.
     */

    public void setBook(Document book) {
        this.book = book;
        updateBookDetails();
    }

    /**
     * Cập nhật thông tin chi tiết sách lên giao diện.
     */
    private void updateBookDetails() {
        // Cập nhật ảnh bìa
        accountID = menuUserController.getAccountID();
        String coverImageUrl = book.getCoverImageUrl();
        try {
            InputStream qrCodeStream = CreateQRCode.generateQRCode("BookID: " + book.getBookID());
            qrCodeImageView.setImage(new Image(qrCodeStream));
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi tạo mã QR.", null);
        }
        if (coverImageUrl != null && !coverImageUrl.isBlank()) {
            try {
                Image image = new Image(coverImageUrl, 200, 300, true, true, true);
                image.errorProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
                        if (newVal != null && newVal) {
                            coverImageView.setImage(new Image("/images/menu/coverArtUnknown.png"));
                        }
                    }
                });
                coverImageView.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                coverImageView.setImage(new Image("/images/menu/coverArtUnknown.png"));
            }
        } else {
            coverImageView.setImage(new Image("/images/menu/coverArtUnknown.png"));
        }

        // Cập nhật thông tin cơ bản
        titleLabel.setText(book.getTitle());
        System.out.println(book.getBookID());
        authorLabel.setText(book.getAuthor());
        publisherLabel.setText(book.getPublisher());
        yearPublishedLabel.setText(String.valueOf(book.getYearPublished()));
        descriptionLabel.setText(book.getDescription());
        categoryLabel.setText(book.getCategory());
        availableCopiesLabel.setText(String.valueOf(book.getAvailableCopies()));
        numberOfRatingsLabel.setText(String.valueOf(book.getReviewCount()));

        // Hiển thị đánh giá (rating)
        updateRatingBox();
    }



    /**
     * Cập nhật đánh giá sao (rating) của sách.
     */
    private void updateRatingBox() {
        try {
            ratingBox.getChildren().clear(); // Xóa các sao cũ
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView();
                if (i <= book.getRating()) {
                    star.setImage(new Image("/images/menu/star_filled.png")); // Đường dẫn đến ảnh sao đen
                } else if (i - book.getRating() <= 0.5) {
                    star.setImage(new Image("/images/menu/halfStar.png")); // Đường dẫn đến ảnh sao nửa
                } else {
                    star.setImage(new Image("/images/menu/star_empty.png")); // Đường dẫn đến ảnh sao trống
                }
                star.setFitHeight(15);
                star.setFitWidth(15);
                star.setPreserveRatio(true);
                ratingBox.getChildren().add(star);
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi cập nhật đánh giá sao.",
                    (Stage) ratingBox.getScene().getWindow());
        }
    }

    /**
     * Xử lý mượn sách.
     */
    @FXML
    private void handleBorrow() {
        if (BorrowReturnDAO.getInstance().isBorrowed(accountID, book.getBookID())) {
            util.ErrorDialog.showError("Thông báo", "Bạn đã mượn sách này rồi.", null);
            return;
        }
        int selectedMemberId = accountID;
        int selectedDocumentId = book.getBookID();
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = borrowDate.plusMonths(1);

        Borrow borrowRecord = new Borrow(selectedMemberId, selectedDocumentId, borrowDate, returnDate, "Borrowed");
        borrowRepository.insert(borrowRecord)
            .thenRun(() -> Platform.runLater(() -> 
                util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.", null)))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    ex.printStackTrace();
                    util.ErrorDialog.showError("Database Error", ex.getMessage(), null);
                });
                return null;
            });
    }

    /**
     * Xử lý trả sách.
     */
    @FXML
    private void handleReturn() {
        borrowReturnRepository.isBorrowed(accountID, book.getBookID())
            .thenCompose(isBorrowed -> {
                if (isBorrowed) {
                    return borrowReturnRepository.getID(accountID, book.getBookID());
                } else {
                    return CompletableFuture.completedFuture(-1);
                }
            })
            .thenCompose(selectedBorrowId -> {
                if (selectedBorrowId == -1) {
                    Platform.runLater(() -> 
                        util.ErrorDialog.showError("Thông báo", "Bạn chưa mượn sách này", null)
                    );
                    return CompletableFuture.completedFuture(-1);
                } else {
                    int damagePercentage = (int) (Math.random() * 100); // Random damage percentage between 0 and 100
                    Return returnRecord = new Return(selectedBorrowId,
                            LocalDate.now(), damagePercentage);
                    return returnRepository.insert(returnRecord)
                        .thenApply(v -> {
                            Platform.runLater(() -> 
                                util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.", null)
                            );
                            return selectedBorrowId;
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                ex.printStackTrace();
                                util.ErrorDialog.showError("Error", ex.getMessage(), null);
                            });
                            return -1;
                        });
                }
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    ex.printStackTrace();
                    util.ErrorDialog.showError("Database Error", ex.getMessage(), null);
                });
                return -1;
            });
    }


  

    @FXML
    private void handleStarClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        boolean update = false;
        if (rating != 0) {
            update = true;
        }
        int currentRating = rating;
        switch (buttonId) {
            case "star1" -> rating = 1;
            case "star2" -> rating = 2;
            case "star3" -> rating = 3;
            case "star4" -> rating = 4;
            case "star5" -> rating = 5;
        }

        updateStarDisplay();
        System.out.println("User rated the book: " + rating + " stars");
        if (update) {
            try {
                book.setRating(
                        (book.getRating() * book.getReviewCount() - currentRating + rating) / book.getReviewCount());
                BookDao.getInstance().update(book, book.getBookID());
                updateBookDetails();
                ErrorDialog.showSuccess("Success", "Rating added successfully.", null);

            } catch (SQLException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Database Error", e.getMessage(), null);
            } catch (Exception e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Error", e.getMessage(), null);
            }
            return;
        }
        try {
            BookDao.getInstance().addRating(book.getBookID(), rating);
            book = BookDao.getInstance().get(book.getBookID());
            updateBookDetails();
            ErrorDialog.showSuccess("Success", "Rating added successfully.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", e.getMessage(), null);
        }
        // Thực hiện các hành động khác như lưu rating vào cơ sở dữ liệu
    }

    private void updateStarDisplay() {
        Button[] stars = { star1, star2, star3, star4, star5 };
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setStyle("-fx-text-fill: gold;");
            } else {
                stars[i].setStyle("-fx-text-fill: black;");
            }
        }
    }

    @FXML
    private void handleQrCodeClick() {
        try {
            Image qrCodeImage = qrCodeImageView.getImage();
            if (qrCodeImage != null) {
                File outputFile = new File(System.getProperty("user.home") + "/Downloads/QRCodeBook.png");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                ImageIO.write(SwingFXUtils.fromFXImage(qrCodeImage, null), "png", outputFile);
                util.ErrorDialog.showSuccess("Success", "QR code has been saved to Downloads folder.", null);
            } else {
                throw new Exception("QR code not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi tải mã QR.", null);
        }
    }
}
