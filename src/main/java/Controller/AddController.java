package Controller;

import java.io.File;
import java.io.IOException;

import Main.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import service.add.AddBookService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.function.Consumer;
import util.*;

public class AddController extends menuController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField quantityField;

    @FXML
    private Button addButton;

    @FXML
    private TextField isbnField;

    private AddBookService addBookService;

    private Consumer<Document> onAddListener;

    public void setOnAddListener(Consumer<Document> listener) {
        this.onAddListener = listener;
    }

    @FXML
    private void handleAddDocument() {
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getText();
        String publisher = publisherField.getText();

        int year;
        int quantity;

        try {
            year = Integer.parseInt(yearField.getText());
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "Định dạng số không hợp lệ.", (Stage) addButton.getScene().getWindow());
            return;
        }

        addBookService.addDocument(title, author, category, publisher, year, quantity)
            .whenComplete((document, ex) -> {
                Platform.runLater(() -> {
                    if (ex != null) {
                        util.ErrorDialog.showError("Lỗi", "Không thể thêm tài liệu: " + ex.getMessage(), (Stage) addButton.getScene().getWindow());
                        ex.printStackTrace();
                    } else {
                        util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.", (Stage) addButton.getScene().getWindow());
                        clearFields();
                        if (onAddListener != null) {
                            onAddListener.accept(document);
                        }
                    }
                });
            });
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryField.clear();
        publisherField.clear();
        yearField.clear();
        quantityField.clear();
    }

    @SuppressWarnings("unused")
    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        addBookService = Main.appContainer.getAddBookService();
        System.out.println("AddController đã được khởi tạo");
    }

    @FXML
    private Button backButton;

    @FXML
    private void handleCancel() {
        clearFields();
        isbnField.clear();
    }

    public void reload() {
        clearFields();
        isbnField.clear();
    }

    @FXML
    private Button addByIsbnButton;

    @FXML
    private void handleAddByIsbn() {
        String isbn = isbnField.getText();
        if (isbn.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng nhập ISBN.", (Stage) addByIsbnButton.getScene().getWindow());
            return;
        }

        addBookService.addBookByIsbn(isbn)
            .whenComplete((document, ex) -> {
                Platform.runLater(() -> {
                    if (ex != null) {
                        util.ErrorDialog.showError("Lỗi", "Không thể thêm tài liệu: " + ex.getMessage(), (Stage) addByIsbnButton.getScene().getWindow());
                        ex.printStackTrace();
                    } else {
                        util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.", (Stage) addByIsbnButton.getScene().getWindow());
                        clearFields();
                        isbnField.clear();
                    }
                });
            });
    }

    @FXML
    private Button addByFileButton;

    @FXML
    private void handleAddByFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(addByFileButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loading1.fxml"));
                Parent loadingRoot = loader.load();
                Stage loadingStage = new Stage();
                loadingStage.initModality(Modality.APPLICATION_MODAL);
                loadingStage.setScene(new Scene(loadingRoot));
                loadingStage.setOnCloseRequest(event -> event.consume());
                loadingStage.show();

                ProgressBar progressBar = (ProgressBar) loader.getNamespace().get("progressBar");

                addBookService.addBooksFromFile(selectedFile, progress -> Platform.runLater(() -> progressBar.setProgress(progress)))
                    .whenComplete((result, ex) -> {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            if (ex != null) {
                                util.ErrorDialog.showError("Lỗi", "Không thể xử lý file: " + ex.getMessage(), (Stage) addByFileButton.getScene().getWindow());
                                ex.printStackTrace();
                            } else {
                                util.ErrorDialog.showSuccess("Kết quả", "Thành công: " + result.successCount + ", Thất bại: " + result.failureCount, (Stage) addByFileButton.getScene().getWindow());
                            }
                        });
                    });
            } catch (IOException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Lỗi", "Không thể tải màn hình loading: " + e.getMessage(), (Stage) addByFileButton.getScene().getWindow());
            }
        }
    }
}
