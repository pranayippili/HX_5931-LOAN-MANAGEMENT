package com.hexaware.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.hexaware.dao.ServiceProvider;


public class DBUtil implements ServiceProvider {
	private static final String JDBC_URL = "jdbc:mysql://localhost/assesment";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "9949848621";

    static {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load JDBC driver");
        }
    }
    
    @Override
    public Connection getConnection() {
        try {
            // Create a JDBC connection
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to obtain a JDBC connection");
        }
    }
}