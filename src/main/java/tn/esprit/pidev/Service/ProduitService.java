package tn.esprit.pidev.Service;

import tn.esprit.pidev.Model.Produit;
import tn.esprit.pidev.Util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {
    private Connection connection;

    public ProduitService() {
        connection = DatabaseConnection.getConnection();
    }

    public void addProduit(Produit produit) {
        String query = "INSERT INTO produit (user_id, category_id, nomprod, image, prix, quantite, descr, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Gérer user_id NULL
            if (produit.getUserId() == 0) { // Si 0 est votre valeur par défaut
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, produit.getUserId());
            }

            statement.setInt(2, produit.getCategoryId());
            statement.setString(3, produit.getNomprod());
            statement.setString(4, produit.getImage());
            statement.setDouble(5, produit.getPrix());
            statement.setInt(6, produit.getQuantite());
            statement.setString(7, produit.getDescr());
            statement.setInt(8, produit.getStatus());
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                produit.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Produit produit = new Produit(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("nomprod"),
                        resultSet.getString("image"),
                        resultSet.getDouble("prix"),
                        resultSet.getInt("quantite"),
                        resultSet.getString("descr"),
                        resultSet.getInt("status")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des produits: " + e.getMessage());
        }
        return produits;
    }

    public void updateProduit(Produit produit) {
        String query = "UPDATE produit SET user_id = ?, category_id = ?, nomprod = ?, image = ?, " +
                "prix = ?, quantite = ?, descr = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, produit.getUserId());
            statement.setInt(2, produit.getCategoryId());
            statement.setString(3, produit.getNomprod());
            statement.setString(4, produit.getImage());
            statement.setDouble(5, produit.getPrix());
            statement.setInt(6, produit.getQuantite());
            statement.setString(7, produit.getDescr());
            statement.setInt(8, produit.getStatus());
            statement.setInt(9, produit.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du produit: " + e.getMessage());
        }
    }

    public void deleteProduit(int id) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // First delete from order_item
            String deleteOrderItems = "DELETE FROM order_item WHERE produit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteOrderItems)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }

            // Then delete the product
            String deleteProduit = "DELETE FROM produit WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteProduit)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Produit getProduitById(int id) {
        String query = "SELECT * FROM produit WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Produit(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("nomprod"),
                        resultSet.getString("image"),
                        resultSet.getDouble("prix"),
                        resultSet.getInt("quantite"),
                        resultSet.getString("descr"),
                        resultSet.getInt("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération du produit: " + e.getMessage());
        }
        return null;
    }

    public List<Produit> getProduitsByCategory(int categoryId) {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit WHERE category_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Produit produit = new Produit(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("nomprod"),
                        resultSet.getString("image"),
                        resultSet.getDouble("prix"),
                        resultSet.getInt("quantite"),
                        resultSet.getString("descr"),
                        resultSet.getInt("status")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des produits par catégorie: " + e.getMessage());
        }
        return produits;
    }
}