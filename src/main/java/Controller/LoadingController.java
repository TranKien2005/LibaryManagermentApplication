package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class LoadingController {
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label message;

    @FXML
    private ImageView logo;

    private Timeline dotsTimeline;

    public void initialize() {
        // Any initialization if needed
        // Set logo if available
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
            if (!img.isError()) {
                logo.setImage(img);
                logo.setPreserveRatio(true);
                logo.setSmooth(true);
                // debug: print image and imageView bounds
                System.out.println("Loading logo width=" + img.getWidth() + " height=" + img.getHeight());
                System.out.println("ImageView bounds: " + logo.getBoundsInParent());
            }
        } catch (Exception ignored) {}

        // animate message with dots
        dotsTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (message == null) return;
                String text = message.getText();
                int dots = (text.length() - text.replace(".", "").length());
                if (dots >= 3) {
                    message.setText("Đang xử lý, xin chờ giây lát");
                } else {
                    message.setText(text + ".");
                }
            }
        }));
        dotsTimeline.setCycleCount(Timeline.INDEFINITE);
        dotsTimeline.play();
    }

    public void stopAnimation() {
        if (dotsTimeline != null) dotsTimeline.stop();
    }
}