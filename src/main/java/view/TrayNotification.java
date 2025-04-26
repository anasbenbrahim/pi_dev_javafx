package view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TrayNotification {
    private final Popup popup;
    private final VBox content;
    private final String title;
    private final String message;
    private final NotificationType type;

    public enum NotificationType {
        SUCCESS, INFO, WARNING, ERROR
    }

    public TrayNotification(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.popup = new Popup();
        this.content = createContent();
        this.popup.getContent().add(content);
    }

    private VBox createContent() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle(getStyleForType(type));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(300);
        box.setMaxWidth(300);

        // Icon
        ImageView icon = new ImageView(getIconForType(type));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(250);

        // Close button
        Label closeLabel = new Label("×");
        closeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-cursor: hand;");
        closeLabel.setOnMouseClicked(e -> popup.hide());

        // Layout
        HBox header = new HBox(10, icon, titleLabel, new Region(), closeLabel);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(header, messageLabel);
        return box;
    }

    private String getStyleForType(NotificationType type) {
        return switch (type) {
            case SUCCESS -> "-fx-background-color: #4CAF50; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";
            case INFO -> "-fx-background-color: #2196F3; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";
            case WARNING -> "-fx-background-color: #FF9800; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";
            case ERROR -> "-fx-background-color: #F44336; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";
        };
    }

    private Image getIconForType(NotificationType type) {
        String iconPath = switch (type) {
            case SUCCESS -> "/images/success.png";
            case INFO -> "/images/info.png";
            case WARNING -> "/images/warning.png";
            case ERROR -> "/images/error.png";
        };
        try {
            return new Image(getClass().getResourceAsStream(iconPath));
        } catch (Exception e) {
            // Fallback to a default image
            return new Image(getClass().getResourceAsStream("/images/info.png"));
        }
    }

    public void showAndDismiss(Stage owner, Duration displayTime) {
        // Position at bottom-right
        double x = owner.getX() + owner.getWidth() - content.getPrefWidth() - 10;
        double y = owner.getY() + owner.getHeight() - content.getPrefHeight() - 10;
        popup.setX(x);
        popup.setY(y);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Pause
        PauseTransition pause = new PauseTransition(displayTime);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), content);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Animation sequence
        SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
        sequence.setOnFinished(e -> popup.hide());
        sequence.play();

        popup.show(owner);
    }
}