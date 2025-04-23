package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientHomeController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Text nextAppointmentDate;

    @FXML
    private Text activePrescriptionsCount;

    @FXML
    private Text unreadMessagesCount;

    @FXML
    private TableView upcomingAppointmentsTable;

    @FXML
    private ListView recentPrescriptionsListView;

    @FXML
    private ComboBox<String> doctorComboBox;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private ComboBox<String> timeSlotComboBox;

    @FXML
    private TextArea reasonTextArea;

    @FXML
    private ComboBox<String> specialityFilterComboBox;

    @FXML
    private TextField doctorSearchField;

    @FXML
    private TableView doctorsTableView;

    @FXML
    private VBox dashboardPane;

    @FXML
    private VBox bookAppointmentPane;

    @FXML
    private VBox appointmentsPane;

    @FXML
    private VBox medicalHistoryPane;

    @FXML
    private VBox prescriptionsPane;

    @FXML
    private VBox findDoctorsPane;

    @FXML
    private VBox profilePane;

    @FXML
    private VBox settingsPane;

    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize dashboard with the logged in patient's information
        if (User.connecte != null) {
            welcomeLabel.setText("Welcome, " + User.connecte.getFirstName() + " " + User.connecte.getLastName());

            // Load dashboard data
            loadDashboardData();

            // Initialize doctor combobox for appointment booking
            loadDoctorsData();

            // Initialize timeSlot combobox
            initializeTimeSlots();

            // Initialize speciality filter for find doctors
            initializeSpecialityFilter();
        }
    }

    private void loadDashboardData() {
        // This would normally connect to your database to get real data
        // For now, we'll just set sample data
        nextAppointmentDate.setText("April 15, 2025");
        activePrescriptionsCount.setText("2");
        unreadMessagesCount.setText("1");

        // Load upcoming appointments and recent prescriptions
        // This would be populated from a database in a real app
    }

    private void loadDoctorsData() {
        // Fetch doctors from database and populate the combobox
        try {
            doctorComboBox.getItems().clear();
            userDAO.getAllAdmin().forEach(doctor -> {
                doctorComboBox.getItems().add(doctor.getFirstName() + " " + doctor.getLastName() + " (" + doctor.getSpecialite() + ")");
            });
        } catch (Exception e) {
            System.err.println("Error loading doctors: " + e.getMessage());
        }
    }

    private void initializeTimeSlots() {
        // Add sample time slots
        timeSlotComboBox.getItems().clear();
        timeSlotComboBox.getItems().addAll(
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
                "11:00 AM", "11:30 AM", "01:00 PM", "01:30 PM",
                "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
                "04:00 PM", "04:30 PM"
        );
    }

    private void initializeSpecialityFilter() {
        // Add sample specialities
        specialityFilterComboBox.getItems().clear();
        specialityFilterComboBox.getItems().addAll(
                "All Specialities",
                "Cardiology",
                "Dermatology",
                "Neurology",
                "Pediatrics",
                "Orthopedics",
                "Psychiatry",
                "Ophthalmology",
                "Endocrinology",
                "Gastroenterology"
        );
    }

    private void hideAllPanes() {
        dashboardPane.setVisible(false);
        bookAppointmentPane.setVisible(false);
        appointmentsPane.setVisible(false);
        medicalHistoryPane.setVisible(false);
        prescriptionsPane.setVisible(false);
        findDoctorsPane.setVisible(false);
        profilePane.setVisible(false);
        settingsPane.setVisible(false);
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        hideAllPanes();
        dashboardPane.setVisible(true);
    }

    @FXML
    private void showBookAppointment(ActionEvent event) {
        hideAllPanes();
        bookAppointmentPane.setVisible(true);
        // Refresh doctor list and time slots
        loadDoctorsData();
        appointmentDatePicker.setValue(null);
        timeSlotComboBox.getSelectionModel().clearSelection();
        reasonTextArea.clear();
    }

    @FXML
    private void showAppointments(ActionEvent event) {
        hideAllPanes();
        appointmentsPane.setVisible(true);
        // Load appointments data here
    }

    @FXML
    private void showMedicalHistory(ActionEvent event) {
        hideAllPanes();
        medicalHistoryPane.setVisible(true);
        // Load medical history data here
    }

    @FXML
    private void showPrescriptions(ActionEvent event) {
        hideAllPanes();
        prescriptionsPane.setVisible(true);
        // Load prescriptions data here
    }

    @FXML
    private void showFindDoctors(ActionEvent event) {
        hideAllPanes();
        findDoctorsPane.setVisible(true);
        // Load doctors data here
        loadDoctorsList();
    }

    @FXML
    private void showProfile(ActionEvent event) {
        hideAllPanes();
        profilePane.setVisible(true);
        // Load profile data here
    }

    @FXML
    private void showSettings(ActionEvent event) {
        hideAllPanes();
        settingsPane.setVisible(true);
        // Load settings data here
    }

    @FXML
    private void bookAppointment(ActionEvent event) {
        // Validate inputs
        if (doctorComboBox.getValue() == null ||
                appointmentDatePicker.getValue() == null ||
                timeSlotComboBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please select a doctor, date, and time slot for your appointment.");
            alert.showAndWait();
            return;
        }

        // In a real application, this would save the appointment to the database
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Appointment Booked");
        success.setHeaderText("Success");
        success.setContentText("Your appointment has been booked successfully.");
        success.showAndWait();

        // Reset fields
        doctorComboBox.getSelectionModel().clearSelection();
        appointmentDatePicker.setValue(null);
        timeSlotComboBox.getSelectionModel().clearSelection();
        reasonTextArea.clear();

        // Show dashboard
        showDashboard(event);
    }

    @FXML
    private void searchDoctors(ActionEvent event) {
        String searchText = doctorSearchField.getText().toLowerCase();
        String speciality = specialityFilterComboBox.getValue();

        // In a real application, this would filter the doctors table based on search text and selected speciality
        System.out.println("Searching for doctors matching: " + searchText + " with speciality: " + speciality);

        // Refresh the doctors table with filtered results
        loadDoctorsList();
    }

    private void loadDoctorsList() {
        // This would load and filter doctors based on search criteria
        // For now, just a placeholder
        System.out.println("Loading doctors list");
    }

    @FXML
    private void clearDoctorSearch(ActionEvent event) {
        doctorSearchField.clear();
        specialityFilterComboBox.getSelectionModel().select("All Specialities");
        // Reload all doctors
        loadDoctorsList();
    }

    @FXML
    private void viewPrescriptionDetails(ActionEvent event) {
        // Show prescription details view
        System.out.println("Viewing prescription details");
    }

    @FXML
    private void downloadPrescription(ActionEvent event) {
        // Download prescription as PDF
        System.out.println("Downloading prescription");
    }

    @FXML
    private void cancelAppointment(ActionEvent event) {
        // Show confirmation dialog and cancel the selected appointment
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Appointment");
        confirm.setHeaderText("Cancel Appointment Confirmation");
        confirm.setContentText("Are you sure you want to cancel this appointment?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Cancel the appointment in the database
                System.out.println("Appointment cancelled");
            }
        });
    }

    @FXML
    private void rescheduleAppointment(ActionEvent event) {
        // Navigate to reschedule view/dialog
        System.out.println("Rescheduling appointment");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Reset the static connected user
            User.connecte = null;

            // Navigate to login screen
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/pidev/View/login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}