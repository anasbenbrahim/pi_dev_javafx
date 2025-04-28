package tn.esprit.pidev.Model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private String status;
    private LocalDateTime createdAt;
    private double total;
    private List<OrderItem> items;

    public Order(int userId, LocalDateTime createdAt, double total, String status) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.total = total;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
