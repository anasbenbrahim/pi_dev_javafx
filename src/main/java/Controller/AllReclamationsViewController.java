package Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import modele.Reclamation;
import services.ServiceReclamation;
import services.ServicePublication;

import java.io.IOException;
import java.time.LocalDate;

public class AllReclamationsViewController {
    @FXML private TableView<Reclamation> reclamationsTable;
    @FXML private TableColumn<Reclamation, String> pubTitleColumn;
    @FXML private TableColumn<Reclamation, String> titreColumn;
    @FXML private TableColumn<Reclamation, String> descriptionColumn;
    @FXML private TableColumn<Reclamation, LocalDate> dateColumn;
    @FXML private TableColumn<Reclamation, String> statusColumn;
    @FXML private TableColumn<Reclamation, Void> actionsColumn;
    @FXML private Button retourButton;

    private final ServiceReclamation reclamationService = new ServiceReclamation();
    private final ServicePublication publicationService = new ServicePublication();
    private int clientId;
    private NavigationManager navigationManager;

    public void setClientId(int clientId) {
        this.clientId = clientId;
        loadReclamations();
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    public void initialize() {
        pubTitleColumn.setCellValueFactory(cellData -> {
            String pubTitle = publicationService.getById(cellData.getValue().getPublicationId()).getTitre();
            return javafx.beans.binding.Bindings.createStringBinding(() -> pubTitle != null ? pubTitle : "Unknown");
        });
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        addActionButtonsToTable();
    }

    private void loadReclamations() {
        reclamationsTable.getItems().setAll(reclamationService.getByClientId(clientId));
    }

    private void addActionButtonsToTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttonsPane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 5; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 5; -fx-cursor: hand;");

                editBtn.setOnAction(event -> {
                    Reclamation reclamation = getTableView().getItems().get(getIndex());
                    editReclamation(reclamation);
                });

                deleteBtn.setOnAction(event -> {
                    Reclamation reclamation = getTableView().getItems().get(getIndex());
                    deleteReclamation(reclamation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsPane);
            }
        });
    }

    private void editReclamation(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditReclamation.fxml"));
            Parent root = loader.load();
            EditReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);
            controller.setOnReclamationUpdated(this::loadReclamations);
            controller.setNavigationManager(navigationManager);
            navigationManager.navigateTo(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur de réclamation");
        }
    }

    private void deleteReclamation(Reclamation reclamation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer Réclamation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            reclamationService.delete(reclamation.getId());
            loadReclamations();
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