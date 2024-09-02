package com.wavemaker.leavemanagement.login;

import com.google.gson.Gson;
import com.wavemaker.leavemanagement.service.EmployeeCookieService;
import com.wavemaker.leavemanagement.service.impl.EmployeeCookieServiceImpl;
import com.wavemaker.leavemanagement.util.CookieUserHolder;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/employee/leave/*")
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static EmployeeCookieService employeeCookieService;
    private static Gson gson = null;

    @Override
    public void init(FilterConfig filterConfig) {
        employeeCookieService = new EmployeeCookieServiceImpl();
        gson = new Gson();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", httpServletRequest);

        HttpSession session = httpServletRequest.getSession(true);
        try {
            int loginId = employeeCookieService.getloginIdByCookieValue(cookieValue);
            if (cookieValue != null && loginId != -1) {
                session.setAttribute("loginId", loginId);
                logger.info("Authentication successful. Proceeding with request.");
                chain.doFilter(request, response);
            } else {
                logger.error("Authentication failed. Redirecting to login.");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String jsonResponse = gson.toJson("Authentication required. Redirecting to login.");
                writeResponse(httpServletResponse, jsonResponse);
            }
        } catch (ServletException | IOException e) {
            logger.error("Error during authentication", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String jsonResponse = gson.toJson("An error occurred during authentication.");
            writeResponse(httpServletResponse, jsonResponse);
        }
    }

    private void writeResponse(HttpServletResponse httpServletResponse, String jsonResponse) {
        try (PrintWriter printWriter = httpServletResponse.getWriter()) {
            logger.info("Sending response to client");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            printWriter.print(jsonResponse);
            printWriter.flush();
        } catch (IOException e) {
            logger.error("Error writing response", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
