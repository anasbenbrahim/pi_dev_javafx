package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import models.Offer;
import models.Demande;
import services.OfferService;
import services.DemandeService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class MainController {
    @FXML
    private ListView<String> offersListView;
    
    @FXML
    private ListView<String> demandsListView;
    
    @FXML
    private TextArea detailsTextArea;
    
    @FXML
    private VBox detailsPane;

    // Services
    private OfferService offerService;
    private DemandeService demandeService;
    
    // Data
    private List<Offer> offersList = new ArrayList<>();
    private List<Demande> demandesList = new ArrayList<>();

    // Constants for view paths (prevents hardcoding strings)
    private static final String MAIN_VIEW = "/com/example/gestion_emploi/Views/main-view.fxml";
    private static final String OFFER_VIEW = "/com/example/gestion_emploi/Views/offer-view.fxml";
    private static final String DEMANDE_VIEW = "/com/example/gestion_emploi/Views/demande-view.fxml";
    private static final String CALENDAR_VIEW = "/com/example/gestion_emploi/Views/calendar-view.fxml";
    private static final String STATISTICS_VIEW = "/com/example/gestion_emploi/Views/statistics-view.fxml";

    @FXML
    private void initialize() {
        try {
            // Initialize services
            offerService = new OfferService();
            demandeService = new DemandeService();
            
            // Set up list view selection listeners
            setupListViewListeners();
            
            // Load initial data
            refreshMainViewData();
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize main view: " + e.getMessage(), e);
        }
    }
    
    private void setupListViewListeners() {
        // Offers list selection listener
        offersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && offersList != null && !offersList.isEmpty()) {
                int selectedIndex = offersListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < offersList.size()) {
                    Offer selectedOffer = offersList.get(selectedIndex);
                    displayOfferDetails(selectedOffer);
                    
                    // Clear selection in the other list
                    demandsListView.getSelectionModel().clearSelection();
                }
            }
        });
        
        // Demands list selection listener
        demandsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && demandesList != null && !demandesList.isEmpty()) {
                int selectedIndex = demandsListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < demandesList.size()) {
                    Demande selectedDemande = demandesList.get(selectedIndex);
                    displayDemandeDetails(selectedDemande);
                    
                    // Clear selection in the other list
                    offersListView.getSelectionModel().clearSelection();
                }
            }
        });
    }
    
    @FXML
    private void refreshMainViewData() {
        try {
            // Load offers data
            loadOffersData();
            
            // Load demands data
            loadDemandsData();
            
            // Clear details
            detailsTextArea.clear();
        } catch (Exception e) {
            showError("Data Refresh Error", "Failed to refresh data: " + e.getMessage(), e);
        }
    }
    
    private void loadOffersData() throws SQLException {
        try {
            // Get latest offers (limit to 10 for performance)
            offersList = offerService.getRecentOffers(10);
            
            // Create display list
            ObservableList<String> offerDisplayList = FXCollections.observableArrayList();
            
            for (Offer offer : offersList) {
                offerDisplayList.add(offer.getNom() + " (" + offer.getDomain() + ")");
            }
            
            // Update the list view
            offersListView.setItems(offerDisplayList);
        } catch (SQLException e) {
            offersList = new ArrayList<>(); // Reset to empty list on error
            throw e; // Rethrow for handling in calling method
        }
    }
    
    private void loadDemandsData() throws SQLException {
        try {
            // Get latest demands (limit to 10 for performance)
            demandesList = demandeService.getRecentDemandes(10);
            
            // Create display list
            ObservableList<String> demandeDisplayList = FXCollections.observableArrayList();
            
            for (Demande demande : demandesList) {
                // Try to get associated offer name
                String offerName = "Unknown Offer";
                try {
                    Offer offer = offerService.getOfferBasicInfoById(demande.getOffer_id());
                    if (offer != null) {
                        offerName = offer.getNom();
                    }
                } catch (Exception e) {
                    // If offer can't be found, just use the ID
                    offerName = "Offer #" + demande.getOffer_id();
                }
                
                // Use service field as the display name if nom isn't available
                String displayName = demande.getService() != null ? demande.getService() : "Demand #" + demande.getId();
                demandeDisplayList.add(displayName + " - " + offerName);
            }
            
            // Update the list view
            demandsListView.setItems(demandeDisplayList);
        } catch (SQLException e) {
            demandesList = new ArrayList<>(); // Reset to empty list on error
            throw e; // Rethrow for handling in calling method
        }
    }
    
    private void displayOfferDetails(Offer offer) {
        StringBuilder details = new StringBuilder();
        details.append("OFFER DETAILS:\n\n");
        details.append("ID: ").append(offer.getId()).append("\n");
        details.append("Name: ").append(offer.getNom()).append("\n");
        details.append("Domain: ").append(offer.getDomain()).append("\n");
        

        
        // Add number of places if available
        try {
            details.append("Number of Places: ").append(offer.getNb_places()).append("\n");
        } catch (Exception e) {
            // Skip if getter doesn't exist or throws exception
        }
        
        // Add description if available
        try {
            if (offer.getDescription() != null && !offer.getDescription().isEmpty()) {
                details.append("\nDescription: ").append(offer.getDescription()).append("\n");
            }
        } catch (Exception e) {
            // Skip if getter doesn't exist or throws exception
        }
        
        detailsTextArea.setText(details.toString());
    }
    
    private void displayDemandeDetails(Demande demande) {
        StringBuilder details = new StringBuilder();
        details.append("DEMAND DETAILS:\n\n");
        details.append("ID: ").append(demande.getId()).append("\n");
        
        // The Demande model doesn't have a 'nom' field, but it might have been added
        try {
            // Use reflection to check if getNom exists
            java.lang.reflect.Method getNomMethod = Demande.class.getMethod("getNom");
            String nom = (String) getNomMethod.invoke(demande);
            if (nom != null && !nom.isEmpty()) {
                details.append("Name: ").append(nom).append("\n");
            }
        } catch (Exception e) {
            // No 'nom' field available - not an issue
        }
        
        details.append("Offer ID: ").append(demande.getOffer_id()).append("\n");
        
        // Try to get associated offer name
        try {
            Offer offer = offerService.getOfferBasicInfoById(demande.getOffer_id());
            if (offer != null) {
                details.append("Offer Name: ").append(offer.getNom()).append("\n");
                details.append("Domain: ").append(offer.getDomain()).append("\n");
            }
        } catch (Exception e) {
            details.append("Offer: Unable to load offer details\n");
        }
        
        // Add service if available
        if (demande.getService() != null && !demande.getService().isEmpty()) {
            details.append("Service: ").append(demande.getService()).append("\n");
        }
        
        // Add date if available
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (demande.getDate_demande() != null) {
            details.append("Date: ").append(demande.getDate_demande().format(formatter)).append("\n");
        }
        
        // Add CV file name if available
        if (demande.getCv_file_name() != null && !demande.getCv_file_name().isEmpty()) {
            details.append("CV File: ").append(demande.getCv_file_name()).append("\n");
        }
        
        // Add phone number if available
        if (demande.getPhone_number() != null && !demande.getPhone_number().isEmpty()) {
            details.append("Phone: ").append(demande.getPhone_number()).append("\n");
        }
        
        // Try to get description if available - using reflection since it might not exist
        try {
            java.lang.reflect.Method getDescriptionMethod = Demande.class.getMethod("getDescription");
            String description = (String) getDescriptionMethod.invoke(demande);
            if (description != null && !description.isEmpty()) {
                details.append("\nDescription: ").append(description).append("\n");
            }
        } catch (Exception e) {
            // No 'description' field available - not an issue
        }
        
        detailsTextArea.setText(details.toString());
    }

    @FXML
    private void showOfferManagement() {
        loadView(OFFER_VIEW);
    }

    @FXML
    private void showDemandeManagement() {
        loadView(DEMANDE_VIEW);
    }

    @FXML
    private void showCalendarView() {
        loadView(CALENDAR_VIEW);
    }
    
    @FXML
    private void showStatisticsView() {
        loadView(STATISTICS_VIEW);
    }

    private void loadView(String fxmlPath) {
        try {
            // Don't load main view to prevent recursion
            if (MAIN_VIEW.equals(fxmlPath)) {
                return;
            }

            // More reliable resource loading
            Parent view = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(fxmlPath)
            ));
            
            // Get the current stage from any visible node
            Stage stage = (Stage) offersListView.getScene().getWindow();
            
            // Create new scene
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.show();
            
            // Close the services if needed
            closeServices();
        } catch (NullPointerException e) {
            showError("FXML Not Found", "File missing: " + fxmlPath, e);
        } catch (IOException e) {
            showError("Load Error", "Failed to load: " + fxmlPath, e);
        } catch (Exception e) {
            showError("Unexpected Error", "Something went wrong", e);
        }
    }
    
    private void closeServices() {
        try {
            if (offerService != null) {
                offerService.close();
            }
            if (demandeService != null) {
                demandeService.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing services: " + e.getMessage());
        }
    }

    private void showError(String title, String message, Exception e) {
        // User-friendly error alert
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Application Error");
        alert.setContentText(message + "\n\nDetails: " + e.getMessage());
        alert.showAndWait();

        // Debug output (optional)
        System.err.println("ERROR: " + title);
        e.printStackTrace();
    }

    // Interface for child controllers if needed
    public interface ChildController {
        void setMainController(MainController mainController);
    }

    @FXML
    private void openFrontOffice(ActionEvent event) {
        try {
            // Load the front office view
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/gestion_emploi/Views/front-office-view.fxml")
            );
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            showError("Navigation Error", "Could not open front office: " + e.getMessage(), e);
        }
    }
}