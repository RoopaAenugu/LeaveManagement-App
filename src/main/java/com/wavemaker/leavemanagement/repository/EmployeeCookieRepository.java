package com.wavemaker.leavemanagement.repository;

public interface EmployeeCookieRepository {
    public void addCookie(String cookieValue,int loginId);
    public int getLoginIdByCookieValue(String cookieValue);
    public void removeCookie(String cookieValue);
}
