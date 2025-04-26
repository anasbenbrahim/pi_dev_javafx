package services;

import modele.Rating;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RatingService {
    private Connection con;

    public RatingService() {
        this.con = DataSource.getInstance().getConnection();
    }

    public void addRating(Rating rating) {
        if (con == null) {
            System.err.println("Database connection is null. Cannot add rating.");
            return;
        }
        String req = "INSERT INTO rating (publication_id, client_id, rating) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, rating.getPublicationId());
            ps.setInt(2, rating.getClientId());
            ps.setInt(3, rating.getRating());
            ps.executeUpdate();
            System.out.println("Rating added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding rating: " + e.getMessage());
        }
    }

    public void updateRating(Rating rating) {
        if (con == null) {
            System.err.println("Database connection is null. Cannot update rating.");
            return;
        }
        String req = "UPDATE rating SET rating=? WHERE publication_id=? AND client_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, rating.getRating());
            ps.setInt(2, rating.getPublicationId());
            ps.setInt(3, rating.getClientId());
            ps.executeUpdate();
            System.out.println("Rating updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating rating: " + e.getMessage());
        }
    }

    public Rating getRating(int publicationId, int clientId) {
        if (con == null) {
            System.err.println("Database connection is null. Cannot retrieve rating.");
            return null;
        }
        String req = "SELECT * FROM rating WHERE publication_id=? AND client_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, publicationId);
            ps.setInt(2, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Rating(
                            rs.getInt("id"),
                            rs.getInt("publication_id"),
                            rs.getInt("client_id"),
                            rs.getInt("rating"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving rating: " + e.getMessage());
        }
        return null;
    }

    public double getAverageRating(int publicationId) {
        if (con == null) {
            System.err.println("Database connection is null. Cannot calculate average rating.");
            return 0.0;
        }
        String req = "SELECT AVG(rating) as avg_rating FROM rating WHERE publication_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Rating> getAllRatings(int publicationId) {
        List<Rating> ratings = new ArrayList<>();
        if (con == null) {
            System.err.println("Database connection is null. Cannot retrieve ratings.");
            return ratings;
        }
        String req = "SELECT * FROM rating WHERE publication_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(new Rating(
                            rs.getInt("id"),
                            rs.getInt("publication_id"),
                            rs.getInt("client_id"),
                            rs.getInt("rating"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving ratings: " + e.getMessage());
        }
        return ratings;
    }
}