package esprit.tn.entities;

import java.sql.Date;
import java.util.Objects;

public class Event {
    private int id;
    private String nom;
    private String descr;
    private Date date;  // Changé en java.sql.Date
    private String type;
    private String photo;

    // Constructeurs
    public Event() {}

    public Event(int id, String nom, String descr, Date date, String type, String photo) {
        this.id = id;
        this.nom = nom;
        this.descr = descr;
        this.date = date;
        this.type = type;
        this.photo = photo;
    }

    public Event(String nom, String descr, Date date, String type, String photo) {
        this.nom = nom;
        this.descr = descr;
        this.date = date;
        this.type = type;
        this.photo = photo;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    // equals, hashCode et toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id == event.id &&
                Objects.equals(nom, event.nom) &&
                Objects.equals(descr, event.descr) &&
                Objects.equals(date, event.date) &&
                Objects.equals(type, event.type) &&
                Objects.equals(photo, event.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, descr, date, type, photo);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", descr='" + descr + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    // Méthode d'alias pour la compatibilité
    public String getDescription() {
        return this.descr;
    }
}