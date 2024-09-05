package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.leavemanagement.util.DateUtil;
import com.wavemaker.leavemanagement.util.DbConnection;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class EmployeeLeaveSummaryRepositoryImpl implements EmployeeLeaveSummaryRepository {

    // Initialize the logger
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryRepositoryImpl.class);

    private static final String INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "INSERT INTO EMPLOYEE_LEAVE_SUMMARY (EMPLOYEE_ID, LEAVE_TYPE_ID, PENDING_LEAVES, TOTAL_LEAVES_TAKEN, LEAVE_TYPE) " +
                    "VALUES (?, ?, ?, ?, ?);";

    private static final String UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY =
            "UPDATE EMPLOYEE_LEAVE_SUMMARY " +
                    "SET TOTAL_LEAVES_TAKEN = ?, PENDING_LEAVES = ? " +
                    "WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?";

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
                    "    EMPLOYEES E " +
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
                    "    E.NAME AS EMPLOYEE_NAME, " +
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
                    "    E.NAME, " +
                    "    ELS.LEAVE_TYPE_ID, " +
                    "    LT.TYPE_NAME, " +
                    "    LT.LIMIT_FOR_LEAVES;";

    private static final String COUNT_APPROVED_LEAVES_BY_TYPE_QUERY =
            "SELECT FROM_DATE, TO_DATE " +
                    "FROM LEAVE_REQUEST " +
                    "WHERE EMPLOYEE_ID = ? " +
                    "AND STATUS = 'APPROVED' " +
                    "AND LEAVE_TYPE_ID = ? " +
                    "AND FROM_DATE >= '2024-04-01' " +
                    "AND TO_DATE <= '2025-03-31'";
    private static final String SELECT_PERSONAL_HOLIDAYS_QUERY =
            "SELECT LR.FROM_DATE, LR.TO_DATE, LT.TYPE_NAME, LR.REASON " +
                    "FROM LEAVE_REQUEST AS LR " +
                    "JOIN LEAVE_TYPES AS LT ON LR.LEAVE_TYPE_ID = LT.LEAVE_TYPE_ID " +
                    "WHERE LR.EMPLOYEE_ID = ? " +
                    "AND LR.STATUS = 'APPROVED'";

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
    public List<Holiday> getPersonalHolidays(int employeeId) throws ServerUnavailableException {
        List<Holiday> holidays = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PERSONAL_HOLIDAYS_QUERY)) {
            preparedStatement.setInt(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String holidayName = resultSet.getString("TYPE_NAME");
                Date holidayStartDate = resultSet.getDate("FROM_DATE");
                Date holidayEndDate = resultSet.getDate("TO_DATE");

                Holiday holiday = new Holiday();
                holiday.setHolidayName(holidayName);
                holiday.setHolidayStartDate(holidayStartDate.toLocalDate());
                holiday.setHolidayEndDate(holidayEndDate.toLocalDate());// Assuming you want to return date as a String
                holiday.setReason(resultSet.getString("REASON"));
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("server is unavailable to fetch holidays", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


            // Handle exceptions (e.g., logging)
        }

        return holidays;

}


@Override
public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
    logger.debug("Adding or updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
    try (Connection connection = DbConnection.getConnection()) {
        // Check if the record exists
        boolean recordExists = checkIfRecordExists(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), connection);

        // Calculate total leaves taken
        int totalLeavesTaken = getTotalLeavesTaken(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), connection);

        // Calculate pending leaves
        int pendingLeaves = employeeLeaveSummary.getTotalAllocatedLeaves() - totalLeavesTaken;

        if (recordExists) {
            // Update the existing record
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {
                preparedStatement.setInt(1, totalLeavesTaken);
                preparedStatement.setInt(2, pendingLeaves);
                preparedStatement.setInt(3, employeeLeaveSummary.getEmployeeId());
                preparedStatement.setInt(4, employeeLeaveSummary.getLeaveTypeId());

                logger.trace("Executing update query: {}", UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.debug("Successfully updated leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                } else {
                    logger.error("Failed to update leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                    throw new ServerUnavailableException("Failed to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (SQLException e) {
                logger.error("SQL exception while updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
                throw new ServerUnavailableException("Server is unavailable to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } else {
            // Insert the new record
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {
                preparedStatement.setInt(1, employeeLeaveSummary.getEmployeeId());
                preparedStatement.setInt(2, employeeLeaveSummary.getLeaveTypeId());
                preparedStatement.setInt(3, pendingLeaves);
                preparedStatement.setInt(4, totalLeavesTaken);
                preparedStatement.setString(5, employeeLeaveSummary.getLeaveType());

                logger.trace("Executing insert query: {}", INSERT_EMPLOYEE_LEAVE_SUMMARY_QUERY);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.debug("Successfully inserted leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                } else {
                    logger.error("Failed to insert leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());
                    throw new ServerUnavailableException("Failed to insert employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (SQLException e) {
                logger.error("SQL exception while inserting leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
                throw new ServerUnavailableException("Server is unavailable to insert employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

    } catch (SQLException e) {
        logger.error("Database connection error while adding or updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
        throw new ServerUnavailableException("Server is unavailable to add or update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    return employeeLeaveSummary;
}

@Override
public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
    logger.debug("Updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId());

    try (Connection connection = DbConnection.getConnection()) {
        // Calculate the total leaves taken
        int totalLeavesTaken = getTotalLeavesTaken(employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), connection);

        // Calculate pending leaves
        int pendingLeaves = employeeLeaveSummary.getTotalAllocatedLeaves() - totalLeavesTaken;


        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_LEAVE_SUMMARY_QUERY)) {
            preparedStatement.setInt(1, totalLeavesTaken);
            preparedStatement.setInt(2, pendingLeaves);
            preparedStatement.setInt(3, employeeLeaveSummary.getEmployeeId());
            preparedStatement.setInt(4, employeeLeaveSummary.getLeaveTypeId());

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
            logger.error("SQL exception while updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
            throw new ServerUnavailableException("Server is unavailable to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    } catch (SQLException e) {
        logger.error("Database connection error while updating leave summary for Employee ID: {}, Leave Type ID: {}", employeeLeaveSummary.getEmployeeId(), employeeLeaveSummary.getLeaveTypeId(), e);
        throw new ServerUnavailableException("Server is unavailable to update employee leave summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

private boolean checkIfRecordExists(int employeeId, int leaveTypeId, Connection connection) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_EMPLOYEE_LEAVE_SUMMARY_EXIST_QUERY)) {
        preparedStatement.setInt(1, employeeId);
        preparedStatement.setInt(2, leaveTypeId);

        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

private int getTotalLeavesTaken(int employeeId, int leaveTypeId, Connection connection) throws SQLException {
    int totalLeavesTaken = 0;
    try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_APPROVED_LEAVES_BY_TYPE_QUERY)) {
        preparedStatement.setInt(1, employeeId);
        preparedStatement.setInt(2, leaveTypeId);

        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                // Calculate total leaves taken by summing up the days
                totalLeavesTaken += DateUtil.calculateTotalDaysExcludingWeekends(rs.getDate("FROM_DATE"), rs.getDate("TO_DATE"));
            }
        }
    }
    return totalLeavesTaken;
}
}
