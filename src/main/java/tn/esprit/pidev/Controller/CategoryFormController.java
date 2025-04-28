package tn.esprit.pidev.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.esprit.pidev.Model.Category;

import java.util.Arrays;
import java.util.List;

public class CategoryFormController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeCombo;

    private Category category;
    private CategoryController categoryController;
    private final List<String> typeOptions = Arrays.asList(
        "Céréales", "Légumes", "Fruits", "Légumineuses", "Plantes aromatiques"
    );

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(typeOptions));
    }

    public void setCategory(Category category) {
        this.category = category;

        if (category != null) {
            nameField.setText(category.getName());
            typeCombo.setValue(category.getType());
        }
    }

    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (category == null) {
                category = new Category();
            }

            category.setName(nameField.getText());
            category.setType(typeCombo.getValue());

            if (category.getId() == 0) {
                // New category
                categoryController.getCategoryService().addCategory(category);
            } else {
                // Existing category
                categoryController.getCategoryService().updateCategory(category);
            }

            categoryController.refreshCategories();
            closeForm();
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Le nom est obligatoire!\n";
        }
        if (typeCombo.getValue() == null) {
            errorMessage += "Le type est obligatoire!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Champs invalides", "Veuillez corriger les champs invalides", errorMessage);
            return false;
        }
    }

    private void closeForm() {
        nameField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}