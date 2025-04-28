package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
    
    @FXML
    private Button marcheButton;

    @FXML
    private void navigateToMarche() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/marche-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) marcheButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
