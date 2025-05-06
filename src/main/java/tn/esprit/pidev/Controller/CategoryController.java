package tn.esprit.pidev.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Service.CategoryService;
import tn.esprit.pidev.Service.ProduitService;
import tn.esprit.pidev.Util.NavigationHelper;

import java.io.IOException;
import java.util.List;

public class CategoryController {
    @FXML private FlowPane produitFlow;
    @FXML private FlowPane categoryFlow;

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public ObservableList<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ObservableList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    private CategoryService categoryService = new CategoryService();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();

    @FXML
    private void navigateToProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/produit/ProduitList.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) produitFlow.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Produit");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        categoryFlow.getChildren().clear();
        List<Category> categories = categoryService.getAllCategories();
        for (Category cat : categories) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, #a3c292, 8, 0.2, 0, 2); -fx-padding: 18; -fx-min-width: 200; -fx-max-width: 220; -fx-alignment: center;");
            Label name = new Label(cat.getName());
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4a8c24;");
            Label type = new Label(cat.getType());
            type.setStyle("-fx-font-size: 14px; -fx-text-fill: #7e57c2;");
            HBox actions = new HBox(8);
            actions.setAlignment(javafx.geometry.Pos.CENTER);
            Button edit = new Button("Modifier");
            edit.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 12;");
            edit.setOnAction(e -> handleEditCategory(cat));
            Button delete = new Button("Supprimer");
            delete.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 12;");
            delete.setOnAction(e -> handleDeleteCategory(cat));
            actions.getChildren().addAll(edit, delete);
            card.getChildren().addAll(name, type, actions);
            categoryFlow.getChildren().add(card);
        }
    }

    @FXML
    private void handleAddCategory() {
        showCategoryForm(null);
    }

    private void handleEditCategory(Category cat) {
        showCategoryForm(cat);
    }

    private void handleDeleteCategory(Category cat) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la catégorie");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                categoryService.deleteCategory(cat.getId());
                loadCategories();
            }
        });
    }

    private void showCategoryForm(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/category/CategoryForm.fxml"));
            Parent root = loader.load();

            CategoryFormController controller = loader.getController();
            controller.setCategory(category);
            controller.setCategoryController(this);

            Stage stage = new Stage();
            stage.setTitle(category == null ? "Nouvelle Catégorie" : "Modifier Catégorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Set stage to maximized
            stage.setMaximized(true);
            
            // Set minimum size
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
        }
    }

    public void refreshCategories() {
        loadCategories();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToCategoryForm(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/category/CategoryForm.fxml", "Ajouter/Modifier Catégorie");
    }

    @FXML
    private void goToMainView(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/MainView.fxml", "Accueil");
    }
}