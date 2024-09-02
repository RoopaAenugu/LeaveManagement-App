package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeLeaveRepository {
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException;
    public List<EmployeeLeave> getAppliedLeaves(int empId, LeaveRequestStatus status) throws ServerUnavailableException;
    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException;
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException;
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds,LeaveRequestStatus status) throws ServerUnavailableException;
    public int getNumberOfLeavesAllocated(String leaveType);
    public  int getTotalNumberOfLeavesTaken(int empId,int leaveTypeId) throws SQLException;
    public int getLeaveTypeId(String leaveType) throws ServerUnavailableException;
    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException;

}
