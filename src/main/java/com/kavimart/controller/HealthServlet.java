package com.kavimart.controller;

import com.kavimart.dto.ApiResponse;
import com.kavimart.listener.DataSourceContextListener;
import com.kavimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        if (getServletContext().getAttribute(DataSourceContextListener.DATASOURCE_ATTRIBUTE) == null) {
            throw new ServletException("Data source is not available.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataSource dataSource = (DataSource) getServletContext()
                .getAttribute(DataSourceContextListener.DATASOURCE_ATTRIBUTE);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next() || resultSet.getInt(1) != 1) {
                throw new IllegalStateException("Health query returned an unexpected value.");
            }
            JsonUtil.write(response, 200, ApiResponse.success(new HealthData("UP", "UP")));
        } catch (Exception exception) {
            JsonUtil.write(response, 503, ApiResponse.failure("DATABASE_UNAVAILABLE", "The database health check failed."));
        }
    }

    private static final class HealthData {
        private final String status;
        private final String db;

        private HealthData(String status, String db) {
            this.status = status;
            this.db = db;
        }
    }
}