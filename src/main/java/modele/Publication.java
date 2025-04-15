package modele;

import java.time.LocalDate;

public class Publication {
    private int id;
    private String titre;
    private String description;
    private LocalDate date;
    private String imageUrl;  // Store image URL

    // Constructor with ID
    public Publication(int id, String titre, String description, LocalDate date, String imageUrl) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    // Constructor without ID (for new publications)
    public Publication(String titre, String description, LocalDate date, String imageUrl) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Titre: " + titre + "\n" +
                "Description: " + description + "\n" +
                "Date: " + date + "\n" +
                "Image URL: " + imageUrl;
    }

}
