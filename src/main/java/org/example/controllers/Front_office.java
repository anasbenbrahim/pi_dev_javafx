package org.example.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.models.Category_equipement;
import org.example.models.Equipements;
import org.example.services.Service_category_equipement;
import org.example.services.Service_equipement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Front_office implements Initializable {

    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML
    private FlowPane productsFlowPane;

    @FXML
    private TextField searchField;

    @FXML
    private Button refresh;

    @FXML
    private Button tri;

    @FXML
    private ComboBox<Category_equipement> categoryCombo;

    @FXML
    private Label resultsCount;

    private final ObservableList<Equipements> equipements = FXCollections.observableArrayList();
    private final Service_equipement equipementService = new Service_equipement();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Service_category_equipement service_category= new Service_category_equipement();
        ObservableList<Category_equipement> category_equipements = FXCollections.observableArrayList(service_category.getCategories());

        categoryCombo.setItems(category_equipements);

        categoryCombo.setCellFactory(param -> new ListCell<Category_equipement>() {
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

        categoryCombo.setButtonCell(new ListCell<Category_equipement>() {
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

        try {
            if (productsFlowPane == null) {
                productsFlowPane = new FlowPane();
            }
            chargerEtAfficherEquipements();
            configurerRafraichissementAutomatique();
        } catch (Exception e) {
            showAlert("Erreur", "Initialisation impossible", e.getMessage());
        }
    }

    private void chargerEtAfficherEquipements() {
        try {
            List<Equipements> resultats = equipementService.afficher();
            int size=resultats.size();
            resultsCount.setText(size+" equipements trouves");
            if (resultats != null) {
                equipements.setAll(resultats);
                afficherEquipements();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Chargement impossible", e.getMessage());
        }
    }
    private Node creerCarteProduit(Equipements equip) {
        if (equip == null) {
            return new Label("Équipement invalide");
        }

        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));

        try {
            // 1. Gestion de l'image
            ImageView imageView = new ImageView();
            imageView.getStyleClass().add("product-image");
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            String imagePath = (equip.getImage() != null && !equip.getImage().isEmpty())
                    ? equip.getImage()
                    : "/images/default.png";

            // Nouvelle gestion améliorée du chargement d'image
            try {
                if (imagePath.startsWith("http")) {
                    // Chargement depuis URL avec gestion d'erreur
                    Image webImage = new Image(imagePath, 150, 150, true, true, true);
                    webImage.errorProperty().addListener((obs, wasError, isNowError) -> {
                        if (isNowError) {
                            chargerImageParDefaut(imageView);
                        }
                    });
                    imageView.setImage(webImage);
                }
                else if (imagePath.startsWith("/")) {
                    // Chargement depuis les ressources JAR
                    InputStream imgStream = getClass().getResourceAsStream(imagePath);
                    if (imgStream != null) {
                        imageView.setImage(new Image(imgStream, 150, 150, true, true));
                    } else {
                        throw new IOException("Image non trouvée dans les ressources: " + imagePath);
                    }
                }
                else {
                    // Chargement depuis le système de fichiers
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        String url = imageFile.toURI().toString();
                        Image fileImage = new Image(url, 150, 150, true, true, true);
                        fileImage.errorProperty().addListener((obs, wasError, isNowError) -> {
                            if (isNowError) {
                                chargerImageParDefaut(imageView);
                            }
                        });
                        imageView.setImage(fileImage);
                    } else {
                        throw new IOException("Fichier image non trouvé: " + imagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement image [" + imagePath + "]: " + e.getMessage());
                chargerImageParDefaut(imageView);
            }

            // 2. Contenu texte
            Label nomLabel = new Label(Objects.toString(equip.getNom(), "Nom inconnu"));
            nomLabel.getStyleClass().add("product-name");
            nomLabel.setWrapText(true);
            nomLabel.setMaxWidth(180);

            Label prixLabel = new Label(String.format("%.2f €", equip.getPrix()));
            prixLabel.getStyleClass().add("product-price");

            // 3. Gestion du stock avec style conditionnel
            Label stockLabel = new Label("Stock: " + equip.getQuantite());
            stockLabel.getStyleClass().add("product-stock");
            if (equip.getQuantite() < 10) {
                stockLabel.getStyleClass().add("low-stock");
            }

            // 4. Bouton détails
            Button detailsBtn = new Button("Voir détails");
            detailsBtn.getStyleClass().add("detail-button");

            // Ajout d'une icône (si disponible)
            try {
                InputStream iconStream = getClass().getResourceAsStream("/icons/eye.png");
                if (iconStream != null) {
                    ImageView icon = new ImageView(new Image(iconStream, 16, 16, true, true));
                    detailsBtn.setGraphic(icon);
                    detailsBtn.setContentDisplay(ContentDisplay.RIGHT);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement icône: " + e.getMessage());
            }

            detailsBtn.setOnAction(e -> afficherDetails(equip));

            // Assemblage de la carte
            card.getChildren().addAll(imageView, nomLabel, prixLabel, stockLabel, detailsBtn);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback minimaliste si erreur grave
            card.getChildren().add(new Label("Erreur d'affichage"));
        }

        return card;
    }

    // Méthode helper pour charger l'image par défaut
    private void chargerImageParDefaut(ImageView imageView) {
        try {
            InputStream defaultImgStream = getClass().getResourceAsStream("/images/default.png");
            if (defaultImgStream != null) {
                imageView.setImage(new Image(defaultImgStream, 150, 150, true, true));
            } else {
                // Placeholder graphique si même l'image par défaut est manquante
                imageView.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #ccc;");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image par défaut: " + e.getMessage());
        }
    }

    private void afficherEquipements() {
        productsFlowPane.getChildren().clear();
        //int i=0;
        for (Equipements equip : equipements) {
            productsFlowPane.getChildren().add(creerCarteProduit(equip));

        }
        resultsCount.setText((equipements.size()+" equipements trouves"));
    }


    @FXML
    private void handleRefresh(ActionEvent event) {
        chargerEtAfficherEquipements();
    }

    @FXML
    private void handleSort(ActionEvent event) {
        equipements.sort((e1, e2) -> Double.compare(e1.getPrix(),e2.getPrix()));
        afficherEquipements();
    }

    private void afficherDetails(Equipements equip) {
        try {
            // Charger le FXML en tant qu'URL
            URL fxmlUrl = getClass().getResource("/devis.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Fichier FXML introuvable");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Vérifier que le contrôleur a bien été chargé
            Devis controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Controller non initialisé");
            }

            controller.setEquipement(equip);

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur Critique", "Échec du chargement", e.toString());
        }
    }

    private void configurerRafraichissementAutomatique() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.minutes(5), e -> chargerEtAfficherEquipements())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void search(KeyEvent keyEvent) {
        String recherche = searchField.getText().toLowerCase().trim();
        if (recherche.isEmpty()) {
            afficherEquipements(); // affiche tout si champ vide
        } else {
            List<Equipements> resultatsFiltres = equipements.filtered(equip ->
                    equip.getNom().toLowerCase().contains(recherche)
            );
            afficherEquipementsFiltres(resultatsFiltres);
        }
    }
    private void afficherEquipementsFiltres(List<Equipements> filtres) {
        productsFlowPane.getChildren().clear();
        for (Equipements equip : filtres) {
            productsFlowPane.getChildren().add(creerCarteProduit(equip));
        }
    }

    public void handleCategorieSelection(ActionEvent event) {
        Category_equipement selectedCategorie = categoryCombo.getValue();

        try {
            List<Equipements> resultats;
            if ("Tous".equals(selectedCategorie)) {
                resultats = equipementService.afficher();
            } else {
                resultats = equipementService.recherchePar_categorie(selectedCategorie);
            }

            equipements.setAll(resultats);
            afficherEquipements();

        } catch (Exception e) {
            showAlert("Erreur", "Filtrage impossible", e.getMessage());
        }
    }

    public void nav_back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Affichage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void nav_llama(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/chat_ollama.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}