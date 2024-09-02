package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.factory.EmployeeCookieRepositoryGlobalInstance;
import com.wavemaker.leavemanagement.repository.EmployeeCookieRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeCookieRepositoryImpl;
import com.wavemaker.leavemanagement.service.EmployeeCookieService;

public class EmployeeCookieServiceImpl implements EmployeeCookieService {
    private final EmployeeCookieRepository employeeCookieTaskRepository;

    // Constructor to inject UserCookieTaskRepository
    public EmployeeCookieServiceImpl() {
        this.employeeCookieTaskRepository = EmployeeCookieRepositoryGlobalInstance.getEmployeeCookieRepositoryInstance();
    }

    @Override
    public void addCookie(String cookieValue, int userId) {
        employeeCookieTaskRepository.addCookie(cookieValue, userId);
    }

    @Override
    public int getloginIdByCookieValue(String cookieValue) {
        return employeeCookieTaskRepository.getLoginIdByCookieValue(cookieValue);
    }

    @Override
    public void removeCookie(String cookieValue) {
        employeeCookieTaskRepository.removeCookie(cookieValue);

    }
}
