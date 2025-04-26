package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.models.Equipements;
import org.example.models.Devis;

import org.example.services.Service_devis;

import java.io.IOException;


public class AjoutDevis {
    private Parent root;
    private Scene  scene;
    private Stage stage;

    @FXML
    private Button ajout_devis;

    @FXML
    private TextField champs_quantite;

    @FXML
    private TextArea champs_proposition;

    private Equipements equipement;

    public void setEquipement(Equipements equip){
        this.equipement=equip;
    }


    public void ajout_devis(ActionEvent event) throws IOException {
        Service_devis service_devis=new Service_devis();
        Devis devis=new Devis();
        if(champs_proposition.getText().isEmpty()||champs_proposition.getText().isEmpty()){
            Alert al=new Alert(Alert.AlertType.ERROR);
            al.setTitle("Erreur");
            al.setContentText("Veuillez remplir toutes les champs");
            al.showAndWait();
            return;
        }

        root= FXMLLoader.load(getClass().getResource("/ajout_devis.fxml"));

        //AjoutDevis controller=root.getController();

        devis.setEquipement_id(equipement.getId());
        devis.setQuantite(Integer.parseInt(champs_quantite.getText()));
        devis.setProposition(champs_proposition.getText());
        try {
            service_devis.ajouter(devis);
            Alert al=new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Ajout avec succes");
            al.setContentText("Devis a ete envoye avec succes");
            al.showAndWait();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert al=new Alert(Alert.AlertType.ERROR);
            al.setTitle("Erreur");
            al.setContentText("Veuillez remplir toutes les champs correctement");
            al.showAndWait();
            System.out.println(e.getMessage());
        }
    }

}