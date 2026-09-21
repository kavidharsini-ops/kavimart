package com.kavimart.filter;

import com.kavimart.dto.ApiResponse;
import com.kavimart.dto.UserResponseDTO;
import com.kavimart.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/account/*", "/api/v1/cart/*", "/api/v1/orders/*", "/api/v1/seller/*", "/api/v1/admin/*"})
public class AuthFilter implements Filter {
    public static final String AUTH_USER_SESSION_ATTRIBUTE = "authUser";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        UserResponseDTO user = (UserResponseDTO) httpRequest.getSession(false) == null
                ? null
                : (UserResponseDTO) httpRequest.getSession(false).getAttribute(AUTH_USER_SESSION_ATTRIBUTE);
        if (user != null) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (httpRequest.getRequestURI().contains("/api/")) {
            JsonUtil.write(httpResponse, 401, ApiResponse.failure("UNAUTHENTICATED", "Please log in to continue."));
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
}