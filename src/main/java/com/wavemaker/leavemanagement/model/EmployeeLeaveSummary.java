package com.wavemaker.leavemanagement.model;

public class EmployeeLeaveSummary {

    private int summaryId;
    private int employeeId;
    private int leaveTypeId;
    private String leaveType;
    private int pendingLeaves;
    private int totalLeavesTaken;
    private int totalAllocatedLeaves;
    private String empName;


    // Getter and Setter for summaryId
    public int getSummaryId() {
        return summaryId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public int getTotalAllocatedLeaves() {
        return totalAllocatedLeaves;
    }

    public void setTotalAllocatedLeaves(int totalAllocatedLeaves) {
        this.totalAllocatedLeaves = totalAllocatedLeaves;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    // Getter and Setter for employeeId
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    // Getter and Setter for leaveTypeId
    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    // Getter and Setter for leaveType
    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    // Getter and Setter for pendingLeaves
    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    // Getter and Setter for totalLeavesTaken
    public int getTotalLeavesTaken() {
        return totalLeavesTaken;
    }

    public void setTotalLeavesTaken(int totalLeavesTaken) {
        this.totalLeavesTaken = totalLeavesTaken;
    }

    @Override
    public String toString() {
        return "EmployeeLeaveSummary{" +
                "summaryId=" + summaryId +
                ", employeeId=" + employeeId +
                ", leaveTypeId=" + leaveTypeId +
                ", leaveType='" + leaveType + '\'' +
                ", pendingLeaves=" + pendingLeaves +
                ", totalLeavesTaken=" + totalLeavesTaken +
                '}';
    }
}
