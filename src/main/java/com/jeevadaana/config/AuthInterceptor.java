package com.jeevadaana.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards a set of URL prefixes by requiring a given session attribute to be present.
 * Unauthenticated requests are redirected to the supplied login URL.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final String sessionKey;
    private final String loginUrl;

    public AuthInterceptor(String sessionKey, String loginUrl) {
        this.sessionKey = sessionKey;
        this.loginUrl = loginUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(sessionKey) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + loginUrl);
        return false;
    }
}
