package com.wavemaker.leavemanagement.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wavemaker.leavemanagement.repository.EmployeeNumberOfLeavesRepository;
import com.wavemaker.leavemanagement.util.DbConnection;

public class EmployeeNumberOfLeavesRepositoryImpl implements EmployeeNumberOfLeavesRepository {

    // Corrected SQL query to match the updated schema
    private static final String COUNT_APPROVED_LEAVES_QUERY =
            "SELECT SUM(DATEDIFF(TO_DATE, FROM_DATE) + 1) AS total_leaves " +
                    "FROM LEAVE_REQUESTS " +
                    "WHERE EMPLOYEE_ID = ? AND STATUS = 'APPROVED' " +
                    "AND FROM_DATE >= '2024-04-01' " +
                    "AND TO_DATE <= '2025-03-31'";

    @Override
    public int getNoOfLeaves(int employeeId) {
        int totalLeaves = 0;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_APPROVED_LEAVES_QUERY)) {

            preparedStatement.setInt(1, employeeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    totalLeaves = resultSet.getInt("total_leaves");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return totalLeaves;
    }
}
