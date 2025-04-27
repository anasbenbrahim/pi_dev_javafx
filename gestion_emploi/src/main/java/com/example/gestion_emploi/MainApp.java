package com.example.gestion_emploi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import controllers.MainController;

public class MainApp extends Application {
    // Use the exact path matching your project structure
    private static final String MAIN_VIEW = "/com/example/gestion_emploi/Views/main-view.fxml";
    private static final String APP_TITLE = "Gestion Emploi - CRUD Application";

    @Override
    public void start(Stage primaryStage) {
        try {
            // Debug: Print the resource path
            System.out.println("Loading FXML from: " + MAIN_VIEW);
            System.out.println("Resource exists: " +
                    (getClass().getResource(MAIN_VIEW) != null));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
            Parent root = loader.load();

            // Set up primary stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.show();

        } catch (Exception e) {
            showErrorAndExit("Startup Error", "Failed to initialize application", e);
        }
    }

    private void showErrorAndExit(String title, String message, Exception e) {
        System.err.println(title + ": " + message);
        e.printStackTrace();

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Fatal Error");
            alert.setContentText(message + "\n\n" + e.getMessage());
            alert.showAndWait();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        // Verify resources are in classpath
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        launch(args);
    }
}
