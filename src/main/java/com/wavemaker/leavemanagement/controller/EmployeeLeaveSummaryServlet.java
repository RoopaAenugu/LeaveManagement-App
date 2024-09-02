package com.wavemaker.leavemanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;
import com.wavemaker.leavemanagement.service.EmployeeLeaveSummaryService;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.service.impl.EmployeeLeaveServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeLeaveSummaryServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeServiceImpl;
import com.wavemaker.leavemanagement.util.LocalDateAdapter;
import com.wavemaker.leavemanagement.util.LocalTimeAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/employee/leave/summary/*")
public class EmployeeLeaveSummaryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryServlet.class);
    private static Gson gson;
    private static EmployeeLeaveSummaryService employeeLeaveSummaryService;
    private static EmployeeService employeeService;
    private static EmployeeLeaveService employeeLeaveService;

    @Override
    public void init() {
        employeeLeaveService = new EmployeeLeaveServiceImpl();
        employeeLeaveSummaryService = new EmployeeLeaveSummaryServiceImpl();
        employeeService = new EmployeeServiceImpl();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/getEmployeeLeaveSummary")) {
            getEmployeeLeaveSummary(request, response);
        } else if (pathInfo != null && pathInfo.equals("/getTeamLeaveSummary")) {
            getTeamLeaveSummary(request, response);
        } else if (pathInfo != null && pathInfo.equals("/getLeaveLimitsForLeaveType")) {
            getLeaveLimitsForLeaveType(request, response);

        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeResponse(response, "The requested resource [" + pathInfo + "] is not available.");
        }
    }

    private void getEmployeeLeaveSummary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    Employee employee = employeeService.getEmployeeByLoginId(loginId);
                    if (employee != null) {
                        int employeeId = employee.getEmployeeId();
                        List<EmployeeLeaveSummary> employeeLeaveSummary = employeeLeaveSummaryService.getEmployeeLeaveSummaryByEmpId(employeeId);
                        String jsonResponse = gson.toJson(employeeLeaveSummary);
                        writeResponse(response, jsonResponse);
                    } else {
                        writeResponse(response, "Employee not found.");
                    }
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            logger.error("Error fetching employee leave summary", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching leaves for the logged-in employee: " + e.getMessage());
        }
    }

    private void getTeamLeaveSummary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    Employee manager = employeeService.getEmployeeByLoginId(loginId);
                    if (manager != null) {
                        int managerId = manager.getEmployeeId();
                        List<Integer> employeeIds = employeeService.getEmpIdUnderManager(managerId);
                        if (employeeIds != null && !employeeIds.isEmpty()) {
                            List<EmployeeLeaveSummary> employeeLeaveSummaries = employeeLeaveSummaryService.getEmployeeLeaveSummaryByEmpIds(employeeIds);
                            String jsonResponse = gson.toJson(employeeLeaveSummaries);
                            writeResponse(response, jsonResponse);
                        } else {
                            writeResponse(response, "No employees found under this manager.");
                        }
                    } else {
                        writeResponse(response, "Manager not found.");
                    }
                } else {
                    writeResponse(response, "Manager ID is missing.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            logger.error("Error retrieving team leave summaries", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving team leave requests: " + e.getMessage());
        }
    }

    private void getLeaveLimitsForLeaveType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String leaveType = request.getParameter("leaveType");
            if (leaveType != null && !leaveType.trim().isEmpty()) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    Integer loginId = (Integer) session.getAttribute("loginId");
                    if (loginId != null) {
                        Employee employee = employeeService.getEmployeeByLoginId(loginId);
                        if (employee != null) {
                            int employeeId = employee.getEmployeeId();
                            int leaveLimit = employeeLeaveService.getNumberOfLeavesAllocated(leaveType);
                            int leaveTypeId = employeeLeaveService.getLeaveTypeId(leaveType);
                            int leavesTaken = employeeLeaveService.getTotalNumberOfLeavesTaken( employeeId,leaveTypeId);

                            // Creating the response object
                            EmployeeLeave leaveDetails = new EmployeeLeave();
                            leaveDetails.setEmployeeId(employeeId);
                            leaveDetails.setTypeLimit(leaveLimit);
                            leaveDetails.setLeaveTypeId(leaveTypeId);
                            leaveDetails.setTotalEmployeeLeavesTaken(leavesTaken);
                            String jsonResponse = gson.toJson(leaveDetails);
                            writeResponse(response, jsonResponse);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            writeResponse(response, "{\"message\":\"Employee not found.\"}");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeResponse(response, "{\"message\":\"User ID is missing.\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeResponse(response, "{\"message\":\"Session is not valid.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, "{\"message\":\"Leave type is missing.\"}");
            }
        } catch (Exception e) {
            logger.error("Error fetching leave limits", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeResponse(response, "{\"message\":\"An error occurred while fetching leave limits.\"}");
        } finally {
            response.flushBuffer();
        }
    }

    private void writeResponse(HttpServletResponse response, String jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
