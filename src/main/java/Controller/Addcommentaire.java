package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import modele.Publication;
import modele.Commentaire;
import modele.Notification;
import services.ServiceCommentaire;
import services.NotificationService;
import utils.FiltreCommentaire;

public class Addcommentaire {

    @FXML private TextArea commentTextArea;
    @FXML private Button submitButton;
    @FXML private Button retourButton;

    private Publication publication;
    private Commentaire commentaire;
    private ServiceCommentaire commentaireService;
    private NotificationService notificationService;
    private Runnable onCommentAdded;
    private final int currentClientId = 1; // Replace with actual user system
    private NavigationManager navigationManager;

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
    private void submitComment() {
        String description = commentTextArea.getText();
        if (description == null || description.trim().isEmpty()) {
            showAlert("Error", "Comment cannot be empty");
            return;
        }

        String filteredDescription = FiltreCommentaire.filtreCommentaire(description);

        try {
            if (commentaire == null) {
                Commentaire newComment = new Commentaire(
                        filteredDescription,
                        publication.getId(),
                        currentClientId
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
                commentaireService.update(commentaire);
            }

            if (onCommentAdded != null) {
                onCommentAdded.run();
            }

            navigationManager.goBack();
        } catch (Exception e) {
            showAlert("Error", "Failed to submit comment: " + e.getMessage());
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