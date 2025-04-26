package services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import modele.Publication;
import utils.DataSource;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServicePublication implements IService<Publication> {
    private Connection con;
    private Cloudinary cloudinary;

    public ServicePublication() {
        this.con = DataSource.getInstance().getConnection();
        // Initialize Cloudinary with credentials from environment variables
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")));
    }

    private void validateConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Database connection is null or closed.");
        }
    }

    // Upload image to Cloudinary and return the URL
    private String uploadImageToCloudinary(File imageFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            System.err.println("Error uploading image to Cloudinary: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public void insert(Publication publication) {
        try {
            validateConnection();
            String imageUrl = publication.getImageUrl();
            // If imageUrl is a local file path, upload to Cloudinary
            if (imageUrl != null && new File(imageUrl).exists()) {
                imageUrl = uploadImageToCloudinary(new File(imageUrl));
                publication.setImageUrl(imageUrl);
            }
            String req = "INSERT INTO publication (titre, description, date, image, client_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setString(1, publication.getTitre());
                ps.setString(2, publication.getDescription());
                ps.setDate(3, Date.valueOf(publication.getDate()));
                ps.setString(4, imageUrl);
                ps.setInt(5, 1); // Temporary static client_id
                ps.executeUpdate();
                System.out.println("Publication inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting publication: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Publication publication) {
        try {
            validateConnection();
            String imageUrl = publication.getImageUrl();
            // If imageUrl is a local file path, upload to Cloudinary
            if (imageUrl != null && new File(imageUrl).exists()) {
                imageUrl = uploadImageToCloudinary(new File(imageUrl));
                publication.setImageUrl(imageUrl);
            }
            String req = "UPDATE publication SET titre=?, description=?, date=?, image=? WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setString(1, publication.getTitre());
                ps.setString(2, publication.getDescription());
                ps.setDate(3, Date.valueOf(publication.getDate()));
                ps.setString(4, imageUrl);
                ps.setInt(5, publication.getId());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Publication updated successfully!");
                } else {
                    System.err.println("No publication found with ID: " + publication.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating publication: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            validateConnection();
            con.setAutoCommit(false);
            try {
                // Delete notifications for related comments
                int notificationsDeleted = deleteRelatedRecords(
                        "DELETE FROM notification WHERE commentaire_id IN (SELECT id FROM commentaire WHERE publication_id = ?)", id);
                System.out.println("Deleted " + notificationsDeleted + " notifications for publication ID: " + id);

                // Delete related comments
                int commentsDeleted = deleteRelatedRecords("DELETE FROM commentaire WHERE publication_id = ?", id);
                System.out.println("Deleted " + commentsDeleted + " comments for publication ID: " + id);

                // Delete related reclamations
                int reclamationsDeleted = deleteRelatedRecords("DELETE FROM reclamation WHERE publication_id = ?", id);
                System.out.println("Deleted " + reclamationsDeleted + " reclamations for publication ID: " + id);

                // Delete related ratings
                int ratingsDeleted = deleteRelatedRecords("DELETE FROM rating WHERE publication_id = ?", id);
                System.out.println("Deleted " + ratingsDeleted + " ratings for publication ID: " + id);

                // Delete publication
                int publicationsDeleted = deleteRelatedRecords("DELETE FROM publication WHERE id = ?", id);
                if (publicationsDeleted == 0) {
                    throw new SQLException("No publication found with ID: " + id);
                }
                System.out.println("Publication deleted successfully!");

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error deleting publication: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to delete publication: " + e.getMessage(), e);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error during deletion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete publication: " + e.getMessage(), e);
        }
    }

    private int deleteRelatedRecords(String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<Publication> getAll() {
        List<Publication> list = new ArrayList<>();
        try {
            validateConnection();
            String req = "SELECT * FROM publication";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(req)) {
                while (rs.next()) {
                    list.add(new Publication(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("image")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving publications: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Publication getById(int id) {
        try {
            validateConnection();
            String req = "SELECT * FROM publication WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Publication(
                                rs.getInt("id"),
                                rs.getString("titre"),
                                rs.getString("description"),
                                rs.getDate("date").toLocalDate(),
                                rs.getString("image")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving publication: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}