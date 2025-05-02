package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import modele.Publication;
import modele.Notification;
import services.ServicePublication;
import services.NotificationService;
import services.RatingService;
import view.TrayNotification;
import utils.EventBus;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.text.Text;

public class AfficherPublicationController {

    @FXML private FlowPane cardContainer;
    @FXML private Button addPublicationBtn;
    @FXML private TextField searchField;
    @FXML private Button adminButton;
    @FXML private HBox topBar;
    @FXML private ScrollPane scrollPane;
    @FXML private Button retourButton;

    private final ServicePublication publicationService = new ServicePublication();
    private final NotificationService notificationService = new NotificationService();
    private final RatingService ratingService = new RatingService();
    private final int currentClientId = 1; // Replace with actual user system
    private NavigationManager navigationManager;
    private Stage primaryStage;
    private Button notificationButton; // Store for updating badge
    private Text notificationBadge; // Badge for unread count

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        loadPublications();
        setupNotificationButton();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPublicationsByTitre(newValue);
        });
        retourButton.setVisible(false);
        startNotificationCheck();
        // Subscribe to comment events
        EventBus.subscribe(event -> {
            if (event.equals("NEW_COMMENT")) {
                updateNotificationBadge();
            }
        });
    }

    private void setupNotificationButton() {
        notificationButton = new Button("🔔");
        notificationButton.getStyleClass().add("action-button");
        notificationButton.setStyle("-fx-font-size: 16px;");

        // Create badge for unread notifications
        notificationBadge = new Text();
        notificationBadge.getStyleClass().add("notification-badge");
        notificationBadge.setVisible(false); // Hidden initially

        // StackPane to overlay badge on button
        StackPane buttonContainer = new StackPane(notificationButton, notificationBadge);
        StackPane.setAlignment(notificationBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(notificationBadge, new Insets(0, 0, 5, 5));

        notificationButton.setOnAction(e -> showNotifications());
        topBar.getChildren().add(buttonContainer);
        HBox.setMargin(buttonContainer, new Insets(0, 10, 0, 10));

        updateNotificationBadge(); // Initial update
    }

    private void startNotificationCheck() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> updateNotificationBadge()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void updateNotificationBadge() {
        List<Notification> notifications = notificationService.getNotificationsByClientId(currentClientId);
        long unreadCount = notifications.stream().filter(n -> !n.isReading()).count();
        if (unreadCount > 0) {
            notificationBadge.setText(String.valueOf(unreadCount));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }

    public void refreshPublications() {
        System.out.println("Refreshing publications in AfficherPublicationController");
        loadPublications();
    }

    private void showNotifications() {
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f9f5;");
        mainContainer.getStyleClass().add("notification-pane");

        Label headerLabel = new Label("Notifications");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c6b2f;");
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().add(headerLabel);

        VBox notificationPane = new VBox(10);
        notificationPane.setPadding(new Insets(15));
        notificationPane.setStyle("-fx-background-color: #f5f9f5;");

        List<Notification> notifications = notificationService.getNotificationsByClientId(currentClientId);
        System.out.println("Notifications retrieved: " + notifications.size());

        if (notifications.isEmpty()) {
            Label emptyLabel = new Label("Aucune notification disponible.");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            emptyLabel.setPadding(new Insets(10));
            notificationPane.getChildren().add(emptyLabel);
        } else {
            for (Notification notification : notifications) {
                HBox notificationBox = new HBox(10);
                notificationBox.setPadding(new Insets(10));
                notificationBox.getStyleClass().add(notification.isReading() ? "notification-box-read" : "notification-box");
                notificationBox.setAlignment(Pos.CENTER_LEFT);

                Label messageLabel = new Label(notification.getMessage());
                messageLabel.getStyleClass().add("notification-message");
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(400);
                HBox.setHgrow(messageLabel, Priority.ALWAYS);

                Button markReadButton = new Button("Marquer comme lu");
                markReadButton.getStyleClass().add("action-button");
                markReadButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                markReadButton.setOnAction(e -> {
                    notificationService.markAsRead(notification.getId());
                    notificationBox.getStyleClass().remove("notification-box");
                    notificationBox.getStyleClass().add("notification-box-read");
                    TrayNotification tray = new TrayNotification(
                            "Notification",
                            "Notification marquée comme lue.",
                            TrayNotification.NotificationType.SUCCESS
                    );
                    if (primaryStage != null) {
                        tray.showAndDismiss(primaryStage, Duration.seconds(3));
                    } else {
                        System.err.println("Unable to show TrayNotification: Primary Stage is null");
                    }
                    updateNotificationBadge(); // Update badge after marking as read
                });

                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("action-button");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteButton.setOnAction(e -> {
                    notificationService.deleteNotification(notification.getId());
                    notificationPane.getChildren().remove(notificationBox);
                    TrayNotification tray = new TrayNotification(
                            "Notification",
                            "Notification supprimée.",
                            TrayNotification.NotificationType.INFO
                    );
                    if (primaryStage != null) {
                        tray.showAndDismiss(primaryStage, Duration.seconds(3));
                    } else {
                        System.err.println("Unable to show TrayNotification: Primary Stage is null");
                    }
                    updateNotificationBadge(); // Update badge after deletion
                });

                notificationBox.getChildren().addAll(messageLabel, markReadButton, deleteButton);
                notificationPane.getChildren().add(notificationBox);
            }
        }

        ScrollPane notificationScrollPane = new ScrollPane(notificationPane);
        notificationScrollPane.setFitToWidth(true);
        notificationScrollPane.getStyleClass().add("card-scroll-pane");
        VBox.setVgrow(notificationScrollPane, Priority.ALWAYS);

        Button retourButton = new Button("Retour");
        retourButton.getStyleClass().add("action-button");
        retourButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 15;");
        retourButton.setOnAction(e -> navigationManager.goBack());
        HBox buttonBox = new HBox(retourButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        mainContainer.getChildren().addAll(headerBox, notificationScrollPane, buttonBox);
        navigationManager.navigateTo(mainContainer);
    }

    private void filterPublicationsByTitre(String searchText) {
        List<Publication> allPublications = publicationService.getAll();
        if (searchText == null || searchText.isEmpty()) {
            displayPublications(allPublications);
            return;
        }

        String lowerCaseFilter = searchText.toLowerCase();
        List<Publication> filteredList = allPublications.stream()
                .filter(pub -> pub.getTitre().toLowerCase().contains(lowerCaseFilter))
                .toList();
        displayPublications(filteredList);
    }

    private void loadPublications() {
        List<Publication> publications = publicationService.getAll();
        displayPublications(publications);
    }

    private void displayPublications(List<Publication> publications) {
        cardContainer.getChildren().clear();
        if (publications == null || publications.isEmpty()) {
            Label noDataLabel = new Label("No publications available.");
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");
            cardContainer.getChildren().add(noDataLabel);
            return;
        }
        for (Publication publication : publications) {
            VBox card = createPublicationCard(publication);
            cardContainer.getChildren().add(card);
        }
    }

    private VBox createPublicationCard(Publication publication) {
        VBox card = new VBox();
        card.getStyleClass().add("publication-card");
        card.setPrefWidth(250);
        card.setSpacing(10);

        double averageRating = ratingService.getAverageRating(publication.getId());
        boolean isTopRated = averageRating > 2.5;

        if (isTopRated) {
            Label topRatedBadge = new Label("Top Rated");
            topRatedBadge.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #2c6b2f; -fx-font-size: 12px; " +
                    "-fx-padding: 5; -fx-background-radius: 5; -fx-font-weight: bold;");
            topRatedBadge.setAlignment(Pos.CENTER);
            card.getChildren().add(topRatedBadge);
        }

        ImageView imageView = new ImageView();
        if (publication.getImageUrl() != null && !publication.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(publication.getImageUrl(), 250, 150, true, true);
                imageView.setImage(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(250);
                imageView.setFitHeight(150);
            } catch (Exception e) {
                Label imageError = new Label("Image non disponible");
                imageError.getStyleClass().add("card-image-error");
                card.getChildren().add(imageError);
            }
        }

        Label titleLabel = new Label(publication.getTitre());
        titleLabel.getStyleClass().add("card-title");

        String shortDescription = publication.getDescription().length() > 100 ?
                publication.getDescription().substring(0, 100) + "..." :
                publication.getDescription();
        Label descLabel = new Label(shortDescription);
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        Label dateLabel = new Label(publication.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.getStyleClass().add("card-date");

        Label ratingLabel = new Label(String.format("Rating: %.1f", averageRating));
        ratingLabel.getStyleClass().add("card-date");

        HBox buttonBox = new HBox(5);
        buttonBox.getStyleClass().add("card-button-box");

        Button moreButton = new Button("⋮");
        moreButton.getStyleClass().add("card-more-button");
        moreButton.setFont(Font.font("Arial", 18));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewItem = new MenuItem("Voir");
        viewItem.setOnAction(e -> openDetailPage(publication));

        MenuItem updateItem = new MenuItem("Modifier");
        updateItem.setOnAction(e -> openUpdatePage(publication));

        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> showDeleteConfirmation(publication));

        MenuItem reclamationItem = new MenuItem("Reclamer");
        reclamationItem.setOnAction(e -> openReclamationPage(publication));

        MenuItem voirReclamationsItem = new MenuItem("Voir mes réclamations");
        voirReclamationsItem.setOnAction(e -> openAllReclamationsView());

        contextMenu.getItems().addAll(viewItem, reclamationItem, voirReclamationsItem);
        if (isAdminUser()) {
            contextMenu.getItems().addAll(updateItem, deleteItem);
        }

        moreButton.setOnAction(e -> contextMenu.show(moreButton, Side.BOTTOM, 0, 0));
        buttonBox.getChildren().add(moreButton);

        card.getChildren().addAll(imageView, titleLabel, descLabel, dateLabel, ratingLabel, buttonBox);
        return card;
    }

    private boolean isAdminUser() {
        return true;
    }

    @FXML
    private void openAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPublicationView.fxml"));
            Parent root = loader.load();
            AdminPublicationController controller = loader.getController();
            controller.setNavigationManager(navigationManager);
            controller.setRefreshCallback(this::refreshPublications);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Failed to load AdminPublicationView.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void openAddPublicationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicationForm.fxml"));
            Parent root = loader.load();
            AjouterPublicationForm controller = loader.getController();
            controller.setNavigationManager(navigationManager);
            controller.setRefreshCallback(this::refreshPublications);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout");
        }
    }

    private void openUpdatePage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdatePublication.fxml"));
            Parent root = loader.load();
            UpdatePublication controller = loader.getController();
            controller.setPublication(publication);
            controller.setNavigationManager(navigationManager);
            controller.setRefreshCallback(this::refreshPublications);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la page de modification");
        }
    }

    private void openDetailPage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Publicationdetail.fxml"));
            Parent root = loader.load();
            Publicationdetail controller = loader.getController();
            controller.setPublication(publication);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails: " + e.getMessage());
        }
    }

    private void openReclamationPage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReclamation.fxml"));
            Parent root = loader.load();
            AddReclamationController controller = loader.getController();
            controller.setPublication(publication);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réclamation");
        }
    }

    private void openAllReclamationsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AllReclamationsView.fxml"));
            Parent root = loader.load();
            AllReclamationsViewController controller = loader.getController();
            controller.setClientId(currentClientId);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des réclamations: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation(Publication publication) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer Publication");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette publication?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            publicationService.delete(publication.getId());
            refreshPublications();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        navigationManager.goBack();
    }
}
