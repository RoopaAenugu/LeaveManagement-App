package com.wavemaker.leavemanagement.factory;

import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveSummaryRepositoryImpl;

public class EmployeeLeaveSummaryRepositoryGlobalInstance {
    private static EmployeeLeaveSummaryRepository employeeLeaveSummaryRepository = null;

    public static EmployeeLeaveSummaryRepository getEmployeeLeaveSummaryRepositoryInstance() {
        if (employeeLeaveSummaryRepository == null) {
            synchronized (EmployeeLeaveSummaryRepositoryGlobalInstance.class) {
                employeeLeaveSummaryRepository = new EmployeeLeaveSummaryRepositoryImpl();
            }

        }
        return employeeLeaveSummaryRepository;
    }

}
