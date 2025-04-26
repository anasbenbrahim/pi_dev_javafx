package org.example.models;

public class Devis {
    private int id;
    private int equipement_id;
    private int fermier_id;
    private int fournisseur_id;
    private String proposition;
    private int quantite;

    public Devis() {
        super();
    }

    public Devis(String proposition, int quantite) {
        this.proposition = proposition;
        this.quantite = quantite;
    }

    public Devis( int equipement, int fermier_id, int fournisseur_id, String proposition, int quantite) {
        this.equipement_id = equipement;
        this.fermier_id = fermier_id;
        this.fournisseur_id = fournisseur_id;
        this.proposition = proposition;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipement_id() {
        return this.equipement_id;
    }

    public void setEquipement_id(int equipement) {
        this.equipement_id = equipement;
    }

    public int getFermier_id() {
        return fermier_id;
    }

    public void setFermier_id(int fermier_id) {
        this.fermier_id = fermier_id;
    }

    public int getFournisseur_id() {
        return fournisseur_id;
    }

    public void setFournisseur_id(int fournisseur_id) {
        this.fournisseur_id = fournisseur_id;
    }

    public String getProposition() {
        return proposition;
    }

    public void setProposition(String proposition) {
        this.proposition = proposition;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Devis{" +
                "id=" + id +
                ", equipement=" + equipement_id +
                ", fermier_id=" + fermier_id +
                ", fournisseur_id=" + fournisseur_id +
                ", proposition='" + proposition + '\'' +
                ", quantite=" + quantite +
                '}';
    }
}
