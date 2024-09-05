package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Holiday;

import java.util.List;

public interface HolidayService {
    public List<Holiday> getUpcomingHolidays() throws ServerUnavailableException;
}
