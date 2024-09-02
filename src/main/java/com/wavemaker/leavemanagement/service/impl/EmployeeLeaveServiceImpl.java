package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.constants.LeaveRequestStatus;
import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.factory.EmployeeLeaveRepositoryGlobalInstance;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeCookieRepository;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeCookieRepositoryImpl;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {
    private final EmployeeLeaveRepository employeeLeaveRepository;

    // Constructor to inject UserCookieTaskRepository
    public EmployeeLeaveServiceImpl() {
        this.employeeLeaveRepository = EmployeeLeaveRepositoryGlobalInstance.getEmployeeLeaveRepositoryInstance();
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
      return  employeeLeaveRepository.applyLeave(leaveRequest);
    }
    @Override
    public List<EmployeeLeave> getAppliedLeaves(int empId, LeaveRequestStatus status) throws ServerUnavailableException {
        return employeeLeaveRepository.getAppliedLeaves(empId,status);
    }

    @Override
    public EmployeeLeave acceptLeaveRequest(int leaveId) throws ServerUnavailableException {
        return employeeLeaveRepository.acceptLeaveRequest(leaveId);
    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) throws ServerUnavailableException {
        return employeeLeaveRepository.rejectLeaveRequest(leaveId);
    }



    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds,LeaveRequestStatus status) throws ServerUnavailableException {
        return employeeLeaveRepository.getLeavesOfEmployees(employeeIds,status);
    }

    @Override
    public int getNumberOfLeavesAllocated(String leaveType) {
        return employeeLeaveRepository.getNumberOfLeavesAllocated(leaveType);
    }

    @Override
    public int getTotalNumberOfLeavesTaken(int empId,int leaveTypeId) throws SQLException {
        return  employeeLeaveRepository.getTotalNumberOfLeavesTaken(empId,leaveTypeId);
    }

    @Override
    public int getLeaveTypeId(String leaveType) throws ServerUnavailableException {
        return employeeLeaveRepository.getLeaveTypeId(leaveType);
    }

    @Override
    public String getLeaveType(int leaveTypeId) throws ServerUnavailableException {
        return employeeLeaveRepository.getLeaveType(leaveTypeId);
    }

}
