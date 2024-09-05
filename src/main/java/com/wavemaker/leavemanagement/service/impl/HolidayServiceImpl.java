package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.factory.HolidayGlobalInstance;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.HolidayRepository;
import com.wavemaker.leavemanagement.service.HolidayService;

import java.util.List;

public class HolidayServiceImpl implements HolidayService {
    private HolidayRepository holidayRepository = null;

    public HolidayServiceImpl() {
        this.holidayRepository = HolidayGlobalInstance.getHolidayRepositoryInstance();
    }

    @Override
    public List<Holiday> getUpcomingHolidays() throws ServerUnavailableException {
        return holidayRepository.getUpcomingHolidays();

    }
}
