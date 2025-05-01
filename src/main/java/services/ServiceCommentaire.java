package services;

import modele.Commentaire;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing Commentaire entities in the database.
 * Provides CRUD operations and retrieval of comments by publication.
 */
public class ServiceCommentaire implements IService<Commentaire> {
    private final Connection con;

    /**
     * Constructor initializes the database connection.
     */
    public ServiceCommentaire() {
        this.con = DataSource.getInstance().getConnection();
    }

    /**
     * Validates that the database connection is active.
     * @throws SQLException if the connection is null or closed
     */
    private void validateConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Database connection is null or closed.");
        }
    }


    @Override
    public void insert(Commentaire commentaire) {
        try {
            validateConnection();
            String query = "INSERT INTO commentaire (description, publication_id, client_id, image) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, commentaire.getDescription());
                ps.setInt(2, commentaire.getPublicationId());
                ps.setInt(3, commentaire.getClientId());
                ps.setString(4, commentaire.getImage());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        commentaire.setId(rs.getInt(1));
                    }
                }
                System.out.println("Comment added successfully with ID: " + commentaire.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error inserting comment: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void update(Commentaire commentaire) {
        try {
            validateConnection();
            String query = "UPDATE commentaire SET description = ?, image = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, commentaire.getDescription());
                ps.setString(2, commentaire.getImage());
                ps.setInt(3, commentaire.getId());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Comment updated successfully with ID: " + commentaire.getId());
                } else {
                    System.err.println("No comment found with ID: " + commentaire.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating comment: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void delete(int id) {
        try {
            validateConnection();
            // Delete related notifications
            int notificationsDeleted = deleteRelatedRecords("DELETE FROM notification WHERE commentaire_id = ?", id);
            System.out.println("Deleted " + notificationsDeleted + " notifications for comment ID: " + id);

            // Delete comment
            String query = "DELETE FROM commentaire WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("No comment found with ID: " + id);
                    throw new SQLException("No comment found with ID: " + id);
                }
                System.out.println("Comment deleted successfully with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete comment: " + e.getMessage(), e);
        }
    }


    private int deleteRelatedRecords(String query, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }


    @Override
    public List<Commentaire> getAll() {
        List<Commentaire> comments = new ArrayList<>();
        try {
            validateConnection();
            String query = "SELECT * FROM commentaire";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    comments.add(new Commentaire(
                            rs.getInt("id"),
                            rs.getInt("publication_id"),
                            rs.getInt("client_id"),
                            rs.getString("description"),
                            rs.getString("image")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comments: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }


    @Override
    public Commentaire getById(int id) {
        try {
            validateConnection();
            String query = "SELECT * FROM commentaire WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Commentaire(
                                rs.getInt("id"),
                                rs.getInt("publication_id"),
                                rs.getInt("client_id"),
                                rs.getString("description"),
                                rs.getString("image")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comment with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public List<Commentaire> afficherCommentairesParPublication(int publicationId) {
        List<Commentaire> comments = new ArrayList<>();
        try {
            validateConnection();
            String query = "SELECT * FROM commentaire WHERE publication_id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, publicationId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Commentaire commentaire = new Commentaire(
                                rs.getInt("id"),
                                rs.getInt("publication_id"),
                                rs.getInt("client_id"),
                                rs.getString("description"),
                                rs.getString("image")
                        );
                        System.out.println("Fetched comment ID: " + commentaire.getId() +
                                ", Description: " + commentaire.getDescription() +
                                ", Image: " + commentaire.getImage());
                        comments.add(commentaire);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comments for publication ID " + publicationId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }
}