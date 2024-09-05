package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.EmployeeManager;
import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String FIND_EMPLOYEES_BY_MANAGER_QUERY =
            "SELECT * FROM EMPLOYEES WHERE MANAGER_ID = ?";

    private static final String INSERT_EMPLOYEE_QUERY =
            "INSERT INTO EMPLOYEES (EMPLOYEE_ID, NAME, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, MANAGER_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String CHECK_MANAGER_QUERY =
            "SELECT COUNT(*) FROM EMPLOYEES WHERE EMAIL = ? AND EMPLOYEE_ID IN " +
                    "(SELECT DISTINCT MANAGER_ID FROM EMPLOYEES WHERE MANAGER_ID IS NOT NULL)";

    private static final String GET_EMPLOYEE_BY_LOGIN_ID_QUERY =
            "SELECT EMPLOYEE_ID FROM LOGIN_CREDENTIALS WHERE LOGIN_ID = ?";

    private static final String GET_EMPLOYEE_DETAILS_QUERY =
            "SELECT * FROM EMPLOYEES WHERE EMPLOYEE_ID = ?";

    private static final String GET_EMPLOYEE_IDS_UNDER_MANAGER_QUERY =
            "SELECT EMPLOYEE_ID FROM EMPLOYEES WHERE MANAGER_ID = ?";

    private static final String GET_EMPLOYEE_MANAGER_DETAILS_QUERY =
            "SELECT e.EMPLOYEE_ID, e.NAME, e.EMAIL, e.DATE_OF_BIRTH, e.PHONE_NUMBER, e.MANAGER_ID, e.GENDER, " +
                    "m.NAME AS MANAGER_NAME, " +
                    "m.EMAIL AS MANAGER_EMAIL, " +
                    "m.DATE_OF_BIRTH AS MANAGER_DATE_OF_BIRTH, " +
                    "m.PHONE_NUMBER AS MANAGER_PHONE_NUMBER, " +
                    "m.GENDER AS MANAGER_GENDER " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN EMPLOYEES m ON e.MANAGER_ID = m.EMPLOYEE_ID " +
                    "WHERE e.EMPLOYEE_ID = ?";
    private static final String GET_EMPLOYEE_DETAILS_AND_LEAVE_SUMMARY_QUERY =
            "SELECT " +
                    "    e.NAME AS employeeName, " +
                    "    e.PHONE_NUMBER AS phoneNumber, " +
                    "    e.EMAIL AS emailId, " +
                    "    lt.TYPE_NAME AS leaveType, " +
                    "    COALESCE(els.TOTAL_LEAVES_TAKEN, 0) AS totalLeavesTaken, " +
                    "    lt.LIMIT_FOR_LEAVES AS leaveTypeLimit " +
                    "FROM " +
                    "    EMPLOYEES e " +
                    "LEFT JOIN " +
                    "    EMPLOYEE_LEAVE_SUMMARY els ON e.EMPLOYEE_ID = els.EMPLOYEE_ID " +
                    "LEFT JOIN " +
                    "    LEAVE_TYPES lt ON els.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE " +
                    "    e.EMPLOYEE_ID = ? " +
                    "ORDER BY " +
                    "    lt.TYPE_NAME";


    @Override
    public Employee addEmployee(Employee employee) {

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE_QUERY)) {

            preparedStatement.setInt(1, employee.getEmployeeId());
            preparedStatement.setString(2, employee.getEmpName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setDate(4, java.sql.Date.valueOf(employee.getDateOfBirth()));  // Convert LocalDate to java.sql.Date
            preparedStatement.setBigDecimal(5, new BigDecimal(employee.getPhoneNumber()));
            preparedStatement.setInt(6, employee.getManagerId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return employee;

    }

    @Override
    public boolean checkManager(String emailId) throws ServerUnavailableException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_MANAGER_QUERY)) {

            preparedStatement.setString(1, emailId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    // If the count is greater than 0, the employee is a manager
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        // Handle exceptions (e.g., logging)

        return false;
    }

    @Override
    public Employee getEmployeeByLoginId(int loginId) throws ServerUnavailableException {
        Employee employee = null;
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement getEmployeeIdStatement = connection.prepareStatement(GET_EMPLOYEE_BY_LOGIN_ID_QUERY)) {
            getEmployeeIdStatement.setInt(1, loginId);
            try (ResultSet employeeIdResultSet = getEmployeeIdStatement.executeQuery()) {
                if (employeeIdResultSet.next()) {
                    int employeeId = employeeIdResultSet.getInt("EMPLOYEE_ID");
                    try (PreparedStatement getEmployeeStatement = connection.prepareStatement(GET_EMPLOYEE_DETAILS_QUERY)) {
                        getEmployeeStatement.setInt(1, employeeId);
                        try (ResultSet employeeResultSet = getEmployeeStatement.executeQuery()) {
                            if (employeeResultSet.next()) {
                                employee = new Employee();
                                employee.setEmployeeId(employeeResultSet.getInt("EMPLOYEE_ID"));
                                employee.setEmpName(employeeResultSet.getString("NAME"));
                                employee.setEmail(employeeResultSet.getString("EMAIL"));
                                employee.setDateOfBirth(employeeResultSet.getDate("DATE_OF_BIRTH").toLocalDate());
                                employee.setPhoneNumber(employeeResultSet.getLong("PHONE_NUMBER"));
                                employee.setManagerId(employeeResultSet.getInt("MANAGER_ID"));
                                employee.setGender(employeeResultSet.getString("GENDER"));
                                // Set other fields as necessary
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


        return employee;
    }

    @Override
    public List<Integer> getEmpIdUnderManager(int managerId) throws ServerUnavailableException {
        List<Integer> employeeIds = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_IDS_UNDER_MANAGER_QUERY)) {
            preparedStatement.setInt(1, managerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    employeeIds.add(resultSet.getInt("EMPLOYEE_ID"));
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return employeeIds;
    }

    @Override
    public EmployeeManager getEmployeeManagerDetails(int employeeId) throws ServerUnavailableException {
        EmployeeManager employeeManager = null;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_MANAGER_DETAILS_QUERY)) {

            preparedStatement.setInt(1, employeeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    employeeManager = new EmployeeManager();

                    // Employee details
                    employeeManager.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeManager.setEmpName(resultSet.getString("NAME"));
                    employeeManager.setEmail(resultSet.getString("EMAIL"));
                    employeeManager.setDateOfBirth(resultSet.getDate("DATE_OF_BIRTH").toLocalDate());
                    employeeManager.setPhoneNumber(resultSet.getLong("PHONE_NUMBER"));
                    employeeManager.setManagerId(resultSet.getInt("MANAGER_ID"));
                    employeeManager.setGender(resultSet.getString("GENDER"));

                    // Manager details
                    employeeManager.setManagerName(resultSet.getString("MANAGER_NAME"));
                    employeeManager.setManagerEmail(resultSet.getString("MANAGER_EMAIL"));
                    employeeManager.setManagerDateOfBirth(resultSet.getDate("MANAGER_DATE_OF_BIRTH").toLocalDate());
                    employeeManager.setManagerPhoneNumber(resultSet.getLong("MANAGER_PHONE_NUMBER"));
                    employeeManager.setManagerGender(resultSet.getString("MANAGER_GENDER"));
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("unavailable to accept leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeManager;
    }

    @Override
    public EmployeeLeave getEmployeeLeaveDetailsAndLeaveSummary(int empId) throws ServerUnavailableException {
        EmployeeLeave employeeLeave = new EmployeeLeave();
        List<EmployeeLeaveSummary> employeeLeaveSummaries = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_DETAILS_AND_LEAVE_SUMMARY_QUERY)) {

            preparedStatement.setInt(1, empId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int totalLeavesTaken = resultSet.getInt("totalLeavesTaken");
                    int leaveTypeLimit = resultSet.getInt("leaveTypeLimit");
                    int pendingLeaves = leaveTypeLimit - totalLeavesTaken;

                    // Setting employee details (only set once)
                    if (employeeLeave.getEmpName() == null) {
                        employeeLeave.setEmpName(resultSet.getString("employeeName"));
                        employeeLeave.setPhoneNumber(resultSet.getLong("phoneNumber")); // Make sure PHONE_NUMBER is of type Long in your DB
                        employeeLeave.setEmail(resultSet.getString("emailId"));
                    }

                    // Create and set EmployeeLeaveSummary
                    EmployeeLeaveSummary employeeLeaveSummary = new EmployeeLeaveSummary();
                    employeeLeaveSummary.setLeaveType(resultSet.getString("leaveType"));
                    employeeLeaveSummary.setTotalLeavesTaken(totalLeavesTaken);
                    employeeLeaveSummary.setTotalAllocatedLeaves(leaveTypeLimit);
                    employeeLeaveSummary.setPendingLeaves(pendingLeaves);

                    employeeLeaveSummaries.add(employeeLeaveSummary);
                }

                employeeLeave.setEmployeeLeaveSummaries(employeeLeaveSummaries);
            }

        } catch (SQLException e) {
            // Adjust exception handling as needed
            throw new ServerUnavailableException("Unable to fetch employee leave details", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return employeeLeave;
    }

}

