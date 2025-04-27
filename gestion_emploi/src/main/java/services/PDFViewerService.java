package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.Loader;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class PDFViewerService {
    private static final Logger logger = LoggerFactory.getLogger(PDFViewerService.class);

    public static void viewPDF(String filePath) {
        try {
            File pdfFile = new File(filePath);
            if (!pdfFile.exists()) {
                showError("PDF not found", "The PDF file could not be found at: " + filePath);
                return;
            }

            // Create PDF document using Loader class
            PDDocument document = Loader.loadPDF(pdfFile);
            PDFRenderer renderer = new PDFRenderer(document);

            // Create a new stage for the PDF viewer
            Stage pdfStage = new Stage();
            pdfStage.setTitle("PDF Viewer - " + pdfFile.getName());
            pdfStage.initModality(Modality.APPLICATION_MODAL);

            // Create image view for the PDF
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Convert first page to image
            Image pdfImage = convertPDFPageToImage(renderer, 0);
            imageView.setImage(pdfImage);

            // Create layout
            StackPane root = new StackPane(imageView);
            Scene scene = new Scene(root, 800, 600);

            // Add zoom controls
            scene.setOnScroll(event -> {
                double zoomFactor = 1.05;
                if (event.getDeltaY() < 0) {
                    zoomFactor = 0.95;
                }
                imageView.setScaleX(imageView.getScaleX() * zoomFactor);
                imageView.setScaleY(imageView.getScaleY() * zoomFactor);
            });

            pdfStage.setScene(scene);
            pdfStage.show();

            // Close document when window is closed
            pdfStage.setOnCloseRequest(event -> {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.error("Error closing PDF document", e);
                }
            });

        } catch (IOException e) {
            logger.error("Error loading PDF", e);
            showError("Error loading PDF", "Could not load the PDF file: " + e.getMessage());
        }
    }

    private static Image convertPDFPageToImage(PDFRenderer renderer, int pageNumber) throws IOException {
        java.awt.image.BufferedImage awtImage = renderer.renderImageWithDPI(pageNumber, 150);
        return SwingFXUtils.toFXImage(awtImage, null);
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 