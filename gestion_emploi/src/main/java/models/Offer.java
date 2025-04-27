package models;

import java.time.LocalDate;

public class Offer {
    private int id;
    private String nom;
    private String domain;
    private LocalDate date_offer;
    private String description;
    private int nb_places;

    // Constructors
    public Offer() {
        this.date_offer = LocalDate.now(); // Default to current date
        this.nb_places = 1; // Default to minimum places
    }

    public Offer(int id, String nom, String domain, LocalDate date_offer,
                 String description, int nb_places) {
        this.id = id;
        this.nom = nom != null ? nom.trim() : null;
        this.domain = domain != null ? domain.trim() : null;
        this.date_offer = date_offer;
        this.description = description != null ? description.trim() : null;
        this.nb_places = nb_places > 0 ? nb_places : 1; // Ensure positive number
    }

    // Getters and Setters
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
        this.nom = nom != null ? nom.trim() : null;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain != null ? domain.trim() : null;
    }

    public LocalDate getDate_offer() {
        return date_offer;
    }

    public void setDate_offer(LocalDate date_offer) {
        this.date_offer = date_offer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public int getNb_places() {
        return nb_places;
    }

    public void setNb_places(int nb_places) {
        this.nb_places = nb_places > 0 ? nb_places : this.nb_places; // Keep current value if invalid
    }

    // Validation method
    public boolean isValid() {
        return nom != null && !nom.isEmpty() &&
                domain != null && !domain.isEmpty() &&
                date_offer != null &&
                nb_places > 0;
    }


    @Override
    public String toString() {
        return String.format("%s (Domain: %s, Places: %d)", nom, domain, nb_places);
    }

    // Utility methods
    public boolean isExpired() {
        return date_offer != null && date_offer.isBefore(LocalDate.now());
    }

    public String getShortDescription(int maxLength) {
        if (description == null) return "";
        return description.length() > maxLength
                ? description.substring(0, maxLength) + "..."
                : description;
    }

    public boolean hasAvailablePlaces() {
        return nb_places > 0;
    }
}
