package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modele.Publication;
import modele.Reclamation;
import services.ServicePublication;
import services.ServiceReclamation;

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

    private final ServiceReclamation reclamationService = new ServiceReclamation();
    private final ServicePublication publicationService = new ServicePublication();
    private int clientId;

    public void setClientId(int clientId) {
        this.clientId = clientId;
        loadReclamations();
    }

    @FXML
    public void initialize() {
        // Configure table columns
        pubTitleColumn.setCellValueFactory(cellData -> {
            Publication pub = publicationService.getById(cellData.getValue().getPublicationId());
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    pub != null ? pub.getTitre() : "Unknown");
        });
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action buttons
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

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Réclamation");
            stage.show();
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
}