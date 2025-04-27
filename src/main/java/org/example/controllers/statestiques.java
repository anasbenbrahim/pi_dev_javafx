package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.models.Equipements;
import org.example.services.Service_equipement;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class statestiques implements Initializable {

    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> quantityBarChart;
    @FXML private PieChart pieChartCompact;
    @FXML private BarChart<String, Number> barChartCompact;
    @FXML private Button refreshButton;
    @FXML private Button exportButton;
    @FXML private Label lastUpdatedLabel;
    @FXML private VBox summaryStatsContainer;  // Added this line

    private Service_equipement equipmentService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equipmentService = new Service_equipement();
        refreshData();

        refreshButton.setOnAction(e -> refreshData());
        exportButton.setOnAction(e -> exportStatistics());
    }

    private void refreshData() {
        List<Equipements> equipmentList = equipmentService.afficher();
        updateCharts(equipmentList);
        updateSummaryStats(equipmentList);  // Added this line
        lastUpdatedLabel.setText("Last updated: " + java.time.LocalDateTime.now().toString());
    }

    private void updateCharts(List<Equipements> equipmentList) {
        categoryPieChart.getData().clear();
        quantityBarChart.getData().clear();
        pieChartCompact.getData().clear();
        barChartCompact.getData().clear();

        // Category Distribution
        Map<String, Long> categoryCounts = equipmentList.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() != null ? e.getCategory().getType() : "Uncategorized",
                        Collectors.counting()
                ));

        categoryCounts.forEach((category, count) -> {
            PieChart.Data data = new PieChart.Data(category + " (" + count + ")", count);
            categoryPieChart.getData().add(data);
            pieChartCompact.getData().add(new PieChart.Data(category, count));
        });

        // Quantity by Category
        Map<String, Integer> categoryQuantities = equipmentList.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() != null ? e.getCategory().getType() : "Uncategorized",
                        Collectors.summingInt(Equipements::getQuantite)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantity");

        XYChart.Series<String, Number> compactSeries = new XYChart.Series<>();
        compactSeries.setName("Quantity");

        categoryQuantities.forEach((category, quantity) -> {
            series.getData().add(new XYChart.Data<>(category, quantity));
            compactSeries.getData().add(new XYChart.Data<>(category, quantity));
        });

        quantityBarChart.getData().add(series);
        barChartCompact.getData().add(compactSeries);
    }

    private void updateSummaryStats(List<Equipements> equipmentList) {
        summaryStatsContainer.getChildren().clear();

        long totalItems = equipmentList.size();
        int totalQuantity = equipmentList.stream().mapToInt(Equipements::getQuantite).sum();
        double totalValue = equipmentList.stream()
                .mapToDouble(e -> e.getPrix() * e.getQuantite())
                .sum();
        long categoryCount = equipmentList.stream()
                .map(e -> e.getCategory() != null ? e.getCategory().getType() : "Uncategorized")
                .distinct()
                .count();

        summaryStatsContainer.getChildren().addAll(
                new Label("• Total Equipment Items: " + totalItems),
                new Label("• Total Quantity Available: " + totalQuantity),
                new Label(String.format("• Total Inventory Value: €%.2f", totalValue)),
                new Label("• Distinct Categories: " + categoryCount)
        );
    }

    private void exportStatistics() {
        // Implement your PDF export logic here
        System.out.println("Export to PDF functionality would go here");
    }
}