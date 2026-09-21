package com.kavimart.controller;

import com.kavimart.dto.ApiResponse;
import com.kavimart.dto.LoginRequest;
import com.kavimart.dto.UserRegistrationRequest;
import com.kavimart.dto.UserResponseDTO;
import com.kavimart.exception.AppException;
import com.kavimart.filter.AuthFilter;
import com.kavimart.listener.DataSourceContextListener;
import com.kavimart.service.AuthService;
import com.kavimart.util.JsonUtil;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/auth/*")
public class AuthServlet extends HttpServlet {
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServlet.class);
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        Object service = getServletContext().getAttribute(DataSourceContextListener.AUTH_SERVICE_ATTRIBUTE);
        if (!(service instanceof AuthService)) {
            throw new ServletException("Auth service is not available.");
        }
        authService = (AuthService) service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = action(request);
        try {
            if ("register".equals(action)) {
                UserRegistrationRequest registration = JsonUtil.fromJson(request.getReader().lines().reduce("", String::concat),
                        UserRegistrationRequest.class);
                UserResponseDTO user = authService.register(registration);
                JsonUtil.write(response, 201, ApiResponse.success(user));
                return;
            }
            if ("login".equals(action)) {
                LoginRequest login = JsonUtil.fromJson(request.getReader().lines().reduce("", String::concat),
                        LoginRequest.class);
                UserResponseDTO user = authService.login(login);
                HttpSession session = request.getSession(true);
                request.changeSessionId();
                session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
                session.setAttribute(AuthFilter.AUTH_USER_SESSION_ATTRIBUTE, user);
                JsonUtil.write(response, 200, ApiResponse.success(user));
                return;
            }
            if ("logout".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                JsonUtil.write(response, 200, ApiResponse.success(null));
                return;
            }
            JsonUtil.write(response, 404, ApiResponse.failure("NOT_FOUND", "Authentication endpoint not found."));
        } catch (JsonParseException exception) {
            JsonUtil.write(response, 400, ApiResponse.failure("INVALID_JSON", "Request body must be valid JSON."));
        } catch (AppException exception) {
            JsonUtil.writeError(response, exception);
        } catch (Exception exception) {
            LOGGER.error("Unexpected authentication request failure.", exception);
            JsonUtil.writeInternalError(response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"me".equals(action(request))) {
            JsonUtil.write(response, 404, ApiResponse.failure("NOT_FOUND", "Authentication endpoint not found."));
            return;
        }
        HttpSession session = request.getSession(false);
        UserResponseDTO user = session == null ? null :
                (UserResponseDTO) session.getAttribute(AuthFilter.AUTH_USER_SESSION_ATTRIBUTE);
        if (user == null) {
            JsonUtil.write(response, 401, ApiResponse.failure("UNAUTHENTICATED", "Please log in to continue."));
            return;
        }
        JsonUtil.write(response, 200, ApiResponse.success(user));
    }

    private String action(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null ? "" : path.replaceFirst("^/", "");
    }
}