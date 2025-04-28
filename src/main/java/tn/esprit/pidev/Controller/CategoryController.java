package tn.esprit.pidev.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Service.CategoryService;

import java.io.IOException;

public class CategoryController {
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idColumn;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, String> typeColumn;

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

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        loadCategories();
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(categoryService.getAllCategories());
        categoryTable.setItems(categoryList);
    }

    @FXML
    private void handleAddCategory() {
        showCategoryForm(null);
    }

    @FXML
    private void handleEditCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            showCategoryForm(selectedCategory);
        } else {
            showAlert("Aucune sélection", "Aucune catégorie sélectionnée", "Veuillez sélectionner une catégorie dans le tableau.");
        }
    }

    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer la catégorie");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    categoryService.deleteCategory(selectedCategory.getId());
                    loadCategories();
                }
            });
        } else {
            showAlert("Aucune sélection", "Aucune catégorie sélectionnée", "Veuillez sélectionner une catégorie dans le tableau.");
        }
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
}