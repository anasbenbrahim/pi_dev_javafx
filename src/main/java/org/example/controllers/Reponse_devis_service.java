package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.models.Devis;
import org.example.models.Equipements;
import org.example.models.ReponseDevis;
import org.example.services.Reponse_devis;
import org.example.services.Service_equipement;

public class Reponse_devis_service {

    private Parent root;
    private Stage stage;
    private Scene scene;


    private Devis devis;

    @FXML
    private ComboBox<String> disponibiliteCombo;

    @FXML
    private TextField prixField;

    @FXML
    private TextArea reponseField;

    @FXML
    void handleAnnuler(ActionEvent event) {

    }

    @FXML
    void handleValider(ActionEvent event) {

        if(prixField.getText().isEmpty() || reponseField.getText().isEmpty() || disponibiliteCombo.getValue().isEmpty()) {
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
        }
        int etat;
        if(disponibiliteCombo.getValue().equals("Disponible")){
            etat=1;
            Equipements equipements=new Equipements();
            Service_equipement serv=new Service_equipement();
            equipements=serv.recherhche(devis.getEquipement_id());
            equipements.setQuantite(equipements.getQuantite()-devis.getQuantite());
            System.out.println(equipements);
        }
        else{
            etat=0;
        }



        ReponseDevis reponse=new ReponseDevis();
        Reponse_devis service=new Reponse_devis();

        reponse.setDevis(devis.getId());
        reponse.setReponse(reponseField.getText());
        reponse.setPrix(Double.parseDouble(prixField.getText()));
        reponse.setFournisseur_id(devis.getFournisseur_id());
        reponse.setFermier_id(devis.getFermier_id());
        reponse.setEtat(etat);

        try{
            service.ajouter(reponse);
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reponse");
            alert.setContentText("Ajout de reponse avec succes");
            alert.showAndWait();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void initData(Devis devisSelectionne) {
        this.devis = devisSelectionne;
    }
}
