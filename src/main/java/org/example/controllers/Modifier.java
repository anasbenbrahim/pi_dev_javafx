package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.models.Category_equipement;
import org.example.models.Equipements;
import org.example.services.Service_category_equipement;
import org.example.services.Service_equipement;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class Modifier {

    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML private ComboBox<Category_equipement> comboCategorie;
    @FXML private TextField descriptionMod, idMod, imageMod, nomMod, prixMod, quantiteMod;

    private Equipements currentEquipment;
    private Runnable refreshCallback; // Callback to refresh the table

    @FXML
    public void initialize() {
        Service_category_equipement service_category = new Service_category_equipement();
        ObservableList<Category_equipement> categories = FXCollections.observableArrayList(service_category.getCategories());
        comboCategorie.setItems(categories);

        comboCategorie.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Category_equipement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getType());
            }
        });

        comboCategorie.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category_equipement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getType());
            }
        });
    }

    // Updated to accept a refresh callback
    public void initData(Equipements equipement, Runnable refreshCallback) {
        this.currentEquipment = equipement;
        this.refreshCallback = refreshCallback;

        idMod.setText(String.valueOf(equipement.getId()));
        nomMod.setText(equipement.getNom());
        descriptionMod.setText(equipement.getDescription());
        imageMod.setText(equipement.getImage());
        prixMod.setText(String.valueOf(equipement.getPrix()));
        quantiteMod.setText(String.valueOf(equipement.getQuantite()));

        // Set the category in ComboBox
        if (equipement.getCategory() != null) {
            comboCategorie.setValue(equipement.getCategory());
        }

        idMod.setEditable(false); // Make ID field read-only
    }

    @FXML
    void btnMod(ActionEvent event) {
        try {
            // Update the current equipment object
            currentEquipment.setNom(nomMod.getText());
            currentEquipment.setDescription(descriptionMod.getText());
            currentEquipment.setImage(imageMod.getText());
            currentEquipment.setPrix(Double.parseDouble(prixMod.getText()));
            currentEquipment.setQuantite(Integer.parseInt(quantiteMod.getText()));
            currentEquipment.setCategory(comboCategorie.getValue());

            Service_equipement service = new Service_equipement();
            service.modifier(currentEquipment);

            showAlert("Succès", "Équipement modifié avec succès", Alert.AlertType.INFORMATION);

            // Execute the refresh callback if it exists
            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour le prix et la quantité", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btn_image(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageMod.setText(file.getAbsolutePath());
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    public void nav_affichage(ActionEvent event) throws IOException {
        loadScene(event,"/Affichage.fxml");

    }

    public void nav_supprimer(ActionEvent event) throws IOException {
        loadScene(event,"/Supprimer.fxml");

    }

    public void nav_ajout(ActionEvent event) throws IOException {
        loadScene(event,"/Ajout.fxml");

    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}