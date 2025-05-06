package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Order;
import tn.esprit.pidev.Model.OrderItem;
import tn.esprit.pidev.Util.NavigationHelper;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.util.List;

public class OrderDetailController {
    @FXML private Label labelOrderId;
    @FXML private Label labelStatus;
    @FXML private Label labelDate;
    @FXML private Label labelTotal;
    @FXML private VBox itemsContainer;
    @FXML private VBox detailButtonContainer;
    @FXML private Button payButton;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        labelOrderId.setText(String.valueOf(order.getId()));
        labelStatus.setText(order.getStatus());
        labelDate.setText(order.getCreatedAt().toString());
        labelTotal.setText(String.format("%.2f TND", order.getTotal()));
        if (order.getItems() != null) {
            displayOrderItems(order.getItems());
        } else {
            itemsContainer.getChildren().clear();
            Label empty = new Label("Aucun article dans cette commande.");
            empty.setStyle("-fx-font-size: 15; -fx-text-fill: #a3a3a3; -fx-padding: 16;");
            itemsContainer.getChildren().add(empty);
        }
        // Show pay button for any variant of 'en attent' or 'en attente'
        String status = order.getStatus() != null ? order.getStatus().trim().toLowerCase() : "";
        if (status.equals("EN_ATTENTE") || status.equals("en attente") || status.equals("en_attente")) {
            payButton.setVisible(true);
            payButton.setManaged(true);
            payButton.setOnAction(e -> handlePayment(order));
        } else {
            payButton.setVisible(false);
            payButton.setManaged(false);
        }
    }

    private void displayOrderItems(List<OrderItem> items) {
        itemsContainer.getChildren().clear();
        if (items == null || items.isEmpty()) {
            Label empty = new Label("Aucun article dans cette commande.");
            empty.setStyle("-fx-font-size: 15; -fx-text-fill: #a3a3a3; -fx-padding: 16;");
            itemsContainer.getChildren().add(empty);
            return;
        }
        for (OrderItem item : items) {
            HBox card = new HBox(18);
            card.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, #b6e7c9, 4, 0, 0, 2); -fx-padding: 12; -fx-alignment: center-left;");
            Label produit = new Label("Produit: " + item.getProduitId());
            produit.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #388e3c;");
            Label quantite = new Label("Quantité: " + item.getQuantite());
            quantite.setStyle("-fx-font-size: 13; -fx-text-fill: #4a8c24;");
            Label prix = new Label(String.format("Prix: %.2f TND", item.getPrix()));
            prix.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #7bb661;");
            card.getChildren().addAll(produit, quantite, prix);
            itemsContainer.getChildren().add(card);
        }
    }

    @FXML
    private void goToOrderHistory(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/order/OrderHistory.fxml", "Historique des commandes");
    }

    @FXML
    private void handleRetour() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/order/OrderHistory.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) itemsContainer.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePayment(Order order) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/payment/PaymentView.fxml"));
            javafx.scene.Parent root = loader.load();
            // Optionally pass the order to the payment controller here
            javafx.stage.Stage stage = (javafx.stage.Stage) payButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
