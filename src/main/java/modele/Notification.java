package modele;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String message;
    private int publicationId;
    private LocalDateTime date;
    private boolean reading;
    private int clientId;
    private Integer commentaireId; // Nullable

    // Constructor for new notifications
    public Notification(String message, int publicationId, int clientId, Integer commentaireId) {
        this.message = message;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.commentaireId = commentaireId;
        this.date = LocalDateTime.now();
        this.reading = false;
    }

    // Constructor for database retrieval
    public Notification(int id, String message, int publicationId, LocalDateTime date, boolean reading, int clientId, Integer commentaireId) {
        this.id = id;
        this.message = message;
        this.publicationId = publicationId;
        this.date = date;
        this.reading = reading;
        this.clientId = clientId;
        this.commentaireId = commentaireId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isReading() {
        return reading;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Integer getCommentaireId() {
        return commentaireId;
    }

    public void setCommentaireId(Integer commentaireId) {
        this.commentaireId = commentaireId;
    }
}