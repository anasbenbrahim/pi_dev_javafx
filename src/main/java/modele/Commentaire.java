package modele;

public class Commentaire {
    private int id;
    private int publicationId;
    private int clientId;
    private String description;
    private String image;

    // Constructor for adding a new comment without an image
    public Commentaire(String description, int publicationId, int clientId) {
        this.description = description;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.image = null;
    }

    // Constructor for database retrieval without image
    public Commentaire(int id, String description, int publicationId, int clientId) {
        this.id = id;
        this.description = description;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.image = null;
    }

    // Constructor for adding a new comment with an image
    public Commentaire(int publicationId, int clientId, String description, String image) {
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.description = description;
        this.image = image;
    }

    // Constructor for database retrieval with image
    public Commentaire(int id, int publicationId, int clientId, String description, String image) {
        this.id = id;
        this.publicationId = publicationId;
        this.clientId = clientId;
        this.description = description;
        this.image = image;
    }

    // Getters and Setters
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