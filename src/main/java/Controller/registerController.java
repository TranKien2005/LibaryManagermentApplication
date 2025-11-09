package Controller;

import java.io.IOException;
import Main.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.register.RegisterService;

public class registerController {

    @FXML
    protected TextField usernameField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected PasswordField confirmPasswordField;

    @FXML
    protected TextField fullnameField;

    @FXML
    protected TextField emailField;

    @FXML
    protected TextField phoneField;

    @FXML
    protected ComboBox<String> accountTypeComboBox;

    @FXML
    protected Button registerButton;

    private final RegisterService registerService;

    public registerController() {
        this.registerService = Main.appContainer.getRegisterService();
    }

    @FXML
    protected void handleRegister() {
        try {
            String username = usernameField.getText();

            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String fullName = fullnameField.getText();

            String email = emailField.getText();
            String phone = phoneField.getText();
            String accountType = accountTypeComboBox.getValue();

            Stage window = (Stage) registerButton.getScene().getWindow();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || accountType == null) {
                util.ErrorDialog.showError("Registration Failed", "All fields must be filled out.", window);
                return;
            }

            if (!password.equals(confirmPassword)) {
                util.ErrorDialog.showError("Registration Failed", "Passwords do not match.", window);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                util.ErrorDialog.showError("Registration Failed", "Invalid email format.", window);
                return;
            }

            // Delegate whole registration flow to service (UI only handles dialogs / navigation)
            registerService.registerNewAccount(username, password, confirmPassword, accountType, fullName, email, phone)
                    .thenAccept(new java.util.function.Consumer<model.Account>() {
                        @Override
                        public void accept(model.Account createdAccount) {
                            Platform.runLater(() -> {
                                util.ErrorDialog.showSuccess("Registration Successful", "Account created successfully. You can now log in.", window);
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/login.fxml"));
                                    Parent loginRoot = loader.load();
                                    Stage stage = window;
                                    stage.setTitle("Đăng nhập");
                                    stage.getScene().setRoot(loginRoot);
                                    stage.setWidth(1000);
                                    stage.setHeight(600);
                                    stage.centerOnScreen();
                                    stage.setMaximized(false);
                                    stage.setResizable(false);
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    util.ErrorDialog.showError("Lỗi không xác định", e.getMessage(), window);
                                }
                            });
                        }
                    }).exceptionally(ex -> {
                        Platform.runLater(() -> util.ErrorDialog.showError("Registration Failed", ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(), window));
                        ex.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", e.getMessage() != null ? e.getMessage() : e.toString(), null);
        }
    }

    @FXML
    protected void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("Đăng nhập");
            stage.getScene().setRoot(loginRoot);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi không xác định", e.getMessage(),
                    (Stage) registerButton.getScene().getWindow());
        }
    }

}