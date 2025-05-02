package services;

import modele.Commentaire;
import utils.DataSource;
import okhttp3.*;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire implements IService<Commentaire> {
    private final Connection con;
    private final OkHttpClient httpClient;

    public ServiceCommentaire() {
        this.con = DataSource.getInstance().getConnection();
        this.httpClient = new OkHttpClient();
    }

    private void validateConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Database connection is null or closed.");
        }
    }

    private void moderateComment(String content) throws SQLException, ProfanityException {
        System.out.println("Checking comment: " + content);
        try {
            String url = "https://www.purgomalum.com/service/containsprofanity?text=" +
                    java.net.URLEncoder.encode(content, java.nio.charset.StandardCharsets.UTF_8);
            Request request = new Request.Builder().url(url).get().build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    throw new SQLException("PurgoMalum API request failed: HTTP " + response.code() + " - " + responseBody);
                }
                String result = response.body().string();
                System.out.println("PurgoMalum API response: " + result);
                if (result.trim().equalsIgnoreCase("true")) {
                    throw new ProfanityException("Your comment contains inappropriate words and was flagged by PurgoMalum");
                }
            }
        } catch (Exception e) {
            if (e instanceof ProfanityException) {
                throw (ProfanityException) e;
            }
            String errorMessage = "Error checking comment with PurgoMalum: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            throw new SQLException("Failed to check comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Commentaire commentaire) {
        try {
            validateConnection();
            moderateComment(commentaire.getDescription());

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
        } catch (ProfanityException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("Error inserting comment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to insert comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Commentaire commentaire) {
        try {
            validateConnection();
            moderateComment(commentaire.getDescription());

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
        } catch (ProfanityException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("Error updating comment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            validateConnection();
            int notificationsDeleted = deleteRelatedRecords("DELETE FROM notification WHERE commentaire_id = ?", id);
            System.out.println("Deleted " + notificationsDeleted + " notifications for comment ID: " + id);

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