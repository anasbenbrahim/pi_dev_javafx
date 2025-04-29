package org.example.models;

public class ReponseDevis {
    private int id;
    private int devis_id;
    private int fournisseur_id;
    private int fermier_id;
    private String reponse;
    private int etat;
    private Double prix;

    public ReponseDevis() {
        super();
    }

    public ReponseDevis(int devis, int fournisseur_id, int fermier_id, String reponse, int etat,double prix) {
        this.devis_id = devis;
        this.fournisseur_id = fournisseur_id;
        this.fermier_id = fermier_id;
        this.reponse = reponse;
        this.etat = etat;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevis() {
        return devis_id;
    }

    public void setDevis(int devis) {
        this.devis_id = devis;
    }

    public int getFournisseur_id() {
        return fournisseur_id;
    }

    public void setFournisseur_id(int fournisseur_id) {
        this.fournisseur_id = fournisseur_id;
    }

    public int getFermier_id() {
        return fermier_id;
    }

    public void setFermier_id(int fermier_id) {
        this.fermier_id = fermier_id;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {

        this.reponse = reponse;
    }

    public int getEtat() {

        return etat;
    }

    public void setEtat(int etat) {

        this.etat = etat;
    }

    public  Double getPrix() {
        return prix;
    }
    public void setPrix(Double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "ReponseDevis{" +
                "id=" + id +
                ", devis_id=" + devis_id +
                ", fournisseur_id=" + fournisseur_id +
                ", fermier_id=" + fermier_id +
                ", reponse='" + reponse + '\'' +
                ", etat=" + etat +
                ", prix=" + prix +
                '}';
    }
}
