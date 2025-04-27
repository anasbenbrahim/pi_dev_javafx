package models;

import java.time.LocalDate;

public class Demande {
    private int id;
    private int offer_id;
    private String service;
    private LocalDate date_demande;
    private String cv_file_name;
    private String phone_number;

    // Constructors
    public Demande() {
        this.date_demande = LocalDate.now(); // Default to current date
    }

    public Demande(int id, int offer_id, String service, LocalDate date_demande,
                   String cv_file_name, String phone_number) {
        this.id = id;
        this.offer_id = offer_id;
        this.service = service != null ? service.trim() : null;
        this.date_demande = date_demande;
        this.cv_file_name = cv_file_name != null ? cv_file_name.trim() : null;
        this.phone_number = phone_number != null ? phone_number.trim() : null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service != null ? service.trim() : null;
    }

    public LocalDate getDate_demande() {
        return date_demande;
    }

    public void setDate_demande(LocalDate date_demande) {
        this.date_demande = date_demande;
    }

    public String getCv_file_name() {
        return cv_file_name;
    }

    public void setCv_file_name(String cv_file_name) {
        this.cv_file_name = cv_file_name != null ? cv_file_name.trim() : null;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number != null ? phone_number.trim() : null;
    }

    // Validation method
    public boolean isValid() {
        return service != null && !service.isEmpty() &&
                date_demande != null &&
                phone_number != null && phone_number.matches("^\\d{10,15}$");
    }

    @Override
    public String toString() {
        return String.format("Demande #%d (Offer: %d, Service: %s)", id, offer_id, service);
    }

    // Additional utility methods
    public String getFormattedDate() {
        return date_demande != null ? date_demande.toString() : "N/A";
    }

    public String getShortServiceName(int maxLength) {
        if (service == null) return "";
        return service.length() > maxLength
                ? service.substring(0, maxLength) + "..."
                : service;
    }

}