package tn.esprit.pidev.Service;

import tn.esprit.pidev.Model.Order;
import tn.esprit.pidev.Service.OrderItemService;
import tn.esprit.pidev.Service.OrderDAO;
import tn.esprit.pidev.Util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class OrderService {
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM orders WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            OrderItemService itemService = new OrderItemService();
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getDouble("total"),
                    rs.getString("status")
                );
                order.setId(rs.getInt("id"));
                // Charger les items de la commande
                order.setItems(itemService.getOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean deleteOrder(int orderId) {
        return new OrderDAO().deleteOrder(orderId);
    }
}
