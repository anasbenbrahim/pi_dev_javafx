package modele;

import java.time.LocalDateTime;

public class Rating {
    private int id;
    private int publicationId;
    private int clientId;
    private int rating;
    private LocalDateTime createdAt;

    public Rating(int id, int publicationId, int clientId, int rating, LocalDateTime createdAt) {
        this.id = id;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Rating(int publicationId, int clientId, int rating) {
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPublicationId() { return publicationId; }
    public void setPublicationId(int publicationId) { this.publicationId = publicationId; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}