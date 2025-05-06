package tn.esprit.pidev.Controller;
import tn.esprit.pidev.Util.NavigationHelper;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Model.Produit;
import tn.esprit.pidev.Service.CategoryService;
import tn.esprit.pidev.Service.ProduitService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ProduitController {
    @FXML private FlowPane produitFlow;
    @FXML private Label totalProduitsLabel;
    @FXML private TextField searchField;
    @FXML private Label inStockLabel;
    @FXML private Label outStockLabel;
    @FXML private Label totalCategoriesLabel;
    @FXML private HBox ruptureNotification;
    @FXML private VBox ruptureToast;
    @FXML private Label ruptureListLabel;
    @FXML private Button closeToastBtn;
    @FXML private Button notifBellBtn;
    @FXML private VBox notifListPane;
    @FXML private ListView<String> notifListView;
    @FXML private Button closeNotifPaneBtn;

    private ProduitService produitService = new ProduitService();
    private ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private CategoryService categoryService = new CategoryService();

    // Liste des notifications
    private final ObservableList<String> notificationHistory = FXCollections.observableArrayList();

    @FXML
    private void navigateToCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/category/CategoryList.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) produitFlow.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Produit selectedProduit;

    @FXML
    public void initialize() {
        System.out.println("Resource found? " + (getClass().getResource("/tn/esprit/pidev/icons/dashboard.png") != null));
        System.out.println("Resource found (no slash)? " + (getClass().getResource("tn/esprit/pidev/icons/dashboard.png") != null));
        loadProduits();

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterProduits(newValue);
            });
        }
        // Affichage notification rupture de stock
        checkRuptureStock();
        // Ajout du handler pour fermeture manuelle du toast
        if (closeToastBtn != null && ruptureToast != null) {
            closeToastBtn.setOnAction(e -> {
                // Animation de fermeture
                javafx.animation.FadeTransition ftOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), ruptureToast);
                ftOut.setFromValue(1.0);
                ftOut.setToValue(0);
                ftOut.setOnFinished(event -> {
                    ruptureToast.setVisible(false);
                    ruptureToast.setOpacity(1.0);
                });
                ftOut.play();
            });
        }
        // Init cloche et panneau historique
        if (notifListView != null) notifListView.setItems(notificationHistory);
        if (notifBellBtn != null && notifListPane != null) {
            notifBellBtn.setOnAction(e -> {
                if (!notifListPane.isVisible()) {
                    // Afficher avec animation
                    notifListPane.setVisible(true);
                    notifListPane.setOpacity(0);
                    javafx.animation.FadeTransition ftIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), notifListPane);
                    ftIn.setFromValue(0);
                    ftIn.setToValue(1.0);
                    ftIn.play();
                } else {
                    // Cacher avec animation
                    javafx.animation.FadeTransition ftOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), notifListPane);
                    ftOut.setFromValue(1.0);
                    ftOut.setToValue(0);
                    ftOut.setOnFinished(event -> notifListPane.setVisible(false));
                    ftOut.play();
                }
            });
        }

        // Handler pour le bouton de fermeture du panneau de notifications
        if (closeNotifPaneBtn != null && notifListPane != null) {
            closeNotifPaneBtn.setOnAction(e -> {
                javafx.animation.FadeTransition ftOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), notifListPane);
                ftOut.setFromValue(1.0);
                ftOut.setToValue(0);
                ftOut.setOnFinished(event -> notifListPane.setVisible(false));
                ftOut.play();
            });
        }
    }

    private void loadProduits() {
        produitList.setAll(produitService.getAllProduits());
        if (totalProduitsLabel != null) totalProduitsLabel.setText(String.valueOf(produitList.size()));
        updateSummaryCards();
        displayProduitCards(produitList);
    }

    private void filterProduits(String searchText) {
        List<Produit> base = produitService.getAllProduits();
        List<Produit> filtered = (searchText == null || searchText.isEmpty()) ? base :
            base.stream().filter(p -> p.getNomprod().toLowerCase().contains(searchText.toLowerCase())).toList();
        displayProduitCards(FXCollections.observableArrayList(filtered));
    }

    private void displayProduitCards(ObservableList<Produit> produits) {
        if (produitFlow == null) return;
        produitFlow.getChildren().clear();
        for (Produit p : produits) {
            VBox card = new VBox(10);
            card.getStyleClass().add("agri-card");
            card.setPadding(new Insets(16));
            card.setPrefWidth(180);
            card.setStyle("-fx-alignment: center;");

            ImageView imageView = new ImageView();
            if (p.getImage() != null && !p.getImage().isEmpty()) {
                try {
                    Image img = new Image(new java.io.File(p.getImage()).toURI().toString(), 82, 82, true, true);
                    imageView.setImage(img);
                } catch (Exception e) { /* fallback */ }
            }
            imageView.setFitWidth(82);
            imageView.setFitHeight(82);

            Label name = new Label(p.getNomprod());
            name.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #388e3c;");
            Label price = new Label(String.format("Prix: %.2f DT", p.getPrix()));
            price.setStyle("-fx-font-size: 15px; -fx-text-fill: #607d3b;");
            Label quant = new Label("Stock: " + p.getQuantite());
            quant.setStyle("-fx-font-size: 15px; -fx-text-fill: #607d3b;");
            Label cat = new Label("Catégorie: " + (categoryService.getCategoryById(p.getCategoryId()) != null ? categoryService.getCategoryById(p.getCategoryId()).getName() : ""));
            cat.setStyle("-fx-font-size: 13px; -fx-text-fill: #7e57c2;");

            card.getChildren().addAll(imageView, name, price, quant, cat);
            card.setOnMouseClicked(event -> {
                if (selectedProduit != null) {
                    produitFlow.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            node.setStyle("-fx-background-color: #f1f8e9; -fx-background-radius: 16; -fx-alignment: center;");
                        }
                    });
                }
                selectedProduit = p;
                card.setStyle("-fx-background-color: #c8e6c9; -fx-background-radius: 16; -fx-alignment: center;");
                System.out.println("Selected product: " + selectedProduit.getNomprod());
            });
            produitFlow.getChildren().add(card);
        }
        // Mettre à jour la notification après affichage
        checkRuptureStock();
    }

    private void updateSummaryCards() {
        int inStock = (int) produitList.stream().filter(p -> p.getStatus() == 1 && p.getQuantite() > 0).count();
        int outStock = (int) produitList.stream().filter(p -> p.getStatus() == 0 || p.getQuantite() == 0).count();
        int totalCat = (int) produitList.stream().map(Produit::getCategoryId).distinct().count();
        if (inStockLabel != null) inStockLabel.setText(String.valueOf(inStock));
        if (outStockLabel != null) outStockLabel.setText(String.valueOf(outStock));
        if (totalCategoriesLabel != null) totalCategoriesLabel.setText(String.valueOf(totalCat));
    }

    private void checkRuptureStock() {
        if (ruptureToast != null && ruptureListLabel != null) {
            List<Produit> ruptures = produitList.stream()
                .filter(p -> p.getQuantite() == 0)
                .toList();
            if (!ruptures.isEmpty()) {
                // Ajoute une notification pour chaque produit nouvellement en rupture
                for (Produit p : ruptures) {
                    String notifMsg = String.format("%s (rupture à %s)", p.getNomprod(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    if (notificationHistory.stream().noneMatch(msg -> msg.contains(p.getNomprod()))) {
                        notificationHistory.add(0, notifMsg);
                    }
                }
                ruptureListLabel.setText(ruptures.stream().map(Produit::getNomprod).reduce((a,b) -> a+"\n"+b).orElse(""));
                showRuptureToast();
            } else {
                ruptureToast.setVisible(false);
            }
        }
    }

    private void showRuptureToast() {
        // Préparation du toast
        ruptureToast.setVisible(true);
        ruptureToast.setOpacity(0);

        // Animation d'entrée: slide + fade in
        javafx.animation.ParallelTransition parallelIn = new javafx.animation.ParallelTransition();

        // Animation de fondu
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), ruptureToast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);

        // Animation de déplacement
        javafx.animation.TranslateTransition slideIn = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(400), ruptureToast);
        slideIn.setFromX(50); // Commence légèrement décalé
        slideIn.setToX(0);    // Revient à sa position normale

        // Combiner les animations
        parallelIn.getChildren().addAll(fadeIn, slideIn);

        // Animation de sortie automatique après délai
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), ruptureToast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(javafx.util.Duration.seconds(6)); // Délai plus long pour laisser le temps de lire
        fadeOut.setOnFinished(e -> {
            ruptureToast.setVisible(false);
            ruptureToast.setOpacity(1.0);
            ruptureToast.setTranslateX(0); // Réinitialiser la position
        });

        // Jouer les animations en séquence
        parallelIn.setOnFinished(e -> fadeOut.play());
        parallelIn.play();
    }

    @FXML
    private void handleAddProduit() { showProduitForm(null); }


    @FXML
    private void handleEditProduit() {
        if (selectedProduit != null) handleEditProduit(selectedProduit);
        else showAlert("Sélection requise", "Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
    }

    private void handleEditProduit(Produit produit) { showProduitForm(produit); }

    @FXML
    private void handleDeleteProduit() {
        if (selectedProduit != null) handleDeleteProduit(selectedProduit);
        else showAlert("Sélection requise", "Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer.");
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
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur de suppression", "Impossible de supprimer le produit. Il est peut-être utilisé dans d'autres tables.");
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
            stage.setOnHidden(e -> loadProduits());
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadProduits();
    }

    @FXML
    private void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter Produits CSV");
        fileChooser.setInitialFileName("produits.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("Nom,Image,Prix,Quantite,Catégorie,Statut");
                for (Produit p : produitList) {
                    String cat = Optional.ofNullable(categoryService.getCategoryById(p.getCategoryId())).map(Category::getName).orElse("");
                    String status = (p.getStatus() == 1 ? "Disponible" : "Indisponible");
                    writer.printf("\"%s\",\"%s\",%s,%s,\"%s\",%s\n",
                        p.getNomprod().replaceAll("\"", ""),
                        p.getImage() != null ? p.getImage() : "",
                        p.getPrix(),
                        p.getQuantite(),
                        cat.replaceAll("\"", ""),
                        status
                    );
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur d'export", e.getMessage());
            }
        }
    }

    @FXML
    private void goToProduitForm(javafx.event.ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/produit/ProduitForm.fxml", "Ajouter/Modifier Produit");
    }

    @FXML
    private void goToMainView(javafx.event.ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/MainView.fxml", "Accueil");
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
