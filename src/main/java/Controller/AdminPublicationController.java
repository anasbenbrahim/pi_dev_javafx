package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modele.Publication;
import modele.Reclamation;
import services.ServicePublication;
import services.ServiceReclamation;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPublicationController implements Initializable {

    @FXML private TableView<Publication> adminTableView;
    @FXML private TableColumn<Publication, String> titreColumn;
    @FXML private TableColumn<Publication, String> descriptionColumn;
    @FXML private TableColumn<Publication, String> dateColumn;
    @FXML private TableColumn<Publication, String> imageColumn;
    @FXML private TableColumn<Publication, Void> deleteButtonColumn;
    @FXML private TableView<Reclamation> reclamationTableView;
    @FXML private TableColumn<Reclamation, String> reclamationTitreColumn;
    @FXML private TableColumn<Reclamation, String> reclamationDescriptionColumn;
    @FXML private TableColumn<Reclamation, String> reclamationDateColumn;
    @FXML private TableColumn<Reclamation, String> reclamationStatusColumn;
    @FXML private TableColumn<Reclamation, Void> approveColumn;
    @FXML private TableColumn<Reclamation, Void> deleteReclamationColumn;
    @FXML private Button retourButton;

    private final ServicePublication servicePublication = new ServicePublication();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private NavigationManager navigationManager;
    private Runnable refreshCallback;

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

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
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

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
                        setGraphic(null);
                    }
                }
            }
        });

        addDeleteButtonToPublicationTable();
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
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    servicePublication.delete(publication.getId());
                    loadPublications();
                    if (refreshCallback != null) {
                        System.out.println("Triggering refresh callback from AdminPublicationController (delete publication)");
                        refreshCallback.run();
                    }
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
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
                approveBtn.setOnAction(event -> {
                    Reclamation reclamation = getTableView().getItems().get(getIndex());
                    reclamation.setStatus("Approuvée");
                    serviceReclamation.update(reclamation);
                    loadReclamations();
                });
            }
            @Override
            protected void updateItem(Void  item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : approveBtn);
            }
        });
    }

    private void addDeleteButtonToReclamationTable() {
        deleteReclamationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
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

    @FXML
    private void goBack() {
        navigationManager.goBack();
    }
}