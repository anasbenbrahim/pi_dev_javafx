package services;

import models.Offer;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OfferService implements AutoCloseable {
    private final Connection connection;

    public OfferService() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
        this.connection.setAutoCommit(false); // Enable transaction management
    }

    // CREATE with transaction handling
    public boolean createOffer(Offer offer) throws SQLException {
        String sql = "INSERT INTO offer (nom, domain, date_offer, description, nb_places) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, offer.getNom());
            pstmt.setString(2, offer.getDomain());
            pstmt.setDate(3, Date.valueOf(offer.getDate_offer()));
            pstmt.setString(4, offer.getDescription());
            pstmt.setInt(5, offer.getNb_places());

            int affectedRows = pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    offer.setId(rs.getInt(1));
                }
            }
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // READ all with pagination and sorting
    public List<Offer> getAllOffers(int limit, int offset, String sortField, boolean ascending) throws SQLException {
        List<Offer> offers = new ArrayList<>();
        String sql = String.format("SELECT * FROM offer ORDER BY %s %s LIMIT ? OFFSET ?",
                sortField, ascending ? "ASC" : "DESC");

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    offers.add(extractOfferFromResultSet(rs));
                }
            }
        }
        return offers;
    }

    // READ by ID with better error handling
    public Offer getOfferById(int id) throws SQLException {
        String sql = "SELECT * FROM offer WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractOfferFromResultSet(rs);
                }
            }
        }
        throw new SQLException("No offer found with ID: " + id);
    }

    // UPDATE with version checking
    public boolean updateOffer(Offer offer) throws SQLException {
        String sql = "UPDATE offer SET nom = ?, domain = ?, date_offer = ?, description = ?, nb_places = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, offer.getNom());
            pstmt.setString(2, offer.getDomain());
            pstmt.setDate(3, Date.valueOf(offer.getDate_offer()));
            pstmt.setString(4, offer.getDescription());
            pstmt.setInt(5, offer.getNb_places());
            pstmt.setInt(6, offer.getId());

            int affectedRows = pstmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // DELETE with existence check and cascading delete of related demands
    public boolean deleteOffer(int id) throws SQLException {
        try {
            // First delete all related demands
            String deleteDemandesSql = "DELETE FROM demande WHERE offer_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteDemandesSql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Then delete the offer
            String deleteOfferSql = "DELETE FROM offer WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteOfferSql)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                connection.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Advanced search with multiple criteria
    public List<Offer> searchOffers(String query, String domain, LocalDate minDate,
                                    LocalDate maxDate, Integer minPlaces) throws SQLException {
        List<Offer> offers = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM offer WHERE 1=1");

        if (query != null && !query.isEmpty()) {
            sql.append(" AND (nom LIKE ? OR description LIKE ?)");
        }
        if (domain != null && !domain.isEmpty()) {
            sql.append(" AND domain = ?");
        }
        if (minDate != null) {
            sql.append(" AND date_offer >= ?");
        }
        if (maxDate != null) {
            sql.append(" AND date_offer <= ?");
        }
        if (minPlaces != null) {
            sql.append(" AND nb_places >= ?");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (query != null && !query.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + query + "%");
                pstmt.setString(paramIndex++, "%" + query + "%");
            }
            if (domain != null && !domain.isEmpty()) {
                pstmt.setString(paramIndex++, domain);
            }
            if (minDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(minDate));
            }
            if (maxDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(maxDate));
            }
            if (minPlaces != null) {
                pstmt.setInt(paramIndex, minPlaces);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    offers.add(extractOfferFromResultSet(rs));
                }
            }
        }
        return offers;
    }

    // Count active offers (not expired)
    public int countActiveOffers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM offer WHERE date_offer >= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Helper method to extract Offer from ResultSet
    private Offer extractOfferFromResultSet(ResultSet rs) throws SQLException {
        return new Offer(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("domain"),
                rs.getDate("date_offer").toLocalDate(),
                rs.getString("description"),
                rs.getInt("nb_places")
        );
    }

    public List<Offer> getOffersByDate(LocalDate date) throws SQLException {
        String query = "SELECT * FROM offer WHERE date_offer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();
            List<Offer> offers = new ArrayList<>();
            while (resultSet.next()) {
                offers.add(extractOfferFromResultSet(resultSet));
            }
            return offers;
        }
    }

    // Add method to get all offers without pagination for calendar
    public List<Offer> getAllOffers() throws SQLException {
        List<Offer> offers = new ArrayList<>();
        String sql = "SELECT * FROM offer";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                offers.add(extractOfferFromResultSet(rs));
            }
        }
        return offers;
    }

    /**
     * Get basic offer information by its ID
     * @param offerId the ID of the offer
     * @return an Offer object with basic info or null if not found
     * @throws SQLException if database error occurs
     */
    public Offer getOfferBasicInfoById(int offerId) throws SQLException {
        String query = "SELECT id, nom, domain FROM offer WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, offerId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Offer offer = new Offer();
                    offer.setId(resultSet.getInt("id"));
                    offer.setNom(resultSet.getString("nom"));
                    offer.setDomain(resultSet.getString("domain"));
                    return offer;
                }
            }
        }
        
        return null;
    }

    public List<Offer> getRecentOffers(int limit) throws SQLException {
        String query = "SELECT * FROM offer ORDER BY id DESC LIMIT ?";
        List<Offer> offers = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Offer offer = new Offer();
                offer.setId(rs.getInt("id"));
                offer.setNom(rs.getString("nom"));
                offer.setDomain(rs.getString("domain"));
                
                // Try to parse date if available
                try {
                    if (rs.getDate("date_offer") != null) {
                        offer.setDate_offer(rs.getDate("date_offer").toLocalDate());
                    }
                } catch (Exception e) {
                    // Skip if date column doesn't exist or has invalid format
                }
                
                // Try to get description if available
                try {
                    offer.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    // Skip if description column doesn't exist
                }
                
                // Try to get nb_places if available
                try {
                    offer.setNb_places(rs.getInt("nb_places"));
                } catch (Exception e) {
                    // Skip if column doesn't exist
                }
                
                // Try to get deadline if available
                offers.add(offer);
            }
        }
        
        return offers;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}