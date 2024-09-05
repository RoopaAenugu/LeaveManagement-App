package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Holiday;

import java.util.List;

public interface HolidayRepository {
    public List<Holiday> getUpcomingHolidays() throws ServerUnavailableException;
}
