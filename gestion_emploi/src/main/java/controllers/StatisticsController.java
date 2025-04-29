package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.chart.*;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Side;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.*;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.HorizontalAlignment;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class StatisticsController {
    @FXML
    private BarChart<String, Number> barChart;
    
    @FXML
    private PieChart pieChart;

    @FXML
    private TabPane chartTabPane;

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
            
            // Initialize charts
            barChart.setAnimated(true);
            barChart.setTitle("Demands per Offer");
            barChart.setLegendVisible(false);
            
            pieChart.setAnimated(true);
            pieChart.setTitle("Distribution of Demands");
            pieChart.setLegendSide(Side.RIGHT);
            
            // Load statistics
            refreshStatistics();
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize statistics view: " + e.getMessage());
        }
    }
    
    @FXML
    private void refreshStatistics() {
        try {
            // Get counts of demands per offer
            Map<Integer, Integer> demandsPerOffer = demandeService.countDemandsPerOffer();
            
            if (demandsPerOffer.isEmpty()) {
                showNoDataMessage();
                return;
            }
            
            updateCharts(demandsPerOffer);
        } catch (Exception e) {
            showError("Statistics Error", "Failed to load statistics: " + e.getMessage());
        }
    }
    
    private void updateCharts(Map<Integer, Integer> demandsPerOffer) throws Exception {
        // Sort offers by demand count (descending)
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(demandsPerOffer.entrySet());
        sortedEntries.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
        
        // Prepare data for charts
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        
        int totalDemands = demandsPerOffer.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            int offerId = entry.getKey();
            int count = entry.getValue();
            
            Offer offer = offerService.getOfferBasicInfoById(offerId);
            if (offer == null) continue;
            
            String offerName = offer.getNom().length() > 15 ? 
                offer.getNom().substring(0, 12) + "..." : 
                offer.getNom();
            
            // Add data to bar chart
            barSeries.getData().add(new XYChart.Data<>(offerName, count));
            
            // Add data to pie chart
            double percentage = (double) count / totalDemands * 100;
            PieChart.Data slice = new PieChart.Data(
                String.format("%s (%.1f%%)", offerName, percentage), 
                count
            );
            pieData.add(slice);
        }
        
        // Update bar chart
        barChart.getData().clear();
        barChart.getData().add(barSeries);
        
        // Style bar chart
        barChart.getData().forEach(series -> {
            series.getData().forEach(data -> {
                data.getNode().setStyle("-fx-bar-fill: #3498db;");
                
                // Add hover effect
                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setStyle("-fx-bar-fill: #2980b9;");
                    showTooltip(data, event);
                });
                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setStyle("-fx-bar-fill: #3498db;");
                    hideTooltip();
                });
            });
        });
        
        // Update pie chart
        pieChart.setData(pieData);
        
        // Style pie chart
        pieData.forEach(data -> {
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setStyle("-fx-pie-color: derive(" + data.getNode().getStyle() + ", -20%);");
                showTooltip(data, event);
            });
            data.getNode().setOnMouseExited(event -> {
                data.getNode().setStyle("");
                hideTooltip();
            });
        });
    }
    
    private Tooltip currentTooltip;
    
    private void showTooltip(XYChart.Data<String, Number> data, MouseEvent event) {
        String tooltipText = String.format("%s\nDemands: %d", 
            data.getXValue(), data.getYValue().intValue());
        showTooltipHelper(tooltipText, event);
    }
    
    private void showTooltip(PieChart.Data data, MouseEvent event) {
        String tooltipText = String.format("%s\nDemands: %.0f", 
            data.getName(), data.getPieValue());
        showTooltipHelper(tooltipText, event);
    }
    
    private void showTooltipHelper(String text, MouseEvent event) {
        if (currentTooltip != null) {
            currentTooltip.hide();
        }
        currentTooltip = new Tooltip(text);
        currentTooltip.show(
            ((Node)event.getSource()).getScene().getWindow(),
            event.getScreenX() + 10,
            event.getScreenY() + 10
        );
    }
    
    private void hideTooltip() {
        if (currentTooltip != null) {
            currentTooltip.hide();
            currentTooltip = null;
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
    
    @FXML
    private void exportToPDF() {
        try {
            // Get demands per offer data first
            Map<Integer, Integer> demandsPerOffer = demandeService.countDemandsPerOffer();
            
            if (demandsPerOffer.isEmpty()) {
                showMessage("No Data", "There is no statistical data available to export.");
                return;
            }

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(
                new ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("statistics_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            // Show save dialog
            File file = fileChooser.showSaveDialog(chartTabPane.getScene().getWindow());
            if (file == null) return;

            // Create PDF document
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title with animation effect
            Paragraph title = new Paragraph("Statistics Report")
                .setFontSize(24)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setBold()
                .setMarginBottom(20);
            document.add(title);
            
            // Add timestamp with fade effect
            document.add(new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setFontSize(10)
                .setItalic()
                .setMarginBottom(30));

            // Add bar chart with animation effect
            document.add(new Paragraph("Demands per Offer - Bar Chart")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.LEFT));
            
            // Animate the bar chart before capturing
            animateBarChart();
            WritableImage barChartImage = barChart.snapshot(new SnapshotParameters(), null);
            ByteArrayOutputStream barChartBytes = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(barChartImage, null), "png", barChartBytes);
            Image barChartPdfImage = new Image(ImageDataFactory.create(barChartBytes.toByteArray()));
            barChartPdfImage.setWidth(UnitValue.createPercentValue(100));
            document.add(barChartPdfImage);

            // Add pie chart with animation effect
            document.add(new Paragraph("Demands Distribution - Pie Chart")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.LEFT));
            
            // Animate the pie chart before capturing
            animatePieChart();
            WritableImage pieChartImage = pieChart.snapshot(new SnapshotParameters(), null);
            ByteArrayOutputStream pieChartBytes = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(pieChartImage, null), "png", pieChartBytes);
            Image pieChartPdfImage = new Image(ImageDataFactory.create(pieChartBytes.toByteArray()));
            pieChartPdfImage.setWidth(UnitValue.createPercentValue(100));
            document.add(pieChartPdfImage);

            // Add QR Code
            document.add(new Paragraph("View Statistics Summary")
                .setFontSize(14)
                .setBold()
                .setMarginTop(20)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            String qrContent = generateQRCodeContent(demandsPerOffer);
            BarcodeQRCode qrCode = new BarcodeQRCode(qrContent);
            
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            
            Image qrCodeImage = new Image(qrCode.createFormXObject(ColorConstants.BLACK, pdf));
            qrCodeImage.setWidth(150);
            qrCodeImage.setHeight(150);
            qrCodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            qrCodeImage.setMarginTop(10);
            qrCodeImage.setMarginBottom(10);
            document.add(qrCodeImage);

            document.add(new Paragraph("Scan this QR code with your phone's camera to view the statistics summary")
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(20));

            // Close document
            document.close();

            showMessage("Export Successful", 
                "Statistics have been successfully exported to PDF:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            showError("Export Error", "Failed to export statistics to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateQRCodeContent(Map<Integer, Integer> demandsPerOffer) throws Exception {
        StringBuilder textBuilder = new StringBuilder();
        
        // Add header
        textBuilder.append("STATISTICS REPORT\n");
        textBuilder.append("================\n\n");
        
        // Add total demands
        int totalDemands = demandsPerOffer.values().stream().mapToInt(Integer::intValue).sum();
        textBuilder.append("Total Number of Demands: ").append(totalDemands).append("\n\n");
        
        // Add timestamp
        textBuilder.append("Report Generated: ")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                  .append("\n\n");
        
        // Sort offers by demand count
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(demandsPerOffer.entrySet());
        sortedEntries.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
        
        // Add top offers
        textBuilder.append("TOP OFFERS ANALYSIS\n");
        textBuilder.append("------------------\n\n");
        
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            if (count >= 5) break;
            
            Offer offer = offerService.getOfferBasicInfoById(entry.getKey());
            if (offer == null) continue;
            
            double percentage = (double) entry.getValue() / totalDemands * 100;
            
            textBuilder.append("Offer: ").append(offer.getNom()).append("\n");
            textBuilder.append("Domain: ").append(offer.getDomain()).append("\n");
            textBuilder.append("Number of Demands: ").append(entry.getValue()).append("\n");
            textBuilder.append("Percentage of Total: ").append(String.format("%.1f%%", percentage)).append("\n");
            textBuilder.append("------------------\n");
            
            count++;
        }
        
        return textBuilder.toString();
    }
    
    private void animateBarChart() {
        // Reset animation
        barChart.setAnimated(false);
        barChart.setAnimated(true);
        
        // Add hover effect animation
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                node.setStyle("-fx-bar-fill: #3498db;");
                
                // Add hover animation
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-bar-fill: #2980b9;");
                    node.setScaleX(1.05);
                    node.setScaleY(1.05);
                });
                
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-bar-fill: #3498db;");
                    node.setScaleX(1.0);
                    node.setScaleY(1.0);
                });
            }
        }
    }

    private void animatePieChart() {
        // Reset animation
        pieChart.setAnimated(false);
        pieChart.setAnimated(true);
        
        // Add hover effect animation
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            
            // Add hover animation
            node.setOnMouseEntered(event -> {
                node.setScaleX(1.1);
                node.setScaleY(1.1);
                node.setStyle("-fx-pie-color: derive(" + node.getStyle() + ", -20%);");
            });
            
            node.setOnMouseExited(event -> {
                node.setScaleX(1.0);
                node.setScaleY(1.0);
                node.setStyle("");
            });
        }
    }

    private Node getCalendarView() {
        try {
            // Load the calendar view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestion_emploi/Views/calendar-view.fxml"));
            Parent calendarRoot = loader.load();
            
            // Get the calendar controller
            CalendarController calendarController = loader.getController();
            
            // Update the calendar view
            calendarController.updateCalendar();
            
            return calendarRoot;
        } catch (Exception e) {
            System.err.println("Error loading calendar view: " + e.getMessage());
            return null;
        }
    }
    
    private void showNoDataMessage() {
        barChart.getData().clear();
        pieChart.getData().clear();
        showMessage("No Data", "No statistics data available");
    }
    
    @FXML
    private void goBack() {
        try {
            // Load the main view which has both calendar and statistics options
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestion_emploi/Views/main-view.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) barChart.getScene().getWindow();
            
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
            Stage stage = (Stage) barChart.getScene().getWindow();
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
