package esprit.tn.services;

import esprit.tn.entities.Reservation;
import java.sql.SQLException;
import java.util.List;

public interface IReservation {
    void ajouter(Reservation reservation) throws SQLException;
    void modifier(Reservation reservation) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<Reservation> getall();
    Reservation getOne(int id) throws SQLException;
}
