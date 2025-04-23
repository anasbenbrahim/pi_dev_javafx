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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FournisseurDashboard implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label specialityLabel;

    @FXML
    private Text todayAppointmentsCount;

    @FXML
    private Text totalPatientsCount;

    @FXML
    private Text pendingReportsCount;

    @FXML
    private TableView upcomingAppointmentsTable;

    @FXML
    private ListView notificationsListView;

    @FXML
    private VBox dashboardPane;

    @FXML
    private VBox appointmentsPane;

    @FXML
    private VBox patientRecordsPane;

    @FXML
    private VBox prescriptionsPane;

    @FXML
    private VBox medicalNotesPane;

    @FXML
    private VBox profilePane;

    @FXML
    private VBox settingsPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize dashboard with the logged in doctor's information
        if (User.connecte != null) {
            welcomeLabel.setText("Welcome, " + User.connecte.getLastName());


            // Load dashboard data
            loadDashboardData();
        }
    }

    private void loadDashboardData() {
        // This would normally connect to your database to get real data
        // For now, we'll just set sample data
        todayAppointmentsCount.setText("5");
        totalPatientsCount.setText("42");
        pendingReportsCount.setText("3");

        // Load upcoming appointments and notifications
        // This would be populated from a database in a real app
    }

    private void hideAllPanes() {
        dashboardPane.setVisible(false);
        appointmentsPane.setVisible(false);
        patientRecordsPane.setVisible(false);
        prescriptionsPane.setVisible(false);
        medicalNotesPane.setVisible(false);
        profilePane.setVisible(false);
        settingsPane.setVisible(false);
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        hideAllPanes();
        dashboardPane.setVisible(true);
    }

    @FXML
    private void showAppointments(ActionEvent event) {
        hideAllPanes();
        appointmentsPane.setVisible(true);
        // Load appointments data here
    }

    @FXML
    private void showPatientRecords(ActionEvent event) {
        hideAllPanes();
        patientRecordsPane.setVisible(true);
        // Load patient records data here
    }

    @FXML
    private void showPrescriptions(ActionEvent event) {
        hideAllPanes();
        prescriptionsPane.setVisible(true);
        // Load prescriptions data here
    }

    @FXML
    private void showMedicalNotes(ActionEvent event) {
        hideAllPanes();
        medicalNotesPane.setVisible(true);
        // Load medical notes data here
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
    private void createNewAppointment(ActionEvent event) {
        // Show appointment creation dialog or navigate to appointment creation page
        System.out.println("Creating new appointment");
    }

    @FXML
    private void writeNewPrescription(ActionEvent event) {
        // Show prescription creation dialog or navigate to prescription creation page
        System.out.println("Writing new prescription");
    }

    @FXML
    private void addMedicalNote(ActionEvent event) {
        // Show medical note creation dialog or navigate to note creation page
        System.out.println("Adding medical note");
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