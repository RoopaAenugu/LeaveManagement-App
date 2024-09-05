package com.wavemaker.leavemanagement.model;

import java.util.List;

public class EmployeeLeave extends LeaveRequest {
    private String empName;
    private int typeLimit;
    private String leaveType;
    private int totalEmployeeLeavesTaken;
    private int pendingLeaves;
    private String email;
    private long phoneNumber;
    private List<EmployeeLeaveSummary> employeeLeaveSummaries;

    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaries() {
        return employeeLeaveSummaries;
    }

    public void setEmployeeLeaveSummaries(List<EmployeeLeaveSummary> employeeLeaveSummaries) {
        this.employeeLeaveSummaries = employeeLeaveSummaries;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    public int getTotalEmployeeLeavesTaken() {
        return totalEmployeeLeavesTaken;
    }

    public void setTotalEmployeeLeavesTaken(int totalEmployeeLeavesTaken) {
        this.totalEmployeeLeavesTaken = totalEmployeeLeavesTaken;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public int getTypeLimit() {
        return typeLimit;
    }

    public void setTypeLimit(int typeLimit) {
        this.typeLimit = typeLimit;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }


}
