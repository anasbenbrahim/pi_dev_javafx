package tn.esprit.pidev.Model;

public class OrderItem {
    private int id;
    private int orderId;
    private int produitId;
    private int quantite;
    private double prix;

    public OrderItem(int orderId, int produitId, int quantite, double prix) {
        this.orderId = orderId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prix = prix;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProduitId() { return produitId; }
    public void setProduitId(int produitId) { this.produitId = produitId; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public double getTotal() {
        return quantite * prix;
    }
}
