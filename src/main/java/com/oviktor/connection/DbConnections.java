package com.oviktor.connection;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DbConnections {

    private static final ThreadLocal<Connection> localConnections = new ThreadLocal<>();

    private static boolean instantiated = false;
    private static DataSource dataSource;

    static void instantiateDefault() throws ClassNotFoundException, IOException {
        if (instantiated) {
            log.info("DB connection was instantiated in default way");
        }
        else {
            dataSource = DataSourceFactory.defaultDataSource();
            instantiated = true;
        }
    }
    public static void instantiateCustom(DataSource dataSource) {
        DbConnections.dataSource = dataSource;
        instantiated = true;
    }

    static void instantiateCustom(String driver, String url, String username, String password) throws ClassNotFoundException {
        if (instantiated) {
            log.info("DB connection was instantiated with custom properties");
            return;
        }
        dataSource = DataSourceFactory.customDataSource(driver, url, username, password);
        instantiated = true;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static Connection getCurrentThreadConnection() throws SQLException {
        Connection currentThreadConnection = localConnections.get();
        if (currentThreadConnection == null) {
            localConnections.set(dataSource.getConnection());
            log.debug("Current thread connection got the connection from data source");
            log.info("Connection with the database for the current transaction operation is received");
        }
        return localConnections.get();
    }

    public static void invalidateConnection() {
        try {
            Connection connection = getCurrentThreadConnection();
            if (connection != null) {
                connection.close();
                log.info("Connection from current thread in transactional method was closed");
                localConnections.remove();
            }
        } catch (SQLException e) {
            log.error("Invalidate connection went wrong", e);
        }
    }
}
