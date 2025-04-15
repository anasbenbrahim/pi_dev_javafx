package modele;

public class Commentaire {
    private int id;
    private int publicationId;
    private int clientId;
    private String description;
    private String image;

    // Constructor that matches the parameters in your Addcommentaire controller
    public Commentaire(int publicationId, int clientId, String description, String image) {
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.description = description;
        this.image = image;
    }

    public Commentaire(int id, int publicationId, int clientId, String description, String image) {
    }

    // Getters and Setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
