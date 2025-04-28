package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import tn.esprit.pidev.Model.*;
import tn.esprit.pidev.Service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class MarcheController implements Initializable {
    @FXML
    private GridPane produitsGrid;
    
    @FXML
    private ListView<PanierItem> panierListView;
    
    @FXML
    private Label totalLabel;

    @FXML
    private TextField filterNameField;
    @FXML
    private ComboBox<String> filterCategoryBox;
    @FXML
    private TextField filterPriceMin;
    @FXML
    private TextField filterPriceMax;

    private static final int PRODUCTS_PER_ROW = 3;

    private Map<Integer, PanierItem> panier = new HashMap<>();
    private ObservableList<PanierItem> panierItems = FXCollections.observableArrayList();
    private ObservableList<Produit> allProduits = FXCollections.observableArrayList();
    private ObservableList<String> allCategories = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();
    private OrderDAO orderDAO = new OrderDAO();
    private CategoryService categoryService = new CategoryService();
    private Map<Integer, String> categoryIdToName = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupFilters();
        loadAndDisplayProduits();
        setupPanierListView();
    }

    private void loadAndDisplayProduits() {
        allProduits.clear();
        List<Produit> produits = produitService.getAllProduits();
        allProduits.addAll(produits);
        filterAndDisplayProduits();
    }

    private void chargerProduits() {
        // DEPRECATED: replaced by loadAndDisplayProduits
    }

    private void setupFilters() {
        // Populate categories dynamically
        allCategories.clear();
        categoryIdToName.clear();
        allCategories.add("Toutes");
        for (Category cat : categoryService.getAllCategories()) {
            allCategories.add(cat.getName());
            categoryIdToName.put(cat.getId(), cat.getName());
        }
        filterCategoryBox.setItems(allCategories);
        filterCategoryBox.setValue("Toutes");

        // Real-time listeners
        filterNameField.textProperty().addListener((obs, ov, nv) -> filterAndDisplayProduits());
        filterCategoryBox.valueProperty().addListener((obs, ov, nv) -> filterAndDisplayProduits());
        filterPriceMin.textProperty().addListener((obs, ov, nv) -> filterAndDisplayProduits());
        filterPriceMax.textProperty().addListener((obs, ov, nv) -> filterAndDisplayProduits());
    }

    private void filterAndDisplayProduits() {
        String nameFilter = filterNameField.getText() != null ? filterNameField.getText().toLowerCase().trim() : "";
        String catFilter = filterCategoryBox.getValue();
        String minStr = filterPriceMin.getText();
        String maxStr = filterPriceMax.getText();
        double min = 0, max = Double.MAX_VALUE;
        try { if (!minStr.isEmpty()) min = Double.parseDouble(minStr); } catch (Exception ignored) {}
        try { if (!maxStr.isEmpty()) max = Double.parseDouble(maxStr); } catch (Exception ignored) {}

        produitsGrid.getChildren().clear();
        produitsGrid.getRowConstraints().clear();
        produitsGrid.getColumnConstraints().clear();

        int col = 0, row = 0;
        int count = 0;
        for (Produit produit : allProduits) {
            boolean match = true;
            String prodCatName = categoryIdToName.getOrDefault(produit.getCategoryId(), "");
            String nom = produit.getNomprod() != null ? produit.getNomprod().toLowerCase() : "";
            String descr = produit.getDescr() != null ? produit.getDescr().toLowerCase() : "";
            if (!nameFilter.isEmpty() && !(nom.contains(nameFilter) || descr.contains(nameFilter))) match = false;
            if (catFilter != null && !catFilter.equals("Toutes") && !prodCatName.equals(catFilter)) match = false;
            if (produit.getPrix() < min || produit.getPrix() > max) match = false;
            if (match) {
                VBox card = createProductCard(produit);
                produitsGrid.add(card, col, row);
                col++;
                count++;
                if (col == PRODUCTS_PER_ROW) {
                    col = 0; row++;
                }
            }
        }
        if (count == 0) {
            Label emptyLabel = new Label("Aucun produit trouvé");
            emptyLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #888; -fx-padding: 40;");
            produitsGrid.add(emptyLabel, 0, 0);
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(12);
        card.getStyleClass().add("product-card");
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #fff; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.13), 10, 0, 0, 2); -fx-alignment: center;");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(new File(produit.getImage()).toURI().toString());
            imageView.setImage(image);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            imageView.setImage(new Image("/tn/esprit/pidev/view/img/no-image.png"));
        }

        Label nomLabel = new Label(produit.getNomprod());
        nomLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #388e3c;");
        Label prixLabel = new Label(String.format("%.2f DT", produit.getPrix()));
        prixLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #2e7d32;");
        Label descrLabel = new Label(produit.getDescr());
        descrLabel.setWrapText(true);
        descrLabel.setMaxWidth(140);
        descrLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555;");
        Button addBtn = new Button("Ajouter au panier");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #43a047, #66bb6a); -fx-text-fill: white; -fx-background-radius: 22; -fx-padding: 7 18; -fx-font-size: 15; -fx-cursor: hand; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> ajouterAuPanier(produit));
        card.getChildren().addAll(imageView, nomLabel, prixLabel, descrLabel, addBtn);
        return card;
    }

    private void setupPanierListView() {
        panierListView.setItems(panierItems);
        panierListView.setCellFactory(lv -> new ListCell<PanierItem>() {
            @Override
            protected void updateItem(PanierItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cell = new HBox(10);
                    Label nameLabel = new Label(item.getProduit().getNomprod());
                    Label quantityLabel = new Label("x" + item.getQuantite());
                    Label priceLabel = new Label(String.format("%.2fDT", item.getTotal()));
                    
                    Button plusBtn = new Button("+");
                    Button minusBtn = new Button("-");
                    
                    plusBtn.setOnAction(e -> ajouterAuPanier(item.getProduit()));
                    minusBtn.setOnAction(e -> retirerDuPanier(item.getProduit()));
                    
                    cell.getChildren().addAll(nameLabel, quantityLabel, priceLabel, minusBtn, plusBtn);
                    setGraphic(cell);
                }
            }
        });
    }

    public void ajouterAuPanier(Produit produit) {
        if (panier.containsKey(produit.getId())) {
            PanierItem item = panier.get(produit.getId());
            if (item.getQuantite() < produit.getQuantite()) {
                item.setQuantite(item.getQuantite() + 1);
                updateTotal();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Stock insuffisant");
                alert.setHeaderText(null);
                alert.setContentText("Il n'y a plus de stock disponible pour ce produit.");
                alert.showAndWait();
            }
        } else {
            PanierItem newItem = new PanierItem(produit, 1);
            panier.put(produit.getId(), newItem);
            panierItems.add(newItem);
            updateTotal();
        }
    }

    public void retirerDuPanier(Produit produit) {
        PanierItem item = panier.get(produit.getId());
        if (item != null) {
            if (item.getQuantite() > 1) {
                item.setQuantite(item.getQuantite() - 1);
            } else {
                panier.remove(produit.getId());
                panierItems.remove(item);
            }
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = panierItems.stream()
            .mapToDouble(PanierItem::getTotal)
            .sum();
        totalLabel.setText(String.format("Total: %.2fDT", total));
    }

    @FXML
    private void validerPanier() {
        if (panierItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Panier vide");
            alert.setHeaderText(null);
            alert.setContentText("Votre panier est vide !");
            alert.showAndWait();
            return;
        }

        double total = panierItems.stream()
            .mapToDouble(PanierItem::getTotal)
            .sum();
            
        Order order = new Order(
            1, // TODO: Remplacer par l'ID de l'utilisateur connecté
            LocalDateTime.now(),
            total,
            "EN_ATTENTE"
        );

        int orderId = orderDAO.createOrder(order);
        if (orderId != -1) {
            List<OrderItem> items = new ArrayList<>();
            for (PanierItem panierItem : panierItems) {
                OrderItem item = new OrderItem(
                    orderId,
                    panierItem.getProduit().getId(),
                    panierItem.getQuantite(),
                    panierItem.getProduit().getPrix()
                );
                items.add(item);
                
                // Mettre à jour le stock
                Produit produit = panierItem.getProduit();
                produit.setQuantite(produit.getQuantite() - panierItem.getQuantite());
                produitService.updateProduit(produit);
            }
            
            orderDAO.addOrderItems(orderId, items);

            // Vider le panier
            panier.clear();
            panierItems.clear();
            updateTotal();
            
            // Recharger les produits pour mettre à jour les quantités affichées
            loadAndDisplayProduits();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Votre commande a été validée avec succès!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la validation de votre commande.");
            alert.showAndWait();
        }
    }
}
