package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeManager;

import java.util.List;

public interface EmployeeService {
    public Employee addEmployee(Employee employee);

    public boolean checkManager(String emailId) throws ServerUnavailableException;

    public Employee getEmployeeByLoginId(int loginId) throws ServerUnavailableException;

    public List<Integer> getEmpIdUnderManager(int managerId) throws ServerUnavailableException;

    public EmployeeManager getEmployeeManagerDetails(int employeeId) throws ServerUnavailableException;

    public EmployeeLeave getEmployeeDetailsAndLeaveSummary(int empId) throws ServerUnavailableException;

}
