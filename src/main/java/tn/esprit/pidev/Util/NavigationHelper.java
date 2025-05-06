package tn.esprit.pidev.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationHelper {
    public static void navigateTo(Stage stage, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(NavigationHelper.class.getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
