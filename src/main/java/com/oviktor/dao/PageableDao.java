package com.oviktor.dao;

import com.oviktor.dao.utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

import static com.oviktor.connection.DbConnections.getConnection;


public interface PageableDao {
    Logger log = LoggerFactory.getLogger(PageableDao.class);

    default int calculateNumOfPages(Pagination pagination, Supplier<Integer> countSup) {
        if (pagination.getPageNum() <= 0) {
            log.error("The number of pages can't be a negative or zero value");
            throw new RuntimeException();
        }
        int numOfRows = countSup.get();
        if (numOfRows == 0) {
            return 1;
        }
        int numOfPages = numOfRows % pagination.getPageSize() == 0
                ? numOfRows / pagination.getPageSize()
                : numOfRows / pagination.getPageSize() + 1;
        if (numOfPages < pagination.getPageNum()) {
            log.error("The number of pages can't be less than ");
            throw new RuntimeException();
        }
        return numOfPages;
    }

    default int getCountAll(String sql) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            log.error("Given resultSet is empty or sql query passed to the method argument has a mistake");
            throw new RuntimeException();
        } catch (SQLException e) {
            log.error("SQLException was caught", e);
            throw new RuntimeException();
        }
    }

    default int getCountAllByLong(String sql, Long parameter) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, parameter);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                log.error("Given resultSet is empty or sql query passed to the method argument has a mistake");
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            log.error("SQLException was caught", e);
            throw new RuntimeException();
        }
    }
}
