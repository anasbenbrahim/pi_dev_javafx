package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Model.Produit;
import tn.esprit.pidev.Service.CategoryService;
import tn.esprit.pidev.Service.ProduitService;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProduitFormController {
    @FXML private TextField nomField;
    @FXML private TextField imageField;
    @FXML private TextField prixField;
    @FXML private TextField quantiteField;
    @FXML private TextArea descrArea;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField userIdField;
    @FXML private ImageView imagePreview;

    private Produit produit;
    private ProduitService produitService;
    private CategoryService categoryService;

    public void setProduitService(ProduitService produitService) {
        this.produitService = produitService;
    }

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void initData(Produit produit) {
        this.produit = produit;
        
        // Initialize status combo box
        statusCombo.getItems().addAll("Disponible", "Indisponible");
        
        // Load categories
        if (categoryService != null) {
            List<Category> categories = categoryService.getAllCategories();
            categoryCombo.getItems().clear();
            categoryCombo.getItems().addAll(categories);
        }
        
        if (produit != null) {
            // Editing existing product
            nomField.setText(produit.getNomprod());
            imageField.setText(produit.getImage());
            prixField.setText(String.valueOf(produit.getPrix()));
            quantiteField.setText(String.valueOf(produit.getQuantite()));
            descrArea.setText(produit.getDescr());
            userIdField.setText(String.valueOf(produit.getUserId()));
            
            // Set status
            statusCombo.setValue(produit.getStatus() == 1 ? "Disponible" : "Indisponible");
            
            // Set category
            if (categoryService != null) {
                categoryCombo.getItems().stream()
                    .filter(c -> c.getId() == produit.getCategoryId())
                    .findFirst()
                    .ifPresent(c -> categoryCombo.setValue(c));
            }
            
            // Load image preview
            updateImagePreview(produit.getImage());
        } else {
            // Set defaults for new product
            userIdField.setText("1"); // Default user ID
            statusCombo.setValue("Disponible");
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imageField.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create uploads directory if it doesn't exist
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdir();
                }

                // Copy file to uploads directory
                File destFile = new File(uploadsDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update image field and preview
                String imagePath = "uploads/" + selectedFile.getName();
                imageField.setText(imagePath);
                updateImagePreview(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'upload", "Impossible de copier le fichier");
            }
        }
    }

    private void updateImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
            }
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                if (produit == null) {
                    produit = new Produit();
                }

                produit.setNomprod(nomField.getText());
                produit.setImage(imageField.getText());
                produit.setPrix(Double.parseDouble(prixField.getText()));
                produit.setQuantite(Integer.parseInt(quantiteField.getText()));
                produit.setDescr(descrArea.getText());
                produit.setUserId(Integer.parseInt(userIdField.getText()));
                produit.setCategoryId(categoryCombo.getValue().getId());
                produit.setStatus("Disponible".equals(statusCombo.getValue()) ? 1 : 0);

                if (produit.getId() == 0) {
                    produitService.addProduit(produit);
                } else {
                    produitService.updateProduit(produit);
                }

                closeForm();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Format invalide", "Le prix et la quantité doivent être des nombres");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur de sauvegarde", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nomField.getText() == null || nomField.getText().isEmpty()) {
            errorMessage += "Le nom du produit est obligatoire!\n";
        }
        if (prixField.getText() == null || prixField.getText().isEmpty()) {
            errorMessage += "Prix doit être positif!\n";
        } else {
            try {
                Double.parseDouble(prixField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Le prix doit être un nombre valide!\n";
            }
        }
        if (quantiteField.getText() == null || quantiteField.getText().isEmpty()) {
            errorMessage += "La quantité est obligatoire!\n";
        } else {
            try {
                Integer.parseInt(quantiteField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "La quantité doit être un nombre entier valide!\n";
            }
        }
        if (userIdField.getText() == null || userIdField.getText().isEmpty()) {
            errorMessage += "L'ID utilisateur est obligatoire!\n";
        } else {
            try {
                Integer.parseInt(userIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "L'ID utilisateur doit être un nombre entier valide!\n";
            }
        }
        if (categoryCombo.getValue() == null) {
            errorMessage += "La catégorie est obligatoire!\n";
        }
        if (statusCombo.getValue() == null) {
            errorMessage += "Le statut est obligatoire!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Champs invalides", "Veuillez corriger les champs invalides", errorMessage);
            return false;
        }
    }

    private void closeForm() {
        nomField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}