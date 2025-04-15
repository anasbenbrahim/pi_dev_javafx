package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modele.Commentaire;
import modele.Publication;
import services.ServiceCommentaire;

import java.io.IOException;
import java.util.List;

public class Publicationdetail {

    @FXML private Label titreLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private ImageView imageView;
    @FXML private VBox commentairesVBox;

    private Publication publication;
    private final ServiceCommentaire commentaireService = new ServiceCommentaire();

    public void setPublication(Publication publication) {
        this.publication = publication;
        titreLabel.setText(publication.getTitre());
        descriptionLabel.setText(publication.getDescription());
        dateLabel.setText(publication.getDate().toString());

        if (publication.getImageUrl() != null && !publication.getImageUrl().isEmpty()) {
            imageView.setImage(new Image(publication.getImageUrl(), true));
        }

        loadCommentaires();
    }

    private void loadCommentaires() {
        commentairesVBox.getChildren().clear();

        List<Commentaire> commentaires = commentaireService.afficherCommentairesParPublication(publication.getId());

        for (Commentaire commentaire : commentaires) {
            VBox singleCommentBox = new VBox(5);
            singleCommentBox.setPadding(new Insets(5));

            Label commentaireLabel = new Label("- " + commentaire.getDescription());
            commentaireLabel.setStyle("-fx-font-size: 13px;");

            // Edit button
            Button editButton = new Button("Modifier");
            editButton.setOnAction(event -> openEditForm(commentaire));

            // Delete button
            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(event -> {
                commentaireService.delete(commentaire.getId());
                refreshCommentaires();
            });

            // Buttons in one line
            HBox buttonBox = new HBox(10, editButton, deleteButton);

            singleCommentBox.getChildren().addAll(commentaireLabel, buttonBox);
            commentairesVBox.getChildren().add(singleCommentBox);
        }
    }

    private void openEditForm(Commentaire commentaire) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modifier le commentaire");

        TextField descField = new TextField(commentaire.getDescription());
        TextField imageField = new TextField(commentaire.getImage());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            commentaire.setDescription(descField.getText());
            commentaire.setImage(imageField.getText());

            commentaireService.update(commentaire);
            popup.close();
            refreshCommentaires();
        });

        VBox layout = new VBox(10, descField, imageField, saveBtn);
        layout.setPadding(new Insets(10));

        popup.setScene(new Scene(layout, 300, 200));
        popup.showAndWait();
    }

    private void refreshCommentaires() {
        loadCommentaires();
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        Stage stage = (Stage) titreLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openCommentaireForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addCommentaire.fxml"));
            Parent root = loader.load();
            Addcommentaire commentaireController = loader.getController();
            commentaireController.setPublication(publication);
            commentaireController.setOnCommentAdded(this::loadCommentaires);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Commentaire");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
