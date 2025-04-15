package Controller;

import modele.Commentaire;
import modele.Publication;
import services.ServiceCommentaire;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Addcommentaire {

    @FXML
    private TextField descriptionField;

    @FXML
    private TextArea imageField;

    private ServiceCommentaire commentaireService;

    public Addcommentaire() {
        commentaireService = new ServiceCommentaire();
    }

    private Publication publication;

    public void setPublication(Publication publication) {
        this.publication = publication;  // Correctly set the publication object
    }

    // This method will be called when the Submit button is clicked
    @FXML
    private void handleAddCommentaire() {
        String description = descriptionField.getText();
        String imageUrl = imageField.getText();

        // Validation to ensure fields are not empty
        if (description == null || description.isEmpty() || imageUrl == null || imageUrl.isEmpty()) {
            showAlert("Error", "Description and image URL cannot be empty.");
            return;
        }

        // Get publicationId and clientId from the Publication and Client
        int publicationId = publication.getId();  // Use the correct publicationId

        // Get clientId from the logged-in user (replace with actual client logic)
        int clientId = getLoggedInClientId();  // Assuming a method to get client ID from session or user info

        // Create a new Commentaire object
        Commentaire commentaire = new Commentaire(publicationId, clientId, description, imageUrl);

        try {
            // Insert the comment using the service
            commentaireService.insert(commentaire);
            showAlert("Success", "Comment added successfully!");

            // Clear the fields after successful submission
            descriptionField.clear();
            imageField.clear();
        } catch (Exception e) {
            showAlert("Error", "Failed to add comment. Please try again.");
            e.printStackTrace();  // Optional: log the error for debugging purposes
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Runnable onCommentAdded;

    public void setOnCommentAdded(Runnable onCommentAdded) {
        this.onCommentAdded = onCommentAdded;
    }

    // Dummy method for getting the clientId, replace with actual logic to get the client
    private int getLoggedInClientId() {
        // Replace with actual logic to get the logged-in client ID (e.g., from session, authentication system, etc.)
        return 1;  // For now, just return 1 as a placeholder
    }
}
