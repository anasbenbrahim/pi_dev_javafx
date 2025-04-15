package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private final String URL = "jdbc:mysql://localhost:3306/gestpub";
    private final String USER = "root";
    private final String PASSWORD = "";

    private Connection con;
    private static DataSource instance;

    // Private constructor for Singleton
    private DataSource() {
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish Connection
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
    }

    // Singleton getInstance method
    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    // Method to get Connection
    public Connection getConnection() {
        return con;
    }
}
