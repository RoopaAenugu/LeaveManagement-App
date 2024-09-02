package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class EmployeeLeaveSummaryRepositoryImpl implements EmployeeLeaveSummaryRepository {

    // Initialize the logger
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryRepositoryImpl.class);

   /* private static final String SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    ELS.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME AS LEAVE_TYPE_NAME, " +
                    "    MAX(ELS.PENDING_LEAVES) AS PENDING_LEAVES, " +
                    "    SUM(ELS.TOTAL_LEAVES_TAKEN) AS TOTAL_LEAVES_TAKEN, " +
                    "    LT.LIMIT_FOR_LEAVES AS ALLOCATED_LEAVES " +
                    "FROM " +
                    "    EMPLOYEE_LEAVE_SUMMARY ELS " +
                    "JOIN " +
                    "    LEAVE_TYPES LT ON ELS.LEAVE_TYPE_ID = LT.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    ELS.EMPLOYEE_ID = ? " +
                    "GROUP BY " +
                    "    ELS.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME, " +
                    "    LT.LIMIT_FOR_LEAVES;";*/

    /*private static final String SELECT_TEAM_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    ELS.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME AS LEAVE_TYPE_NAME, " +
                    "    MAX(ELS.PENDING_LEAVES) AS PENDING_LEAVES, " +
                    "    SUM(ELS.TOTAL_LEAVES_TAKEN) AS TOTAL_LEAVES_TAKEN, " +
                    "    LT.LIMIT_FOR_LEAVES AS ALLOCATED_LEAVES " +
                    "FROM " +
                    "    EMPLOYEE_LEAVE_SUMMARY ELS " +
                    "JOIN " +
                    "    LEAVE_TYPES LT ON ELS.LEAVE_TYPE_ID = LT.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    ELS.EMPLOYEE_ID IN (%s) " +
                    "GROUP BY " +
                    "    ELS.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME, " +
                    "    LT.LIMIT_FOR_LEAVES;";*/

    private static final String INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "INSERT INTO EMPLOYEE_LEAVE_SUMMARY (EMPLOYEE_ID, LEAVE_TYPE_ID, PENDING_LEAVES, TOTAL_LEAVES_TAKEN, LEAVE_TYPE) " +
                    "VALUES (?, ?, ?, ?, ?);";

    private static final String UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "UPDATE EMPLOYEE_LEAVE_SUMMARY els\n" +
                    "JOIN (\n" +
                    "    SELECT lr.EMPLOYEE_ID, lr.LEAVE_TYPE_ID, \n" +
                    "           SUM(DATEDIFF(lr.TO_DATE, lr.FROM_DATE) + 1) AS TOTAL_LEAVES_TAKEN \n" +
                    "    FROM LEAVE_REQUEST lr \n" +
                    "    WHERE lr.STATUS = 'APPROVED' AND lr.EMPLOYEE_ID = ? \n" +
                    "    GROUP BY lr.EMPLOYEE_ID, lr.LEAVE_TYPE_ID\n" +
                    ") t ON els.EMPLOYEE_ID = t.EMPLOYEE_ID \n" +
                    "   AND els.LEAVE_TYPE_ID = t.LEAVE_TYPE_ID\n" +
                    "JOIN LEAVE_TYPES lt ON lt.LEAVE_TYPE_ID = els.LEAVE_TYPE_ID\n" +
                    "SET els.TOTAL_LEAVES_TAKEN = t.TOTAL_LEAVES_TAKEN, \n" +
                    "    els.PENDING_LEAVES = lt.LIMIT_FOR_LEAVES - t.TOTAL_LEAVES_TAKEN\n";

    private static final String CHECK_EMPLOYEE_LEAVE_SUMMARY_EXIST_QUERY =
            "SELECT COUNT(*) FROM EMPLOYEE_LEAVE_SUMMARY WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?;";



    private static final String SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    ELS.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME AS LEAVE_TYPE_NAME, " +
                    "    COALESCE(MAX(ELS.PENDING_LEAVES), 0) AS PENDING_LEAVES, " +
                    "    COALESCE(SUM(ELS.TOTAL_LEAVES_TAKEN), 0) AS TOTAL_LEAVES_TAKEN, " +
                    "    COALESCE(LT.LIMIT_FOR_LEAVES, 0) AS ALLOCATED_LEAVES " +
                    "FROM " +
                    "    EMPLOYEES E " +  // Assuming EMPLOYEE is the table for all employees
                    "LEFT JOIN " +
                    "    EMPLOYEE_LEAVE_SUMMARY ELS ON E.EMPLOYEE_ID = ELS.EMPLOYEE_ID " +
                    "LEFT JOIN " +
                    "    LEAVE_TYPES LT ON ELS.LEAVE_TYPE_ID = LT.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    E.EMPLOYEE_ID = ? " +
                    "GROUP BY " +
                    "    E.EMPLOYEE_ID, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME, " +
                    "    LT.LIMIT_FOR_LEAVES;";

    private static final String SELECT_TEAM_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    E.EMPLOYEE_ID, " +
                    "    E.NAME AS EMPLOYEE_NAME, " + // Add employee name
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME AS LEAVE_TYPE_NAME, " +
                    "    COALESCE(MAX(ELS.PENDING_LEAVES), 0) AS PENDING_LEAVES, " +
                    "    COALESCE(SUM(ELS.TOTAL_LEAVES_TAKEN), 0) AS TOTAL_LEAVES_TAKEN, " +
                    "    COALESCE(LT.LIMIT_FOR_LEAVES, 0) AS ALLOCATED_LEAVES " +
                    "FROM " +
                    "    EMPLOYEES E " +
                    "LEFT JOIN " +
                    "    EMPLOYEE_LEAVE_SUMMARY ELS ON E.EMPLOYEE_ID = ELS.EMPLOYEE_ID " +
                    "LEFT JOIN " +
                    "    LEAVE_TYPES LT ON ELS.LEAVE_TYPE_ID = LT.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    E.EMPLOYEE_ID IN %s " +
                    "GROUP BY " +
                    "    E.EMPLOYEE_ID, " +
                    "    E.NAME, " + // Group by employee name
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME, " +
                    "    LT.LIMIT_FOR_LEAVES;";


    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException {
        List<EmployeeLeaveSummary> leaveSummaryList = new ArrayList<>();
        logger.debug("Fetching leave summary for Employee ID: {}", employeeId);
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {

            preparedStatement.setInt(1, employeeId);
            logger.trace("Executing query: {}", SELECT_EMPLOYEE_LEAVE_SUMMARY_QUERY);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    EmployeeLeaveSummary summary = new EmployeeLeaveSummary();

                    summary.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
                    summary.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                    summary.setLeaveType(rs.getString("LEAVE_TYPE_NAME"));
                    summary.setPendingLeaves(rs.getInt("PENDING_LEAVES"));
                    summary.setTotalLeavesTaken(rs.getInt("TOTAL_LEAVES_TAKEN"));
                    summary.setTotalAllocatedLeaves(rs.getInt("ALLOCATED_LEAVES"));

                    leaveSummaryList.add(summary);
                }
                logger.debug("Retrieved {} leave summaries for Employee ID: {}", leaveSummaryList.size(), employeeId);
            } catch (SQLException e) {
                logger.error("SQL exception while fetching leave summary for Employee ID: {}", employeeId, e);
                throw new ServerUnavailableException("Server is unavailable to fetch employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (SQLException e) {
            logger.error("Database connection error while fetching leave summary for Employee ID: {}", employeeId, e);
            throw new ServerUnavailableException("Server is unavailable to fetch employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveSummaryList;
    }
    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException {
        List<EmployeeLeaveSummary> leaveSummaryList = new ArrayList<>();
        if (employeeIds == null || employeeIds.isEmpty()) {
            throw new IllegalArgumentException("Employee IDs list cannot be null or empty");
        }

        // Construct the IN clause
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (int i = 0; i < employeeIds.size(); i++) {
            sj.add("?");
        }
        String inClause = sj.toString();

        String query = SELECT_TEAM_LEAVE_SUMMARY_QUERY.replace("%s", inClause);

        logger.debug("Fetching leave summaries for Employee IDs: {}", employeeIds);
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the IN clause
            for (int i = 0; i < employeeIds.size(); i++) {
                preparedStatement.setInt(i + 1, employeeIds.get(i));
            }

            logger.trace("Executing query: {}", query);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    EmployeeLeaveSummary summary = new EmployeeLeaveSummary();

                    summary.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
                    summary.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                    summary.setLeaveType(rs.getString("LEAVE_TYPE_NAME"));
                    summary.setPendingLeaves(rs.getInt("PENDING_LEAVES"));
                    summary.setTotalLeavesTaken(rs.getInt("TOTAL_LEAVES_TAKEN"));
                    summary.setTotalAllocatedLeaves(rs.getInt("ALLOCATED_LEAVES"));
                    summary.setEmpName(rs.getString("EMPLOYEE_NAME"));

                    leaveSummaryList.add(summary);
                }
                logger.debug("Retrieved {} leave summaries for employee IDs: {}", leaveSummaryList.size(), employeeIds);
            } catch (SQLException e) {
                logger.error("SQL exception while fetching leave summaries for employee IDs: {}", employeeIds, e);
                throw new ServerUnavailableException("Server is unavailable to fetch leave summaries for employees", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (SQLException e) {
            logger.error("Database connection error while fetching leave summaries for employee IDs: {}", employeeIds, e);
            throw new ServerUnavailableException("Server is unavailable to fetch leave summaries for employees", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveSummaryList;
    }

    @Override
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        logger.debug("Adding/updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
        try (Connection connection = DbConnection.getConnection()) {

            // Check if the record exists
            boolean exists = checkIfEmployeeLeaveSummaryExists(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), connection);

            if (exists) {
                logger.debug("Record exists. Updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                // Update the record
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {

                    preparedStatement.setInt(1, employeeLeaveSummary.getEmployeeId()); // Employee ID for the LEAVE_REQUEST subquery

                    logger.trace("Executing update query: {}", UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        logger.debug("Successfully updated leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                        return employeeLeaveSummary;  // Successfully updated
                    } else {
                        logger.error("Failed to update leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                        throw new ServerUnavailableException("Failed to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }

                }
            } else {
                logger.debug("Record does not exist. Inserting new leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                // Insert the record
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {

                    int totalLeavesTaken = getTotalLeavesTaken(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), connection);
                    int pendingLeaves = employeeLeaveSummary.getTotalAllocatedLeaves() - totalLeavesTaken;

                    preparedStatement.setInt(1, employeeLeaveSummary.getEmployeeId());
                    preparedStatement.setInt(2, employeeLeaveSummary.getLeaveTypeId());
                    preparedStatement.setInt(3, pendingLeaves);
                    preparedStatement.setInt(4, totalLeavesTaken);
                    preparedStatement.setString(5, employeeLeaveSummary.getLeaveType());

                    logger.trace("Executing insert query: {}", INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        logger.debug("Successfully inserted leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                        return employeeLeaveSummary;  // Successfully inserted
                    } else {
                        logger.error("Failed to insert leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                        throw new ServerUnavailableException("Failed to insert employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }

                }
            }

        } catch (SQLException e) {
            logger.error("Database connection error while adding/updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
            throw new ServerUnavailableException("Server is unavailable to update or insert employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY )) {
            preparedStatement.setInt(1, employeeLeaveSummary.getEmployeeId());
            logger.trace("Executing update query: {}", UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.debug("Successfully updated leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                return true;  // Successfully updated
            } else {
                logger.error("Failed to update leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                throw new ServerUnavailableException("Failed to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (SQLException e) {
            logger.error("Database connection error while updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
            throw new ServerUnavailableException("Server is unavailable to update or insert employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkIfEmployeeLeaveSummaryExists(int employeeId, int leaveTypeId, Connection connection) throws SQLException {
        logger.debug("Checking existence of leave summary for Employee ID: {}, Leave Type ID: {}", employeeId, leaveTypeId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_EMPLOYEE_LEAVE_SUMMARY_EXIST_QUERY)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, leaveTypeId);

            logger.trace("Executing existence check query: {}", CHECK_EMPLOYEE_LEAVE_SUMMARY_EXIST_QUERY);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // Get the count from the result set and check if it's greater than 0
                    int count = rs.getInt(1);
                    logger.debug("Employee ID: {}, Leave Type ID: {}, Count: {}", employeeId, leaveTypeId, count);
                    return count > 0;
                } else {
                    logger.debug("No result returned for Employee ID: {}, Leave Type ID: {}", employeeId, leaveTypeId);
                    return false; // No rows found
                }
            }
        }
    }

    private int getTotalLeavesTaken(int employeeId, int leaveTypeId, Connection connection) throws SQLException {
        String totalLeavesTakenQuery =
                "SELECT SUM(DATEDIFF(lr.TO_DATE, lr.FROM_DATE) + 1) AS TOTAL_LEAVES_TAKEN " +
                        "FROM LEAVE_REQUEST lr " +
                        "WHERE lr.EMPLOYEE_ID = ? AND lr.LEAVE_TYPE_ID = ? AND lr.STATUS = 'APPROVED';";

        logger.debug("Calculating total leaves taken for Employee ID: {}, Leave Type ID: {}", employeeId, leaveTypeId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(totalLeavesTakenQuery)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, leaveTypeId);

            logger.trace("Executing total leaves taken query: {}", totalLeavesTakenQuery);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int totalLeavesTaken = rs.getInt("TOTAL_LEAVES_TAKEN");
                    logger.debug("Total Leaves Taken for Employee ID: {}, Leave Type ID: {} is {}", employeeId, leaveTypeId, totalLeavesTaken);
                    return totalLeavesTaken;
                } else {
                    logger.debug("No leaves taken for Employee ID: {}, Leave Type ID: {}", employeeId, leaveTypeId);
                    return 0;  // No leaves taken for this leave type
                }
            }
        }
    }
}
