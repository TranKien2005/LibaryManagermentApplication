package Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import QR.QRScanner;
import Main.Main;
import data.*;
import googleAPI.BookInfo;
import googleAPI.GoogleApiBookController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.*;
import service.MenuService;
import util.ErrorDialog;
import util.ThreadManager;

public class menuController {
    private static menuController instance;
    private static int accountID;
    public List<Document> bookList = new ArrayList<>();
    public List<User> userList = new ArrayList<>();
    public List<BorrowReturn> borrowReturnList = new ArrayList<>();

    private MyAccountController myAccountController;
    private AddController addController;
    private DeleteController deleteController;
    private EditController editController;
    private MemberManagementController memberManagementController;
    private MenuService menuService;

    public Image defaulImage = new Image(getClass().getResourceAsStream("/images/menu/coverArtUnknown.png"));

    public static int getAccountID() {
        return accountID;
    }

    public static void setAccountID(int accountID) {
        menuController.accountID = accountID;
    }

    public static menuController getInstance() {
        if (instance == null) {
            instance = new menuController();
        }
        return instance;
    }

    public void resetList() {
        menuService.loadInitialData().whenComplete((initialData, ex) -> {
            if (ex != null) {
                Platform.runLater(() -> ErrorDialog.showError("Database Error", ex.getMessage(), null));
            } else {
                this.bookList = initialData.books;
                this.userList = initialData.users;
                this.borrowReturnList = initialData.borrowReturns;
                Platform.runLater(this::populateUI);
            }
        });
    }

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane documentTab;

    @FXML
    private BorderPane managementTab;

    @FXML
    private StackPane managementStackPane;

    @FXML
    private BorderPane borrowAndReturnTab;
    @FXML
    private TableView<Document> tvDocuments;
    @FXML
    private TableColumn<Document, Integer> colId;

    @FXML
    private TableColumn<Document, String> colName;

    @FXML
    private TableColumn<Document, String> colAuthor;

    @FXML
    private TableColumn<Document, String> colCategory;

    @FXML
    private TableColumn<Document, String> colPublisher;

    @FXML
    private TableColumn<Document, Integer> colYear;

    @FXML
    private TableColumn<Document, Integer> colQuantity;

    @FXML
    private TextArea taDocumentDetails;

    @FXML
    private ComboBox<String> cbMembers;

    @FXML
    private ComboBox<String> cbDocuments;

    @FXML
    private TextField tfFilter;
    @FXML
    private DatePicker dpReturnDate;

    @FXML
    private DatePicker dpBorrowDate;

    @FXML
    private TableView<BorrowReturn> tvBorrowedDocuments;

    @FXML
    private TableColumn<BorrowReturn, Integer> colBorrowId;

    @FXML
    private TableColumn<BorrowReturn, String> colMember;

    @FXML
    private TableColumn<BorrowReturn, String> colDocument;

    @FXML
    private TableColumn<BorrowReturn, LocalDate> colBorrowDate;

    @FXML
    private TableColumn<BorrowReturn, LocalDate> colReturnDate;

    @FXML
    private TableColumn<BorrowReturn, String> colStatus;

    @FXML
    private TableColumn<BorrowReturn, LocalDate> colActualReturnDate;

    @FXML
    private TableColumn<BorrowReturn, Double> colDamagePercentage;

    @FXML
    private TableColumn<BorrowReturn, Double> colPenaltyFee;

    @FXML
    private Label scoreLabel;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private Label reviewCountLabel;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbSearchCriteria;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private Label userName;

    @FXML
    private void initialize() {
        instance = this;
        setupServices();
        setupTableColumns();
        setupEventListeners();
        loadSubscenes();

        menuService.getAccountDisplayName(accountID).thenAccept(name -> Platform.runLater(() -> userName.setText(name)));
        resetList();

        dpBorrowDate.setValue(LocalDate.now());
    }

    private void setupServices() {
        menuService = Main.appContainer.getMenuService();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowID"));
        colMember.setCellValueFactory(new PropertyValueFactory<>("member"));
        colDocument.setCellValueFactory(new PropertyValueFactory<>("book"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colActualReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colDamagePercentage.setCellValueFactory(new PropertyValueFactory<>("damagePercentage"));
        colPenaltyFee.setCellValueFactory(new PropertyValueFactory<>("penaltyFee"));
    }

    private void setupEventListeners() {
        tvDocuments.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                displayDocumentDetails(newSelection);
            } else {
                clearDocumentDetails();
            }
        });

        // Simplified ComboBox filtering
        cbMembers.setOnKeyReleased(event -> filterComboBox(cbMembers, userList.stream().map(u -> u.getAccountID() + " - " + u.getFullName()).collect(Collectors.toList())));
        cbDocuments.setOnKeyReleased(event -> filterComboBox(cbDocuments, bookList.stream().map(d -> d.getBookID() + " - " + d.getTitle()).collect(Collectors.toList())));
    }

    private void filterComboBox(ComboBox<String> comboBox, List<String> originalItems) {
        String filter = comboBox.getEditor().getText().toLowerCase();
        ObservableList<String> filteredList = originalItems.stream()
                .filter(item -> item.toLowerCase().contains(filter))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        comboBox.setItems(filteredList);
        comboBox.show();
    }

    private void displayDocumentDetails(Document doc) {
        reviewTextArea.setText(doc.getDescription());
        scoreLabel.setText(String.valueOf(doc.getRating()));
        reviewCountLabel.setText(String.valueOf(doc.getReviewCount()));
        String imageUrl = doc.getCoverImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                Image image = new Image(imageUrl, 120, 180, true, true, true);
                bookCoverImageView.setImage(image);
            } catch (Exception e) {
                bookCoverImageView.setImage(defaulImage);
            }
        } else {
            bookCoverImageView.setImage(defaulImage);
        }
    }

    private void clearDocumentDetails() {
        taDocumentDetails.clear();
        reviewTextArea.clear();
        scoreLabel.setText("");
        reviewCountLabel.setText("");
        bookCoverImageView.setImage(null);
    }

    private void loadSubscenes() {
        try {
            FXMLLoader addBookLoader = new FXMLLoader(getClass().getResource("/view/add.fxml"));
            managementStackPane.getChildren().add(addBookLoader.load());
            addController = addBookLoader.getController();

            FXMLLoader deleteBookLoader = new FXMLLoader(getClass().getResource("/view/delete.fxml"));
            managementStackPane.getChildren().add(deleteBookLoader.load());
            deleteController = deleteBookLoader.getController();

            FXMLLoader editBookLoader = new FXMLLoader(getClass().getResource("/view/edit.fxml"));
            managementStackPane.getChildren().add(editBookLoader.load());
            editController = editBookLoader.getController();

            FXMLLoader manageMembersLoader = new FXMLLoader(getClass().getResource("/view/member_management.fxml"));
            managementStackPane.getChildren().add(manageMembersLoader.load());
            memberManagementController = manageMembersLoader.getController();

            FXMLLoader myAccountLoader = new FXMLLoader(getClass().getResource("/view/myAccount.fxml"));
            stackPane.getChildren().add(myAccountLoader.load());
            myAccountController = myAccountLoader.getController();

            showDocumentListTab();
        } catch (IOException e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
            Platform.exit();
        }
    }

    private void populateUI() {
        tvDocuments.setItems(FXCollections.observableArrayList(bookList));
        tvBorrowedDocuments.setItems(FXCollections.observableArrayList(borrowReturnList));

        List<String> memberNames = userList.stream().map(user -> user.getAccountID() + " - " + user.getFullName()).collect(Collectors.toList());
        cbMembers.setItems(FXCollections.observableArrayList(memberNames));

        List<String> documentTitles = bookList.stream().map(doc -> doc.getBookID() + " - " + doc.getTitle()).collect(Collectors.toList());
        cbDocuments.setItems(FXCollections.observableArrayList(documentTitles));
    }

    @FXML
    private void showDocumentListTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        documentTab.setVisible(true);
        handleReload();
    }

    @FXML
    private void showManagementTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        managementTab.setVisible(true);
        showPane(0);
        handleReload();
    }

    @FXML
    private void showBorrowReturnTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        borrowAndReturnTab.setVisible(true);
        handleReload();
    }

    @FXML
    private void showMyAccountTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        stackPane.getChildren().get(stackPane.getChildren().size() - 1).setVisible(true);
        myAccountController.handleReload();
    }

    public void showPane(int index) {
        for (int i = 0; i < managementStackPane.getChildren().size(); i++) {
            managementStackPane.getChildren().get(i).setVisible(i == index);
        }
    }

    @FXML
    public void handleReload() {
        resetList();
        clearDocumentDetails();
        tvDocuments.getSelectionModel().clearSelection();
        tfSearch.clear();
        cbMembers.getEditor().clear();
        cbDocuments.getEditor().clear();
        dpBorrowDate.setValue(LocalDate.now());
        dpReturnDate.getEditor().clear();

        if (myAccountController != null) myAccountController.handleReload();
        if (memberManagementController != null) memberManagementController.reload();
        if (addController != null) addController.reload();
        if (deleteController != null) deleteController.reload();
        if (editController != null) editController.handleReload();

        menuService.getAccountDisplayName(accountID).thenAccept(name -> Platform.runLater(() -> userName.setText(name)));
    }

    @FXML
    private void handleChangeDescription() {
        Document selectedDocument = tvDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu từ danh sách.", (Stage) tvDocuments.getScene().getWindow());
            return;
        }
        String newDescription = taDocumentDetails.getText().trim();
        menuService.updateDocumentDescription(selectedDocument.getBookID(), newDescription)
                .whenComplete((v, ex) -> Platform.runLater(() -> {
                    if (ex != null) {
                        ErrorDialog.showError("Database Error", ex.getMessage(), (Stage) taDocumentDetails.getScene().getWindow());
                    } else {
                        ErrorDialog.showSuccess("Thành công", "Mô tả đã được thay đổi thành công.", (Stage) taDocumentDetails.getScene().getWindow());
                        handleReload();
                    }
                }));
    }

    @FXML
    private void handleFetchIncorrectInfo() {
        Document selected = tvDocuments.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        ThreadManager.execute(() -> {
            try {
                BookInfo bookinfo = GoogleApiBookController.getBookInfo(selected.getTitle());
                Platform.runLater(() -> {
                    reviewTextArea.setText(bookinfo.getDescription() != null ? bookinfo.getDescription() : "No review available.");
                    scoreLabel.setText(bookinfo.getRating() != null ? bookinfo.getRating() : "No rating available.");
                    reviewCountLabel.setText(bookinfo.getReviewCount() != null ? bookinfo.getReviewCount() : "No review count available.");
                    if (bookinfo.getImageUrl() != null) {
                        bookCoverImageView.setImage(new Image(bookinfo.getImageUrl()));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> ErrorDialog.showError("Error", "Failed to fetch book information: " + e.getMessage(), null));
            }
        });
    }

    @FXML
    public void handleFilterAction() {
        String filterText = tfFilter.getText().toLowerCase().trim();
        ObservableList<Document> filteredDocuments = bookList.stream()
                .filter(doc -> doc.getTitle().toLowerCase().contains(filterText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tvDocuments.setItems(filteredDocuments);

        if (filteredDocuments.isEmpty()) {
            ErrorDialog.showError("Không tìm thấy", "Không có tài liệu nào phù hợp.", (Stage) tvDocuments.getScene().getWindow());
            tvDocuments.setItems(FXCollections.observableArrayList(bookList));
        }
    }

    @FXML
    private void handleBorrowDocument() {
        String docValue = cbDocuments.getValue();
        String memberValue = cbMembers.getValue();
        LocalDate borrowDate = dpBorrowDate.getValue();
        LocalDate returnDate = dpReturnDate.getValue();

        if (docValue == null || memberValue == null || borrowDate == null || returnDate == null) {
            ErrorDialog.showError("Lỗi", "Vui lòng điền đầy đủ thông tin mượn sách.", (Stage) cbDocuments.getScene().getWindow());
            return;
        }

        try {
            int docId = Integer.parseInt(docValue.split(" - ")[0]);
            int memberId = Integer.parseInt(memberValue.split(" - ")[0]);

            menuService.borrowDocument(docId, memberId, borrowDate, returnDate)
                    .whenComplete((v, ex) -> Platform.runLater(() -> {
                        if (ex != null) {
                            ErrorDialog.showError("Error", ex.getMessage(), (Stage) cbDocuments.getScene().getWindow());
                        } else {
                            ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.", (Stage) cbDocuments.getScene().getWindow());
                            handleReload();
                        }
                    }));
        } catch (NumberFormatException e) {
            ErrorDialog.showError("Lỗi", "ID tài liệu hoặc thành viên không hợp lệ.", (Stage) cbDocuments.getScene().getWindow());
        }
    }

    @FXML
    private void handleReturnDocument() {
        BorrowReturn selected = tvBorrowedDocuments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu đã mượn từ bảng để trả.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            return;
        }

        menuService.returnDocument(selected.getBorrowID())
                .whenComplete((v, ex) -> Platform.runLater(() -> {
                    if (ex != null) {
                        ErrorDialog.showError("Error", ex.getMessage(), (Stage) tvBorrowedDocuments.getScene().getWindow());
                    } else {
                        ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.", (Stage) tvBorrowedDocuments.getScene().getWindow());
                        handleReload();
                    }
                }));
    }

    @FXML
    private void handleSearchAction() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String searchCriteria = cbSearchCriteria.getValue();

        if (searchCriteria == null || searchText.isEmpty()) {
            ErrorDialog.showError("Lỗi", "Vui lòng nhập và chọn tiêu chí tìm kiếm.", (Stage) tfSearch.getScene().getWindow());
            return;
        }

        ObservableList<BorrowReturn> filteredBorrows = borrowReturnList.stream()
                .filter(borrow -> {
                    if (searchCriteria.equals("Thành viên")) {
                        return borrow.getMember().toLowerCase().contains(searchText);
                    } else { // "Tài liệu"
                        return borrow.getBook().toLowerCase().contains(searchText);
                    }
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tvBorrowedDocuments.setItems(filteredBorrows);
        if (filteredBorrows.isEmpty()) {
            ErrorDialog.showError("Không tìm thấy", "Không có kết quả nào phù hợp.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            tvBorrowedDocuments.setItems(FXCollections.observableArrayList(borrowReturnList));
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) tvDocuments.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Đăng nhập");
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isHandlingQR = false;
    public QRScanner qrScanner = QRScanner.getInstance();

    @FXML
    private void handleQRCodeScan() {
        if (qrScanner.isRunning() || isHandlingQR) return;
        isHandlingQR = true;

        qrScanner.startQRScanner(qrCodeText -> {
            menuService.processQRCode(qrCodeText).whenComplete((result, ex) -> Platform.runLater(() -> {
                if (ex != null) {
                    ErrorDialog.showError("QR Code Error", ex.getMessage(), null);
                } else if (result instanceof MenuService.UserQRResult res) {
                    if (!borrowAndReturnTab.isVisible()) showBorrowReturnTab();
                    cbMembers.setValue(res.user().getAccountID() + " - " + res.user().getFullName());
                } else if (result instanceof MenuService.BookQRResult res) {
                    if (borrowAndReturnTab.isVisible()) {
                        cbDocuments.setValue(res.document().getBookID() + " - " + res.document().getTitle());
                    } else {
                        showDocumentListTab();
                        tvDocuments.getSelectionModel().select(res.document());
                    }
                } else if (result instanceof MenuService.InvalidQRResult res) {
                    ErrorDialog.showError("QR Code Error", res.error(), null);
                }
                qrScanner.stopQRScanner();
                isHandlingQR = false;
            }));
        });
    }
}
