package tn.esprit.pidev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/tn/esprit/pidev/view/MainView.fxml"));
        
        // Get screen dimensions
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        
        // Create scene with screen dimensions
        Scene scene = new Scene(fxmlLoader.load(), screenWidth * 0.8, screenHeight * 0.8);
        
        // Configure stage
        stage.setTitle("AgroStock - Système de Gestion Agricole");
        stage.setMaximized(true);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        
        // Set scene to stage
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}