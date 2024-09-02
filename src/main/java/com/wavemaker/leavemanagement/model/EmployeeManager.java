package com.wavemaker.leavemanagement.model;

import java.time.LocalDate;

public class EmployeeManager extends Employee {
    String managerName;
    String managerEmail;
    private LocalDate managerDateOfBirth;
    private long managerPhoneNumber;
    private String managerGender;

    public LocalDate getManagerDateOfBirth() {
        return managerDateOfBirth;
    }

    public void setManagerDateOfBirth(LocalDate managerDateOfBirth) {
        this.managerDateOfBirth = managerDateOfBirth;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public long getManagerPhoneNumber() {
        return managerPhoneNumber;
    }

    public void setManagerPhoneNumber(long managerPhoneNumber) {
        this.managerPhoneNumber = managerPhoneNumber;
    }

    public String getManagerGender() {
        return managerGender;
    }

    public void setManagerGender(String managerGender) {
        this.managerGender = managerGender;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
