package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection;
    private static Database instance;

    private final String URL = "jdbc:mysql://localhost:3306/projet_integration_final";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private Database() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Database getInstance() {
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
