package controllers;

import models.Offer;
import services.OfferService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class OfferController {
    @FXML private TableView<Offer> offerTable;
    @FXML private TableColumn<Offer, Integer> idColumn;
    @FXML private TableColumn<Offer, String> nomColumn;
    @FXML private TableColumn<Offer, String> domainColumn;
    @FXML private TableColumn<Offer, LocalDate> date_offerColumn;
    @FXML private TableColumn<Offer, Integer> nb_placesColumn;

    @FXML private TextField nomField;
    @FXML private TextField domainField;
    @FXML private DatePicker date_offerPicker;
    @FXML private TextArea descriptionArea;
    @FXML private Spinner<Integer> nb_placesSpinner;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private TextField searchField;

    private OfferService offerService;
    private final ObservableList<Offer> offers = FXCollections.observableArrayList();
    private final ObservableList<Offer> filteredOffers = FXCollections.observableArrayList();
    private Offer selectedOffer;

    @FXML
    private void initialize() {
        try {
            offerService = new OfferService();
            setupTableColumns();
            setupSpinner();
            loadOffers();
            setupTableSelection();
            setupSearchListener();

            // Initialize buttons as disabled
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        domainColumn.setCellValueFactory(new PropertyValueFactory<>("domain"));
        date_offerColumn.setCellValueFactory(new PropertyValueFactory<>("date_offer"));
        nb_placesColumn.setCellValueFactory(new PropertyValueFactory<>("nb_places"));

        // Enable table sorting
        offerTable.setSortPolicy(table -> true);
    }

    private void setupSpinner() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        nb_placesSpinner.setValueFactory(valueFactory);
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOffers(newValue);
        });
    }

    private void filterOffers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            offerTable.setItems(offers);
            return;
        }

        String lowerCaseSearch = searchText.toLowerCase();
        filteredOffers.clear();
        
        for (Offer offer : offers) {
            if (offer.getNom() != null && offer.getNom().toLowerCase().contains(lowerCaseSearch) ||
                offer.getDomain() != null && offer.getDomain().toLowerCase().contains(lowerCaseSearch) ||
                offer.getDescription() != null && offer.getDescription().toLowerCase().contains(lowerCaseSearch)) {
                filteredOffers.add(offer);
            }
        }
        
        offerTable.setItems(filteredOffers);
    }

    @FXML
    private void handleSearch() {
        filterOffers(searchField.getText());
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        offerTable.setItems(offers);
    }

    private void loadOffers() {
        try {
            offers.setAll(offerService.getAllOffers(100, 0, "date_offer", false));
            offerTable.setItems(offers);
        } catch (Exception e) {
            showAlert("Error", "Failed to load offers: " + e.getMessage());
        }
    }

    private void setupTableSelection() {
        offerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOffer = newSelection;
            // Enable/disable buttons based on selection
            boolean hasSelection = (newSelection != null);
            updateButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);

            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void populateForm(Offer offer) {
        if (offer == null) return;

        nomField.setText(offer.getNom());
        domainField.setText(offer.getDomain());
        date_offerPicker.setValue(offer.getDate_offer());
        descriptionArea.setText(offer.getDescription());
        nb_placesSpinner.getValueFactory().setValue(offer.getNb_places());
    }

    private boolean validateForm() {
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a name");
            return false;
        }
        if (domainField.getText() == null || domainField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a domain");
            return false;
        }
        if (date_offerPicker.getValue() == null) {
            showAlert("Error", "Please select a date");
            return false;
        }
        if (date_offerPicker.getValue().isBefore(LocalDate.now())) {
            showAlert("Error", "Date cannot be in the past");
            return false;
        }
        if (nb_placesSpinner.getValue() <= 0) {
            showAlert("Error", "Number of places must be positive");
            return false;
        }
        return true;
    }

    @FXML
    private void handleAddOffer() {
        if (!validateForm()) return;

        Offer newOffer = new Offer(
                0, // ID will be generated by database
                nomField.getText().trim(),
                domainField.getText().trim(),
                date_offerPicker.getValue(),
                descriptionArea.getText().trim(),
                nb_placesSpinner.getValue()
        );

        try {
            offerService.createOffer(newOffer);
            offers.add(newOffer);
            showSuccess("Offer added successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to add offer: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateOffer() {
        if (selectedOffer == null) {
            showAlert("Warning", "No offer selected for update");
            return;
        }
        if (!validateForm()) return;

        selectedOffer.setNom(nomField.getText().trim());
        selectedOffer.setDomain(domainField.getText().trim());
        selectedOffer.setDate_offer(date_offerPicker.getValue());
        selectedOffer.setDescription(descriptionArea.getText().trim());
        selectedOffer.setNb_places(nb_placesSpinner.getValue());

        try {
            offerService.updateOffer(selectedOffer);
            offerTable.refresh();
            showSuccess("Offer updated successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to update offer: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteOffer() {
        if (selectedOffer == null) {
            showAlert("Warning", "No offer selected for deletion");
            return;
        }

        try {
            offerService.deleteOffer(selectedOffer.getId());
            offers.remove(selectedOffer);
            showSuccess("Offer deleted successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to delete offer: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        nomField.clear();
        domainField.clear();
        date_offerPicker.setValue(LocalDate.now());
        descriptionArea.clear();
        nb_placesSpinner.getValueFactory().setValue(1);
        offerTable.getSelectionModel().clearSelection();
        selectedOffer = null;
        // Disable buttons when form is cleared
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleRefresh() {
        loadOffers();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndExit(String title, String message) {
        showAlert(title, message);
        Platform.exit();
    }

    public void shutdown() {
        // Clean up resources
        if (offerService != null) {
            try {
                if (offerService instanceof AutoCloseable) {
                    ((AutoCloseable)offerService).close();
                }
            } catch (Exception ignored) {}
        }
    }

    // Cleanup resources on exit
    private void closeResources() {
        try {
            if (offerService != null) {
                offerService.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
    
    @FXML
    private void goBackToMain() {
        try {
            // Close current resources
            closeResources();
            
            // Load the main view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/example/gestion_emploi/Views/main-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get current stage and set new scene
            javafx.stage.Stage stage = (javafx.stage.Stage) nomField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not return to main menu: " + e.getMessage());
        }
    }
}
