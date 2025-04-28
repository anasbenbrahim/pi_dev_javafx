package tn.esprit.pidev.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/pi_dev200";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = ""; // Set your password here
    private static Connection connection;

    // Singleton pattern: Get the connection, don't close it here
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
                System.out.println("Database connected successfully");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Database connection failed: " + e.getMessage());
                throw new SQLException("Database connection failed");
            }
        }
        return connection;
    }

    // Manually close the connection, if needed (typically after all operations)
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
