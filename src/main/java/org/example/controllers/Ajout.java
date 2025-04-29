package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.models.Category_equipement;
import org.example.models.Equipements;
import org.example.services.Service_category_equipement;
import org.example.services.Service_equipement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ajout {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ComboBox<Category_equipement> comboCategorie;

    @FXML
    private TextField ajout_description;

    @FXML
    private TextField ajout_image;

    @FXML
    private TextField ajout_nom;

    @FXML
    private TextField ajout_prix;

    @FXML
    private TextField ajout_quantite;

    @FXML
    private Button ajouter;

    @FXML
    private Button navigate_modifier;


    @FXML
    private Button navigate_affichage;

    @FXML
    private Button navigate_supprimer;

    @FXML
    private Button btn_categorie;

    @FXML
    private ImageView imageView;

    @FXML
    public void parcourirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            ajout_image.setText(file.getAbsolutePath());
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    public void initialize() {
        Service_category_equipement service_category = new Service_category_equipement();
        ObservableList<Category_equipement> categories = FXCollections.observableArrayList(service_category.getCategories());

        comboCategorie.setItems(categories);

        // Affiche uniquement le type dans le ComboBox
        comboCategorie.setCellFactory(param -> new ListCell<Category_equipement>() {
            @Override
            protected void updateItem(Category_equipement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getType());
                }
            }
        });

        comboCategorie.setButtonCell(new ListCell<Category_equipement>() {
            @Override
            protected void updateItem(Category_equipement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getType());
                }
            }
        });
    }


    @FXML
    void nav_supprimer(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Supprimer.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    void bouton_ajouter(ActionEvent event) {
        Service_equipement service = new Service_equipement();
        Equipements equipement = new Equipements();

        // Vérification des champs
        if (ajout_nom.getText().isEmpty() ||
                ajout_quantite.getText().isEmpty() ||
                ajout_prix.getText().isEmpty() ||
                ajout_description.getText().isEmpty() ||
                ajout_image.getText().isEmpty() ||
                comboCategorie.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        // Remplir l'objet équipement
        equipement.setNom(ajout_nom.getText());
        equipement.setQuantite(Integer.parseInt(ajout_quantite.getText()));
        equipement.setPrix(Integer.parseInt(ajout_prix.getText()));
        equipement.setDescription(ajout_description.getText());
        equipement.setImage(ajout_image.getText());
        equipement.setCategory(comboCategorie.getValue());  // Catégorie sélectionnée

        try {
            // Appel du service
            service.ajouter(equipement);

            // Alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("L'équipement a été ajouté avec succès.");
            alert.showAndWait();

            // Optionnel : vider les champs après ajout
            ajout_nom.clear();
            ajout_quantite.clear();
            ajout_prix.clear();
            ajout_description.clear();
            ajout_image.clear();
            comboCategorie.getSelectionModel().clearSelection();

        } catch (Exception e) {
            Alert al2 = new Alert(Alert.AlertType.ERROR);
            al2.setTitle("Erreur");
            al2.setContentText("Une erreur est survenue : " + e.getMessage());
            al2.showAndWait();
        }
    }

    @FXML
    void nav_affichage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Affichage.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void nav_categorie(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Afficher_category.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
