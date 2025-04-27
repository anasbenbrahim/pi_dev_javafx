package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Offer;
import models.Demande;
import services.OfferService;
import services.DemandeService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CalendarController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private VBox calendarContainer;
    @FXML
    private GridPane calendarGrid;

    private OfferService offerService;
    private DemandeService demandeService;
    private Map<LocalDate, List<Object>> eventsMap;
    private YearMonth currentYearMonth;

    @FXML
    private void initialize() {
        try {
            offerService = new OfferService();
            demandeService = new DemandeService();
            eventsMap = new HashMap<>();
            currentYearMonth = YearMonth.now();

            // Set up date picker to current date
            datePicker.setValue(LocalDate.now());
            
            // Initial calendar update
            updateCalendar();
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize calendar view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDatePicker() {
        try {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                currentYearMonth = YearMonth.from(selectedDate);
                updateCalendar();
            }
        } catch (Exception e) {
            showError("Error", "Failed to handle date selection: " + e.getMessage());
        }
    }
    
    @FXML
    private void showToday() {
        try {
            currentYearMonth = YearMonth.now();
            datePicker.setValue(LocalDate.now());
            updateCalendar();
        } catch (Exception e) {
            showError("Error", "Failed to show today: " + e.getMessage());
        }
    }
    
    @FXML
    private void showPreviousMonth() {
        try {
            currentYearMonth = currentYearMonth.minusMonths(1);
            datePicker.setValue(currentYearMonth.atDay(1));
            updateCalendar();
        } catch (Exception e) {
            showError("Error", "Failed to show previous month: " + e.getMessage());
        }
    }
    
    @FXML
    private void showNextMonth() {
        try {
            currentYearMonth = currentYearMonth.plusMonths(1);
            datePicker.setValue(currentYearMonth.atDay(1));
            updateCalendar();
        } catch (Exception e) {
            showError("Error", "Failed to show next month: " + e.getMessage());
        }
    }
    
    @FXML
    private void openStatisticsView() {
        try {
            // Load statistics view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestion_emploi/Views/statistics-view.fxml"));
            Parent statisticsView = loader.load();
            
            // Create new scene and set it on current stage
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.setScene(new Scene(statisticsView));
        } catch (IOException e) {
            showError("Navigation Error", "Could not open statistics view: " + e.getMessage());
        }
    }
    
    @FXML
    private void goBackToMain() {
        try {
            // Close current resources
            closeResources();
            
            // Load the main view
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/gestion_emploi/Views/main-view.fxml")
            );
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            showError("Navigation Error", "Could not return to main menu: " + e.getMessage());
        }
    }

    private void updateCalendar() {
        try {
            // Clear previous calendar
            calendarGrid.getChildren().clear();
            calendarGrid.getColumnConstraints().clear();
            calendarGrid.getRowConstraints().clear();

            // Set up grid constraints
            for (int i = 0; i < 7; i++) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(100.0 / 7);
                calendarGrid.getColumnConstraints().add(column);
            }

            // Add day names
            String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (int i = 0; i < 7; i++) {
                Label dayLabel = new Label(dayNames[i]);
                dayLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
                calendarGrid.add(dayLabel, i, 0);
            }

            // Get the first day of the month and adjust to start from Sunday
            LocalDate firstDay = currentYearMonth.atDay(1);
            int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Convert to 0-6 (Sun-Sat)

            // Load events for the current month
            loadEventsForMonth();

            // Fill the calendar
            int day = 1;
            int maxDays = currentYearMonth.lengthOfMonth();
            int row = 1;

            // Add empty cells for days before the first day of the month
            for (int i = 0; i < dayOfWeek; i++) {
                VBox cell = createEmptyCell();
                calendarGrid.add(cell, i, row);
            }

            // Add cells for each day of the month
            while (day <= maxDays) {
                for (int i = dayOfWeek; i < 7 && day <= maxDays; i++) {
                    LocalDate date = currentYearMonth.atDay(day);
                    VBox cell = createDayCell(date, day);
                    calendarGrid.add(cell, i, row);
                    day++;
                }
                dayOfWeek = 0;
                row++;
            }

        } catch (Exception e) {
            showError("Error", "Failed to update calendar: " + e.getMessage());
        }
    }

    private void loadEventsForMonth() throws Exception {
        eventsMap.clear();
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        // Load offers
        List<Offer> offers = offerService.getAllOffers();
        for (Offer offer : offers) {
            LocalDate offerDate = offer.getDate_offer();
            if (offerDate != null && !offerDate.isBefore(startDate) && !offerDate.isAfter(endDate)) {
                eventsMap.computeIfAbsent(offerDate, k -> new ArrayList<>()).add(offer);
            }
        }

        // Load demands
        List<Demande> demands = demandeService.getAllDemandes();
        for (Demande demande : demands) {
            LocalDate demandeDate = demande.getDate_demande();
            if (demandeDate != null && !demandeDate.isBefore(startDate) && !demandeDate.isAfter(endDate)) {
                eventsMap.computeIfAbsent(demandeDate, k -> new ArrayList<>()).add(demande);
            }
        }
    }

    private VBox createDayCell(LocalDate date, int day) {
        VBox cell = new VBox(2);
        cell.getStyleClass().add("calendar-day-cell");
        cell.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 5;");
        cell.setPrefHeight(100);
        
        // Style for today's date
        if (date.equals(LocalDate.now())) {
            cell.setStyle("-fx-border-color: #2980b9; -fx-border-width: 2; -fx-background-color: #d6eaf8; -fx-padding: 5;");
        } 
        // Style for weekends
        else if (date.getDayOfWeek().getValue() >= 6) { // Saturday or Sunday
            cell.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f5f5f5; -fx-padding: 5;");
        }

        // Add day number
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.getStyleClass().add("date-label");
        dayLabel.setStyle("-fx-font-weight: bold;");
        
        // Make today's day number stand out
        if (date.equals(LocalDate.now())) {
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");
        }
        
        cell.getChildren().add(dayLabel);

        // Add events
        List<Object> events = eventsMap.get(date);
        if (events != null) {
            for (Object event : events) {
                if (event instanceof Offer) {
                    Offer offer = (Offer) event;
                    Label offerLabel = new Label(offer.getNom());
                    offerLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 3 5; " +
                                       "-fx-background-radius: 3; -fx-font-size: 11px; -fx-max-width: 150;");
                    offerLabel.setWrapText(true);
                    offerLabel.setMaxWidth(Double.MAX_VALUE);
                    cell.getChildren().add(offerLabel);
                } else if (event instanceof Demande) {
                    Demande demande = (Demande) event;
                    Label demandeLabel = new Label(demande.getService());
                    demandeLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 3 5; " +
                                         "-fx-background-radius: 3; -fx-font-size: 11px; -fx-max-width: 150;");
                    demandeLabel.setWrapText(true);
                    demandeLabel.setMaxWidth(Double.MAX_VALUE);
                    cell.getChildren().add(demandeLabel);
                }
            }
        }

        return cell;
    }

    private VBox createEmptyCell() {
        VBox cell = new VBox();
        cell.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: #f9f9f9;");
        cell.setPrefHeight(100);
        return cell;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeResources() {
        try {
            if (offerService != null) {
                offerService.close();
            }
            if (demandeService != null) {
                demandeService.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
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