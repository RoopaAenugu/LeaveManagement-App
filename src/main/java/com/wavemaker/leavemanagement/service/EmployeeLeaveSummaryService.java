package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.EmployeeLeaveSummary;
import com.wavemaker.leavemanagement.model.Holiday;

import java.util.List;

public interface EmployeeLeaveSummaryService {
    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpId(int employeeId) throws ServerUnavailableException;

    public EmployeeLeaveSummary addEmployeeLeaveSummary(EmployeeLeaveSummary employeeSummary) throws ServerUnavailableException;

    public boolean updateEmployeeLeaveSummary(EmployeeLeaveSummary employeeLeaveSummary) throws ServerUnavailableException;

    public List<EmployeeLeaveSummary> getEmployeeLeaveSummaryByEmpIds(List<Integer> employeeIds) throws ServerUnavailableException;

    public List<Holiday> getPersonalHolidays(int employeeId) throws ServerUnavailableException;

}
