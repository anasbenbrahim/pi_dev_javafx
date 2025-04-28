package tn.esprit.pidev.Model;

public class PanierItem {
    private Produit produit;
    private int quantite;

    public PanierItem(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getTotal() {
        return produit.getPrix() * quantite;
    }
}
