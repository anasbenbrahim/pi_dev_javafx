package esprit.tn.controllers;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import esprit.tn.services.ReservationService;
import java.sql.SQLException;

public class CalendarController {

    @FXML private FlowPane calendar;
    @FXML private Text year;
    @FXML private Text month;
    private LocalDate currentlyShownDate;
    private final ReservationService reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());    private List<Reservation> reservations;

    public CalendarController() throws SQLException {
    }

    @FXML
    public void initialize() {
        try {
            currentlyShownDate = LocalDate.now();

            // Load CSS if it exists
            String cssResource = getClass().getResource("/calendar.css").toExternalForm();
            calendar.getStylesheets().add(cssResource);

            // Load and log reservations
            this.reservations = reservationService.getall();
            System.out.println("Number of reservations: " + reservations.size());

            // Debug print all reservations
            for (Reservation r : reservations) {
                if (r.getDate() == null) {
                    System.out.println("WARNING: Reservation with null date: " + r.getNom());
                } else {
                    System.out.println("Reservation: " + r.getNom() + " on " + r.getDate() + " event ID: " + r.getEventId());
                }
            }

            updateCalendar();
        } catch (Exception e) {
            showError("Erreur d'initialisation", "Échec du chargement du calendrier");
            e.printStackTrace();
        }
    }

    private void updateCalendar() {
        try {
            // Clear existing calendar cells
            calendar.getChildren().clear();

            if (currentlyShownDate == null) {
                currentlyShownDate = LocalDate.now();
            }

            // Update year and month display
            year.setText(String.valueOf(currentlyShownDate.getYear()));
            String monthName = currentlyShownDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            month.setText(monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase());

            // Get all reservations
            System.out.println("[DEBUG] Total reservations loaded: " + reservations.size());

            // Add day headers (replacing the FXML static headers)
            calendar.getChildren().clear(); // Clear everything including headers
            String[] dayHeaders = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
            for (String day : dayHeaders) {
                Label header = new Label(day);
                header.getStyleClass().add("calendar-header");
                calendar.getChildren().add(header);
            }

            // Calculate first day of month and total days
            LocalDate firstDayOfMonth = currentlyShownDate.withDayOfMonth(1);
            int daysInMonth = currentlyShownDate.lengthOfMonth();

            // Get day of week for first day (0=Sunday, 1=Monday, etc. in our UI)
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
            // Adjust for Sunday as first day in the UI
            dayOfWeek = dayOfWeek == 7 ? 0 : dayOfWeek;

            // Add empty cells for days before the 1st of the month
            for (int i = 0; i < dayOfWeek; i++) {
                StackPane emptyCell = new StackPane();
                emptyCell.getStyleClass().add("calendar-cell-empty");
                calendar.getChildren().add(emptyCell);
            }

            // Create cells for each day of the month
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate cellDate = firstDayOfMonth.withDayOfMonth(day);

                // Filter reservations for this specific date
                int year = cellDate.getYear();
                int month = cellDate.getMonthValue();
                int dayOfMonth = cellDate.getDayOfMonth();

                List<Reservation> reservationsForDay = reservations.stream()
                        .filter(r -> {
                            if (r.getDate() == null) return false;

                            LocalDate resDate = r.getDate().toLocalDate();
                            return resDate.getYear() == year &&
                                    resDate.getMonthValue() == month &&
                                    resDate.getDayOfMonth() == dayOfMonth;
                        })
                        .collect(Collectors.toList());

                System.out.println("[DEBUG] " + cellDate + " has " +
                        reservationsForDay.size() + " reservations");

                // Create and add the calendar cell
                StackPane cell = createCalendarCell(cellDate, reservationsForDay);
                calendar.getChildren().add(cell);
            }
        } catch (Exception e) {
            showError("Erreur de calendrier", "Échec de la mise à jour du calendrier");
            e.printStackTrace();
        }
    }

    private StackPane createCalendarCell(LocalDate date, List<Reservation> reservations) {
        // Create the cell container
        StackPane cell = new StackPane();
        cell.getStyleClass().add("calendar-cell");
        cell.setMinHeight(70);
        cell.setMinWidth(95);

        // Create vertical layout for date and reservations
        VBox vbox = new VBox();
        vbox.setSpacing(2);
        vbox.setMaxWidth(Double.MAX_VALUE);

        // Add the date number
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        vbox.getChildren().add(dateLabel);

        // Add reservation entries
        if (reservations != null && !reservations.isEmpty()) {
            for (Reservation r : reservations) {
                // Create compact reservation display
                String info = r.getNom();
                if (r.getPrenom() != null && !r.getPrenom().isEmpty()) {
                    info += " " + r.getPrenom().substring(0, 1) + ".";
                }

                Label resLabel = new Label(info);
                resLabel.getStyleClass().add("calendar-reservation");
                resLabel.setMaxWidth(Double.MAX_VALUE);
                resLabel.setStyle("-fx-background-color: #e6f7ff; -fx-padding: 2px 4px; " +
                        "-fx-background-radius: 3px; -fx-font-size: 10px;");
                vbox.getChildren().add(resLabel);
            }
        }

        cell.getChildren().add(vbox);

        // Highlight today's date
        if (date.equals(LocalDate.now())) {
            cell.setStyle("-fx-background-color: #f0f7ff; -fx-border-color: #0078d7; -fx-border-width: 2px;");
        }

        return cell;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backOneMonth() {
        currentlyShownDate = currentlyShownDate.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void forwardOneMonth() {
        currentlyShownDate = currentlyShownDate.plusMonths(1);
        updateCalendar();
    }

    public void showAsFullScreen(Stage stage) {
        stage.setFullScreen(true);
    }
}