package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Offer;
import services.OfferService;
import services.DemandeService;

import java.io.IOException;
import java.util.*;

public class StatisticsController {
    @FXML
    private Pane statisticsPane;

    private OfferService offerService;
    private DemandeService demandeService;

    // Predefined colors for statistics circles
    private final Color[] CIRCLE_COLORS = {
        Color.web("#3498db"), // Blue
        Color.web("#e74c3c"), // Red
        Color.web("#2ecc71"), // Green
        Color.web("#f39c12"), // Orange
        Color.web("#9b59b6"), // Purple
        Color.web("#1abc9c"), // Turquoise
        Color.web("#d35400"), // Dark Orange
        Color.web("#34495e"), // Dark Blue
        Color.web("#16a085"), // Green Sea
        Color.web("#c0392b"), // Dark Red
        Color.web("#8e44ad"), // Wisteria
        Color.web("#f1c40f")  // Yellow
    };

    @FXML
    private void initialize() {
        try {
            offerService = new OfferService();
            demandeService = new DemandeService();
            
            // Load statistics
            refreshStatistics();
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize statistics view: " + e.getMessage());
        }
    }
    
    @FXML
    private void refreshStatistics() {
        try {
            // Clear previous statistics
            statisticsPane.getChildren().clear();
            
            // Get counts of demands per offer
            Map<Integer, Integer> demandsPerOffer = demandeService.countDemandsPerOffer();
            
            if (demandsPerOffer.isEmpty()) {
                showNoDataMessage();
                return;
            }
            
            drawStatisticsCircles(demandsPerOffer);
        } catch (Exception e) {
            showError("Statistics Error", "Failed to load statistics: " + e.getMessage());
        }
    }
    
    @FXML
    private void showDetailedReport() {
        try {
            // Get demands per offer data
            Map<Integer, Integer> demandsPerOffer = demandeService.countDemandsPerOffer();
            
            if (demandsPerOffer.isEmpty()) {
                showMessage("No Data", "There is no statistical data available to display.");
                return;
            }
            
            // Create a dialog to show tabular data
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Detailed Statistics Report");
            dialog.setHeaderText("Demand Statistics by Offer");
            
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            
            // Create a table view
            TableView<StatisticRow> tableView = new TableView<>();
            tableView.setPrefWidth(500);
            tableView.setPrefHeight(400);
            
            // Define columns
            TableColumn<StatisticRow, String> offerNameCol = new TableColumn<>("Offer Name");
            offerNameCol.setCellValueFactory(new PropertyValueFactory<>("offerName"));
            offerNameCol.setPrefWidth(200);
            
            TableColumn<StatisticRow, String> domainCol = new TableColumn<>("Domain");
            domainCol.setCellValueFactory(new PropertyValueFactory<>("domain"));
            domainCol.setPrefWidth(150);
            
            TableColumn<StatisticRow, Integer> demandsCol = new TableColumn<>("Demands");
            demandsCol.setCellValueFactory(new PropertyValueFactory<>("demands"));
            demandsCol.setPrefWidth(100);
            
            TableColumn<StatisticRow, String> percentCol = new TableColumn<>("Percentage");
            percentCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            percentCol.setPrefWidth(100);
            
            tableView.getColumns().addAll(offerNameCol, domainCol, demandsCol, percentCol);
            
            // Calculate total demands for percentage
            int totalDemands = demandsPerOffer.values().stream().mapToInt(Integer::intValue).sum();
            
            // Create data for table
            List<StatisticRow> data = new ArrayList<>();
            
            // Sort offers by demand count (descending)
            List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(demandsPerOffer.entrySet());
            sortedEntries.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
            
            for (Map.Entry<Integer, Integer> entry : sortedEntries) {
                int offerId = entry.getKey();
                int count = entry.getValue();
                
                Offer offer = offerService.getOfferBasicInfoById(offerId);
                if (offer == null) continue;
                
                double percentage = (double) count / totalDemands * 100;
                String percentageStr = String.format("%.1f%%", percentage);
                
                data.add(new StatisticRow(offer.getNom(), offer.getDomain(), count, percentageStr));
            }
            
            tableView.getItems().addAll(data);
            
            // Add table to dialog
            dialogPane.setContent(tableView);
            
            // Show dialog
            dialog.showAndWait();
            
        } catch (Exception e) {
            showError("Report Error", "Failed to generate detailed report: " + e.getMessage());
        }
    }
    
    @FXML
    private void exportStatistics() {
        try {
            // Get demands per offer data
            Map<Integer, Integer> demandsPerOffer = demandeService.countDemandsPerOffer();
            
            if (demandsPerOffer.isEmpty()) {
                showMessage("No Data", "There is no statistical data available to export.");
                return;
            }
            
            // Create content for CSV file
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Offer Name,Domain,Number of Demands,Percentage\n");
            
            // Calculate total demands for percentage
            int totalDemands = demandsPerOffer.values().stream().mapToInt(Integer::intValue).sum();
            
            // Sort offers by demand count (descending)
            List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(demandsPerOffer.entrySet());
            sortedEntries.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
            
            for (Map.Entry<Integer, Integer> entry : sortedEntries) {
                int offerId = entry.getKey();
                int count = entry.getValue();
                
                Offer offer = offerService.getOfferBasicInfoById(offerId);
                if (offer == null) continue;
                
                double percentage = (double) count / totalDemands * 100;
                
                // Escape any commas in strings
                String escapedName = offer.getNom().replace(",", "\"\"");
                String escapedDomain = offer.getDomain().replace(",", "\"\"");
                
                csvContent.append(String.format("\"%s\",\"%s\",%d,%.1f%%\n", 
                    escapedName, escapedDomain, count, percentage));
            }
            
            // Show a success message with the content
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Statistics");
            alert.setHeaderText("Statistics exported successfully");
            alert.setContentText("In a production environment, this would save the following data to a CSV file:");
            
            TextArea textArea = new TextArea(csvContent.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(550);
            textArea.setPrefHeight(300);
            
            alert.getDialogPane().setExpandableContent(textArea);
            alert.getDialogPane().setExpanded(true);
            
            alert.showAndWait();
            
        } catch (Exception e) {
            showError("Export Error", "Failed to export statistics: " + e.getMessage());
        }
    }
    
    private void drawStatisticsCircles(Map<Integer, Integer> demandsPerOffer) throws Exception {
        // Find the maximum count to scale circles properly
        int maxCount = demandsPerOffer.values().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(1);
        
        // Base radius for circles
        final double baseRadius = 40;
        final double maxRadius = 80;
        
        // Sort offers by demand count (descending)
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(demandsPerOffer.entrySet());
        sortedEntries.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
        
        // Get the width and height of the pane
        double width = statisticsPane.getWidth();
        double height = statisticsPane.getHeight();
        
        // If width or height is 0 (not yet rendered), use default values
        if (width <= 0) width = 700;
        if (height <= 0) height = 400;
        
        // Calculate positions for the circles - arrange in a spiral pattern
        int totalCircles = Math.min(sortedEntries.size(), 12); // Limit to 12 circles
        
        // Calculate center of the pane
        double centerX = width / 2;
        double centerY = height / 2;
        
        // Draw circles
        for (int i = 0; i < totalCircles; i++) {
            Map.Entry<Integer, Integer> entry = sortedEntries.get(i);
            int offerId = entry.getKey();
            int count = entry.getValue();
            
            // Get offer details
            Offer offer = offerService.getOfferBasicInfoById(offerId);
            if (offer == null) continue;
            
            // Calculate circle size based on count relative to max count
            double ratio = (double) count / maxCount;
            double radius = baseRadius + (maxRadius - baseRadius) * ratio;
            
            // Calculate position - arrange in a circle formation
            double angle = 2 * Math.PI * i / totalCircles;
            double distance = 180; // Distance from center
            double x = centerX + Math.cos(angle) * distance;
            double y = centerY + Math.sin(angle) * distance;
            
            // Create and add circle
            Circle circle = new Circle(x, y, radius);
            circle.setFill(CIRCLE_COLORS[i % CIRCLE_COLORS.length]);
            circle.setOpacity(0.8);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            
            // Add tooltip to circle
            Tooltip tooltip = new Tooltip(offer.getNom() + "\nDomain: " + offer.getDomain() + "\nDemands: " + count);
            Tooltip.install(circle, tooltip);
            
            // Add text with count inside circle
            Text countText = new Text(String.valueOf(count));
            countText.setFill(Color.WHITE);
            countText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            countText.setX(x - countText.getLayoutBounds().getWidth() / 2);
            countText.setY(y + countText.getLayoutBounds().getHeight() / 4);
            
            // Add label below with offer name
            Text nameText = new Text(offer.getNom().length() > 12 ? 
                                     offer.getNom().substring(0, 12) + "..." : 
                                     offer.getNom());
            nameText.setTextAlignment(TextAlignment.CENTER);
            nameText.setWrappingWidth(100);
            nameText.setStyle("-fx-font-weight: bold;");
            nameText.setX(x - 50);
            nameText.setY(y + radius + 20);
            
            // Add label for domain
            Text domainText = new Text(offer.getDomain());
            domainText.setTextAlignment(TextAlignment.CENTER);
            domainText.setWrappingWidth(100);
            domainText.setStyle("-fx-font-style: italic; -fx-font-size: 11px;");
            domainText.setX(x - 50);
            domainText.setY(y + radius + 40);
            
            statisticsPane.getChildren().addAll(circle, countText, nameText, domainText);
        }
    }
    
    private void showNoDataMessage() {
        Text noDataText = new Text("No statistics data available");
        noDataText.setStyle("-fx-font-size: 18px;");
        noDataText.setX(statisticsPane.getWidth() / 2 - 100);
        noDataText.setY(statisticsPane.getHeight() / 2);
        statisticsPane.getChildren().add(noDataText);
    }
    
    @FXML
    private void goBack() {
        try {
            // Load the main view which has both calendar and statistics options
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestion_emploi/Views/main-view.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) statisticsPane.getScene().getWindow();
            
            // Set the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Navigation Error", "Failed to navigate back: " + e.getMessage());
        }
    }
    
    @FXML
    private void goBackToMain() {
        try {
            // Close current resources if any
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
            Stage stage = (Stage) statisticsPane.getScene().getWindow();
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
    
    // Helper class for TableView in detailed report
    public static class StatisticRow {
        private final String offerName;
        private final String domain;
        private final int demands;
        private final String percentage;
        
        public StatisticRow(String offerName, String domain, int demands, String percentage) {
            this.offerName = offerName;
            this.domain = domain;
            this.demands = demands;
            this.percentage = percentage;
        }
        
        public String getOfferName() { return offerName; }
        public String getDomain() { return domain; }
        public int getDemands() { return demands; }
        public String getPercentage() { return percentage; }
    }
}
