package com.wavemaker.leavemanagement.factory;

import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;

public class EmployeeLeaveRepositoryGlobalInstance {
    private static EmployeeLeaveRepository employeeLeaveRepository = null;

    public static EmployeeLeaveRepository getEmployeeLeaveRepositoryInstance() {
        if (employeeLeaveRepository == null) {
            synchronized (EmployeeLeaveRepositoryGlobalInstance.class) {
                employeeLeaveRepository = new EmployeeLeaveRepositoryImpl();
            }
        }
        return employeeLeaveRepository;
    }

}
