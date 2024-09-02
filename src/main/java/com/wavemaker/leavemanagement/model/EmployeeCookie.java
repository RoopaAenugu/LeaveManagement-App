package com.wavemaker.leavemanagement.model;


import java.time.LocalDate;

public class EmployeeCookie {

    private int cookieId;
    private String cookieName;
    private String cookieValue;
    private int loginId;
    private LocalDate expiryLocalDate;

    // Default constructor
    public EmployeeCookie() {
    }

    // Parameterized constructor
    public EmployeeCookie(int cookieId, String cookieName, String cookieValue, int loginId, LocalDate expiryLocalDate) {
        this.cookieId = cookieId;
        this.cookieName = cookieName;
        this.cookieValue = cookieValue;
        this.loginId = loginId;
        this.expiryLocalDate = expiryLocalDate;
    }

    // Getters and Setters
    public int getCookieId() {
        return cookieId;
    }

    public void setCookieId(int cookieId) {
        this.cookieId = cookieId;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public LocalDate getExpiryLocalDate() {
        return expiryLocalDate;
    }

    public void setExpiryLocalDate(LocalDate expiryLocalDate) {
        this.expiryLocalDate = expiryLocalDate;
    }

    @Override
    public String toString() {
        return "EmployeeCookie{" +
                "cookieId=" + cookieId +
                ", cookieName='" + cookieName + '\'' +
                ", cookieValue='" + cookieValue + '\'' +
                ", loginId=" + loginId +
                ", expiryLocalDate=" + expiryLocalDate +
                '}';
    }
}
