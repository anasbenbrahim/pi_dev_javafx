package tn.esprit.pidev.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Model.Produit;
import tn.esprit.pidev.Service.CategoryService;
import tn.esprit.pidev.Service.ProduitService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProduitController {
    @FXML private FlowPane produitContainer;
    @FXML private Label totalProduitsLabel;
    @FXML private TextField searchField;
    
    private ProduitService produitService = new ProduitService();
    private CategoryService categoryService = new CategoryService();
    private ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private VBox selectedCard = null;
    private Produit selectedProduit = null;

    @FXML
    public void initialize() {
        loadProduits();
        
        // Add search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterProduits(newValue);
            });
        }
    }

    private void filterProduits(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadProduits();
        } else {
            List<Produit> filteredList = produitService.getAllProduits().stream()
                .filter(p -> p.getNomprod().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
            produitList.clear();
            produitList.addAll(filteredList);
            updateProduitCards();
        }
    }

    private void loadProduits() {
        produitList.clear();
        produitList.addAll(produitService.getAllProduits());
        updateProduitCards();
    }

    private void updateProduitCards() {
        produitContainer.getChildren().clear();
        
        for (Produit produit : produitList) {
            VBox card = createProduitCard(produit);
            produitContainer.getChildren().add(card);
        }
        
        // Update total count
        if (totalProduitsLabel != null) {
            totalProduitsLabel.setText(String.valueOf(produitList.size()));
        }
    }

    private VBox createProduitCard(Produit produit) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setSpacing(10);
        card.setMinWidth(250);
        card.setMinHeight(300);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
        
        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            File file = new File(produit.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        // Product info
        Label nameLabel = new Label(produit.getNomprod());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3d5c1e;");

        Label prixLabel = new Label("Prix: " + produit.getPrix() + " DT");
        prixLabel.setStyle("-fx-text-fill: #4a8c24;");

        Label quantiteLabel = new Label("Stock: " + produit.getQuantite() + " kg");
        quantiteLabel.setStyle("-fx-text-fill: #4a8c24;");

        Label statusLabel = new Label("Statut: " + (produit.getStatus() == 1 ? "Disponible" : "Indisponible"));
        statusLabel.setStyle("-fx-text-fill: " + (produit.getStatus() == 1 ? "#5cb85c" : "#d9534f") + ";");

        // Actions buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");

        Button detailsButton = new Button("Détails");
        detailsButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white; -fx-background-radius: 5;");
        detailsButton.setOnAction(e -> showDetails(produit));

        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-background-radius: 5;");
        editButton.setOnAction(e -> handleEditProduit(produit));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-background-radius: 5;");
        deleteButton.setOnAction(e -> handleDeleteProduit(produit));

        buttonBox.getChildren().addAll(detailsButton, editButton, deleteButton);

        VBox infoContainer = new VBox(10);
        infoContainer.setStyle("-fx-padding: 10;");
        infoContainer.getChildren().addAll(nameLabel, prixLabel, quantiteLabel, statusLabel, buttonBox);

        card.getChildren().addAll(imageView, infoContainer);

        // Add selection handling
        card.setOnMouseClicked(e -> {
            selectCard(card, produit);
        });
        
        return card;
    }

    private void selectCard(VBox card, Produit produit) {
        // Reset previous selection
        if (selectedCard != null) {
            selectedCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        }
        
        // Set new selection
        selectedCard = card;
        selectedProduit = produit;
        card.setStyle("-fx-background-color: #e8f4d8; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
    }

    private void showDetails(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Produit");
        alert.setHeaderText(produit.getNomprod());
        
        String details = String.format("""
            ID: %d
            Nom: %s
            Prix: %.2f DT
            Quantité: %d kg
            Description: %s
            Statut: %s
            Catégorie ID: %d
            User ID: %d
            """,
            produit.getId(),
            produit.getNomprod(),
            produit.getPrix(),
            produit.getQuantite(),
            produit.getDescr(),
            produit.getStatus() == 1 ? "Disponible" : "Indisponible",
            produit.getCategoryId(),
            produit.getUserId()
        );
        
        alert.setContentText(details);
        alert.showAndWait();
    }

    @FXML
    private void handleAddProduit() {
        showProduitForm(null);
    }

    @FXML
    private void handleEditProduit() {
        if (selectedProduit != null) {
            handleEditProduit(selectedProduit);
        } else {
            showAlert("Sélection requise", "Aucun produit sélectionné", 
                     "Veuillez sélectionner un produit à modifier.");
        }
    }

    private void handleEditProduit(Produit produit) {
        showProduitForm(produit);
    }

    @FXML
    private void handleDeleteProduit() {
        if (selectedProduit != null) {
            handleDeleteProduit(selectedProduit);
        } else {
            showAlert("Sélection requise", "Aucun produit sélectionné", 
                     "Veuillez sélectionner un produit à supprimer.");
        }
    }

    private void handleDeleteProduit(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + produit.getNomprod() + " ?\n" +
                           "Cette action supprimera également toutes les commandes associées à ce produit.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    produitService.deleteProduit(produit.getId());
                    loadProduits();
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur de suppression", 
                             "Impossible de supprimer le produit. Il est peut-être utilisé dans d'autres tables.");
                }
            }
        });
    }

    private void showProduitForm(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/produit/ProduitForm.fxml"));
            Parent root = loader.load();

            ProduitFormController controller = loader.getController();
            controller.setProduitService(produitService);
            controller.setCategoryService(categoryService);
            controller.initData(produit);

            Stage stage = new Stage();
            stage.setTitle(produit == null ? "Ajouter Produit" : "Modifier Produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            
            // Refresh the list after the form is closed
            stage.setOnHidden(e -> loadProduits());
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}