package services;

import modele.Publication;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePublication implements IService<Publication> {
    private Connection con;

    public ServicePublication() {
        this.con = DataSource.getInstance().getConnection();
    }

    @Override
    public void insert(Publication publication) {
        String req = "INSERT INTO publication (titre, description, date, image, client_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, publication.getTitre());
            ps.setString(2, publication.getDescription());
            ps.setDate(3, Date.valueOf(publication.getDate()));
            ps.setString(4, publication.getImageUrl());
            ps.setInt(5, 1); // Temporary static client_id
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting publication: " + e.getMessage());
        }
    }

    @Override
    public void update(Publication publication) {
        String req = "UPDATE publication SET titre=?, description=?, date=?, image=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, publication.getTitre());
            ps.setString(2, publication.getDescription());
            ps.setDate(3, Date.valueOf(publication.getDate()));
            ps.setString(4, publication.getImageUrl());
            ps.setInt(5, publication.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating publication: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        Connection conn = null;
        try {
            conn = DataSource.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Delete related comments
            deleteRelatedRecords(conn, "DELETE FROM commentaire WHERE publication_id = ?", id);

            // Delete related reclamations
            deleteRelatedRecords(conn, "DELETE FROM reclamation WHERE publication_id = ?", id);

            // Delete publication
            deleteRelatedRecords(conn, "DELETE FROM publication WHERE id = ?", id);

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            System.err.println("Error deleting publication: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private void deleteRelatedRecords(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Publication> getAll() {
        List<Publication> list = new ArrayList<>();
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
        } catch (SQLException e) {
            System.err.println("Error retrieving publications: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Publication getById(int id) {
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
        } catch (SQLException e) {
            System.err.println("Error retrieving publication: " + e.getMessage());
        }
        return null;
    }
}