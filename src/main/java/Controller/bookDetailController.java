package Controller;

import java.io.File;
import java.io.InputStream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import QR.CreateQRCode;
import data.AppContainer;
import data.BookRepository;
import data.BorrowRepository;
import data.BorrowReturnRepository;
import data.DefaultAppContainer;
import data.ReturnRepository;
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

public class bookDetailController {

    @FXML
    private ImageView coverImageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label yearPublishedLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label availableCopiesLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private HBox ratingBox;

    @FXML
    private Label numberOfRatingsLabel;

    private Document book;

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

    private final ReturnRepository returnRepository;
    private final BorrowReturnRepository borrowReturnRepository;
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private int rating = 0;

    int accountID = menuUserController.getAccountID();

    public bookDetailController() {
        AppContainer container = DefaultAppContainer.getInstance();
        this.returnRepository = container.getReturnRepository();
        this.borrowReturnRepository = container.getBorrowReturnRepository();
        this.borrowRepository = container.getBorrowRepository();
        this.bookRepository = container.getBookRepository();
    }

    public void setBook(Document book) {
        this.book = book;
        updateBookDetails();
    }

    private void updateBookDetails() {
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

        titleLabel.setText(book.getTitle());
        System.out.println(book.getBookID());
        authorLabel.setText(book.getAuthor());
        publisherLabel.setText(book.getPublisher());
        yearPublishedLabel.setText(String.valueOf(book.getYearPublished()));
        descriptionLabel.setText(book.getDescription());
        categoryLabel.setText(book.getCategory());
        availableCopiesLabel.setText(String.valueOf(book.getAvailableCopies()));
        numberOfRatingsLabel.setText(String.valueOf(book.getReviewCount()));

        updateRatingBox();
    }

    private void updateRatingBox() {
        try {
            ratingBox.getChildren().clear();
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView();
                if (i <= book.getRating()) {
                    star.setImage(new Image("/images/menu/star_filled.png"));
                } else if (i - book.getRating() <= 0.5) {
                    star.setImage(new Image("/images/menu/halfStar.png"));
                } else {
                    star.setImage(new Image("/images/menu/star_empty.png"));
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

    @FXML
    private void handleBorrow() {
        if (borrowReturnRepository.isBorrowed(accountID, book.getBookID())) {
            util.ErrorDialog.showError("Thông báo", "Bạn đã mượn sách này rồi.", null);
            return;
        }
        int selectedMemberId = accountID;
        int selectedDocumentId = book.getBookID();
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = borrowDate.plusMonths(1);

        Borrow borrowRecord = new Borrow(selectedMemberId, selectedDocumentId, borrowDate, returnDate, "Borrowed");
        try {
            borrowRepository.add(borrowRecord);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        }
    }

    @FXML
    private void handleReturn() {
        try {
            if (borrowReturnRepository.isBorrowed(accountID, book.getBookID())) {
                int selectedBorrowId = borrowReturnRepository.getID(accountID, book.getBookID());
                int damagePercentage = (int) (Math.random() * 100);
                Return returnRecord = new Return(selectedBorrowId, LocalDate.now(), damagePercentage);
                returnRepository.add(returnRecord);
                util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.", null);
            } else {
                util.ErrorDialog.showError("Thông báo", "Bạn chưa mượn sách này", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        }
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
        try {
            if (update) {
                book.setRating(
                        (book.getRating() * book.getReviewCount() - currentRating + rating) / book.getReviewCount());
                bookRepository.update(book, book.getBookID());
            } else {
                bookRepository.addRating(book.getBookID(), rating);
                book = bookRepository.get(book.getBookID());
            }
            updateBookDetails();
            ErrorDialog.showSuccess("Success", "Rating added successfully.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        }
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
