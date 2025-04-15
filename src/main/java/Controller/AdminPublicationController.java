package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import modele.Publication;
import modele.Reclamation;
import services.ServicePublication;
import services.ServiceReclamation;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ResourceBundle;

public class AdminPublicationController implements Initializable {

    // Publications
    @FXML private TableView<Publication> adminTableView;
    @FXML private TableColumn<Publication, String> titreColumn;
    @FXML private TableColumn<Publication, String> descriptionColumn;
    @FXML private TableColumn<Publication, String> dateColumn;
    @FXML private TableColumn<Publication, String> imageColumn;
    @FXML private TableColumn<Publication, Void> deleteButtonColumn;

    // Reclamations
    @FXML private TableView<Reclamation> reclamationTableView;
    @FXML private TableColumn<Reclamation, String> reclamationTitreColumn;
    @FXML private TableColumn<Reclamation, String> reclamationDescriptionColumn;
    @FXML private TableColumn<Reclamation, String> reclamationDateColumn;
    @FXML private TableColumn<Reclamation, String> reclamationStatusColumn;
    @FXML private TableColumn<Reclamation, Void> approveColumn;
    @FXML private TableColumn<Reclamation, Void> deleteReclamationColumn;

    private final ServicePublication servicePublication = new ServicePublication();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPublicationTable();
        setupReclamationTable();
        loadPublications();
        loadReclamations();
    }

    private void setupPublicationTable() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Set cell value factory for image column using imageUrl
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        // Set custom cell factory to display images
        imageColumn.setCellFactory(column -> new TableCell<Publication, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null || imageUrl.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image(imageUrl, 60, 60, true, true);
                        imageView.setImage(image);
                        imageView.setFitWidth(60);
                        imageView.setFitHeight(60);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null); // handle bad URL or loading error
                    }
                }
            }
        });

        addDeleteButtonToPublicationTable(); // Your existing method for delete buttons
    }

    private void setupReclamationTable() {
        reclamationTitreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        reclamationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        reclamationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reclamationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        addApproveButtonToReclamationTable();
        addDeleteButtonToReclamationTable();
    }

    private void loadPublications() {
        adminTableView.getItems().clear();
        adminTableView.getItems().addAll(servicePublication.getAll());
    }

    private void loadReclamations() {
        reclamationTableView.getItems().clear();
        reclamationTableView.getItems().addAll(serviceReclamation.getAll());
    }

    private void addDeleteButtonToPublicationTable() {
        deleteButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    servicePublication.delete(publication.getId());
                    loadPublications();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void addApproveButtonToReclamationTable() {
        approveColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approuver");
            {
                approveBtn.setOnAction(event -> {
                    Reclamation reclamation = getTableView().getItems().get(getIndex());
                    reclamation.setStatus("Approuvée");
                    serviceReclamation.update(reclamation);
                    loadReclamations();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : approveBtn);
            }
        });
    }

    private void addDeleteButtonToReclamationTable() {
        deleteReclamationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setOnAction(event -> {
                    Reclamation reclamation = getTableView().getItems().get(getIndex());
                    serviceReclamation.delete(reclamation.getId());
                    loadReclamations();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }
}
