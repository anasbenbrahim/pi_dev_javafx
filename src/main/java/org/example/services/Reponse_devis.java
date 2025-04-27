package org.example.services;

import org.example.utils.Database;
import org.example.models.ReponseDevis;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Reponse_devis implements Service<ReponseDevis>{

    private Connection connection = Database.getInstance().getConnection();


    @Override
    public void ajouter(ReponseDevis reponse) {
        String req="insert into reponse_devis (devis_id,fournisseur_id,fermier_id,reponse,etat,prix) values (?,?,?,?,?,?)";
        int etat;
        if(reponse.getEtat()==true)
            etat=1;
        else
            etat=0;
        try{
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1,reponse.getDevis());
            ps.setInt(2,2);
            ps.setInt(3,1);
            ps.setString(4,reponse.getReponse());
            ps.setInt(5,etat);
            ps.setDouble(6,reponse.getPrix());
            ps.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(ReponseDevis reponseDevis) {

        String req="delete from reponse_devis where id=?";
        try{
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1,reponseDevis.getId());
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modifier(ReponseDevis reponseDevis) {
        int etat;
        if(reponseDevis.getEtat()==true)
            etat=1;
        else
            etat=0;
        String req="update reponse_devis set reponse=?, etat=? ,prix=?  where id=?";
        try{
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1,reponseDevis.getReponse());
            ps.setInt(2,etat);
            ps.setDouble(3,reponseDevis.getPrix());
            ps.setInt(4,reponseDevis.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<ReponseDevis> afficher() {
        List<ReponseDevis> list = new ArrayList<>();
        String req="select * from reponse_devis";
        try{
            PreparedStatement ps= connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            ReponseDevis reponse=new ReponseDevis();
            while(rs.next()){
                reponse.setDevis(rs.getInt("devis_id"));
                reponse.setReponse(rs.getString("reponse"));
                reponse.setPrix(rs.getDouble("prix"));
                reponse.setFermier_id(1);
                reponse.setFournisseur_id(2);
                int etat=rs.getInt("etat");
                if(etat==1)
                    reponse.setEtat(true);
                else
                    reponse.setEtat(false);
                list.add(reponse);
            }
            return list;
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public ReponseDevis recherhche(int t) {
        return null;
    }
}
