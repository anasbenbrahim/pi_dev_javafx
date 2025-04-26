package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.models.Equipements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Devis {

    private Parent root;
    private Scene scene;
    private Stage stage;

    private Equipements equipements;

    @FXML private ImageView equipmentImage;
    @FXML private Label equipmentName;
    @FXML private Text equipmentDescription;
    @FXML private Text equipmentSpecs;
    @FXML private Button backButton; // Doit correspondre au fx:id dans FXML

    @FXML
    public void initialize() {
        // Initialisation du bouton retour
        if (equipmentDescription != null && equipmentDescription.getStyle().contains("-fx-text")) {
            equipmentDescription.setStyle("-fx-fill: #333333; -fx-font-size: 14px;");
        }
        if (equipmentSpecs != null && equipmentSpecs.getStyle().contains("-fx-text")) {
            equipmentSpecs.setStyle("-fx-fill: #333333; -fx-font-size: 14px;");
        }
    }

    public void setEquipement(Equipements equip) {
        if (equip == null) return;

        this.equipements = equip;
        equipmentName.setText(equip.getNom());
        equipmentDescription.setText(equip.getDescription());
        equipmentSpecs.setText(String.format("Prix: %.2f €\nQuantité: %d",
                equip.getPrix(), equip.getQuantite()));

        // Chargement amélioré de l'image
        loadEquipmentImage(equip.getImage());
    }

    private void loadEquipmentImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                loadDefaultImage();
                return;
            }

            // Normalisation du chemin pour Windows
            imagePath = imagePath.replace("\\", "/");

            if (imagePath.startsWith("http")) {
                // Chargement depuis URL
                Image image = new Image(imagePath, true);
                image.errorProperty().addListener((obs, wasError, isNowError) -> {
                    if (isNowError) loadDefaultImage();
                });
                equipmentImage.setImage(image);
            }
            else if (new File(imagePath).exists()) {
                // Chargement depuis système de fichiers absolu
                equipmentImage.setImage(new Image(new File(imagePath).toURI().toString()));
            }
            else {
                // Chargement depuis ressources
                String resourcePath = imagePath.startsWith("/") ? imagePath : "/images/" + imagePath;
                InputStream is = getClass().getResourceAsStream(resourcePath);

                if (is != null) {
                    equipmentImage.setImage(new Image(is));
                } else {
                    System.out.println("[DEBUG] Chemin testé: " + resourcePath);
                    loadDefaultImage();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur critique chargement image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/default.png");
            if (is != null) {
                equipmentImage.setImage(new Image(is));
            } else {
                equipmentImage.setVisible(false); // Cache l'ImageView si l'image par défaut est manquante
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image par défaut: " + e.getMessage());
        }
    }

    public void demande(ActionEvent event) throws IOException {

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        try {
            // Charger le FXML en tant qu'URL
            URL fxmlUrl = getClass().getResource("/ajout_devis.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Fichier FXML introuvable");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            root = loader.load();

            AjoutDevis controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Controller non initialisé");
            }
            controller.setEquipement(equipements);
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
                e.printStackTrace();
                //("Erreur Critique", "Échec du chargement", e.toString());
            }
    }

    public void openAjoutDevisWindow(Equipements equipement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout_devis.fxml"));
            Parent root = loader.load();

            // Initialiser le contrôleur
            AjoutDevis controller = loader.getController();
            controller.setEquipement(equipement);

            // Créer la scène
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Devis");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            //showAlert("Erreur", "Impossible d'ouvrir la fenêtre", Alert.AlertType.ERROR);
        }
    }

    public void bouton_retour(ActionEvent event) throws IOException {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}