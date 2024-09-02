package com.wavemaker.leavemanagement.service;

public interface EmployeeCookieService {
    public void addCookie(String cookieValue,int userId);
    public int getloginIdByCookieValue(String cookieValue);
    public void removeCookie(String cookieValue);
}
