package esprit.tn.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection cnx;
    private static DatabaseConnection instance;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/pi_dev200"; // Changed database name
        String username = "root";
        String password = ""; // Add your password if you have one

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            cnx = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully with pi_dev200 database");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    // Singleton pattern implementation
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

    // Optional: Method to close connection
    public void closeConnection() {
        if (cnx != null) {
            try {
                cnx.close();
                System.out.println("Connection closed");
                instance = null; // Reset the instance
            } catch (SQLException e) {
                System.err.println("Error while closing connection");
                e.printStackTrace();
            }
        }
    }
}