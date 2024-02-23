package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://4.172.208.70:3306/WorldPopulationRanking";
    private static final String USER = "danilo";
    private static final String PASSWORD = "danilo";

    // Method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Establish connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database: " + e.getMessage());
        }
    }

    // Method to close the database connection
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

}
