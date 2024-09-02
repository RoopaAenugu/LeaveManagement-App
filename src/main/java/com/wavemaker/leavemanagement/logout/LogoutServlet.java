package com.wavemaker.leavemanagement.logout;

import com.google.gson.Gson;
import com.wavemaker.leavemanagement.service.EmployeeCookieService;
import com.wavemaker.leavemanagement.service.impl.EmployeeCookieServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static Gson gson;
    private static EmployeeCookieService employeeCookieService;

    @Override
    public void init() {
        gson = new Gson();
        employeeCookieService = new EmployeeCookieServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Remove the cookie associated with the user
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth_cookie")) {
                    // Remove the cookie from the client
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);

                    // Remove the cookie from the server (if applicable)
                   employeeCookieService.removeCookie(cookie.getValue());
                    break;
                }
            }
        }

        // Redirect to the login page after logout
        response.sendRedirect("login.html");
    }
}
