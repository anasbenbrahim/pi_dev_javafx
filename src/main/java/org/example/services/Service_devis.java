package org.example.services;

import org.example.models.Devis;
import org.example.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Service_devis implements Service<Devis>{

    private Connection connection = Database.getInstance().getConnection();


    @Override
    public void ajouter(Devis devis) {
        String req;
        req="insert into devis (equipement_id,fermier_id,fournisseur_id,proposition,quantite) values (?,?,?,?,?)";
        try{
            PreparedStatement ps=connection.prepareStatement(req);
            ps.setInt(1,devis.getEquipement_id());
            ps.setInt(2,1);
            ps.setInt(3,2);
            ps.setString(4, devis.getProposition());
            ps.setInt(5,devis.getQuantite());
            ps.executeUpdate();
            System.out.println("devis ajoutee avec succes");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(Devis devis) {
        String req="Delete from devis where id=?";
        try{
            PreparedStatement ps= connection.prepareStatement(req);
            ps.setInt(1,devis.getId());
            ps.executeUpdate();
            System.out.println("devis supprime avec succes");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modifier(Devis devis) {
        String req = "update devis set proposition=?,quantite=? where id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1,devis.getProposition());
            ps.setInt(2,devis.getQuantite());
            ps.setInt(3,devis.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public List<Devis> afficher() {
        String req= "SELECT * from devis";
        List<Devis> listeDevis = new ArrayList<>();
        try{
            PreparedStatement ps= connection.prepareStatement(req);
            //ps.setInt(1,);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Devis devis=new Devis();
                devis.setId(rs.getInt("id"));
                devis.setProposition(rs.getString("proposition"));
                devis.setFermier_id(rs.getInt("fermier_id"));
                devis.setFournisseur_id(rs.getInt("fournisseur_id"));
                devis.setEquipement_id(rs.getInt("equipement_id"));
                devis.setQuantite(rs.getInt("quantite"));
                listeDevis.add(devis);
            }
            return listeDevis;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Devis recherhche(int t) {
        String req = "SELECT * from devis where id=?";
        try{
            PreparedStatement ps= connection.prepareStatement(req);
            ps.setInt(1,t);
            Devis devis=new Devis();
            ResultSet rs = ps.executeQuery();
            devis.setId(rs.getInt("id"));
            devis.setProposition(rs.getString("proposition"));
            devis.setFermier_id(rs.getInt("fermier_id"));
            devis.setFournisseur_id(rs.getInt("fournisseur_id"));
            devis.setEquipement_id(rs.getInt("equipement_id"));
            devis.setQuantite(rs.getInt("quantite"));
            return devis;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
