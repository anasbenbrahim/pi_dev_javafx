package tn.esprit.pidev.Service;

import tn.esprit.pidev.Model.Order;
import tn.esprit.pidev.Model.OrderItem;
import tn.esprit.pidev.Util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection conn;

    public OrderDAO() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, created_at, total, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
            pstmt.setDouble(3, order.getTotal());
            pstmt.setString(4, order.getStatus());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addOrderItems(int orderId, List<OrderItem> items) {
        String sql = "INSERT INTO order_item (order_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (OrderItem item : items) {
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, item.getProduitId());
                pstmt.setInt(3, item.getQuantite());
                pstmt.setDouble(4, item.getPrix());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getDouble("total"),
                    rs.getString("status")
                );
                order.setId(rs.getInt("id"));
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("order_id"),
                    rs.getInt("produit_id"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                );
                item.setId(rs.getInt("id"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getDouble("total"),
                    rs.getString("status")
                );
                order.setId(rs.getInt("id"));
                order.setItems(getOrderItems(order.getId()));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteOrder(int orderId) {
        String deleteItemsSql = "DELETE FROM order_item WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement pstmtItems = conn.prepareStatement(deleteItemsSql);
             PreparedStatement pstmtOrder = conn.prepareStatement(deleteOrderSql)) {
            pstmtItems.setInt(1, orderId);
            pstmtItems.executeUpdate();
            pstmtOrder.setInt(1, orderId);
            int rowsAffected = pstmtOrder.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
