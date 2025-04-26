package org.example.models;

public class Equipements {

    private int id;
    private String nom;
    private int quantite;
    private double prix;
    private String description;
    private String image;
    private Category_equipement category;

    public Equipements() {
        super();
    }

    public Equipements(int id, String nom, int quantite, double prix, String description, String image,Category_equipement category) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.description = description;
        this.image = image;
        this.category = category;
    }

    public Equipements(String nom, int quantite, double prix, String description, String image,Category_equipement category) {
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.description = description;
        this.image = image;
        this.category = category;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category_equipement getCategory() {
        return category;
    }

    public void setCategory(Category_equipement category) {
        this.category = category;
    }

    public String getNomCategorie() {
        if (category == null) {
            return "Indisponible";
        }
        return category.getType(); // ou getNom(), selon ton modèle
    }

    @Override
    public String toString() {
        return "Equipements{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }


}
