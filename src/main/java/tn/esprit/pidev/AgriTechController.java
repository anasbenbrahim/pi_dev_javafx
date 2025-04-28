package tn.esprit.pidev;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class AgriTechController implements Initializable {

    @FXML
    private VBox sidebar;

    @FXML
    private Button openSidebarBtn;

    @FXML
    private Button closeSidebarBtn;

    @FXML
    private LineChart<String, Number> productionChart;

    @FXML
    private PieChart revenueChart;

    @FXML
    private ComboBox<String> yearSelector;

    @FXML
    private TableView<Culture> culturesTable;

    @FXML
    private TableColumn<Culture, String> cultureColumn;

    @FXML
    private TableColumn<Culture, String> parcelColumn;

    @FXML
    private TableColumn<Culture, String> seedDateColumn;

    @FXML
    private TableColumn<Culture, String> statusColumn;

    @FXML
    private TableColumn<Culture, Double> progressColumn;

    @FXML
    private TableColumn<Culture, Button> actionsColumn;

    @FXML
    private ToggleButton yearToggle;

    @FXML
    private ToggleButton monthToggle;

    @FXML
    private ToggleButton weekToggle;

    // Composants du dashboard
    @FXML private Label welcomeLabel;
    @FXML private Label specialityLabel;
    @FXML private Text todayAppointmentsCount;
    @FXML private Text totalPatientsCount;
    @FXML private Text pendingReportsCount;
    @FXML private TableView<?> upcomingAppointmentsTable;
    @FXML private ListView<?> notificationsListView;

    // Panneaux de contenu
    @FXML private VBox dashboardPane;
    @FXML private VBox appointmentsPane;
    @FXML private VBox patientRecordsPane;
    @FXML private VBox prescriptionsPane;
    @FXML private VBox medicalNotesPane;
    @FXML private VBox userManagementPane;
    @FXML private VBox profilePane;
    @FXML private VBox settingsPane;

    @FXML
    private ImageView myImageView;

    // Composants du profil utilisateur
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private Label profileEmail;
    @FXML private Label profileSubtitle;
    @FXML private TextField profileFirstName;
    @FXML private TextField profileLastName;
    @FXML private TextField profileEmailField;
    @FXML private TextField profilePhone;
    @FXML private PasswordField oldPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmPassword;

    // Contrôleur pour la gestion des utilisateurs
    private UserManagementController userManagementController;

    // Initialisation du contrôleur
    public void initialize(URL url, ResourceBundle rb) {
        // Initialiser les composants de l'interface utilisateur
        if (User.connecte != null) {
            welcomeLabel.setText("Welcome, " + User.connecte.getLastName());

            // Initialiser les champs du profil si l'utilisateur est connecté
            if (profileName != null) {
                profileName.setText(User.connecte.getFirstName() + " " + User.connecte.getLastName());

                profileEmail.setText(User.connecte.getEmail());
                profileSubtitle.setText("Gérez vos informations personnelles, " + User.connecte.getFirstName());

                // Remplir les champs du formulaire
                profileFirstName.setText(User.connecte.getFirstName());
                profileLastName.setText(User.connecte.getLastName());
                profileEmailField.setText(User.connecte.getEmail());
                // Ajouter d'autres champs si disponibles dans votre modèle User
            }
        }

        // Initialisation du panneau de gestion des utilisateurs
        initUserManagementPane();

        setupSidebar();
        setupProductionChart();
        setupRevenueChart();
        setupYearSelector();
        setupCulturesTable();
    }

    private void hideAllPanes() {
        userManagementPane.setVisible(false);
        dashboardPane.setVisible(true);

        // Masquer les autres panneaux si vous en ajoutez
    }

    @FXML
    private void showUserManagement(ActionEvent event) {

        userManagementPane.setVisible(true);
        userManagementController.loadUsers(); // Rafraîchit la liste des utilisateurs
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        hideAllPanes();
        dashboardPane.setVisible(true);
    }

    @FXML
    private void navigateToProfile(ActionEvent event) {
        hideAllPanes();
        profilePane.setVisible(true);

        // Mettre à jour les informations de profil avec les données de l'utilisateur connecté
        if (User.connecte != null) {
            profileName.setText(User.connecte.getFirstName() + " " + User.connecte.getLastName());

            profileEmail.setText(User.connecte.getEmail());

            // Remplir les champs du formulaire
            profileFirstName.setText(User.connecte.getFirstName());
            profileLastName.setText(User.connecte.getLastName());
            profileEmailField.setText(User.connecte.getEmail());
            // Vous pouvez ajouter d'autres champs si disponibles dans votre modèle User
        }
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

    private void setupProductionChart() {
        // Configuration du graphique de production
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Production");

        // Données mensuelles pour 2024
        series.getData().add(new XYChart.Data<>("Jan", 12));
        series.getData().add(new XYChart.Data<>("Fév", 15));
        series.getData().add(new XYChart.Data<>("Mar", 18));
        series.getData().add(new XYChart.Data<>("Avr", 22));
        series.getData().add(new XYChart.Data<>("Mai", 26));
        series.getData().add(new XYChart.Data<>("Juin", 30));
        series.getData().add(new XYChart.Data<>("Juil", 32));
        series.getData().add(new XYChart.Data<>("Août", 30));
        series.getData().add(new XYChart.Data<>("Sep", 25));
        series.getData().add(new XYChart.Data<>("Oct", 21));
        series.getData().add(new XYChart.Data<>("Nov", 18));
        series.getData().add(new XYChart.Data<>("Déc", 15));

        productionChart.getData().add(series);
    }

    private void setupRevenueChart() {
        // Configuration du graphique de revenus
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Blé", 35),
                new PieChart.Data("Maïs", 25),
                new PieChart.Data("Tomates", 20),
                new PieChart.Data("Colza", 15)
        );

        revenueChart.setData(pieChartData);
    }

    private void setupYearSelector() {
        // Configuration du sélecteur d'année
        yearSelector.setItems(FXCollections.observableArrayList("2022", "2023", "2024", "2025"));
        yearSelector.setValue("2024");
    }

    private void setupCulturesTable() {
        // Configuration de la table des cultures
        ObservableList<Culture> cultures = FXCollections.observableArrayList(
                new Culture("Blé", "Parcelle A", "15 Mars 2025", "En croissance", 65),
                new Culture("Maïs", "Parcelle B", "10 Avril 2025", "Semé", 25),
                new Culture("Tomates", "Serre 1", "22 Avril 2025", "Plantées", 15),
                new Culture("Colza", "Parcelle C", "5 Mars 2025", "En croissance", 70)
        );

        // Configuration des cellules de la table
        cultureColumn.setCellValueFactory(cellData -> cellData.getValue().cultureProperty());
        parcelColumn.setCellValueFactory(cellData -> cellData.getValue().parcelProperty());
        seedDateColumn.setCellValueFactory(cellData -> cellData.getValue().seedDateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        progressColumn.setCellValueFactory(cellData -> cellData.getValue().progressProperty().asObject());

        // Ajout des données à la table
        culturesTable.setItems(cultures);
    }

    // Gestion des événements
    @FXML
    public void toggleSidebar() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
        openSidebarBtn.setVisible(isVisible);
        closeSidebarBtn.setVisible(!isVisible);
    }

    @FXML
    public void filterByYear() {
        yearToggle.setSelected(true);
        monthToggle.setSelected(false);
        weekToggle.setSelected(false);
        updateChartData("year");
    }

    @FXML
    public void filterByMonth() {
        yearToggle.setSelected(false);
        monthToggle.setSelected(true);
        weekToggle.setSelected(false);
        updateChartData("month");
    }

    @FXML
    public void filterByWeek() {
        yearToggle.setSelected(false);
        monthToggle.setSelected(false);
        weekToggle.setSelected(true);
        updateChartData("week");
    }

    private void updateChartData(String timeframe) {
        // Mise à jour des données du graphique en fonction de la période sélectionnée
        productionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Production");

        if ("year".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("2020", 150));
            series.getData().add(new XYChart.Data<>("2021", 200));
            series.getData().add(new XYChart.Data<>("2022", 180));
            series.getData().add(new XYChart.Data<>("2023", 220));
            series.getData().add(new XYChart.Data<>("2024", 250));
        } else if ("month".equals(timeframe)) {
            setupProductionChart(); // Utilise les données mensuelles existantes
            return;
        } else if ("week".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("S1", 5));
            series.getData().add(new XYChart.Data<>("S2", 8));
            series.getData().add(new XYChart.Data<>("S3", 7));
            series.getData().add(new XYChart.Data<>("S4", 10));
        }

        productionChart.getData().add(series);
    }

    @FXML
    public void createNewCulture() {
        // Logique pour créer une nouvelle culture
        System.out.println("Création d'une nouvelle culture");
    }

    @FXML
    public void generateReport() {
        // Logique pour générer un rapport
        System.out.println("Génération d'un rapport");
    }

    @FXML
    public void scheduleTask() {
        // Logique pour planifier une tâche
        System.out.println("Planification d'une tâche");
    }

    @FXML
    public void showAllTasks() {
        // Afficher toutes les tâches
        System.out.println("Affichage de toutes les tâches");
    }

    @FXML
    public void showWeatherDetails() {
        // Afficher les détails météo
        System.out.println("Affichage des détails météo");
    }

    @FXML
    public void filterCultures() {
        // Filtrer les cultures
        System.out.println("Filtrage des cultures");
    }

    @FXML
    public void prevPage() {
        // Page précédente
        System.out.println("Page précédente");
    }

    @FXML
    public void nextPage() {
        // Page suivante
        System.out.println("Page suivante");
    }

    @FXML
    public void goToPage() {
        // Aller à une page spécifique
        System.out.println("Aller à la page");
    }

    // Navigation
    @FXML
    public void navigateToDashboard() {
        System.out.println("Navigation vers le tableau de bord");
    }

    @FXML
    public void navigateToCultures() {
        System.out.println("Navigation vers les cultures");
    }

    @FXML
    public void navigateToInventory() {
        System.out.println("Navigation vers l'inventaire");
    }

    @FXML
    public void navigateToOrders() {
        System.out.println("Navigation vers les commandes");
    }

    @FXML
    public void navigateToCalendar() {
        System.out.println("Navigation vers le calendrier");
    }

    @FXML
    public void navigateToEmployees() {
        System.out.println("Navigation vers les employés");
    }

    @FXML
    public void navigateToReports() {
        System.out.println("Navigation vers les rapports");
    }

    @FXML
    public void navigateToSettings() {
        System.out.println("Navigation vers les paramètres");
    }

    @FXML
    public void logout() {
        System.out.println("Déconnexion");
    }

    // Classe modèle pour les cultures
    public static class Culture {
        private final javafx.beans.property.SimpleStringProperty culture;
        private final javafx.beans.property.SimpleStringProperty parcel;
        private final javafx.beans.property.SimpleStringProperty seedDate;
        private final javafx.beans.property.SimpleStringProperty status;
        private final javafx.beans.property.SimpleDoubleProperty progress;

        public Culture(String culture, String parcel, String seedDate, String status, double progress) {
            this.culture = new javafx.beans.property.SimpleStringProperty(culture);
            this.parcel = new javafx.beans.property.SimpleStringProperty(parcel);
            this.seedDate = new javafx.beans.property.SimpleStringProperty(seedDate);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.progress = new javafx.beans.property.SimpleDoubleProperty(progress);
        }

        public javafx.beans.property.StringProperty cultureProperty() {
            return culture;
        }

        public javafx.beans.property.StringProperty parcelProperty() {
            return parcel;
        }

        public javafx.beans.property.StringProperty seedDateProperty() {
            return seedDate;
        }

        public javafx.beans.property.StringProperty statusProperty() {
            return status;
        }

        public javafx.beans.property.DoubleProperty progressProperty() {
            return progress;
        }
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
            stage.setTitle("AgriConnect / Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}