package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.EmployeeManager;
import com.wavemaker.leavemanagement.model.LeaveRequest;

import java.util.List;

public interface EmployeeRepository {

    public Employee addEmployee(Employee employee);
    public boolean checkManager(String emailId);
    public Employee  getEmployeeByLoginId(int loginId);
    public List<Integer> getEmpIdUnderManager(int managerId);
    public EmployeeManager getEmployeeManagerDetails(int employeeId);


}
