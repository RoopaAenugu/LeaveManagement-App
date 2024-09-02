package com.wavemaker.leavemanagement.util;


import com.mysql.cj.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    //constants
    private static final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private final static String DB_URL = "jdbc:mysql://127.0.0.1:3306/LEAVEMANAGEMENT";
    private final static String DB_USERNAME = "root";
    private final static String DB_PASSWORD = "Roopa#77";
    private static volatile Connection connection;

    private DbConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new Driver());
        connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        return connection;
    }
}

