package tn.esprit.pidev;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;

import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class StatisticsController implements Initializable {

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalDoctorsLabel;

    @FXML
    private Label totalPatientsLabel;

    @FXML
    private Label totalAppointmentsLabel;

    @FXML
    private Label totalPrescriptionsLabel;

    @FXML
    private PieChart userTypePieChart;

    @FXML
    private BarChart<String, Number> specialtyBarChart;

    @FXML
    private LineChart<String, Number> appointmentsLineChart;

    @FXML
    private ComboBox<String> chartFilterComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private VBox adminStatsBox;

    @FXML
    private VBox doctorStatsBox;

    private UserDAO userDAO;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DAOs
        userDAO = new UserDAO();
        // You'll need to implement these DAOs


        // Initialize filter combo box
        chartFilterComboBox.getItems().addAll(
                "This Week",
                "This Month",
                "Last 3 Months",
                "Last 6 Months",
                "Last Year",
                "Custom Range"
        );
        chartFilterComboBox.setValue("This Month");

        // Set default date range (this month)
        LocalDate now = LocalDate.now();
        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now);

        // Only show date pickers when custom range is selected
        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);

        // Handle filter changes
        chartFilterComboBox.setOnAction(e -> {
            boolean isCustomRange = "Custom Range".equals(chartFilterComboBox.getValue());
            startDatePicker.setVisible(isCustomRange);
            endDatePicker.setVisible(isCustomRange);

            // Update charts based on selected filter
            loadChartData();
        });

        // Add listeners to date pickers
        startDatePicker.setOnAction(e -> loadChartData());
        endDatePicker.setOnAction(e -> loadChartData());

        if (User.connecte != null) {
            String[] userRoles = User.connecte.getRoles();
            boolean isAdmin = Arrays.asList(userRoles).contains("admin");
            boolean isFermier = Arrays.asList(userRoles).contains("fermier");

            if (isAdmin) {
                adminStatsBox.setVisible(true);
                doctorStatsBox.setVisible(true);
            } else if (isFermier) {
                adminStatsBox.setVisible(false);
                doctorStatsBox.setVisible(true);
            } else {
                adminStatsBox.setVisible(false);
                doctorStatsBox.setVisible(false);
            }
        }

        // Load initial data
        loadSummaryData();
        loadChartData();
    }

    private void loadSummaryData() {
        // Load summary statistics
        try {
            // Count total users
            List<User> allUsers = userDAO.getAllUsers();
            int totalUsers = allUsers.size();
            totalUsersLabel.setText(String.valueOf(totalUsers));

            // Count doctors
            List<User> doctors = userDAO.getUsersByRole("fermier");
            int totalDoctors = doctors.size();
            totalDoctorsLabel.setText(String.valueOf(totalDoctors));

            // Count patients
            List<User> patients = userDAO.getUsersByRole("client");
            int totalPatients = patients.size();
            totalPatientsLabel.setText(String.valueOf(totalPatients));

            // Count appointments (you'll need to implement this in AppointmentDAO)


        } catch (Exception e) {
            System.err.println("Error loading summary data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChartData() {
        // Load and update all charts based on the selected filter
        loadUserTypePieChart();
        loadSpecialtyBarChart();
        loadAppointmentsLineChart();
    }

    private void loadUserTypePieChart() {
        try {
            // Get user counts by role
            int doctorCount = userDAO.getUsersByRole("medecin").size();
            int patientCount = userDAO.getUsersByRole("patient").size();
            int adminCount = userDAO.getUsersByRole("admin").size();

            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Doctors", doctorCount),
                    new PieChart.Data("Patients", patientCount),
                    new PieChart.Data("Admins", adminCount)
            );

            userTypePieChart.setData(pieChartData);
            userTypePieChart.setTitle("User Distribution by Role");

        } catch (Exception e) {
            System.err.println("Error loading user type pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSpecialtyBarChart() {
        try {
            // Get doctors
            List<User> doctors = userDAO.getUsersByRole("medecin");

            // Count doctors by specialty
            Map<String, Integer> specialtyCounts = new HashMap<>();
            for (User doctor : doctors) {
                String specialty = doctor.getSpecialite();
                if (specialty != null && !specialty.isEmpty()) {
                    specialtyCounts.put(specialty, specialtyCounts.getOrDefault(specialty, 0) + 1);
                }
            }

            // Create x-axis
            CategoryAxis xAxis = (CategoryAxis) specialtyBarChart.getXAxis();
            xAxis.setLabel("Specialties");

            // Create y-axis
            NumberAxis yAxis = (NumberAxis) specialtyBarChart.getYAxis();
            yAxis.setLabel("Number of Doctors");

            // Create dataset
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doctors per Specialty");

            // Add data to series
            for (Map.Entry<String, Integer> entry : specialtyCounts.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            // Clear previous data and add new series
            specialtyBarChart.getData().clear();
            specialtyBarChart.getData().add(series);
            specialtyBarChart.setTitle("Doctors by Specialty");

        } catch (Exception e) {
            System.err.println("Error loading specialty bar chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAppointmentsLineChart() {
        try {
            // Get date range based on filter
            LocalDate startDate, endDate;
            String filter = chartFilterComboBox.getValue();
            LocalDate now = LocalDate.now();

            switch (filter) {
                case "This Week":
                    startDate = now.minusDays(now.getDayOfWeek().getValue() - 1);
                    endDate = now;
                    break;
                case "This Month":
                    startDate = now.withDayOfMonth(1);
                    endDate = now;
                    break;
                case "Last 3 Months":
                    startDate = now.minusMonths(3).withDayOfMonth(1);
                    endDate = now;
                    break;
                case "Last 6 Months":
                    startDate = now.minusMonths(6).withDayOfMonth(1);
                    endDate = now;
                    break;
                case "Last Year":
                    startDate = now.minusYears(1);
                    endDate = now;
                    break;
                case "Custom Range":
                    startDate = startDatePicker.getValue();
                    endDate = endDatePicker.getValue();
                    // Handle invalid date range
                    if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                        return;
                    }
                    break;
                default:
                    startDate = now.withDayOfMonth(1);
                    endDate = now;
            }

            // Get appointment counts by date (you'll need to implement this method)
            //  Map<LocalDate, Integer> appointmentsByDate = appointmentDAO.getAppointmentCountsByDateRange(startDate, endDate);

            // Create x-axis
            CategoryAxis xAxis = (CategoryAxis) appointmentsLineChart.getXAxis();
            xAxis.setLabel("Date");

            // Create y-axis
            NumberAxis yAxis = (NumberAxis) appointmentsLineChart.getYAxis();
            yAxis.setLabel("Number of Appointments");

            // Create dataset
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Appointments");

            // Add data to series


            // Clear previous data and add new series
            appointmentsLineChart.getData().clear();
            appointmentsLineChart.getData().add(series);
            appointmentsLineChart.setTitle("Appointments Over Time");

        } catch (Exception e) {
            System.err.println("Error loading appointments line chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            // Determine which home page to show based on user role
            String fxmlFile;
            if (User.connecte.getRoles().equals("admin")) {
                fxmlFile = "AgriTechDashboard.fxml";
            } else if (User.connecte.getRoles().equals("fermier")) {
                fxmlFile = "FermierDashboard.fxml";
            } else {
                fxmlFile = "ClientHome.fxml";
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadSummaryData();
        loadChartData();
    }
}
