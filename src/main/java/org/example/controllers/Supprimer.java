package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.models.Equipements;
import org.example.services.Service_equipement;

import java.io.IOException;

public class Supprimer {

    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML
    private Button btn_supprimer;

    @FXML
    private TextField champ_supprimer;
    @FXML
    private Button retour_affichage;


    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void btn_retour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Ajout.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    void supprimer(ActionEvent event) {
        String id=champ_supprimer.getText().trim();
        if(id.isEmpty()){
            showWarningAlert("Veuiller inserer l'id ");
            return;
        }

        int equipement_id=Integer.parseInt(id);
        Equipements equipement=new Equipements();
        Service_equipement service= new Service_equipement();
        equipement.setId(equipement_id);
        if(service.recherhche(equipement_id)==null)
        {
            showErrorAlert("Cet equipement n'existe pas ");
            return;
        }
        try{
            service.supprimer(equipement);
            showInfoAlert("Equipement supprime");
        }
        catch (NumberFormatException e){
            showErrorAlert(e.getMessage());
        }
        catch (Exception e){
            showErrorAlert(e.getMessage());

        }
    }

    public void nav_categorie(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Afficher_category.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void nav_affichage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Affichage.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }




}
