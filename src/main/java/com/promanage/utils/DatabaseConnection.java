package com.promanage.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//  Utility class to manage PostgreSQL database connections
//  Implements Singleton pattern to ensure single connection instance

public class DatabaseConnection {

    // Database credentials - UPDATE THESE WITH YOUR DETAILS
    private static final String URL = "jdbc:postgresql://localhost:5432/projectscheduledb";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "toor";  // UPDATE THIS

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

//  Get database connection
//  Creates new connection if none exists, returns existing connection otherwise
//  @return Connection object
//  @throws SQLException if connection fails

    public static Connection getConnection() throws SQLException {
        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            // Check if connection is null or closed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✓ Database connection established successfully!");
            }

            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("✗ PostgreSQL JDBC Driver not found!");
            throw new SQLException("Driver not found", e);

        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

//  Close database connection
//  Should be called when application exits

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing database connection: " + e.getMessage());
        }
    }

//  Test database connection
//  @return true if connection successful, false otherwise

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection test successful!");
                System.out.println("  Connected to: " + URL);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed!");
            return false;
        }
        return false;
    }
}