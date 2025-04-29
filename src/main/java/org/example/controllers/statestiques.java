package org.example.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.models.Equipements;
import org.example.services.Service_equipement;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class statestiques implements Initializable {

    // Chart components
    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> quantityBarChart;
    @FXML private PieChart pieChartCompact;
    @FXML private BarChart<String, Number> barChartCompact;

    // UI components
    @FXML private Button refreshButton;
    @FXML private Button exportButton;
    @FXML private Label lastUpdatedLabel;
    @FXML private VBox summaryStatsContainer;

    private Service_equipement equipmentService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            equipmentService = new Service_equipement();

            // Debug initialization
            System.out.println("Initializing controller...");
            System.out.println("Components initialized:");
            System.out.println("- categoryPieChart: " + (categoryPieChart != null));
            System.out.println("- quantityBarChart: " + (quantityBarChart != null));
            System.out.println("- pieChartCompact: " + (pieChartCompact != null));
            System.out.println("- barChartCompact: " + (barChartCompact != null));

            // Initialize button actions
            if (refreshButton != null) {
                refreshButton.setOnAction(e -> refreshData());
            } else {
                System.err.println("Refresh button is null!");
            }

            if (exportButton != null) {
                exportButton.setOnAction(e -> exportData());
            }

            // Initial data load
            Platform.runLater(() -> {
                System.out.println("Performing initial data load...");
                refreshData();
            });

        } catch (Exception e) {
            System.err.println("Initialization error:");
            e.printStackTrace();
            showErrorAlert("Erreur d'initialisation: " + e.getMessage());
        }
    }

    private void refreshData() {
        try {
            System.out.println("Refreshing data...");
            List<Equipements> equipmentList = equipmentService.afficher();

            if (equipmentList == null) {
                System.err.println("La liste des équipements est null!");
                showErrorAlert("Erreur: Aucune donnée disponible (liste nulle)");
                return;
            }

            System.out.println("Nombre d'équipements trouvés: " + equipmentList.size());

            if (equipmentList.isEmpty()) {
                System.out.println("La liste des équipements est vide");
                showInfoAlert("Aucun équipement trouvé dans la base de données");
                return;
            }

            // Debug: Print first few items
            System.out.println("Exemple d'équipements:");
            equipmentList.stream().limit(5).forEach(e ->
                    System.out.println(e.getNom() + " - Catégorie: " +
                            (e.getCategory() != null ? e.getCategory().getType() : "null") +
                            " - Quantité: " + e.getQuantite())
            );

            Platform.runLater(() -> {
                updateCharts(equipmentList);
                updateSummaryStats(equipmentList);
                if (lastUpdatedLabel != null) {
                    lastUpdatedLabel.setText("Dernière mise à jour: " + java.time.LocalDateTime.now());
                } else {
                    System.err.println("lastUpdatedLabel is null!");
                }
            });

        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement des données:");
            e.printStackTrace();
            showErrorAlert("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void updateCharts(List<Equipements> equipmentList) {
        System.out.println("Mise à jour des graphiques...");
        clearChartData();

        // 1. Préparation des données pour les camemberts (répartition par catégorie)
        Map<String, Long> categoryCounts = equipmentList.stream()
                .collect(Collectors.groupingBy(
                        e -> {
                            String category = (e.getCategory() != null && e.getCategory().getType() != null)
                                    ? e.getCategory().getType()
                                    : "Non catégorisé";
                            System.out.println("Catégorie traitée: " + category);
                            return category;
                        },
                        Collectors.counting()
                ));

        System.out.println("Répartition par catégorie: " + categoryCounts);

        // Ajout des données aux camemberts
        categoryCounts.forEach((category, count) -> {
            String label = String.format("%s (%d)", category, count);
            System.out.println("Ajout au camembert: " + label + " - " + count);

            if (categoryPieChart != null) {
                categoryPieChart.getData().add(new PieChart.Data(label, count));
            }
            if (pieChartCompact != null) {
                pieChartCompact.getData().add(new PieChart.Data(category, count));
            }
        });

        // 2. Préparation des données pour les barres (quantité par catégorie)
        Map<String, Integer> categoryQuantities = equipmentList.stream()
                .collect(Collectors.groupingBy(
                        e -> (e.getCategory() != null && e.getCategory().getType() != null)
                                ? e.getCategory().getType()
                                : "Non catégorisé",
                        Collectors.summingInt(Equipements::getQuantite)
                ));

        System.out.println("Quantités par catégorie: " + categoryQuantities);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantité");

        categoryQuantities.forEach((category, quantity) -> {
            System.out.println("Ajout au graphique à barres: " + category + " - " + quantity);
            series.getData().add(new XYChart.Data<>(category, quantity));
        });

        if (quantityBarChart != null) {
            quantityBarChart.getData().add(series);
            // Force refresh of bar chart
            quantityBarChart.setAnimated(false);
            quantityBarChart.setAnimated(true);
        }

        if (barChartCompact != null) {
            barChartCompact.getData().add(series);
            // Force refresh of compact bar chart
            barChartCompact.setAnimated(false);
            barChartCompact.setAnimated(true);
        }

        // Force refresh of pie charts
        if (categoryPieChart != null) {
            categoryPieChart.setAnimated(false);
            categoryPieChart.setAnimated(true);
        }
        if (pieChartCompact != null) {
            pieChartCompact.setAnimated(false);
            pieChartCompact.setAnimated(true);
        }
    }

    private void clearChartData() {
        System.out.println("Nettoyage des données des graphiques...");
        if (categoryPieChart != null) {
            categoryPieChart.getData().clear();
        }
        if (quantityBarChart != null) {
            quantityBarChart.getData().clear();
        }
        if (pieChartCompact != null) {
            pieChartCompact.getData().clear();
        }
        if (barChartCompact != null) {
            barChartCompact.getData().clear();
        }
    }

    private void updateSummaryStats(List<Equipements> equipmentList) {
        if (summaryStatsContainer == null) {
            System.err.println("summaryStatsContainer is null!");
            return;
        }

        summaryStatsContainer.getChildren().clear();

        long totalItems = equipmentList.size();
        int totalQuantity = equipmentList.stream().mapToInt(Equipements::getQuantite).sum();
        double totalValue = equipmentList.stream()
                .mapToDouble(e -> e.getPrix() * e.getQuantite())
                .sum();
        long categoryCount = equipmentList.stream()
                .map(e -> e.getCategory() != null ? e.getCategory().getType() : "Non catégorisé")
                .distinct()
                .count();

        System.out.println("Statistiques résumées:");
        System.out.println("- Total items: " + totalItems);
        System.out.println("- Total quantité: " + totalQuantity);
        System.out.println("- Valeur totale: " + totalValue);
        System.out.println("- Nombre de catégories: " + categoryCount);

        summaryStatsContainer.getChildren().addAll(
                createStatLabel("Nombre total d'équipements: " + totalItems),
                createStatLabel("Quantité totale disponible: " + totalQuantity),
                createStatLabel(String.format("Valeur totale du stock: €%.2f", totalValue)),
                createStatLabel("Nombre de catégories: " + categoryCount)
        );
    }

    private Label createStatLabel(String text) {
        Label label = new Label("• " + text);
        label.setStyle("-fx-font-size: 14; -fx-padding: 5 0;");
        return label;
    }

    private void exportData() {
        showInfoAlert("Fonctionnalité d'export à venir!");
    }

    // Navigation methods
    public void navDashboard(ActionEvent event) {
        loadScene(event, "/Affichage.fxml");
    }

    public void navStats(ActionEvent event) {
        loadScene(event, "/afficher_demande_devis.fxml");
    }

    public void navEquipements(ActionEvent event) {
        loadScene(event, "/equipments.fxml");
    }

    public void navClients(ActionEvent event) {
        loadScene(event, "/front.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            System.out.println("Chargement de la vue: " + fxmlPath);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue:");
            e.printStackTrace();
            showErrorAlert("Erreur lors du chargement de la vue: " + fxmlPath);
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}