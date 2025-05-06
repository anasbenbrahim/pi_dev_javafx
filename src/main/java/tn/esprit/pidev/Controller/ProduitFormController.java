package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import tn.esprit.pidev.Model.Category;
import tn.esprit.pidev.Model.Produit;
import tn.esprit.pidev.Service.CategoryService;
import tn.esprit.pidev.Service.ProduitService;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.stage.Stage;
import tn.esprit.pidev.Util.NavigationHelper;

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
                    boolean created = uploadsDir.mkdirs(); // Use mkdirs() instead of mkdir() to create parent directories if needed
                    if (!created) {
                        throw new IOException("Impossible de créer le répertoire 'uploads'");
                    }
                }

                // Generate a unique filename to avoid conflicts
                String originalFilename = selectedFile.getName();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFilename = System.currentTimeMillis() + fileExtension;

                // Copy file to uploads directory
                File destFile = new File(uploadsDir, uniqueFilename);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Verify the file was copied successfully
                if (!destFile.exists() || destFile.length() == 0) {
                    throw new IOException("Le fichier n'a pas été copié correctement");
                }

                // Update image field and preview
                String imagePath = "uploads/" + uniqueFilename;
                imageField.setText(imagePath);
                updateImagePreview(imagePath);
            } catch (IOException e) {
                System.err.println("Error uploading image: " + e.getMessage());
                showAlert("Erreur", "Erreur lors de l'upload", "Impossible de copier le fichier: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                showAlert("Erreur", "Erreur inattendue", "Une erreur inattendue s'est produite: " + e.getMessage());
            }
        }
    }

    private void updateImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                try {
                    Image image = new Image(file.toURI().toString());
                    imagePreview.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                    // Set a default "image not found" image or clear the current image
                    imagePreview.setImage(null);
                }
            } else {
                System.err.println("Image file does not exist: " + imagePath);
                // Clear the image preview
                imagePreview.setImage(null);
            }
        } else {
            // Clear the image preview when path is null or empty
            imagePreview.setImage(null);
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                if (produit == null) {
                    produit = new Produit();
                }

                produit.setNomprod(nomField.getText().trim());
                produit.setImage(imageField.getText().trim());
                produit.setPrix(Double.parseDouble(prixField.getText().trim()));
                produit.setQuantite(Integer.parseInt(quantiteField.getText().trim()));
                produit.setDescr(descrArea.getText().trim());
                produit.setUserId(Integer.parseInt(userIdField.getText().trim()));

                // Safe check for category selection
                Category selectedCategory = categoryCombo.getValue();
                if (selectedCategory != null) {
                    produit.setCategoryId(selectedCategory.getId());
                } else {
                    // This should not happen due to validation, but as a safeguard
                    showAlert("Erreur", "Catégorie manquante", "Veuillez sélectionner une catégorie");
                    return;
                }

                // Safe check for status selection
                String selectedStatus = statusCombo.getValue();
                if (selectedStatus != null) {
                    produit.setStatus("Disponible".equals(selectedStatus) ? 1 : 0);
                } else {
                    // This should not happen due to validation, but as a safeguard
                    showAlert("Erreur", "Statut manquant", "Veuillez sélectionner un statut");
                    return;
                }

                if (produit.getId() == 0) {
                    produitService.addProduit(produit);
                } else {
                    produitService.updateProduit(produit);
                }

                closeForm();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Format invalide", "Le prix et la quantité doivent être des nombres valides");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur de sauvegarde", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    @FXML
    private void goToProduitList(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/produit/ProduitList.fxml", "Liste des produits");
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nomField.getText() == null || nomField.getText().isEmpty()) {
            errorMessage += "Le nom du produit est obligatoire!\n";
        }

        if (prixField.getText() == null || prixField.getText().isEmpty()) {
            errorMessage += "Le prix est obligatoire!\n";
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) {
                    errorMessage += "Le prix doit être positif!\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "Le prix doit être un nombre valide!\n";
            }
        }

        if (quantiteField.getText() == null || quantiteField.getText().isEmpty()) {
            errorMessage += "La quantité est obligatoire!\n";
        } else {
            try {
                int quantite = Integer.parseInt(quantiteField.getText());
                if (quantite < 0) {
                    errorMessage += "La quantité ne peut pas être négative!\n";
                }
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
