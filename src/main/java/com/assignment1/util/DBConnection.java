package com.assignment1.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.assignment1.App;

/**
 * Utility class for establishing a database connection.
 */
public class DBConnection {
    /**
     * Retrieves a connection to the database.
     *
     * @return A connection to the database.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {

        try (InputStream input = DBConnection.class.getResourceAsStream("/com/assignment1/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            // Establish connection using DriverManager
            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLException("Error loading database configuration", e);
        }
    }
}
