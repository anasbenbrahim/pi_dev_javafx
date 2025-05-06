package tn.esprit.pidev.Service;

import tn.esprit.pidev.Model.OrderItem;
import tn.esprit.pidev.Util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class OrderItemService {
    /**
     * Retrieves all order items for a specific order.
     * @param orderId The ID of the order
     * @return A list of order items
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
}
