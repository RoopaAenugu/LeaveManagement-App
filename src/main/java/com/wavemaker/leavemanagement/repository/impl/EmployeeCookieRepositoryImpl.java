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
    private static final String INSERT_INTO_COOKIE = "INSERT INTO COOKIE (COOKIE_NAME, COOKIE_VALUE, LOGIN_ID) VALUES (?, ?, ?)";
    private static final String GET_LOGIN_ID_BY_COOKIE_VALUE_QUERY = "SELECT LOGIN_ID FROM COOKIE WHERE COOKIE_VALUE = ?";
    private static final String DELETE_FROM_COOKIE_WHERE_COOKIE_VALUE = "DELETE FROM COOKIE WHERE COOKIE_VALUE = ?";

    @Override
    public void addCookie(String cookieValue, int loginId) {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_COOKIE)) {

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
        }
    }

    @Override
    public int getLoginIdByCookieValue(String cookieValue) {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LOGIN_ID_BY_COOKIE_VALUE_QUERY)) {

            preparedStatement.setString(1, cookieValue);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("LOGIN_ID");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch user ID by cookie value due to a database error.", e);

        }
        return -1;
    }

    @Override
    public void removeCookie(String cookieValue) {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_COOKIE_WHERE_COOKIE_VALUE)) {

            preparedStatement.setString(1, cookieValue);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Cookie deleted successfully for cookie value: " + cookieValue);
            } else {
                logger.warn("No cookie found with the given cookie value: " + cookieValue);
            }
        } catch (SQLException e) {
            logger.error("Failed to delete cookie due to a database error.", e);

        }

    }
}
