package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;

import java.util.List;

public interface EmployeeLeaveSummaryRepository {
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException;
    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeSummary) throws ServerUnavailableException;
    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary  employeeLeaveSummary) throws ServerUnavailableException;
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException;
}
