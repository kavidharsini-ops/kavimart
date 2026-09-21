package com.kavimart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.kavimart.service.AuthService;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DataSourceContextListener implements ServletContextListener {
    public static final String DATASOURCE_ATTRIBUTE =
            "com.kavimart.datasource";
    public static final String AUTH_SERVICE_ATTRIBUTE =
            "com.kavimart.authService";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceContextListener.class);
    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(getEnv("DB_URL", "jdbc:h2:./data/kavimart;AUTO_SERVER=TRUE"));
        config.setUsername(getEnv("DB_USERNAME", "sa"));
        config.setPassword(getEnv("DB_PASSWORD", ""));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("kaviMartPool");
        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            runScript(connection, "schema.sql");
            runScript(connection, "seed.sql");
            ServletContext context = event.getServletContext();
            context.setAttribute(DATASOURCE_ATTRIBUTE, (DataSource) dataSource);
            context.setAttribute(AUTH_SERVICE_ATTRIBUTE, new AuthService(dataSource));
            LOGGER.info("kaviMart database initialized.");
        } catch (Exception exception) {
            if (dataSource != null) {
                dataSource.close();
            }
            throw new IllegalStateException("Unable to initialize the kaviMart database.", exception);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (dataSource != null) {
            dataSource.close();
            LOGGER.info("kaviMart database pool closed.");
        }
    }

    private void runScript(Connection connection, String resourceName) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IllegalStateException("Missing database resource: " + resourceName);
        }
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            RunScript.execute(connection, reader);
        }
    }

    private String getEnv(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}