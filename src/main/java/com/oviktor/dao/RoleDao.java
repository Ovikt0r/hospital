package com.oviktor.dao;

import com.oviktor.entity.Role;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;

@Slf4j
public class RoleDao {

    public List<Role> getAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "select id, role_name from roles";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                roles.add((Role.builder()
                        .id(rs.getLong("id"))
                        .roleName(rs.getString("role_name"))
                        .build()));

            }
            return roles;
        } catch (SQLException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
