package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.concurrent.atomic.AtomicBoolean;

public class ErrorDialog {
    // Atomic flag so multiple threads won't open multiple error dialogs concurrently
    private static final AtomicBoolean isErrorDialogShowing = new AtomicBoolean(false);

    /**
     * Show an error dialog that blocks the entire application until dismissed.
     * This uses Modality.APPLICATION_MODAL so the user cannot interact with other windows until the dialog is closed.
     */
    public static void showError(String title, String header, String content, Stage owner) {
        // Attempt to set the flag to true; if already true, don't show another dialog
        if (!isErrorDialogShowing.compareAndSet(false, true)) {
            return;
        }

        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                // Use APPLICATION_MODAL to block input to the whole app, not only the owner window
                alert.initModality(Modality.APPLICATION_MODAL);
                if (owner != null) {
                    alert.initOwner(owner);
                }
                // showAndWait will display the dialog and block until user dismisses it
                alert.showAndWait();
            } finally {
                // Reset flag so future errors can show dialogs
                isErrorDialogShowing.set(false);
            }
        });
    }

    public static void showError(String title, String content, Stage owner) {
        showError(title, null, content, owner);
    }

    /**
     * Informational dialogs keep their original modality (window-modal) so they don't necessarily block the whole app.
     * If you want informational dialogs to also block the whole app, change Modality.WINDOW_MODAL to APPLICATION_MODAL here.
     */
    public static void showSuccess(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                alert.initOwner(owner);
            }
            alert.showAndWait(); // Chờ cho đến khi thông báo được tắt
        });
    }

    public static void showSuccess(String title, String content, Stage owner) {
        showSuccess(title, null, content, owner);
    }
}