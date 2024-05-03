package com.oviktor.dao;

import com.oviktor.dto.UserInfo;
import com.oviktor.dto.UserNameDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;
@Slf4j
public class SecurityDao {

    public Optional<String> getPasswordByLogin(String login, boolean transactional) {
        return transactional ? getPasswordByLoginWithinTransaction(login) : getPasswordByLogin(login);
    }

    private Optional<String> getPasswordByLoginWithinTransaction(String login) {
        try {
            Connection connection = getCurrentThreadConnection();
            return getPasswordByLogin(login, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public Optional<String> getPasswordByLogin(String login) {
        try {
            Connection connection = getConnection();
            return getPasswordByLogin(login, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    private Optional<String> getPasswordByLogin(String login, Connection connection) {
        String sql = "SELECT users_password FROM users WHERE email=?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next()
                        ? Optional.of(rs.getString("users_password"))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public Optional<UserInfo> getUserInfoByUserLogin(String login, boolean transaction) {
        return transaction ? getUserInfoByUserLoginWithinTransaction(login) : getUserInfoByUserLogin(login);
    }

    private Optional<UserInfo> getUserInfoByUserLoginWithinTransaction(String login) {
        try {
            Connection connection = getCurrentThreadConnection();
            return getUserInfoByUserLogin(login, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public Optional<UserInfo> getUserInfoByUserLogin(String login) {
        try {
            Connection connection = getConnection();
            return getUserInfoByUserLogin(login, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    private Optional<UserInfo> getUserInfoByUserLogin(String login, Connection connection) {
        String sql = """
                select u.id, r.role_name
                from users u
                         join roles r on r.id = u.role_id
                where u.email = ?;
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next()
                        ? Optional.of(new UserInfo(rs.getLong("id"), rs.getString("role_name")))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public boolean userWithRoleAndPermissionExists(Long id, Long roleId, Long permissionId) {
        String sql = """
                select exists(select 1
                              from users u
                                       join roles r on r.id = u.role_id
                                       join roles_permissions rp on r.id = rp.role_id
                                       join permissions p on p.id = rp.permission_id
                              where u.id = ?
                                and r.id = ?
                                and p.id = ?);
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, roleId);
            preparedStatement.setLong(3, permissionId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public UserNameDto getUserNameById(Long userId) {
        String sql = """
                select u.last_name, u.first_name
                from users u
                where u.id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserNameDto(rs.getString("last_name"), rs.getString("first_name"));
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return null;
    }
}
