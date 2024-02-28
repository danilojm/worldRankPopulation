package com.assignment1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for establishing a database connection.
 */
public class DBConnection {
    // Database URL, username, and password
    private static final String URL = "jdbc:mysql://4.172.208.70:3306/WorldPopulationRanking";
    private static final String USER = "danilo";
    private static final String PASSWORD = "danilo";

    /**
     * Retrieves a connection to the database.
     *
     * @return A connection to the database.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        // Establish connection using DriverManager
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
