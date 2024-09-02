package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.repository.EmployeeCookieRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeCookieRepositoryImpl implements EmployeeCookieRepository {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeCookieRepositoryImpl.class);

    @Override
    public void addCookie(String cookieValue, int loginId) {
        String insertCookieSQL = "INSERT INTO COOKIE (COOKIE_NAME, COOKIE_VALUE, LOGIN_ID) VALUES (?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertCookieSQL)) {

            String cookieName = "auth_cookie";
            preparedStatement.setString(1, cookieName);
            preparedStatement.setString(2, cookieValue);
            preparedStatement.setInt(3, loginId);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Cookie added successfully for user ID " + loginId);
            }
        } catch (SQLException e) {
            logger.error("Failed to add cookie due to a database error.", e);
            // Handle the error as needed
        }
    }

    @Override
    public int getLoginIdByCookieValue(String cookieValue) {
        String query = "SELECT LOGIN_ID FROM COOKIE WHERE COOKIE_VALUE = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cookieValue);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("LOGIN_ID");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch user ID by cookie value due to a database error.", e);
            // Handle the error as needed
        }
        return -1; // Return -1 or another sentinel value indicating no user ID found
    }

    @Override
    public void removeCookie(String cookieValue) {
        String deleteCookieSQL = "DELETE FROM COOKIE WHERE COOKIE_VALUE = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteCookieSQL)) {

            preparedStatement.setString(1, cookieValue);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Cookie deleted successfully for cookie value: " + cookieValue);
            } else {
                logger.warn("No cookie found with the given cookie value: " + cookieValue);
            }
        } catch (SQLException e) {
            logger.error("Failed to delete cookie due to a database error.", e);
            // Handle the error as needed
        }

    }
}
