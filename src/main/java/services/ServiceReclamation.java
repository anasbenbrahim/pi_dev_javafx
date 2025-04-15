package services;

import modele.Reclamation;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService<Reclamation> {
    private Connection con;

    public ServiceReclamation() {
        this.con = DataSource.getInstance().getConnection();
    }

    @Override
    public void insert(Reclamation reclamation) {
        String req = "INSERT INTO reclamation (titre, description, date, status, publication_id, client_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reclamation.getTitre());
            ps.setString(2, reclamation.getDescription());
            ps.setDate(3, Date.valueOf(reclamation.getDate()));
            ps.setString(4, reclamation.getStatus());
            ps.setInt(5, reclamation.getPublicationId());
            ps.setInt(6, reclamation.getClientId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reclamation.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting reclamation: " + e.getMessage());
        }
    }

    @Override
    public void update(Reclamation reclamation) {
        String req = "UPDATE reclamation SET titre=?, description=?, date=?, status=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, reclamation.getTitre());
            ps.setString(2, reclamation.getDescription());
            ps.setDate(3, Date.valueOf(reclamation.getDate()));
            ps.setString(4, reclamation.getStatus());
            ps.setInt(5, reclamation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reclamation: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM reclamation WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reclamation: " + e.getMessage());
        }
    }

    @Override
    public List<Reclamation> getAll() {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        try (PreparedStatement ps = con.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractReclamationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reclamations: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Reclamation getById(int id) {
        String req = "SELECT * FROM reclamation WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractReclamationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reclamation: " + e.getMessage());
        }
        return null;
    }

    public List<Reclamation> getByPublicationId(int publicationId) {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE publication_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReclamationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reclamations by publication: " + e.getMessage());
        }
        return list;
    }

    public Reclamation getByPublicationAndClient(int publicationId, int clientId) {
        String req = "SELECT * FROM reclamation WHERE publication_id=? AND client_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, publicationId);
            ps.setInt(2, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractReclamationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reclamation by publication and client: " + e.getMessage());
        }
        return null;
    }

    public List<Reclamation> getByClientId(int clientId) {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE client_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReclamationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reclamations by client: " + e.getMessage());
        }
        return list;
    }

    private Reclamation extractReclamationFromResultSet(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setId(rs.getInt("id"));
        r.setTitre(rs.getString("titre"));
        r.setDescription(rs.getString("description"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setStatus(rs.getString("status"));
        r.setPublicationId(rs.getInt("publication_id"));
        r.setClientId(rs.getInt("client_id"));
        return r;
    }
}