package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modele.Publication;
import modele.Commentaire;
import modele.Rating;
import services.ServiceCommentaire;
import services.RatingService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Publicationdetail {

    @FXML private Label titleLabel;
    @FXML private ImageView imageView;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private VBox commentsContainer;
    @FXML private Button addCommentButton;
    @FXML private VBox mainContainer;
    @FXML private HBox ratingBox;
    @FXML private Button star1;
    @FXML private Button star2;
    @FXML private Button star3;
    @FXML private Button star4;
    @FXML private Button star5;
    @FXML private Label averageRatingLabel;
    @FXML private Button retourButton;

    private Publication publication;
    private ServiceCommentaire commentaireService;
    private RatingService ratingService;
    private final int currentClientId = 1;
    private Button[] starButtons;
    private NavigationManager navigationManager;

    public Publicationdetail() {
        commentaireService = new ServiceCommentaire();
        ratingService = new RatingService();
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
        populateUI();
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    public void initialize() {
        starButtons = new Button[]{star1, star2, star3, star4, star5};
        for (int i = 0; i < starButtons.length; i++) {
            final int ratingValue = i + 1;
            starButtons[i].setUserData(ratingValue);
        }
    }

    private void populateUI() {
        if (publication == null) {
            titleLabel.setText("Error: No publication data");
            descriptionLabel.setText("No description available");
            dateLabel.setText("No date available");
            imageView.setImage(null);
            commentsContainer.getChildren().clear();
            return;
        }

        titleLabel.setText(publication.getTitre() != null ? publication.getTitre() : "No Title");
        descriptionLabel.setText(publication.getDescription() != null ? publication.getDescription() : "No Description");
        dateLabel.setText(publication.getDate() != null ?
                publication.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "No Date");

        if (publication.getImageUrl() != null && !publication.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(publication.getImageUrl(), 400, 200, true, true);
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(null);
                descriptionLabel.setText(descriptionLabel.getText() + "\n(Image not available)");
            }
        } else {
            imageView.setImage(null);
        }

        loadComments();
        loadRating();
    }

    private void loadRating() {
        if (publication == null) {
            averageRatingLabel.setText("(0.0)");
            return;
        }

        Rating clientRating = ratingService.getRating(publication.getId(), currentClientId);
        int ratingValue = (clientRating != null) ? clientRating.getRating() : 0;

        for (int i = 0; i < starButtons.length; i++) {
            starButtons[i].setText((i < ratingValue) ? "★" : "☆");
        }

        double averageRating = ratingService.getAverageRating(publication.getId());
        averageRatingLabel.setText(String.format("(%.1f)", averageRating));
    }

    @FXML
    private void ratePublication() {
        if (publication == null) {
            showAlert("Error", "Cannot rate: No publication data");
            return;
        }

        Button source = (Button) ratingBox.getScene().getFocusOwner();
        Integer ratingValue = (Integer) source.getUserData();
        if (ratingValue == null) {
            return;
        }

        Rating existingRating = ratingService.getRating(publication.getId(), currentClientId);
        Rating rating = new Rating(publication.getId(), currentClientId, ratingValue);
        if (existingRating == null) {
            ratingService.addRating(rating);
        } else {
            ratingService.updateRating(rating);
        }

        loadRating();
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        if (publication == null) {
            Label errorLabel = new Label("Cannot load comments: No publication data");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #F44336;");
            commentsContainer.getChildren().add(errorLabel);
            return;
        }

        List<Commentaire> commentaires = commentaireService.afficherCommentairesParPublication(publication.getId());
        if (commentaires.isEmpty()) {
            Label noCommentsLabel = new Label("No comments yet.");
            noCommentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            commentsContainer.getChildren().add(noCommentsLabel);
        } else {
            for (Commentaire commentaire : commentaires) {
                VBox commentBox = new VBox(5);
                commentBox.setPadding(new Insets(10));
                commentBox.getStyleClass().add("comment-box");

                Label commentText = new Label(commentaire.getDescription() != null ? commentaire.getDescription() : "No description");
                commentText.setWrapText(true);
                commentText.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c6b2f; -fx-opacity: 1.0;");

                Label translatedTextLabel = new Label("");
                translatedTextLabel.setWrapText(true);
                translatedTextLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555; -fx-font-style: italic;");

                if (commentaire.getImage() != null && !commentaire.getImage().isEmpty()) {
                    try {
                        Image image = new Image("file:" + commentaire.getImage(), 200, 100, true, true);
                        ImageView commentImageView = new ImageView(image);
                        commentImageView.setPreserveRatio(true);
                        commentBox.getChildren().add(commentImageView);
                    } catch (Exception e) {
                        Label imageError = new Label("Image not available");
                        imageError.setStyle("-fx-font-size: 12px; -fx-text-fill: #F44336;");
                        commentBox.getChildren().add(imageError);
                    }
                }

                HBox buttonBox = new HBox(10);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);

                Button editButton = new Button("Modifier");
                editButton.getStyleClass().add("action-button");
                editButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                editButton.setOnAction(e -> openEditCommentForm(commentaire));

                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("action-button");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteButton.setOnAction(e -> deleteComment(commentaire.getId()));

                ComboBox<String> languageComboBox = new ComboBox<>();
                languageComboBox.getItems().addAll("English", "French", "Spanish", "German", "Italian");
                languageComboBox.setPromptText("Select Language");
                languageComboBox.getStyleClass().add("language-selector");

                Button translateButton = new Button("Traduire");
                translateButton.getStyleClass().add("action-button");
                translateButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                translateButton.setOnAction(e -> {
                    String selectedLanguage = languageComboBox.getValue();
                    if (selectedLanguage == null) {
                        showAlert("Error", "Please select a target language.");
                        return;
                    }
                    String targetLangCode = switch (selectedLanguage) {
                        case "English" -> "en";
                        case "French" -> "fr";
                        case "Spanish" -> "es";
                        case "German" -> "de";
                        case "Italian" -> "it";
                        default -> "en";
                    };
                    try {
                        String translatedText = commentaireService.translateComment(commentaire.getDescription(), "auto", targetLangCode);
                        translatedTextLabel.setText("Translated (" + selectedLanguage + "): " + translatedText);
                    } catch (RuntimeException ex) {
                        showAlert("Translation Error", "Failed to translate comment: " + ex.getMessage());
                    }
                });

                buttonBox.getChildren().addAll(editButton, deleteButton, languageComboBox, translateButton);
                commentBox.getChildren().addAll(commentText, translatedTextLabel, buttonBox);
                commentsContainer.getChildren().add(commentBox);
            }
        }
    }

    private void deleteComment(int commentId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Comment");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette publication?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            commentaireService.delete(commentId);
            loadComments();
        }
    }

    private void openEditCommentForm(Commentaire commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Addcommentaire.fxml"));
            Parent root = loader.load();
            Addcommentaire controller = loader.getController();
            controller.setPublication(publication);
            controller.setCommentaire(commentaire);
            controller.setOnCommentAdded(this::loadComments);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Error", "Failed to open edit comment form: " + e.getMessage());
        }
    }

    @FXML
    private void openAddCommentForm() {
        if (publication == null) {
            showAlert("Error", "Cannot add comment: No publication data");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Addcommentaire.fxml"));
            Parent root = loader.load();
            Addcommentaire controller = loader.getController();
            controller.setPublication(publication);
            controller.setOnCommentAdded(this::loadComments);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Error", "Failed to open add comment form: " + e.getMessage());
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