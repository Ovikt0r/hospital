package com.oviktor.connection;

import com.oviktor.utils.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Slf4j
class DataSourceFactory {

    /**
     * At the beginning of the method, properties are read from the database.properties file
     * using the static method PropertiesReader.readPropertiesFile(). This method takes the path
     * to the file and returns a Properties object that contains properties describing the database connection.
     * The method then uses the received properties to load the database driver and configure
     * the HikariConfig instance to create connections to the database. HikariConfig is a class
     * that allows you to configure database connection parameters such as url, username, password, etc.
     *
     * @return {@link HikariDataSource} object, which is an implementation
     * of the DataSource interface and provides a pool of database connections.
     * @throws IOException is an exception class in Java that indicates input/output errors. This can be
     * raised when an input/output operation (such as reading or writing a file) cannot be completed or
     * if there are problems with connections to external resources, such as databases or network connections.
     * @throws ClassNotFoundException is an exception class in Java that indicates errors while searching
     * for a class by name. It occurs when the class cannot be found at runtime, for example if the class
     * has been deleted or renamed, or if its name has been typed.
     */

    static DataSource defaultDataSource() throws IOException, ClassNotFoundException {
        Properties dbProperties = PropertiesReader.readPropertiesFile("db/database.properties");

        Class.forName(dbProperties.getProperty("driver"));

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbProperties.getProperty("url"));
        hikariConfig.setUsername(dbProperties.getProperty("username"));
        hikariConfig.setPassword(dbProperties.getProperty("password"));
        log.info("hikariConfig set up data from the property");
        return new HikariDataSource(hikariConfig);
    }

    static DataSource customDataSource(String driver, String url, String username, String password) throws ClassNotFoundException {
        Class.forName(driver);

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
