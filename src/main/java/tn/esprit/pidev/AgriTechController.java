package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;

import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AgriTechController implements Initializable {

    // Composants du dashboard
    @FXML private Label welcomeLabel;
    @FXML private Label specialityLabel;
    @FXML private Text todayAppointmentsCount;
    @FXML private Text totalPatientsCount;
    @FXML private Text pendingReportsCount;
    @FXML private TableView<?> upcomingAppointmentsTable;
    @FXML private ListView<?> notificationsListView;

    @FXML
    private VBox sidebar;

    @FXML
    private Button openSidebarBtn;

    @FXML
    private Button closeSidebarBtn;

    // Panneaux de contenu
    @FXML private VBox dashboardPane;
    @FXML private VBox statPane;
    @FXML private VBox appointmentsPane;
    @FXML private VBox patientRecordsPane;
    @FXML private VBox prescriptionsPane;
    @FXML private VBox medicalNotesPane;
    @FXML private VBox userManagementPane;
    @FXML private VBox profilePane;
    @FXML private VBox settingsPane;

    @FXML
    private ImageView myImageView;

    // Contrôleur pour la gestion des utilisateurs
    private UserManagementController userManagementController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation avec l'utilisateur connecté
        if (User.connecte != null) {
            welcomeLabel.setText("Welcome, " + User.connecte.getLastName());


        }

        // Initialisation du panneau de gestion des utilisateurs
        initUserManagementPane();



        setupSidebar();

    }

    private void initUserManagementPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserManagement.fxml"));
            Node userManagementView = loader.load();
            userManagementController = loader.getController();

            // Ajout unique de la vue au panneau
            userManagementPane.getChildren().setAll(userManagementView);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de UserManagement.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSidebar() {
        // Configuration initiale de la barre latérale
        openSidebarBtn.setVisible(false);
        closeSidebarBtn.setVisible(true);
    }

    @FXML
    public void toggleSidebar() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
        openSidebarBtn.setVisible(isVisible);
        closeSidebarBtn.setVisible(!isVisible);
    }



    private void hideAllPanes() {
        dashboardPane.setVisible(false);
        appointmentsPane.setVisible(false);
        patientRecordsPane.setVisible(false);
        prescriptionsPane.setVisible(false);
        medicalNotesPane.setVisible(false);
        userManagementPane.setVisible(false);
        profilePane.setVisible(false);
        settingsPane.setVisible(false);
        statPane.setVisible(false);
    }



    // Méthodes de navigation
    @FXML
    private void showDashboard(ActionEvent event) {
        hideAllPanes();
        dashboardPane.setVisible(true);
    }

    @FXML
    private void showAppointments(ActionEvent event) {
        hideAllPanes();
        appointmentsPane.setVisible(true);
    }

    @FXML
    private void showPatientRecords(ActionEvent event) {
        hideAllPanes();
        patientRecordsPane.setVisible(true);
    }

    @FXML
    private void showPrescriptions(ActionEvent event) {
        hideAllPanes();
        prescriptionsPane.setVisible(true);
    }

    @FXML
    private void showMedicalNotes(ActionEvent event) {
        hideAllPanes();
        medicalNotesPane.setVisible(true);
    }

    @FXML
    private void showUserManagement(ActionEvent event) {
        hideAllPanes();
        userManagementPane.setVisible(true);
        userManagementController.loadUsers(); // Rafraîchit la liste des utilisateurs
    }

    @FXML
    private void showProfile(ActionEvent event) {
        hideAllPanes();
        profilePane.setVisible(true);
    }

    @FXML
    private void showStat(ActionEvent event) {
        hideAllPanes();
        statPane.setVisible(true);
    }

    @FXML
    private void showSettings(ActionEvent event) {
        hideAllPanes();
        settingsPane.setVisible(true);
    }

    // Méthodes des actions rapides
    @FXML
    private void createNewAppointment(ActionEvent event) {
        showAlert("Création de rendez-vous", "Fonctionnalité à implémenter");
    }

    @FXML
    private void writeNewPrescription(ActionEvent event) {
        showAlert("Nouvelle ordonnance", "Fonctionnalité à implémenter");
    }

    @FXML
    private void addMedicalNote(ActionEvent event) {
        showAlert("Ajout de note médicale", "Fonctionnalité à implémenter");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            User.connecte = null; // Réinitialisation de l'utilisateur connecté

            // Chargement de la vue de login
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/pidev/LoginView.fxml"));
            Scene scene = new Scene(root);

            // Obtention de la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changement de scène
            stage.setScene(scene);
            stage.setTitle("HopeNest / Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}