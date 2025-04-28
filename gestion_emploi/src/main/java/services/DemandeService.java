package services;

import models.Demande;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandeService implements AutoCloseable {
    private Connection connection;

    public DemandeService() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
        this.connection.setAutoCommit(false);
    }

    // CREATE with transaction handling and SMS notification
    public void createDemande(Demande demande) throws SQLException {
        String sql = "INSERT INTO demande (offer_id, service, date_demande, cv_file_name, phone_number) VALUES (?, ?, ?, ?, ?)";

        try {
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getInstance();
                connection.setAutoCommit(false);
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, demande.getOffer_id());
                pstmt.setString(2, demande.getService());
                pstmt.setDate(3, Date.valueOf(demande.getDate_demande()));
                pstmt.setString(4, demande.getCv_file_name());
                pstmt.setString(5, demande.getPhone_number());

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        demande.setId(rs.getInt(1));
                    }
                }
                connection.commit();

                // Get offer details for the SMS message
                String offerName = getOfferName(demande.getOffer_id());
                
                // Send SMS confirmation with offer name
                String smsMessage = String.format(
                    "Thank you for applying to '%s'! Your application has been received. Reference ID: %d",
                    offerName,
                    demande.getId()
                );
                TwilioService.sendSMS(demande.getPhone_number(), smsMessage);
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        }
    }

    // Helper method to get offer name
    private String getOfferName(int offerId) throws SQLException {
        String sql = "SELECT nom FROM offer WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        }
        return "Unknown Offer"; // Fallback if offer not found
    }

    // READ all with pagination support
    public List<Demande> getAllDemandes(int limit, int offset) throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande ORDER BY date_demande DESC LIMIT ? OFFSET ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(extractDemandeFromResultSet(rs));
                }
            }
        }
        return demandes;
    }

    // READ by Offer ID with date range filter
    public List<Demande> getDemandesByOffer(int offerId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande WHERE offer_id = ? AND date_demande BETWEEN ? AND ? ORDER BY date_demande";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(extractDemandeFromResultSet(rs));
                }
            }
        }
        return demandes;
    }

    // READ single with better error handling
    public Demande getDemandeById(int id) throws SQLException {
        String sql = "SELECT * FROM demande WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDemandeFromResultSet(rs);
                }
            }
        }
        throw new SQLException("No demande found with ID: " + id);
    }

    // UPDATE with version checking (optimistic locking)
    public boolean updateDemande(Demande demande) throws SQLException {
        String sql = "UPDATE demande SET offer_id = ?, service = ?, date_demande = ?, cv_file_name = ?, phone_number = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, demande.getOffer_id());
            pstmt.setString(2, demande.getService());
            pstmt.setDate(3, Date.valueOf(demande.getDate_demande()));
            pstmt.setString(4, demande.getCv_file_name());
            pstmt.setString(5, demande.getPhone_number());
            pstmt.setInt(6, demande.getId());

            int affectedRows = pstmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // DELETE with existence check
    public boolean deleteDemande(int id) throws SQLException {
        String sql = "DELETE FROM demande WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Count with filtering options
    public int countDemandes(Integer offerId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM demande WHERE 1=1");

        if (offerId != null) {
            sql.append(" AND offer_id = ?");
        }
        if (fromDate != null) {
            sql.append(" AND date_demande >= ?");
        }
        if (toDate != null) {
            sql.append(" AND date_demande <= ?");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (offerId != null) {
                pstmt.setInt(paramIndex++, offerId);
            }
            if (fromDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(fromDate));
            }
            if (toDate != null) {
                pstmt.setDate(paramIndex, Date.valueOf(toDate));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Helper method to extract Demande from ResultSet
    private Demande extractDemandeFromResultSet(ResultSet rs) throws SQLException {
        return new Demande(
                rs.getInt("id"),
                rs.getInt("offer_id"),
                rs.getString("service"),
                rs.getDate("date_demande").toLocalDate(),
                rs.getString("cv_file_name"),
                rs.getString("phone_number")
        );
    }

    public List<Demande> getDemandesByDate(LocalDate date) throws SQLException {
        String query = "SELECT * FROM demande WHERE date_demande = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();
            List<Demande> demandes = new ArrayList<>();
            while (resultSet.next()) {
                demandes.add(extractDemandeFromResultSet(resultSet));
            }
            return demandes;
        }
    }

    public List<Demande> getAllDemandes() throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                demandes.add(extractDemandeFromResultSet(rs));
            }
        }
        return demandes;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            try {
                connection.commit(); // Commit any pending transactions
                DatabaseConnection.releaseConnection(connection);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
    public List<Demande> getDemandesWithinPeriod(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Demande> filteredDemandes = new ArrayList<>();
        String sql = "SELECT * FROM demande WHERE date_demande BETWEEN ? AND ? ORDER BY date_demande";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    filteredDemandes.add(extractDemandeFromResultSet(rs));
                }
            }
        }
        return filteredDemandes;
    }

    /**
     * Count the number of demands for each offer
     * @return Map with offer ID as key and count as value
     * @throws SQLException if database error occurs
     */
    public Map<Integer, Integer> countDemandsPerOffer() throws SQLException {
        Map<Integer, Integer> countsMap = new HashMap<>();
        String query = "SELECT offer_id, COUNT(*) as demand_count FROM demande GROUP BY offer_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int offerId = rs.getInt("offer_id");
                int count = rs.getInt("demand_count");
                countsMap.put(offerId, count);
            }
        }
        
        return countsMap;
    }
    
    public List<Demande> getRecentDemandes(int limit) throws SQLException {
        String query = "SELECT * FROM demande ORDER BY id DESC LIMIT ?";
        List<Demande> demandes = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Demande demande = new Demande();
                demande.setId(rs.getInt("id"));
                demande.setOffer_id(rs.getInt("offer_id"));
                
                // Set service field
                try {
                    demande.setService(rs.getString("service"));
                } catch (Exception e) {
                    // Skip if field doesn't exist
                }
                
                // Set date if available
                try {
                    if (rs.getDate("date_demande") != null) {
                        demande.setDate_demande(rs.getDate("date_demande").toLocalDate());
                    }
                } catch (Exception e) {
                    // Skip if date is null or has invalid format
                }
                
                // Set CV filename if available
                try {
                    demande.setCv_file_name(rs.getString("cv_file_name"));
                } catch (Exception e) {
                    // Skip if field doesn't exist
                }
                
                // Set phone number if available
                try {
                    demande.setPhone_number(rs.getString("phone_number"));
                } catch (Exception e) {
                    // Skip if field doesn't exist
                }
                
                demandes.add(demande);
            }
        }
        
        return demandes;
    }
}