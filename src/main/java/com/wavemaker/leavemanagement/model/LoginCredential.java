package com.wavemaker.leavemanagement.model;

public class LoginCredential {

    private int loginId;
    private String emailId;
    private String password;

    // Default constructor
    public LoginCredential() {
    }

    // Parameterized constructor
    public LoginCredential(int loginId, String emailId, String password) {
        this.loginId = loginId;
        this.emailId = emailId;
        this.password = password;
    }

    // Getters and Setters
    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginCredentials{" +
                "loginId=" + loginId +
                ", emailId='" + emailId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
