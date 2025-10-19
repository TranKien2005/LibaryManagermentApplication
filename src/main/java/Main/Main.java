package Main;

import java.io.IOException;

import data.AppContainer;
import data.DefaultAppContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.Platform;
import util.ThreadManager;

public class Main extends Application {

    private static Main instance;
    public static AppContainer appContainer;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void start(Stage stage) throws Exception {
        // Initialize the app container
        appContainer = DefaultAppContainer.getInstance();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("Error: FXML file not found!");
                return;
            }
            Scene scene = new Scene(loader.load());
            stage.setTitle("Đăng nhập");
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.getScene().getRoot().setStyle("-fx-border-color: black; -fx-border-width: 2px;");

            // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
            Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
            if (icon.isError()) {
                System.err.println("Error: Image file not found!");
                return;
            }
            stage.getIcons().add(icon);

            stage.setOnCloseRequest(e -> {
                // Try graceful JavaFX exit
                Platform.exit();
                // Ensure background pools will not block
                ThreadManager.shutdown();
                // Fallback hard exit to release terminal when necessary
                System.exit(0);
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        // Called by JavaFX when application is stopping
        try {
            ThreadManager.shutdown();
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        launch(args);

    }
}