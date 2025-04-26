package org.example.services;

import org.example.models.Category_equipement;
import org.example.models.Equipements;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_equipement implements Service<Equipements>{

    private Connection connection = Database.getInstance().getConnection();


    @Override
    public void ajouter(Equipements equipement) {
        try {
            String req = "INSERT INTO equipements (user_id, nom, quantite, prix, description, image, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setInt(1, 1); // user_id en dur, à adapter selon contexte
            pst.setString(2, equipement.getNom());
            pst.setInt(3, equipement.getQuantite());
            pst.setDouble(4, equipement.getPrix());
            pst.setString(5, equipement.getDescription());
            pst.setString(6, equipement.getImage());
            pst.setInt(7, equipement.getCategory().getId()); // ajout du category_id

            pst.executeUpdate();
            System.out.println("Équipement ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Equipements equipement) {
        try {
            String req = "DELETE FROM equipements WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, equipement.getId());
            pst.executeUpdate();
            System.out.println("Équipement supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Equipements equipement) {
        String req = "UPDATE equipements SET nom = ?, quantite = ?, prix = ?, description = ?, image = ?, category_id = ? WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, equipement.getNom());
            pst.setInt(2, equipement.getQuantite());
            pst.setDouble(3, equipement.getPrix());
            pst.setString(4, equipement.getDescription());
            pst.setString(5, equipement.getImage());
            pst.setInt(6, equipement.getCategory().getId()); // MAJ catégorie
            pst.setInt(7, equipement.getId());

            pst.executeUpdate();
            System.out.println("Équipement modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Equipements> afficher() {
        ArrayList<Equipements> list = new ArrayList<>();
        String req = """
    SELECT e.*, c.id as cat_id, c.type as cat_type
    FROM equipements e
    LEFT JOIN category_equipements c ON e.category_id = c.id
""";


        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Equipements eq = new Equipements();
                eq.setId(rs.getInt("id"));
                eq.setNom(rs.getString("nom"));
                eq.setQuantite(rs.getInt("quantite"));
                eq.setPrix(rs.getDouble("prix"));
                eq.setDescription(rs.getString("description"));
                eq.setImage(rs.getString("image"));

                int catId = rs.getInt("cat_id");
                if (!rs.wasNull()) {
                    Category_equipement cat = new Category_equipement();
                    cat.setId(catId);
                    cat.setType(rs.getString("cat_type"));
                    eq.setCategory(cat);
                } else {
                    eq.setCategory(null);
                }


                list.add(eq);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Equipements> recherchePar_categorie(Category_equipement cat)  {
        String req;
        int id=cat.getId();
        List<Equipements> list = new ArrayList<>();
        try{
            req="SELECT * from Equipements where category_id = ?";
            PreparedStatement ps= connection.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipements eq = new Equipements();
                eq.setId(rs.getInt("id"));
                eq.setNom(rs.getString("nom"));
                eq.setQuantite(rs.getInt("quantite"));
                eq.setPrix(rs.getDouble("prix"));
                eq.setDescription(rs.getString("description"));
                eq.setImage(rs.getString("image"));
                eq.setCategory(cat);
                list.add(eq);
            }
            return list;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return list;
    }


    @Override
    public Equipements recherhche(int id) {
        String req = "SELECT * FROM equipements WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Equipements e = new Equipements();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setQuantite(rs.getInt("quantite"));
                e.setPrix(rs.getDouble("prix"));
                e.setDescription(rs.getString("description"));
                e.setImage(rs.getString("image"));

                int categoryId = rs.getInt("category_id");
                Service_category_equipement sc = new Service_category_equipement();
                Category_equipement cat = sc.recherhche(categoryId);
                e.setCategory(cat);

                System.out.println("Équipement trouvé");
                return e;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //return equipement;
}

