package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Offer;
import models.Demande;
import services.OfferService;
import services.DemandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FrontOfficeController {
    @FXML
    private TableView<Offer> offersTable;
    @FXML
    private TableColumn<Offer, String> titleColumn;
    @FXML
    private TableColumn<Offer, String> domainColumn;
    @FXML
    private TableColumn<Offer, String> descriptionColumn;
    @FXML
    private TableColumn<Offer, String> dateColumn;
    @FXML
    private TableColumn<Offer, Void> actionsColumn;
    @FXML
    private TextField searchField;
    @FXML
    private TextField serviceField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField cvFileField;
    @FXML
    private VBox demandForm;

    private OfferService offerService;
    private DemandeService demandeService;
    private ObservableList<Offer> offersList;
    private Offer selectedOffer;
    private File selectedCvFile;

    @FXML
    private void initialize() {
        try {
            offerService = new OfferService();
            demandeService = new DemandeService();
            
            // Initialize table columns
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            domainColumn.setCellValueFactory(new PropertyValueFactory<>("domain"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            dateColumn.setCellValueFactory(cellData -> {
                LocalDate date = cellData.getValue().getDate_offer();
                return new SimpleStringProperty(
                    date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            });
            
            // Add action buttons column
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button addDemandButton = new Button("Add Demand");
                
                {
                    addDemandButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                    addDemandButton.setOnAction(event -> {
                        Offer offer = getTableView().getItems().get(getIndex());
                        handleAddDemand(offer);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(addDemandButton);
                    }
                }
            });
            
            // Load offers
            loadOffers();
            
            // Set up search functionality
            setupSearch();
            
            // Hide demand form initially
            demandForm.setVisible(false);
            
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize front office: " + e.getMessage());
        }
    }
    
    private void loadOffers() throws Exception {
        List<Offer> offers = offerService.getAllOffers(100, 0, "date_offer", false);
        offersList = FXCollections.observableArrayList(offers);
        offersTable.setItems(offersList);
    }
    
    private void setupSearch() {
        FilteredList<Offer> filteredData = new FilteredList<>(offersList, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(offer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                return offer.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       offer.getDomain().toLowerCase().contains(lowerCaseFilter) ||
                       offer.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<Offer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(offersTable.comparatorProperty());
        offersTable.setItems(sortedData);
    }
    
    @FXML
    private void handleSearch() {
        // Search is handled by the listener in setupSearch()
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
    }
    
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CV File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx")
        );
        
        selectedCvFile = fileChooser.showOpenDialog(cvFileField.getScene().getWindow());
        if (selectedCvFile != null) {
            cvFileField.setText(selectedCvFile.getName());
        }
    }
    
    private void handleAddDemand(Offer offer) {
        selectedOffer = offer;
        demandForm.setVisible(true);
        serviceField.clear();
        descriptionField.clear();
        contactField.clear();
        cvFileField.clear();
        selectedCvFile = null;
    }
    
    @FXML
    private void handleSubmitDemand() {
        try {
            if (selectedOffer == null) {
                showError("Error", "No offer selected");
                return;
            }
            
            String service = serviceField.getText().trim();
            String description = descriptionField.getText().trim();
            String contact = contactField.getText().trim();
            
            if (service.isEmpty() || description.isEmpty() || contact.isEmpty()) {
                showError("Validation Error", "Please fill in all fields");
                return;
            }
            
            if (selectedCvFile == null) {
                showError("Validation Error", "Please select a CV file");
                return;
            }
            
            // Create new demand
            Demande demande = new Demande();
            demande.setService(service);
            demande.setPhone_number(contact);
            demande.setDate_demande(LocalDate.now());
            demande.setOffer_id(selectedOffer.getId());
            
            // Copy CV file to application directory
            String cvFileName = "cv_" + System.currentTimeMillis() + "_" + selectedCvFile.getName();
            Path targetPath = Path.of("uploads", cvFileName);
            Files.createDirectories(targetPath.getParent());
            Files.copy(selectedCvFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            demande.setCv_file_name(cvFileName);
            
            // Save demand
            demandeService.createDemande(demande);
            
            // Show success message
            showMessage("Success", "Demand submitted successfully");
            
            // Reset form
            demandForm.setVisible(false);
            selectedOffer = null;
            
        } catch (Exception e) {
            showError("Error", "Failed to submit demand: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelDemand() {
        demandForm.setVisible(false);
        selectedOffer = null;
        selectedCvFile = null;
    }
    
    @FXML
    private void goBackToMain() {
        try {
            // Close current resources
            if (offerService != null) {
                offerService.close();
            }
            if (demandeService != null) {
                demandeService.close();
            }
            
            // Load the main view
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/gestion_emploi/Views/main-view.fxml")
            );
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) offersTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            showError("Navigation Error", "Could not return to main menu: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void shutdown() {
        try {
            if (offerService != null) {
                offerService.close();
            }
            if (demandeService != null) {
                demandeService.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing services: " + e.getMessage());
        }
    }
} 