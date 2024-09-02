package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.repository.LoginCredentialRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class LoginCredentialRepositoryImpl implements LoginCredentialRepository {
    private static final Logger logger = LoggerFactory.getLogger(LoginCredentialRepositoryImpl.class);

    @Override
    public int isValidate(LoginCredential loginCredential) {
        String query = "SELECT * FROM LOGIN_CREDENTIALS WHERE EMAILID=? AND PASSWORD=?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query))  {

            preparedStatement.setString(1, loginCredential.getEmailId());
            preparedStatement.setString(2, loginCredential.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("LOGIN_ID");
            }

        } catch (SQLException e) {
            logger.debug("Error validating user", e);
        }

        return -1;
    }

    @Override
    public LoginCredential addEmployeeLogin(LoginCredential loginCredential) {
        String query = "INSERT INTO LOGIN_CREDENTIALS(EMAILID,PASSWORD) VALUES(?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, loginCredential.getEmailId());
            preparedStatement.setString(2, loginCredential.getPassword());
            preparedStatement.executeUpdate();
            return loginCredential;  // Return the added user object

        } catch (SQLException e) {
            logger.debug("Error adding user", e);

        }
        return null;  // Return null if user addition fails
    }
}
