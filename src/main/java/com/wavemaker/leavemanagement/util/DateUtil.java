package com.wavemaker.leavemanagement.util;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtil {

    public static int calculateTotalDaysExcludingWeekends(Date fromDate, Date toDate) {
        // Convert java.sql.Date to java.time.LocalDate directly
        LocalDate start = fromDate.toLocalDate();
        LocalDate end = toDate.toLocalDate();

        int totalDays = 0;
        LocalDate currentDate = start;

        while (!currentDate.isAfter(end)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                totalDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalDays;
    }
}
