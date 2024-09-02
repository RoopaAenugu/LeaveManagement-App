package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
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

public class EmployeeLeaveRepositoryImpl implements EmployeeLeaveRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveRepositoryImpl.class);
    private static final String GET_LEAVES_BY_EMPLOYEE_ID_QUERY =
            "SELECT  DISTINCT lr.LEAVE_ID, lr.EMPLOYEE_ID, lr.LEAVE_TYPE_ID, lr.FROM_DATE, lr.TO_DATE, " +
                    "lr.REASON, lr.STATUS, lr.COMMENTS,lr.DATE_OF_APPLICATION , e.NAME AS EMPLOYEE_NAME,lt.TYPE_NAME,lt.LIMIT_FOR_LEAVES " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEES e ON lr.EMPLOYEE_ID = e.EMPLOYEE_ID " +
                    "JOIN LEAVE_TYPES lt ON lr.LEAVE_TYPE_ID = lt. LEAVE_TYPE_ID " +
                    "WHERE lr.EMPLOYEE_ID = ?";
    private static final String GET_LEAVES_REQUESTS_BY_STATUS =
            "SELECT  DISTINCT lr.LEAVE_ID, lr.EMPLOYEE_ID, lr.LEAVE_TYPE_ID, lr.FROM_DATE, lr.TO_DATE, " +
                    "lr.REASON, lr.STATUS, lr.COMMENTS,lr.DATE_OF_APPLICATION , e.NAME AS EMPLOYEE_NAME, lt.TYPE_NAME,lt.LIMIT_FOR_LEAVES " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEES e ON lr.EMPLOYEE_ID = e.EMPLOYEE_ID " +
                    "JOIN LEAVE_TYPES lt ON lr.LEAVE_TYPE_ID = lt. LEAVE_TYPE_ID " +
                    "WHERE lr.EMPLOYEE_ID = ? AND lr.STATUS =?";
    private static final String UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY = "UPDATE LEAVE_REQUEST SET STATUS = 'APPROVED' " +
            "WHERE LEAVE_ID = ?";
    private static final String UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY = "UPDATE LEAVE_REQUEST SET STATUS = 'REJECTED' " +
            "WHERE LEAVE_ID = ?";
    private static final String GET_LEAVE_REQUEST_QUERY = "SELECT * FROM LEAVE_REQUEST WHERE LEAVE_ID = ?";
    private static final String INSERT_LEAVE_REQUEST_QUERY = "INSERT INTO LEAVE_REQUEST (EMPLOYEE_ID, LEAVE_TYPE_ID," +
            " FROM_DATE, TO_DATE, REASON, STATUS, MANAGER_ID, COMMENTS,DATE_OF_APPLICATION) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
    private static final String GET_NUMBER_OF_LEAVES_ALLOCATED =
            "SELECT LIMIT_FOR_LEAVES " +
                    "FROM LEAVE_TYPES " +
                    "WHERE TYPE_NAME = ?";
    private static final String COUNT_APPROVED_LEAVES_BY_TYPE_QUERY =
            "SELECT SUM(DATEDIFF(TO_DATE, FROM_DATE) + 1) AS total_leaves " +
                    "FROM LEAVE_REQUEST " +
                    "WHERE EMPLOYEE_ID = ? " +
                    "AND STATUS = 'APPROVED' " +
                    "AND LEAVE_TYPE_ID = ? " +
                    "AND FROM_DATE >= '2024-04-01' " +
                    "AND TO_DATE <= '2025-03-31'";
    private static final String SELECT_LEAVE_TYPE_ID_QUERY =
            "SELECT LEAVE_TYPE_ID FROM LEAVE_TYPES WHERE TYPE_NAME = ?";
    private static final String SELECT_LEAVE_TYPE_QUERY =
            "SELECT TYPE_NAME FROM LEAVE_TYPES WHERE LEAVE_TYPE_ID  = ?";


    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LEAVE_REQUEST_QUERY)) {

            preparedStatement.setInt(1, leaveRequest.getEmployeeId());
            preparedStatement.setInt(2, leaveRequest.getLeaveTypeId());
            preparedStatement.setDate(3, java.sql.Date.valueOf(leaveRequest.getFromDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(leaveRequest.getToDate()));
            preparedStatement.setString(5, leaveRequest.getReason());
            preparedStatement.setString(6, leaveRequest.getStatus());
            preparedStatement.setInt(7, leaveRequest.getManagerId());
            preparedStatement.setString(8, leaveRequest.getComments());
            preparedStatement.setDate(9, java.sql.Date.valueOf(leaveRequest.getCurrentDate()));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Leave request submitted successfully.");
                return leaveRequest; // Return the leave request if insertion is successful
            }
        } catch (SQLException e) {
            logger.error("Error applying leave request", e);
            throw new ServerUnavailableException("Error applying leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return null; // Return null if insertion fails
    }

    @Override
    public List<EmployeeLeave> getAppliedLeaves(int employeeId, LeaveRequestStatus status) throws ServerUnavailableException {
        List<EmployeeLeave> leaveRequests = new ArrayList<>();
        String query;
        if (status == LeaveRequestStatus.ALL) {
            query = GET_LEAVES_BY_EMPLOYEE_ID_QUERY;
        } else {
            query = GET_LEAVES_REQUESTS_BY_STATUS;
        }
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, employeeId);
            if (status != LeaveRequestStatus.ALL) {
                preparedStatement.setString(2, status.name());
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeLeave employeeLeave = new EmployeeLeave();
                    employeeLeave.setLeaveId(resultSet.getInt("LEAVE_ID"));
                    employeeLeave.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeLeave.setLeaveType(resultSet.getString("TYPE_NAME"));
                    employeeLeave.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
                    employeeLeave.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
                    employeeLeave.setReason(resultSet.getString("REASON"));
                    employeeLeave.setStatus(resultSet.getString("STATUS"));
                    employeeLeave.setComments(resultSet.getString("COMMENTS"));
                    employeeLeave.setEmpName(resultSet.getString("EMPLOYEE_NAME"));
                    employeeLeave.setTypeLimit(resultSet.getInt("LIMIT_FOR_LEAVES"));
                    employeeLeave.setCurrentDate(resultSet.getDate("DATE_OF_APPLICATION").toLocalDate());
                    employeeLeave.setLeaveTypeId(resultSet.getInt("LEAVE_TYPE_ID"));
                    int totalLeavesTaken = getTotalNumberOfLeavesTaken(employeeId, employeeLeave.getLeaveTypeId());
                    employeeLeave.setTotalEmployeeLeavesTaken(totalLeavesTaken);
                    leaveRequests.add(employeeLeave);

                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("server is unavailable to fetch applied leaves", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


        }
        return leaveRequests;
    }

    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException {
        // SQL query to update the leave status
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY);
             PreparedStatement selectStatement = connection.prepareStatement(GET_LEAVE_REQUEST_QUERY)) {
            // Update the leave request status
            updateStatement.setInt(1, leaveId);
            int rowsAffected = updateStatement.executeUpdate();
            // Check if the update was successful
            if (rowsAffected > 0) {
                // Retrieve the updated leave request
                selectStatement.setInt(1, leaveId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToLeaveRequest(resultSet);
                    }
                }
            }

        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return null; // Return null if the leave request was not found or update failed
    }

    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds, LeaveRequestStatus status) throws ServerUnavailableException {
        List<EmployeeLeave> employeeLeaves = new ArrayList<>();
        if (employeeIds == null || employeeIds.isEmpty()) {
            return employeeLeaves; // Return an empty list if no employee IDs are provided
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT e.EMPLOYEE_ID, e.NAME AS EMPLOYEE_NAME, lr.LEAVE_ID, ")
                .append("lr.FROM_DATE, lr.TO_DATE, lr.REASON, lr.STATUS, lr.DATE_OF_APPLICATION, ")
                .append("lr.LEAVE_TYPE_ID, lt.TYPE_NAME AS LEAVE_TYPE_NAME, lt.LIMIT_FOR_LEAVES, lr.COMMENTS ")
                .append("FROM EMPLOYEES e ")
                .append("JOIN LEAVE_REQUEST lr ON e.EMPLOYEE_ID = lr.EMPLOYEE_ID ")
                .append("JOIN LEAVE_TYPES lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID ")
                .append("WHERE e.EMPLOYEE_ID IN (");

        // Add placeholders for employee IDs
        for (int i = 0; i < employeeIds.size(); i++) {
            queryBuilder.append("?");
            if (i < employeeIds.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");

        // Add condition for status if not ALL
        if (status != LeaveRequestStatus.ALL) {
            queryBuilder.append(" AND lr.STATUS = ?");
        }

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            // Set employee IDs in the query
            for (int i = 0; i < employeeIds.size(); i++) {
                preparedStatement.setInt(i + 1, employeeIds.get(i));
            }

            // Set status if not ALL
            if (status != LeaveRequestStatus.ALL) {
                preparedStatement.setString(employeeIds.size() + 1, status.name());
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeLeave employeeLeave = new EmployeeLeave();
                    employeeLeave.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeLeave.setEmpName(resultSet.getString("EMPLOYEE_NAME"));
                    employeeLeave.setLeaveId(resultSet.getInt("LEAVE_ID"));
                    employeeLeave.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
                    employeeLeave.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
                    employeeLeave.setReason(resultSet.getString("REASON"));
                    employeeLeave.setStatus(resultSet.getString("STATUS"));
                    employeeLeave.setComments(resultSet.getString("COMMENTS"));
                    employeeLeave.setCurrentDate(resultSet.getDate("DATE_OF_APPLICATION").toLocalDate());
                    employeeLeave.setLeaveTypeId(resultSet.getInt("LEAVE_TYPE_ID"));
                    employeeLeave.setLeaveType(resultSet.getString("LEAVE_TYPE_NAME"));
                    employeeLeave.setTypeLimit(resultSet.getInt("LIMIT_FOR_LEAVES"));

                    // Set total leaves taken
                    int totalLeavesTaken = getTotalNumberOfLeavesTaken(employeeLeave.getEmployeeId(), employeeLeave.getLeaveTypeId());
                    employeeLeave.setTotalEmployeeLeavesTaken(totalLeavesTaken);

                    employeeLeaves.add(employeeLeave);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching leave details for employees", e);
            throw new ServerUnavailableException("server is unavailable to fetch the leaves of team requests", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeLeaves;
    }


    @Override
    public int getNumberOfLeavesAllocated(String leaveType) {
        int leaveLimit = 0;
        String leaveTypeName = leaveType.trim(); // Ensure trimming

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NUMBER_OF_LEAVES_ALLOCATED)) {

            preparedStatement.setString(1, leaveTypeName);
            logger.info("Executing query to get leave limit for type: {}", leaveTypeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    leaveLimit = resultSet.getInt("LIMIT_FOR_LEAVES");
                    logger.info("Leave limit for type '{}' is: {} ", leaveTypeName, leaveLimit);
                } else {
                    logger.warn("No leave type found for: {}", leaveTypeName);
                }
            } catch (Exception e) {
                logger.error("Error while exeucuting Limit Leaves Query", e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("SQL Error in getNumberOfLeavesAllocated", e);
        }
        logger.info("Final Leave limit for type '{}' is: {} ", leaveTypeName, leaveLimit);
        return leaveLimit;
    }

    @Override
    public int getTotalNumberOfLeavesTaken(int employeeId,int leaveTypeId) {
        int totalLeaves = 0;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_APPROVED_LEAVES_BY_TYPE_QUERY)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, leaveTypeId);

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

    @Override
    public int getLeaveTypeId(String leaveType) throws ServerUnavailableException {
        int leaveTypeId = -1; // Default value if not found


        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LEAVE_TYPE_ID_QUERY)) {

            preparedStatement.setString(1, leaveType);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    leaveTypeId = resultSet.getInt("LEAVE_TYPE_ID");
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to retrieve leave type ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveTypeId;

    }

    @Override
    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException {
        String leaveType=""; // Default value if not found


        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LEAVE_TYPE_QUERY)) {

            preparedStatement.setInt(1, leaveTypeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    leaveType = resultSet.getString("TYPE_NAME");
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to retrieve leave type ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveType;

    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException {

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY);
             PreparedStatement selectStatement = connection.prepareStatement(GET_LEAVE_REQUEST_QUERY)) {
            // Update the leave request status
            updateStatement.setInt(1, leaveId);
            int rowsAffected = updateStatement.executeUpdate();
            // Check if the update was successful
            if (rowsAffected > 0) {
                // Retrieve the updated leave request
                selectStatement.setInt(1, leaveId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToLeaveRequest(resultSet);
                    }
                }
            }

        } catch (SQLException e) {
            throw new ServerUnavailableException("server is Unavailable to reject leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Handle SQL exceptions (e.g., logging)
        }

        return null;

    }

    private EmployeeLeave mapResultSetToLeaveRequest(ResultSet resultSet) throws SQLException {
        EmployeeLeave employeeLeave = new EmployeeLeave();
        employeeLeave.setLeaveId(resultSet.getInt("LEAVE_ID"));
        employeeLeave.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
        employeeLeave.setLeaveTypeId(resultSet.getInt("LEAVE_TYPE_ID"));
        employeeLeave.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
        employeeLeave.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
        employeeLeave.setReason(resultSet.getString("REASON"));
        employeeLeave.setStatus(resultSet.getString("STATUS"));
        employeeLeave.setManagerId(resultSet.getInt("MANAGER_ID"));
        employeeLeave.setComments(resultSet.getString("COMMENTS"));
        employeeLeave.setCurrentDate(resultSet.getDate("DATE_OF_APPLICATION").toLocalDate());
        return  employeeLeave;

    }

}
