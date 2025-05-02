package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import modele.Publication;
import modele.Commentaire;
import modele.Notification;
import services.ServiceCommentaire;
import services.NotificationService;
import utils.FiltreCommentaire;

import java.io.File;
import java.sql.SQLException;

public class Addcommentaire {

    @FXML private TextArea commentTextArea;
    @FXML private Button submitButton;
    @FXML private Button retourButton;
    @FXML private Button selectImageButton;
    @FXML private Label imageNameLabel;

    private Publication publication;
    private Commentaire commentaire;
    private ServiceCommentaire commentaireService;
    private NotificationService notificationService;
    private Runnable onCommentAdded;
    private final int currentClientId = 1; // Replace with actual user system
    private NavigationManager navigationManager;
    private File selectedImageFile;

    public Addcommentaire() {
        commentaireService = new ServiceCommentaire();
        notificationService = new NotificationService();
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public void setCommentaire(Commentaire commentaire) {
        this.commentaire = commentaire;
        if (commentaire != null) {
            commentTextArea.setText(commentaire.getDescription());
            submitButton.setText("Update Comment");
            if (commentaire.getImage() != null && !commentaire.getImage().isEmpty()) {
                imageNameLabel.setText(new File(commentaire.getImage()).getName());
            }
        }
    }

    public void setOnCommentAdded(Runnable onCommentAdded) {
        this.onCommentAdded = onCommentAdded;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    public void initialize() {
        if (commentaire == null) {
            submitButton.setText("Add Comment");
        }
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(commentTextArea.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imageNameLabel.setText(file.getName());
        }
    }

    @FXML
    private void submitComment() {
        String description = commentTextArea.getText();
        if (description == null || description.trim().isEmpty()) {
            showAlert("Error", "Comment cannot be empty");
            return;
        }

        String filteredDescription = FiltreCommentaire.filtreCommentaire(description);
        String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : null;

        try {
            if (commentaire == null) {
                Commentaire newComment = new Commentaire(
                        publication.getId(),
                        currentClientId,
                        filteredDescription,
                        imagePath
                );
                commentaireService.insert(newComment);

                String message = "New comment added to publication: " + publication.getTitre();
                Notification notification = new Notification(
                        message,
                        publication.getId(),
                        currentClientId,
                        newComment.getId()
                );
                notificationService.addNotification(notification);
            } else {
                commentaire.setDescription(filteredDescription);
                commentaire.setImage(imagePath);
                commentaireService.update(commentaire);
            }

            if (onCommentAdded != null) {
                onCommentAdded.run();
            }

            navigationManager.goBack();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException && e.getMessage().contains("inappropriate words")) {
                showAlert("Invalid Comment", "Your comment contains inappropriate words. Please revise and try again.");
            } else {
                showAlert("Error", "Failed to submit comment: " + e.getMessage());
            }
        }
    }
    @FXML
    private void goBack() {
        navigationManager.goBack();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}