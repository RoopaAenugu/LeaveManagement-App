package com.wavemaker.leavemanagement.factory;

import com.wavemaker.leavemanagement.repository.HolidayRepository;
import com.wavemaker.leavemanagement.repository.impl.HolidayRepositoryImpl;

public class HolidayGlobalInstance {

    private static HolidayRepository holidayRepository = null;

    public static HolidayRepository getHolidayRepositoryInstance() {
        if (holidayRepository == null) {
            synchronized (EmployeeLeaveRepositoryGlobalInstance.class) {
                if (holidayRepository == null) {
                    holidayRepository = new HolidayRepositoryImpl();
                }
            }
        }
        return holidayRepository;
    }
}
