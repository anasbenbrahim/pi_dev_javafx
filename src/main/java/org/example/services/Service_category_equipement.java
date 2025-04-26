package org.example.services;

import org.example.models.Category_equipement;
import org.example.models.Equipements;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_category_equipement implements Service<Category_equipement> {

    private Connection connection = Database.getInstance().getConnection();

    @Override
    public void ajouter(Category_equipement category) {

        String req = "INSERT INTO category_equipements (type) VALUES (?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, category.getType());
            pst.executeUpdate();
            System.out.println("Categorie ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Category_equipement category) {
        String req = " DELETE FROM category_equipements WHERE id = ? ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, category.getId());
            pst.executeUpdate();
            System.out.println("Categorie Supprime");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Category_equipement categoryEquipement) {
        String req = "UPDATE category_equipements SET type = ? WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categoryEquipement.getType());
            pst.setInt(2, categoryEquipement.getId());
            pst.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Category_equipement> afficher() {
        ArrayList<Category_equipement> list = new ArrayList<>();
        String req = "SELECT type FROM category_equipements";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Category_equipement(rs.getString("type")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public Category_equipement recherhche(int id) {
        String req = "SELECT * FROM category_equipements WHERE id = ?";
        Category_equipement categorie = new Category_equipement();

        try{
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                categorie.setId(rs.getInt("id"));
                categorie.setType(rs.getString("type"));
                System.out.println("Catégorie trouvée");
                return categorie;
            } else {
                System.out.println("Aucune catégorie trouvée avec l'id : " + id);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de la catégorie : " + e.getMessage());
            return null;
        }
    }
    public List<Category_equipement> getCategories() {
        List<Category_equipement> categories = new ArrayList<>();

        String query = "SELECT * FROM category_equipements"; // Adapté selon ton nom de table
        try {
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String type = resultSet.getString("type");
                    System.out.println("ID : " + id);
                    categories.add(new Category_equipement(id, type));
                }
            }
         catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
