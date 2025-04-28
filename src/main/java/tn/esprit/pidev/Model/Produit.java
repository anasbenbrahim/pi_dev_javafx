package tn.esprit.pidev.Model;

public class Produit {
    private int id;
    private int userId;
    private int categoryId;
    private String nomprod;
    private String image;
    private double prix;
    private int quantite;
    private String descr;
    private int status;  // 1 = Disponible, 0 = Indisponible

    // Constructors
    public Produit() {
    }

    public Produit(int userId, int categoryId, String nomprod, String image,
                   double prix, int quantite, String descr, int status) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.nomprod = nomprod;
        this.image = image;
        this.prix = prix;
        this.quantite = quantite;
        this.descr = descr;
        this.status = status;
    }

    // Full constructor with ID
    public Produit(int id, int userId, int categoryId, String nomprod, String image,
                   double prix, int quantite, String descr, int status) {
        this(userId, categoryId, nomprod, image, prix, quantite, descr, status);
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getNomprod() {
        return nomprod;
    }

    public void setNomprod(String nomprod) {
        this.nomprod = nomprod;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Helper method for UI display
    public String getStatusAsString() {
        return status == 1 ? "Disponible" : "Indisponible";
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f TND (Stock: %d)",
                nomprod, prix, quantite);
    }
}