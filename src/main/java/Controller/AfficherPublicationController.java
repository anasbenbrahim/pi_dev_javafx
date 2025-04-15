package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modele.Publication;
import services.ServicePublication;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AfficherPublicationController {

    @FXML private TableView<Publication> tableView;
    @FXML private TableColumn<Publication, String> titreColumn;
    @FXML private TableColumn<Publication, String> descriptionColumn;
    @FXML private TableColumn<Publication, LocalDate> dateColumn;
    @FXML private TableColumn<Publication, Void> imageColumn;
    @FXML private TableColumn<Publication, Void> testbutton;
    @FXML private TableColumn<Publication, Void> supprimerbutton;
    @FXML private TableColumn<Publication, Void> voirbutton;
    @FXML private TableColumn<Publication, Void> reclamationButton;
    @FXML private TableColumn<Publication, Void> viewMyReclamationButton;
    @FXML private Button addPublicationBtn;

    private final ServicePublication publicationService = new ServicePublication();

    @FXML
    public void initialize() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        addImageColumnToTable();
        addUpdateButtonToTable();
        addDeleteButtonToTable();
        addVoirButtonToTable();
        addReclamationButtonToTable();
        addViewMyReclamationButtonToTable();
        loadPublications();
    }

    private void loadPublications() {
        List<Publication> publications = publicationService.getAll();
        tableView.getItems().setAll(publications);
    }

    @FXML
    private void openAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPublicationView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Panel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log stack trace
            showAlert("Erreur", "Failed to load AdminPublicationView.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void openAddPublicationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicationForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Publication");
            stage.show();

            stage.setOnHidden(e -> loadPublications());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout");
        }
    }

    private void addImageColumnToTable() {
        imageColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Publication publication = getTableRow().getItem();
                    if (publication.getImageUrl() != null && !publication.getImageUrl().isEmpty()) {
                        Image image = new Image(publication.getImageUrl(), 60, 60, true, true);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void addUpdateButtonToTable() {
        testbutton.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("Modifier");

            {
                updateBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    openUpdatePage(publication);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateBtn);
            }
        });
    }

    private void addDeleteButtonToTable() {
        supprimerbutton.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(publication);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void addVoirButtonToTable() {
        voirbutton.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn = new Button("Voir");

            {
                voirBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    openDetailPage(publication);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : voirBtn);
            }
        });
    }

    private void addReclamationButtonToTable() {
        reclamationButton.setCellFactory(param -> new TableCell<>() {
            private final Button reclamationBtn = new Button("Reclamer");

            {
                reclamationBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    openReclamationPage(publication);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : reclamationBtn);
            }
        });
    }

    private void addViewMyReclamationButtonToTable() {
        viewMyReclamationButton.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("Mes Réclamations");

            {
                viewBtn.setOnAction(event -> openAllReclamationsView());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }

    private void openUpdatePage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdatePublication.fxml"));
            Parent root = loader.load();

            UpdatePublication controller = loader.getController();
            controller.setPublication(publication);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Publication");
            stage.show();

            stage.setOnHidden(e -> loadPublications());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la page de modification");
        }
    }

    private void openDetailPage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/publicationdetail.fxml"));
            Parent root = loader.load();

            Publicationdetail controller = loader.getController();
            controller.setPublication(publication);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails Publication");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails");
        }
    }

    private void openReclamationPage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReclamation.fxml"));
            Parent root = loader.load();

            AddReclamationController controller = loader.getController();
            controller.setPublication(publication);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Soumettre Réclamation");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réclamation");
        }
    }

    private void openAllReclamationsView() {
        try {
            // Debug resource loading
            System.out.println("Loading FXML from: " + getClass().getResource("/AllReclamationsView.fxml"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AllReclamationsView.fxml"));
            Parent root = loader.load();

            AllReclamationsViewController controller = loader.getController();
            if (controller == null) {
                throw new Exception("Controller initialization failed");
            }

            controller.setClientId(getLoggedInClientId());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Mes Réclamations");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading AllReclamationsView:");
            e.printStackTrace();
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
            tableView.getItems().remove(publication);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getLoggedInClientId() {
        return 1; // Replace with actual authentication logic
    }
}