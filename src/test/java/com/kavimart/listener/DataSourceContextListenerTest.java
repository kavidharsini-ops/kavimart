package com.kavimart.listener;

import com.kavimart.dao.JdbcUserDao;
import com.kavimart.model.Role;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;

class DataSourceContextListenerTest {
    @Test
    void initializesPoolSchemaAndIdempotentSeedData() throws Exception {
        ServletContext context = mock(ServletContext.class);
        DataSourceContextListener listener = new DataSourceContextListener();

        listener.contextInitialized(new ServletContextEvent(context));
        ArgumentCaptor<Object> dataSourceCaptor = ArgumentCaptor.forClass(Object.class);
        verify(context).setAttribute(
                org.mockito.Mockito.eq(DataSourceContextListener.DATASOURCE_ATTRIBUTE),
                dataSourceCaptor.capture());
        DataSource dataSource = (DataSource) dataSourceCaptor.getValue();
        assertNotNull(dataSource);
        var seededAdmin = new JdbcUserDao(dataSource).findByEmail("admin@kavimart.local");
        assertEquals(Role.ADMIN, seededAdmin.orElseThrow().getRole());

        listener.contextDestroyed(new ServletContextEvent(context));
    }
}