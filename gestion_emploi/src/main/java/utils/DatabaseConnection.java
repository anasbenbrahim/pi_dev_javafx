package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pi_dev987";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final int POOL_SIZE = 10;

    private static BlockingQueue<Connection> connectionPool;
    private static List<Connection> allConnections;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializePool();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    private static void initializePool() {
        if (connectionPool == null) {
            connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
            allConnections = new ArrayList<>();

            for (int i = 0; i < POOL_SIZE; i++) {
                try {
                    Connection conn = createNewConnection();
                    connectionPool.offer(conn);
                    allConnections.add(conn);
                } catch (SQLException e) {
                    System.err.println("Error initializing connection pool: " + e.getMessage());
                }
            }
        }
    }

    private static Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false);
        return conn;
    }

    public static Connection getInstance() throws SQLException {
        if (connectionPool == null) {
            initializePool();
        }

        try {
            Connection connection = connectionPool.take();
            if (connection.isClosed()) {
                connection = createNewConnection();
            }
            return connection;
        } catch (InterruptedException e) {
            throw new SQLException("Error getting connection from pool", e);
        }
    }

    public static void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.setAutoCommit(false);
                    connectionPool.offer(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
        }
    }

    public static void closeAllConnections() {
        if (allConnections != null) {
            for (Connection conn : allConnections) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
            allConnections.clear();
            connectionPool.clear();
        }
    }
}