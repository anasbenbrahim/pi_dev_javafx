package controllers;

import models.Demande;
import models.Offer;
import services.DemandeService;
import services.OfferService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import services.PDFViewerService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class DemandeController {
    // FXML injections (keeping all original property names)
    @FXML private TableView<Demande> demandeTable;
    @FXML private TableColumn<Demande, Integer> idColumn;
    @FXML private TableColumn<Demande, Integer> offer_idColumn;
    @FXML private TableColumn<Demande, String> serviceColumn;
    @FXML private TableColumn<Demande, String> phone_numberColumn;
    @FXML private ComboBox<Offer> offerCombo;
    @FXML private TextField serviceField;
    @FXML private DatePicker date_demandePicker;
    @FXML private TextField cv_file_nameField;
    @FXML private TextField phone_numberField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button viewPDFButton;

    private DemandeService demandeService;
    private OfferService offerService;
    private final ObservableList<Demande> demandes = FXCollections.observableArrayList();
    private final ObservableList<Offer> offers = FXCollections.observableArrayList();
    private Demande selectedDemande;

    @FXML
    private void initialize() {
        try {
            demandeService = new DemandeService();
            offerService = new OfferService();
            setupTableColumns();
            loadOffers();
            loadDemandes();  // Now with pagination
            setupTableSelection();
            date_demandePicker.setValue(LocalDate.now());

            // Initialize buttons as disabled
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        } catch (Exception e) {
            showAlertAndExit("Fatal Error", "Failed to initialize: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        offer_idColumn.setCellValueFactory(new PropertyValueFactory<>("offer_id"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        phone_numberColumn.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

        // Enable table sorting
        demandeTable.setSortPolicy(table -> true);
    }

    private void loadOffers() {
        try {
            offers.setAll(offerService.getAllOffers(100, 0, "date_offer", false));
            offerCombo.setItems(offers);
        } catch (Exception e) {
            showAlert("Error", "Failed to load offers: " + e.getMessage());
        }
    }


    private void loadDemandes() {
        try {
            // Show first 100 records (adjust as needed)
            demandes.setAll(demandeService.getAllDemandes(100, 0));
            demandeTable.setItems(demandes);
        } catch (Exception e) {
            showAlert("Error", "Failed to load demandes: " + e.getMessage());
        }
    }

    private void setupTableSelection() {
        demandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedDemande = newSelection;
            // Enable/disable buttons based on selection
            boolean hasSelection = (newSelection != null);
            updateButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
            viewPDFButton.setDisable(!hasSelection || newSelection.getCv_file_name() == null || newSelection.getCv_file_name().isEmpty());

            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void populateForm(Demande demande) {
        if (demande == null) return;

        offerCombo.getSelectionModel().select(findOfferById(demande.getOffer_id()));
        serviceField.setText(demande.getService());
        date_demandePicker.setValue(demande.getDate_demande());
        cv_file_nameField.setText(demande.getCv_file_name());
        phone_numberField.setText(demande.getPhone_number());
    }

    private Offer findOfferById(int id) {
        return offers.stream()
                .filter(offer -> offer.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private boolean validateForm() {
        if (offerCombo.getValue() == null) {
            showAlert("Error", "Please select an offer");
            return false;
        }
        if (serviceField.getText() == null || serviceField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a service");
            return false;
        }
        if (date_demandePicker.getValue() == null) {
            showAlert("Error", "Please select a date");
            return false;
        }
        if (date_demandePicker.getValue().isAfter(LocalDate.now())) {
            showAlert("Error", "Date cannot be in the future");
            return false;
        }
        if (phone_numberField.getText() == null || phone_numberField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a phone number");
            return false;
        }
        if (!phone_numberField.getText().matches("^\\d{11}$")) {
            showAlert("Error", "Phone number must be : 216 ******** ");
            return false;
        }
        return true;
    }

    @FXML
    private void handleAddDemande() {
        if (!validateForm()) return;

        Demande newDemande = new Demande(
                0, // ID will be generated by database
                offerCombo.getValue().getId(),
                serviceField.getText().trim(),
                date_demandePicker.getValue(),
                cv_file_nameField.getText().trim(),
                phone_numberField.getText().trim()
        );

        try {
            demandeService.createDemande(newDemande);
            demandes.add(newDemande);
            showSuccess("Demande added successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to add demande: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateDemande() {
        if (selectedDemande == null) {
            showAlert("Warning", "No demande selected for update");
            return;
        }
        if (!validateForm()) return;

        selectedDemande.setOffer_id(offerCombo.getValue().getId());
        selectedDemande.setService(serviceField.getText().trim());
        selectedDemande.setDate_demande(date_demandePicker.getValue());
        selectedDemande.setCv_file_name(cv_file_nameField.getText().trim());
        selectedDemande.setPhone_number(phone_numberField.getText().trim());

        try {
            demandeService.updateDemande(selectedDemande);
            demandeTable.refresh();
            showSuccess("Demande updated successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to update demande: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteDemande() {
        if (selectedDemande == null) {
            showAlert("Warning", "No demande selected for deletion");
            return;
        }

        try {
            demandeService.deleteDemande(selectedDemande.getId());
            demandes.remove(selectedDemande);
            showSuccess("Demande deleted successfully");
            handleClearForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to delete demande: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        offerCombo.getSelectionModel().clearSelection();
        serviceField.clear();
        date_demandePicker.setValue(LocalDate.now());
        cv_file_nameField.clear();
        phone_numberField.clear();
        demandeTable.getSelectionModel().clearSelection();
        selectedDemande = null;
        // Disable buttons when form is cleared
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Create cvs directory if it doesn't exist
                Path cvsDir = Paths.get("cvs");
                if (!Files.exists(cvsDir)) {
                    Files.createDirectory(cvsDir);
                }

                // Generate unique filename to prevent overwriting
                String originalFilename = selectedFile.getName();
                String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
                Path destinationPath = cvsDir.resolve(uniqueFilename);

                // Copy the file to cvs directory
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Update the cv_file_name field with the unique filename
                cv_file_nameField.setText(uniqueFilename);
                
            } catch (IOException e) {
                showAlert("Error", "Failed to copy CV file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadOffers();
        loadDemandes();
    }

    @FXML
    private void handleViewPDF() {
        if (selectedDemande == null || selectedDemande.getCv_file_name() == null || selectedDemande.getCv_file_name().isEmpty()) {
            showAlert("Warning", "No PDF file selected or available");
            return;
        }

        try {
            // Check if the file exists in the cvs directory
            Path pdfPath = Paths.get("cvs", selectedDemande.getCv_file_name());
            if (!Files.exists(pdfPath)) {
                showAlert("Error", "CV file not found: " + pdfPath);
                return;
            }

            // View the PDF using PDFViewerService
            PDFViewerService.viewPDF(pdfPath.toString());
        } catch (Exception e) {
            showAlert("Error", "Failed to view PDF: " + e.getMessage());
        }
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

    // Cleanup resources on exit
    private void closeResources() {
        try {
            if (demandeService != null) {
                demandeService.close();
            }
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
            javafx.stage.Stage stage = (javafx.stage.Stage) serviceField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not return to main menu: " + e.getMessage());
        }
    }

    public void shutdown() {
        // Clean up resources
        if (demandeService != null) {
            try { demandeService.close(); } catch (Exception ignored) {}
        }
        if (offerService != null) {
            try { offerService.close(); } catch (Exception ignored) {}
        }
    }
}