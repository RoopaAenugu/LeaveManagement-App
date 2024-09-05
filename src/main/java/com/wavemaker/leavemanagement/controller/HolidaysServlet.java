package com.wavemaker.leavemanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.service.HolidayService;
import com.wavemaker.leavemanagement.service.impl.HolidayServiceImpl;
import com.wavemaker.leavemanagement.util.LocalDateAdapter;
import com.wavemaker.leavemanagement.util.LocalTimeAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/holidays")
public class HolidaysServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HolidaysServlet.class);
    private static Gson gson;
    private static HolidayService holidayService;

    @Override
    public void init() {
        holidayService = new HolidayServiceImpl();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String jsonResponse = null;
        try {
            List<Holiday> holidays = holidayService.getUpcomingHolidays();
            jsonResponse = gson.toJson(holidays);
            writeResponse(response, jsonResponse);
        } catch (Exception e) {
            jsonResponse = "An error occurred while retrieving holidays: " + e.getMessage();
            writeResponse(response, jsonResponse);

        }
    }

    private void writeResponse(HttpServletResponse response, String jsonResponse) {
        PrintWriter printWriter = null;
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            printWriter = response.getWriter();
            printWriter.print(jsonResponse);
            printWriter.flush();
        } catch (IOException e) {
            jsonResponse = "server Unavailable";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printWriter.print(jsonResponse);
            printWriter.flush();

        }
    }


}