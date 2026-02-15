package com.promanage.utils;

import com.promanage.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//Test class to verify database connection
//Run this to ensure everything is working before proceeding
public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===\n");
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null) {
                System.out.println("\n=== Testing Database Query ===");
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                    "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = 'public'"
                );
                
                System.out.println("\nTables in database:");
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("table_name"));
                }
                
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM projects");
                if (rs.next()) {
                    System.out.println("\nTotal projects in database: " + rs.getInt("count"));
                }
                
                rs.close();
                stmt.close();
                
                System.out.println("\n✓ All tests passed successfully!");
            }
            
        } catch (Exception e) {
            System.err.println("\n✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
