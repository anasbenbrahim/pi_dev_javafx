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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.models.Category_equipement;
import org.example.services.Service_category_equipement;

import java.io.IOException;
import java.util.List;

public class AfficherCategory {

    @FXML
    private Button bouton_ajouter;

    @FXML
    private Button bouton_modifier;

    @FXML
    private Button bouton_supprimer;

    @FXML
    private TableColumn<Category_equipement, String> typeCol;

    @FXML
    private TableView<Category_equipement> table_affichage;

    @FXML
    private TextField typeField;

    private final Service_category_equipement service = new Service_category_equipement();
    private ObservableList<Category_equipement> observableList;
    private Category_equipement selectedCategory = null;

    private Parent root;
    private Scene scene;
    private Stage stage;
    @FXML
    void initialize() {
        loadData();

        // Configure table column
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Handle table selection
        table_affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !observableList.isEmpty()) {
                selectedCategory = newSelection;
                typeField.setText(newSelection.getType());
            } else {
                selectedCategory = null;
                typeField.clear();
            }
        });
    }

    private void loadData() {
        List<Category_equipement> list = service.getCategories(); // Using the method that gets IDs
        observableList = FXCollections.observableArrayList(list);
        table_affichage.setItems(observableList);
        System.out.println("Loaded " + list.size() + " categories"); // Debug
    }

    @FXML
    void btn_modifier(ActionEvent event) {
        if (selectedCategory == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une catégorie à modifier.");
            return;
        }

        String newType = typeField.getText().trim();
        if (newType.isEmpty()) {
            showAlert("Champ vide", "Le type ne peut pas être vide.");
            return;
        }

        // Only update if type has changed
        if (!selectedCategory.getType().equals(newType)) {
            System.out.println("Updating category ID " + selectedCategory.getId() +
                    " from '" + selectedCategory.getType() +
                    "' to '" + newType + "'"); // Debug

            selectedCategory.setType(newType);
            service.modifier(selectedCategory);

            // Refresh the table
            table_affichage.refresh();
            showInfo("Succès", "Catégorie modifiée avec succès.");
        } else {
            System.out.println("No changes detected for category ID " + selectedCategory.getId()); // Debug
        }
    }

    @FXML
    void btn_supprimer(ActionEvent event) {
        if (selectedCategory == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie '" +
                selectedCategory.getType() + "'?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                // 1. Supprimer de la base de données
                service.supprimer(selectedCategory);

                // 2. Supprimer de l'ObservableList
                observableList.remove(selectedCategory);

                // 3. Réinitialiser la sélection
                table_affichage.getSelectionModel().clearSelection();

                // 4. Nettoyer les champs
                typeField.clear();
                selectedCategory = null;

                // 5. Rafraîchir la table
                table_affichage.refresh();

                showInfo("Succès", "Catégorie supprimée avec succès.");
            } catch (Exception e) {
                showAlert("Erreur", "La suppression a échoué : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btn_ajouter(ActionEvent event) {
        String type = typeField.getText().trim();
        if (type.isEmpty()) {
            showAlert("Champ vide", "Le type ne peut pas être vide.");
            return;
        }

        // Check for duplicate
        if (observableList.stream().anyMatch(c -> c.getType().equalsIgnoreCase(type))) {
            showAlert("Existe déjà", "Une catégorie avec ce type existe déjà.");
            return;
        }

        System.out.println("Adding new category: " + type); // Debug

        Category_equipement nouvelleCategorie = new Category_equipement(type);
        service.ajouter(nouvelleCategorie);

        // Reload data to get the generated ID
        loadData();
        typeField.clear();
        showInfo("Succès", "Catégorie ajoutée avec succès.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void nav_dashboard(ActionEvent event) throws IOException {
        loadScene(event,"/Affichage.fxml");
    }

    public void nav_equipement(ActionEvent event) throws IOException {
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