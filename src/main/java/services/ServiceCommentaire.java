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

    @Override
    public void insert(Commentaire commentaire) {
        String req = "INSERT INTO commentaire (publication_id, client_id, description, image) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, commentaire.getPublicationId());
            ps.setInt(2, commentaire.getClientId());
            ps.setString(3, commentaire.getDescription());
            ps.setString(4, commentaire.getImage());
            ps.executeUpdate();
            System.out.println("Commentaire added successfully!");
        } catch (SQLException e) {
            System.out.println("Error inserting commentaire: " + e.getMessage());
        }
    }

    @Override
    public void update(Commentaire commentaire) {
        String req = "UPDATE commentaire SET publication_id=?, client_id=?, description=?, image=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, commentaire.getPublicationId());
            ps.setInt(2, commentaire.getClientId());
            ps.setString(3, commentaire.getDescription());
            ps.setString(4, commentaire.getImage());
            ps.setInt(5, commentaire.getId());
            ps.executeUpdate();
            System.out.println("Commentaire updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating commentaire: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM commentaire WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Commentaire deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting commentaire: " + e.getMessage());
        }
    }

    @Override
    public List<Commentaire> getAll() {
        List<Commentaire> list = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                list.add(new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("publication_id"),
                        rs.getInt("client_id"),
                        rs.getString("description"),
                        rs.getString("image")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving commentaires: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Commentaire getById(int id) {
        String req = "SELECT * FROM commentaire WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
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
        } catch (SQLException e) {
            System.out.println("Error retrieving commentaire: " + e.getMessage());
        }
        return null;
    }

    public List<Commentaire> afficherCommentairesParPublication(int publicationId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE publication_id = ?";

        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, publicationId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Ensure all fields are correctly set
                Commentaire c = new Commentaire(
                        rs.getInt("publication_id"),
                        rs.getInt("client_id"),
                        rs.getString("description"),
                        rs.getString("image")
                );
                c.setId(rs.getInt("id")); // Set the ID field separately if it's not in the constructor
                commentaires.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des commentaires: " + e.getMessage());
        }

        return commentaires;
    }

}
