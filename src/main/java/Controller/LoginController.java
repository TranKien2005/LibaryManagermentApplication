package Controller;

import java.io.IOException;
import model.Account;
import service.auth.AuthService;
import service.ServiceFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.ErrorDialog;
import QR.*;

public class LoginController {

    @FXML
    protected TextField usernameField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected Button loginButton;

    private static final String DEFAULT_BASE_URL = "http://localhost:8080"; // TODO: centralize

    @FXML
    public void initialize() {
        // ensure app cache is fresh when showing login screen
        service.AppCache.getInstance().clear();
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        AuthService authService = ServiceFactory.getAuthService(DEFAULT_BASE_URL);

        // capture the current stage immediately (before we swap scenes) so we can close it later
        final Stage capturedStage = (Stage) loginButton.getScene().getWindow();

    authService.authenticate(username, password).thenAccept(account -> {
            if (account == null) {
                Platform.runLater(() -> ErrorDialog.showError("Login Error", "Invalid username or password", null));
                return;
            }
            // show loading UI on captured stage
            // show loading UI and proceed to menu
            // clear entire cache and then set current account
            service.AppCache.getInstance().clear();
            service.AppCache.getInstance().setCurrentAccount(account);
            showLoadingAndOpenMenu(account, capturedStage);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> ErrorDialog.showError("Login Error", ex.getMessage(), null));
            return null;
        });
    }

    @FXML
    private void handleRegister() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/register.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Register");

            stage.setScene(scene);
            stage.setHeight(650);
            stage.setWidth(1000);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading register.fxml: " + e.getMessage());
            ErrorDialog.showError("Register Error", e.getMessage(), null);
            e.printStackTrace();
        }
    }

    private void openMenuForAccount(Account account, Stage currentStage) {
        try {
            String fxmlFile = account.getAccountType().equals("User") ? "../view/menuUser.fxml" : "../view/menu.fxml";
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(fxmlFile));

            int accountId = account.getAccountID();
            if (account.getAccountType().equals("User")) {
                menuUserController.setAccountID(accountId);
                menuUserController userLoader = menuUserController.getInstance();
                mainLoader.setController(userLoader);
            } else {
                menuController.setAccountID(accountId);
                menuController menuLoader = menuController.getInstance();
                mainLoader.setController(menuLoader);
            }

            Parent mainRoot = mainLoader.load();
            Scene mainScene = new Scene(mainRoot);
            Stage mainStage = new Stage();
            mainStage.setScene(mainScene);
            mainStage.setTitle("Menu");
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            mainStage.setX((screenBounds.getWidth() - 1500) / 2);
            mainStage.setY((screenBounds.getHeight() - 800) / 2);
            mainStage.setWidth(1500);
            mainStage.setHeight(800);
            mainStage.setResizable(false);
            Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
            if (icon.isError()) {
                System.err.println("Error: Image file not found!");
                return;
            }
            mainStage.getIcons().add(icon);

            if (currentStage != null) {
                currentStage.close();
            }
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            ErrorDialog.showError("Error", e.getMessage(), null);
        }
    }

    /**
     * Show the loading view on the given stage and, after a short delay,
     * open the main menu for the provided account.
     */
    private void showLoadingAndOpenMenu(Account account, Stage stageToReplace) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/loading.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stageToReplace.setScene(scene);
                stageToReplace.setTitle("Loading");
                stageToReplace.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        util.ThreadManager.execute(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> openMenuForAccount(account, stageToReplace));
        });
    }


    private boolean loginInProgress = false;
    public QRScanner qrScanner = QRScanner.getInstance();

    private boolean isValidQRCodeFormat(String qrCodeText) {
        if (qrCodeText == null || !qrCodeText.startsWith("accountID:")) {
            return false;
        }
        try {
            Integer.parseInt(qrCodeText.substring("accountID:".length()).trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleQRLogin() {
        boolean isscanning = (qrScanner != null && qrScanner.isRunning());
        if (isscanning || loginInProgress) {
            return;
        }
        qrScanner.startQRScanner(qrCodeText -> {
            Platform.runLater(() -> {

                if (qrCodeText.startsWith("accountID:")) {
                    try {
                        if (loginInProgress) {
                            return;
                        }
                        if (!isValidQRCodeFormat(qrCodeText)) {
                            ErrorDialog.showError("QR Code Error", "Invalid QR Code format", null);
                            return;
                        }
                        int accountId = Integer.parseInt(qrCodeText.substring("accountID:".length()).trim());
                        loginInProgress = true;
                        qrScanner.stopQRScanner();
                        AuthService authService = ServiceFactory.getAuthService(DEFAULT_BASE_URL);
                        final Stage capturedStage = (Stage) loginButton.getScene().getWindow();
                        authService.authenticateByAccountId(accountId).thenAccept(account -> {
                            if (account == null) {
                                Platform.runLater(() -> ErrorDialog.showError("Login Error", "Account not found", null));
                                loginInProgress = false;
                                return;
                            }
                            // clear entire cache and then set current account
                            service.AppCache.getInstance().clear();
                            service.AppCache.getInstance().setCurrentAccount(account);
                            // show loading UI and proceed to menu
                            showLoadingAndOpenMenu(account, capturedStage);
                            loginInProgress = false;
                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            Platform.runLater(() -> ErrorDialog.showError("Login Error", ex.getMessage(), null));
                            loginInProgress = false;
                            return null;
                        });

                    } catch (NumberFormatException e) {
                        ErrorDialog.showError("QR Code Error", "Invalid account ID", null);
                        e.printStackTrace();
                        loginInProgress = false;
                    } catch (RuntimeException e) {
                        ErrorDialog.showError("QR Code Error", e.getMessage(), null);
                        e.printStackTrace();
                        loginInProgress = false;
                    } catch (Exception e) {
                        ErrorDialog.showError("QR Code Error", e.getMessage(), null);
                        e.printStackTrace();
                        loginInProgress = false;
                    }
                } else {
                    ErrorDialog.showError("QR Code Error", "Invalid QR Code format", null);
                    loginInProgress = false;
                }
            });
        });

    }

}