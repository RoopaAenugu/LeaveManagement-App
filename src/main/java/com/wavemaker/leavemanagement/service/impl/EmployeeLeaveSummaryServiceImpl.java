package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.factory.EmployeeLeaveSummaryRepositoryGlobalInstance;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveSummaryRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveSummaryRepositoryImpl;
import com.wavemaker.leavemanagement.service.EmployeeLeaveSummaryService;

import java.util.List;

public class EmployeeLeaveSummaryServiceImpl implements EmployeeLeaveSummaryService {
    private final EmployeeLeaveSummaryRepository employeeLeaveSummaryRepository;

    // Constructor to inject UserCookieTaskRepository
    public EmployeeLeaveSummaryServiceImpl() {
        this.employeeLeaveSummaryRepository = EmployeeLeaveSummaryRepositoryGlobalInstance.getEmployeeLeaveSummaryRepositoryInstance();
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException {
       return employeeLeaveSummaryRepository.getEmployeeLeaveSummaryByEmpId(employeeId);
    }

    @Override
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeSummary) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.addEmployeeLeaveSummary(employeeSummary);
    }

    @Override
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.updateEmployeeLeaveSummary(employeeLeaveSummary);
    }

    @Override
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException {
        return employeeLeaveSummaryRepository.getEmployeeLeaveSummaryByEmpIds(employeeIds);
    }
}
