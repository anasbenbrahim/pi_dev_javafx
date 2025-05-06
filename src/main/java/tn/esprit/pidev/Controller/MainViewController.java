package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import tn.esprit.pidev.Util.NavigationHelper;

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

    @FXML
    private void goToProduitList(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/produit/ProduitList.fxml", "Liste des produits");
    }

    @FXML
    private void goToCategoryList(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/category/CategoryList.fxml", "Liste des catégories");
    }

    @FXML
    private void goToOrderHistory(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/order/OrderHistory.fxml", "Historique des commandes");
    }

    @FXML
    private void goToMarche(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/marche-view.fxml", "Marché");
    }
}
