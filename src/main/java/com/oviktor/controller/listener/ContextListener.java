package com.oviktor.controller.listener;


import com.oviktor.connection.DataSourceInitializer;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            DataSourceInitializer.initialize();
        } catch (Exception e) {
            log.error("Exception was caught. More details: ", e);
            throw new RuntimeException();
        }
    }
}