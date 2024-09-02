package com.wavemaker.leavemanagement.model;
import java.time.LocalDate;
import java.util.Objects;

public class Employee {
    private int employeeId;
    private String empName;
    private String email;
    private LocalDate DateOfBirth;
    private long phoneNumber;
    private int managerId;
    private String gender;


    public Employee() {

    }

    public Employee(int employeeId, String empName, String email, LocalDate DateOfBirth, long phoneNumber, int managerId) {
        this.employeeId = employeeId;
        this.empName = empName;
        this.email = email;
        this.DateOfBirth = DateOfBirth;
        this.phoneNumber = phoneNumber;
        this.managerId = managerId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(LocalDate DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return employeeId == employee.employeeId && phoneNumber == employee.phoneNumber && managerId == employee.managerId && Objects.equals(empName, employee.empName) && Objects.equals(email, employee.email) && Objects.equals(DateOfBirth, employee.DateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, empName, email, DateOfBirth, phoneNumber, managerId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", empName='" + empName + '\'' +
                ", email='" + email + '\'' +
                ", DateOfBirth=" + DateOfBirth +
                ", phoneNumber=" + phoneNumber +
                ", managerId=" + managerId +
                '}';
    }
}
