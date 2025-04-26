package services;

import modele.Notification;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final Connection con;

    public NotificationService() {
        this.con = DataSource.getInstance().getConnection();
    }

    // Add a new notification
    public void addNotification(Notification notification) {
        String sql = "INSERT INTO notification (message, publication_id, date, Reading, client_id, commentaire_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, notification.getMessage());
            ps.setInt(2, notification.getPublicationId());
            ps.setTimestamp(3, Timestamp.valueOf(notification.getDate()));
            ps.setBoolean(4, notification.isReading());
            ps.setInt(5, notification.getClientId());
            if (notification.getCommentaireId() != null) {
                ps.setInt(6, notification.getCommentaireId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();

            // Set the generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                notification.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error adding notification: " + e.getMessage());
        }
    }

    // Get notifications for a user
    public List<Notification> getNotificationsByClientId(int clientId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE client_id = ? ORDER BY date DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getInt("publication_id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getBoolean("Reading"),
                        rs.getInt("client_id"),
                        rs.getObject("commentaire_id") != null ? rs.getInt("commentaire_id") : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications: " + e.getMessage());
        }
        return notifications;
    }

    // Mark a notification as read
    public void markAsRead(int notificationId) {
        String sql = "UPDATE notification SET Reading = TRUE WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
        }
    }

    // Delete a notification
    public void deleteNotification(int notificationId) {
        String sql = "DELETE FROM notification WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
        }
    }
}