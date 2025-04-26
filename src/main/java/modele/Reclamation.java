package modele;

import java.time.LocalDate;

public class Reclamation {
    private int id;
    private String titre;
    private String description;
    private LocalDate date;
    private String status;
    private int publicationId;
    private int clientId;

    // Constructors
    public Reclamation() {
        this.date = LocalDate.now();
        this.status = "Pending"; // default status
    }

    public Reclamation(String titre, String description, int publicationId, int clientId) {
        this();
        this.titre = titre;
        this.description = description;
        this.publicationId = publicationId;
        this.clientId = clientId;
    }

    public Reclamation(String titre, String description, int publicationId, int clientId, LocalDate date) {
        this.titre = titre;
        this.description = description;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.date = date != null ? date : LocalDate.now();
        this.status = "Pending";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", publicationId=" + publicationId +
                ", clientId=" + clientId +
                '}';
    }
}