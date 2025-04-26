package org.example;

import Controller.AfficherPublicationController;
import Controller.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize NavigationManager
        NavigationManager navigationManager = NavigationManager.getInstance(primaryStage);

        // Load the initial view (AfficherPublication.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPublication.fxml"));
        Parent root = loader.load();
        AfficherPublicationController controller = loader.getController();
        controller.setNavigationManager(navigationManager);

        // Set the initial view
        navigationManager.setInitialView(root);

        // Set stage properties
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(50);
        primaryStage.setY(50);
        primaryStage.setWidth(screenBounds.getWidth() - 100);
        primaryStage.setHeight(screenBounds.getHeight() - 100);
        primaryStage.setTitle("List des publications");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}