package com.wavemaker.leavemanagement.model;

public class EmployeeLeave extends LeaveRequest {
    private String empName;
    private int typeLimit;
    private String leaveType;
    private int totalEmployeeLeaves;


    public int getTotalEmployeeLeaves() {
        return totalEmployeeLeaves;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setTotalEmployeeLeavesTaken(int totalEmployeeLeaves) {
        this.totalEmployeeLeaves = totalEmployeeLeaves;
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
