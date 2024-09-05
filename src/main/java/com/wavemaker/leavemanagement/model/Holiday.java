package com.wavemaker.leavemanagement.model;

import java.time.LocalDate;

public class Holiday {
    int holidayId;
    String holidayName;
    LocalDate holidayStartDate;
    LocalDate holidayEndDate;
    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }


    public void setHolidayStartDate(LocalDate holidayStartDate) {
        this.holidayStartDate = holidayStartDate;
    }

    public LocalDate getHolidayEndDate() {
        return holidayEndDate;
    }

    public void setHolidayEndDate(LocalDate holidayEndDate) {
        this.holidayEndDate = holidayEndDate;
    }
}
