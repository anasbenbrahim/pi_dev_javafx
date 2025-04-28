package esprit.tn.services;

import esprit.tn.entities.Reservation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IReservation {

    private final Connection connection;

    public ReservationService(Connection connection) {
        this.connection = connection;
    }

    public ReservationService() throws SQLException {
        try {
            // Connexion SQLite par défaut
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:reservation.db");
            // Create table if it doesn't exist
            String createTable = "CREATE TABLE IF NOT EXISTS reservation (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nom TEXT, " +
                    "prenom TEXT, " +
                    "date DATE, " +
                    "event_id INTEGER, " +
                    "event TEXT" +
                    ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTable);
            }
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new SQLException("Unable to connect to SQLite database", e);
        }
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (nom, prenom, date, event_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reservation.getNom());
            statement.setString(2, reservation.getPrenom());
            statement.setDate(3, reservation.getDate());
            statement.setInt(4, reservation.getEventId());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Reservation reservation) throws SQLException {
        String query = "UPDATE reservation SET nom = ?, prenom = ?, date = ?, event_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, reservation.getNom());
            statement.setString(2, reservation.getPrenom());
            statement.setDate(3, reservation.getDate());
            statement.setInt(4, reservation.getEventId());
            statement.setInt(5, reservation.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<Reservation> getall() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";

        try {
            // Ensure connection is valid
            if (connection == null || connection.isClosed()) {
                System.err.println("Database connection is closed or null!");
                return reservations;
            }

            // Create statement and execute query
            Statement statement = connection.createStatement();
            System.out.println("Executing query: " + query);
            ResultSet resultSet = statement.executeQuery(query);

            // Process results
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                Date date = resultSet.getDate("date");
                int eventId = resultSet.getInt("event_id");

                System.out.println("Found reservation: ID=" + id + ", Name=" + nom +
                        ", Date=" + date + ", EventID=" + eventId);

                Reservation reservation = new Reservation(id, nom, prenom, date, eventId);
                reservations.add(reservation);
            }

            System.out.println("Successfully retrieved " + reservations.size() + " reservations");

        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    @Override
    public Reservation getOne(int id) throws SQLException {
        String query = "SELECT id, nom, prenom, date, event_id FROM reservation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Reservation(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("prenom"),
                            resultSet.getDate("date"),
                            resultSet.getInt("event_id")
                    );
                }
            }
        }
        return null;
    }

    public List<LocalDate> getReservationsForMonth(LocalDate date) {
        List<LocalDate> dates = new ArrayList<>();
        int year = date.getYear();
        int month = date.getMonthValue();

        // This query works for SQLite
        String query = "SELECT date FROM reservation WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(year));
            stmt.setString(2, String.format("%02d", month));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date sqlDate = rs.getDate("date");
                if (sqlDate != null) {
                    dates.add(sqlDate.toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return dates;
    }
}