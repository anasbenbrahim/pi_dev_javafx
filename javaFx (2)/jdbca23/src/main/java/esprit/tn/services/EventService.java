package esprit.tn.services;

import esprit.tn.entities.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements Iservice<Event> {

    private Connection connection; // Suppression du modificateur static

    public EventService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        String query = "INSERT INTO event (nom, descr, date, type, photo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, event.getNom());
            statement.setString(2, event.getDescr());
            statement.setDate(3, new java.sql.Date(event.getDate().getTime()));
            statement.setString(4, event.getType());
            statement.setString(5, event.getPhoto());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la création, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création, aucun ID obtenu.");
                }
            }
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String query = "UPDATE event SET nom = ?, descr = ?, date = ?, type = ?, photo = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, event.getNom());
            statement.setString(2, event.getDescr());
            statement.setDate(3, new java.sql.Date(event.getDate().getTime()));
            statement.setString(4, event.getType());
            statement.setString(5, event.getPhoto());
            statement.setInt(6, event.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException { // Suppression du modificateur static
        String query = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Event> getall() { // Suppression du modificateur static
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("descr"),
                        resultSet.getDate("date"),
                        resultSet.getString("type"),
                        resultSet.getString("photo")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event getOne(int id) {
        String query = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Event(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("descr"),
                        resultSet.getDate("date"),
                        resultSet.getString("type"),
                        resultSet.getString("photo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}