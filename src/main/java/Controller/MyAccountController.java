package Controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.google.zxing.WriterException;

import Main.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Account;
import model.Manager;
import model.User;
import service.MyAccountService;
import util.ErrorDialog;

public class MyAccountController {

    private int accountID;
    @FXML
    private ImageView userIcon;

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final MyAccountService myAccountService;

    public MyAccountController() {
        this.myAccountService = Main.appContainer.getMyAccountService();
    }

    @FXML
    public void initialize() {

        try {
            if (menuUserController.getAccountID() != 0) {
                accountID = menuUserController.getAccountID();
                User user = myAccountService.getUser(accountID);
                Account account = myAccountService.getAccount(accountID);
                fullnameField.setText(user.getFullName());
                phoneField.setText(user.getPhone());
                emailField.setText(user.getEmail());
                passwordField.setText(account.getPassword());
                userIcon.setImage(new Image(QR.CreateQRCode.generateQRCode("accountID: " + accountID)));
            } else {
                menuController.getInstance();
                accountID = menuController.getAccountID();
                Manager user = myAccountService.getManager(accountID);
                Account account = myAccountService.getAccount(accountID);
                fullnameField.setText(user.getFullName());
                phoneField.setText(user.getPhone());
                emailField.setText(user.getEmail());
                passwordField.setText(account.getPassword());
                userIcon.setImage(new Image(QR.CreateQRCode.generateQRCode("accountID: " + accountID)));
            }

        } catch (SQLException e) {
            ErrorDialog.showError("SQL Error", e.getMessage(), null);
            e.printStackTrace();
        } catch (WriterException | IOException e) {
            ErrorDialog.showError("Error", e.getMessage(), null);
            e.printStackTrace();
        }
    }

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleUserIconClick(MouseEvent event) {
        try {
            Image image = userIcon.getImage();
            if (image != null) {
                File outputFile = new File(System.getProperty("user.home") + "/Downloads/QRCodeAccount.png");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
                util.ErrorDialog.showSuccess("Success", "QR code has been saved to Downloads folder.", null);
            } else {
                throw new Exception("QR code not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi tải mã QR.", null);
        }

    }

    private boolean validateInputFields() {
        if (fullnameField.getText().isEmpty() || phoneField.getText().isEmpty() || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            ErrorDialog.showError("Validation Error", "Please fill in all fields.",
                    (Stage) fullnameField.getScene().getWindow());
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            ErrorDialog.showError("Validation Error", "Passwords do not match.",
                    (Stage) fullnameField.getScene().getWindow());
            return false;
        }
        return true;
    }

    @FXML
    private void handleUpdate() {
        // Validate input fields
        if (!validateInputFields()) {
            return;
        }

        // Update user object with new values

        try {
            if (menuUserController.getAccountID() != 0) {
                myAccountService.updateUser(accountID, fullnameField.getText(), phoneField.getText(), emailField.getText(), passwordField.getText());
            } else {
                myAccountService.updateManager(accountID, fullnameField.getText(), phoneField.getText(), emailField.getText(), passwordField.getText());
            }

            ErrorDialog.showSuccess("Success", "Account updated successfully.",
                    (Stage) fullnameField.getScene().getWindow());
            if (menuUserController.getAccountID() != 0) {
                menuUserController.getInstance().reload();
            } else {
                menuController.getInstance().handleReload();
            }
        } catch (SQLException e) {
            ErrorDialog.showError("SQL Error", e.getMessage(), (Stage) fullnameField.getScene().getWindow());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            ErrorDialog.showError("Error", e.getMessage(), (Stage) fullnameField.getScene().getWindow());
            e.printStackTrace();
        }

    }

    @FXML
    private void handleCancel() {
        fullnameField.clear();
        phoneField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        initialize();
    }

    public void handleReload() {
        handleCancel();
    }
}