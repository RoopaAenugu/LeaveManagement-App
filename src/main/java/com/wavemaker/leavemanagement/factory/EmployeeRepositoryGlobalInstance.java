package com.wavemaker.leavemanagement.factory;

import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeRepositoryImpl;

public class EmployeeRepositoryGlobalInstance {
    private static EmployeeRepository employeeRepository = null;

    public static EmployeeRepository getEmployeeRepositoryInstance() {
        if (employeeRepository == null) {
            synchronized (EmployeeLeaveRepositoryGlobalInstance.class) {
                employeeRepository = new EmployeeRepositoryImpl();
            }
        }
        return employeeRepository;
    }
}
