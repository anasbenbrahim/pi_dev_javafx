package services;

import modele.Commentaire;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire implements IService<Commentaire> {
    private Connection con;

    public ServiceCommentaire() {
        this.con = DataSource.getInstance().getConnection();
    }

    private void validateConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Database connection is null or closed.");
        }
    }

    @Override
    public void insert(Commentaire commentaire) {
        try {
            validateConnection();
            String req = "INSERT INTO commentaire (description, publication_id, client_id, image) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
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
                System.out.println("Comment added successfully!");
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
            String req = "UPDATE commentaire SET description=?, image=? WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setString(1, commentaire.getDescription());
                ps.setString(2, commentaire.getImage());
                ps.setInt(3, commentaire.getId());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Comment updated successfully!");
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
            String req = "DELETE FROM commentaire WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("No comment found with ID: " + id);
                    throw new SQLException("No comment found with ID: " + id);
                }
                System.out.println("Comment deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete comment: " + e.getMessage(), e);
        }
    }

    private int deleteRelatedRecords(String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<Commentaire> getAll() {
        List<Commentaire> list = new ArrayList<>();
        try {
            validateConnection();
            String req = "SELECT * FROM commentaire";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(req)) {
                while (rs.next()) {
                    list.add(new Commentaire(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getInt("publication_id"),
                            rs.getInt("client_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Commentaire getById(int id) {
        try {
            validateConnection();
            String req = "SELECT * FROM commentaire WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Commentaire(
                                rs.getInt("id"),
                                rs.getString("description"),
                                rs.getInt("publication_id"),
                                rs.getInt("client_id")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Commentaire> afficherCommentairesParPublication(int publicationId) {
        List<Commentaire> list = new ArrayList<>();
        try {
            validateConnection();
            String req = "SELECT * FROM commentaire WHERE publication_id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setInt(1, publicationId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Commentaire commentaire = new Commentaire(
                                rs.getInt("id"),
                                rs.getString("description"),
                                rs.getInt("publication_id"),
                                rs.getInt("client_id")
                        );
                        System.out.println("Fetched comment: " + commentaire.getDescription());
                        list.add(commentaire);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving comments for publication: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}