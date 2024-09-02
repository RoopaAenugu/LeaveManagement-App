package com.wavemaker.leavemanagement.model;


import java.time.LocalDate;

public class LeaveRequest {
    private int leaveId;
    private int employeeId;
    private int leaveTypeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String status;
    private int managerId;
    private String comments;
    private LocalDate currentDate;

    // Default constructor
    public LeaveRequest() {
    }

    public LeaveRequest(int leaveId, int employeeId, int leaveTypeId, LocalDate fromDate, LocalDate toDate, String reason, String status, int managerId, String comments) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = "PENDING";
        this.managerId = managerId;
        this.comments = comments;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


}
