package tn.esprit.pidev.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.Order;
import tn.esprit.pidev.Service.OrderService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import tn.esprit.pidev.Util.NavigationHelper;
import java.util.List;

public class OrderHistoryController {
    @FXML private VBox ordersContainer;

    private int userId = 1; // Remplacer par l'utilisateur connecté

    @FXML
    public void initialize() {
        loadOrders();
    }

    private void loadOrders() {
        try {
            ordersContainer.getChildren().clear();
            OrderService service = new OrderService();
            List<Order> orders = service.getOrdersByUserId(userId);
            for (Order order : orders) {
                HBox card = new HBox(24);
                card.setStyle("-fx-background-color: #fff; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, #b6e7c9, 6, 0, 0, 2); -fx-padding: 18; -fx-alignment: center-left;");
                Label id = new Label("Commande #" + order.getId());
                id.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #388e3c;");
                Label status = new Label(order.getStatus());
                status.setStyle("-fx-background-color: #e8f4d8; -fx-padding: 5 14 5 14; -fx-background-radius: 8; -fx-text-fill: #4a8c24;");
                Label date = new Label(order.getCreatedAt().toString());
                date.setStyle("-fx-font-size: 13; -fx-text-fill: #7bb661;");
                Label total = new Label(String.format("%.2f TND", order.getTotal()));
                total.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #388e3c;");
                card.getChildren().addAll(id, status, date, total);

                Button deleteButton = new Button("Supprimer");
                deleteButton.setStyle("-fx-background-color: #e57373; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;");
                deleteButton.setOnAction(e -> {
                    OrderService serviceDel = new OrderService();
                    serviceDel.deleteOrder(order.getId());
                    loadOrders();
                });

                Button payButton = null;
                if (order.getStatus().equalsIgnoreCase("en attent")) {
                    payButton = new Button("Payer");
                    payButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;");
                    payButton.setOnAction(e -> handlePayment(order));
                }

                card.getChildren().add(deleteButton);
                if (payButton != null) card.getChildren().add(payButton);

                card.setOnMouseClicked(e -> showOrderDetail(order));
                ordersContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrderDetail(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/order/OrderDetail.fxml"));
            Parent root = loader.load();
            OrderDetailController controller = loader.getController();
            controller.setOrder(order);
            Stage stage = (Stage) ordersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToOrderDetail(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/order/OrderDetail.fxml", "Détail Commande");
    }

    @FXML
    private void goToMainView(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateTo(stage, "/tn/esprit/pidev/view/MainView.fxml", "Accueil");
    }

    // Ajout de la méthode de suppression dans OrderService
    public boolean deleteOrder(int orderId) {
        return new tn.esprit.pidev.Service.OrderDAO().deleteOrder(orderId);
    }

    // Handler pour le paiement
    private void handlePayment(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/view/payment/PaymentView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ordersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            // Vous pouvez passer l'objet order au contrôleur de paiement ici
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
