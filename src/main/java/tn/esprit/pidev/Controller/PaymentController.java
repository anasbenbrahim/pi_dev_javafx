package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Order;
import tn.esprit.pidev.Service.OrderDAO;
import tn.esprit.pidev.Service.StripePaymentService;
import tn.esprit.pidev.Util.NavigationHelper;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import com.stripe.exception.StripeException;

public class PaymentController implements Initializable {

    @FXML
    private Label orderIdLabel;
    
    @FXML
    private Label orderDateLabel;
    
    @FXML
    private Label orderTotalLabel;
    
    @FXML
    private TextField cardNumberField;
    
    @FXML
    private TextField cardNameField;
    
    @FXML
    private TextField expiryMonthField;
    
    @FXML
    private TextField expiryYearField;
    
    @FXML
    private TextField cvcField;
    
    @FXML
    private Button payButton;
    
    @FXML
    private Label paymentStatusLabel;
    
    @FXML
    private WebView webView;
    
    private Order currentOrder;
    private OrderDAO orderDAO;
    private StripePaymentService stripeService;
    private String paymentIntentClientSecret;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orderDAO = new OrderDAO();
        stripeService = new StripePaymentService();
    }
    
    /**
     * Set the order to be paid
     * @param orderId The ID of the order to be paid
     */
    public void setOrder(int orderId) {
        currentOrder = orderDAO.getOrderById(orderId);
        if (currentOrder != null) {
            displayOrderDetails();
            try {
                // Create a payment intent with Stripe
                paymentIntentClientSecret = stripeService.createPaymentIntent(currentOrder);
                loadPaymentPage("https://stripe.com/pay/" + paymentIntentClientSecret);
            } catch (StripeException e) {
                e.printStackTrace();
                showPaymentError("Erreur lors de la création du paiement. Veuillez réessayer.");
            }
        } else {
            showPaymentError("Commande introuvable.");
        }
    }
    
    /**
     * Display order details in the UI
     */
    private void displayOrderDetails() {
        orderIdLabel.setText(String.valueOf(currentOrder.getId()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        orderDateLabel.setText(currentOrder.getCreatedAt().format(formatter));
        
        orderTotalLabel.setText(String.format("%.2f DT", currentOrder.getTotal()));
    }
    
    /**
     * Handle the payment button click
     */
    @FXML
    private void handlePayment() {
        if (!validatePaymentForm()) {
            return;
        }
        
        // In a real implementation, we would use the Stripe SDK to confirm the payment
        // using the paymentIntentClientSecret and card details
        // For this demo, we'll simulate a successful payment
        
        boolean paymentSuccessful = simulatePayment();
        
        if (paymentSuccessful) {
            // Update order status to PAYEE
            if (orderDAO.updateOrderStatus(currentOrder.getId(), "PAYEE")) {
                showPaymentSuccess();
                
                // Navigate to order history after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            Stage stage = (Stage) payButton.getScene().getWindow();
                            NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/order/OrderHistory.fxml", "Historique des commandes");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showPaymentError("Erreur lors de la mise à jour du statut de la commande.");
            }
        } else {
            showPaymentError("Paiement refusé. Veuillez vérifier vos informations et réessayer.");
        }
    }
    
    /**
     * Validate the payment form
     * @return true if the form is valid, false otherwise
     */
    private boolean validatePaymentForm() {
        // Reset error message
        paymentStatusLabel.setVisible(false);
        
        // Check if all fields are filled
        if (cardNumberField.getText().trim().isEmpty() ||
            cardNameField.getText().trim().isEmpty() ||
            expiryMonthField.getText().trim().isEmpty() ||
            expiryYearField.getText().trim().isEmpty() ||
            cvcField.getText().trim().isEmpty()) {
            
            showPaymentError("Veuillez remplir tous les champs.");
            return false;
        }
        
        // Validate card number (should be 16 digits)
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        if (!cardNumber.matches("\\d{16}")) {
            showPaymentError("Le numéro de carte doit contenir 16 chiffres.");
            return false;
        }
        
        // Validate expiry month (1-12)
        try {
            int month = Integer.parseInt(expiryMonthField.getText());
            if (month < 1 || month > 12) {
                showPaymentError("Le mois d'expiration doit être entre 1 et 12.");
                return false;
            }
        } catch (NumberFormatException e) {
            showPaymentError("Le mois d'expiration doit être un nombre.");
            return false;
        }
        
        // Validate expiry year (current year or later)
        try {
            int year = Integer.parseInt(expiryYearField.getText());
            int currentYear = java.time.LocalDate.now().getYear() % 100; // Get last 2 digits of year
            if (year < currentYear) {
                showPaymentError("L'année d'expiration ne peut pas être dans le passé.");
                return false;
            }
        } catch (NumberFormatException e) {
            showPaymentError("L'année d'expiration doit être un nombre.");
            return false;
        }
        
        // Validate CVC (3 or 4 digits)
        if (!cvcField.getText().matches("\\d{3,4}")) {
            showPaymentError("Le code de sécurité doit contenir 3 ou 4 chiffres.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Simulate a payment (for demo purposes)
     * @return true if the payment was successful, false otherwise
     */
    private boolean simulatePayment() {
        // In a real implementation, we would use the Stripe SDK to confirm the payment
        // For this demo, we'll simulate a successful payment
        return true;
    }
    
    /**
     * Show a payment error message
     * @param message The error message to show
     */
    private void showPaymentError(String message) {
        paymentStatusLabel.setText(message);
        paymentStatusLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
        paymentStatusLabel.setVisible(true);
    }
    
    /**
     * Show a payment success message
     */
    private void showPaymentSuccess() {
        paymentStatusLabel.setText("Paiement réussi! Redirection vers l'historique des commandes...");
        paymentStatusLabel.setStyle("-fx-text-fill: #43a047; -fx-font-weight: bold;");
        paymentStatusLabel.setVisible(true);
    }
    
    /**
     * Handle the cancel button click
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) payButton.getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/marche-view.fxml", "Marché");
    }
    
    /**
     * Load Stripe Checkout payment page
     * @param url The URL of the payment page
     */
    public void loadPaymentPage(String url) {
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            engine.load(url);
        }
    }
}